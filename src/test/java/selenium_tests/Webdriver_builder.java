package selenium_tests;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.*;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.testng.SkipException;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class Webdriver_builder implements WebDriver {

	private WebDriver webdriver;
	private MutableCapabilities options;
	private BrowserMobProxyServer web_proxy;
	private Proxy seleniumProxy;
	private WebDriverWait wait;

	private final String operating_system;
	private final String browser;
	private final String chrome_logging_enabled;

	private String home_url;

	private static String os_name = System.getProperty("os.name").toLowerCase();

	private String error_log_base_path = System.getProperty("user.dir").replace("\\", "/")  + 
			"/target/CAPTURED-ERRORS/";

	private final int max_wait_time = 15;
	private final int messsage_level_threshold = 0;
	private final int millisecond_performance_threshold = 5000;



	/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

This class is a webdriver which is self-configuring based on the parameters sent to it when its launched. 

Key features include:
(1) Can be a local or remote web driver and includes added driver capabilities 
	typically used in testing such as bypassing certification issues.

	When used as a remote driver it acts as a hub and can be any 
	operating system, browsers and browser version that are supported by your nodes.

	When used as a local driver it self-configures web driver binaries using a driver manager 
	and supports all major browsers.

(2) Has enhanced logging for test failures that include a detailed stack trace, screenshot
    and HAR file dump.  

(3) Has an inner class with enhanced Selenium methods. 
	These methods should be called when building test scripts rather than natively calling 
	the webdriver, because they are designed to build more robust scripts and 
	to track HTTP issues. 

   	Robust scripting is achieved through the auto use of Ajax waiting after events, 
   	explicit wait conditions and javascript scrolling. 
   	This has proven to reduce the failure rate of test scripts without 
   	causing unnecessary test run delay.	

	HTTP tracking is achieved by routing requests through a proxy and then auto logging 
	requests that result in HTTP error codes or performance issues. 

	There are also some utility methods, for example, the method which logs and takes 
	screenshots on failure. 
	Such a method works well when called by a testNG listener on failure.

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */	

	@SuppressWarnings("deprecation")
	public Webdriver_builder(	
			String operating_system, 
			String browser, 
			String browser_version,
			String selenium_grid_enabled, 
			String selenium_grid_hub,
			String web_proxy_enabled, 
			String chrome_logging_enabled)
					throws MalformedURLException, AWTException {

		this.operating_system = operating_system;
		this.browser = browser;
		this.chrome_logging_enabled = chrome_logging_enabled; 



		// ==================================
		// Selenium Grid not Enabled: - will run on current machine. Will attempt
		// to execute all tests found in environment_configurations_to_test.xml however
		// will skip if operating system doesnt match.
		// ==================================

		if (selenium_grid_enabled.equalsIgnoreCase("no")) {

			if (!build_machine_supports_desired_OperatingSystem(operating_system)) {

				System.out.println("************");
				System.out.println("[skipping test] This build machine does not support operating system: " + os_name);
				System.out.println("************");
				throw new SkipException("skipping test");

			}

			// ==================================
			// Set driver capabilities and launch local driver
			// ==================================

			set_driver_options(web_proxy_enabled,chrome_logging_enabled);

			switch (this.browser.toLowerCase()) {
			case "chrome": 	this.webdriver = new ChromeDriver((ChromeOptions)options); break;
			case "firefox":	this.webdriver = new FirefoxDriver((FirefoxOptions)options);break;
			case "edge":	this.webdriver = new EdgeDriver((EdgeOptions)options);break;
			case "internet explorer": this.webdriver = new InternetExplorerDriver();break;	
			case "opera": 	this.webdriver = new OperaDriver();

			}

		} else {

			// ==================================
			// Selenium Grid Enabled: will find node/s to match current
			// environment_configurations_to_test.xml test
			// ==================================		

			// ==================================
			// Set driver capabilities and do not launch remote driver
			// ==================================

			set_driver_options(web_proxy_enabled,chrome_logging_enabled);

			// Set capabilityType, which is used to find grid node with matching
			// capabilities
			options.setCapability(CapabilityType.BROWSER_NAME, browser);
			if (!browser_version.equals(""))
				options.setCapability(CapabilityType.BROWSER_VERSION, browser_version);
			if (!operating_system.equals(""))
				options.setCapability(CapabilityType.PLATFORM_NAME, operating_system);
			if (!operating_system.equals(""))
				options.setCapability(CapabilityType.PLATFORM, operating_system);


			// Launch Selenium grid, looking for node/s which match above capabilities
			this.webdriver = new RemoteWebDriver(new URL(selenium_grid_hub), options);

			System.out.println("Webdriver launched on node successfully for: " + operating_system + "/" + browser);

		}

		this.webdriver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
		this.webdriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		
		this.webdriver.manage().window().setSize(new Dimension(1080, 1920));
		this.webdriver.manage().window().maximize();

		wait = new WebDriverWait(this.webdriver, max_wait_time);

	}


	private void set_driver_options(String web_proxy_enabled, String chrome_logging_enabled){

		switch (this.browser.toLowerCase()) {
		case "chrome":

			WebDriverManager.chromedriver().setup();

			options = new ChromeOptions(); 
			((ChromeOptions)options).setAcceptInsecureCerts(true);
			((ChromeOptions)options).setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
			((ChromeOptions)options).addArguments("start-maximized");
			((ChromeOptions)options).addArguments("disable-infobars");
			//((ChromeOptions)options).setHeadless(true);

			if (web_proxy_enabled.equalsIgnoreCase("yes")) options = set_web_proxy(options);


			//code for Chrome browser logging
			if(chrome_logging_enabled.equalsIgnoreCase("yes")){

				LoggingPreferences logPrefs = new LoggingPreferences();
				logPrefs.enable(LogType.BROWSER, Level.SEVERE);
				logPrefs.enable(LogType.BROWSER, Level.WARNING);

				((ChromeOptions)options).setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

			}

			break;

		case "firefox":

			WebDriverManager.firefoxdriver().setup();

			options = new FirefoxOptions(); 
			((FirefoxOptions)options).setAcceptInsecureCerts(true);
			System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

			if (web_proxy_enabled.equalsIgnoreCase("yes")) options = set_web_proxy(options);

			//FirefoxBinary firefoxBinary = new FirefoxBinary();
			//firefoxBinary.addCommandLineOptions("--headless");
			//f_options.setBinary(firefoxBinary);

			break;

		case "edge":

			WebDriverManager.edgedriver().setup();	

			options = new EdgeOptions();

			if (web_proxy_enabled.equalsIgnoreCase("yes")) options = set_web_proxy(options);

			break;

		case "internet explorer":	

			WebDriverManager.iedriver().setup();
			options = new InternetExplorerOptions();
			break;

		case "opera":

			WebDriverManager.operadriver().setup();
			options = new OperaOptions();
			break;

		default:

			System.out.println("===========================");
			System.out.println("[skipping test] " + browser + " is not a recognised web browser, please check config.");
			System.out.println("===========================");
			throw new SkipException("skipping test");

		}
	}

	private MutableCapabilities set_web_proxy(MutableCapabilities options) {

		this.web_proxy = new BrowserMobProxyServer();

		web_proxy.setTrustAllServers(true);
		web_proxy.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		web_proxy.start(0);

		seleniumProxy = ClientUtil.createSeleniumProxy(web_proxy);

		options.setCapability(CapabilityType.PROXY, seleniumProxy);

		web_proxy.newHar();

		return options;

	}

	private boolean build_machine_supports_desired_OperatingSystem(String operating_system) {

		// Check the current test config specified operating system matches build
		// machine, if not then skip test.
		// If multiple OS testing is required then consider turning on Selenium Grid
		// flag.

		boolean valid = false;

		if (operating_system.equalsIgnoreCase("windows") && os_name.indexOf("win") >= 0)
			valid = true;
		if (operating_system.equalsIgnoreCase("linux")
				&& (os_name.indexOf("nix") >= 0 || os_name.indexOf("nux") >= 0 || os_name.indexOf("aix") >= 0))
			valid = true;
		if (operating_system.equalsIgnoreCase("mac") && os_name.indexOf("mac") >= 0)
			valid = true;

		return valid;

	}

	/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	Class methods

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */		

	@Override public void get(String url) {webdriver.get(url);}
	@Override public String getCurrentUrl() {return webdriver.getCurrentUrl();}
	@Override public String getTitle() {return webdriver.getTitle();}
	@Override public List<WebElement> findElements(By by) {return webdriver.findElements(by);}
	@Override public WebElement findElement(By by) {return webdriver.findElement(by);}
	@Override public String getPageSource() {return webdriver.getPageSource();}
	@Override public void close() {webdriver.close();}
	@Override public void quit() {webdriver.quit();}
	@Override public Set<String> getWindowHandles() {return webdriver.getWindowHandles();}
	@Override public String getWindowHandle() {return webdriver.getWindowHandle();}
	@Override public TargetLocator switchTo() {return webdriver.switchTo();}
	@Override public Navigation navigate() {return webdriver.navigate();}
	@Override public Options manage() {return webdriver.manage();}	

	public boolean get_driver_enabled() {

		if (webdriver != null)  return true;

		return false;
	}

	public String get_browser() {

		return browser;
	}

	public String get_operating_system() {

		return operating_system;
	}

	public BrowserMobProxyServer get_web_proxy() {

		return web_proxy;

	}

	public WebDriverWait get_wait() {

		return wait;

	}


	public String get_home_url() {

		return home_url;

	}

	public void set_home_url(String url) {

		this.home_url = url;

	}

	/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	[INNER CLASS] - gives access to enhanced selenium methods through esm instance

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	public Enhanced_selenium_methods esm = new Enhanced_selenium_methods();  

	public final class Enhanced_selenium_methods {

		//===========================
		// Actions which if fail should throw Exception causing scenario fail
		//===========================


		public void goto_home_url() throws Exception  {

			webdriver.get(home_url);

			wait_for_ajax_to_finish();

		}


		public void click(By target) throws Exception  {

			wait_element_exists(target);

			try{

				webdriver.findElement(target).click();

			}catch(Throwable t){


                //[Warning] Only use this Javascript workaround in very limited circumstances as it's not user-realistic. 
                //In the example of "Element is obscured" thrown by the Edge browser, this is a bug in the driver rather than 
                //an actual DOM element obscuring the click. Therefore javascript click is used as a workaround. 
                //If the findElement click failed for another reason (i.e. a genuine reason), then an exception should 
				//correctly be thrown and the test marked as failed. 
				if (t.getMessage().contains("Element is obscured") && t.getMessage().contains("edge")){

					System.out.println("Known bug with Edge browser detected 'Element is obscured', using workaround javascript click");
					
					WebElement element = webdriver.findElement(target); 
					JavascriptExecutor executor = (JavascriptExecutor)webdriver; 
					executor.executeScript("arguments[0].click();", element);
		
				}else{
					throw new Exception(t.getMessage());  
				}

			}

			log_chrome_browser_warnings_and_errors(messsage_level_threshold);
			log_http_error_codes_and_slow_http_elements(millisecond_performance_threshold);

			wait_for_ajax_to_finish();

		}


		public void send_keys(By target,String textToSend) throws Exception {

			wait_element_exists(target);
			clear_text(target);

			webdriver.findElement(target).sendKeys(textToSend);

			log_chrome_browser_warnings_and_errors(messsage_level_threshold);
			log_http_error_codes_and_slow_http_elements(millisecond_performance_threshold);

			wait_for_ajax_to_finish();		


		}


		public void select_list_value_by_index(By target,int index) throws Exception{

			wait_element_exists(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByIndex(index);

			log_chrome_browser_warnings_and_errors(messsage_level_threshold);
			log_http_error_codes_and_slow_http_elements(millisecond_performance_threshold);

			wait_for_ajax_to_finish();	

		}

		public void select_list_value_by_text(By target,String text) throws Exception{

			wait_element_exists(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByVisibleText(text);


			log_chrome_browser_warnings_and_errors(messsage_level_threshold);
			log_http_error_codes_and_slow_http_elements(millisecond_performance_threshold);

			wait_for_ajax_to_finish();

			//[Fail-safe] Poll until dropDown menu text changes to what we expect.
			int iWaitTime = 0;
			while(!get_list_item_text(target).contains(text)){
				Thread.sleep(500);
				iWaitTime++;

				//System.out.println(iWaitTime + " polling element" + target);
				if (iWaitTime==10){break;}
			}	

		}


		public String get_text(By target) throws Exception {

			wait_element_exists(target);

			return webdriver.findElement(target).getText();

		}	

		public String get_inner_html(By target) throws Exception{

			wait_element_exists(target);

			return webdriver.findElement(target).getAttribute("innerHTML");

		}		


		public String get_list_item_text(By target) throws Exception {

			wait_element_exists(target);

			Select select = new Select(webdriver.findElement(target));

			return select.getFirstSelectedOption().getText();

		}

		public boolean check_text_exists(String text) throws Exception {


			return webdriver.getPageSource().toLowerCase().contains(text.toLowerCase());

		}	


		public boolean verify_image(By by) throws Exception {

			WebElement ImageFile = webdriver.findElement(by);
			return  (Boolean) ((JavascriptExecutor)webdriver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile);

		}	

		public void wait_element_exists(By target) throws Exception {

			wait.until(ExpectedConditions.presenceOfElementLocated(target));

		}	


		//===========================
		// Actions which if fail, should CATCH warning, but are not critical to stop test execution
		//===========================

		public void clear_text(By target) {

			try{

				//Clear text field if it has text before sending text.
				if(!webdriver.findElement(target).getAttribute("innerHTML").equals("") ||
						!webdriver.findElement(target).getText().equals("")){

					webdriver.findElement(target).clear();
				}


			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}	

		public int count_matching_elements(By target) {

			try{

				return webdriver.findElements(target).size();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return 0;

		}	

		public List<WebElement> get_all_matching_elements(By target)  {

			try{

				return webdriver.findElements(target);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return null;

		}	

		public boolean check_element_exists(By target) {


			try{

				if (webdriver.findElements(target).size()>0){

					return true;
				}

				return false;


			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return false;

		}	

		public boolean check_element_displayed(By target) {

			try{

				if (check_element_exists(target)){

					return webdriver.findElement(target).isDisplayed();
				}


				return false;

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return false;

		}	


		public boolean check_element_enabled(By target) {

			try{

				if (check_element_exists(target)){

					return webdriver.findElement(target).isEnabled();
				}

				return false;

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return false;

		}	

		public void wait_element_visible(By target) {

			try{

				wait.until(ExpectedConditions.visibilityOfElementLocated(target));

			}
			catch (Throwable t){

				standard_warning_output(t.getMessage());

			}
		}

		public void wait_element_invisible(By target) {

			try{
				wait.until(ExpectedConditions.invisibilityOfElementLocated(target));
			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}

		public void wait_element_clickable(By target) {

			try{
				wait.until(ExpectedConditions.elementToBeClickable(target));
			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}	

		public void wait_element_not_clickable(By target) {


			try{
				wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(target)));
			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}


		public void move_to_element_and_wait(By target) throws Exception  {

			long startTime = System.currentTimeMillis();

			wait_element_exists(target);

			try{

				//Do JS scroll too because sometimes action moveToElement is unreliable on its own. Espcially in firefox
				WebElement element = webdriver.findElement(target);
				((JavascriptExecutor) webdriver).executeScript("arguments[0].scrollIntoView(true);", element);

				Actions action = new Actions(webdriver);
				action.moveToElement(webdriver.findElement(target)).perform();


			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			//After moving to the element, wait for javascript events to finish. 
			wait_for_ajax_to_finish();
			wait_element_visible(target);
			highLight_element(target);

			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime); 

			if (duration > 5000){

				System.out.println("[warning] " + browser + " Execution time for move_to_element_and_verify took: " + duration + "MS");

			}	
		}		


		public void scroll_by_pixel(int pixels) {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollBy(0," + pixels +")", "");
				Thread.sleep(500);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void scroll_bottom_page()  {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
				Thread.sleep(500);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void scroll_top_page() {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollTo(0, 0)");
				Thread.sleep(500);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	



		public void highLight_element(By target)  {

			try{

				WebElement we = webdriver.findElement(target);
				((JavascriptExecutor) webdriver).executeScript("arguments[0].style.border='3px dotted blue'", we);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	

		public void delete_cookies() throws Exception {

			try{

				//With some browsers, a page needs to be open before attepting to clear cookies otherwise will result in failure.
				if (webdriver.getCurrentUrl().equals("data:,") || webdriver.getCurrentUrl().equals("about:blank")) {

					goto_home_url();

				}

				webdriver.manage().deleteAllCookies();
				//webdriver.navigate().refresh();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}


		public void wait_for_page_load() {

			try{

				new WebDriverWait(webdriver, 30).until((ExpectedCondition<Boolean>) wd ->
				((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}


		public void wait_for_ajax_to_finish() {

			long startTime = System.currentTimeMillis();

			wait_for_page_load(); 

			try{

				webdriver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
				((JavascriptExecutor) webdriver).executeAsyncScript(
						"var callback = arguments[arguments.length - 1];" +
								"var xhr = new XMLHttpRequest();" +
								"xhr.open('POST', '/" + "selenium_call" + "', true);" +
								"xhr.onreadystatechange = function() {" +
								"  if (xhr.readyState == 4) {" +
								"    callback(xhr.responseText);" +
								"  }" +
								"};" +
						"xhr.send();");

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}finally{

				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime); 
				//System.out.println("waiting for AJAX took: " + duration + "MS");

			}

		}	

		public void goto_new_tab() {

			try{

				String parentWindow;
				String childWindow;

				parentWindow = webdriver.getWindowHandle();
				childWindow = null;

				Set <String> allWindows =  webdriver.getWindowHandles();

				//Only attempt to switch to RecentTab, if a new tab exists. 
				for(String wHandle: allWindows){

					if (wHandle != parentWindow) {

						childWindow = wHandle;
					}
				}

				int attempts=1;
				if (!childWindow.equals(parentWindow)){
					while(webdriver.getWindowHandle().equals(parentWindow)) {
						webdriver.switchTo().window(childWindow);
						//Reporter.log("Switch window attempt:" +  attempts,true);
						attempts++;
					}
				}

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	

		//===========================
		// Logging related methods
		//===========================		

		public void clear_captured_proxy_data(){

			if(web_proxy == null) return;

			web_proxy.newHar();

		}


		private void log_chrome_browser_warnings_and_errors(int messsage_level_threshold) throws IOException {

			//only run if enabled and Chrome browser
			if(!chrome_logging_enabled.equalsIgnoreCase("yes")) return;
			if (!browser.toLowerCase().equals("chrome")) return;

			long startTime = System.currentTimeMillis();

			//messsage_level_threshold 0 - warning and Severe
			//messsage_level_threshold 1 - severe


			StringBuilder log_output =new StringBuilder(""); 

			int log_count = 0 ;

			LogEntries logEntries = webdriver.manage().logs().get(LogType.BROWSER);
			for (LogEntry entry : logEntries) {

				//skip outputting the non intrusive 404 Ajax call we insert which checks if the pages Ajax calls have completed
				if (entry.getMessage().contains("selenium_call")) continue;

				//if level 1 selected, dont show warning messages
				if (messsage_level_threshold == 1 && entry.getLevel().equals(Level.WARNING)) continue;


				log_output.append("[Chrome message:" + " Issues detected on URL:" + webdriver.getCurrentUrl() + System.lineSeparator() + 
						new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage() 
						+ System.lineSeparator() + System.lineSeparator());

				log_count++;

			}

			if (log_output.length() != 0){
				//System.out.print(log_output);

				write_to_static_log_file(log_output,"chrome-browser-warnings-errors/" + operating_system);	
			}

			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime); 


			if (duration > 5000){

				System.out.println("[warning] reading " + log_count + " logs with log_chrome_browser_warnings_and_errors took: " + duration + "MS");
			}


		}	


		private void log_http_error_codes_and_slow_http_elements(long millisecond_performance_threshold) throws IOException{

			long startTime = System.currentTimeMillis();

			if(web_proxy == null) return;

			StringBuilder log_output1 =new StringBuilder("");  
			StringBuilder log_output2 =new StringBuilder("");  

			int log_count = 0;

			List<HarEntry> entries = web_proxy.getHar().getLog().getEntries();
			for (HarEntry entry : entries) {

				if (entry.getResponse().getStatus() >= 400 & 
						!entry.getRequest().getUrl().contains("selenium_call")){

					log_output1.append(
							"[HTTP Error code:" + 
									entry.getResponse().getStatus() + "] " +
									entry.getRequest().getMethod() + " " +
									entry.getRequest().getUrl()  + " : via:" +
									entries.get(0).getRequest().getUrl() 
									+ System.lineSeparator());

				}


				if (entry.getTime()>= (millisecond_performance_threshold)){

					log_output2.append(
							"[Slow HTTP element:" + 
									entry.getTime() + "ms] " +
									entry.getRequest().getMethod() + " : " +
									entry.getRequest().getUrl()  + " : " +  
									entry.getResponse().getStatus() 
									+ System.lineSeparator());

				}

				log_count++;
			}


			if (log_output1.length() != 0){
				//System.out.print(log_output1);
				write_to_static_log_file(log_output1,"http-error-codes/" + operating_system + "-" + browser);	
			}
			if (log_output2.length() != 0){
				//System.out.print(log_output2);
				write_to_static_log_file(log_output2,"http-slow-resources/" + operating_system + "-" + browser);
			}


			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime); 

			if (duration > 5000){
				System.out.println("[warning] reading " + log_count + " logs with log_http_error_codes_and_slow_http_elements took: " + duration + "MS");
			}	

		}


		private void write_to_static_log_file(StringBuilder log_output, String log_dir) throws IOException  {

			FileWriter fw = null;

			File file_log_dir = new File(error_log_base_path + log_dir + "/" + "log.txt");
			FileUtils.touch(file_log_dir);

			fw = new FileWriter(file_log_dir, true);

			try {
				fw.write(log_output + System.lineSeparator());

			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				fw.close();
			}	

		}

		public void log_test_failure_and_take_screenshot(String testID,String stack_trace)  {

			try{

				//Convert web driver object to TakeScreenshot
				TakesScreenshot scrShot =((TakesScreenshot)webdriver);

				//Call getScreenshotAs method to create image file
				File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

				String uniqueID = UUID.randomUUID().toString();


				//dynamic log file per failure (using dat/time)
				String filePath = error_log_base_path + "test-failures/" + 
						operating_system + "-" + browser + "_" + uniqueID; 

				String screenshotPath = filePath + "/" + "screenshot.png";

				File DestFile=new File(screenshotPath);

				//Copy file at destination
				FileUtils.copyFile(SrcFile, DestFile);

				System.out.println("==============================================");
				System.out.println("[Test ID]");
				System.out.println(testID);
				System.out.println("");
				System.out.println("[Failed URL]");	
				System.out.println(webdriver.getCurrentUrl());
				System.out.println("");		
				System.out.println("[Page Title]");	
				System.out.println(webdriver.getTitle());
				System.out.println("");					
				System.out.println("[Environment]");
				System.out.println(operating_system + "_" + browser);
				System.out.println("");
				System.out.println("[Screenshot ands logs found here]");		
				System.out.println(filePath);
				System.out.println("");
				System.out.println("[Stack trace]");		
				System.out.println(stack_trace);	
				System.out.println("");


				//Output failed scenario name, URL + page title to text file next to screenshot
				File failed_scenario_details_file = new File(filePath + "/" + "failed_scenario_details.txt");

				FileWriter fw = new FileWriter(failed_scenario_details_file, false);

				try {
					fw.write("[Test ID]" + System.lineSeparator()  + testID + System.lineSeparator() + System.lineSeparator() +
							"[Failed URL]" + System.lineSeparator() + webdriver.getCurrentUrl() + System.lineSeparator() + System.lineSeparator() +
							"[Page Title]" + System.lineSeparator() + webdriver.getTitle() + System.lineSeparator() + System.lineSeparator() +
							"[Stack trace]" + System.lineSeparator() + stack_trace);

				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					fw.close();
				}	


				if(web_proxy == null) return;

				//Get the HAR data
				Har har = web_proxy.getHar();
				File harFile = new File(filePath + "/" + "proxy.har");

				//Write the HAR data
				har.writeTo(harFile);


			}catch(Throwable t){

				System.out.println("[Error when logging failure]" + t.getMessage()); 

			}

		}		


		private void standard_warning_output(String message){

			System.out.println("[Warning]");
			System.out.println(message);
			System.out.println("");
			System.out.println("[Continuing test scenerio]");	
			System.out.println("Selenium will fail if normal execution flow is impacted");
			System.out.println("");	

		}

	}

}

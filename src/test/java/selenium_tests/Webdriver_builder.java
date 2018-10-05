package selenium_tests;

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
	private final String web_proxy_enabled;
	private final String chrome_logging_enabled;
	private String home_url;


	private static String os_name = System.getProperty("os.name").toLowerCase();

	private String error_log_base_path = System.getProperty("user.dir").replace("\\", "/")  + 
			"/target/error-log/";

	private final int max_wait_time = 60;
	private final int performance_issue_tracking_threshold = 5;	

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
					throws MalformedURLException {

		this.operating_system = operating_system;
		this.browser = browser;
		this.web_proxy_enabled = web_proxy_enabled; 
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

			set_driver_options();

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

			set_driver_options();

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


		this.webdriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		this.webdriver.manage().window().setSize(new Dimension(1080, 1920));
		this.webdriver.manage().window().maximize();

		wait = new WebDriverWait(this.webdriver, max_wait_time);

	}


	private void set_driver_options(){

		switch (this.browser.toLowerCase()) {
		case "chrome":

			WebDriverManager.chromedriver().setup();

			options = new ChromeOptions(); 
			((ChromeOptions)options).setAcceptInsecureCerts(true);
			((ChromeOptions)options).setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
			((ChromeOptions)options).addArguments("start-maximized");
			((ChromeOptions)options).addArguments("disable-infobars");
			//options.setHeadless(true); 

			if (web_proxy_enabled.equalsIgnoreCase("yes")) options = set_web_proxy(options);


			//code for Chrome browser logging
			if(chrome_logging_enabled.equalsIgnoreCase("yes")){

				DesiredCapabilities caps = DesiredCapabilities.chrome();
				LoggingPreferences logPrefs = new LoggingPreferences();
				logPrefs.enable(LogType.BROWSER, Level.SEVERE);
				//logPrefs.enable(LogType.BROWSER, Level.WARNING);
				caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
				options.merge(caps);
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


		public void goto_url(String URL) throws Exception  {

			webdriver.get(URL);

			wait_for_ajax_to_finish();

		}

		public void goto_home_url() throws Exception  {

			webdriver.get(home_url);

			wait_for_ajax_to_finish();

		}

		public void click(By target) throws Exception  {

			focus_on(target);

			webdriver.findElement(target).click();

			wait_for_ajax_to_finish();
			find_http_errors();
			find_slow_http(performance_issue_tracking_threshold);

		}

		public void send_keys(By target,String textToSend) throws Exception {

			focus_on(target);
			clear_text(target);

			webdriver.findElement(target).sendKeys(textToSend);

			wait_for_ajax_to_finish();
			find_http_errors();
			find_slow_http(performance_issue_tracking_threshold);

		}

		public String get_text(By target) throws Exception {

			focus_on(target);

			return webdriver.findElement(target).getText();

		}	

		public String get_inner_html(By target) throws Exception{

			focus_on(target);

			return webdriver.findElement(target).getAttribute("innerHTML");

		}	

		public void select_list_value_by_index(By target,int index) throws Exception{

			focus_on(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByIndex(index);

			wait_for_ajax_to_finish();
			find_http_errors();
			find_slow_http(performance_issue_tracking_threshold);

		}

		public void select_list_value_by_text(By target,String text) throws Exception{

			focus_on(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByVisibleText(text);

			wait_for_ajax_to_finish();
			find_http_errors();
			find_slow_http(performance_issue_tracking_threshold);

			//[Fail-safe] Poll until dropDown menu text changes to what we expect.
			int iWaitTime = 0;
			while(!get_list_item_text(target).contains(text)){
				Thread.sleep(500);
				iWaitTime++;

				//System.out.println(iWaitTime + " polling element" + target);
				if (iWaitTime==10){break;}
			}	

		}

		public String get_list_item_text(By target) throws Exception {

			focus_on(target);

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

		public void wait_until_present(By target) throws Exception {

			wait.until(ExpectedConditions.presenceOfElementLocated(target));

		}	

		//===========================
		// Actions which if fail should give warning, but are not critical to stop test execution
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

		public void wait_until_visible(By target) {

			try{

				wait.until(ExpectedConditions.visibilityOfElementLocated(target));
			}
			catch (Throwable t){

				standard_warning_output(t.getMessage());

			}
		}

		public void wait_until_invisible(By target) {

			try{
				wait.until(ExpectedConditions.invisibilityOfElementLocated(target));
			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}

		public void wait_until_clickable(By target) {

			try{
				wait.until(ExpectedConditions.elementToBeClickable(target));
			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}	

		public void wait_until_not_clickable(By target) {


			try{
				wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(target)));
			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
		}


		public void goto_new_tab_if_exists() {

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


		public void focus_on(By target) throws Exception  {


			wait_until_present(target);

			//======Focusing Start ======
			try{
				WebElement element = webdriver.findElement(target);
				((JavascriptExecutor) webdriver).executeScript("arguments[0].scrollIntoView(true);", element);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}
			//======Focusing End ======

			wait_until_visible(target);

			wait_for_ajax_to_finish();


		}

		public void scroll_by_pixel(int pixels) {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollBy(0," + pixels +")", "");

				wait_for_ajax_to_finish();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void scroll_bottom_page()  {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");

				wait_for_ajax_to_finish();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void scroll_top_page() {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollTo(0, 0)");

				wait_for_ajax_to_finish();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	


		public void move_mouse_to_element(By target) throws Exception  {

			focus_on(target);

			try{

				Actions action = new Actions(webdriver);
				action.moveToElement(webdriver.findElement(target)).build().perform();

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

				if (webdriver.getCurrentUrl().equals("data:,") || 
						webdriver.getCurrentUrl().equals("about:blank")){

					return;
				}

				webdriver.manage().deleteAllCookies();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void wait_for_page_load() {

			try{

				JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webdriver;

				int iWaitTime = 0;
				int iWaitFinish = 200;	

				while (!javascriptExecutor.executeScript("return document.readyState")
						.toString().equals("complete")) {

					Thread.sleep(500);
					iWaitTime++;

					//System.out.println(iWaitTime + "/" + iWaitFinish + " Waiting for page to load (AJAX not included)");

					//fail-safe 
					if (iWaitTime==iWaitFinish){break;}
				}

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

		public void clear_captured_proxy_data(){

			if (web_proxy_enabled.equalsIgnoreCase("yes")){

				web_proxy.newHar();

			}	
		}


		public void find_severe_browser_messages() {

			if(chrome_logging_enabled.equalsIgnoreCase("yes")){

				long startTime = System.currentTimeMillis();
				
				LogEntries logEntries = webdriver.manage().logs().get(LogType.BROWSER);

				for (LogEntry entry : logEntries) {

					//skip outputting the non intrusive 404 Ajax call we insert which checks if the pages Ajax calls have completed
					if (entry.getMessage().contains("selenium_call")) continue;

					System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
					//do something useful with the data
				}
				
				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime); 
				//System.out.println("waiting for log scan took: " + duration + "MS");
				
			}
		}	


		public void find_http_errors() throws IOException{

			
			find_severe_browser_messages();
			
			if (web_proxy_enabled.equalsIgnoreCase("yes")){

				List<HarEntry> entries = web_proxy.getHar().getLog().getEntries();
				for (HarEntry entry : entries) {

					if (entry.getResponse().getStatus() >= 400 & 
							!entry.getRequest().getUrl().contains("selenium_call")){

						String message = 
								"[Error:" + 
										entry.getResponse().getStatus() + "] " +
										entry.getRequest().getMethod() + " " +
										entry.getRequest().getUrl()  + " : via:" +
										entries.get(0).getRequest().getUrl() 
										+ System.lineSeparator();

						System.out.print(message);
						log_http_issues(message,1);


					}

				}

			}

		}

		public void find_slow_http(long seconds) throws IOException{

			if (web_proxy_enabled.equalsIgnoreCase("yes")){

				List<HarEntry> entries = web_proxy.getHar().getLog().getEntries();
				for (HarEntry entry : entries) {

					if (entry.getTime()>= (seconds * 1000)){

						String message =
								"[Performance warning:" + 
										entry.getTime() + "ms] " +
										entry.getRequest().getMethod() + " : " +
										entry.getRequest().getUrl()  + " : " +  
										entry.getResponse().getStatus() 
										+ System.lineSeparator();

						System.out.print(message);
						log_http_issues(message,2);

					}

				}

			}

		}	

		public void log_http_issues(String issue_message, int type) throws IOException  {

			FileWriter fw = null ;

			//functional
			if (type ==1){


				String file_path_http_error_codes = error_log_base_path + "http-error-codes/" + 
						operating_system + "-" + browser; 

				File file_http_error_codes = new File(file_path_http_error_codes + 
						"/" + "log.txt");

				FileUtils.touch(file_http_error_codes);

				fw = new FileWriter(file_http_error_codes, true);

			}

			//performance
			if (type ==2){


				String file_path_http_slow_resources = error_log_base_path + "http-slow-resources/" + 
						operating_system + "-" + browser; 	

				File file_http_slow_resources = new File(file_path_http_slow_resources + 
						"/" + "log.txt");

				FileUtils.touch(file_http_slow_resources);

				fw = new FileWriter(file_http_slow_resources, true);

			}

			try {
				fw.write(issue_message + System.lineSeparator());

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

				String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date());

				String filePath = error_log_base_path + "test-failures/" + 
						operating_system + "-" + browser + "_" + currentDateTime; 

				String screenshotPath = filePath + "/" + "screenshot.png";

				File DestFile=new File(screenshotPath);

				//Copy file at destination
				FileUtils.copyFile(SrcFile, DestFile);

				System.out.println("==============================================");
				System.out.println("[Test ID]");
				System.out.println(testID);
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

				if (web_proxy_enabled.equalsIgnoreCase("yes")){

					//Get the HAR data
					Har har = web_proxy.getHar();
					File harFile = new File(filePath + "/" + "proxy.har");

					//Write the HAR data
					har.writeTo(harFile);

				}

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

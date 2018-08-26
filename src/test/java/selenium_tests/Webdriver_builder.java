package selenium_tests;

import static selenium_tests.Runner.driver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.*;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.testng.SkipException;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.*;
import java.util.concurrent.*;

public class Webdriver_builder implements WebDriver {

	private final WebDriver webdriver;
	private final String operating_system;
	private final String browser;
	private BrowserMobProxyServer web_proxy;
	private WebDriverWait wait;

	private final int max_wait_time = 10;

	private static String os_name = System.getProperty("os.name").toLowerCase();

	private String home_url;

	/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

This class is a webdriver which is self-configuring based on the parameters 
sent to it when its launched. 

Key features include:
(1) Can be a local or remote web driver.
	When used as a remote driver it acts as a hub and can be any 
	operating system, browser and browser version that your nodes can support.

	When used as a local driver it self-configures web driver binaries and 
	common driver capabilities for either chrome, firefox or Edge.

(2) Has a web_proxy for capturing HTTP traffic in HAR files. 

(3) Has an inner class with enhanced Selenium methods. This essentially allows the 
	calling method to utilise more robust Selenium calls for test script creation.

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */	

	@SuppressWarnings("deprecation")
	public Webdriver_builder(String operating_system, String browser, String browser_version,
			String web_proxy_enabled, String selenium_grid_enabled, String selenium_grid_hub)
					throws MalformedURLException {

		this.operating_system = operating_system;
		this.browser = browser;

		MutableCapabilities options = null;

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
			// Enable web proxy
			// ==================================
			if (web_proxy_enabled.equalsIgnoreCase("yes")) options = set_web_proxy(options);

			// ==================================
			// Set driver capabilities and launch
			// ==================================
			browser = browser.toLowerCase();

			switch (browser) {
			case "chrome":

				WebDriverManager.chromedriver().setup();

				((ChromeOptions)options).setAcceptInsecureCerts(true);
				((ChromeOptions)options).setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
				((ChromeOptions)options).addArguments("start-maximized");
				((ChromeOptions)options).addArguments("disable-infobars");
				//options.setHeadless(true); 

				this.webdriver = new ChromeDriver((ChromeOptions)options);
				break;

			case "firefox":

				WebDriverManager.firefoxdriver().setup();


				((FirefoxOptions)options).setAcceptInsecureCerts(true);
				System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

				//FirefoxBinary firefoxBinary = new FirefoxBinary();
				//firefoxBinary.addCommandLineOptions("--headless");
				//f_options.setBinary(firefoxBinary);

				this.webdriver = new FirefoxDriver((FirefoxOptions)options);
				break;

			case "edge":

				WebDriverManager.edgedriver().setup();	

				this.webdriver = new EdgeDriver((EdgeOptions)options);
				break;

			default:

				System.out.println("===========================");
				System.out.println("[skipping test] " + browser + " is not a recognised web browser, please check config.");
				System.out.println("===========================");
				throw new SkipException("skipping test");

			}


		// ==================================
		// Selenium Grid Enabled: will find node/s to match current
		// environment_configurations_to_test.xml test
		// ==================================

		} else {


			if (web_proxy_enabled.equalsIgnoreCase("yes")) options = set_web_proxy(options);

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

		this.webdriver.manage().window().setSize(new Dimension(1080, 1920));
		this.webdriver.manage().window().maximize();

		wait = new WebDriverWait(this.webdriver, max_wait_time);


	}

	private MutableCapabilities set_web_proxy(MutableCapabilities options) {


		this.web_proxy = new BrowserMobProxyServer();

		web_proxy.setTrustAllServers(true);
		web_proxy.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		web_proxy.start(0);

		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(web_proxy);

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

		if (webdriver != null) return true;
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

		}

		public void send_keys(By target,String textToSend) throws Exception {

			focus_on(target);
			clear_text(target);

			webdriver.findElement(target).sendKeys(textToSend);

			wait_for_ajax_to_finish();

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

		}

		public void select_list_value_by_text(By target,String text) throws Exception{

			focus_on(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByVisibleText(text);

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
								"xhr.open('POST', '/" + "Ajax_call" + "', true);" +
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

			if(web_proxy!= null){

				web_proxy.newHar();

			}	
		}


		public void get_all_scripts() {

			wait_for_ajax_to_finish();

			try{

				//String scriptToExecute = "return performance.getEntries({initiatorType : \"script\"});";
				String scriptToExecute = "return performance.getEntriesByType(\"resource\");";

				String netData = ((JavascriptExecutor)webdriver).executeScript(scriptToExecute).toString();
				String[] resourceNames = netData.split("name=");

				//========================================
				// Output resource details
				//========================================
				String[] _resourceNames = new String[resourceNames.length];

				System.out.println("==================================================");

				int scriptCounter = 0;

				for (int i=1;i<resourceNames.length;i++){

					if (resourceNames[i].contains("initiatorType=script")){

						_resourceNames[i] = resourceNames[i].split(", ")[0];
						System.out.println(_resourceNames[i]);
						scriptCounter++;
					}

				}
				System.out.println("==================================================");
				System.out.println(scriptCounter + " scripts executed by " + webdriver.getCurrentUrl());


			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	

		//================================================
		// Save Screenshots and log info (includes HTTP response code)
		//================================================

		public void output_logs_and_screenshot(String testID,String stack_trace)  {

			try{


				//Convert web driver object to TakeScreenshot
				TakesScreenshot scrShot =((TakesScreenshot)webdriver);

				//Call getScreenshotAs method to create image file
				File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

				String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date());

				String filePath = System.getProperty("user.dir").replace("\\", "/")  + 
						"/target/screenshots_logs_on_failure/" + 
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

				if(driver.get().get_web_proxy() != null){

					//Get the HAR data
					Har har = web_proxy.getHar();
					File harFile = new File(filePath + "/" + 
							operating_system + "_" + 
							browser + ".har");

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

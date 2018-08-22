package selenium_tests;

import java.net.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.opera.*;
import org.openqa.selenium.safari.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.remote.*;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.testng.SkipException;
import cucumber.api.Scenario;
import java.util.*;
import java.util.concurrent.*;

public class Webdriver_builder {

	private final WebDriver webdriver;
	private final String operating_system;
	private final String browser;
	private final String browser_headless;
	private BrowserMobProxyServer mobProxyServer;
	private WebDriverWait wait;
	private Scenario scenario;

	private final int max_wait_time = 10;

	private static String os_name = System.getProperty("os.name").toLowerCase();

	private String home_url;

/*
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
	Build WebDriver (constructor and private methods)
		
	A Webdriver_instance contains a configured webdriver, this webdriver can be:
	(1) Any major browser against specific OS 
	(2) Local or Remote 
	(3) headless mode 
	(4) contain Web Proxy 
	(5) configured explicit wait 
	(6) gives access to enhanced selenium methods via inner class
	
	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
*/	
	
	@SuppressWarnings("deprecation")
	public Webdriver_builder(String operating_system, String browser, String browser_version, String browser_headless,
			String web_proxy_enabled, String selenium_grid_enabled, String selenium_grid_hub)
					throws MalformedURLException {

		this.operating_system = operating_system;
		this.browser = browser;
		this.browser_headless = browser_version;

		MutableCapabilities options;

		// ==================================
		// Selenium Grid not Enabled: - will run on current machine. Will still attempt
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

			load_driver_from_file(operating_system);

			// Create browser specific webdriver with capabilities
			options = setBrowserCapabilities(browser, browser_headless, web_proxy_enabled);

			// Create the correct webdriver based on test requirements
			switch (browser) {
			case "chrome":
				this.webdriver = new ChromeDriver((ChromeOptions) options);
				break;
			case "firefox":
				this.webdriver = new FirefoxDriver((FirefoxOptions) options);
				break;
			case "edge":
				this.webdriver = new EdgeDriver((EdgeOptions) options);
				break;
			default:
				this.webdriver = new ChromeDriver((ChromeOptions) options);

			}

		} else {

			// ==================================
			// Selenium Grid Enabled: will find node/s to match current
			// environment_configurations_to_test.xml test
			// ==================================

			// build browser options / capabilities
			options = setBrowserCapabilities(browser, browser_headless, web_proxy_enabled);

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

	private void load_driver_from_file(String operating_system) {

		// Set driver property
		switch (operating_system) {

		case "windows":

			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")
					+ "\\src\\test\\resources\\browser_drivers\\windows\\chromedriver.exe");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")
					+ "\\src\\test\\resources\\browser_drivers\\windows\\geckodriver.exe");
			System.setProperty("webdriver.edge.driver", System.getProperty("user.dir")
					+ "\\src\\test\\resources\\browser_drivers\\windows\\MicrosoftWebDriver.exe");
			break;

		case "linux":

			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\linux\\todo");
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\linux\\todo");
			System.setProperty("webdriver.edge.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\linux\\todo");
			break;

		case "mac":

			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\mac\\todo");
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\mac\\todo");
			System.setProperty("webdriver.edge.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\mac\\todo");
			break;

		}
	}

	private MutableCapabilities setBrowserCapabilities(String browser, String browser_headless, String web_proxy) {

		MutableCapabilities options;

		browser = browser.toLowerCase();

		switch (browser) {

		case "chrome":

			options = new ChromeOptions();
			((ChromeOptions) options).setAcceptInsecureCerts(true);

			if (browser_headless.equalsIgnoreCase("yes"))
				((ChromeOptions) options).addArguments("headless");

			break;

		case "firefox":

			System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

			options = new FirefoxOptions();
			((FirefoxOptions) options).setAcceptInsecureCerts(true);

			if (browser_headless.equalsIgnoreCase("yes")) {

				FirefoxBinary firefoxBinary = new FirefoxBinary();
				firefoxBinary.addCommandLineOptions("--headless");
				((FirefoxOptions) options).setBinary(firefoxBinary);

			}

			break;

		case "edge":
			options = new EdgeOptions();
			break;

		case "internet explorer":
			options = new InternetExplorerOptions();
			break;
		case "safari":
			options = new SafariOptions();
			break;
		case "opera":
			options = new OperaOptions();
			break;

		default:
			System.out.println("===========================");
			System.out.println("[skipping test] " + browser + " is not a recognised web browser, please check config.");
			System.out.println("===========================");
			throw new SkipException("skipping test");
		}

		// Create a browser proxy to capture HTTP data for analysis
		if (web_proxy.equalsIgnoreCase("yes")) {

			this.mobProxyServer = new BrowserMobProxyServer();

			mobProxyServer.setTrustAllServers(true);
			mobProxyServer.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
			mobProxyServer.start(0);

			Proxy seleniumProxy = ClientUtil.createSeleniumProxy(mobProxyServer);

			options.setCapability(CapabilityType.PROXY, seleniumProxy);

			// System.out.println("Port started:" + mobProxyServer.getPort());

			mobProxyServer.newHar(this.operating_system + "_" + this.browser + ".har");

		}

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
		
	Getters and Setters
		
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
*/		
	
	
	public WebDriver get_webdriver() throws Exception {

		return this.webdriver;

	}

	public String get_browser() {

		return browser;
	}

	public String get_browser_headless() {

		return browser_headless;
	}

	public String get_operating_system() {

		return operating_system;
	}

	public BrowserMobProxyServer get_web_proxy() {

		return mobProxyServer;

	}

	public WebDriverWait get_wait() {

		return wait;

	}


	public Scenario get_cucumber_scenario() {

		return this.scenario;

	}


	public String get_home_url() {

		return home_url;

	}


	public void set_cucumber_scenario(Scenario scenario) {

		this.scenario = scenario;

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

		public String getInnerHTML(By target) throws Exception{

			focus_on(target);

			return webdriver.findElement(target).getAttribute("innerHTML");

		}	

		public void selectByIndex(By target,int index) throws Exception{

			focus_on(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByIndex(index);

			wait_for_ajax_to_finish();

		}

		public void selectByVisibleText(By target,String text) throws Exception{

			focus_on(target);

			Select select = new Select(webdriver.findElement(target));
			select.selectByVisibleText(text);

			wait_for_ajax_to_finish();

			//[Fail-safe] Poll until dropDown menu text changes to what we expect.
			int iWaitTime = 0;
			while(!getDropDownMenuText(target).contains(text)){
				Thread.sleep(500);
				iWaitTime++;

				//System.out.println(iWaitTime + " polling element" + target);
				if (iWaitTime==10){break;}
			}	

		}

		public String getDropDownMenuText(By target) throws Exception {

			focus_on(target);

			Select select = new Select(webdriver.findElement(target));

			return select.getFirstSelectedOption().getText();

		}

		public boolean text_exists(String text) throws Exception {

			return webdriver.getPageSource().toLowerCase().contains(text.toLowerCase());

		}	


		public boolean image_exists(By by) throws Exception {

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

		public int elementCount(By target) {

			try{

				return webdriver.findElements(target).size();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return 0;

		}	

		public List<WebElement> getAllElements(By target)  {

			try{

				return webdriver.findElements(target);

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return null;

		}	

		public boolean elementExists(By target) {


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

		public boolean element_displayed(By target) {

			try{

				if (elementExists(target)){

					return webdriver.findElement(target).isDisplayed();
				}


				return false;

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

			return false;

		}	


		public boolean element_enabled(By target) {

			try{

				if (elementExists(target)){

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

		public void scrollBy(int pixels) {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollBy(0," + pixels +")", "");

				wait_for_ajax_to_finish();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void scrollBottom()  {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");

				wait_for_ajax_to_finish();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}

		public void scrollTop() {

			try{

				((JavascriptExecutor) webdriver).executeScript("window.scrollTo(0, 0)");

				wait_for_ajax_to_finish();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	


		public void mouse_to(By target) throws Exception  {

			focus_on(target);

			try{

				Actions action = new Actions(webdriver);
				action.moveToElement(webdriver.findElement(target)).build().perform();

			}catch(Throwable t){

				standard_warning_output(t.getMessage());

			}

		}	

		public void highLight_element(By by)  {

			try{

				WebElement we = webdriver.findElement(by);
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

				//System.out.println("Selenium_core.waitForAjaxComplete() threw: " + e.getMessage());

				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime); 
				//System.out.println("waiting for AJAX took: " + duration + "MS");

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

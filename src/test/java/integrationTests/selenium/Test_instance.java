package integrationTests.selenium;


import java.net.*;

import org.openqa.selenium.*;
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

/*
 * An instance of this class will contain:
 *  A webdriver with capabilities
 *  Proxy server for capturing HTTP traffic
 *  Details on the configured test operating_system and browser details
 */

public class Test_instance {

	private final WebDriver webdriver;
	private final String operating_system;
	private final String browser;
	private final String browser_headless;
	private BrowserMobProxyServer mobProxyServer;
	private WebDriverWait wait;
	
	private final int max_wait_time = 30;

	private static String os_name = System.getProperty("os.name").toLowerCase();

	@SuppressWarnings("deprecation")
	public Test_instance(
			String operating_system, 
			String browser, 
			String browser_version,
			String browser_headless,
			String web_proxy_enabled,
			String selenium_grid_enabled,
			String selenium_grid_hub) throws MalformedURLException {

		this.operating_system = operating_system;
		this.browser = browser;
		this.browser_headless = browser_version;

		MutableCapabilities options;

		//==================================
		// Selenium Grid not Enabled: - will run on current machine. Will still attempt to execute all tests found in environment_configurations_to_test.xml however will skip if operating system doesnt match. 
		//==================================

		if (selenium_grid_enabled.equalsIgnoreCase("no")){

			if (!build_machine_supports_desired_OperatingSystem(operating_system)){

				System.out.println("************");
				System.out.println("[skipping test] This build machine does not support operating system: " + os_name);
				System.out.println("************");	
				throw new SkipException("skipping test");

			}

			load_driver_from_file(operating_system);

			//Create browser specific webdriver with capabilities
			options = setBrowserCapabilities(browser , browser_headless, web_proxy_enabled);

			//Create the correct webdriver based on test requirements
			switch(browser){
			case "chrome": this.webdriver = new ChromeDriver((ChromeOptions)options); break;
			case "firefox": this.webdriver = new FirefoxDriver((FirefoxOptions)options); break;
			case "edge": this.webdriver = new EdgeDriver((EdgeOptions)options);break;
			default: this.webdriver = new ChromeDriver((ChromeOptions)options); 

			}


		}else{

			//==================================
			// Selenium Grid Enabled: will find node/s to match current environment_configurations_to_test.xml test 
			//==================================

			//build browser options / capabilities
			options = setBrowserCapabilities(browser , browser_headless, web_proxy_enabled);

			//Set capabilityType, which is used to find grid node with matching capabilities
			options.setCapability(CapabilityType.BROWSER_NAME, browser);
			if (!browser_version.equals(""))options.setCapability(CapabilityType.BROWSER_VERSION, browser_version);
			if (!operating_system.equals(""))options.setCapability(CapabilityType.PLATFORM_NAME, operating_system);
			if (!operating_system.equals(""))options.setCapability(CapabilityType.PLATFORM, operating_system);

			//Launch Selenium grid, looking for node/s which match above capabilities
			this.webdriver = new RemoteWebDriver(new URL(selenium_grid_hub), options);

			System.out.println("Webdriver launched on node successfully for: " + operating_system + "/" + browser);

		}

		this.webdriver.manage().window().setSize(new Dimension(1080, 1920));
		this.webdriver.manage().window().maximize();
		
		wait = new WebDriverWait(this.webdriver,max_wait_time);
		

	}

	public WebDriver get_webdriver() throws Exception{

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
	
	public int get_max_wait_time() {
		
		return max_wait_time;
		
	
	}
	
	
	private void load_driver_from_file(String operating_system){

		//Set driver property
		switch(operating_system){

		case "windows":

			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\windows\\chromedriver.exe");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")  + "\\src\\test\\resources\\browser_drivers\\windows\\geckodriver.exe");
			System.setProperty("webdriver.edge.driver", System.getProperty("user.dir")   + "\\src\\test\\resources\\browser_drivers\\windows\\MicrosoftWebDriver.exe");
			break;

		case "linux":

			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\linux\\todo");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")  + "\\src\\test\\resources\\browser_drivers\\linux\\todo");
			System.setProperty("webdriver.edge.driver", System.getProperty("user.dir")   + "\\src\\test\\resources\\browser_drivers\\linux\\todo");
			break;

		case "mac":

			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\resources\\browser_drivers\\mac\\todo");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")  + "\\src\\test\\resources\\browser_drivers\\mac\\todo");
			System.setProperty("webdriver.edge.driver", System.getProperty("user.dir")   + "\\src\\test\\resources\\browser_drivers\\mac\\todo");
			break;

		}
	}

	private MutableCapabilities setBrowserCapabilities(String browser, String browser_headless, String web_proxy){

		MutableCapabilities options;

		browser = browser.toLowerCase();

		switch (browser){

		case "chrome":    

			options = new ChromeOptions(); 
			((ChromeOptions) options).setAcceptInsecureCerts(true);

			if (browser_headless.equalsIgnoreCase("yes"))((ChromeOptions) options).addArguments("headless");			

			break;

		case "firefox": 		

			System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");

			options = new FirefoxOptions(); 
			((FirefoxOptions) options).setAcceptInsecureCerts(true);

			if (browser_headless.equalsIgnoreCase("yes")){

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

		//Create a browser proxy to capture HTTP data for analysis
		if (web_proxy.equalsIgnoreCase("yes")){

			this.mobProxyServer = new BrowserMobProxyServer();

			mobProxyServer.setTrustAllServers(true);
			mobProxyServer.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
			mobProxyServer.start(0);

			Proxy seleniumProxy  = ClientUtil.createSeleniumProxy(mobProxyServer);

			options.setCapability(CapabilityType.PROXY, seleniumProxy);

			//System.out.println("Port started:" +  mobProxyServer.getPort());

			mobProxyServer.newHar(this.operating_system + "_" + this.browser + ".har");


		}

		return options;

	}


	private boolean build_machine_supports_desired_OperatingSystem(String operating_system){

		//Check the current test config specified operating system matches build machine, if not then skip test.
		//If multiple OS testing is required then consider turning on Selenium Grid flag.

		boolean valid = false;

		if (operating_system.equalsIgnoreCase("windows") && os_name.indexOf("win") >= 0) valid = true;
		if (operating_system.equalsIgnoreCase("linux") && (os_name.indexOf("nix") >= 0 || os_name.indexOf("nux") >= 0 || os_name.indexOf("aix") >= 0)) valid = true;
		if (operating_system.equalsIgnoreCase("mac") && os_name.indexOf("mac") >= 0) valid = true;

		return valid;



	}

}

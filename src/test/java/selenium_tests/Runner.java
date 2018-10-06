package selenium_tests;

import cucumber.api.CucumberOptions;

import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;

/*
The runner class is launched via the testNG XML file. 
It is responsible for generating a webdriver and running cucumber scenarios 
using the testNG @Test annotation.

Parameters provided from the TestNG and Maven XML files help build a unique webdriver with
specific capabilities. 

The simpliest most efficient way to build a webdriver is to build it once, and then make it
static so that it can be referenced by tests in other classes. 
This avoids other classes having to dynamically build their own webdriver instance, 
which is inefficient when this process has to be repeated across all the classes containing tests. 

However, the problem with a static webdriver is you only have one, making parallel processing
problematic. This is because parallel tests are all interacting with the same static webdriver.
This is overcome using ThreadLocal. 

Whenever a runner() instance is generated, it creates a webdriver instance 
and puts it in the static ThreadLocal container. 

Now in an example where 3 runner instances are launched at the same. 
Each runner generates a webdriver instance and stores it in a static ThreadLocal container.
Each runner then triggers tests (stored in other classes) in parallel. 

All of those tests don't generate a webdriver, they all simply reference the same 
ThreadLocal container. However rather than the parallel tests all impacting the same webdriver, 
they are now only impacting their local thread webdriver. 
Thus you achieve autonomous parallel processing without having to inefficiently 
recreate webdrivers for each test class.  
 */

@CucumberOptions( 
		//Tags (Send using Maven:[clean verify -Dcucumber.options="-t @Retest"]
		features = "src/test/resources/Features/",
		glue={"selenium_tests"},
		tags={"not @Homepage"}
		//Plugin (Json plugin dynamically created per test env. Used for reporting)
		)
public class Runner {

	public static ThreadLocal<Webdriver_builder> driver = new ThreadLocal<Webdriver_builder>();
	private TestNGCucumberRunner testNGCucumberRunner;
	private volatile static int testID;

	
	@BeforeClass(alwaysRun = true)
	@Parameters({"tags","operating_system","browser","browser_version"})
	public void setup(
			@Optional("") String tags,
			String operating_system,
			String browser,
			@Optional("") String browser_version
			) throws Exception{


		//System properties set in Maven POM.xml

		String selenium_grid_enabled = System.getProperty("selenium.grid.enabled");
		String selenium_grid_hub = System.getProperty("selenium.grid.hub");
		String web_proxy_enabled = System.getProperty("browsermob.proxy.enabled");	
		String chrome_logging_enabled = System.getProperty("chrome.logging.enabled");	
		

		//==========================
		// Output build configurations being tested
		//==========================	

		driver.set(null);
		driver.set(new Webdriver_builder(
				operating_system, 
				browser, 
				browser_version, 
				selenium_grid_enabled,
				selenium_grid_hub,
				web_proxy_enabled,
				chrome_logging_enabled));


		driver.get().set_home_url(System.getProperty("env.qa.url"));

		String uniqueID = UUID.randomUUID().toString();
		
		rewrite_cucumber_options(this.getClass(), "plugin", new String [] {"json:target/" + uniqueID + "_" + operating_system + "_" + browser + ".json"}, true);
		rewrite_cucumber_options(this.getClass(), "tags", new String [] {tags}, false);


		//Output once
		synchronized(this){
			if (++testID == 1){
				System.out.println("Test URL: " + System.getProperty("env.qa.url"));	
				System.out.println("Selenium Grid Enabled: " + selenium_grid_enabled );	
				System.out.println("Web Proxy Enabled: " + web_proxy_enabled);	
				System.out.println("Chrome logging enabled: " + chrome_logging_enabled);					
		
				if (selenium_grid_enabled.equals("yes")) System.out.println("Selenium Grid hub: " + selenium_grid_hub );		
			}
		}
		
		System.out.println("");
		System.out.println("Starting Test: (" + operating_system + " " +  browser + ")");	

		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
		

	}

	//==========================
	// Using TestNG DataProvider to fetch cucumber scenarios to be run within the @Test method
	//==========================	
	@DataProvider
	public Object[][] scenarios() {

		try{
			return testNGCucumberRunner.provideScenarios();
		}catch(Exception e){

			if (driver.get() == null) System.out.println("There was a problem initialising the Webdriver");

			throw e;

		}
	}

	@Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void run_scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cucumberFeature) throws Throwable {

		driver.get().esm.clear_captured_proxy_data();

		testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());


	}


	@AfterClass(alwaysRun = true)
	public void tear_down() throws Exception {

		testNGCucumberRunner.finish();

		if(driver.get().get_web_proxy() != null){

			driver.get().get_web_proxy().stop();

		}

		//==========================
		// Generate cucumber report and quit local thread web driver 
		//==========================	


		if (driver.get().get_driver_enabled()){

			Report_generator.GenerateMasterthoughtReport();	
			
			

			try {
				driver.get().quit();
				driver.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}else{

			System.out.println("There was an issue generating WebDriver for [OS/Browser]:"
					+ " " + driver.get().get_operating_system()
					+ "/" + driver.get().get_browser());

		}
	}


	//==============================================
	// Use reflection to dynamically change cucumber options (create unique .json files/results).
	//==============================================  

	static volatile boolean firstThread = true;

	private synchronized void rewrite_cucumber_options(Class<?> clazz, String key, Object newValue, boolean requireOffset) throws Exception{  

		if (requireOffset){

			//Slightly offset each parralel thread so each gets unique CucumberOptions (.json file name)
			if (!firstThread) Thread.sleep(6000);
			firstThread = false;

		}

		Annotation options = clazz.getAnnotation(CucumberOptions.class);                   //get the CucumberOptions annotation  
		InvocationHandler proxyHandler = Proxy.getInvocationHandler(options);              //setup handler so we can update Annotation using reflection. Basically creates a proxy for the Cucumber Options class
		Field f = proxyHandler.getClass().getDeclaredField("memberValues");                //the annotaton key/values are stored in the memberValues field
		f.setAccessible(true);                                                             //suppress any access issues when looking at f
		@SuppressWarnings("unchecked")
		Map<String, Object> memberValues = (Map<String, Object>) f.get(proxyHandler);      //get the key-value map for the proxy
		memberValues.remove(key);                                                          //renove the key entry...don't worry, we'll add it back
		memberValues.put(key,newValue);     
		//add the new key-value pair. The annotation is now updated.



	}  


}
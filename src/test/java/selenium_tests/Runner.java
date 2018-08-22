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


/*
The runner class is launched via the testNG XML file. 
It is responsible for generating a webdriver and running cucumber scenarios 
using the testNG @Test annotation.

When it generates a webdriver it uses parameters from both the TestNG and Maven XML files. 
These parameters dictate the type of webdriver to be created and its capabilities. 

Multiple runner classes can be generated at the same time via the testNG XML file 
This enables parallel processing.

In order for parallel processing to work, a unique webdriver instance needs to be created
for each thread. Runner class achieves this by storing each unique webdriver in a static 
threadLocal container. 

This enables tests located in other classes to make a static reference to the web driver
to control it. However, because it's in a ThreadLocal container, static references from different
threads do not impact each other. Thus allowing for an autonomous webdriver per thread. 

Alternative solutions to achieve parallel processing is to spin up a webdriver object within
the test classes themselves, however, this is inefficient due to the requirement to
rebuild a web driver instance per test suite class.
*/

@CucumberOptions( 
		//Tags (Send using Maven:[clean verify -Dcucumber.options="-t @Retest"]
		features = "src/test/resources/Features/",
		glue={"integrationTests"}
		//Plugin (Json plugin dynamically created per test env. Used for reporting)
		)
public class Runner {

	public static ThreadLocal<Webdriver_builder> driver = new ThreadLocal<Webdriver_builder>();
	private TestNGCucumberRunner testNGCucumberRunner;
	private volatile static int testID;
	
	@BeforeClass(alwaysRun = true)
	@Parameters({"operating_system","browser","browser_version","browser_headless"})
	public void setup(
			String operating_system,
			String browser,
			@Optional("") String browser_version,
			@Optional("no") String browser_headless ) throws Exception{

		//System properties set in Maven POM.xml
		String selenium_grid_enabled= System.getProperty("selenium.grid.enabled");
		String selenium_grid_hub = System.getProperty("selenium.grid.hub");
		String web_proxy_enabled= System.getProperty("browsermob.proxy.enabled");

		
		driver.set(new Webdriver_builder(
				operating_system, 
				browser, 
				browser_version, 
				browser_headless, 
				web_proxy_enabled, 
				selenium_grid_enabled,
				selenium_grid_hub));

		
		String home_url = System.getProperty("env.qa.url");
		
		driver.get().set_home_url(home_url);
		

		//==========================
		// Output build configurations being tested
		//==========================	

		synchronized(this){testID++;}

		//Output once
		if (testID == 1){
			System.out.println("Test URL: " + home_url);	
			System.out.println("Web Proxy Enabled: " + web_proxy_enabled);		
			System.out.println("Selenium Grid Enabled: " + selenium_grid_enabled );	
			if (selenium_grid_enabled.equals("yes")) System.out.println("Selenium Grid hub: " + selenium_grid_hub );		
		}

		System.out.println("");
		System.out.println("Starting Test ID: " + testID +
				" (" + operating_system + " " +  browser + ")");

		create_unique_json_file(this.getClass(), "plugin", new String [] {"json:target/" + operating_system + "_" + browser + ".json"});

		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
		

	}

	//==========================
	// Using TestNG DataProvider execute cucumber scenarios
	//==========================	

	@Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cucumberFeature) throws Throwable {

		if(driver.get().get_web_proxy()!= null){

			driver.get().get_web_proxy().newHar(driver.get().get_operating_system() + "_" + driver.get().get_browser() + ".har");

		}

		testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());

	}

	@DataProvider
	public Object[][] scenarios() {

		return testNGCucumberRunner.provideScenarios();


	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {

		testNGCucumberRunner.finish();

		if(driver.get().get_web_proxy() != null){

			driver.get().get_web_proxy().stop();

		}

		//==========================
		// Generate report and quit local thread web driver 
		//==========================	

		if (driver.get().get_webdriver() != null){

			Report_generator.GenerateMasterthoughtReport();	

			try {
				driver.get().get_webdriver().quit();
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

	private synchronized void create_unique_json_file(Class<?> clazz, String key, Object newValue) throws Exception{  

		//Slightly offset each parralel thread so each gets unique CucumberOptions (.json file name)
		if (!firstThread) Thread.sleep(6000);
		firstThread = false;

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
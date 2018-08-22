package integrationTests;

import cucumber.api.CucumberOptions;
import cucumber.api.Scenario;
import cucumber.api.java.After;
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

import integrationTests.selenium.Test_instance;
import integrationTests.selenium.ESM; //Enhanced Selenium Methods

@CucumberOptions( 
		//Tags (Send using Maven:[clean verify -Dcucumber.options="-t @Retest"]
		features = "src/test/resources/Features/",
		glue={"integrationTests"}
		//Plugin (Json plugin dynamically created per test env. Used for reporting)
		)
public class Runner {

	/*
	 * A test_instance contains a configured webdriver, this webdriver can be:
	 * (1) Any major browser against specific OS (2) Local or Remote (3) headless mode (4) contain Web Proxy (5) configured explicit wait
	 * test_instance webdriver is static and so can be referenced directly from other classes via Runner class. 
	 * Its using ThreadLocal and so there is a copy of test_instance per instance of Runner.
	 * Multiple Runner classes are generated via testNG xml file. This enables parallel processing.
	 * This gives scaleability control to testNG, rather than individual tests creating their own test_instance webdrivers. 
	 * This ensures central control of how many test_instance objects are created and configured via testNG xml.
	 * Any other class wanting to manipulate the webdriver just needs to reference test_instance, and only their local thread copy instance is affected.
	 */
	
	private Test_instance test; 
	public static ThreadLocal<Test_instance> test_instance = new ThreadLocal<Test_instance>();

	
	//==============================================
	// Calls the Grid (or non Grid) webdriver and pass on paramters to it
	//==============================================

	private volatile static int testID;

	private TestNGCucumberRunner testNGCucumberRunner;

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

		//Properties come from the environment_configurations_to_test.xml and POM.xml
		//environment_configurations_to_test.xml values may change between instances
		//POM.xml will remain consistent

		test = new Test_instance(
				operating_system, 
				browser, 
				browser_version, 
				browser_headless, 
				web_proxy_enabled, 
				selenium_grid_enabled,
				selenium_grid_hub);

		
		String home_url = System.getProperty("env.qa.url");
		
		test.set_home_url(home_url);
		
		test_instance.set(test);

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

		if(test.get_web_proxy()!= null){

			test.get_web_proxy().newHar(test.get_operating_system() + "_" + test.get_browser() + ".har");

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

		if(test.get_web_proxy() != null){

			test.get_web_proxy().stop();

		}

		//==========================
		// Generate report and quit local thread web driver 
		//==========================	

		if (test.get_webdriver() != null){

			Report_generator.GenerateMasterthoughtReport();	

			try {
				test.get_webdriver().quit();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}else{

			System.out.println("There was an issue generating WebDriver for [OS/Browser]:"
					+ " " + test.get_operating_system()
					+ "/" + test.get_browser());

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
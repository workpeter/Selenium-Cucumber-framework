package integrationTests;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import integrationTests.selenium.ESM;
import net.lightbody.bmp.core.har.Har;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static integrationTests.Runner.test_instance;

public class Listeners implements ITestListener {
	
	//==========================
	// If testNG registers a test as NOT a success then output logs and screenshot
	//==========================	

	@Override
	public void onTestFailure(ITestResult iTestResult) {

		log_output_and_screenshot(get_stack_trace(iTestResult));

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

		log_output_and_screenshot(get_stack_trace(iTestResult));

	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {

		log_output_and_screenshot(get_stack_trace(iTestResult));
	}

	
	@Override public void onTestStart(ITestResult arg0) {
		
		try {
			
			ESM.delete_cookies();
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		

	}
	

	@Override public void onTestSuccess(ITestResult iTestResult) {}
	@Override public void onFinish(ITestContext arg0) {}
	@Override public void onStart(ITestContext arg0) {}
	

	//==========================
	// Cucumber hook
	//==========================	

	@After
	public void save_cucumber_scenario_details(Scenario scenario) throws Exception{

		//Required to be able to query the scenario failure within the other methods
		test_instance.get().set_cucumber_scenario(scenario);

	}

	private String get_stack_trace(ITestResult arg0){

		return ExceptionUtils.getStackTrace(arg0.getThrowable());

	}

	//================================================
	// Save Screenshots and log info (includes HTTP response code)
	//================================================

	private void log_output_and_screenshot(String stack_trace)  {

		try{

			//exit method is cucumber scenario didnt fail
			if(!test_instance.get().get_cucumber_scenario().isFailed()) return;

			String scenario_name = test_instance.get().get_cucumber_scenario().getName();
			String browser = test_instance.get().get_browser();
			String operatingSystem = test_instance.get().get_operating_system();

			//Convert web driver object to TakeScreenshot
			TakesScreenshot scrShot =((TakesScreenshot)test_instance.get().get_webdriver());

			//Call getScreenshotAs method to create image file
			File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

			String currentDateTime =  new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date());

			String filePath = System.getProperty("user.dir").replace("\\", "/")  + 
					"/target/screenshots_logs_on_failure/" + 
					operatingSystem + "-" + browser + "_" + currentDateTime; 

			String screenshotPath = filePath + "/" + "screenshot.png";

			File DestFile=new File(screenshotPath);

			//Copy file at destination
			FileUtils.copyFile(SrcFile, DestFile);

			System.out.println("==============================================");
			System.out.println("[Scenario Failed]");
			System.out.println(scenario_name);
			System.out.println("");
			System.out.println("[Environment]");
			System.out.println(operatingSystem + "_" + browser);
			System.out.println("");
			System.out.println("[Screenshot ands logs found here]");		
			System.out.println(filePath);
			System.out.println("");
			System.out.println("[Stack trace]");		
			System.out.println(stack_trace);	
			System.out.println("");

			if(test_instance.get().get_web_proxy() != null){

				//Get the HAR data
				Har har = test_instance.get().get_web_proxy().getHar();
				File harFile = new File(filePath + "/" + 
						operatingSystem + "_" + 
						browser + ".har");

				//Write the HAR data
				har.writeTo(harFile);

			}

			//Output failed scenario name, URL + page title to text file next to screenshot
			File failed_scenario_details_file = new File(filePath + "/" + "failed_scenario_details.txt");

			FileWriter fw = new FileWriter(failed_scenario_details_file, false);

			try {
				fw.write("Failed Scenario: " + scenario_name + System.lineSeparator() +
						"Failed URL: " + test_instance.get().get_webdriver().getCurrentUrl() + System.lineSeparator() +
						"Page Title: " + test_instance.get().get_webdriver().getTitle() + System.lineSeparator() +
						"Stack trace:" + stack_trace);

			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				fw.close();
			}	


		}catch(Throwable t){

			System.out.println("[Error when logging failure]" + t.getMessage()); 

		}

	}	


}
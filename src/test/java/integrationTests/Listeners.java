package integrationTests;


import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import integrationTests.selenium.ESM;

import org.apache.commons.lang.exception.ExceptionUtils;

import static integrationTests.Runner.test_instance;

public class Listeners implements ITestListener {

	//==========================
	// Cucumber hooks
	//==========================	

	@Before
	public void clear_saved_stack_traces() {

		test_instance.get().set_selenium_stack_trace(null);
		test_instance.get().set_testNG_stack_trace(null);

	}

	@After
	public void save_cucumber_scenario_details(Scenario scenario) throws Exception{

		test_instance.get().set_cucumber_scenario(scenario);

		if(scenario.isFailed()) {

			System.out.println("capture_failed_cucumber_scenario");


		}

	}


	@Override
	public void onTestFailure(ITestResult iTestResult) {

		System.out.println("onTestFailure");
		save_testNG_stack_trace(iTestResult);
		log_output_and_screenshot();

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

		System.out.println("onTestFailedButWithinSuccessPercentage");
		save_testNG_stack_trace(iTestResult);


	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {

		System.out.println("onTestSkipped");
		save_testNG_stack_trace(iTestResult);

	}


	@Override public void onTestSuccess(ITestResult iTestResult) {
		System.out.println("onTestSuccess");
	}

	@Override public void onFinish(ITestContext arg0) {}
	@Override public void onStart(ITestContext arg0) {}
	@Override public void onTestStart(ITestResult arg0) {}

	private void save_testNG_stack_trace(ITestResult arg0){

		String testNG_stack_trace = ExceptionUtils.getStackTrace(arg0.getThrowable());

		test_instance.get().set_testNG_stack_trace(testNG_stack_trace);


	}

	private void log_output_and_screenshot(){

		try{

			if(test_instance.get().get_cucumber_scenario().isFailed()){

				ESM.log_output_and_screenshot(test_instance.get().get_cucumber_scenario().getName());
			}

		}catch (Exception e) {

			e.printStackTrace();
		}	
	}


}
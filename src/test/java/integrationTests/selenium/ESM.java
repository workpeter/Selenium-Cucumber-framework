package integrationTests.selenium;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.io.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;

import net.lightbody.bmp.core.har.Har;

//Any Selenium calls need to be against a specific instance of test_instance webdriver
import static integrationTests.Runner.test_instance;


import cucumber.api.Scenario;
import junit.framework.Assert;
import org.apache.commons.lang.exception.ExceptionUtils;

//Enhanced Selenium Methods
public final class ESM {

	//===========================
	// Actions which if fail should throw Exception causing scenario fail
	//===========================

	public static void goto_home_url() throws Exception  {

		String home_url = test_instance.get().get_home_url();
		test_instance.get().get_webdriver().get(home_url);

	}

	public static void click(By target) throws Exception  {

		focus_on(target);

		test_instance.get().get_webdriver().findElement(target).click();

	}

	public static void send_keys(By target,String textToSend) throws Exception {

		focus_on(target);
		clear_text(target);

		test_instance.get().get_webdriver().findElement(target).sendKeys(textToSend);

	}

	public static String get_text(By target) throws Exception {

		focus_on(target);

		return test_instance.get().get_webdriver().findElement(target).getText();

	}	

	public static String getInnerHTML(By target) throws Exception{

		focus_on(target);

		return test_instance.get().get_webdriver().findElement(target).getAttribute("innerHTML");

	}	

	public static void selectByIndex(By target,int index) throws Exception{

		focus_on(target);

		Select select = new Select(test_instance.get().get_webdriver().findElement(target));
		select.selectByIndex(index);

	}

	public static void selectByVisibleText(By target,String text) throws Exception{

		focus_on(target);

		Select select = new Select(test_instance.get().get_webdriver().findElement(target));
		select.selectByVisibleText(text);

		//[Fail-safe] Poll until dropDown menu text changes to what we expect.
		int iWaitTime = 0;
		while(!getDropDownMenuText(target).contains(text)){
			Thread.sleep(500);
			iWaitTime++;

			//System.out.println(iWaitTime + " polling element" + target);
			if (iWaitTime==10){break;}
		}	

	}

	public static String getDropDownMenuText(By target) throws Exception {

		focus_on(target);

		Select select = new Select(test_instance.get().get_webdriver().findElement(target));

		return select.getFirstSelectedOption().getText();

	}

	public static boolean text_exists(String text) throws Exception {

		return test_instance.get().get_webdriver().getPageSource().toLowerCase().contains(text.toLowerCase());

	}	


	public static boolean image_exists(By by) throws Exception {

		WebElement ImageFile = test_instance.get().get_webdriver().findElement(by);
		return  (Boolean) ((JavascriptExecutor)test_instance.get().get_webdriver()).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile);

	}	

	public static void wait_until_present(By target) throws Exception {

		test_instance.get().get_wait()
		.until(ExpectedConditions.presenceOfElementLocated(target));

	}	


	//===========================
	// Actions which if fail should give warning, but are not critical to stop test execution
	//===========================

	public static void clear_text(By target) {

		try{

			//Clear text field if it has text before sending text.
			if(!test_instance.get().get_webdriver().findElement(target).getAttribute("innerHTML").equals("") ||
					!test_instance.get().get_webdriver().findElement(target).getText().equals("")){

				test_instance.get().get_webdriver().findElement(target).clear();
			}


		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}
	}	

	public static int elementCount(By target) {

		try{

			return test_instance.get().get_webdriver().findElements(target).size();

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

		return 0;

	}	

	public static List<WebElement> getAllElements(By target)  {

		try{

			return test_instance.get().get_webdriver().findElements(target);

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

		return null;

	}	

	public static boolean elementExists(By target) {


		try{

			if (test_instance.get().get_webdriver().findElements(target).size()>0){

				return true;
			}

			return false;


		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

		return false;

	}	

	public static boolean element_displayed(By target) {

		try{

			if (elementExists(target)){

				return test_instance.get().get_webdriver().findElement(target).isDisplayed();
			}


			return false;

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

		return false;

	}	


	public static boolean element_enabled(By target) {

		try{

			if (elementExists(target)){

				return test_instance.get().get_webdriver().findElement(target).isEnabled();
			}

			return false;

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

		return false;

	}	

	public static void wait_until_visible(By target) {

		try{

			test_instance.get().get_wait()
			.until(ExpectedConditions.visibilityOfElementLocated(target));
		}
		catch (Throwable t){

			standard_warning_output(t.getMessage());

		}
	}

	public static void wait_until_invisible(By target) {

		try{
			test_instance.get().get_wait()
			.until(ExpectedConditions.invisibilityOfElementLocated(target));
		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}
	}

	public static void wait_until_clickable(By target) {

		try{
			test_instance.get().get_wait()
			.until(ExpectedConditions.elementToBeClickable(target));
		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}
	}	

	public static void wait_until_not_clickable(By target) {

		try{
			test_instance.get().get_wait()
			.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(target)));
		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}
	}


	public static void goto_new_tab_if_exists() {

		try{

			String parentWindow;
			String childWindow;

			parentWindow = test_instance.get().get_webdriver().getWindowHandle();
			childWindow = null;

			Set <String> allWindows =  test_instance.get().get_webdriver().getWindowHandles();

			//Only attempt to switch to RecentTab, if a new tab exists. 
			for(String wHandle: allWindows){

				if (wHandle != parentWindow) {

					childWindow = wHandle;
				}
			}

			int attempts=1;
			if (!childWindow.equals(parentWindow)){
				while(test_instance.get().get_webdriver().getWindowHandle().equals(parentWindow)) {
					test_instance.get().get_webdriver().switchTo().window(childWindow);
					//Reporter.log("Switch window attempt:" +  attempts,true);
					attempts++;
				}
			}

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}	


	public static void focus_on(By target) throws Exception  {


		wait_until_present(target);

		//======Focusing Start ======
		try{
			WebElement element = test_instance.get().get_webdriver().findElement(target);
			((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("arguments[0].scrollIntoView(true);", element);

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}
		//======Focusing End ======

		wait_until_visible(target);


	}

	public static void scrollBy(int pixels) {

		try{

			((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("window.scrollBy(0," + pixels +")", "");

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}

	public static void scrollBottom()  {

		try{

			((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}

	public static void scrollTop() {

		try{

			((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("window.scrollTo(0, 0)");

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}	


	public static void mouse_to(By target) throws Exception  {

		focus_on(target);

		try{

			Actions action = new Actions(test_instance.get().get_webdriver());
			action.moveToElement(test_instance.get().get_webdriver().findElement(target)).build().perform();

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}	

	public static void highLight_element(By by)  {

		try{

			WebElement we = test_instance.get().get_webdriver().findElement(by);
			((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("arguments[0].style.border='3px dotted blue'", we);

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}	

	public static void delete_cookies() throws Exception {

		try{

			if (test_instance.get().get_webdriver().getCurrentUrl().equals("data:,") || 
					test_instance.get().get_webdriver().getCurrentUrl().equals("about:blank")){

				return;
			}

			test_instance.get().get_webdriver().manage().deleteAllCookies();

		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}

	public static void wait_for_page_load() {

		try{

			JavascriptExecutor javascriptExecutor = (JavascriptExecutor) test_instance.get().get_webdriver();

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

	public static void wait_for_ajax_to_finish() {

		long startTime = System.currentTimeMillis();

		wait_for_page_load(); 

		try{

			test_instance.get().get_webdriver().manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
			((JavascriptExecutor) test_instance.get().get_webdriver()).executeAsyncScript(
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

	public static void get_all_scripts() {

		wait_for_ajax_to_finish();

		try{

			//String scriptToExecute = "return performance.getEntries({initiatorType : \"script\"});";
			String scriptToExecute = "return performance.getEntriesByType(\"resource\");";

			String netData = ((JavascriptExecutor)test_instance.get().get_webdriver()).executeScript(scriptToExecute).toString();
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
			System.out.println(scriptCounter + " scripts executed by " + test_instance.get().get_webdriver().getCurrentUrl());


		}catch(Throwable t){

			standard_warning_output(t.getMessage());

		}

	}	

	private static void standard_warning_output(String message){

		System.out.println("[Warning]");
		System.out.println(message);
		System.out.println("");
		System.out.println("[Continuing test scenerio]");	
		System.out.println("Selenium will fail if normal execution flow is impacted");
		System.out.println("");	

	}



}

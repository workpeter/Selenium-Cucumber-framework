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

import integrationTests.selenium.page_object_model.*;
import static integrationTests.Start_Test_Instance.test_instance;

public class Common_methods_and_pom {

	//===========================
	//create POM objects
	//===========================
	protected static POM_mainHeader mainHeader = new POM_mainHeader();
	protected static POM_basket basket = new POM_basket();
	protected static POM_productResults productResults = new POM_productResults();
	protected static POM_categorySplashPage categorySplashPage = new POM_categorySplashPage();	
	protected static POM_popup popup = new POM_popup();
	protected static POM_productPage productPage = new POM_productPage();
	protected static POM_popupBasket popupBasket = new POM_popupBasket();	

	//===========================
	// Common methods
	//===========================

	private WebDriverWait wait;
	
	public void gotoPage(String url) throws Exception{

	
		
		test_instance.get().get_webdriver().get(url);
		waitForAjaxComplete();

	}

	public void navigateBack() throws Exception{

		test_instance.get().get_webdriver().navigate().back();
		waitForAjaxComplete();

	}

	public int elementCount(By target) throws Exception {

		waitForAjaxComplete();
		return test_instance.get().get_webdriver().findElements(target).size();

	}	

	public List<WebElement> getAllElements(By target) throws Exception {

		waitForAjaxComplete();
		return  test_instance.get().get_webdriver().findElements(target);

	}	

	public boolean elementExists(By target) throws Exception{

		waitForAjaxComplete();

		if (test_instance.get().get_webdriver().findElements(target).size()>0){

			//[Fail-safe] check its clickable, to ensure it really does exist.
			try{

				WebDriverWait quickWait = new WebDriverWait(test_instance.get().get_webdriver(), 1);

				quickWait.until(ExpectedConditions.elementToBeClickable(target));

				return true;
			}
			catch (Exception e){
				return false;
			}
		}

		return false;

	}	

	public void waitForElement(By target) throws Exception{

		waitForAjaxComplete();

		try{

			wait = new WebDriverWait(test_instance.get().get_webdriver(),60);
			wait.until(ExpectedConditions.visibilityOfElementLocated(target));
		}
		catch (Exception e){
			System.out.println("Selenium has waiting its max time for the following element to be visible" + target );	
		}
	}

	public void waitForElementInvisible(By target) throws Exception{

		waitForAjaxComplete();

		try{
			wait = new WebDriverWait(test_instance.get().get_webdriver(),60);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(target));
		}catch(Exception e){

			System.out.println("Selenium has waiting its max time for the following element to not be visible" + target );	
		}

	}

	public void waitForElementNotClickable(By target) throws Exception{

		waitForAjaxComplete();

		try{
			wait = new WebDriverWait(test_instance.get().get_webdriver(),60);
			wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(target)));
		}catch(Exception e){

			System.out.println("Selenium has waiting its max time for the following element to not be clickable: " + target );	
		}

	}

	//==================================================
	// Wait for DOM ready and Ajax calls on page to complete (Start)
	//==================================================

	public void waitForPageLoad() throws Exception {

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

	}

	public void waitForAjaxComplete() throws Exception {

		long startTime = System.currentTimeMillis();

		waitForPageLoad(); 

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

		}catch(Exception e){

			//System.out.println("Selenium_core.waitForAjaxComplete() threw: " + e.getMessage());

			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime); 
			//			System.out.println("waiting for AJAX took: " + duration + "MS on URL: " 
			//					+ WebDriver_factory.getLocalThreadWebDriver().getCurrentUrl());

		}

	}

	//==================================================
	// Wait for DOM ready and Ajax calls on page to complete (End)
	//==================================================

	public boolean textExists(String text) throws Exception {

		waitForAjaxComplete();

		return test_instance.get().get_webdriver().getPageSource().toLowerCase().contains(text.toLowerCase());

	}

	public void click(By target) throws Exception{

		waitForElement(target);

		try{
			wait = new WebDriverWait(test_instance.get().get_webdriver(),60);
			wait.until(ExpectedConditions.elementToBeClickable(target));
		}catch(Exception e){
			System.out.println("Selenium has waiting its max time for the following element to be clickable: " + target );	
		}finally{
			test_instance.get().get_webdriver().findElement(target).click();
		}

	}

	public void sendkeys(By target,String textToSend) throws Exception{

		waitForElement(target);

		try{

			//Clear text field if it has text before sending text.
			if(!test_instance.get().get_webdriver().findElement(target).getAttribute("innerHTML").equals("") ||
					!test_instance.get().get_webdriver().findElement(target).getText().equals("")){

				test_instance.get().get_webdriver().findElement(target).clear();
			}

		}finally{
			test_instance.get().get_webdriver().findElement(target).sendKeys(textToSend);	
		}	

	}

	public String getText(By target) throws Exception{

		waitForElement(target);

		return test_instance.get().get_webdriver().findElement(target).getText();

	}	

	public String getInnerHTML(By target) throws Exception{

		waitForElement(target);

		return test_instance.get().get_webdriver().findElement(target).getAttribute("innerHTML");

	}	

	public void selectByIndex(By target,int index) throws Exception{

		waitForElement(target);

		Select select = new Select(test_instance.get().get_webdriver().findElement(target));
		select.selectByIndex(index);

		waitForAjaxComplete();

	}

	public void selectByVisibleText(By target,String text) throws Exception{

		waitForElement(target);

		Select select = new Select(test_instance.get().get_webdriver().findElement(target));
		select.selectByVisibleText(text);

		waitForAjaxComplete();

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

		waitForElement(target);

		Select select = new Select(test_instance.get().get_webdriver().findElement(target));

		return select.getFirstSelectedOption().getText();

	}

	public void gotoNewTabIfExists() throws Exception{

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

	}	

	public void deleteCookies() throws Exception{

		if (test_instance.get().get_webdriver().getCurrentUrl().equals("data:,") || 
				test_instance.get().get_webdriver().getCurrentUrl().equals("about:blank")){

			return;
		}

		test_instance.get().get_webdriver().manage().deleteAllCookies();

	}

	//================================================
	// - Scrolling  (code Start)
	//================================================

	public void scrollTo(By target) throws Exception {

		WebElement element = test_instance.get().get_webdriver().findElement(target);
		((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("arguments[0].scrollIntoView(true);", element);

		waitForAjaxComplete();

	}

	public void scrollBy(int pixels) throws Exception {

		((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("window.scrollBy(0," + pixels +")", "");
		waitForAjaxComplete();

	}

	public void scrollBottom() throws Exception {

		((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		waitForAjaxComplete();

	}

	public void scrollTop() throws Exception {

		((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("window.scrollTo(0, 0)");
		waitForAjaxComplete();

	}	

	//================================================
	// - Scrolling  (code End)
	//================================================

	public void mouseTo(By target) throws Exception {

		waitForElement(target);

		Actions action = new Actions(test_instance.get().get_webdriver());
		action.moveToElement(test_instance.get().get_webdriver().findElement(target)).build().perform();
		waitForAjaxComplete();

	}	

	public void highLightElement(By by) throws Exception  {

		WebElement we = test_instance.get().get_webdriver().findElement(by);
		((JavascriptExecutor) test_instance.get().get_webdriver()).executeScript("arguments[0].style.border='3px dotted blue'", we);

	}	

	
	
	
	//================================================
	// Save Screenshots and log info (includes HTTP response code)
	//================================================

	public static void takeSnapShotAndLogs(String scenarioName) throws Exception{

		
		
		
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

		System.out.println("");
		System.out.println("Scenario Failed: " + scenarioName);
		System.out.println("Environment: " + operatingSystem + "_" + browser);
		System.out.println("Screenshot ands logs found here: ");		
		System.out.println(filePath);
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
			fw.write("Failed Scenario: " + scenarioName + System.lineSeparator() +
					"Failed URL: " + test_instance.get().get_webdriver().getCurrentUrl() + System.lineSeparator() +
					"Page Title: " + test_instance.get().get_webdriver().getTitle()); 	
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			fw.close();
		}	

	}

	public void getAllJS() throws Exception{

		waitForAjaxComplete();

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

	}	

	public boolean checkImageExists(By by) throws Exception{

		waitForAjaxComplete();

		WebElement ImageFile = test_instance.get().get_webdriver().findElement(by);
		return  (Boolean) ((JavascriptExecutor)test_instance.get().get_webdriver()).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile);

	}

}

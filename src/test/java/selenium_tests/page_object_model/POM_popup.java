package selenium_tests.page_object_model;

import org.openqa.selenium.By;
import static selenium_tests.Runner.driver;

public final class POM_popup  {

	private static By btnClose = By.xpath("//a[@class=\"acsCloseButton acsAbandonButton \"]");	
	public static By linkCookieGotIt = By.xpath("//*[@id=\"__tealiumGDPRecModal\"]/div/div/div[2]/a");
	
	public static void escPopup() throws Exception{

		
		driver.get().esm.wait_for_ajax_to_finish();

		if(driver.get().esm.check_element_displayed(btnClose)){ 

			driver.get().esm.click(btnClose);
			driver.get().esm.wait_element_invisible(btnClose);
		}

		if(driver.get().esm.check_element_displayed(linkCookieGotIt)){

			driver.get().esm.click(linkCookieGotIt);
			driver.get().esm.wait_element_invisible(linkCookieGotIt);

		}	
		
	}

}

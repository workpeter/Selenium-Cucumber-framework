package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;
import integrationTests.selenium.ESM;

public final class POM_popup  {

	private static By btnClose = By.xpath("//a[@class=\"acsCloseButton acsAbandonButton \"]");	
	public static By linkCookieGotIt = By.xpath("//*[@id=\"__tealiumGDPRecModal\"]/div/div/div[2]/a");
	
	public static void escPopup() throws Exception{

		
		ESM.wait_for_ajax_to_finish();

		if(ESM.element_displayed(btnClose)){ 

			ESM.click(btnClose);
			ESM.wait_until_invisible(btnClose);
		}

		if(ESM.element_displayed(linkCookieGotIt)){

			ESM.click(linkCookieGotIt);
			ESM.wait_until_invisible(linkCookieGotIt);

		}	
		
	}

}

package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;

import integrationTests.selenium.Common_methods_and_pom;

public class POM_popup extends Common_methods_and_pom {

	private By btnClose = By.xpath("//a[@class=\"acsCloseButton acsAbandonButton \"]");	

	public By linkCookieGotIt = By.xpath("//*[@id=\"__tealiumGDPRecModal\"]/div/div/div[2]/a");
	
	
	public void escPopup() throws Exception{

		
		waitForAjaxComplete();

		if(elementExists(btnClose)){ 

			click(btnClose);
			waitForElementInvisible(btnClose);
		}

		if(elementExists(linkCookieGotIt)){

			click(linkCookieGotIt);
			waitForElementInvisible(linkCookieGotIt);

		}	
		
	}

}

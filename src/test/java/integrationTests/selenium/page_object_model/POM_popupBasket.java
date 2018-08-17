package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;

import integrationTests.selenium.Common_methods_and_pom;

public class POM_popupBasket extends Common_methods_and_pom {

	private By btnContinueShopping = By.xpath("//button[@class=\"button button--full button--secondary\"]");	
	private By loadingWheel = By.xpath("//div[@class=\"ac-loading-wheel ac-loading-wheel--contained\"]");
	
	public void checkContinueShopping() throws Exception{

		popup.escPopup();
		
		waitForElementInvisible(loadingWheel);
		click(btnContinueShopping);

	}


}

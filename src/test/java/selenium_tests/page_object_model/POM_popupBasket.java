package selenium_tests.page_object_model;

import org.openqa.selenium.By;
import static selenium_tests.Runner.driver;


public final class POM_popupBasket {

	private static By btnContinueShopping = By.xpath("//button[@class=\"button button--full button--secondary\"]");	
	private static By loadingWheel = By.xpath("//div[@class=\"ac-loading-wheel ac-loading-wheel--contained\"]");
	
	public static void checkContinueShopping() throws Exception{

		POM_popup.escPopup();
		
		driver.get().esm.wait_until_invisible(loadingWheel);
		driver.get().esm.click(btnContinueShopping);

	}


}

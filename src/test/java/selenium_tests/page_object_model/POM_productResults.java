package selenium_tests.page_object_model;

import org.openqa.selenium.By;
import static selenium_tests.Runner.driver;

public final class POM_productResults  {

	public static By txtSearchResultCount = By.xpath("//div[@class=\"font-standard md-hidden search-results-count\"]");
	public static By dropProductSort = By.xpath("//select[@class=\"font-standard form-control sort-select\"]");
	public static By txtNoResults = By.xpath("//h1[@class=\"no-results__heading\"]");	
	public static By btnAddToBasketFirstProduct = By.xpath("(//button[@class=\" add-to-trolley-button button button--primary button--full add-to-trolley-button--beta\"])[1]");	
	public static By btnAddToBasketSecondProduct = By.xpath("(//button[@class=\" add-to-trolley-button button button--primary button--full add-to-trolley-button--beta\"])[2]");
	public static By btnChooseOptionsFirstProduct = By.xpath("(//a[@class=\"ac-product-cta__button ac-product-cta__button--secondary\"])[1]");	
	public static By btnChooseOptionsSecondProduct = By.xpath("(//a[@class=\"ac-product-cta__button ac-product-cta__button--secondary\"])[2]");	

	public static void add_first_product_to_Basket_via_productResults(int quantity) throws Throwable {

		for (int i=0;i< quantity;i++){

			POM_popup.escPopup(); 

			if(!driver.get().esm.check_element_exists(btnChooseOptionsFirstProduct)){

				driver.get().esm.click(btnAddToBasketFirstProduct);
				POM_popupBasket.checkContinueShopping();

			}else{
				driver.get().esm.click(btnChooseOptionsFirstProduct);
				
				POM_productPage.adds_product_to_basket_via_productPage();
				driver.get().get_webdriver().navigate().back();
			}

		}

	}

	public static void add_second_product_to_Basket_via_productResults(int quantity) throws Throwable {

		for (int i=0;i< quantity;i++){

			POM_popup.escPopup(); //if required

			if(!driver.get().esm.check_element_exists(btnChooseOptionsSecondProduct)){

				driver.get().esm.click(btnAddToBasketSecondProduct);
				POM_popupBasket.checkContinueShopping();

			}else{
				driver.get().esm.click(btnChooseOptionsSecondProduct);
				POM_productPage.adds_product_to_basket_via_productPage();
				driver.get().get_webdriver().navigate().back();
			}

		}

	}

}

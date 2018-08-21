package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;
import integrationTests.selenium.ESM;
import static integrationTests.Runner.test_instance;

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

			if(!ESM.elementExists(btnChooseOptionsFirstProduct)){

				ESM.click(btnAddToBasketFirstProduct);
				POM_popupBasket.checkContinueShopping();

			}else{
				ESM.click(btnChooseOptionsFirstProduct);
				
				POM_productPage.adds_product_to_basket_via_productPage();
				test_instance.get().get_webdriver().navigate().back();
			}

		}

	}

	public static void add_second_product_to_Basket_via_productResults(int quantity) throws Throwable {

		for (int i=0;i< quantity;i++){

			POM_popup.escPopup(); //if required

			if(!ESM.elementExists(btnChooseOptionsSecondProduct)){

				ESM.click(btnAddToBasketSecondProduct);
				POM_popupBasket.checkContinueShopping();

			}else{
				ESM.click(btnChooseOptionsSecondProduct);
				POM_productPage.adds_product_to_basket_via_productPage();
				test_instance.get().get_webdriver().navigate().back();
			}

		}

	}

}

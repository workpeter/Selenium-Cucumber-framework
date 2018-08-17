package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;

import integrationTests.selenium.Common_methods_and_pom;
import integrationTests.selenium.step_definitions.Basket_feature;

public class POM_productResults extends Common_methods_and_pom {

	public By txtSearchResultCount = By.xpath("//div[@class=\"font-standard md-hidden search-results-count\"]");
	public By dropProductSort = By.xpath("//select[@class=\"font-standard form-control sort-select\"]");
	public By txtNoResults = By.xpath("//h1[@class=\"no-results__heading\"]");	
	public By btnAddToBasketFirstProduct = By.xpath("(//button[@class=\" add-to-trolley-button button button--primary button--full add-to-trolley-button--beta\"])[1]");	
	public By btnAddToBasketSecondProduct = By.xpath("(//button[@class=\" add-to-trolley-button button button--primary button--full add-to-trolley-button--beta\"])[2]");
	public By btnChooseOptionsFirstProduct = By.xpath("(//a[@class=\"ac-product-cta__button ac-product-cta__button--secondary\"])[1]");	
	public By btnChooseOptionsSecondProduct = By.xpath("(//a[@class=\"ac-product-cta__button ac-product-cta__button--secondary\"])[2]");	

	public void add_first_product_to_Basket_via_productResults(int quantity) throws Throwable {

		for (int i=0;i< quantity;i++){

			popup.escPopup(); //if required

			if(!elementExists(btnChooseOptionsFirstProduct)){

				click(btnAddToBasketFirstProduct);
				popupBasket.checkContinueShopping();

			}else{
				click(btnChooseOptionsFirstProduct);
				new Basket_feature().adds_product_to_basket();
				navigateBack();
			}

		}

	}

	public void add_second_product_to_Basket_via_productResults(int quantity) throws Throwable {

		for (int i=0;i< quantity;i++){

			popup.escPopup(); //if required

			if(!elementExists(btnChooseOptionsSecondProduct)){

				click(btnAddToBasketSecondProduct);
				popupBasket.checkContinueShopping();

			}else{
				click(btnChooseOptionsSecondProduct);
				new Basket_feature().adds_product_to_basket();
				navigateBack();
			}

		}

	}

}

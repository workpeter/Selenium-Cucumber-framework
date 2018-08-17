package integrationTests.selenium.step_definitions;

import org.testng.Assert;
import cucumber.api.java.en.*;
import integrationTests.selenium.Common_methods_and_pom;

public class Basket_feature extends Common_methods_and_pom {

	@When("^customer views basket$")
	public void customer_views_basket() throws Throwable {

		popup.escPopup(); 
		mainHeader.openBasket();
		
	}

	@When("^adds product to basket$")
	public void adds_product_to_basket() throws Throwable {

		productPage.adds_product_to_basket_via_productPage();

	}

	@When("^adds product: (.+) to basket twice$")
	public void adds_product_to_basket_twice(String product) throws Throwable {

		productPage.adds_product_to_basket_via_productPage();
		productPage.adds_product_to_basket_via_productPage();

	}

	@When("^adds first product to basket x(\\d+) quantity$")
	public void adds_first_product_to_basket_x_quantity(int quantity) throws Throwable {

		productResults.add_first_product_to_Basket_via_productResults(quantity);

	}

	@When("^adds second product to basket x(\\d+) quantity$")
	public void adds_second_product_to_basket_x_quantity(int quantity) throws Throwable {

		productResults.add_second_product_to_Basket_via_productResults(quantity);

	}
	
	@When("customer removes firt product from basket")
	public void customer_removes_firt_product_from_basket() throws Throwable {

	    basket.remove_first_product_from_basket();
	    
	}
	

	@When("customer changes quantity of first product to x(\\d+)")
	public void customer_changes_quantity_of_first_product_to_x(int quantity) throws Throwable {
	
		basket.change_quantity_first_product(quantity);
		
	}
	
	
	@Then("^empty basket is shown$")
	public void empty_basket_is_shown() throws Throwable {

		popup.escPopup(); 
		
		Assert.assertTrue(textExists("Your trolley is currently empty") ||
				textExists("your shopping trolley is empty"));
		
	}
	
	@Then("^basket with (.+) products and (.+) quantity is shown$")
	public void basket_with_products_and_quantity_is_shown(String productCount, String quantityCount) throws Throwable {

		popup.escPopup();
		basket.wait_for_basket_to_load();

		//convert expected cucumber values from strings to integers
		int iproductCount = Integer.valueOf(productCount);
		int iquantityCount = Integer.valueOf(quantityCount);	

		Assert.assertEquals(basket.productCount(),iproductCount);
		Assert.assertEquals(basket.quantityCount(),iquantityCount);

	}
	
}

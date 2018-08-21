package integrationTests.selenium.step_definitions;

import static integrationTests.Runner.test_instance;

import org.testng.Assert;
import cucumber.api.java.en.*;
import integrationTests.Runner;
import integrationTests.selenium.Common_methods_and_pom;


public class Product_feature extends Common_methods_and_pom {

	String product;
	
	@Given("^customer is on product page : (.+)$")
	public void customer_is_on_product_page(String product) throws Throwable {
		
		//Goto Homepage
		goto_url(Runner.get_env_url());
		popup.escPopup(); 
		
		customer_searches_by_product_using_search_feature(product);
		customer_clicks_on_first_product();
		
	}
	
	@When("^customer searches by product: (.+) using search feature$")
	public void customer_searches_by_product_using_search_feature(String product) throws Throwable {

		mainHeader.searchBar(product);
		
		Assert.assertTrue(text_exists("Filter by"));
		
		this.product = product;

	}
	
	@When("^customer clicks on first product$")
	public void customer_clicks_on_first_product() throws Throwable {
	    
		productPage.click_on_first_product();
			
	}

	@When("^customer sorts by price low-to-high$")
	public void customer_sorts_by_price_low_to_high() throws Throwable {

		productPage.sort_products("Price: Low - High");
		
	}


	@When("^customer sorts by price high-to-low$")
	public void customer_sorts_by_price_high_to_low() throws Throwable {

		productPage.sort_products("Price: High - Low");
		
		
	}

	@When("^customer sorts by customer rating$")
	public void customer_sorts_by_customer_rating() throws Throwable {

	
		productPage.sort_products("Customer Rating");
		
	}
	
	@When("^customer changes store location to : (.+)$")
	public void customer_changes_store_location_to(String postcode) throws Throwable {
	  
		
		productPage.change_store_location(postcode);
		
	}
	
	
	@Then("^product page is shown$")
	public void product_page_is_shown() throws Throwable {
		
		Assert.assertTrue(text_exists("About this product"));
		Assert.assertTrue(elementExists(productPage.txtProductPrice));
		Assert.assertTrue(get_text(productPage.txtProductPrice).contains("£"));
	}
	
	@Then("^results of matching products are shown by order of price low-to-high$")
	public void results_of_matching_products_are_shown_by_order_of_price_low_to_high() throws Throwable {
	
		Assert.assertEquals(getDropDownMenuText(productPage.dropSort),"Price: Low - High");
	}
	
	
	@Then("^results of matching products are shown by order of price high-to-low$")
	public void results_of_matching_products_are_shown_by_order_of_price_high_to_low() throws Throwable {

		Assert.assertEquals(getDropDownMenuText(productPage.dropSort),"Price: High - Low");	
		
	}
	
	@Then("^results of matching products are shown by order of customer rating$")
	public void results_of_matching_products_are_shown_by_order_of_customer_rating() throws Throwable {
		
		Assert.assertEquals(getDropDownMenuText(productPage.dropSort),"Customer Rating");	
		
	}
	

	@Then("^customer can see stock availability$")
	public void customer_can_see_stock_availability() throws Throwable {

		Assert.assertTrue(text_exists("Order now, collect right away") || 
						  text_exists("Not in stock") || 
						  text_exists("Not available") || 
						  text_exists("Delivery within") ||
						  text_exists("How would you like to get your item")
				);
				
	}

	@Then("^customer can see important product data$")
	public void customer_can_see_important_product_data() throws Throwable {

		Assert.assertTrue(elementExists(productPage.txtPrice));		
		Assert.assertTrue(elementExists(productPage.txtProductDescription));	
		Assert.assertTrue(image_exists(productPage.imgProduct));			
		Assert.assertTrue(elementExists(productPage.linkProductOwner));		

	}	
	
	@Then("^customer can see related products$")
	public void customer_can_see_related_products() throws Throwable {

		Assert.assertTrue(elementExists(productPage.imgRelatedProduct));
		
	}

}

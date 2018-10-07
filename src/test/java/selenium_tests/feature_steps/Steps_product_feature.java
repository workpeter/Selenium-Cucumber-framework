package selenium_tests.feature_steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import selenium_tests.page_object_model.*;

import static selenium_tests.Runner.driver;


import org.testng.Assert;

public class Steps_product_feature {

	String product;
	
	@Given("^customer is on product page : (.+)$")
	public void customer_is_on_product_page(String product) throws Throwable {
		
		//Goto Homepage
		driver.get().esm.goto_home_url();
		driver.get().esm.delete_cookies();
		
		POM_popup.escPopup(); 
		
		customer_searches_by_product_using_search_feature(product);
		customer_clicks_on_first_product();
		
	}
	
	@When("^customer searches by product: (.+) using search feature$")
	public void customer_searches_by_product_using_search_feature(String product) throws Throwable {

		POM_popup.escPopup(); 
		
		driver.get().esm.send_keys(POM_mainHeader.txtSearchBar,product);
		driver.get().esm.click(POM_mainHeader.btnSearch);
		
		Assert.assertTrue(driver.get().esm.check_text_exists("Filter by"));
		
		this.product = product;

	}
	
	@When("^customer clicks on first product$")
	public void customer_clicks_on_first_product() throws Throwable {
	    
		POM_popup.escPopup(); 
		driver.get().esm.click(POM_productPage.linkProduct);
		
		Assert.assertTrue(driver.get().esm.check_text_exists("Click to zoom"));
		
		
		
			
	}

	@When("^customer sorts by price low-to-high$")
	public void customer_sorts_by_price_low_to_high() throws Throwable {

		
		POM_popup.escPopup(); 
		driver.get().esm.select_list_value_by_text(POM_productPage.dropSort,"Price: Low - High");	
		
	}


	@When("^customer sorts by price high-to-low$")
	public void customer_sorts_by_price_high_to_low() throws Throwable {

		POM_popup.escPopup(); 
		driver.get().esm.select_list_value_by_text(POM_productPage.dropSort,"Price: High - Low");			
		
		
	}

	@When("^customer sorts by customer rating$")
	public void customer_sorts_by_customer_rating() throws Throwable {

		
		POM_popup.escPopup(); 
		driver.get().esm.select_list_value_by_text(POM_productPage.dropSort,"Customer Rating");			
		
		
	}
	
	@When("^customer changes store location to : (.+)$")
	public void customer_changes_store_location_to(String postcode) throws Throwable {
	  
		
		POM_popup.escPopup(); 
		
		driver.get().esm.send_keys(POM_productPage.txtPostCode,postcode);
		driver.get().esm.wait_element_invisible(POM_productPage.loadingWheel);
		driver.get().esm.click(POM_productPage.btnCheckPostcode);
		driver.get().esm.wait_element_invisible(POM_productPage.btnCheckPostcode);
		
		
	}
	
	
	@Then("^product page is shown$")
	public void product_page_is_shown() throws Throwable {
		
		Assert.assertTrue(driver.get().esm.check_text_exists("Click to zoom"));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_productPage.txtProductPrice));
		Assert.assertTrue(driver.get().esm.get_text(POM_productPage.txtProductPrice).contains("£"));
	}
	
	@Then("^results of matching products are shown by order of price low-to-high$")
	public void results_of_matching_products_are_shown_by_order_of_price_low_to_high() throws Throwable {
	
		Assert.assertEquals(driver.get().esm.get_list_item_text(POM_productPage.dropSort),"Price: Low - High");
	}
	
	
	@Then("^results of matching products are shown by order of price high-to-low$")
	public void results_of_matching_products_are_shown_by_order_of_price_high_to_low() throws Throwable {

		Assert.assertEquals(driver.get().esm.get_list_item_text(POM_productPage.dropSort),"Price: High - Low");	
		
	}
	
	@Then("^results of matching products are shown by order of customer rating$")
	public void results_of_matching_products_are_shown_by_order_of_customer_rating() throws Throwable {
		
		Assert.assertEquals(driver.get().esm.get_list_item_text(POM_productPage.dropSort),"Customer Rating");	
		
	}
	

	@Then("^customer can see stock availability$")
	public void customer_can_see_stock_availability() throws Throwable {

		Assert.assertTrue(
				driver.get().esm.check_text_exists("Order now, collect right away") || 
				driver.get().esm.check_text_exists("Not in stock") || 
				driver.get().esm.check_text_exists("Not available") || 
				driver.get().esm.check_text_exists("Delivery within") ||
				driver.get().esm.check_text_exists("How would you like to get your item")
				);
				
	}

	@Then("^customer can see important product data$")
	public void customer_can_see_important_product_data() throws Throwable {

		Assert.assertTrue(driver.get().esm.check_element_exists(POM_productPage.txtPrice));		
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_productPage.txtProductDescription));	
		Assert.assertTrue(driver.get().esm.verify_image(POM_productPage.imgProduct));	
		Assert.assertTrue(driver.get().esm.check_text_exists("Click to zoom"));


	}	
	
	@Then("^customer can see related products$")
	public void customer_can_see_related_products() throws Throwable {

		Assert.assertTrue(driver.get().esm.check_element_exists(POM_productPage.imgRelatedProduct));
		
	}

}

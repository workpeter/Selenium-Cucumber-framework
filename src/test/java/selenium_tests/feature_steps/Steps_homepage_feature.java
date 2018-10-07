package selenium_tests.feature_steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import selenium_tests.page_object_model.*;

import org.testng.Assert;
import static selenium_tests.Runner.driver;

public class Steps_homepage_feature {

	@Given("^customer is on homepage$")
	public void customer_is_on_homepage() throws Throwable {

		driver.get().esm.goto_home_url();
		driver.get().esm.delete_cookies();
		
		POM_popup.escPopup(); 
		
	}
	
	@When("customer clicks on help menu")
	public void customer_clicks_on_help_menu() throws Exception {

		driver.get().esm.click(POM_homepage.linkHelp);
		
	}

	@Then("help page is shown")
	public void help_page_is_shown() throws Exception {

		Assert.assertTrue(driver.get().esm.check_text_exists("HELP!"));

	}	
	
	
	@Then("all key homepage elements are shown")
	public void all_key_homepage_elements_are_shown() throws Exception {
	 
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.btnBasket));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.btnSearch));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.btnLogin));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.btnWishList));	
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkTechnology));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkHomeAndGarden));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkBabyAndNursery));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkToys));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkSportsAndLeisure));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkHealthAndBeauty));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkClothing));
		Assert.assertTrue(driver.get().esm.check_element_exists(POM_mainHeader.LinkJewelleryAndWatches));
		
	}
	
}

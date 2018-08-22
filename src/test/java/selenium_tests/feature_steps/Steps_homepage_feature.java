package selenium_tests.feature_steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import selenium_tests.page_object_model.*;

import org.testng.Assert;
import static selenium_tests.Runner.driver;

public class Steps_homepage_feature {

	@Given("^customer is on homepage$")
	public void customer_is_on_homepage() throws Throwable {

		driver.get().esm.goto_home_url();
		POM_popup.escPopup(); 
		
	}
	
	@Then("all key homepage elements are shown")
	public void all_key_homepage_elements_are_shown() throws Exception {
	 
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.btnBasket));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.btnSearch));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.btnLogin));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.btnWishList));	
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkTechnology));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkHomeAndGarden));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkBabyAndNursery));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkToys));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkSportsAndLeisure));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkHealthAndBeauty));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkClothing));
		Assert.assertTrue(driver.get().esm.elementExists(POM_mainHeader.LinkJewelleryAndWatches));
		
	}
	
}

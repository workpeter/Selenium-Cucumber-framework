package integrationTests.selenium.step_definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.testng.Assert;
import integrationTests.selenium.page_object_model.*;
import integrationTests.selenium.ESM;

public class Homepage {

	@Given("^customer is on homepage$")
	public void customer_is_on_homepage() throws Throwable {

		ESM.goto_home_url();
		POM_popup.escPopup(); 
		
	}
	
	@Then("all key homepage elements are shown")
	public void all_key_homepage_elements_are_shown() throws Exception {
	 
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.btnBasket));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.btnSearch));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.btnLogin));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.btnWishList));	
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkTechnology));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkHomeAndGarden));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkBabyAndNursery));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkToys));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkSportsAndLeisure));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkHealthAndBeauty));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkClothing));
		Assert.assertTrue(ESM.elementExists(POM_mainHeader.LinkJewelleryAndWatches));
		
	}
	
}

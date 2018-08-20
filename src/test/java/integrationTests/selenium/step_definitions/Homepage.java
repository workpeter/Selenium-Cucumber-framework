package integrationTests.selenium.step_definitions;

import static integrationTests.Runner.test_instance;

import org.testng.Assert;

import cucumber.api.java.en.*;
import integrationTests.Runner;
import integrationTests.selenium.Common_methods_and_pom;

public class Homepage extends Common_methods_and_pom {

	@Given("^customer is on homepage$")
	public void customer_is_on_homepage() throws Throwable {

		gotoPage(Runner.get_env_url());
		popup.escPopup(); 
		
	}
	
	@Then("all key homepage elements are shown")
	public void all_key_homepage_elements_are_shown() throws Exception {
	 
		Assert.assertTrue(elementExists(mainHeader.btnBasket));
		Assert.assertTrue(elementExists(mainHeader.btnSearch));
		Assert.assertTrue(elementExists(mainHeader.btnLogin));
		Assert.assertTrue(elementExists(mainHeader.btnWishList));	
		Assert.assertTrue(elementExists(mainHeader.LinkTechnology));
		Assert.assertTrue(elementExists(mainHeader.LinkHomeAndGarden));
		Assert.assertTrue(elementExists(mainHeader.LinkBabyAndNursery));
		Assert.assertTrue(elementExists(mainHeader.LinkToys));
		Assert.assertTrue(elementExists(mainHeader.LinkSportsAndLeisure));
		Assert.assertTrue(elementExists(mainHeader.LinkHealthAndBeauty));
		Assert.assertTrue(elementExists(mainHeader.LinkClothing));
		Assert.assertTrue(elementExists(mainHeader.LinkJewelleryAndWatches));
		
	}
	
}

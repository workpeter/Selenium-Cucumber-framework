package selenium_tests.feature_steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import selenium_tests.page_object_model.*;
import org.testng.Assert;
import static selenium_tests.Runner.driver;

public class Steps_categorySearch_feature  {

	String category;
	String nicheCategory;
	String unrecognisedCategory;
	

	@When("^customer clicks on category: (.+) in the menu$")
	public void customer_clicks_on_a_category_in_the_menu(String category) throws Throwable {

		POM_mainHeader.clickCategory(category);

		this.category = category;

	}

	@When("^customer hovers on category: (.+) in the menu$")
	public void customer_hovers_on_a_category_in_the_menu(String category) throws Throwable {
		
		POM_mainHeader.mouseToCategory(category);
		
		this.category = category;
		
	}

	@When("^clicks on sub-category: (.+)$")
	public void clicks_on_a_sub_category(String category) throws Throwable {

		POM_mainHeader.clickSubCategory(category);
		
		this.category = category;
		
	}

	@When("^clicks on niche-category via main header: (.+)$")
	public void clicks_on_a_niche_category_via_mh(String nicheCategory) throws Throwable {

		POM_mainHeader.clickNicheCategory_viaMainheader(nicheCategory);
		
	}

	
	@When("^clicks on niche-category via category splash screen: (.+)$")
	public void clicks_on_a_niche_category_css(String nicheCategory) throws Throwable {

		POM_categorySplashPage.clickNicheCategory_viaCategorySplashPage(nicheCategory);
		
	}
	
	@When("^customer searches and misspells (.+) with (.+) using search feature$")
	public void customer_searches_and_misspells_category_using_search_feature(String category, String misspelling) throws Throwable {
		
		POM_mainHeader.enter_term_in_search_bar_and_click(misspelling);

		this.category = category;
		
	}

	@When("^customer searches for unrecognised category: (.+) using search feature$")
	public void customer_searches_for_unrecognised_category_using_search_feature(String unrecognisedCategory) throws Throwable {
		
		POM_mainHeader.enter_term_in_search_bar_and_click(unrecognisedCategory);

	}

	
	@Then("^category splash screen is shown$")
	public void category_splash_screen_is_shown() throws Throwable {

		POM_popup.escPopup();
		
		
		Assert.assertEquals(
				driver.get().esm.get_text(POM_categorySplashPage.txtH1).toLowerCase(), 
				category.toLowerCase());
		
		
		Assert.assertEquals(
				driver.get().esm.get_text(POM_categorySplashPage.txtH1).toLowerCase(),
				category.toLowerCase());
		
	}
	
	@Then("^results of matching products are shown by order of relevance$")
	public void results_of_matching_products_are_shown_by_order_of_relevance() throws Throwable {

		POM_popup.escPopup();
		Assert.assertEquals(
				driver.get().esm.getDropDownMenuText(POM_productResults.dropProductSort),
				"Relevance");
		
	}
	
	@Then("^results of matching products are shown by order of popularity$")
	public void results_of_matching_products_are_shown_by_order_of_popularity() throws Throwable {

		POM_popup.escPopup();
		Assert.assertEquals(
				driver.get().esm.getDropDownMenuText(POM_productResults.dropProductSort),
				"Most Popular");

	}
	
	@Then("^no search results page is shown$")
	public void no_search_results_page_is_shown() throws Throwable {
		
		POM_popup.escPopup();
		Assert.assertTrue(
				driver.get().esm.text_exists("Please use different words or broaden your search") ||
				driver.get().esm.text_exists("but you might like"));
			
	}	

}

package integrationTests.selenium.step_definitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.testng.Assert;
import integrationTests.selenium.page_object_model.*;
import integrationTests.selenium.ESM;

public class CategorySearch_feature  {

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
		
		POM_mainHeader.searchBar(misspelling);

		this.category = category;
		
	}

	@When("^customer searches for unrecognised category: (.+) using search feature$")
	public void customer_searches_for_unrecognised_category_using_search_feature(String unrecognisedCategory) throws Throwable {
		
		POM_mainHeader.searchBar(unrecognisedCategory);

	}

	
	@Then("^category splash screen is shown$")
	public void category_splash_screen_is_shown() throws Throwable {

		POM_popup.escPopup();
		
		
		Assert.assertEquals(
				ESM.get_text(POM_categorySplashPage.txtH1).toLowerCase(), 
				category.toLowerCase());
		
		
		Assert.assertEquals(
				ESM.get_text(POM_categorySplashPage.txtH1).toLowerCase(),
				category.toLowerCase());
		
	}
	
	@Then("^results of matching products are shown by order of relevance$")
	public void results_of_matching_products_are_shown_by_order_of_relevance() throws Throwable {

		POM_popup.escPopup();
		Assert.assertEquals(
				ESM.getDropDownMenuText(POM_productResults.dropProductSort),
				"Relevance");
		
	}
	
	@Then("^results of matching products are shown by order of popularity$")
	public void results_of_matching_products_are_shown_by_order_of_popularity() throws Throwable {

		POM_popup.escPopup();
		Assert.assertEquals(
				ESM.getDropDownMenuText(POM_productResults.dropProductSort),
				"Most Popular");

	}
	
	@Then("^no search results page is shown$")
	public void no_search_results_page_is_shown() throws Throwable {
		
		POM_popup.escPopup();
		Assert.assertTrue(
				ESM.get_text(POM_productResults.txtNoResults).contains("couldn't find any products"));
			
	}	

}

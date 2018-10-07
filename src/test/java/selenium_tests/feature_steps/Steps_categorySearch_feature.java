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

		POM_popup.escPopup(); 
		
		switch(category){
		case "TECHNOLOGY": {driver.get().esm.click(POM_mainHeader.LinkTechnology); break;}
		case "HOME AND GARDEN": {driver.get().esm.click(POM_mainHeader.LinkHomeAndGarden); break;}
		case "BABY AND NURSERY": {driver.get().esm.click(POM_mainHeader.LinkBabyAndNursery); break;}
		case "TOYS": {driver.get().esm.click(POM_mainHeader.LinkToys); break;}
		case "SPORTS AND LEISURE": {driver.get().esm.click(POM_mainHeader.LinkSportsAndLeisure); break;}
		case "HEALTH AND BEAUTY": {driver.get().esm.click(POM_mainHeader.LinkHealthAndBeauty); break;}
		case "CLOTHING": {driver.get().esm.click(POM_mainHeader.LinkClothing); break;}
		case "JEWELLERY AND WATCHES": {driver.get().esm.click(POM_mainHeader.LinkJewelleryAndWatches); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}

		this.category = category;

	}

	@When("^customer hovers on category: (.+) in the menu$")
	public void customer_hovers_on_a_category_in_the_menu(String category) throws Throwable {
		
		POM_popup.escPopup(); 

		switch(category){
		case "TECHNOLOGY": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkTechnology); break;}
		case "HOME AND GARDEN": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkHomeAndGarden); break;}
		case "BABY AND NURSERY": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkBabyAndNursery); break;}
		case "TOYS": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkToys); break;}
		case "SPORTS AND LEISURE": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkSportsAndLeisure); break;}
		case "HEALTH AND BEAUTY": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkHealthAndBeauty); break;}
		case "CLOTHING": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkClothing); break;}
		case "JEWELLERY AND WATCHES": {driver.get().esm.move_to_element_and_wait(POM_mainHeader.LinkJewelleryAndWatches); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
	
		driver.get().esm.wait_element_visible(POM_mainHeader.popupMenu);
		
		this.category = category;
		
	}

	@When("^clicks on sub-category: (.+)$")
	public void clicks_on_a_sub_category(String category) throws Throwable {

		POM_popup.escPopup(); 
		
		switch(category){
		case "televisions and accessories": {driver.get().esm.click(POM_mainHeader.LinkTelevisionsAndAccessories); break;}
		case "Laptops and PCs": {driver.get().esm.click(POM_mainHeader.LinkLaptopsAndPCs); break;}
		case "living room furniture": {driver.get().esm.click(POM_mainHeader.LinkLivingRoomFurniture); break;}
		case "Bedding": {driver.get().esm.click(POM_mainHeader.LinkBedding); break;}
		case "Travel": {driver.get().esm.click(POM_mainHeader.LinkTravel); break;}
		case "safety and health": {driver.get().esm.click(POM_mainHeader.LinkSafetyAndHealth); break;}
		case "Womens": {driver.get().esm.click(POM_mainHeader.LinkWomenClothing); break;}
		case "Mens": {driver.get().esm.click(POM_mainHeader.LinkMenClothing); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}	
		
		this.category = category;
		
	}

	@When("^clicks on niche-category via main header: (.+)$")
	public void clicks_on_a_niche_category_via_mh(String nicheCategory) throws Throwable {

		POM_popup.escPopup(); 
		
		switch(nicheCategory){

		case "Dash Cams": {driver.get().esm.click(POM_mainHeader.LinkDashCams); break;}
		case "Sofas": {driver.get().esm.click(POM_mainHeader.LinkSofas); break;}
		case "Pushchairs": {driver.get().esm.click(POM_mainHeader.LinkPushchairs); break;}
		case "Batteries And Rechargeable Batteries": {driver.get().esm.click(POM_mainHeader.LinkBatteries); break;}
		case "Treadmills": {driver.get().esm.click(POM_mainHeader.LinkTreadmills); break;}
		case "Hair Dryers": {driver.get().esm.click(POM_mainHeader.LinkHairDryers); break;}
		case "Bras": {driver.get().esm.click(POM_mainHeader.LinkBras); break;}
		case "Ladies' Earrings": {driver.get().esm.click(POM_mainHeader.LinkLadiesEarrings); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
		
	}

	
	@When("^clicks on niche-category via category splash screen: (.+)$")
	public void clicks_on_a_niche_category_css(String nicheCategory) throws Throwable {
		
		POM_popup.escPopup(); 

		switch(nicheCategory){
		case "hdmi-cables-and-optical-cables": {driver.get().esm.click(POM_categorySplashPage.LinkHDMIandCables); break;}
		case "tv-remote-controls": {driver.get().esm.click(POM_categorySplashPage.LinkTVremoteControls); break;}
		case "Gaming laptops and PCs": {driver.get().esm.click(POM_categorySplashPage.LinkGaminglaptopsAndPCs); break;}
		case "imacs": {driver.get().esm.click(POM_categorySplashPage.LinkiMacs); break;}
		case "armchairs-and-chairs": {driver.get().esm.click(POM_categorySplashPage.LinkArmchairsAndChairs); break;}
		case "cd-and-dvd-storage": {driver.get().esm.click(POM_categorySplashPage.LinkCDandDVDstorage); break;}
		case "duvets": {driver.get().esm.click(POM_categorySplashPage.LinkDuvets); break;}
		case "pillows": {driver.get().esm.click(POM_categorySplashPage.LinkPillows); break;}
		case "pushchairs": {driver.get().esm.click(POM_categorySplashPage.LinkPushchairs); break;}
		case "baby-carriers": {driver.get().esm.click(POM_categorySplashPage.LinkBabycarriers); break;}
		case "baby health": {driver.get().esm.click(POM_categorySplashPage.LinkBabyHealth); break;}
		case "accessories": {driver.get().esm.click(POM_categorySplashPage.LinkAccessories); break;}
		case "dresses": {driver.get().esm.click(POM_categorySplashPage.LinkDresses); break;}
		case "coats-and-jackets": {driver.get().esm.click(POM_categorySplashPage.LinkCoatsAndJackets); break;}

		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}		
		
	}
	
	@When("^customer searches and misspells (.+) with (.+) using search feature$")
	public void customer_searches_and_misspells_category_using_search_feature(String category, String misspelling) throws Throwable {
		
		//POM_mainHeader.enter_term_in_search_bar_and_click(misspelling);
		
		POM_popup.escPopup(); 
		
		driver.get().esm.send_keys(POM_mainHeader.txtSearchBar,misspelling);
		driver.get().esm.click(POM_mainHeader.btnSearch);	
		
		

		this.category = category;
		
	}

	@When("^customer searches for unrecognised category: (.+) using search feature$")
	public void customer_searches_for_unrecognised_category_using_search_feature(String unrecognisedCategory) throws Throwable {
		
		POM_popup.escPopup(); 
		
		driver.get().esm.send_keys(POM_mainHeader.txtSearchBar,unrecognisedCategory);
		driver.get().esm.click(POM_mainHeader.btnSearch);

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
				driver.get().esm.get_list_item_text(POM_productResults.dropProductSort),
				"Relevance");
		
	}
	
	@Then("^results of matching products are shown by order of popularity$")
	public void results_of_matching_products_are_shown_by_order_of_popularity() throws Throwable {

		POM_popup.escPopup();
		
		Assert.assertEquals(
				driver.get().esm.get_list_item_text(POM_productResults.dropProductSort),
				"Most Popular");

	}
	
	@Then("^no search results page is shown$")
	public void no_search_results_page_is_shown() throws Throwable {
		
		POM_popup.escPopup();
		
		Assert.assertTrue(
				driver.get().esm.check_text_exists("Please use different words or broaden your search") ||
				driver.get().esm.check_text_exists("but you might like"));
			
	}	

}

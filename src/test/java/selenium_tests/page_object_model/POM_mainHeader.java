package selenium_tests.page_object_model;

import org.openqa.selenium.By;
import org.testng.Assert;
import static selenium_tests.Runner.driver;


public final class POM_mainHeader {

	public static By btnWishList = By.xpath("(//span[@class=\"argos-header__link-label\"])[1]");
	public static By btnBasket = By.xpath("(//span[@class=\"argos-header__link-label\"])[2]");
	public static By txtBasketCount = By.xpath("//span[@class=\"argos-header__trolley-count argos-header__trolley-badge badge\"]");
	public static By txtSearchBar = By.xpath("//*[@id=\"searchTerm\"]");
	public static By btnSearch = By.xpath("//button[@class=\"argos-header__search-button\"]");
	public static By btnLogin = By.xpath("//a[@class=\"font-condensed uppercase argos-header__link argos-header__link--signin\"]");
	public static By txtCategory = By.xpath("//li[@class=\"font-condensed-extra-bold uppercase meganav__nav-item meganav__nav-item--categories \"]");
	public static By btnLogo = By.xpath("//*[@id=\"argos-logo\"]/path[2]");

	//Category Links
	public static By LinkTechnology = By.xpath("//a[contains(.,'Technology')]");
	public static By LinkHomeAndGarden = By.xpath("//a[contains(.,'Home & Garden')]");
	public static By LinkBabyAndNursery = By.xpath("//a[contains(.,'Baby & Nursery')]");
	public static By LinkToys = By.xpath("//a[contains(.,'Toys')]");
	public static By LinkSportsAndLeisure =By.xpath("//a[contains(.,'Sports & Leisure')]");
	public static By LinkHealthAndBeauty = By.xpath("//a[contains(.,'Health & Beauty')]");
	public static By LinkClothing = By.xpath("//a[contains(.,'Clothing')]");
	public static By LinkJewelleryAndWatches = By.xpath("//a[contains(.,'Jewellery &')]");

	//Sub-Category links
	public static By LinkTelevisionsAndAccessories = By.xpath(buildLinkXpath("technology/televisions-and-accessories/c:29955/"));
	public static By LinkLaptopsAndPCs = By.xpath(buildLinkXpath("technology/laptops-and-pcs/c:29953/"));
	public static By LinkLivingRoomFurniture = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/c:29368/"));
	public static By LinkBedding = By.xpath(buildLinkXpath("home-and-garden/bedding/c:29358/"));
	public static By LinkTravel = By.xpath(buildLinkXpath("baby-and-nursery/travel/c:29002/"));
	public static By LinkSafetyAndHealth = By.xpath(buildLinkXpath("baby-and-nursery/safety-and-health/c:29003/")); 
	public static By LinkWomenClothing = By.xpath(buildLinkXpath("clothing/womens/c:691104/"));
	public static By LinkMenClothing = By.xpath(buildLinkXpath("clothing/mens/c:691144/"));
	
	//Niche-Category links
	public static By LinkDashCams = By.xpath(buildLinkXpath("technology/dash-cams/c:30278/"));
	public static By LinkSofas = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/sofas/c:29643/")); 
	public static By LinkPushchairs = By.xpath(buildLinkXpath("baby-and-nursery/travel/pushchairs/c:29042/"));
	public static By LinkBatteries = By.xpath(buildLinkXpath("technology/batteries-and-chargers/batteries-and-rechargeable-batteries/c:30218/"));
	public static By LinkTreadmills = By.xpath(buildLinkXpath("sports-and-leisure/fitness-equipment/treadmills/c:30612/")); 
	public static By LinkHairDryers = By.xpath(buildLinkXpath("health-and-beauty/hair-care/hair-dryers/c:29235/"));
	public static By LinkBras = By.xpath(buildLinkXpath("clothing/womens/lingerie/bras/c:691137/"));
	public static By LinkLadiesEarrings = By.xpath(buildLinkXpath("jewellery-and-watches/ladies'-jewellery/ladies'-earrings/c:29315/"));        
	public static By popupMenu = By.xpath("//span[@class=\"meganav__level-2-title\"]");	


	private static String buildLinkXpath(String indentifier){
		
		String path = "//a[@href=\"/browse/" + indentifier + "\"]";
		
		return path;
			
	}
	
//	public static void clickCategory(String category) throws Exception{
//		
//		POM_popup.escPopup(); 
//		
//		switch(category){
//		case "TECHNOLOGY": {driver.get().esm.click(LinkTechnology); break;}
//		case "HOME AND GARDEN": {driver.get().esm.click(LinkHomeAndGarden); break;}
//		case "BABY AND NURSERY": {driver.get().esm.click(LinkBabyAndNursery); break;}
//		case "TOYS": {driver.get().esm.click(LinkToys); break;}
//		case "SPORTS AND LEISURE": {driver.get().esm.click(LinkSportsAndLeisure); break;}
//		case "HEALTH AND BEAUTY": {driver.get().esm.click(LinkHealthAndBeauty); break;}
//		case "CLOTHING": {driver.get().esm.click(LinkClothing); break;}
//		case "JEWELLERY AND WATCHES": {driver.get().esm.click(LinkJewelleryAndWatches); break;}
//		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
//		}
//		
//	}
	
	
//	public static void mouseToCategory(String category) throws Exception{
//		
//		POM_popup.escPopup(); 
//
//		switch(category){
//		case "TECHNOLOGY": {driver.get().esm.move_to_element(LinkTechnology); break;}
//		case "HOME AND GARDEN": {driver.get().esm.move_to_element(LinkHomeAndGarden); break;}
//		case "BABY AND NURSERY": {driver.get().esm.move_to_element(LinkBabyAndNursery); break;}
//		case "TOYS": {driver.get().esm.move_to_element(LinkToys); break;}
//		case "SPORTS AND LEISURE": {driver.get().esm.move_to_element(LinkSportsAndLeisure); break;}
//		case "HEALTH AND BEAUTY": {driver.get().esm.move_to_element(LinkHealthAndBeauty); break;}
//		case "CLOTHING": {driver.get().esm.move_to_element(LinkClothing); break;}
//		case "JEWELLERY AND WATCHES": {driver.get().esm.move_to_element(LinkJewelleryAndWatches); break;}
//		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
//		}
//	
//		driver.get().esm.wait_until_visible(popupMenu);
//
//	}
	
//	public static void clickSubCategory(String category) throws Exception{
//		
//		POM_popup.escPopup(); 
//		
//		switch(category){
//		case "televisions and accessories": {driver.get().esm.click(LinkTelevisionsAndAccessories); break;}
//		case "Laptops and PCs": {driver.get().esm.click(LinkLaptopsAndPCs); break;}
//		case "living room furniture": {driver.get().esm.click(LinkLivingRoomFurniture); break;}
//		case "Bedding": {driver.get().esm.click(LinkBedding); break;}
//		case "Travel": {driver.get().esm.click(LinkTravel); break;}
//		case "safety and health": {driver.get().esm.click(LinkSafetyAndHealth); break;}
//		case "Womens": {driver.get().esm.click(LinkWomenClothing); break;}
//		case "Mens": {driver.get().esm.click(LinkMenClothing); break;}
//		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
//		}	
//
//	}
	
//	public static void clickNicheCategory_viaMainheader(String nicheCategory) throws Exception{
//		
//		POM_popup.escPopup(); 
//		
//		switch(nicheCategory){
//
//		case "Dash Cams": {driver.get().esm.click(LinkDashCams); break;}
//		case "Sofas": {driver.get().esm.click(LinkSofas); break;}
//		case "Pushchairs": {driver.get().esm.click(LinkPushchairs); break;}
//		case "Batteries And Rechargeable Batteries": {driver.get().esm.click(LinkBatteries); break;}
//		case "Treadmills": {driver.get().esm.click(LinkTreadmills); break;}
//		case "Hair Dryers": {driver.get().esm.click(LinkHairDryers); break;}
//		case "Bras": {driver.get().esm.click(LinkBras); break;}
//		case "Ladies' Earrings": {driver.get().esm.click(LinkLadiesEarrings); break;}
//		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
//		}
//	
//	}
	
//	public static void enter_term_in_search_bar_and_click(String category) throws Exception{
//		
//		POM_popup.escPopup(); 
//		
//		driver.get().esm.send_keys(txtSearchBar,category);
//		driver.get().esm.click(btnSearch);
//		
//	}	
	
//	public static void openBasket() throws Exception{
//		
//		POM_popup.escPopup(); 	
//		
//		driver.get().esm.scroll_top_page();
//		driver.get().esm.click(btnBasket);	
//		
//	}	
	
}

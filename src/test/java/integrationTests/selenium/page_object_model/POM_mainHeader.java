package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;
import org.testng.Assert;
import integrationTests.selenium.ESM;

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
	public static By LinkMaternity = By.xpath(buildLinkXpath("baby-and-nursery/maternity/c:29007/")); 
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
	
	public static void clickCategory(String category) throws Exception{
		
		POM_popup.escPopup(); 
		
		switch(category){
		case "TECHNOLOGY": {ESM.click(LinkTechnology); break;}
		case "HOME AND GARDEN": {ESM.click(LinkHomeAndGarden); break;}
		case "BABY AND NURSERY": {ESM.click(LinkBabyAndNursery); break;}
		case "TOYS": {ESM.click(LinkToys); break;}
		case "SPORTS AND LEISURE": {ESM.click(LinkSportsAndLeisure); break;}
		case "HEALTH AND BEAUTY": {ESM.click(LinkHealthAndBeauty); break;}
		case "CLOTHING": {ESM.click(LinkClothing); break;}
		case "JEWELLERY AND WATCHES": {ESM.click(LinkJewelleryAndWatches); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
		
	}
	
	
	public static void mouseToCategory(String category) throws Exception{
		
		POM_popup.escPopup(); 

		switch(category){
		case "TECHNOLOGY": {ESM.mouse_to(LinkTechnology); break;}
		case "HOME AND GARDEN": {ESM.mouse_to(LinkHomeAndGarden); break;}
		case "BABY AND NURSERY": {ESM.mouse_to(LinkBabyAndNursery); break;}
		case "TOYS": {ESM.mouse_to(LinkToys); break;}
		case "SPORTS AND LEISURE": {ESM.mouse_to(LinkSportsAndLeisure); break;}
		case "HEALTH AND BEAUTY": {ESM.mouse_to(LinkHealthAndBeauty); break;}
		case "CLOTHING": {ESM.mouse_to(LinkClothing); break;}
		case "JEWELLERY AND WATCHES": {ESM.mouse_to(LinkJewelleryAndWatches); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
	
		ESM.wait_until_visible(popupMenu);

	}
	
	public static void clickSubCategory(String category) throws Exception{
		
		POM_popup.escPopup(); 
		
		switch(category){
		case "televisions and accessories": {ESM.click(LinkTelevisionsAndAccessories); break;}
		case "Laptops and PCs": {ESM.click(LinkLaptopsAndPCs); break;}
		case "living room furniture": {ESM.click(LinkLivingRoomFurniture); break;}
		case "Bedding": {ESM.click(LinkBedding); break;}
		case "Travel": {ESM.click(LinkTravel); break;}
		case "Maternity": {ESM.click(LinkMaternity); break;}
		case "Womens": {ESM.click(LinkWomenClothing); break;}
		case "Mens": {ESM.click(LinkMenClothing); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}	

	}
	
	public static void clickNicheCategory_viaMainheader(String nicheCategory) throws Exception{
		
		POM_popup.escPopup(); 
		
		switch(nicheCategory){

		case "Dash Cams": {ESM.click(LinkDashCams); break;}
		case "Sofas": {ESM.click(LinkSofas); break;}
		case "Pushchairs": {ESM.click(LinkPushchairs); break;}
		case "Batteries And Rechargeable Batteries": {ESM.click(LinkBatteries); break;}
		case "Treadmills": {ESM.click(LinkTreadmills); break;}
		case "Hair Dryers": {ESM.click(LinkHairDryers); break;}
		case "Bras": {ESM.click(LinkBras); break;}
		case "Ladies' Earrings": {ESM.click(LinkLadiesEarrings); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
	
	}
	
	public static void enter_term_in_search_bar_and_click(String category) throws Exception{
		
		POM_popup.escPopup(); 
		
		ESM.send_keys(txtSearchBar,category);
		ESM.click(btnSearch);
		
	}	
	
	public static void openBasket() throws Exception{
		
		POM_popup.escPopup(); 	
		
		ESM.scrollTop();
		ESM.click(btnBasket);	
		
	}	
	
}

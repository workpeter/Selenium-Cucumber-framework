package selenium_tests.page_object_model;

import org.openqa.selenium.By;

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
	
	
}

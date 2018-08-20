package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;
import org.testng.Assert;

import integrationTests.selenium.Common_methods_and_pom;

public class POM_mainHeader extends Common_methods_and_pom {

	public By btnWishList = By.xpath("(//span[@class=\"argos-header__link-label\"])[1]");
	public By btnBasket = By.xpath("(//span[@class=\"argos-header__link-label\"])[2]");
	public By txtBasketCount = By.xpath("//span[@class=\"argos-header__trolley-count argos-header__trolley-badge badge\"]");
	public By txtSearchBar = By.xpath("//*[@id=\"searchTerm\"]");
	public By btnSearch = By.xpath("//button[@class=\"argos-header__search-button\"]");
	public By btnLogin = By.xpath("//a[@class=\"font-condensed uppercase argos-header__link argos-header__link--signin\"]");
	public By txtCategory = By.xpath("//li[@class=\"font-condensed-extra-bold uppercase meganav__nav-item meganav__nav-item--categories \"]");
	public By btnLogo = By.xpath("//*[@id=\"argos-logo\"]/path[2]");

	//Category Links
	public By LinkTechnology = By.xpath("//a[contains(.,'Technology')]");
	public By LinkHomeAndGarden = By.xpath("//a[contains(.,'Home & Garden')]");
	public By LinkBabyAndNursery = By.xpath("//a[contains(.,'Baby & Nursery')]");
	public By LinkToys = By.xpath("//a[contains(.,'Toys')]");
	public By LinkSportsAndLeisure =By.xpath("//a[contains(.,'Sports & Leisure')]");
	public By LinkHealthAndBeauty = By.xpath("//a[contains(.,'Health & Beauty')]");
	public By LinkClothing = By.xpath("//a[contains(.,'Clothing')]");
	public By LinkJewelleryAndWatches = By.xpath("//a[contains(.,'Jewellery &')]");

	//Sub-Category links
	public By LinkTelevisionsAndAccessories = By.xpath(buildLinkXpath("technology/televisions-and-accessories/c:29955/"));
	public By LinkLaptopsAndPCs = By.xpath(buildLinkXpath("technology/laptops-and-pcs/c:29953/"));
	public By LinkLivingRoomFurniture = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/c:29368/"));
	public By LinkBedding = By.xpath(buildLinkXpath("home-and-garden/bedding/c:29358/"));
	public By LinkTravel = By.xpath(buildLinkXpath("baby-and-nursery/travel/c:29002/"));
	public By LinkMaternity = By.xpath(buildLinkXpath("baby-and-nursery/maternity/c:29007/")); 
	public By LinkWomenClothing = By.xpath(buildLinkXpath("clothing/womens/c:691104/"));
	public By LinkMenClothing = By.xpath(buildLinkXpath("clothing/mens/c:691144/"));

	//Niche-Category links
	public By LinkDashCams = By.xpath(buildLinkXpath("technology/dash-cams/c:30278/"));
	public By LinkSofas = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/sofas/c:29643/")); 
	public By LinkPushchairs = By.xpath(buildLinkXpath("baby-and-nursery/travel/pushchairs/c:29042/"));
	public By LinkBatteries = By.xpath(buildLinkXpath("technology/batteries-and-chargers/batteries-and-rechargeable-batteries/c:30218/"));
	public By LinkTreadmills = By.xpath(buildLinkXpath("sports-and-leisure/fitness-equipment/treadmills/c:30612/")); 
	public By LinkHairDryers = By.xpath(buildLinkXpath("health-and-beauty/hair-care/hair-dryers/c:29235/"));
	public By LinkBras = By.xpath(buildLinkXpath("clothing/womens/lingerie/bras/c:691137/"));
	public By LinkLadiesEarrings = By.xpath(buildLinkXpath("jewellery-and-watches/ladies'-jewellery/ladies'-earrings/c:29315/"));
	            
	public By popupMenu = By.xpath("//span[@class=\"meganav__level-2-title\"]");	
	//<span class="meganav__level-2-title">Womens Clothing</span>
	
	private String buildLinkXpath(String indentifier){
		
		String path = "//a[@href=\"/browse/" + indentifier + "\"]";
		
		return path;
			
	}
	
	public void clickCategory(String category) throws Exception{
		
		popup.escPopup(); 
		
		switch(category){
		case "TECHNOLOGY": {click(LinkTechnology); break;}
		case "HOME AND GARDEN": {click(LinkHomeAndGarden); break;}
		case "BABY AND NURSERY": {click(LinkBabyAndNursery); break;}
		case "TOYS": {click(LinkToys); break;}
		case "SPORTS AND LEISURE": {click(LinkSportsAndLeisure); break;}
		case "HEALTH AND BEAUTY": {click(LinkHealthAndBeauty); break;}
		case "CLOTHING": {click(LinkClothing); break;}
		case "JEWELLERY AND WATCHES": {click(LinkJewelleryAndWatches); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
		
	}
	
	
	public void mouseToCategory(String category) throws Exception{
		
		popup.escPopup(); 

		switch(category){
		case "TECHNOLOGY": {mouseTo(LinkTechnology); break;}
		case "HOME AND GARDEN": {mouseTo(LinkHomeAndGarden); break;}
		case "BABY AND NURSERY": {mouseTo(LinkBabyAndNursery); break;}
		case "TOYS": {mouseTo(LinkToys); break;}
		case "SPORTS AND LEISURE": {mouseTo(LinkSportsAndLeisure); break;}
		case "HEALTH AND BEAUTY": {mouseTo(LinkHealthAndBeauty); break;}
		case "CLOTHING": {mouseTo(LinkClothing); break;}
		case "JEWELLERY AND WATCHES": {mouseTo(LinkJewelleryAndWatches); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
	
		wait_until_visible(popupMenu);

	}
	
	public void clickSubCategory(String category) throws Exception{
		
		popup.escPopup(); 
		
		switch(category){
		case "televisions and accessories": {click(LinkTelevisionsAndAccessories); break;}
		case "Laptops and PCs": {click(LinkLaptopsAndPCs); break;}
		case "living room furniture": {click(LinkLivingRoomFurniture); break;}
		case "Bedding": {click(LinkBedding); break;}
		case "Travel": {click(LinkTravel); break;}
		case "Maternity": {click(LinkMaternity); break;}
		case "Womens": {click(LinkWomenClothing); break;}
		case "Mens": {click(LinkMenClothing); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}	

	}
	
	public void clickNicheCategory_viaMainheader(String nicheCategory) throws Exception{
		
		popup.escPopup(); 
		
		switch(nicheCategory){

		case "Dash Cams": {click(LinkDashCams); break;}
		case "Sofas": {click(LinkSofas); break;}
		case "Pushchairs": {click(LinkPushchairs); break;}
		case "Batteries And Rechargeable Batteries": {click(LinkBatteries); break;}
		case "Treadmills": {click(LinkTreadmills); break;}
		case "Hair Dryers": {click(LinkHairDryers); break;}
		case "Bras": {click(LinkBras); break;}
		case "Ladies' Earrings": {click(LinkLadiesEarrings); break;}
		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}
	
	}
	
	public void searchBar(String category) throws Exception{
		
		popup.escPopup(); 
		
		sendkeys(txtSearchBar,category);
		click(btnSearch);
		
	}	
	
	public void openBasket() throws Exception{
		
		popup.escPopup(); 	
		
		scrollTop();
		click(btnBasket);	
		
	}	
	
}

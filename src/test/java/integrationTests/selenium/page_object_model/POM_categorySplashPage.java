package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;
import org.testng.Assert;

import integrationTests.selenium.Common_methods_and_pom;

public class POM_categorySplashPage extends Common_methods_and_pom {

public By txtH1 = By.xpath("//h1");


//Niche-Category links
public By LinkHDMIandCables =  By.xpath(buildLinkXpath("technology/televisions-and-accessories/hdmi-cables-and-optical-cables/c:30103/"));
public By LinkTVremoteControls =  By.xpath(buildLinkXpath("technology/televisions-and-accessories/tv-remote-controls/c:30104/"));
public By LinkGaminglaptopsAndPCs = By.xpath(buildLinkXpath("technology/laptops-and-pcs/gaming-laptops-and-pcs/c:30279/"));
public By LinkiMacs = By.xpath(buildLinkXpath("technology/laptops-and-pcs/imacs/c:30274/"));
public By LinkArmchairsAndChairs = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/armchairs-and-chairs/c:29641/"));
public By LinkCDandDVDstorage = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/cd-and-dvd-storage/c:29895/")); 	
public By LinkDuvets = By.xpath(buildLinkXpath("home-and-garden/bedding/duvets/c:29473/")); 
public By LinkPillows = By.xpath(buildLinkXpath("home-and-garden/bedding/pillows/c:29479/")); 	
public By LinkPushchairs = By.xpath(buildLinkXpath("baby-and-nursery/travel/pushchairs/c:29042/")); 
public By LinkBabycarriers = By.xpath(buildLinkXpath("baby-and-nursery/travel/baby-carriers/c:29041/")); 	
public By LinkMaternityAccessories =By.xpath(buildLinkXpath("baby-and-nursery/maternity/maternity-accessories/c:29114/")); 
public By LinkAccessories = By.xpath(buildLinkXpath("clothing/womens/accessories/c:691106/")); 
public By LinkDresses = By.xpath(buildLinkXpath("clothing/womens/dresses/c:691111/")); 
public By LinkCoatsAndJackets = By.xpath(buildLinkXpath("clothing/mens/coats-and-jackets/c:691147/")); 	

private String buildLinkXpath(String indentifier){
	
	String path = "//a[@href=\"/browse/" + indentifier + "\"]";
	
	return path;
		
}


public void clickNicheCategory_viaCategorySplashPage(String nicheCategory) throws Exception{
	
	popup.escPopup(); 
	
	switch(nicheCategory){
	case "hdmi-cables-and-optical-cables": {click(LinkHDMIandCables); break;}
	case "tv-remote-controls": {click(LinkTVremoteControls); break;}
	case "Gaming laptops and PCs": {click(LinkGaminglaptopsAndPCs); break;}
	case "imacs": {click(LinkiMacs); break;}
	case "armchairs-and-chairs": {click(LinkArmchairsAndChairs); break;}
	case "cd-and-dvd-storage": {click(LinkCDandDVDstorage); break;}
	case "duvets": {click(LinkDuvets); break;}
	case "pillows": {click(LinkPillows); break;}
	case "pushchairs": {click(LinkPushchairs); break;}
	case "baby-carriers": {click(LinkBabycarriers); break;}
	case "maternity-accessories": {click(LinkMaternityAccessories); break;}
	case "accessories": {click(LinkAccessories); break;}
	case "dresses": {click(LinkDresses); break;}
	case "coats-and-jackets": {click(LinkCoatsAndJackets); break;}

	default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
	}

}


}

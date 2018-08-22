package selenium_tests.page_object_model;

import org.openqa.selenium.By;
import org.testng.Assert;
import static selenium_tests.Runner.driver;


public final class POM_categorySplashPage  {

	public static By txtH1 = By.xpath("//h1");

	//Niche-Category links
	public static By LinkHDMIandCables =  By.xpath(buildLinkXpath("technology/televisions-and-accessories/hdmi-cables-and-optical-cables/c:30103/"));
	public static By LinkTVremoteControls =  By.xpath(buildLinkXpath("technology/televisions-and-accessories/tv-remote-controls/c:30104/"));
	public static By LinkGaminglaptopsAndPCs = By.xpath(buildLinkXpath("technology/laptops-and-pcs/gaming-laptops-and-pcs/c:30279/"));
	public static By LinkiMacs = By.xpath(buildLinkXpath("technology/laptops-and-pcs/imacs/c:30274/"));
	public static By LinkArmchairsAndChairs = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/armchairs-and-chairs/c:29641/"));
	public static By LinkCDandDVDstorage = By.xpath(buildLinkXpath("home-and-garden/living-room-furniture/cd-and-dvd-storage/c:29895/")); 	
	public static By LinkDuvets = By.xpath(buildLinkXpath("home-and-garden/bedding/duvets/c:29473/")); 
	public static By LinkPillows = By.xpath(buildLinkXpath("home-and-garden/bedding/pillows/c:29479/")); 	
	public static By LinkPushchairs = By.xpath(buildLinkXpath("baby-and-nursery/travel/pushchairs/c:29042/")); 
	public static By LinkBabycarriers = By.xpath(buildLinkXpath("baby-and-nursery/travel/baby-carriers/c:29041/")); 	
	public static By LinkBabyHealth =By.xpath(buildLinkXpath("baby-and-nursery/safety-and-health/baby-health/c:29057/")); 
	public static By LinkAccessories = By.xpath(buildLinkXpath("clothing/womens/accessories/c:691106/")); 
	public static By LinkDresses = By.xpath(buildLinkXpath("clothing/womens/dresses/c:691111/")); 
	public static By LinkCoatsAndJackets = By.xpath(buildLinkXpath("clothing/mens/coats-and-jackets/c:691147/")); 	

	private static String buildLinkXpath(String indentifier){

		String path = "//a[@href=\"/browse/" + indentifier + "\"]";

		return path;

	}

	public static void clickNicheCategory_viaCategorySplashPage(String nicheCategory) throws Exception{

		POM_popup.escPopup(); 

		switch(nicheCategory){
		case "hdmi-cables-and-optical-cables": {driver.get().esm.click(LinkHDMIandCables); break;}
		case "tv-remote-controls": {driver.get().esm.click(LinkTVremoteControls); break;}
		case "Gaming laptops and PCs": {driver.get().esm.click(LinkGaminglaptopsAndPCs); break;}
		case "imacs": {driver.get().esm.click(LinkiMacs); break;}
		case "armchairs-and-chairs": {driver.get().esm.click(LinkArmchairsAndChairs); break;}
		case "cd-and-dvd-storage": {driver.get().esm.click(LinkCDandDVDstorage); break;}
		case "duvets": {driver.get().esm.click(LinkDuvets); break;}
		case "pillows": {driver.get().esm.click(LinkPillows); break;}
		case "pushchairs": {driver.get().esm.click(LinkPushchairs); break;}
		case "baby-carriers": {driver.get().esm.click(LinkBabycarriers); break;}
		case "baby health": {driver.get().esm.click(LinkBabyHealth); break;}
		case "accessories": {driver.get().esm.click(LinkAccessories); break;}
		case "dresses": {driver.get().esm.click(LinkDresses); break;}
		case "coats-and-jackets": {driver.get().esm.click(LinkCoatsAndJackets); break;}

		default: Assert.fail("Category not defined in Selenium page object model (POM) - contact tester");
		}

	}

}

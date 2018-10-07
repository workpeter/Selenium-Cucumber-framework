package selenium_tests.page_object_model;

import org.openqa.selenium.By;

public final class POM_productPage  {

	public static By linkProduct = By.xpath("(//a[@class=\"ac-product-link ac-product-card__image\"])[1]");
	public static By linkChangeStore = By.xpath("//class[@class=\"a xs-5--none change-store\"]");	
	public static By linkChangePostcode =By.xpath("//*[@id=\"branch\"]/div[5]/div[2]/div/span");	
	public static By linkProductOwner = By.xpath("//a[@itemprop=\"brand\"]");	
	public static By txtProductPrice = By.xpath("//li[@class=\"price product-price-primary\"]");
	public static By txtPostCode = By.xpath("//input[@class=\"form-control form-group__input fulfilment-search\"]");
	public static By txtAvail = By.xpath("//span[@class=\"availability-message message-tick  has-fast-track has-fast-track-clickable\"]");		
	public static By txtPrice = By.xpath("//li[@itemprop=\"price\"]");		
	public static By txtProductDescription = By.xpath("//div[@class=\"product-description-content-text\"]");	
	public static By btnCheckPostcode = By.xpath("//button[@class=\"button button--secondary button--quarter\"]");	
	public static By btnAddBasketSmall = By.xpath("//button[@class=\" add-to-trolley-button button button--secondary button--quarter button--tiny\"]");	
	public static By btnAddToBasket = By.xpath("//button[@class=\" add-to-trolley-button button button--primary button--full\"]");	
	public static By imgProduct = By.xpath("//img[contains(@style, 'transform:translate(0px, 0px)')]");	
	public static By imgRelatedProduct = By.xpath("//img[@class=\"product-card__image\"]");				
	public static By dropSort = By.xpath("//select[@class=\"font-standard form-control sort-select\"]");
	public static By loadingWheel = By.xpath("//div[@class=\"ac-loading-wheel ac-loading-wheel--contained\"]");	
		

}

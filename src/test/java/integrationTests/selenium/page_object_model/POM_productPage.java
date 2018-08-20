package integrationTests.selenium.page_object_model;

import org.openqa.selenium.By;
import cucumber.api.PendingException;
import integrationTests.selenium.Common_methods_and_pom;

public class POM_productPage extends Common_methods_and_pom {

	public By linkProduct = By.xpath("//a[@class=\"ac-product-link ac-product-card__details\"]");
	public By linkChangeStore = By.xpath("//class[@class=\"a xs-5--none change-store\"]");	
	
	public By linkChangePostcode =By.xpath("//*[@id=\"branch\"]/div[5]/div[2]/div/span");	
	public By linkProductOwner = By.xpath("//a[@itemprop=\"brand\"]");	
	
	public By txtProductPrice = By.xpath("//li[@class=\"price product-price-primary\"]");
	public By txtPostCode = By.xpath("//input[@class=\"form-control form-group__input fulfilment-search\"]");
	public By txtAvail = By.xpath("//span[@class=\"availability-message message-tick  has-fast-track has-fast-track-clickable\"]");		
	public By txtPrice = By.xpath("//li[@itemprop=\"price\"]");		
	public By txtProductDescription = By.xpath("//div[@class=\"product-description-content-text\"]");	
	public By btnCheckPostcode = By.xpath("//button[@class=\"button button--secondary button--quarter\"]");	
	public By btnAddBasketSmall = By.xpath("//button[@class=\" add-to-trolley-button button button--secondary button--quarter button--tiny\"]");	
	public By btnAddToBasket = By.xpath("//button[@class=\" add-to-trolley-button button button--primary button--full\"]");	
	public By imgProduct = By.xpath("//img[contains(@style, 'transform:translate(0px, 0px)')]");	
	public By imgRelatedProduct = By.xpath("//img[@class=\"product-card__image\"]");				
	public By dropSort = By.xpath("//select[@class=\"font-standard form-control sort-select\"]");
	public By loadingWheel = By.xpath("//div[@class=\"ac-loading-wheel ac-loading-wheel--contained\"]");	
		
	
	public void click_on_first_product() throws Exception{
		
		popup.escPopup(); 
		click(linkProduct);
	}

	
	
	public void sort_products(String sortBy) throws Exception{
		
		popup.escPopup(); 
		selectByVisibleText(dropSort,sortBy);
		
	}
	
	
	
	public void adds_product_to_basket_via_productPage() throws Throwable {

		popup.escPopup();
		
		scrollBottom();

		if (!textExists("Not available online")){
			click(btnAddToBasket);
			popupBasket.checkContinueShopping();
		}else{
			System.out.println("[Skipping next scenerio step/s] This item is not available online to add to basket");
			throw new PendingException();
		}

	}
	
	
	
	public void change_store_location(String postcode) throws Throwable {

		popup.escPopup();
		
		sendkeys(txtPostCode,postcode);
		wait_until_invisible(loadingWheel);
		click(btnCheckPostcode);
		wait_until_invisible(btnCheckPostcode);
		
	}
	

}

package selenium_tests.page_object_model;

import org.openqa.selenium.By;


public final class POM_productResults  {

	public static By txtSearchResultCount = By.xpath("//div[@class=\"font-standard md-hidden search-results-count\"]");
	public static By dropProductSort = By.xpath("//select[@class=\"font-standard form-control sort-select\"]");
	public static By txtNoResults = By.xpath("//h1[@class=\"no-results__heading\"]");	
	public static By btnAddToBasketFirstProduct = By.xpath("(//button[@class=\" add-to-trolley-button button button--primary button--full add-to-trolley-button--beta\"])[1]");	
	public static By btnAddToBasketSecondProduct = By.xpath("(//button[@class=\" add-to-trolley-button button button--primary button--full add-to-trolley-button--beta\"])[2]");
	public static By btnChooseOptionsFirstProduct = By.xpath("(//a[@class=\"ac-product-cta__button ac-product-cta__button--secondary\"])[1]");	
	public static By btnChooseOptionsSecondProduct = By.xpath("(//a[@class=\"ac-product-cta__button ac-product-cta__button--secondary\"])[2]");	


}

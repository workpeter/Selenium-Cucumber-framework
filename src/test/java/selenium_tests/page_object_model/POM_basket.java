package selenium_tests.page_object_model;

import org.openqa.selenium.*;

public final class POM_basket {
	

	public static By dropQuantity = By.xpath("//select[starts-with(@id,'quantity')]");
	public static By txtPrice = By.xpath("//span[@class=\"price\"]");
	public static By btnRemove = By.xpath("//input[@value=\"Remove\"]");	
	public static By btnRemoveAlternative = By.xpath("//button[starts-with(@class,'ProductCard')]");


	
}

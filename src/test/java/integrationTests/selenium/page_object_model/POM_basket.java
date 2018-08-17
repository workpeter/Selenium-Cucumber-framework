package integrationTests.selenium.page_object_model;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import integrationTests.selenium.Common_methods_and_pom;

import java.util.*;

public class POM_basket extends Common_methods_and_pom {

	public By txtH1 = By.xpath("//*[@id=\"emptytrolleylister\"]/div[1]/h1");
	public By dropQuantity = By.xpath("//select[starts-with(@id,'quantity')]");
	public By txtPrice = By.xpath("//span[@class=\"price\"]");
	public By btnRemove = By.xpath("//input[@value=\"Remove\"]");	
	public By btnRemoveAlternative = By.xpath("//button[starts-with(@class,'ProductCard')]");

	public int productCount() throws Exception{
	
		int productCount = elementCount(dropQuantity);
		//System.out.println("number of different products: " + productCount);
		return  productCount;
		
	}

	public int quantityCount() throws Exception{

		int quantityCount = 0;
		
		List<WebElement> rows = getAllElements(dropQuantity);

		Iterator<WebElement> iter = rows.iterator();
		while (iter.hasNext()) {

			WebElement element = iter.next();
			
			Select select = new Select(element);
			quantityCount = quantityCount + Integer.valueOf(select.getFirstSelectedOption().getText().replaceAll("\\s+",""));
			
			//System.out.println("quantity of product: " + quantityCount);
		}
		
		return quantityCount;

	}
	
	
	public void remove_first_product_from_basket() throws Throwable {

		popup.escPopup();
		
		if (elementExists(btnRemove)){click(btnRemove);}
		else{click(btnRemoveAlternative);}

	}
	
	
	public void change_quantity_first_product(int quantity) throws Throwable {

		popup.escPopup();
		selectByVisibleText(dropQuantity,String.valueOf(quantity));
		
	}
	
	
	
	public void wait_for_basket_to_load() throws Exception{
	
		waitForElement(dropQuantity);
		
	}
	
}

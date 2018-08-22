package selenium_tests.page_object_model;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.*;
import static selenium_tests.Runner.driver;

public final class POM_basket {

	public static By txtH1 = By.xpath("//*[@id=\"emptytrolleylister\"]/div[1]/h1");
	public static By dropQuantity = By.xpath("//select[starts-with(@id,'quantity')]");
	public static By txtPrice = By.xpath("//span[@class=\"price\"]");
	public static By btnRemove = By.xpath("//input[@value=\"Remove\"]");	
	public static By btnRemoveAlternative = By.xpath("//button[starts-with(@class,'ProductCard')]");

	public static int productCount() throws Exception{
	
		int productCount = driver.get().esm.count_matching_elements(dropQuantity);
		//System.out.println("number of different products: " + productCount);
		return  productCount;
		
	}

	public static int quantityCount() throws Exception{

		int quantityCount = 0;
		
		List<WebElement> rows = driver.get().esm.get_all_matching_elements(dropQuantity);

		Iterator<WebElement> iter = rows.iterator();
		while (iter.hasNext()) {

			WebElement element = iter.next();
			
			Select select = new Select(element);
			quantityCount = quantityCount + Integer.valueOf(select.getFirstSelectedOption().getText().replaceAll("\\s+",""));
			
			//System.out.println("quantity of product: " + quantityCount);
		}
		
		return quantityCount;

	}
	
	public static void remove_first_product_from_basket() throws Throwable {

		POM_popup.escPopup();
		
		if (driver.get().esm.check_element_exists(btnRemove)){
			driver.get().esm.click(btnRemove);
		}else{
			driver.get().esm.click(btnRemoveAlternative);
		}

	}
	
	public static void change_quantity_first_product(int quantity) throws Throwable {

		POM_popup.escPopup();
		driver.get().esm.select_list_value_by_text(dropQuantity,String.valueOf(quantity));
		
	}
	
	public static void wait_for_basket_to_load() throws Exception{
	
		driver.get().esm.wait_until_visible(dropQuantity);
		
	}
	
}

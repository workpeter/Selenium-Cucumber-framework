package selenium_tests.feature_steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import selenium_tests.page_object_model.*;

import static selenium_tests.Runner.driver;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class Steps_basket_feature  {

	@When("^customer views basket$")
	public void customer_views_basket() throws Throwable {

		POM_popup.escPopup(); 
		driver.get().esm.scroll_top_page();
		driver.get().esm.click(POM_mainHeader.btnBasket);	
		
		Assert.assertTrue(
				driver.get().esm.check_text_exists("your trolley") ||
				driver.get().esm.check_text_exists("my trolley"));

	}

	@When("^adds product to basket$")
	public void adds_product_to_basket() throws Throwable {

		POM_popup.escPopup(); 

		driver.get().esm.scroll_bottom_page();

		if (!driver.get().esm.check_text_exists("Not available online")){
			
			driver.get().esm.click(POM_productPage.btnAddToBasket);
			driver.get().esm.wait_element_invisible(POM_popupBasket.loadingWheel);
			driver.get().esm.click(POM_popupBasket.btnContinueShopping);
			
			
		}else{
			System.out.println("[Skipping next scenerio step/s] This item is not available online to add to basket");
			throw new PendingException();
		}

	}

	@When("^adds product: (.+) to basket twice$")
	public void adds_product_to_basket_twice(String product) throws Throwable {

		adds_product_to_basket();
		adds_product_to_basket();

	}

	@When("^adds first product to basket x(\\d+) quantity$")
	public void adds_first_product_to_basket_x_quantity(int quantity) throws Throwable {


		for (int i=0;i< quantity;i++){

			POM_popup.escPopup(); 

			if(!driver.get().esm.check_element_exists(POM_productResults.btnChooseOptionsFirstProduct)){

				driver.get().esm.click(POM_productResults.btnAddToBasketFirstProduct);
				driver.get().esm.wait_element_invisible(POM_popupBasket.loadingWheel);
				driver.get().esm.click(POM_popupBasket.btnContinueShopping);

			}else{
				driver.get().esm.click(POM_productResults.btnChooseOptionsFirstProduct);

				adds_product_to_basket();
				driver.get().navigate().back();
			}

		}	



	}

	@When("^adds second product to basket x(\\d+) quantity$")
	public void adds_second_product_to_basket_x_quantity(int quantity) throws Throwable {

		for (int i=0;i< quantity;i++){

			POM_popup.escPopup(); //if required

			if(!driver.get().esm.check_element_exists(POM_productResults.btnChooseOptionsSecondProduct)){

				driver.get().esm.click(POM_productResults.btnAddToBasketSecondProduct);
				driver.get().esm.wait_element_invisible(POM_popupBasket.loadingWheel);
				driver.get().esm.click(POM_popupBasket.btnContinueShopping);

			}else{
				driver.get().esm.click(POM_productResults.btnChooseOptionsSecondProduct);
				adds_product_to_basket();
				driver.get().navigate().back();

			}

		}

	}

	@When("customer removes firt product from basket")
	public void customer_removes_firt_product_from_basket() throws Throwable {

		POM_popup.escPopup();

		if (driver.get().esm.check_element_exists(POM_basket.btnRemove)){
			driver.get().esm.click(POM_basket.btnRemove);
		}else{
			driver.get().esm.click(POM_basket.btnRemoveAlternative);
		}

	}


	@When("customer changes quantity of first product to x(\\d+)")
	public void customer_changes_quantity_of_first_product_to_x(int quantity) throws Throwable {

		POM_popup.escPopup();
		driver.get().esm.select_list_value_by_text(POM_basket.dropQuantity,String.valueOf(quantity));

	}


	@Then("^empty basket is shown$")
	public void empty_basket_is_shown() throws Throwable {

		POM_popup.escPopup(); 

		Assert.assertTrue(
				driver.get().esm.check_text_exists("Your trolley is currently empty") ||
				driver.get().esm.check_text_exists("your shopping trolley is empty"));


	}

	@Then("^basket with (.+) products and (.+) quantity is shown$")
	public void basket_with_products_and_quantity_is_shown(String productCount, String quantityCount) throws Throwable {

		POM_popup.escPopup();

		driver.get().esm.wait_element_exists(POM_basket.dropQuantity);

		//convert expected cucumber values from strings to integers
		int iproductCountExpected = Integer.valueOf(productCount);
		int iquantityCountExpected = Integer.valueOf(quantityCount);	


		int productCountActual = 0;  //driver.get().esm.count_matching_elements(POM_basket.dropQuantity);
		int quantityCountActual = 0; 

		List<WebElement> rows = driver.get().esm.get_all_matching_elements(POM_basket.dropQuantity);

		Iterator<WebElement> iter = rows.iterator();
		while (iter.hasNext()) {

			WebElement element = iter.next();


			if (!element.isDisplayed()) continue;

			Select select = new Select(element);

			String dropDownValue = select.getFirstSelectedOption().getText().replaceAll("\\s+","");


			productCountActual++;
			quantityCountActual = quantityCountActual + Integer.valueOf(dropDownValue);



		}	

		Assert.assertEquals(productCountActual, iproductCountExpected);
		Assert.assertEquals(quantityCountActual, iquantityCountExpected);	

	}

}

Feature: Homepage
Custumer is able to view search feature, categories, basket, login, wishlist and logo
		 

  @Homepage 
  Scenario: Customer able to see all key features on homepage
	Given customer is on homepage
	Then all key homepage elements are shown

	
  @Homepage 
  Scenario: Customer opens up help menu	
	Given customer is on homepage
	When customer clicks on help menu
	Then help page is shown	
	

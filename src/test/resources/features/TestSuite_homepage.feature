Feature: Homepage
Custumer is able to view search feature, categories, basket, login, wishlist and logo
		 

  @Homepage @Grid
  Scenario: Customer able to see all key features on homepage
	Given customer is on homepage
	Then all key homepage elements are shown

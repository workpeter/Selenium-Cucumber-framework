Feature: Products
		 Customer has ability to search for product, sort results and look at specific product data and delivery information.



  @Product 
  Scenario Outline: customer searches for product by name 
  	Given customer is on homepage
    When customer searches by product: <Product> using search feature
	Then results of matching products are shown by order of popularity
	When customer clicks on first product
	Then customer can see important product data
	And customer can see related products
   Examples:
    | Product 					|
    | JEANS 					|   
    | RING					 	|     
    | XBOX		 				|
    | COOKERS 					|
    | PUSHCHAIR 				|
    | SPIDERMAN					|
    | FOOTBALL 					|
    | WEIGHTS					|
  
  @Product
  Scenario Outline: customer searches for product and changes list order
  	Given customer is on homepage
    When customer searches by product: <Product> using search feature
	And customer sorts by price low-to-high
	Then results of matching products are shown by order of price low-to-high
	When customer sorts by price high-to-low
	Then results of matching products are shown by order of price high-to-low
	When customer sorts by customer rating
	Then results of matching products are shown by order of customer rating
   Examples:
    | Product 					|
    | XBOX		 				|
    | COOKERS 					|
    | PUSHCHAIR 				|
    | SPIDERMAN					|
    | FOOTBALL 					|
    | WEIGHTS					|
    | JEANS 					|        
    | RING					 	| 	
    
     
  @Product  
  Scenario Outline: customer changes store location
	Given customer is on product page : <Product>
	When customer changes store location to : <Postcode>
	Then customer can see stock availability
  Examples:
    | Product 	| Postcode 	|
    | COOKERS	| BL0 9JH	|
    | XBOX		| SE6 1SB	|
    | PUSHCHAIR	| SS1 2TW	|
    | SPIDERMAN	| FK7 7TZ	|
  
 


 

    
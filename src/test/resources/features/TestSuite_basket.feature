Feature: Basket 
		 Customer has ability to View basket and add/update/delete products.

  @Basket
  Scenario: Customer views empty basket
	Given customer is on homepage
	When customer views basket
	Then empty basket is shown

 
  @Basket 
  Scenario Outline: Customer views basket with x1 products, totalling x1 quantity
	Given customer is on product page : <Product>
	And adds product to basket
	And customer views basket
	Then basket with <ProductCount> products and <Quantity> quantity is shown
   Examples:
    | Product 					| ProductCount	| Quantity	|
    | XBOX		 				| 1				| 1			|
    | COOKERS 					| 1				| 1			|
    | PUSHCHAIR 				| 1				| 1			|
    | SPIDERMAN					| 1				| 1			|
    | FOOTBALL 					| 1				| 1			|
    | WEIGHTS					| 1				| 1			|
    | JEANS 					| 1				| 1			|       
    | RING					 	| 1				| 1			|	


  @Basket @retest
  Scenario Outline:  Customer views basket with x1 products, totalling x2 quantity
	Given customer is on product page : <Product>
	And adds product: <Product> to basket twice
	When customer views basket
	Then basket with <ProductCount> products and <Quantity> quantity is shown
	When customer changes quantity of first product to x10
	Then basket with <ProductCount> products and <QuantityUpdated> quantity is shown
	
   Examples:
    | Product 					| ProductCount	| Quantity	| QuantityUpdated	|
    | WEIGHTS					| 1				| 2			| 10				|     
    | FOOTBALL 					| 1				| 2			| 10				| 
    | JEANS 					| 1				| 2			| 10				|       

    
  @Basket 
  Scenario Outline: Customer views basket with x2 products, totalling x2 quantity
	Given customer is on homepage
    When customer searches by product: <Product> using search feature
	And adds first product to basket x1 quantity
	And adds second product to basket x1 quantity
	When customer views basket
	Then basket with <ProductCount> products and <Quantity> quantity is shown
   Examples:
    | Product 					| ProductCount	| Quantity	|
    | PUSHCHAIR 				| 2				| 2			|
    | SPIDERMAN					| 2				| 2			|
    | FOOTBALL 					| 2				| 2			|

    
  @Basket 
  Scenario Outline: Customer views basket with x2 products, totalling x4 quantity then removes 1st product
	Given customer is on homepage
    When customer searches by product: <Product> using search feature
	And adds first product to basket x2 quantity
	And adds second product to basket x2 quantity
	When customer views basket
	Then basket with <ProductCount> products and <Quantity> quantity is shown
	When customer removes firt product from basket
	Then basket with <ProductCountUpdated> products and <QuantityUpdated> quantity is shown
	
   Examples:
    | Product 					| ProductCount	| Quantity	| ProductCountUpdated	| QuantityUpdated	|
    | COOKERS 					| 2				| 4			| 1						| 2					|
    | JEANS 					| 2				| 4			| 1 					| 2					|  
    | XBOX		 				| 2				| 4			| 1						| 2					|    






	
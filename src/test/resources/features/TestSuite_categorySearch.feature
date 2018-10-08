Feature: CategorySearch 
		 Customer has ability to browse for items by category (both search and click)

  Background: 
  	Given customer is on homepage

 
  @CategorySearch
   Scenario Outline: customer searches by category using category menu
	When customer clicks on category: <Category> in the menu
	Then category splash screen is shown
   Examples:
    | Category 					|
    | TECHNOLOGY 				|
    | HOME AND GARDEN 			|
    | BABY AND NURSERY 			|
    | TOYS 						|
    | SPORTS AND LEISURE 		|
    | HEALTH AND BEAUTY 		|
    | CLOTHING 					|        
    | JEWELLERY AND WATCHES 	| 	

  @CategorySearch 	
 Scenario Outline: customer navigates to subcategory via category menu hover feature then goes to niche-category
	When customer hovers on category: <Category> in the menu
	And clicks on sub-category: <Sub-category>
	Then category splash screen is shown
	And clicks on niche-category via category splash screen: <Niche-category>
	Then results of matching products are shown by order of relevance
   Examples:
    | Category 				|Sub-category					|Niche-category 				|
    | TECHNOLOGY 			|televisions and accessories	|hdmi-cables-and-optical-cables	|
    | TECHNOLOGY 			|televisions and accessories	|tv-remote-controls 			|  
    | TECHNOLOGY 			|Laptops and PCs 				|Gaming laptops and PCs 		|
    | TECHNOLOGY 			|Laptops and PCs 				|imacs				 			|
    | HOME AND GARDEN 		|living room furniture			|armchairs-and-chairs			|
    | HOME AND GARDEN 		|living room furniture			|cd-and-dvd-storage				|
    | HOME AND GARDEN 		|Bedding						|duvets							|
    | HOME AND GARDEN 		|Bedding						|pillows						|
    | BABY AND NURSERY 		|Travel							|pushchairs						|
    | BABY AND NURSERY 		|Travel							|baby-carriers					|
    | BABY AND NURSERY 		|safety and health				|baby health         			|
    | CLOTHING 				|Womens 						|accessories					| 
    | CLOTHING 				|Womens 						|dresses						|
    | CLOTHING 				|Mens 							|coats-and-jackets				|      

  @CategorySearch	 
  Scenario Outline: customer navigates to niche-category via category menu hover feature
	When customer hovers on category: <Category> in the menu
	And clicks on niche-category via main header: <Niche-category>
	Then results of matching products are shown by order of relevance
	Examples:
    | Category 					| Niche-category 						|
    | TECHNOLOGY 				| Dash Cams 							|
    | HOME AND GARDEN 			| Sofas		 							|
    | BABY AND NURSERY 			| Pushchairs 							|
    | TOYS 						| Batteries And Rechargeable Batteries	|
    | SPORTS AND LEISURE 		| Treadmills 							|
    | HEALTH AND BEAUTY 		| Hair Dryers							|
    | CLOTHING 					| Bras		 							|       
    | JEWELLERY AND WATCHES 	| Ladies' Earrings 						|



  @CategorySearch
  Scenario Outline: customer searches for category using misspelling
	When customer searches and misspells <category> with <misspelling> using search feature
	Then results of matching products are shown by order of popularity
   Examples:
    | Category 					| misspelling 				|
    | TECHNOLOGY 				| TECNOLOGY 			 	|
    | GARGEN		 			| GARDON					|
    | NURSERY 					| NERSERY					|
    | TOYS 						| TOIS			 			|
    | LEISURE 					| LESSURE					|
    | HEALTH			 		| helth						|
    | CLOTHING 					| CLOTHIN 			 		|       
    | JEWELLERY				 	| JEWLERY					|


  @CategorySearch 
  Scenario Outline: customer searches for unrecognised category
	When customer searches for unrecognised category: <Category> using search feature
	Then no search results page is shown
   Examples:
    | Category 			|
    | gdfgdfe			|
    | 12332432			|
    | xxxxxxx			|
    | TBYSPOS 			|
    | SPOSPO			|
    | AND AND AND 		|
    | OR OR OR  		|        
    | @@@				| 

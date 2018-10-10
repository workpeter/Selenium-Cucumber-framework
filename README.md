# Selenium Cucumber framework

## Author

* **Peter Anderson (peter.x4000@gmail.com)** 

## Built With

* [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) - Programming language
* [Selenium (inc. Selenium Grid)](https://en.wikipedia.org/wiki/Selenium_(software)) - Automate web applications
* [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm) - Java implementation of Cucumber
* [Maven](https://en.wikipedia.org/wiki/Apache_Maven) - Build automation tool
* [TestNG](https://en.wikipedia.org/wiki/TestNG) - Testing framework
* [Browsermob web proxy](https://github.com/lightbody/browsermob-proxy) - Web proxy to capture HTTP content
* [Master Thoughts](https://mvnrepository.com/artifact/net.masterthought/cucumber-reporting) - Provides pretty html reports for Cucumber

## Introduction

A common approach is to trigger Selenium tests using Maven, either as part of a complete build solution or just as a convenient way to compile your tests and manage your dependencies.   

Selenium at its core is just a browser automation tool, and so a test framework like TestNG is required in order to encapsulate Selenium code into discrete tests, provide features such as assertions, control execution flow, test configuration, data providers, parallel execution, reporting etc.     

When Cucumber is added to the mix, it acts as an abstract layer that sits on top of Selenium and TestNG. Cucumber provides the means to write human-readable tests and group them into scenarios which describe how to use your business features.   
To actual make those steps do something, an automation tester (like me) will 'glue' those steps to Selenium code.     

The beauty of this approach is when the results are shared, the code is hidden, and the report focuses on the easy-to-follow business steps, which are highlighted with a pass or fail.     
* [Example Report](https://cdn.rawgit.com/workpeter/cucumber-selenium-framework/731b904d/example%20report/feature-overview.html)
  
Using this framework has many advantages, which are listed below in the feature list. However, the main benefit is to provide a stable and fast test solution.

This framework comes with example tests for the www.argos.co.uk website. The tests are written using 'page object modelling' style, which is recommended because it promotes code modularisation allowing for reusable code and easier to maintain scripts.    


## Features
 
 * **Enhanced web drivers**  
When this framework creates WebDrivers for you it does many things under the hood. Such as: 
    * Dynamically download and configure the browser drivers for any major browser and operating system. 
    * Provide enhanced capabilities such as capturing browser logs and HTTP traffic (including **Javascript errors**)
    * Ability to switch between local and remote webdriver (Selenium Grid)
    * Route common user actions (click, select, etc) through custom methods in order to make them more reliable. Additional checks include verifying element exists and waiting for Ajax call completions.   
    * Provide dozens of custom methods for common script solution thus reducing the number of code lines required in tests and encouraging standardisation.  
    * Every WebDriver launched is wrapped in a 'threadLocal' container called "webdriver". Meaning all scripts can reference this webdriver object to manipulate the webdriver, however, they will only influence their own webdriver without causing conflicts with other WebDrivers. This makes writing each script straightforward. 
 
* **Scaleable web drivers**  
At its heart, this framework has two classes which manage both the creation and configuration of WebDrivers.  
In the TestNG configuration file the tester defines:   
The number of WebDrivers to execute, their scope, browser type and expected operating system. When the framework is launch, these drivers are dynamically created (no coding required) and will automatically begin to execute your Cucumber tests. 

* **Parallel Execution:**   
By providing more than one test condition in the TestNG configuration file, you also have the option to run these tests in parallel. Meaning you could run different Cucumber scenarios in parallel, or even run the same scenarios but with different operating system/browsers in parallel. It's very flexible. 

* **Selenium Grid support**  
Whilst the TestNG configuration file is used for configuring WebDrivers, the Maven POM file is used to do more high-level configuration such as switching on/off Selenium grid. If for example, you wanted to test Chrome, Firefox and Edge in parallel you can do this locally without Grid. However, you would be limited to one operating system. If you decided to switch on Selenium grid, this framework will automatically use your configured nodes to execute tests, meaning you can configure your WebDrivers to run against different operating systems too.  

* **Advanced logging:**     
This framework does logging in two ways. The main way is using listeners which detect failures, then outputs log files containing trace logs, scenario name, failed URL, and also screenshot. Each failure has its own log file which are categorised by browser type and operating system.   
The second method is triggered when the custom methods are used. When triggered, they run checks on browser warning/errors (including **javascript errors**), HTTP error code detection and slow web element detection.  
This level of logging can be switched on/off globally via the Maven POM file. 

* **Clear automated reporting**  
Every parallel test that runs continuously outputs its result into its own JSON file. As each test completes, these JSON files are compiled into a custom Cucumber reporter, which converts them into a pretty and interactive report. This single report combines the results of all tests and allows you to drill down into specific features, tests and even stack traces. 
    * [Example Report](https://cdn.rawgit.com/workpeter/cucumber-selenium-framework/731b904d/example%20report/feature-overview.html)

* **Data Driven:**  
Cucumber comes with data-driven capabilities within the feature files. However, this framework also provides a means to pull data from excel files too. When Cucumber is used as a data driver, the actual data values appear in the test step names. This is sometimes helpful, but sometimes too much information. One smart method is to use an abstract data references such as "VIP Customer 1", then link that to a row in Excel which pulls all the low-level details for your test. That way the test report shows the high-level data which is useful but hides the low-level incidental data. 

## Running the tests

Below is a typical maven command to trigger the test.

Run with:
```bash
   mvn clean verify
```

### Run steps

1) Maven is triggered, things get compiled and tests run. 
2) The Maven Fail-safe plugin triggers the TestNG framework, which in turn looks at the TestNG config file which defines the test scope.
3) Each test configuration launches a Runner() instance, which launches Cucumber Scenarios using TestNG.  

### Example test configurations

**Parallel execution of browsers. Each browser executes all scenarios**  
Note: With Selenium Grid switched on in Maven, its possible to change operating systems and browser versions (assuming you have the nodes to supports those configurations).  

~~~
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Environment config tsts" parallel="tests"
    thread-count="10">

    <listeners>
        <listener class-name="selenium_tests.Listeners"></listener>
    </listeners>

    <test name="windows chrome - no tags (all features)">
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="chrome" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>

    <test name="windows firefox - no tags (all features)">
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="firefox" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>

    <test name="windows edge - no tags (all features)">
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="edge" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>
    
</suite> 
~~~

**Parallel execution of features. Each instance of Chrome targets a specific feature**
~~~
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Environment config tsts" parallel="tests"
    thread-count="10">

    <listeners>
        <listener class-name="selenium_tests.Listeners"></listener>
    </listeners>


    <test name="windows chrome - just Product feature">
        <parameter name="tags" value="@Product" />
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="chrome" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>


    <test name="windows chrome - just Homepage feature">
        <parameter name="tags" value="@Homepage" />
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="chrome" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>    

    <test name="windows chrome - just Basket feature">
        <parameter name="tags" value="@Basket" />
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="chrome" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>    
    
    <test name="windows chrome - just CategorySearch feature">
        <parameter name="tags" value="@CategorySearch" />
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="chrome" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>            
    
</suite> 
~~~ 
 
### Project structure

* The Cucumber feature files have **.feature** extension. This is where the human readable tests are written/stored.
* The actual Selenium test code is in the **feature_steps** folder. 
* The Runner class 'glues' Cucumber and Selenium together by matching the step names. The Runner class also:
  * Is the main class triggered by TestNG. When triggered, an instance is created. Multiple instances mean parallel tests. This is configured in the TestNG XML file (environment_configurations_to_test.xml).
  * Ues the Webdriver_builder class to dynamically build an enhanced webdriver per instance.
  * Triggers Cucumber scenarios using the TestNG framework.
* The files in the page_object_model folder are used for storing DOM locator references. These references are shared by multiple scripts and means maintenance is centralised. 
* Step modularisation is inherently achieved using Cucumber steps, therefore it's not required within page_object_model files. This also simplifies the code. 

~~~
C:.
|   .classpath
|   .project
|   environment_configurations_to_test.xml
|   pom.xml
|   README.md
|
\---src
    +---main
    |   +---java
    |   |   \---myApp
    |   \---resources
    \---test
        +---java
        |   \---selenium_tests
        |       |   External_data_reader.java
        |       |   Listeners.java
        |       |   Report_generator.java
        |       |   Runner.java
        |       |   Webdriver_builder.java
        |       |
        |       +---feature_steps
        |       |       Steps_basket_feature.java
        |       |       Steps_categorySearch_feature.java
        |       |       Steps_homepage_feature.java
        |       |       Steps_product_feature.java
        |       |
        |       \---page_object_model
        |               POM_basket.java
        |               POM_categorySplashPage.java
        |               POM_mainHeader.java
        |               POM_popup.java
        |               POM_popupBasket.java
        |               POM_productPage.java
        |               POM_productResults.java
        |
        \---resources
            +---data
            \---features
                    TestSuite_basket.feature
                    TestSuite_categorySearch.feature
                    TestSuite_homepage.feature
                    TestSuite_Product.feature
~~~

### Page Object Modelling

This is a style for developing Selenium scripts, which is advised regardless if using a framework like this or not.
In coding, it's always good practice to create modularised, reusable code contained within classes and/or methods. This makes code maintenance easier and enforces standardisations.  

page-object-modelling follows this principle. The idea is to create a class for each webpage, and within it save DOM element locations and common methods/operations performed on that page.   

Personally, I do page-object-modelling with static modifiers allowing me to directly reference DOM elements from scripts without having to create an instance of the page-object-model class. A small thing, but reduces unnecessary lines of code. And whenever a DOM affecting multiple scripts updates, I only need to update the reference in one place.  

When it comes to using Cucumber, I don't put common steps within the page-object-modelling classes anymore. I used to, but then I realised Cucumber inherently forces you to break your tests in discrete steps using @Given @When @Then. These steps are reusable and are grouped within a feature so they are related to each other in scope. I used to keep the code within these steps abstract and call a page-object-model method to do the work, but then I noticed for every step I was creating two methods (one in Cucumber and the other in a page-object-model class). This created redundant code, which took longer to debug.  And so I would only recommend putting methods in a page-object-model class if a BDD framework wasn't being used.    

Example page object model class
~~~
import org.openqa.selenium.By;

public final class POM_exampleClass {

	public static final By example1 = By.xpath("//*[@id=\"searchTerm\"]");
	public static final By example2 = By.xpath("//span[@class=\"price\"]");
	public static final By example3 = By.xpath("(//a[@class=\"ac-product-link ac-product-card__image\"])[1]");	
	public static final By example4 = By.xpath("(//a[@class=\"ac-product-link ac-product-card__image\"])[2]");	
	public static final By example5 = By.xpath("//select[starts-with(@id,'quantity')]");
	public static final By example6 = By.xpath("//img[contains(@style, 'transform:translate(0px, 0px)')]");	
	public static final By example7 = By.xpath("//a[contains(.,'Technology')]");
	
}

~~~

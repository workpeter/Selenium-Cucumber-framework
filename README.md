# Selenium Cucumber framework

## Author

* **Peter Anderson (peter.x4000@gmail.com)** 

## Introduction

A common approach is to trigger Selenium tests using Maven, either as part of a complete build solution or just as a convenient way to compile your tests and manage your dependencies.   

Selenium at its core is just a browser automation tool, and so a test framework like TestNG is required in order to encapsulate Selenium code into discrete tests, provide features such as assertions, control execution flow, test configuration, data providers, parallel execution, reporting etc.     

When Cucumber is added to the mix, it acts as an abstract layer that sits on top of Selenium and TestNG. Cucumber provides the means to write human-readable tests and group them into scenarios which describe how to use your business features.   
To actual make those steps do something, an automation tester (like me) will 'glue' those steps to Selenium code.     

The beauty of this approach is when the results are shared, the code is hidden, and the report focuses on the easy-to-follow business steps, which are highlighted with a pass or fail.     
* [Example Report](https://cdn.rawgit.com/workpeter/cucumber-selenium-framework/731b904d/example%20report/feature-overview.html)
  
Using this framework has many advantages, which are listed below in the feature list. However, the main benefit is to provide a stable and fast test solution.

This framework comes with example tests for the www.argos.co.uk website. The tests are organised using 'page object modelling' style, which is recommended because it promotes code modularisation making it reusable and easier to manage.    


## Features
 

* **Scaleable web drivers**  
At its heart, this framework has two classes which manage both the creation and configuration of WebDrivers.  
In the TestNG configuration file the tester defines how many WebDrivers they want running, their scope, browser type and expected operating system. When the framework is launch, these drivers will be dynamically created (no coding required) and will automatically begin to execute your Cucumber tests. 

* **Selenium Grid support**  
Whilst the TestNG configuration file is used for configuring WebDrivers, the Maven POM file is used to do more high-level configuration such as switching on/off Selenium grid. If for example, you wanted to test Chrome, Firefox and Edge in parallel you can do this locally without Grid. However, you would be limited to one operating system. If you decided to switch on Selenium grid, this framework will automatically use your nodes to execute tests, meaning you can configure your WebDrivers to use different operating systems too.  

* **Parallel Execution:**   
By providing more than one test condition in the TestNG configuration file, you also have the option to run these tests in parallel. Meaning you could run different Cucumber scenarios in parallel, or even run the same scenarios but with different operating system/browsers in parallel. It's very flexible. 

* **Enhanced web drivers**  
When this framework creates WebDrivers for you it does many things under the hood. Such as: 
  * Dynamically download and configure browser drivers for any major browser. 
  * Provide enhanced capabilities such as capturing browser logs and HTTP traffic
  * Provides custom methods for writing scripts, which are more reliable and realistic. For example, when clicking on an element, the framework will first wait until its present, scroll it into view and ensure its visible before clicking on it. 
  * Dynamic waiting is a crucial feature built into each custom method. This ensures script dynamically wait for elements to be in the right condition before proceeding. To assist with this, the methods also wait until Ajax calls have completed, to ensure the page is fully loaded. This technique greatly improves script robustness, and the use of dynamic rather than static wait times means the script run very efficiently. 
  * Every WebDriver launched is wrapped in a 'threadLocal' container called "webdriver". Meaning all scripts can reference this webdriver object to manipulate the webdriver, however, they will only influence their own webdriver without causing conflicts with other webdrivers. This makes writing each script straightforward. 

* **Advanced logging:**     
This framework does logging in two ways. The main way is using listeners which detect failures, then outputs log files containing trace logs, scenario name, failed URL, and also screenshot. Each failure has its own log file which are categorised by browser type and operating system.   
The second method is triggered when the custom methods are used. When custom methods are triggered, they also perform checks and are able to detect things like browser warning/errors (including **javascript issues**), HTTP client/server errors and slow web elements.  
This level of logging can be switched on/off globally via the Maven POM file. 

* **Clear automated reporting**  
Every parallel test that runs continuously outputs its result into its own JSON file. As each test completes, these JSON files are read by a custom Cucumber reporter, which turns them into a pretty and interactive report. This single report combines the results of all tests and allows you to drill down into specific features, tests and even stack traces. 
* [Example Report](https://cdn.rawgit.com/workpeter/cucumber-selenium-framework/731b904d/example%20report/feature-overview.html)

* **Data Driven:**

Cucumber inherently id data-driven when your feature file scenarios contain data lines (using the scenario outline feature). However, this framework also provides a means to pull data from excel files too. When Cucumber is used as a data driver, the actual data values appear in the test step names. This is sometimes helpful, but sometimes too much information. One smart method is to use an abstract data references such as "VIP Customer 1", then link that to a row in Excel which pulls all the low-level details for your test. That way the test report shows the high-level data which is useful but hides the low-level incidental data. 


## Built With

* [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) - Programming language
* [Selenium (inc. Selenium Grid)](https://en.wikipedia.org/wiki/Selenium_(software)) - Automate web applications
* [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm) - Java implementation of Cucumber
* [Maven](https://en.wikipedia.org/wiki/Apache_Maven) - Build automation tool
* [TestNG](https://en.wikipedia.org/wiki/TestNG) - Testing framework
* [Browsermob web proxy](https://github.com/lightbody/browsermob-proxy) - Web proxy to capture HTTP content
* [Master Thoughts](https://mvnrepository.com/artifact/net.masterthought/cucumber-reporting) - Provides pretty html reports for Cucumber

## Running the tests

Below is a typical maven command to trigger the test.

Run with:
```bash
   mvn clean verify"
```

### Steps

* Maven is triggered, things get compiled and tests run. 
* The Maven Fail-safe plugin triggers the TestNG framework, which in turn looks at the TestNG config file which defines the test scope.
* Each test configuration launches a Runner() instance, which launches Cucumber Scenarios using TestNG.  


### Selenium design pattern 

**Runner class**  
Parameters provided from the TestNG and Maven XML files help build a unique webdriver with specific capabilities. 

The simplest most efficient way to build a webdriver is to build it once, and then make it static so that it can be referenced by tests in other classes. 
This avoids other classes having to dynamically build their own webdriver instance, which is inefficient when this process has to be repeated across all the classes containing tests. 

However, the problem with a static webdriver is you only have one, making parallel processing problematic. This is because parallel tests are all interacting with the same static webdriver. This is overcome using ThreadLocal. 

Whenever a runner() instance is generated, it creates a webdriver instance and puts it in the static ThreadLocal container. 

Now in an example where 3 runner instances are launched at the same. 
Each runner generates a webdriver instance and stores it in a static ThreadLocal container.
Each runner then triggers tests (stored in other classes) in parallel. 

All of those tests don't generate a webdriver, they all simply reference the same ThreadLocal container. However rather than the parallel tests all impacting the same webdriver, they are now only impacting their local thread webdriver.   
Thus you achieve autonomous parallel processing without having to inefficiently recreate webdrivers for each test class.  

**Webdriver_builder class**  
This class is a webdriver which is self-configuring based on the parameters sent to it when its launched. 

Key features include:
1. Can be a local or remote web driver and includes added driver capabilities typically used in testing such as bypassing certification issues.  
* When used as a remote driver it acts as a hub and can be any operating system, browsers and browser version that are supported by your nodes.
* When used as a local driver it self-configures web driver binaries using a driver manager and supports all major browsers.

2. Has enhanced logging for test failures that include a detailed stack trace, screenshot and HAR file dump.  

3. Has an inner class with enhanced Selenium methods.  

These methods should be called when building test scripts rather than natively calling the webdriver, because they are designed to build more robust scripts and to track HTTP issues.   
      
Robust scripting is achieved through the auto use of Ajax waiting after events, explicit wait conditions and javascript scrolling. 
This has proven to reduce the failure rate of test scripts without causing unnecessary test run delay.      
   
HTTP tracking is achieved by routing requests through a proxy and then auto logging requests that result in HTTP error codes or performance issues.   
  
There are also some utility methods, for example, the method which logs and takes screenshots on failure. Such a method works well when called by a TestNG listener on failure.


**Page object model (pom) classes**     
A java class is created per webpage which contains the pages key DOM objects, and methods to manipulate the page.  

**Feature steps classes**    
Are the glue which links cucumber feature scenarios to Selenium code. These are kept as abstract as possible, often utilising Selenium code found in page object model classes to drive testing. 



 

# Cucumber Selenium framework

## Author

* **Peter Anderson (peter.x4000@gmail.com)** 

## Features
 
This Selenium cucumber framework has the following features:  

* **BDD Driven:** Reads standard BDD cucumber feature files written in Gerkin. Feature files are linked to Selenium code, which is wrapped within the TestNG framework. This enables web-based test automation based on easy to follow scenarios. 

* **Build Automation:** Maven is used to build and execute the scripts. A common scenario includes triggering a Maven build after a code commit, which in turn will trigger the Selenium tests and provide feedback which is asserted to decide if the build is a success or not. Maven also handles all the library dependencies required by the scripts and is used to pass high-level testing parameters such as test scope, test env URL, enable/disable web proxy, enable/disable Selenium grid.

* **Advanced logging:** A combination of custom methods, web proxy and TestNG listeners, enable a detailed log file and folder per failure. Details such as stack trace, screenshot, HTTP error code (if applicable), slow HTTP resources (if applicable), HAR file dump, Scenario name and environment configuration (i.e. which Operating system and browser) are logged. 
  * [Example 1](https://en.wikipedia.org/wiki/Java_(programming_language))
  * [Example 2](https://en.wikipedia.org/wiki/Java_(programming_language))

* **More reliable scripts:** Each request is routed via custom functions which dynamically use explicit waits and javascript scrolling. This has two notable effects. (1) Scripts act more like end users, and (2) the failure rate is massively reduced when interacting with dynamic DOM elements. Script processing speed is very efficient. 

* **Parallel processing:** Using the TestNG XML file and Java ThreadLocal, parallel processing is achieved. The current setup enables the same tests to run with different configurations (operating systems and browsers) at the same time. For example, an eCommerce site can be tested in parallel against Windows-Edge, Linux-Firefox, and Mac-Chrome.

* **Selenium Grid:** Can be switched on/off within the Maven XML file. When switched on, this framework will automatically launch a remote webdriver based on the configuration details provided and will run configurations in accordance to the TestNG XML file. When switched off, it will instead use the local webdriver. Local webdriver can also run tests in parallel however because tests would run from the same machine, it logically to only test multiple browsers and not operating systems. To test the later, Selenium Grid-enabled is advised. 

* **Data Driven:** Cucumber inherently enables scenarios to be data-driven within the feature file by using the scenario outline feature. However, this framework also contains a class for interacting with excel files, which can be used as a subsite to supply data. When used in conjunction with Cucumber its practical use is to store low-level data and for cucumber to store abstract representational data. This keeps the Cucumber scenarios more human readable.  

* **Code Modulation:** The framework comes with an example project, which utilises the Selenium **page object model**. This model promotes code modularisation allowing for more efficient script maintenance. 

* **Clear automated reporting** The framework uses the master thoughts cucumber reporter and compiles all parallel tests within a single report. The reports are HTML based and can be interacted with to drill down into specific areas.   
  * [Example](https://en.wikipedia.org/wiki/Java_(programming_language))

## Built With

* [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) - Programming language
* [Selenium (inc. Selenium Grid)](https://en.wikipedia.org/wiki/Selenium_(software)) - Automate web applications
* [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm) - Java implementation of Cucumber
* [Maven](https://en.wikipedia.org/wiki/Apache_Maven) - Build automation tool
* [TestNG](https://en.wikipedia.org/wiki/TestNG) - Testing framework
* [Browsermob web proxy](https://github.com/lightbody/browsermob-proxy) - Web proxy to capture HTTP content
* [Master Thoughts](https://mvnrepository.com/artifact/net.masterthought/cucumber-reporting) - Provides pretty html reports for Cucumber

## Running the tests

Below is a typical maven command to trigger the test. The cucumber.options tag can be omitted to trigger all cucumber scenarios or set to trigger specific tests.   

Run with:
```bash
   mvn clean verify -Dcucumber.options="-t @Smoke"
```

### Configuration

There are two key config files:
* Maven's pom.xml file:  
  
Manages library dependency, plugins and used to set env URL, switch on/off Selenium grid and Browsermob web proxy capturing.

* TestNG's XML file (environment_configurations_to_test.xml):  
  
Manages various environment configurations (Operating system, browser and browser version) and parallel testing using multi-threading. 

### TestNG's xml file (environment_configurations_to_test.xml): 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Environment config tsts" parallel="tests"
    thread-count="3">

    <listeners>
        <listener class-name="selenium_tests.Listeners"></listener>
    </listeners>

    <test name="windows chrome">
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="chrome" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>

    <test name="windows firefox">
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="firefox" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>

    <test name="windows edge">
        <parameter name="operating_system" value="windows" />
        <parameter name="browser" value="edge" />
        <classes>
            <class name="selenium_tests.Runner" />
        </classes>
    </test>


</suite> 
```

### Steps

* Maven triggers build. 
* Fail-safe plugin triggers TestNG and looks at TestNG's XML file. 
* TestNG's XML file will trigger tests via the runner() class. The example included in this framework runs 3 configurations in parallel. 
* The runner is responsible for generating a ThreadLocal webdriver, executing cucumber scenarios using the TestNG framework and triggering Cucumber report.
* Cucumber scenarios are driven by Selenium using page object model and custom methods. 
* TestNG listeners trigger logging on test failure. 
* HTTP error code and performance analysis is done automatically when web_proxy is turned on and test scripts use the custom methods to drive testing.  


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



 

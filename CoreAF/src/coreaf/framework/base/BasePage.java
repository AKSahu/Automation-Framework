package coreaf.framework.base;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.google.common.base.Function;

import coreaf.framework.util.CommandList;
import coreaf.framework.util.ConfigUtil;
import coreaf.framework.util.ScreenshotCapture;

/**
 * BasePage is the class that all other page classes should extend for web-based
 * UI testing.<br>
 * The actual usage of this class methods will be on test project.
 * 
 * @author A. K. Sahu
 */

public class BasePage {

	private static WebDriver driver = null;
	private Alert alert = null;

	protected static Logger log = Logger.getLogger(BasePage.class);

	/***********************************************************************
	 * The default timeout is set to 50 seconds
	 * *********************************************************************/
	protected static String timeoutMilliSeconds = "50000"; // time in
															// milliseconds
	protected static long timeoutSeconds = 50; // time in seconds	

    CommandList command = CommandList.getInstance();

	static {
		setTimeout();
	}

	public BasePage() {
		if (driver == null) {
			driver = DriverManager.getDriver();
			windowMaximize();
		}
	}

	/**
	 * Gets the instance of the current driver class
	 * 
	 * @return
	 */
	protected WebDriver getDriver() {
		return BasePage.driver;
	}

	/**
	 * Resets the current driver object to null
	 */
	public void resetDriver() {
		BasePage.driver = null;
	}

	/**
	 * Navigates the page to the given URL
	 * 
	 * @param url
	 */
	public void openUrl(String url) {
		log.info("Openning the url '" + url + "' in the browser...");
		command.reportSuccess("Openning the url '" + url + "' in the browser...");
		driver.navigate().to(url);
	}

	/**
	 * Starts the test by opening the browser
	 */
	protected void init() {
		openUrl(ConfigUtil.getBaseUrl());
	}

	/**
	 * Maximizes the current browser window
	 */
	protected void windowMaximize() {
		log.info("Maximizing the browser window.");
		command.reportSuccess("Maximizing the browser window.");
		driver.manage().window().maximize();

		if (DriverManager.isBrowserChrome()) {

			Point targetPosition = new Point(0, 0);
			driver.manage().window().setPosition(targetPosition);

			String w = "return screen.availWidth";
			String h = "return screen.availHeight";
			JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
			int width = ((Long) javascriptExecutor.executeScript(w)).intValue();
			int height = ((Long) javascriptExecutor.executeScript(h))
					.intValue();
			Dimension targetSize = new Dimension(width, height);

			driver.manage().window().setSize(targetSize);
		}
	}

	/**
	 * Returns the default timeout defined in <code>timeoutMilliSeconds</code>
	 * in the <code>BasePage.java</code>. This value changes if we define
	 * <code>timeout</code> property in the
	 * <code>config/config.properties</code> file
	 * 
	 * @return
	 */
	public String getTimeOutInMilliSeconds() {
		return timeoutMilliSeconds;
	}

	/**
	 * Returns the default timeout defined in <code>timeoutSeconds</code> in the
	 * <code>BasePage.java</code>. This value changes if we define
	 * <code>timeout</code> property in the
	 * <code>config/config.properties</code> file
	 * 
	 * @return
	 */
	public long getTimeOutInSeconds() {
		return timeoutSeconds;
	}

	/**
	 * Sets the waiting time to the specified timeout value in the
	 * <code>config/config.properties</code>, if not mentioned the default
	 * timeout value is the timeout defined in <code>BasePage.java</code>
	 */
	private static void setTimeout() {
		String timeout = ConfigUtil.getTimeOut();
		if ((timeout != null) && (!"".equals(timeout))) {
			timeoutMilliSeconds = timeout;
			timeoutSeconds = Integer.parseInt(timeout) / 1000;
			log.info("The timeout value is set to " + timeoutSeconds
					+ " seconds.");
		}
	}

	/**
	 * Finds an element by the given <code>by</code> locator
	 * 
	 * @param by
	 *            a locator e.g. xpath, css, name, id, class name etc
	 * @return the web element
	 */
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}

	/**
	 * Finds all the elements by the given <code>by</code> locator
	 * 
	 * @param by
	 *            a locator e.g. xpath, css, name, id, class name etc
	 * @return the web elements
	 */
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	/**
	 * Finds an element by executing the given jQuery statement
	 * <code>jQueryScript</code>
	 * 
	 * @param jQueryScript
	 *            a JQuery Script e.g. For an element 'div' with class 'sorted' <br>
	 *            (1)To find the element use:
	 *            <code>jQuery(\"div.sorted\")</code>. Here we need to use
	 *            jQuery selectors only like in this example 'div.sorted'. <br>
	 *            (2)To find parent of the element use:
	 *            <code>jQuery(\"div.sorted\").parent()</code> <br>
	 *            (3)To mouseover on the element use:
	 *            <code>jQuery(\"div.sorted\").mouseover()</code>
	 * @return WebElement that is described by that jQuery statement
	 *         <code>jQueryScript</code>, if not found, returns null.
	 */
	public WebElement findElementByJQuery(final String jQueryScript) {
		WebElement element = null;

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeoutSeconds, TimeUnit.SECONDS)
				.pollingEvery(50, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class);
		try {
			element = wait.until(new Function<WebDriver, WebElement>() {
				@Override
				public WebElement apply(WebDriver d) {
					String script = "return " + jQueryScript + ".get(0);";
					JavascriptExecutor jse = (JavascriptExecutor) d;
					WebElement webElement = (WebElement) jse
							.executeScript(script);
					return webElement;
				}
			});
		} catch (Exception e) {
			log.error("Failed to find the element by executing JQuery script '"
					+ jQueryScript + "'." + e);
		}
		return element;
	}

	/**
	 * Find all the elements by executing the given jQuery statement
	 * <code>jQueryScript</code>
	 * 
	 * @param jQueryScript
	 *            a JQuery Script e.g. For all the 'div' elements with class
	 *            'sorted' <br>
	 *            (1)To find the element use:
	 *            <code>jQuery(\"div.sorted\")</code>. Here we need to use
	 *            jQuery selectors only like in this example 'div.sorted'. <br>
	 *            (2)To find parent of the element use:
	 *            <code>jQuery(\"div.sorted\").parent()</code> <br>
	 *            (3)To mouseover on the element use:
	 *            <code>jQuery(\"div.sorted\").mouseover()</code>
	 * @return WebElement that is described by that jQuery statement
	 *         <code>jQueryScript</code>, if not found, returns null.
	 */
	public List<WebElement> findElementsByJQuery(final String jQueryScript) {
		List<WebElement> elements = null;

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeoutSeconds, TimeUnit.SECONDS)
				.pollingEvery(50, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class);
		try {
			elements = wait.until(new Function<WebDriver, List<WebElement>>() {
				@Override
				public List<WebElement> apply(WebDriver d) {
					List<WebElement> webElements = new ArrayList<WebElement>();
					for (int i = 0;; i++) {
						String script = "return " + jQueryScript + ".get(" + i
								+ ");";
						JavascriptExecutor jse = (JavascriptExecutor) d;
						WebElement webElement = (WebElement) jse
								.executeScript(script);
						if (webElement != null) {
							webElements.add(webElement);
						} else {
							break;
						}
					}
					return webElements;
				}
			});
		} catch (Exception e) {
			log.error("Failed to find the elements by executing JQuery script '"
					+ jQueryScript + "'." + e);
		}
		return elements;
	}

	/**
	 * Mouse overs on the element by its locator <code>by</code>
	 * 
	 * @param by
	 *            a locator e.g. xpath, css, name, id, class name etc Note: Make
	 *            sure to maximize browser before using this
	 */
	public void mouseOverOnElementUsingRobot(By by) {

		log.info("Mouseover on the element located by "+by);
		command.reportSuccess("Mouseover on the element located by "+by);
		try {
			Point coordinates = getDriver().findElement(by).getLocation();
			Robot robot = new Robot();
			robot.mouseMove(coordinates.getX(), coordinates.getY() + 60);
			/*
			 * WebDriver provide document coordinates, where as Robot class is
			 * based on Screen coordinates, so I have added +60 to compensate
			 * the browser header. You can even adjust if needed.
			 */

		} catch (AWTException e) {
			log.error("Failed to mouseover on the element '" + by + "'. " + e);
			command.reportFailure("Failed to mouseover on the element '" + by + "'. " + e);
		}
	}

	/**
	 * Clicks on an element by its location on screen
	 * 
	 * @param by
	 */
	public void clickElementByLocation(By by) {

		log.info("Clicking on element by its location "+by);
		command.reportSuccess("Clicking on element by its location "+by);
		Point coordinates = getDriver().findElement(by).getLocation();
		Actions actions = new Actions(driver);
		Action action = actions
				.moveToElement(findElement(by), coordinates.getX(),
						coordinates.getY() + 60).click().build();
		action.perform();

	}

	/**
	 * Mouse overs on the element by its locator <code>by</code>
	 * 
	 * @param by
	 *            a locator e.g. xpath, css, name, id, class name etc
	 */
	public void mouseOverOnElement(By by) {

		log.info("Mouseover on the element located by "+by);
		command.reportSuccess("Mouseover on the element located by "+by);
		Actions actions = new Actions(driver);
		WebElement mainMenu = driver.findElement(by);
		actions.moveToElement(mainMenu);
		actions.perform();
	}

	/**
	 * Drags the web element <code>byFromElement</code> and drop at web element
	 * <code>byToElement</code>
	 * 
	 * @param byFromElement
	 * @param byToElement
	 */
	public void dragAndDrop(By byFromElement, By byToElement) {

		log.info("Drag and drop from the element "+byFromElement+" to the element "+byToElement);
		command.reportSuccess("Drag and drop from the element "+byFromElement+" to the element "+byToElement);
		Actions builder = new Actions(driver);

		WebElement fromWebElement = driver.findElement(byFromElement);
		WebElement toWebElement = driver.findElement(byToElement);
		Action dragAndDrop = builder.clickAndHold(fromWebElement)
				.moveToElement(toWebElement).release(toWebElement).build();

		dragAndDrop.perform();
	}

	/***********************************************************************
	 * START - Enter text into an input filed by its locator
	 * *********************************************************************/

	/**
	 * Enter text into an input filed by its <code>xpath</code> locator
	 * 
	 * @param xpath
	 * @param text
	 */
	public void typeByXpath(String xpath, String text) {
		log.info("Entering text '" + text
				+ "' to the element specified by xpath '" + xpath + "'");
		command.reportSuccess("Entering text '" + text
				+ "' to the element specified by xpath '" + xpath + "'");
		driver.findElement(By.xpath(xpath)).sendKeys(text);
	}

	/**
	 * Enter text into an input filed by its <code>css</code> locator
	 * 
	 * @param css
	 * @param text
	 */
	public void typeByCSS(String css, String text) {
		log.info("Entering text '" + text
				+ "' to the element specified by css '" + css + "'");
		command.reportSuccess("Entering text '" + text
				+ "' to the element specified by css '" + css + "'");
		driver.findElement(By.cssSelector(css)).sendKeys(text);
	}

	/**
	 * Enter text into an input filed by its <code>id</code> locator
	 * 
	 * @param id
	 * @param text
	 */
	public void typeByID(String id, String text) {
		log.info("Entering text '" + text
				+ "' to the element specified by id '" + id + "'");
		command.reportSuccess("Entering text '" + text
				+ "' to the element specified by id '" + id + "'");
		driver.findElement(By.id(id)).sendKeys(text);
	}

	/**
	 * Enter text into an input filed by its <code>name</code> locator
	 * 
	 * @param name
	 * @param text
	 */
	public void typeByName(String name, String text) {
		log.info("Entering text '" + text
				+ "' to the element specified by name '" + name + "'");
		command.reportSuccess("Entering text '" + text
				+ "' to the element specified by name '" + name + "'");
		driver.findElement(By.name(name)).sendKeys(text);
	}

	/**
	 * Enter text into an input filed by its <code>className</code> locator
	 * 
	 * @param className
	 * @param text
	 */
	public void typeByClassName(String className, String text) {
		log.info("Entering text '" + text
				+ "' to the element specified by className '" + className + "'");
		command.reportSuccess("Entering text '" + text
				+ "' to the element specified by className '" + className + "'");
		driver.findElement(By.className(className)).sendKeys(text);
	}

	/***********************************************************************
	 * END - Enter text into an input filed by its locator
	 * *********************************************************************/

	/***********************************************************************
	 * START - Click on the element by its locator
	 * *********************************************************************/

	/**
	 * Click on the element by its <code>xpath</code> locator
	 * 
	 * @param xpath
	 */
	public void clickByXpath(String xpath) {
		log.info("Clicking on the element specified by xpath '" + xpath + "'");
		command.reportSuccess("Clicking on the element specified by xpath '" + xpath + "'");
		driver.findElement(By.xpath(xpath)).click();
	}

	/**
	 * Click on the element by its <code>css</code> locator
	 * 
	 * @param css
	 */
	public void clickByCSS(String css) {
		log.info("Clicking on the element specified by css '" + css + "'");
		command.reportSuccess("Clicking on the element specified by css '" + css + "'");
		driver.findElement(By.cssSelector(css));
	}

	/**
	 * Click on the element by its <code>id</code> locator
	 * 
	 * @param id
	 */
	public void clickByID(String id) {
		log.info("Clicking on the element specified by id '" + id + "'");
		command.reportSuccess("Clicking on the element specified by id '" + id + "'");
		driver.findElement(By.id(id)).click();
	}

	/**
	 * Click on the element by its <code>name</code> locator
	 * 
	 * @param name
	 */
	public void clickByName(String name) {
		log.info("Clicking on the element specified by name '" + name + "'");
		command.reportSuccess("Clicking on the element specified by name '" + name + "'");
		driver.findElement(By.name(name)).click();
	}

	/**
	 * Click on the element by its <code>className</code> locator
	 * 
	 * @param className
	 */
	public void clickByClassName(String className) {
		log.info("Clicking on the element specified by className '" + className
				+ "'");
		command.reportSuccess("Clicking on the element specified by className '" + className
				+ "'");
		driver.findElement(By.className(className)).click();
	}

	/**
	 * Click on the link by its link text
	 * 
	 * @param linkText
	 */
	public void clickByLinkText(String linkText) {
		log.info("Clicking on the link specified by linkText '" + linkText
				+ "'");
		command.reportSuccess("Clicking on the link specified by linkText '" + linkText
				+ "'");
		driver.findElement(By.linkText(linkText)).click();
	}

	/**
	 * Click on the link by partial of the link text
	 * 
	 * @param partialLinkText
	 */
	public void clickByPartialLinkText(String partialLinkText) {
		log.info("Clicking on the link specified by partialLinkText '"
				+ partialLinkText + "'");
		command.reportSuccess("Clicking on the link specified by partialLinkText '"
				+ partialLinkText + "'");
		driver.findElement(By.partialLinkText(partialLinkText));
	}

	/***********************************************************************
	 * END - Click on the element by its locator
	 * *********************************************************************/

	/***********************************************************************
	 * START - Get the element by its locator
	 * *********************************************************************/

	/**
	 * Get the element by its <code>xpath</code> locator
	 * 
	 * @param xpath
	 * @return
	 */
	public WebElement findElementByXpath(String xpath) {
		log.info("Getting the Webelement specified by xpath '" + xpath + "'");
		return driver.findElement(By.xpath(xpath));
	}

	/**
	 * Get the element by its <code>css</code> locator
	 * 
	 * @param css
	 * @return
	 */
	public WebElement findElementByCSS(String css) {
		log.info("Getting the Webelement specified by css '" + css + "'");
		return driver.findElement(By.cssSelector(css));
	}

	/**
	 * Get the element by its <code>id</code> locator
	 * 
	 * @param id
	 * @return
	 */
	public WebElement findElementByID(String id) {
		log.info("Getting the Webelement specified by id '" + id + "'");
		return driver.findElement(By.id(id));
	}

	/**
	 * Get the element by its <code>name</code> locator
	 * 
	 * @param name
	 * @return
	 */
	public WebElement findElementByName(String name) {
		log.info("Getting the Webelement specified by name '" + name + "'");
		return driver.findElement(By.name(name));
	}

	/**
	 * Get the element by its <code>className</code> locator
	 * 
	 * @param className
	 * @return
	 */
	public WebElement findElementByClassName(String className) {
		log.info("Getting the Webelement specified by className '" + className
				+ "'");
		return driver.findElement(By.className(className));
	}

	/***********************************************************************
	 * END - Get the element by its locator
	 * *********************************************************************/

	/***********************************************************************
	 * START - Get the elements by its locator
	 * *********************************************************************/

	/**
	 * Gets all the elements by their <code>xpath</code> locator
	 * 
	 * @param xpath
	 * @return
	 */
	public List<WebElement> findElementsByXpath(String xpath) {
		log.info("Getting the Webelements specified by xpath '" + xpath + "'");
		return driver.findElements(By.xpath(xpath));
	}

	/**
	 * Get the element by its <code>css</code> locator
	 * 
	 * @param css
	 * @return
	 */
	public List<WebElement> findElementsByCSS(String css) {
		log.info("Getting the Webelements specified by css '" + css + "'");
		return driver.findElements(By.cssSelector(css));
	}

	/**
	 * Get the element by its <code>id</code> locator
	 * 
	 * @param id
	 * @return
	 */
	public List<WebElement> findElementsByID(String id) {
		log.info("Getting the Webelements specified by id '" + id + "'");
		return driver.findElements(By.id(id));
	}

	/**
	 * Get the element by its <code>name</code> locator
	 * 
	 * @param name
	 * @return
	 */
	public List<WebElement> findElementsByName(String name) {
		log.info("Getting the Webelements specified by name '" + name + "'");
		return driver.findElements(By.name(name));
	}

	/**
	 * Get the element by its <code>className</code> locator
	 * 
	 * @param className
	 * @return
	 */
	public List<WebElement> findElementsByClassName(String className) {
		log.info("Getting the Webelements specified by className '" + className
				+ "'");
		return driver.findElements(By.className(className));
	}

	/***********************************************************************
	 * END - Get the elements by its locator
	 * *********************************************************************/

	/**
	 * Gets the coordinates of the element location by its <code>xpath</code>
	 * locator
	 * 
	 * @param xpath
	 * @return
	 */
	public Point getLocationByXpath(String xpath) {
		log.info("Getting the coordinates of the element location by its xpath '"
				+ xpath + "'");
		return driver.findElement(By.xpath(xpath)).getLocation();
	}

	/**
	 * Gets the coordinates of the locator by its <code>css</code> locator
	 * 
	 * @param css
	 * @return
	 */
	public Point getLocationByCSS(String css) {
		log.info("Getting the coordinates of the element location by its css '"
				+ css + "'");
		return driver.findElement(By.cssSelector(css)).getLocation();
	}

	/***********************************************************************
	 * START - Is element present specified by its locator
	 * *********************************************************************/

	/**
	 * Check whether the element specified by its <code>xpath</code> is
	 * displayed
	 * 
	 * @param xpath
	 * @return
	 */
	public boolean isElementDisplayedByXpath(String xpath) {
		log.info("Checking whether the element specified by its xpath '"
				+ xpath + "' is displayed on the page");
		command.reportSuccess("Checking whether the element specified by its xpath '"
				+ xpath + "' is displayed on the page");
		return driver.findElement(By.xpath(xpath)).isDisplayed();
	}

	/**
	 * Check whether the element specified by its <code>css</code> is displayed
	 * 
	 * @param css
	 * @return
	 */
	public boolean isElementDisplayedByCSS(String css) {
		log.info("Checking whether the element specified by its css '" + css
				+ "' is displayed on the page");
		command.reportSuccess("Checking whether the element specified by its css '" + css
				+ "' is displayed on the page");
		return driver.findElement(By.cssSelector(css)).isDisplayed();
	}

	/**
	 * Check whether the element specified by its <code>id</code> is displayed
	 * 
	 * @param id
	 * @return
	 */
	public boolean isElementDisplayedByID(String id) {
		log.info("Checking whether the element specified by its id '" + id
				+ "' is displayed on the page");
		command.reportSuccess("Checking whether the element specified by its id '" + id
				+ "' is displayed on the page");
		return driver.findElement(By.id(id)).isDisplayed();
	}

	/**
	 * Check whether the element specified by its <code>name</code> is displayed
	 * 
	 * @param name
	 * @return
	 */
	public boolean isElementDisplayedByName(String name) {
		log.info("Checking whether the element specified by its name '" + name
				+ "' is displayed on the page");
		command.reportSuccess("Checking whether the element specified by its name '" + name
				+ "' is displayed on the page");
		return driver.findElement(By.name(name)).isDisplayed();
	}

	/**
	 * Check whether the element specified by its <code>className</code> is
	 * displayed
	 * 
	 * @param className
	 * @return
	 */
	public boolean isElementDisplayedByClassName(String className) {
		log.info("Checking whether the element specified by its className '"
				+ className + "' is displayed on the page");
		command.reportSuccess("Checking whether the element specified by its className '"
				+ className + "' is displayed on the page");
		return driver.findElement(By.className(className)).isDisplayed();
	}

	/**
	 * Check whether the element specified by its <code>linkText</code> is
	 * displayed
	 * 
	 * @param linkText
	 * @return
	 */
	public boolean isElementDisplayedByLinkText(String linkText) {
		log.info("Checking whether the element specified by its linkText '"
				+ linkText + "' is displayed on the page");
		command.reportSuccess("Checking whether the element specified by its linkText '"
				+ linkText + "' is displayed on the page");
		return driver.findElement(By.linkText(linkText)).isDisplayed();
	}

	/***********************************************************************
	 * END - Is element present specified by its locator
	 * *********************************************************************/

	/**
	 * Takes screen shot with the specified <code>fileName</code>. This creates
	 * a screenshot of the browser window only.
	 * 
	 * @param fileName
	 *            The name of the file to save the screen shot
	 */
	public static void takeBrowserScreenshot(String fileName) {
		log.info("Taking screen shot for the test '" + fileName + "'");
		try {
			File srcFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);

			String imgLoc = new File(".").getCanonicalPath() + File.separator
					+ ConfigUtil.getScreenshotDirectory() + File.separator
					+ fileName + ".png";

			FileUtils.copyFile(srcFile, new File(imgLoc));

			log.info("Screen shot has been captured '" + imgLoc + "'");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes screen shot with the specified <code>fileName</code>. This creates
	 * a screenshot of the entire window.
	 * 
	 * @param fileName
	 *            The name of the file to save the screen shot
	 */
	public static void takeEntireScreenshot(String fileName) {
		log.info("Taking screen shot for the test '" + fileName + "'");
		ScreenshotCapture.takeScreenshot(fileName);
	}
	
	/**
	 * Closes the current browser instance
	 */
	public void closeBrowser() {
		log.info("Closing the current instance of the browser '"
				+ ConfigUtil.getBrowser() + "'");
		command.reportSuccess("Closing the current instance of the browser '"
				+ ConfigUtil.getBrowser() + "'");
		driver.close();
	}

	/**
	 * Closes all the instances of the browser
	 */
	public void quitBrowser() {
		log.info("Closing all the instances of the browser '"
				+ ConfigUtil.getBrowser() + "'");
		command.reportSuccess("Closing all the instances of the browser '"
				+ ConfigUtil.getBrowser() + "'");
		driver.quit();
	}

	/**
	 * Pauses the flow of execution(thread) up to the given milliseconds
	 * 
	 * @param miliSeconds
	 */
	public void sleep(long miliSeconds) {
		try {
			log.info("Paused the thread for " + miliSeconds + " seconds");
			Thread.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/***********************************************************************
	 * START - Get text of the element specified by its locator
	 * *********************************************************************/

	/**
	 * Gets the text of the element specified by its <code>xpath</code> locator
	 * 
	 * @param xpath
	 * @return
	 */
	public String getTextByXpath(String xpath) {
		log.info("Getting text of the element specified by its xpath '" + xpath
				+ "'");
		command.reportSuccess("Getting text of the element specified by its xpath '" + xpath
				+ "'");
		return driver.findElement(By.xpath(xpath)).getText();
	}

	/**
	 * Gets the text of the element specified by its <code>css</code> locator
	 * 
	 * @param css
	 * @return
	 */
	public String getTextByCSS(String css) {
		log.info("Getting text of the element specified by its css '" + css
				+ "'");
		command.reportSuccess("Getting text of the element specified by its css '" + css
				+ "'");
		return driver.findElement(By.cssSelector(css)).getText();
	}

	/**
	 * Gets the text of the element specified by its <code>id</code> locator
	 * 
	 * @param id
	 * @return
	 */
	public String getTextByID(String id) {
		log.info("Getting text of the element specified by its id '" + id + "'");
		command.reportSuccess("Getting text of the element specified by its id '" + id + "'");
		return driver.findElement(By.id(id)).getText();
	}

	/**
	 * Gets the text of the element specified by its <code>name</code> locator
	 * 
	 * @param name
	 * @return
	 */
	public String getTextByName(String name) {
		log.info("Getting text of the element specified by its name '" + name
				+ "'");
		command.reportSuccess("Getting text of the element specified by its name '" + name
				+ "'");
		return driver.findElement(By.name(name)).getText();
	}

	/**
	 * Gets the text of the element specified by its <code>className</code>
	 * locator
	 * 
	 * @param className
	 * @return
	 */
	public String getTextByClassName(String className) {
		log.info("Getting text of the element specified by its className '"
				+ className + "'");
		command.reportSuccess("Getting text of the element specified by its className '"
				+ className + "'");
		return driver.findElement(By.className(className)).getText();
	}

	/***********************************************************************
	 * END - Get text of the element specified by its locator
	 * *********************************************************************/

	/***********************************************************************
	 * START - Clear text of the element specified by its locator
	 * *********************************************************************/

	/**
	 * Clears text of the input field by its <code>xpath</code> locator
	 * 
	 * @param xpath
	 */
	public void clearTextByXpath(String xpath) {
		log.info("Clearing the text of the element specified by its xpath '"
				+ xpath + "'");
		command.reportSuccess("Clearing the text of the element specified by its xpath '"
				+ xpath + "'");
		driver.findElement(By.xpath(xpath)).clear();
	}

	/**
	 * Clears text of the input field by its <code>css</code> locator
	 * 
	 * @param css
	 */
	public void clearTextByCSS(String css) {
		log.info("Clearing the text of the element specified by its css '"
				+ css + "'");
		command.reportSuccess("Clearing the text of the element specified by its css '"
				+ css + "'");
		driver.findElement(By.cssSelector(css)).clear();
	}

	/**
	 * Clears text of the input field by its <code>id</code> locator
	 * 
	 * @param id
	 */
	public void clearTextByID(String id) {
		log.info("Clearing the text of the element specified by its id '" + id
				+ "'");
		command.reportSuccess("Clearing the text of the element specified by its id '" + id
				+ "'");
		driver.findElement(By.id(id)).clear();
	}

	/**
	 * Clears text of the input field by its <code>name</code> locator
	 * 
	 * @param name
	 */
	public void clearTextByName(String name) {
		log.info("Clearing the text of the element specified by its name '"
				+ name + "'");
		command.reportSuccess("Clearing the text of the element specified by its name '"
				+ name + "'");
		driver.findElement(By.name(name)).clear();
	}

	/**
	 * Clears text of the input field by its <code>className</code> locator
	 * 
	 * @param className
	 */
	public void clearTextByClassName(String className) {
		log.info("Clearing the text of the element specified by its className '"
				+ className + "'");
		command.reportSuccess("Clearing the text of the element specified by its className '"
				+ className + "'");
		driver.findElement(By.className(className)).clear();
	}

	/***********************************************************************
	 * END - Clear text of the element specified by its locator
	 * *********************************************************************/

	/***********************************************************************
	 * START - Deal with Alert
	 * *********************************************************************/

	/**
	 * Waits for the alert to appear
	 * 
	 * @param timeOutInSeconds
	 */
	public void waitForAlert(long timeOutInSeconds) {
		log.info("Waiting for alert...");
		command.reportSuccess("Waiting for alert...");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.alertIsPresent());
	}

	/**
	 * Switches to the Alert window from current window
	 * 
	 * @return
	 */
	public Alert switchToAlert() {
		log.info("Switching to the alert...");
		command.reportSuccess("Switching to the alert...");
		return alert = driver.switchTo().alert();
	}

	/**
	 * Accepts the current alert
	 */
	public void acceptAlert() {
		if (alert == null)
			switchToAlert();
		log.info("Accepting the alert...");
		command.reportSuccess("Accepting the alert...");
		alert.accept();
	}

	/**
	 * Dismisses the current alert
	 */
	public void dismissAlert() {
		if (alert == null)
			switchToAlert();
		log.info("Dismissing the alert...");
		command.reportSuccess("Dismissing the alert...");
		alert.dismiss();
	}

	/**
	 * Gets text from the alert shown in the current page
	 */
	public String getTextFromAlert() {
		if (alert == null)
			switchToAlert();
		log.info("Getting text from the alert");
		command.reportSuccess("Getting text from the alert");
		return alert.getText();
	}

	/***********************************************************************
	 * END - Deal with Alert
	 * *********************************************************************/

	/**
	 * Switches to the default content window from other window(e.g. Alert
	 * window)
	 */
	public void swtichToDefaultContent() {
		log.info("Switching to the Default Content window...");
		command.reportSuccess("Switching to the Default Content window...");
		driver.switchTo().defaultContent();
	}

	/**
	 * Checks whether the specified <code>text</code> is present in the page
	 * 
	 * @param text
	 * @return
	 */
	public boolean isTextPresent(String text) {
		log.info("Checking for the text '" + text + "' in the page");
		command.reportSuccess("Checking for the text '" + text + "' in the page");
		return driver.getPageSource().contains(text);
	}

	/**
	 * Assert a title equals exactly a string specified by <code>title</code>.
	 * 
	 * @param title
	 *            The title of current page
	 */
	protected void assertTitle(String title) {
		String actualTitle = driver.getTitle();
		command.reportSuccess("Asserting title of the page");
		Assert.assertEquals(title, actualTitle, "Expect HTML title '" + title
				+ "' but got '" + actualTitle + "'.");

	}

	/**
	 * Assert that text specified in <code>text</code> exists in the current
	 * page.
	 * 
	 * @param text
	 *            The text that should be present
	 */
	protected void assertText(String text) {
		command.reportSuccess("Asserting text '"+text+"' in the page");
		Assert.assertTrue(isTextPresent(text), "Expected text '" + text
				+ "' in the page not found.");

	}

	/***********************************************************************
	 * START - Wait for element by its locator
	 * *********************************************************************/

	/**
	 * Waits for the element specified by its <code>xpath</code> locator
	 * 
	 * @param xpath
	 * @param timeOutInSeconds
	 */
	public void waitForElementLocatedByXpath(String xpath, long timeOutInSeconds) {
		log.info("Waiting for the element specified by xpath '" + xpath + "'");
		command.reportSuccess("Waiting for the element specified by xpath '" + xpath + "'");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	}

	/**
	 * Waits for the element specified by its <code>css</code> locator
	 * 
	 * @param css
	 * @param timeOutInSeconds
	 */
	public void waitForElementLocatedByCSS(String css, long timeOutInSeconds) {
		log.info("Waiting for the element specified by css '" + css + "'");
		command.reportSuccess("Waiting for the element specified by css '" + css + "'");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.cssSelector(css)));
	}

	/**
	 * Waits for the element specified by its <code>id</code> locator
	 * 
	 * @param id
	 * @param timeOutInSeconds
	 */
	public void waitForElementLocatedByID(String id, long timeOutInSeconds) {
		log.info("Waiting for the element specified by id '" + id + "'");
		command.reportSuccess("Waiting for the element specified by id '" + id + "'");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
	}

	/**
	 * Waits for the element specified by its <code>name</code> locator
	 * 
	 * @param name
	 * @param timeOutInSeconds
	 */
	public void waitForElementLocatedByName(String name, long timeOutInSeconds) {
		log.info("Waiting for the element specified by name '" + name + "'");
		command.reportSuccess("Waiting for the element specified by name '" + name + "'");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
	}

	/**
	 * Waits for the element specified by its <code>className</code> locator
	 * 
	 * @param className
	 * @param timeOutInSeconds
	 */
	public void waitForElementLocatedByClassName(String className,
			long timeOutInSeconds) {
		log.info("Waiting for the element specified by className '" + className
				+ "'");
		command.reportSuccess("Waiting for the element specified by className '" + className
				+ "'");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.className(className)));
	}

	/**
	 * Waits for the element specified by its <code>linkText</code> locator
	 * 
	 * @param linkText
	 * @param timeOutInSeconds
	 */
	public void waitForElementLocatedByLinkText(String linkText,
			long timeOutInSeconds) {
		log.info("Waiting for the element specified by linkText '" + linkText
				+ "'");
		command.reportSuccess("Waiting for the element specified by linkText '" + linkText
				+ "'");
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.linkText(linkText)));
	}

	/***********************************************************************
	 * END - Wait for element by its locator
	 * *********************************************************************/

	/**
	 * Switch to the window specified by the <code>arg0</code> target locator
	 * 
	 * @param arg0
	 */
	public void switchToWindow(String arg0) {
		command.reportSuccess("Switching to window: "+arg0);
		driver.switchTo().window(arg0);
	}

	/**
	 * Switch to the frame specified by the <code>arg0</code> target locator
	 * 
	 * @param arg0
	 */
	public void switchToFrame(String arg0) {
		command.reportSuccess("Switching to frame: "+arg0);
		driver.switchTo().frame(arg0);
	}

	/**
	 * Waits for a page to load for <code>timeOutInSeconds</code> number of
	 * seconds.
	 * 
	 * @param timeOutInSeconds
	 */
	public void waitForPageToLoad(long timeOutInSeconds) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
		try {
			log.info("Waiting for page to load...");
			command.reportSuccess("Waiting for page to load...");
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			wait.until(expectation);
		} catch (Throwable error) {
			log.error("Timeout waiting for Page Load Request to complete after "
					+ timeOutInSeconds + " seconds");
			command.reportFailure("Timeout waiting for Page Load Request to complete after "
					+ timeOutInSeconds + " seconds");
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}
	}

	/**
	 * Waits until there are no more active ajax connection and until the
	 * specified <code>timeoutInSeconds</code> is timeout
	 * 
	 * @param timeoutInSeconds
	 */
	public void waitForAjax(long timeoutInSeconds) {
		log.info("Checking active ajax calls by calling jquery.active");
		command.reportSuccess("Checking active ajax calls by calling jquery.active");
		try {
			if (driver instanceof JavascriptExecutor) {
				JavascriptExecutor jsDriver = (JavascriptExecutor) driver;

				for (int i = 0; i < timeoutInSeconds; i++) {
					Object numberOfAjaxConnections = jsDriver
							.executeScript("return jQuery.active");
					// return should be a number
					if (numberOfAjaxConnections instanceof Long) {
						Long n = (Long) numberOfAjaxConnections;
						log.info("Number of active jquery ajax calls: " + n);
						if (n.longValue() == 0L)
							break;
					}
					Thread.sleep(1000);// 1 second sleep
				}
			} else {
				log.error("Web driver: " + driver
						+ " cannot execute javascript");
			}
		} catch (Exception e) {
			log.error("Failed to wait for ajax response: " + e);
			command.reportFailure("Failed to wait for ajax response: " + e);
		}
	}

	/**
	 * Waits until there are no more active ajax connection and until the
	 * specified <code>timeoutInSeconds</code> is timeout
	 * 
	 * @param timeOutInSeconds
	 * @return
	 */
	public boolean waitForAjaxComplete(final long timeOutInSeconds) {
		log.info("Checking active ajax calls by calling jquery.active");
		command.reportSuccess("Checking active ajax calls by calling jquery.active");
		final WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		return wait.until(new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				final long startTime = System.currentTimeMillis();
				final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;

				while ((startTime + timeOutInSeconds) >= System
						.currentTimeMillis()) {
					final Boolean scriptResult = (Boolean) javascriptExecutor
							.executeScript("return jQuery.active == 0");
					if (scriptResult)
						return true;

					sleep(100);

				}
				return false;
			}
		});
	}

	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	public Set<String> getWindowHandles() {
		return driver.getWindowHandles();
	}
}

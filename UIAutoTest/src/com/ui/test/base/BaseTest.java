package com.ui.test.base;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.ui.test.flows.LoginFlow;
import com.ui.test.pages.LoginPage;
import com.ui.test.pages.HomePage;

/**
 * This class is to be extended from actual test classes. It provides
 * functionalities that is common to all test cases (eg. login and logout)
 * 
 * @author A. K. Sahu
 * 
 */
public class BaseTest {

	protected HomePage homePage = null;
	protected LoginPage loginPage = null;
	protected static Logger log = Logger.getLogger(BaseTest.class);

	@BeforeMethod(alwaysRun = true)
	public void runBeforeMethod() {

		loginPage = new LoginPage();
		log.info("Logging in to the application...");
		homePage = LoginFlow.login(loginPage, loginPage.getUserName(),
				loginPage.getPassword());
	}

	@AfterMethod(alwaysRun = true)
	public void runAfterMethod() {

		if (homePage != null) {
			log.info("Logging out of the application...");
			homePage.clickLogout();
		}

		if (loginPage != null) {
			loginPage.closeBrowser();
			loginPage.quitBrowser();
			loginPage.resetDriver();
			loginPage = null;
		}
	}
}
package com.ui.test.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ui.test.flows.LoginFlow;
import com.ui.test.pages.LoginPage;

public class LoginPageTest {

//	@DataProvider
//	public Object[][] ValidDataProvider() {
//		return new Object[][] { { "admin", "admin" }, { "admin", "admin" } };
//	}
//
//	@Test(dataProvider = "ValidDataProvider", groups = { "alltests", "Sanity" }, description = "Login to application")
//	public void test_TCID1_LoginToApplication(String username, String password) {
//		LoginPage loginPage = new LoginPage();
//		LoginFlow.login(loginPage, username, password);
//		if (loginPage != null) {
//			loginPage.quitBrowser();
//		}
//	}

	@Test(groups = { "alltests", "Sanity" }, description = "Login to application with Super user", invocationCount = 2)
	public void test_TCID2_LoginWithSuperUser() {
		LoginPage loginPage = new LoginPage();
		LoginFlow.login(loginPage, loginPage.getUserName(),
				loginPage.getPassword());
		if (loginPage != null) {
			loginPage.closeBrowser();
			loginPage.quitBrowser();
		}
	}

	@Test(groups = { "alltests", "Sanity" }, description = "Login to application with Admin user", invocationCount = 0)
	public void test_TCID2_LoginWithAdminUser() {
		LoginPage loginPage = new LoginPage();
		LoginFlow.login(loginPage, loginPage.getUserName(),
				loginPage.getPassword());
		if (loginPage != null) {
			loginPage.closeBrowser();
			loginPage.quitBrowser();
		}
	}
}

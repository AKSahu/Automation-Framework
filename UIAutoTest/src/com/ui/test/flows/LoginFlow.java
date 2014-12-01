package com.ui.test.flows;

import com.ui.test.pages.LoginPage;
import com.ui.test.pages.HomePage;

public class LoginFlow {

	/**
	 * Login to application
	 * 
	 * @param loginPage
	 * @param userName
	 * @param password
	 * @return
	 */
	public static HomePage login(LoginPage loginPage, String userName,
			String password) {
		loginPage.typeUsername(userName);
		loginPage.typePassword(password);
		loginPage.clickSigninButton();
		return new HomePage(loginPage);
	}
}

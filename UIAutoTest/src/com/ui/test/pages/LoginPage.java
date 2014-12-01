package com.ui.test.pages;

import org.testng.Assert;

import coreaf.framework.base.BasePage;
import coreaf.framework.util.ConfigUtil;

public class LoginPage extends BasePage {

	private static final String TEXTFIELD_NAME_USER_NAME = "tl_login";
	private static final String TEXTFIELD_NAME_PASSWORD = "tl_password";
	private static final String BUTTON_NAME_LOGIN = "login_submit";

	public LoginPage() {
		super.init();
	}

	public LoginPage(BasePage basePage) {
		waitForElementLocatedByName(BUTTON_NAME_LOGIN, getTimeOutInSeconds());
		this.validate();
	}

	private void validate() {

		assertTitle("TestLink");

		assertText("Please log in ...");
		assertText("Login");
		assertText("Password");
		Assert.assertTrue(isElementDisplayedByLinkText("New User?"),
				"The link is not shown.");
	}

	/**
	 * Gets the value of 'username' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public String getUserName() {
		return ConfigUtil.getProperty("username");
	}

	/**
	 * Gets the value of 'password' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public String getPassword() {
		return ConfigUtil.getProperty("password");
	}

	public void typeUsername(String username) {
		typeByName(TEXTFIELD_NAME_USER_NAME, username);
	}

	public void typePassword(String password) {
		typeByName(TEXTFIELD_NAME_PASSWORD, password);
	}

	public void clickSigninButton() {
		clickByName(BUTTON_NAME_LOGIN);
	}

}

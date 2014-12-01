package com.ui.test.pages;

import coreaf.framework.base.BasePage;

public class HomePage extends BasePage {

	private static final String BUTTON_CSS_LOGOUT = "img[title='Logout']";
	
	public HomePage(BasePage basePage) {
		waitForPageToLoad(getTimeOutInSeconds());
		this.validate();
	}

	/**
	 * Validate that we are indeed in the Home Page
	 * 
	 */
	private void validate() {
//		assertTitle("TestLink 1.9.12 [Dev] (GaGa and the twins)");

//		assertText("System");
//		assertText("Test Project");
//		assertText("Requirements");
//		assertText("Test Plan");
//		assertText("Test Execution");
	}

	public void clickLogout() {
//		clickByCSS(BUTTON_CSS_LOGOUT);
		//wait for element
	}
}

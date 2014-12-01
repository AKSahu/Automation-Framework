package com.ui.test.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ui.test.base.BaseTest;

import coreaf.framework.util.CommandList;

public class HomePageTest extends BaseTest {

//	CommandList command = CommandList.getInstance();

	@Test(groups = { "alltests", "Smoke" }, description = "Verify Home Page")
	public void test_TCID3_VerifyHomePage() throws Exception {
		try {
			homePage.clickByLinkText("Define Custom Fields");
			homePage.waitForElementLocatedByName("create_cfield", 10);
			Assert.assertTrue(homePage.isTextPresent("Custom fields"),
					"Expected text 'Custom fields' in the page not found.");
		} catch (Exception e) {
//			command.reportFailure(e.getMessage());
			throw e;
		}
	}

	@Test(groups = { "alltests", "Sanity" }, description = "Upload photo in the Naukri application", priority = 3)
	public void test_TCID4_PerformActions() throws Exception {
		try {
			homePage.clickByLinkText("Test Project Management ");
			homePage.waitForElementLocatedByID("create", 10);
			Assert.assertTrue(
					homePage.isTextPresent("Test Project Management"),
					"Expected text 'Test Project Management' in the page not found.");
		} catch (Exception e) {
//			command.reportFailure(e.getMessage());
			throw e;
		}
	}

	@Test(groups = { "alltests", "Sanity" }, description = "Upload photo in the Naukri application", priority = 3)
	public void test_TCID5_CheckTools() throws Exception {
		try {
			homePage.clickByLinkText("Test Project Management ");
			homePage.waitForElementLocatedByID("create", 10);
			Assert.assertTrue(
					homePage.isTextPresent("Test Project Management"),
					"Expected text 'Test Project Management' in the page not found.");
		} catch (Exception e) {
//			command.reportFailure(e.getMessage());
			throw e;
		}
	}
}

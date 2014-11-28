package coreaf.framework.report;

import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import coreaf.framework.util.DateUtil;
import coreaf.framework.util.ScreenshotCapture;

/**
 * This class has to be added to the testng task to listen for events.
 * 
 * It has an extra functionality that it takes a screenshot(of the entire
 * screen) when a test pass or fails or skip.
 * 
 * Example usage:
 * 
 * <pre>
 * <testng outputdir="reports/reportng" groups="smoke" useDefaultListeners="true"
 *         listener="org.uncommons.reportng.HTMLReporter,org.uncommons.reportng.JUnitXMLReporter,com.pramati.core.util.GenericReportNGListener">
 * </pre>
 * 
 * @author A. K. Sahu
 */
public class GenericReportNGListener extends TestListenerAdapter {

	private int count = 0;
	private static Logger log = Logger.getLogger(GenericReportNGListener.class);

	@Override
	public void onTestFailure(ITestResult result) {

		doReportNGReporting(result, "FAILED");

	}

	@Override
	public void onTestSkipped(ITestResult result) {

		doReportNGReporting(result, "SKIPPED");
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		doReportNGReporting(result, "PASSED");
	}

	/**
	 * The method does all reporting into ReportsNG.
	 * 
	 * @param result
	 * @param status
	 */
	private void doReportNGReporting(ITestResult result, String status) {

		try {

			String timestamp = DateUtil.getTimeStamp();
			String testName = result.getName();
			String screenshotFileUrl = "file:///"
					+ ScreenshotCapture.getScreenshotDirectory() + testName
					+ timestamp + ".png";

			log.debug(status + ": Screenshot file location of test '"
					+ testName + "' is : " + screenshotFileUrl);

			if (status.equals("SKIPPED")) {
				if (count == 0) {// take only one screenshot
					ScreenshotCapture.takeScreenshot(testName + timestamp);
					count++;
				}
			} else
				ScreenshotCapture.takeScreenshot(testName + timestamp);

			Reporter.setCurrentTestResult(result);

			Object[] parameters = result.getParameters();
			Reporter.log("<p><font face=arial size=2 color=000099");
			if (parameters.length > 0)
				Reporter.log("<p>Total number of input parameters: "
						+ parameters.length + "<p>");

			for (int i = 0; i < parameters.length; i++) {
				Reporter.log("Parameter: " + parameters[i]);
			}

			Reporter.log("<b>Screenshot</b><br>");
			Reporter.log("<p><a href='" + screenshotFileUrl + "'>"
					+ "<img src='" + screenshotFileUrl
					+ "' height='100' width='100'/></a>");
			Reporter.log("<p>");
			Reporter.log("<font size=1>Click thumbnail image to view screenshot</font><p><br></font>");

			Reporter.setCurrentTestResult(null);

		} catch (Exception e) {
			log.error(e);
		}
	}
}

package coreaf.framework.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import coreaf.framework.base.BasePage;
import coreaf.framework.base.DriverManager;
import coreaf.framework.util.CommandList;
import coreaf.framework.util.ConfigUtil;
import coreaf.framework.util.DateUtil;
import coreaf.framework.util.ScreenshotCapture;

/**
 * This class has to be added to the testng task to listen for events.
 * 
 * It has an extra functionality that it takes a screenshot(of the browser
 * window) when a test pass or fails or skip.
 * 
 * Example usage:
 * 
 * <pre>
 * <testng outputdir="reports/reportng" groups="smoke" useDefaultListeners="true"
 *         listener="org.uncommons.reportng.HTMLReporter,org.uncommons.reportng.JUnitXMLReporter,com.pramati.core.util.CustomizedReportNGListener">
 * </pre>
 * 
 * @author A. K. Sahu
 */
public class CustomizedReportNGListener extends TestListenerAdapter {

	private int divId = 1;
	private static Logger log = Logger
			.getLogger(CustomizedReportNGListener.class);

	private List<String> classNames = new ArrayList<String>();
	private List<String> methodNames = new ArrayList<String>();
	private List<String> statuses = new ArrayList<String>();
	private List<String> exeTime = new ArrayList<String>();

	private int totalTestCount = 0;
	private int passedTestCount = 0;
	private int skippedTestCount = 0;
	private int failedTestCount = 0;
	private int pendingTestCount = 0;

	private int SUCCESS = 1;
	private int FAILURE = 2;
	private int SKIP = 3;

	private String currentStatus = "Tests are running...";
	long totalDuration = 0;

	@Override
	public void onStart(ITestContext context) {
		super.onStart(context);

		ITestNGMethod[] iTestNGMethods = context.getAllTestMethods();
		this.totalTestCount = iTestNGMethods.length;
		for (ITestNGMethod iTestNGMethod : iTestNGMethods) {
			classNames.add(iTestNGMethod.getRealClass().getName());
			methodNames.add(iTestNGMethod.getMethodName().toString());
			statuses.add("PENDING");
			exeTime.add(". . .");
		}
		pendingTestCount = totalTestCount;
		generateTestExecutionReport();
	}

	// @Override
	// public void beforeConfiguration(ITestResult result) {
	// super.beforeConfiguration(result);
	//
	// CommandList.getInstance().clearSuccessList();
	// CommandList.getInstance().clearFailureList();
	// }
	// ##################
	@Override
	public void onConfigurationSuccess(ITestResult result) {
		super.onConfigurationSuccess(result);

		CommandList.getInstance().clearSuccessList();
		CommandList.getInstance().clearFailureList();
	}

	@Override
	public void onConfigurationFailure(ITestResult result) {
		super.onConfigurationFailure(result);

		CommandList.getInstance().clearSuccessList();
		CommandList.getInstance().clearFailureList();
	}

	@Override
	public void onConfigurationSkip(ITestResult result) {
		super.onConfigurationSkip(result);

		CommandList.getInstance().clearSuccessList();
		CommandList.getInstance().clearFailureList();
	}

	// ############################
	@Override
	public void onTestSuccess(ITestResult result) {

		super.onTestSuccess(result);
		doReportNGReporting(result, "PASSED");
		refreshCurrentResults(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {

		super.onTestFailure(result);
		doReportNGReporting(result, "FAILED");
		refreshCurrentResults(result);

	}

	@Override
	public void onTestSkipped(ITestResult result) {

		super.onTestSkipped(result);
		doReportNGReporting(result, "SKIPPED");
		refreshCurrentResults(result);
	}

	@Override
	public void onFinish(ITestContext context) {
		super.onFinish(context);
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
				BasePage.takeEntireScreenshot(testName + timestamp);
			} else
				BasePage.takeBrowserScreenshot(testName + timestamp);

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

			includeEnvironmemtDetails(divId++);
			generateCommandLogReport(divId++);

			Reporter.setCurrentTestResult(null);

		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Shows test environment details in the report
	 * 
	 * @param divId
	 */
	private void includeEnvironmemtDetails(int divId) {

		if (!ConfigUtil.getProperty("showEnvironmentDetails").equals("true")) {
			return;
		}

		log.debug("including environment details");

		String newId = "envDetailsDiv" + divId;

		// Note: toggleElement is a javascript function provided by ReportsNG
		Reporter.log("<p><input style=\"background-color:#ededed;border:1px solid #dcdcdc;padding:3px;border-radius: 5px;box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);\" type=\"button\" onclick=\"javascript:toggleElement('"
				+ newId
				+ "', 'block');\" value=\"Show/Hide Environment Details\" /><p><br>");

		Reporter.log("<div class=\"mid\" id=\"" + newId
				+ "\" style=\"DISPLAY: none\">");

		Reporter.log("<style>.env {font-size:11px;color:darkBlue;padding:2px;;background-color:#f2f2f2;}</style>");

		Reporter.log("<table style=\"width:400px;border: 2px dotted grey;border-radius: 5px;padding:2px;box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);\">");
		Reporter.log("<tr class='env' style=\"background-color:grey;color:white;\"><td style=\"padding: 0.5em 1em 0.5em 1em;\">Environment Details:</td><td></td></tr>");
		Reporter.log("<tr class='env'><td >Browser Name: </td><td>"
				+ DriverManager.getCurrentBrowserName() + "</td><tr>");
		Reporter.log("<tr class='env'><td >Browser Version: </td><td>"
				+ DriverManager.getCurrentBrowserVersion() + "</td><tr>");
		Reporter.log("<tr class='env'><td >Operating System: </td><td>"
				+ DriverManager.getCurrentOperatingSystem() + "</td><tr>");
		Reporter.log("<tr class='env'><td >OS Architecture: </td><td>"
				+ DriverManager.getCurrentOperatingSystemArchitecture()
				+ "</td><tr>");
		Reporter.log("<tr class='env'><td >Java Version: </td><td>"
				+ DriverManager.getCurrentJavaVersion() + "</td><tr>");
		Reporter.log("</table>");

		Reporter.log("</div><br>");

	}

	/**
	 * Log the selenium command output to testNG report area
	 * 
	 * @param divId
	 */
	private void generateCommandLogReport(int divId) {

		log.debug("Is selenium command list empty: "
				+ CommandList.getInstance().isEmptySuccessList());

		if (CommandList.getInstance().isEmptySuccessList()
				|| !ConfigUtil.getProperty("captureSeleniumCommands").equals(
						"true")) {
			CommandList.getInstance().clearSuccessList();
			return;
		}

		String newId = "commandlogdiv" + divId;

		String[] listSuccess = CommandList.getInstance().getSuccessList();

		log.debug("Writing the selenium command log with : "
				+ listSuccess.length + " commands");

		// Note: toggleElement is a javascript function provided by ReportsNG
		Reporter.log("<p><input style=\"background-color:#ededed;border:1px solid #dcdcdc;padding:3px;border-radius: 5px;box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);\" type=\"button\" onclick=\"javascript:toggleElement('"
				+ newId
				+ "', 'block');\" value=\"Show/Hide Commands Log Report\" /><p><br>");
		Reporter.log("<div class=\"mid\" id=\"" + newId
				+ "\" style=\"DISPLAY: none\">");

		Reporter.log("<style>"
				+ "table tr th:first-child, td:first-child { border-top-left-radius:0.5em;border-bottom-left-radius:0.5em;}"
				+ "table tr th:last-child, td:last-child {  border-top-right-radius:0.5em;border-bottom-right-radius:0.5em;}"
				+ "</style>");

		Reporter.log("<style>.cLog {font-family: Arial, Helvetica, sans-serif;font-size:13px;color:darkBlue;}</style>");// border-collapse:
																														// collapse;
		Reporter.log("<table cellpadding=\"3\" cellspacing=\"3\" class='cLog' style=\"border:2px dotted #778899;border-radius: 5px;\">");
		Reporter.log("<tr><td class='cLog' style=\"background-color:#778899;color:white;padding: 0.5em 1em 0.5em 1em;\"><b>Commands Executed:</b></td><td class='cLog' style=\"background-color:#778899;color:white;padding: 0.5em 1em 0.5em 1em;\"><b>Status</b></td></tr>");

		for (int i = 0; i < listSuccess.length; i++) {

			if (i % 2 == 0) {
				Reporter.log("<tr class='cLog' style=\"background-color:#f2f2f2;\"><td class='cLog'>");
			} else {
				Reporter.log("<tr class='cLog' style=\"background-color:#f4f4f4;\"><td class='cLog'>");
			}
			Reporter.log(listSuccess[i]);
			Reporter.log("</td><td class='cLog' style=\"width:30px;vertical-align: middle;text-align: center;\"><font size=1.6 color=green><b>&#10004;</b></font></td><tr>");
		}

		if (!CommandList.getInstance().isEmptyFailureList()) {
			String[] listFailure = CommandList.getInstance().getFailureList();
			for (int i = 0; i < listFailure.length; i++) {
				if ((listSuccess.length + i) % 2 == 0) {
					Reporter.log("<tr class='cLog' style=\"background-color:#f2f2f2;\"><td class='cLog'>");
				} else {
					Reporter.log("<tr class='cLog' style=\"background-color:#f4f4f4;\"><td class='cLog'>");
				}
				Reporter.log(listFailure[0]);
				Reporter.log("</td><td class='cLog' style=\"width:30px;vertical-align: middle;text-align: center;\"><font size=1.6 color=red><b>&#10008;</b></font></td><tr>");
			}
		}

		Reporter.log("</table>");
		Reporter.log("<br>");

		CommandList.getInstance().clearSuccessList();
		CommandList.getInstance().clearFailureList();
	}

	private void refreshCurrentResults(ITestResult iTestResult) {

		long startTime = iTestResult.getStartMillis();
		long endTime = iTestResult.getEndMillis();
		long currentDuration = (endTime - startTime);
		totalDuration = totalDuration + currentDuration;

		int index = methodNames
				.indexOf(iTestResult.getMethod().getMethodName());
		if (iTestResult.getStatus() == SUCCESS) {
			statuses.set(index, "SUCCESS");
		} else if (iTestResult.getStatus() == FAILURE) {
			statuses.set(index, "FAILURE");
		} else if (iTestResult.getStatus() == SKIP) {
			statuses.set(index, "SKIP");
		} else {
			// do nothing
		}

		passedTestCount = getPassedTests().size();
		failedTestCount = getFailedTests().size();
		skippedTestCount = getSkippedTests().size();
		pendingTestCount = totalTestCount
				- (passedTestCount + failedTestCount + skippedTestCount);

		exeTime.set(index, String.valueOf(currentDuration));
		generateTestExecutionReport();
	}

	private void generateTestExecutionReport() {
		if (pendingTestCount == 0) {
			currentStatus = "Test execution completed!";
		}
		String percent = String.format("%.2f",
				(float) ((totalTestCount - pendingTestCount) * 100.0f)
						/ totalTestCount);
		try {
			String fileName = ConfigUtil.getReportsDirectory() + File.separator
					+ "reportng" + File.separator + "intermediateReport.html";
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("<html><head><title>Test Execution Status</title>");
			bw.write("<style>"
					+ "table tr th:first-child, td:first-child { border-top-left-radius:0.5em;border-bottom-left-radius:0.5em;}"
					+ "table tr th:last-child, td:last-child {  border-top-right-radius:0.5em;border-bottom-right-radius:0.5em;}"
					+ "</style>");
			bw.write("</head><body style=\"font-family: arial,sans-serif;color:darkBlue;\">");

			bw.write("<h3>Execution summary of tests:</h3>");

			bw.write("<table style=\"width:500px\">"
					+ "<tr><td><b>Status: </b>"
					+ currentStatus
					+ "</td><td style=\"width:100px\"><b>Time: </b>"
					+ totalDuration
					+ " ms"
					+ "</td></tr><tr><td><b>Progress Info: </b><progress value=\""
					+ (totalTestCount - pendingTestCount) + "\" max=\""
					+ totalTestCount + "\"></progress>&nbsp;" + percent
					+ " %</td><td style=\"width:100px\">&nbsp;</td></tr><tr>");
			bw.write("<table cellpadding=\"5\" style=\"width:500px;border:2px dashed maroon;text-align:center;font-size:14px;\">"
					+ "<tr>"
					+ "<td style=\"background-color:green;color:white;\">Passed</td>"
					+ "<td style=\"background-color:#D5B96A;color:white;\">Skipped</td>"
					+ "<td style=\"background-color:#F08080;color:white;\">Failed</td>"
					+ "<td style=\"background-color:#A9A9A9;color:white;\">Pending</td>"
					+ "<td style=\"background-color:#778899;color:white;\">Total</td></tr>"
					+ "<tr><td style=\"background-color:#AAD279;\">"
					+ passedTestCount
					+ "</td><td style=\"background-color:#FCEDB0;\">"
					+ skippedTestCount
					+ "</td><td style=\"background-color:#F7C9BA;\">"
					+ failedTestCount
					+ "</td><td style=\"background-color:#F5F5F5;\">"
					+ pendingTestCount
					+ "</td><td style=\"background-color:#D3D3D3;\">"
					+ totalTestCount + "</td></tr></table>" + "</tr></table>");

			bw.write("<br><h3>Execution status of all the tests:</h3>");

			bw.write("<table cellpadding=\"5\" cellspacing=\"3\" style=\"-webkit-border-radius:10px;-moz-border-radius:10px;border-radius:10px;font-size:14px;border: 2px solid #3E4F69;padding:7px;\">");

			bw.write("<tr style=\"background-color:#3E4F69;color:white;font-size:15px;padding: 0.5em 1em 0.5em 1em;\">"
					+ "<th> Test Class/ Method Name </th><th> Status </th> <th> Time </th></tr>");

			for (int i = 0; i < totalTestCount; i++) {
				String status = statuses.get(i);
				if (status.equals("SUCCESS")) {
					bw.write("<tr style=\"background-color:#AAD279;\"><td>"
							+ classNames.get(i) + " / " + methodNames.get(i)
							+ "</td><td><center>" + status
							+ "</center></td><td><center>" + exeTime.get(i)
							+ " ms" + "</center></td></tr>");
				} else if (status.equals("FAILURE")) {
					bw.write("<tr style=\"background-color:#F7C9BA;\"><td>"
							+ classNames.get(i) + " / " + methodNames.get(i)
							+ "</td><td><center>" + status
							+ "</center></td><td><center>" + exeTime.get(i)
							+ " ms" + "</center></td></tr>");
				} else if (status.equals("SKIP")) {
					bw.write("<tr style=\"background-color:#FCEDB0;\"><td>"
							+ classNames.get(i) + " / " + methodNames.get(i)
							+ "</td><td><center>" + status
							+ "</center></td><td><center>" + exeTime.get(i)
							+ " ms" + "</center></td></tr>");
				} else {
					bw.write("<tr style=\"background-color:#F5F5F5;\"><td>"
							+ classNames.get(i) + " / " + methodNames.get(i)
							+ "</td><td><center>" + status
							+ "</center></td><td><center>" + exeTime.get(i)
							+ "</center></td></tr>");
				}
			}
			bw.write("</table>");
			bw.write("</body></html>");
			bw.close();
		} catch (Exception e) {
			log.error("Couldn't show intermediate execution report."
					+ e.getMessage());
		}

	}

}

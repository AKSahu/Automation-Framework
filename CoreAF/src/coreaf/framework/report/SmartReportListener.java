package coreaf.framework.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.IConfigurationListener2;
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
 *         listener="org.uncommons.reportng.HTMLReporter,org.uncommons.reportng.JUnitXMLReporter,coreaf.framework.report.SmartReportListener">
 * </pre>
 * 
 * @author A. K. Sahu
 */
public class SmartReportListener extends TestListenerAdapter implements
		IConfigurationListener2 {

	private int divId = 1;
	private static Logger log = Logger.getLogger(SmartReportListener.class);

	private List<String> classNames = new ArrayList<String>();
	private List<String> methodNames = new ArrayList<String>();
	private List<String> statuses = new ArrayList<String>();
	private List<String> durations = new ArrayList<String>();
	private List<String> exeTimes = new ArrayList<String>();
	private List<String> details = new ArrayList<String>();
	private List<String> detailsID = new ArrayList<String>();
	private List<String> screenShots = new ArrayList<String>();

	private int totalTestCount = 0;
	private int passedTestCount = 0;
	private int skippedTestCount = 0;
	private int failedTestCount = 0;
	private int pendingTestCount = 0;
	private String executedPercent = "0";

	private int SUCCESS = 1;
	private int FAILURE = 2;
	private int SKIP = 3;

	private String currentStatus = "Tests are running...";
	private String errorLog = "";
	long totalDuration = 0;

	private String defaultStatus = "PENDING";
	private String defaultDuration = ". . .";
	private String defaultExecutionTime = "Not Captured";

	// long configurationTimeStart;
	// long configurationTimeEnd;

	@Override
	public void onStart(ITestContext context) {

		super.onStart(context);

		ITestNGMethod[] iTestNGMethods = context.getAllTestMethods();
		this.totalTestCount = iTestNGMethods.length;
		for (int i = 0; i < totalTestCount; i++) {
			classNames.add(iTestNGMethods[i].getRealClass().getName());
			methodNames.add(iTestNGMethods[i].getMethodName().toString());
			statuses.add(defaultStatus);
			durations.add(defaultDuration);
			exeTimes.add(defaultExecutionTime);
			String aID = "ID"
					+ Long.toHexString(System.currentTimeMillis() + i);
			detailsID.add(aID);
			details.add("<td colspan=\"4\" id=\""
					+ aID
					+ "\" style=\"display:none;\">"
					+ "Execution didn't complete yet ! Refresh page to get current status.<br> The detailed execution report will show only after completing test execution.</td>");
			screenShots.add(ScreenshotCapture.getScreenshotDirectory());
		}
		pendingTestCount = totalTestCount;
		generateCustomizedIntermediateReport();
	}

	@Override
	public void beforeConfiguration(ITestResult result) {

		CommandList.getInstance().clearCommandLog();
		errorLog = "No log found! The test executed successfully or skipped.";

		// configurationTimeStart = result.getStartMillis();
		// configurationTimeEnd = result.getEndMillis();
		// totalDuration = totalDuration
		// + (configurationTimeEnd - configurationTimeStart);
	}

	@Override
	public void onConfigurationSuccess(ITestResult result) {

		super.onConfigurationSuccess(result);

		if (!ConfigUtil.getProperty("captureConfigurationCommands").equals(
				"true")) {
			CommandList.getInstance().clearCommandLog();
		}
	}

	@Override
	public void onConfigurationFailure(ITestResult result) {

		super.onConfigurationFailure(result);

		CommandList.getInstance().reportFailure(
				result.getThrowable().toString());
		CommandList.getInstance()
				.reportFailure(
						Arrays.asList(result.getThrowable().getStackTrace())
								.toString());
	}

	@Override
	public void onConfigurationSkip(ITestResult result) {

		super.onConfigurationSkip(result);

		CommandList.getInstance().clearCommandLog();
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		super.onTestSuccess(result);

		doReportNGReporting(result, "PASSED");

		CommandList.getInstance().clearCommandLog();
	}

	@Override
	public void onTestFailure(ITestResult result) {

		super.onTestFailure(result);

		CommandList.getInstance().reportFailure(
				result.getThrowable().toString());
		CommandList.getInstance()
				.reportFailure(
						Arrays.asList(result.getThrowable().getStackTrace())
								.toString());

		doReportNGReporting(result, "FAILED");

		CommandList.getInstance().clearCommandLog();

	}

	@Override
	public void onTestSkipped(ITestResult result) {

		super.onTestSkipped(result);

		doReportNGReporting(result, "SKIPPED");

		CommandList.getInstance().clearCommandLog();
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

		// try {

		String timestamp = DateUtil.getTimeStamp();
		String testName = result.getName();
		String screenshotFileUrl = "file:///"
				+ ScreenshotCapture.getScreenshotDirectory() + testName
				+ timestamp + ".png";

		log.debug(status + ": Screenshot file location of test '" + testName
				+ "' is : " + screenshotFileUrl);

		try {
			BasePage.takeBrowserScreenshot(testName + timestamp);
		} catch (Exception e) {// status.equals("SKIPPED")
			log.error("Failed to take browser screenshot. Capturing entire screenshot...");
			BasePage.takeEntireScreenshot(testName + timestamp);
		}

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
		Reporter.log("<p><a href='" + screenshotFileUrl + "'>" + "<img src='"
				+ screenshotFileUrl + "' height='100' width='100'/></a>");
		Reporter.log("<p>");
		Reporter.log("<font size=1>Click thumbnail image to view screenshot</font><p><br></font>");

		includeEnvironmemtDetails(divId++);
		generateCommandLogReport(divId++);

		refreshCurrentResults(result, screenshotFileUrl);

		Reporter.setCurrentTestResult(null);

		// } catch (Exception e) {
		// log.error(e);
		// }
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
			if ((listSuccess.length + 1) % 2 == 0) {
				Reporter.log("<tr class='cLog' style=\"background-color:#f2f2f2;\"><td class='cLog'>");
			} else {
				Reporter.log("<tr class='cLog' style=\"background-color:#f4f4f4;\"><td class='cLog'>");
			}
			Reporter.log(listFailure[0]);
			Reporter.log("</td><td class='cLog' style=\"width:30px;vertical-align: middle;text-align: center;\"><font size=1.6 color=red><b>&#10008;</b></font></td><tr>");

		}

		Reporter.log("</table>");
		Reporter.log("<br>");
	}

	private void refreshCurrentResults(ITestResult iTestResult,
			String screenshotFileUrl) {

		long startTime = iTestResult.getStartMillis();
		long endTime = iTestResult.getEndMillis();
		long currentDuration = 0;
		// if (configurationTime < startTime) {
		// currentDuration = (endTime - configurationTime);
		// } else {
		currentDuration = (endTime - startTime);
		// }
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

		durations.set(index, String.valueOf(currentDuration));
		exeTimes.set(index, new Date().toString());

		if (pendingTestCount == 0) {
			currentStatus = "Test execution completed!";
		}

		executedPercent = String.format("%.2f",
				(float) ((totalTestCount - pendingTestCount) * 100.0f)
						/ totalTestCount);
		screenShots.set(index, screenshotFileUrl);
		details.set(
				index,
				getDetailedReport(classNames.get(index),
						methodNames.get(index), statuses.get(index),
						durations.get(index), exeTimes.get(index),
						detailsID.get(index), screenShots.get(index)));

		generateCustomizedIntermediateReport();
	}

	/**
	 * Generates the customized report
	 */
	private void generateCustomizedIntermediateReport() {
		try {
			String fileName = ConfigUtil.getReportsDirectory() + File.separator
					+ "index.html";
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("<html><head><title>Test Execution Report</title>");
			String scriptHtml0 = "<script src=\"http://code.jquery.com/jquery-1.6.3.min.js\"></script>"
					+ "<script src=\"http://code.highcharts.com/highcharts.js\"></script>"
					+ "<script src=\"http://code.highcharts.com/modules/exporting.js\"></script>";
			bw.write(scriptHtml0);

			String scriptHtml1 = "<script type=\"text/javascript\">"
					+ "function toggleVisibility(id, self) {"
					+ "   var e = document.getElementById(id);"
					+ "   if(e.style.display == 'block'){ e.style.display = 'none';}"
					+ "   else{ e.style.display = 'block';}"
					+ "	  "
					+ "   var f = self.innerText;"
					+ "   var g = self.textContent;"
					+ "   var text = '';"
					+ "   if(f != 'undefined'){text=f;}"
					+ "   if(g != 'undefined'){text=g;}"
					+ "   if(text.indexOf('Show Details')!=-1){ self.innerText='Hide Details'; self.textContent='Hide Details'; }"
					+ "   else{ self.innerText='Show Details'; self.textContent='Show Details'; }"
					+ "}"

					+ "function toggleContent(id) {"
					+ "	var contentId = document.getElementById(id);"
					+ "	contentId.style.display == \"block\" ? contentId.style.display = \"none\" : "
					+ "	contentId.style.display = \"block\"; " + "}" +

					"</script>";
			bw.write(scriptHtml1);

			String passedPercent = String.format("%.2f",
					((float) passedTestCount / totalTestCount) * 100);
			String failedPercent = String.format("%.2f",
					((float) failedTestCount / totalTestCount) * 100);
			String skippedPercent = String.format("%.2f",
					((float) skippedTestCount / totalTestCount) * 100);
			String pendingPercent = String.format("%.2f",
					((float) pendingTestCount / totalTestCount) * 100);

			String scriptHtml2 = "<script>"
					+ "function createChart() {"
					+ "	if (!navigator.onLine) {"
					+ "	  alert('Seems you are offline! Please check your internet connectivity!');"
					+ "	  return false;"
					+ "	}"
					+ "	var chartLabel = \"Test Summary - Overview\"; "
					+ "	var seriesName = 'Report Summary';"
					+ "	var lagendNames = ['Passed', 'Failed', 'Skipped', 'Pending'];"
					+ "	var colorValues = ['#50B432', '#ED561B', 'gold', '#A9A9A9'];	"
					+ "	var passedValue = "+passedPercent+";"
					+ "	var failedValue = "+failedPercent+";"
					+ "	var skippedValue = "+skippedPercent+";"
					+ "	var pendingValue = "+pendingPercent+";"
					+ "	var chartWidth = 350;"
					+ "	var chartHeight = 300;		"
					+ "    $('#pieChart').highcharts({"
					+ "        chart: {"
					+ "            plotBackgroundColor: null,"
					+ "            plotBorderWidth: null,"
					+ "			borderWidth: 1,"
					+ "			borderColor: 'lightGrey',"
					+ "            plotShadow: false,"
					+ "			width: chartWidth,"
					+ "			height: chartHeight,"
					+ "			style: { fontFamily: 'Calibri, sans-serif', fontSize: '14px', color: 'grey' }			"
					+ "        },"
					+ "        title: {"
					+ "            text: chartLabel +' (pie)',"
					+ "			style: { fontFamily: 'Calibri, sans-serif', lineHeight: '16px', fontSize: '15px', color: 'purple'}"
					+ "        },"
					+ "        tooltip: {"
					+ "            formatter: function () {"
					+ "				return '<b>' + this.point.name + '</b>: ' + Highcharts.numberFormat(this.percentage, 2) + ' %';"
					+ "			}"
					+ "        },"
					+ "		credits: {"
					+ "		    enabled: false"
					+ "		},		"
					+ "        plotOptions: {"
					+ "            pie: {"
					+ "                allowPointSelect: true,"
					+ "                cursor: 'pointer',"
					+ "                dataLabels: {"
					+ "                    enabled: true,"
					+ "                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',"
					+ "                    style: { color: 'darkBlue'},"
					+ "					connectorColor: 'silver'"
					+ "                }"
					+ "            }"
					+ "        },		"
					+ "		colors: colorValues,"
					+ "        series: [{"
					+ "            type: 'pie',"
					+ "			name: seriesName,"
					+ "            data: [			"
					+ "                ['Passed',passedValue],"
					+ "                ['Failed',failedValue],"
					+ "                ['Skipped',skippedValue],"
					+ "                {"
					+ "                    name: 'Pending',"
					+ "                    y: pendingValue,"
					+ "                    sliced: true,"
					+ "                    selected: true"
					+ "                }"
					+ "            ]"
					+ "        }]"
					+ "    });"
					+ "	$('#columnChart').highcharts({"
					+ "        chart: {"
					+ "            type: 'column',			"
					+ "            plotBorderWidth: 0,"
					+ "			borderWidth: 1,"
					+ "			borderColor: 'lightGrey',"
					+ "			width: chartWidth,"
					+ "			height: chartHeight,"
					+ "			style: { fontFamily: 'Calibri, sans-serif', lineHeight: '16px', fontSize: '12px', color: 'grey'}"
					+ "        },"
					+ "		title: {"
					+ "			text: chartLabel+' (column)',"
					+ "			style: { fontFamily: 'Calibri, sans-serif', lineHeight: '16px', fontSize: '15px', color: 'purple'}"
					+ "		},"
					+ "        xAxis: {"
					+ "            categories: lagendNames,"
					+ "			title: {"
					+ "				text: 'TC Status',"
					+ "				style: { fontFamily: 'Calibri, sans-serif', lineHeight: '16px', fontSize: '12px', color: 'darkBlue' }"
					+ "			},"
					+ "			labels: {                "
					+ "				style: { fontFamily: 'Calibri, sans-serif', lineHeight: '16px', fontSize: '12px', color: 'grey' }"
					+ "            }"
					+ "        },"
					+ "		yAxis: {"
					+ "			title: {"
					+ "				text: 'Values (in %)',"
					+ "				style: { fontFamily: 'Calibri, sans-serif', lineHeight: '16px', fontSize: '12px', color: 'darkBlue' }"
					+ "			}"
					+ "		},"
					+ "		credits: {"
					+ "		    enabled: false"
					+ "		},"
					+ "        plotOptions: {"
					+ "            series: {"
					+ "                allowPointSelect: true,"
					+ "				dataLabels: {"
					+ "					enabled: true,"
					+ "					 format: '{point.y:.1f}%'"
					+ "				}"
					+ "            },"
					+ "			column: {"
					+ "				colorByPoint: true,"
					+ "				events: {"
					+ "					legendItemClick: function () {"
					+ "						return false;"
					+ "					}"
					+ "				},				"
					+ "				allowPointSelect: false"
					+ "			}"
					+ "        },"
					+ "		tooltip: {"
					+ "			formatter: function() {"
					+ "                return '<b>'+this.x +'</b>: '+ Highcharts.numberFormat(this.y, 2) +'%';"
					+ "			}"
					+ "		},"
					+ "		colors: colorValues,"
					+ "		series: [{"
					+ "			type: 'column',"
					+ "			name: seriesName,"
					+ "			showInLegend: false,"
					+ "			allowPointSelect: false,"
					+ "			data: [passedValue, failedValue, skippedValue, pendingValue]"
					+ "		}]" + "    });" + "}" + "</script>";
			bw.write(scriptHtml2);

			String styleHtml = "<style type=\"text/css\">"
					+ "body {"
					+ "    font-family: calibri,verdana,helvetica,arial,sans-serif;"
					+ "    font-size: 11px;"
					+ "    color: rgb(95, 96, 98);"
					+ "}"
					+ ""
					+ "table tr th:first-child, td:first-child {"
					+ "    border-top-left-radius: 0.5em;"
					+ "    border-bottom-left-radius: 0.5em;"
					+ "}"
					+ ""
					+ "table tr th:last-child, td:last-child {"
					+ "    border-top-right-radius: 0.5em;"
					+ "    border-bottom-right-radius: 0.5em;"
					+ "}"
					+ ""
					+ "tr.reportTableRow: {"
					+ "}"
					+ ""
					+ "tr.reportTableRow:hover {"
					+ "    background-color: #eee;"
					+ "    color: maroon;"
					+ "    background-image: linear-gradient(#CCC, #AAA);"
					+ "}"
					+ ""
					+ "#showChart {"
					+ "    border: 1px solid #CCCCCC;"
					+ "    color: #4A4A4A;"
					+ "    background-image: linear-gradient(to bottom, #F7F5F6, #DDDDDD);"
					+ "    -webkit-border-radius: 10px;"
					+ "    -moz-border-radius: 10px;"
					+ "    border-radius: 10px;"
					+ "    line-height: 20px;"
					+ "    cursor: pointer;"
					+ "}"
					+ ""
					+ "#showChart:hover {"
					+ "    border: 1px solid #ADADAD;"
					+ "    background-image: linear-gradient(to bottom, #F7F5F6, #CCCCCC);"
					+ "}" + "</style>";
			bw.write(styleHtml);
			bw.write("</head><body><center>");
			String summaryHtml = "<fieldset style=\"width:550px;padding:15px;border:1px dashed grey;-webkit-border-radius:10px;-moz-border-radius:10px;border-radius:10px;\">"
					+ "<legend><h2>Execution summary of tests</h2></legend>"
					+ "<table style=\"width:500px\"><tbody>"
					+ "<tr><td><b>Status: </b>"
					+ currentStatus
					+ "</td><td style=\"width:200px\"><b>Time: </b>"
					+ DateUtil.milliSecondsTo_HH_MM_SS(totalDuration)
					+ "</td></tr>"
					+ "<tr><td colspan=\"2\" ><b>Progress Info: </b><progress max=\""
					+ totalTestCount
					+ "\" value=\""
					+ (totalTestCount - pendingTestCount)
					+ "\"></progress>&nbsp;"
					+ executedPercent
					+ " %</td></tr>"
					+ "<tr></tr></tbody></table><br>"
					+ "<table cellpadding=\"5\" style=\"width:500px;border:0px dashed maroon;text-align:center;\">"
					+ "<tbody><tr>"
					+ "<td style=\"background-color:green;color:white;\">Passed</td>"
					+ "<td style=\"background-color:#D5B96A;color:white;\">Skipped</td>"
					+ "<td style=\"background-color:#F08080;color:white;\">Failed</td>"
					+ "<td style=\"background-color:#A9A9A9;color:white;\">Pending</td>"
					+ "<td style=\"background-color:#778899;color:white;\">Total</td></tr>"
					+ "<tr><td style=\"background-color:#AAD279;\">"
					+ passedTestCount
					+ "</td>"
					+ "<td style=\"background-color:#FCEDB0;\">"
					+ skippedTestCount
					+ "</td>"
					+ "<td style=\"background-color:#F7C9BA;\">"
					+ failedTestCount
					+ "</td>"
					+ "<td style=\"background-color:#F5F5F5;\">"
					+ pendingTestCount
					+ "</td>"
					+ "<td style=\"background-color:#D3D3D3;\">"
					+ totalTestCount
					+ "</td></tr></tbody></table>"
					+ "<br><button id=\'showChart\' onClick=\"createChart();toggleContent('charts');\">Show/Hide Chart Summary</button>"
					+ "</fieldset><br>";
			bw.write(summaryHtml);

			String highChartHtml = "<div style=\"width:710px;\">"
					+ "	<div id=\"charts\" style=\"display:none;\">"
					+ "	   <div id=\"pieChart\" style=\"float:left;background-color:white;\"></div>"
					+ "	   <div style=\"float:left;\"> </div>"
					+ "	   <div id=\"columnChart\" style=\"float:left;background-color:white;\"></div>	"
					+ "	</div>" + "</div><br><br>";
			bw.write(highChartHtml);

			String reportTableHeader = "<h2>Complete Execution Report</h2>"
					+ "<table cellpadding=\"10\" cellspacing=\"1\" style=\"-webkit-border-radius:10px;-moz-border-radius:10px;border-radius:10px;border: 2px solid #3E4F69;padding:7px; width: 70%;text-align:center;background-color:white;\">"
					+ "<tbody><tr style=\"text-align:center;box-sizing: border-box; font-family: 'Patua One', cursive; font-size: 15px; color: rgb(255, 255, 255); text-shadow: rgba(0, 0, 0, 0.498039) 1px 1px 0px; border-top-width: 1px; border-top-style: solid; border-top-color: rgb(133, 141, 153); border-top-left-radius: 5px; background-image: linear-gradient(rgb(100, 111, 127), rgb(74, 85, 100));\">"
					+ "<th>Test Class Name/ Method Name</th><th>Status</th><th>Duration</th><th>Show/Hide Details</th></tr>";
			bw.write(reportTableHeader);

			for (int i = 0; i < totalTestCount; i++) {
				String time = durations.get(i);
				if (!time.equals(defaultDuration)) {
					time = DateUtil.milliSecondsTo_HH_MM_SS(Long
							.parseLong(time));
				}
				String style = "background-color:#eee;background-image: linear-gradient(#ddd, #eee);";
				if (statuses.get(i).equals("SUCCESS")) {
					style = "background-color:#eee;background-image: linear-gradient(#98C5AB, #AAD279);";
				} else if (statuses.get(i).equals("FAILURE")) {
					style = "background-color:#eee;background-image: linear-gradient(#F7C9BE, #FFE9E8);";
				} else if (statuses.get(i).equals("SKIP")) {
					style = "background-color:#eee;background-image: linear-gradient(#D5B959, #FCEDB0);";
				} else {
					// do nothing(takes declared value of 'style')
				}
				String rowVisibleSection = "<tr class=\"reportTableRow\" style=\""
						+ style
						+ "\">"
						+ "<td style=\"text-align:left;\">"
						+ classNames.get(i)
						+ " / "
						+ methodNames.get(i)
						+ "</td>"
						+ "<td>"
						+ statuses.get(i)
						+ "</td>"
						+ "<td>"
						+ time
						+ "</td>"
						+ "<td onclick=\"toggleVisibility('"
						+ detailsID.get(i)
						+ "',this);\" style=\"cursor:pointer;\">Show Details</td>"
						+ "</tr>";
				String rowHiddenSection = details.get(i);

				bw.write(rowVisibleSection);
				bw.write(rowHiddenSection);
			}

			bw.write("</tbody></table>");
			bw.write("</center></body></html>");
			bw.close();
		} catch (Exception e) {
			log.error("Couldn't show intermediate execution report."
					+ e.getMessage());
		}
	}

	/**
	 * Hidden row for detailed report for the test
	 * 
	 * @param className
	 *            Name of the test class
	 * @param methodName
	 *            Name of the test method
	 * @param status
	 *            Status of the test execution
	 * @param duration
	 *            Time taken to execute the test
	 * @param executionTime
	 *            Start time of test execution
	 * @param detailID
	 *            an unique id
	 * @param screenshotFileUrl
	 *            Location of screenshot captured
	 * @return
	 */
	private String getDetailedReport(String className, String methodName,
			String status, String duration, String executionTime,
			String detailID, String screenshotFileUrl) {

		String rowSectionFirst = "<tr>"
				+ "<td colspan=\"4\" id=\""
				+ detailID
				+ "\" style=\"display:none;\">"
				+ "<table align=\"center\" border=\"0\" cellpadding=\"5\" cellspacing=\"0\" style=\"background-color: white;border:1px solid lightgrey;\">"
				+ "<tr>";
		String rowSectionLeft1 = "<td style=\"vertical-align: top;\"><div style=\"border-bottom: 1px solid #eee;-webkit-border-radius:10px;-moz-border-radius:10px;border-radius:10px;background-image: linear-gradient(#ddd, #eee);padding:5px;\">"
				+ "<div><strong>Class Name: </strong><span>"
				+ className
				+ "</span></div>"
				+ "<div><strong>Method Name: </strong><span>"
				+ methodName
				+ "</span></div>"
				+ "</div>"
				+ "<div style=\"max-width:500px;max-height:550px;overflow:auto;padding:5px;\">";

		String rowSectionLeft2 = "<table border=\"0\" cellpadding=\"5\" cellspacing=\"2\" style=\"border-color:lightGrey;box-shadow: 0 0 5px #CCCCCC;\">"
				+ "<caption style=\"padding: 2px; margin: 2px;\"><strong>Command Execution Log Report</strong></caption>"
				+ "<thead><tr style=\"padding: 2px; margin: 0px; color: white;text-align:center;background-image: linear-gradient(#778899, #AAA);\">"
				+ "<td>Step#</td><td>Actions Performed</td><td>Step Result</td><td>Time</td></tr></thead>"
				+ "<tbody>";

		String[] listSuccess = CommandList.getInstance().getSuccessList();
		String[] listSuccessTimeStamp = CommandList.getInstance()
				.getSuccessTimeStampList();
		String[] listFailure = CommandList.getInstance().getFailureList();
		String[] listFailureTimeStamp = CommandList.getInstance()
				.getFailureTimeStampList();

		for (int i = 0; i < listSuccess.length; i++) {
			rowSectionLeft2 = rowSectionLeft2
					+ "<tr style=\"background-color: #eee;\">"
					+ "<td style=\"text-align:center;\">"
					+ (i + 1)
					+ "</td>"
					+ "<td>"
					+ listSuccess[i]
					+ "</td>"
					+ "<td style=\"text-align:center;\"><font color=\"green\"><b>&#10004;</b></font></td>"
					+ "<td style=\"text-align:center;\">"
					+ listSuccessTimeStamp[i] + "</td></tr>";
		}

		if (!CommandList.getInstance().isEmptyFailureList()) {
			String failureMsg = listFailure[0];
			// you can use '(' if you want additional message
			failureMsg = (failureMsg.split("\\:"))[0];
			failureMsg = failureMsg + "<br> (refer error log for details)";
			rowSectionLeft2 = rowSectionLeft2
					+ "<tr style=\"background-color: #eee;\">"
					+ "<td style=\"text-align:center;\">"
					+ (listSuccess.length + 1)
					+ "</td>"
					+ "<td style=\"color:red;\">"
					+ failureMsg
					+ "</td>"
					+ "<td style=\"text-align:center;\"><font color=\"red\"><b>&#10008;</b></font></td>"
					+ "<td style=\"text-align:center;\">"
					+ listFailureTimeStamp[0] + "</td></tr>";
		}
		rowSectionLeft2 = rowSectionLeft2 + "</tbody></table>";
		String rowSectionLeft3 = "</div></td>";
		String rowSectionRight1 = "<td width=\"400px\" style=\"vertical-align: top;max-height:500px;\">"
				+ "<table cellpadding=\"5\" cellspacing=\"2\" style=\"width:300px;background-color: white;border-color:lightGrey;text-align:center;color: darkblue;\">"
				+ "<tbody>"
				+ "<tr style=\"padding: 2px; color: white; background-color: grey;background-image: linear-gradient(grey, darkGrey);\">"
				+ "<td colspan=\"2\">Environment Details</td></tr>"
				+ "<tr style=\"background-color: #eee;\">	<td>Browser Name</td><td>"
				+ DriverManager.getCurrentBrowserName()
				+ "</td></tr>"
				+ "<tr style=\"background-color: #eee;\">	<td>Browser Version</td><td>"
				+ DriverManager.getCurrentBrowserVersion()
				+ "</td></tr>"
				+ "<tr style=\"background-color: #eee;\">	<td>Operating System</td><td>"
				+ DriverManager.getCurrentOperatingSystem()
				+ "</td></tr>"
				+ "<tr style=\"background-color: #eee;\">	<td>OS Architecture</td><td>"
				+ DriverManager.getCurrentOperatingSystemArchitecture()
				+ "</td></tr>"
				+ "<tr style=\"background-color: #eee;\">	<td>Java Version</td><td>"
				+ DriverManager.getCurrentJavaVersion()
				+ "</td></tr>"
				+ "</tbody></table><br>";
		String rowSectionRight2 = "<table border=\"0\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-collapse:collapse;\">"
				+ "<tbody><tr>"
				+ "<td style=\"padding: 2px; margin: 0px; color: white; background-color: grey;background-image: linear-gradient(grey, darkGrey);text-align:center;\">Screenshot</td>"
				+ "</tr><tr><td rowspan=\"3\"><a href=\""
				+ screenshotFileUrl
				+ "\" target=\"_blank\"><img alt=\"Click on thumbnail to preview it\" src=\""
				+ screenshotFileUrl
				+ "\" style=\"border-width: 1px; border-style: solid; margin: 5px; width: 160px; height: 120px;\" /></a></td>"
				+ "<td><strong>Executed on: </strong><br />"
				+ executionTime
				+ "</td></tr>"
				+ "<tr><td><strong>Test duration: </strong><br /> "
				+ DateUtil.milliSecondsTo_HH_MM_SS(Long.parseLong(duration))
				+ "</td></tr>" + "<tr><td>&nbsp;</td></tr></tbody></table>";
		String[] failureList = CommandList.getInstance().getFailureList();
		String errorMsg = "Not Available.";
		String errorTrace = "Not Available.";
		if (failureList.length > 0) {
			errorMsg = failureList[0];
			if (failureList.length > 1) {
				errorTrace = failureList[1];
			}
			errorLog = "<b> Error Message: </b><br>"
					+ errorMsg
					+ "<br><hr><font color=\"maroon\"><b> Stack Trace: </b><br>"
					+ errorTrace + "</font>";
		}

		String rowSectionRight3 = "<table border=\"0\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-collapse:collapse;\">"
				+ "<tbody><tr><td style=\"padding: 2px; margin: 0px; color: white; background-image: linear-gradient(grey, darkGrey);text-align:center;\">Error Log</td>"
				+ "</tr><tr>";
		if (errorLog.contains("No log found")) {
			rowSectionRight3 = rowSectionRight3
					+ "<td style=\"background-color:rgb(218,244,217);color:#714D03;\">";
		} else {
			rowSectionRight3 = rowSectionRight3
					+ "<td style=\"background-color:#FCFADD;color:#714D03;\">";
		}

		rowSectionRight3 = rowSectionRight3
				+ "<div style=\"max-height:200px;max-width:400px;overflow:auto;padding:5px;\">"
				+ errorLog + "</div>" + "</td></tr></tbody>	</table></td>";
		String rowSectionLast = "</tr></table></td></tr>";

		String leftPanel = "";
		String noCommandLog = "<p style=\"background-color:#FCFADD;color:#714D03;border:1px solid lightGrey;padding:5px;\">"
				+ "No Command Log found. Seems the test execution skipped.</p>";

		if (CommandList.getInstance().isEmptySuccessList()
				&& CommandList.getInstance().isEmptySuccessList()) {
			leftPanel = rowSectionLeft1 + noCommandLog + rowSectionLeft3;
		} else {
			leftPanel = rowSectionLeft1 + rowSectionLeft2 + rowSectionLeft3;
		}

		String rightPanel = rowSectionRight1 + rowSectionRight2
				+ rowSectionRight3;
		String reportTableRow = rowSectionFirst + rightPanel + leftPanel
				+ rowSectionLast;// the panels can be changed as per requirement
		return reportTableRow;
	}

}

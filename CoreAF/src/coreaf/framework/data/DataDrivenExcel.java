package coreaf.framework.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class allows us the use of an Excel spreadsheet to provide input data to
 * a test or set of tests.
 * 
 * Note: This class needs following jars:
 * 
 * <pre>
 * poi-3.7-20101029.jar,<br> poi-ooxml-3.7-20101029.jar,<br> poi-ooxml-schemas-3.7-20101029.jar,<br> xmlbeans-2.3.0.jar<br> and dom4j-1.6.1.jar
 * </pre>
 * 
 * Example usage:
 * 
 * <code><br>
 * DataDrivenExcel obj = new DataDrivenExcel();<br>
 * obj.open("D:/workspace/DataDrivenXSL/data/SampleExcel.xlsx", "Sheet 1");<br>
 * obj.getCell(1, 3);<br>
 * </code>
 * 
 * @author A. K. Sahu
 * 
 */

public class DataDrivenExcel {

	private Workbook wb;
	private Sheet ws;

	/**
	 * Opens a excel sheet
	 * 
	 * @param fileName
	 *            name of the file where you want data
	 * @param sheetName
	 *            name of the sheet in the excel file
	 */
	public DataDrivenExcel(String fileName, String sheetName) {
		try {
			if (fileName.indexOf("xlsx") < 0) {
				wb = new HSSFWorkbook(new FileInputStream(new File(fileName)));
				ws = wb.getSheet(sheetName);
			} else {
				wb = new XSSFWorkbook(fileName);
				ws = (XSSFSheet) wb.getSheet(sheetName);
			}
		} catch (IOException io) {
			throw new Error("Invalid file '" + fileName
					+ "' or incorrect sheet '" + sheetName
					+ "', enter a valid one");
		}
	}

	/**
	 * Gets a cell value from the opened sheet
	 * 
	 * @param rowIndex
	 *            starting with 0 index
	 * @param columnIndex
	 *            starting with 0 index
	 * @return
	 */
	public String getCell(int rowIndex, int columnIndex) {
		String cellValue = null;
		try {
			cellValue = ws.getRow(rowIndex).getCell(columnIndex).toString();
		} catch (Exception e) {
			throw new Error("The cell with row '" + rowIndex + "' and column '"
					+ columnIndex + "' doesn't exist in the sheet");
		}
		return cellValue;
	}
}
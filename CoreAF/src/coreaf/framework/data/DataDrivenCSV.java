package coreaf.framework.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class allows us the use of an Excel spreadsheet to provide input data to
 * a test or set of tests.
 * 
 * Example usage:
 * 
 * <code><br>
 * DataDrivenExcel obj = new DataDrivenExcel();<br>
 * obj.open("D:/workspace/DataDriven/data/SampleCSV.csv", ",");<br>
 * obj.getCell(1, 3);<br>
 * </code>
 * 
 * @author A. K. Sahu
 * 
 */
public class DataDrivenCSV {

	private String fileName = null;
	private String separator = null;

	/**
	 * Opens the CSV file specified by <code>fileName</code> Note: Make sure the
	 * <code>separator</code> is not part of a cell value
	 * 
	 * @param fileName
	 *            a CSV file to read data
	 * @param separator
	 *            a delimiter to separate
	 */
	public DataDrivenCSV(String fileName, String separator) {
		this.fileName = fileName;
		this.separator = separator;
	}

	/**
	 * Gets content of a cell in the CSV file
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	public String getCell(int rowIndex, int columnIndex) {
		int row = -1;
		String line = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {
				row++;
				if (row == rowIndex)
					break;
			}
			String[] data = line.split(separator);
			return data[columnIndex];

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return null;
	}

}

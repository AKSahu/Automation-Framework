package coreaf.framework.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This util class gives different functionalities related to data and time <br>
 * e.g. getting current date, time stamp in different time zones etc.
 * 
 * @author A. K. Sahu
 * 
 */
public class DateUtil {

	/**
	 * Gets a string with current date and time in
	 * <code>dd-MM-yyyy_hh.mm.ss_a</code> format for the system time
	 * 
	 * @return
	 */
	public static String getTimeStamp() {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy_hh.mm.ss_a");
		return df.format(new Date());
	}

	/**
	 * Gets a string with current date and time in
	 * <code>dd-MM-yyyy_hh.mm.ss_a</code> format for the specified
	 * <code>timeZone</code>
	 * 
	 * @param timeZone
	 *            a time zone e.g. PST, IST
	 * @return
	 */
	public static String getTimeStamp(String timeZone) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy_hh.mm.ss_a");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		return df.format(new Date());
	}

	/**
	 * Gets the current system time as hex-decimal string
	 * 
	 * @return
	 */
	public static String getTimeStampLong() {
		return Long.toHexString(System.currentTimeMillis());
	}
	
	/**
	 * Gets only the time form the time stamp 
	 * @return a hh:mm:ss value
	 */
	public static String getTimeStampOnlyTime() {
		return (new SimpleDateFormat("hh:mm:ss")).format(new Date());
	}
	
	/**
     * Takes a time in milliseconds and returns an hours, minutes and
     * seconds representation.  Fractional ms times are rounded down.
     * Leading zeros and all-zero slots are removed.  A table of input
     * and output examples follows.
     *
     * <p>Recall that 1 second = 1000 milliseconds.
     *
     * <table border='1' cellpadding='5'>
     * <tr><td><i>Input ms</i></td><td><i>Output String</i></td></tr>
     * <tr><td>0</td><td><code>:00</code></td></tr>
     * <tr><td>999</td><td><code>:00</code></td></tr>
     * <tr><td>1001</td><td><code>:01</code></td></tr>
     * <tr><td>32,000</td><td><code>:32</code></td></tr>
     * <tr><td>61,000</td><td><code>1:01</code></td></tr>
     * <tr><td>11,523,000</td><td><code>3:12:03</code></td></tr>
     * </table>
     *
     * @param ms Time in milliseconds.
     * @return String-based representation of time in hours, minutes
     * and second format.
     */
	public static String milliSecondsTo_HH_MM_SS(long ms) {
		long totalSecs = ms / 1000;
		long hours = (totalSecs / 3600);
		long mins = (totalSecs / 60) % 60;
		long secs = totalSecs % 60;
		String minsString = (mins == 0) ? "00" : ((mins < 10) ? "0" + mins : ""
				+ mins);
		String secsString = (secs == 0) ? "00" : ((secs < 10) ? "0" + secs : ""
				+ secs);
		if (hours > 0)
			return hours + " hr, " + minsString + " min, " + secsString
					+ " seconds";
		else if (mins > 0)
			return mins + " min, " + secsString + " sec";
		else
			return secsString+ " sec";
	}
}

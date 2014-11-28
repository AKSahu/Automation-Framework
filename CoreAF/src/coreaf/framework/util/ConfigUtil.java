package coreaf.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This util class models the allows us to access all the properties defined in
 * the <code>config/config.properties</code> of the test project. <br>
 * e.g. getting browser property
 * 
 * @author A. K. Sahu
 * 
 */
public class ConfigUtil {

	private static Properties config = null;
	private static String configLoc = null;
	private static Logger log = Logger.getLogger(ConfigUtil.class);

	static {
		config = new Properties();
		try {
			configLoc = new File(".").getCanonicalPath() + File.separator
					+ "config" + File.separator ;
			FileInputStream fis = new FileInputStream(configLoc + "config.properties");

			config.load(fis);
			log.info("Properties file loaded at '" + configLoc + "config.properties"+"'");
		} catch (FileNotFoundException e) {
			log.error("Unable to load properties file: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Unable to load properties file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the location(complete path) of configurations directory
	 * 
	 * @return
	 */
	public static String getConfigurationDirectory() {
		return configLoc;
	}
	
	/**
	 * Gets the value of 'browser' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public static String getBrowser() {
		return config.getProperty("browser");
	}

	/**
	 * Gets the value of 'url' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public static String getBaseUrl() {
		return config.getProperty("url");
	}

	/**
	 * Gets the value of 'coreAFLoc' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public static String getCoreAFLocation() {
		return config.getProperty("coreAFLoc");
	}

	/**
	 * Gets the value of 'screenshotsDirectory' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public static String getScreenshotDirectory() {
		return config.getProperty("screenshotsDirectory");
	}
	

	/**
	 * Gets the value of 'reportsDirectory' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public static String getReportsDirectory() {
		return config.getProperty("reportsDirectory");
	}
	
	/**
	 * Gets the value of 'timeout' property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @return
	 */
	public static String getTimeOut() {
		return config.getProperty("timeout");
	}

	/**
	 * Gets the value of input property <i>key</i> property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return config.getProperty(key);
	}

	/**
	 * Sets the value of input property <i>key</i> property defined in the
	 * <code>"config/config.properties"</code> file
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setProperty(String key, String value) {
		config.setProperty(key, value);
	}
}

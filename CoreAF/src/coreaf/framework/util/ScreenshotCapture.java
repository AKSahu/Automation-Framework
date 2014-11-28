package coreaf.framework.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * This class gives functionality to capture screenshot of the entire screen
 * 
 * @author A. K. Sahu
 * 
 */
public class ScreenshotCapture {

	private static String imgLoc = null;

	private static Logger log = Logger.getLogger(ScreenshotCapture.class);

	static {
		try {
			imgLoc = new File(".").getCanonicalPath() + File.separator
					+ ConfigUtil.getScreenshotDirectory() + File.separator;
			log.info("Screenshot location is set to '" + imgLoc + "'");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the location(complete path) of screenshot directory
	 * 
	 * @return
	 */
	public static String getScreenshotDirectory() {
		return imgLoc;
	}

	/**
	 * Takes a screen shot of the full screen in <code>png</code> format with
	 * the default name '<i>image</i>'
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void takeScreenshot() {

		String timestamp = DateUtil.getTimeStamp();
		File screenshotFile = null;

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenBounds = new Rectangle(0, 0, screenDim.width,
				screenDim.height);

		try {
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenBounds);

			screenshotFile = new File(imgLoc + "image" + timestamp + ".png");
			log.info("Creating screenshot '" + imgLoc + "image" + timestamp
					+ ".png'");

			ImageIO.write(image, "png", screenshotFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a screen shot of the full screen in <code>png</code> format with
	 * the name specified by <code>fileName</code>
	 * 
	 * @param fileName
	 */
	public static void takeScreenshot(String fileName) {

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenBounds = new Rectangle(0, 0, screenDim.width,
				screenDim.height);

		try {
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenBounds);

			File screenshotFile = new File(imgLoc + fileName + ".png");
			log.info("Creating screenshot '" + imgLoc + fileName + ".png'");

			ImageIO.write(image, "png", screenshotFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a screen shot of the full screen in specified
	 * <code>extension</code> format with the name specified by
	 * <code>fileName</code>
	 * 
	 * @param fileName
	 * @param extension
	 */
	public static void takeScreenshot(String fileName, String extension) {

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenBounds = new Rectangle(0, 0, screenDim.width,
				screenDim.height);

		try {

			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenBounds);

			File screenshotFile = new File(imgLoc + fileName + "." + extension);
			log.info("Creating screenshot '" + imgLoc + fileName + extension
					+ "'");

			ImageIO.write(image, extension, screenshotFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

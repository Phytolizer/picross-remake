package common;


import com.sun.javaws.exceptions.InvalidArgumentException;

import java.awt.*;

import static common.Motion.getNumberBetween;

/**
 * Created by mario on 1/12/2017.
 */
public class Background {
	private static Color initialColor = Color.white;
	private static Color destinationColor = Color.white;
	private static Color currentColor = Color.white;
	private static int colorIntervalMS = 10000;
	protected static Timer timer;

	public static Color getCurrentColor() {
		return currentColor;
	}

	public static Color getInitialColor() {
		return initialColor;
	}

	public static Color getDestinationColor() {
		return destinationColor;
	}

	public static int getColorIntervalMS() {
		return colorIntervalMS;
	}

	public static void updateColor() {
		if (timer != null) {
			if (equalColors(currentColor, destinationColor)) {
				do {
					destinationColor = RandomColorGenerator.getOpaqueColor();
				} while (destinationColor.getRed() + destinationColor.getBlue() + destinationColor.getGreen() < 350);
				initialColor = currentColor;
				timer.restart();
			}
			int currMS = (timer.countingUp() ? timer.getMS() : colorIntervalMS - timer.getMS());
			double progress = (double) currMS / (double) colorIntervalMS;
			if (progress > 1d) {
				progress = 1d;
			}
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			try {
				currentR = getNumberBetween(Motion.MODE_LOGISTIC, initialColor.getRed(), destinationColor.getRed(), progress);
				currentG = getNumberBetween(Motion.MODE_LOGISTIC, initialColor.getGreen(), destinationColor.getGreen(), progress);
				currentB = getNumberBetween(Motion.MODE_LOGISTIC, initialColor.getBlue(), destinationColor.getBlue(), progress);
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			}
			currentColor = new Color(currentR, currentG, currentB);
		}
	}

	protected static boolean equalColors(Color c1, Color c2) {
		return c1.getRed() == c2.getRed()
				&& c1.getGreen() == c2.getGreen()
				&& c1.getBlue() == c2.getBlue()
				&& c1.getAlpha() == c2.getAlpha();
	}

	public static void setTimer(Timer t){
		timer = t;
	}
}

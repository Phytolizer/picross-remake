package mygl;

import java.awt.*;

public class Colors {
	/**
	 * Determines the luminance of a color in the sRGB color space.
	 * @param r the amount of red, from 0-255
	 * @param g the amount of green, from 0-255
	 * @param b the amount of blue, from 0-255
	 * @return the calculated brightness of the color represented by these three values, from 0-255
	 */
	public static double getLuminance(int r, int g, int b) {
		return Math.sqrt(0.299 * Math.pow(r, 2) + 0.587 * Math.pow(g, 2) + 0.114 * Math.pow(b, 2));
	}

	public static double getLuminance(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		return Math.sqrt(0.299 * Math.pow(r, 2) + 0.587 * Math.pow(g, 2) + 0.114 * Math.pow(b, 2));
	}
	public static double getLuminance(int rgb) {
		//get lower 8 bits of each shifted rgb value
		int r = rgb >> 16 & 0xFF;
		int g = rgb >> 8 & 0xFF;
		int b = rgb & 0xFF;
		return Math.sqrt(0.299 * Math.pow(r, 2) + 0.587 * Math.pow(g, 2) + 0.114 * Math.pow(b, 2));
	}
}

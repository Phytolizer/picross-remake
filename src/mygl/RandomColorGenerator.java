package mygl;

import java.awt.*;
import java.util.List;

/**
 * @author onContentStop
 */
public class RandomColorGenerator {
	private List<Color> colorList = null;
	private double minBrightness = -1;
	private Color lastColor;

	public RandomColorGenerator(List<Color> colors) {
		if (colors.size() <= 0) {
			throw new IllegalArgumentException("Color list is too small! (size of " + colors.size() + ")");
		}
		colorList = colors;
		lastColor = colors.get(0);
	}

	public RandomColorGenerator(double minBrightness) {
		if (minBrightness < 0 || minBrightness > 255) {
			throw new IllegalArgumentException("Invalid minimum brightness " + minBrightness + "! Must be between 0 and 255");
		}
		this.minBrightness = minBrightness;
		lastColor = Color.WHITE;
	}

	public static Color getOpaqueColor() {
		int r, g, b;
		r = (int) (Math.random() * 256);
		g = (int) (Math.random() * 256);
		b = (int) (Math.random() * 256);
		return new Color(r, g, b);
	}

	public Color getNextColor() {
		if (minBrightness == -1) {
			if (colorList.size() > 1) {
				int lastColorIndex = colorList.indexOf(lastColor);
				int index = (int) (Math.random() * colorList.size() - 1);
				if (index >= lastColorIndex)
					index++;
				Color c = colorList.get(index);
				lastColor = c;
				return c;//Note that a list of size 2 will just return each color, alternating forever.
			} else {
				return colorList.get(0); //there's no randomness here, in fact the color doesn't even change. Just use a static color, you doofus.
			}
		} else if (colorList == null) {
			Color testColor = null;
			double luminance;
			do {
				int r = (int) (Math.random() * 256);
				int g = (int) (Math.random() * 256);
				int b = (int) (Math.random() * 256);
				testColor = new Color(r, g, b);
				luminance = Colors.getLuminance(r, g, b);
			} while (luminance < minBrightness);
			return testColor;
		}
		return null; //should never reach this point because cannot initialize both colorList and minBrightness in the same instance
	}

	public void addColor(Color c) {
		colorList.add(c);
	}

	/**
	 * @param c the color to remove.
	 * @return whether the operation was successful.
	 */
	public boolean removeColor(Color c) {
		return colorList.remove(c);
	}
}

package mygl;

import java.awt.*;
import java.util.List;

/**
 * Created by mario on 1/12/2017.
 */
public class Background {
	private Color initialColor, destinationColor, currentColor;
	private int interval;
	private RandomColorGenerator randomColorGenerator;
	private Timer timer;

	public Background(List<Color> colors, Timer colorTimer, int colorIntervalMS) {
		randomColorGenerator = new RandomColorGenerator(colors);
		initialColor = randomColorGenerator.getNextColor();
		currentColor = new Color(initialColor.getRGB());
		destinationColor = randomColorGenerator.getNextColor();
		timer = colorTimer;
		interval = colorIntervalMS;
	}

	public Background(double minBrightness, Timer colorTimer, int colorIntervalMS) {
		randomColorGenerator = new RandomColorGenerator(minBrightness);
		initialColor = randomColorGenerator.getNextColor();
		currentColor = new Color(initialColor.getRGB());
		destinationColor = randomColorGenerator.getNextColor();
		timer = colorTimer;
		interval = colorIntervalMS;
	}

	private static boolean equalColors(Color c1, Color c2) {
		return c1.getRed() == c2.getRed()
				&& c1.getGreen() == c2.getGreen()
				&& c1.getBlue() == c2.getBlue()
				&& c1.getAlpha() == c2.getAlpha();
	}

	public Color getCurrentColor() {
		return currentColor;
	}

	public Color getInitialColor() {
		return initialColor;
	}

	public Color getDestinationColor() {
		return destinationColor;
	}

	public int getColorIntervalMS() {
		return interval;
	}

	public void update() {
		if (timer != null) {
			if (equalColors(currentColor, destinationColor)) {
				destinationColor = randomColorGenerator.getNextColor();
				initialColor = currentColor;
				timer.restart();
			}
			int ms = (timer.countingUp() ? timer.getMS() : interval - timer.getMS());
			double progress = (double) ms / (double) interval;
			if (progress > 1) {
				progress = 1;
			}
			int r, g, b;
			r = Motion.getNumberBetween(MotionMode.CIRCLE_QUADRANT, initialColor.getRed(), destinationColor.getRed(), progress);
			g = Motion.getNumberBetween(MotionMode.CIRCLE_QUADRANT, initialColor.getGreen(), destinationColor.getGreen(), progress);
			b = Motion.getNumberBetween(MotionMode.CIRCLE_QUADRANT, initialColor.getBlue(), destinationColor.getBlue(), progress);
			currentColor = new Color(r, g, b);
		}
	}

	public void setTimer(Timer t) {
		timer = t;
	}
}

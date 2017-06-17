package mygl;

import java.awt.*;
import java.util.List;

/**
 * Background is a class that should make dynamic colors easier for everyone.<br/>It can
 * be set to generate random colors and shift between them in an arbitrary manner according to one of
 * the functions defined in the {@link Motion Motion} class. Or it can be given a list of colors to switch between, never selecting
 * the same color twice. The rate at which it shifts is defined at initialization.
 */
public class Background {
	private Color initialColor, destinationColor, currentColor;
	private int interval;
	private RandomColorGenerator randomColorGenerator;
	private Timer timer;
	private MotionMode motionMode = MotionMode.CIRCLE_QUADRANT;

	/**
	 * Creates a new Background which will shift between colors in <code>colors</code> randomly and smoothly.
	 * @param colors The colors this Background will choose between. Can contain any amount of Colors, but it is recommended to use at least 3 for random behavior.
	 * @param colorTimer A Timer that will help the Background figure out where it is supposed to be in a given instant.
	 * @param colorIntervalMS The amount of time it should take, in milliseconds, for the Background to switch from one Color to another.
	 */
	public Background(List<Color> colors, Timer colorTimer, int colorIntervalMS) {
		randomColorGenerator = new RandomColorGenerator(colors);
		initialColor = randomColorGenerator.getNextColor();
		currentColor = new Color(initialColor.getRGB());
		destinationColor = randomColorGenerator.getNextColor();
		timer = colorTimer;
		interval = colorIntervalMS;
	}

	/**
	 * Creates a new Background which will shift between <i>random</i> colors smoothly.
	 * @param minBrightness The minimum luminance which the Background will permit to switch to. For all possible colors to be eligible, this should be -1.
	 * @param colorTimer A Timer that will help the Background figure out where it is supposed to be in a given instant.
	 * @param colorIntervalMS The amount of time it should take, in milliseconds, for the Background to switch from one Color to another.
	 */
	public Background(double minBrightness, Timer colorTimer, int colorIntervalMS) {
		randomColorGenerator = new RandomColorGenerator(minBrightness);
		initialColor = randomColorGenerator.getNextColor();
		currentColor = new Color(initialColor.getRGB());
		destinationColor = randomColorGenerator.getNextColor();
		timer = colorTimer;
		interval = colorIntervalMS;
	}

	/**
	 * Returns whether two Colors are identical.
	 * @param c1 A Color
	 * @param c2 A Color to compare <code>c1</code> against
	 * @return whether the color of c1 is equal to that of c2
	 */
	private static boolean equalColors(Color c1, Color c2) {
		return c1.getRed() == c2.getRed()
				&& c1.getGreen() == c2.getGreen()
				&& c1.getBlue() == c2.getBlue()
				&& c1.getAlpha() == c2.getAlpha();
	}

	/**
	 *
	 * @return the color of this Background as of when it was last updated; this will be a mixture of <code>initialColor</code> and <code>destinationColor</code>.
	 */
	public Color getCurrentColor() {
		return currentColor;
	}

	/**
	 *
	 * @return the color that the Background is departing from as it shifts between colors.
	 */
	public Color getInitialColor() {
		return initialColor;
	}

	/**
	 *
	 * @return the color that the Background is approaching as it shifts between colors.
	 */
	public Color getDestinationColor() {
		return destinationColor;
	}

	/**
	 *
	 * @return the amount of time it will take for the Background to go from one color to another
	 */
	public int getColorIntervalMS() {
		return interval;
	}

	/**
	 * Uses the {@link Timer timer} provided to calculate the current color of this Background. Will also determine the next Color in sequence if it has
	 * arrived at {@link Background#destinationColor destinationColor}.
	 */
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
			r = Motion.getNumberBetween(motionMode, initialColor.getRed(), destinationColor.getRed(), progress);
			g = Motion.getNumberBetween(motionMode, initialColor.getGreen(), destinationColor.getGreen(), progress);
			b = Motion.getNumberBetween(motionMode, initialColor.getBlue(), destinationColor.getBlue(), progress);
			currentColor = new Color(r, g, b);
		}
	}

	/**
	 * May be used to change the Timer that the Background relies on for color calculation.
	 * @param t the new Timer to be used
	 */
	public void setTimer(Timer t) {
		timer = t;
	}

	/**
	 * May be used to change how the Background shifts from one color to another. (Default is {@link MotionMode#CIRCLE_QUADRANT circle quadrant})
	 * <br/>
	 * The {@link MotionMode MotionMode} follows a particular graph, with (0, 0) as the starting point and (1, 1) as the end point.
	 * It translates an x-value (time) into a y-value (percentage of the first color to use in the mixture).
	 * @param newMode the new MotionMode to use when the Background is next updated
	 */
	public void setMotionMode(MotionMode newMode) {
		motionMode = newMode;
	}
}

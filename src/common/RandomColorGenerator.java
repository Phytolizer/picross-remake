package common;

import java.awt.*;

/**
 * @author onContentStop
 */
public class RandomColorGenerator {
	public static Color getOpaqueColor() {
		int r, g, b;
		r = (int) (Math.random() * 256);
		g = (int) (Math.random() * 256);
		b = (int) (Math.random() * 256);
		return new Color(r, g, b);
	}
}

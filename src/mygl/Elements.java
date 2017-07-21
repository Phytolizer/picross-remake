package mygl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyle on 6/16/17.
 */
public class Elements {
	private static List<Element> all_elements = new ArrayList<>();
	public static void update() {
		for(Element e : all_elements) {
			e.update();
		}
	}
	public static void add(Element e) {
		all_elements.add(e);
	}
	public static void draw() {
		for(Element e : all_elements) {
			if(e.isVisible()) {
				e.draw();
			}
		}
	}

	/**
	 * Modifies the coordinates and dimensions of all elements in the array provided in order to center them in a way that gives each element equal height and spacing.
	 * Recommended to only use this function between elements of the same type, although it will work for any combination thereof.
	 * <br/>
	 * Please note that this does <b>not</b> modify the x-position of the elements; this only performs vertical spacing and centering.
	 * @param elements The elements to space equally in the available space
	 * @param defaultHeight the ideal height for the elements, they will not exceed this height
	 * @param defaultSpacing the ideal spacing between elements, they will not space farther than this
	 * @param availableSpace the amount of space in which to fit the elements
	 * @param topPixelRow The y-coordinate of the beginning of available space (in the graphics2d)
	 */
	public static void centerAndSpaceElements(Element[] elements, int defaultHeight, int defaultSpacing, int availableSpace, int topPixelRow) {
		int numElements = elements.length;
		int softMinSpace = numElements * defaultHeight + numElements * defaultSpacing;
		boolean hasEnoughSpaceForDefaults = availableSpace >= softMinSpace;
		int newHeight = defaultHeight, newSpacing = defaultSpacing;
		if(hasEnoughSpaceForDefaults) {
			int remainingSpace = availableSpace - softMinSpace;
			//center the buttons in the available space
			int topPad = remainingSpace / 2;
			topPixelRow += topPad;
		} else {
			int spacePerButton = availableSpace / numElements;
			double elementSquishFactor = (double) spacePerButton / (defaultHeight + defaultSpacing);
			newHeight = (int) (elementSquishFactor * defaultHeight);
			newSpacing = (int) (elementSquishFactor * defaultSpacing);
		}
		for(int i = 0; i < numElements; i++) {
			Element e = elements[i];
			e.setHeight(newHeight);
			e.setAlignY(Align.TOP);
			e.setY(topPixelRow + i * (newHeight + newSpacing));
		}
	}
}

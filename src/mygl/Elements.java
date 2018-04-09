package mygl;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by kyle on 6/16/17.
 */
public class Elements {
    private static List<Element> all_elements = new ArrayList<>();

    public static void update() {
        for (int i = 0; i < all_elements.size(); i++) {
            Element e = all_elements.get(i);
            e.update();
        }
    }

    public static void add(Element e) {
        all_elements.add(e);
    }

    public static void draw() {
        for (int i = 0; i < all_elements.size(); i++) {
            Element e = all_elements.get(i);
            if (e.isVisible()) {
                e.draw();
            }
        }
    }

    /**
     * Modifies the coordinates and dimensions of all elements in the array provided in order to center them in a way that gives each element equal height and spacing.
     * Recommended to only use this function between elements of the same type, although it will work for any combination thereof.
     * <br/>
     * Please note that this does <b>not</b> modify the x-position of the elements; this only performs vertical spacing and centering.
     *
     * @param elements       The elements to space equally in the available space
     * @param defaultSize    the ideal size for the elements (height OR width, based on the Axis), they will not exceed this value
     * @param defaultSpacing the ideal spacing between elements, they will not space farther than this
     * @param availableSpace the amount of space in which to fit the elements
     * @param startPixelRow  The coordinate of the beginning of available space (in the graphics2d), i.e the y- or x-position of the first element in the array
     * @param axis           the axis on which to center and space the elements.
     */
    public static void centerAndSpaceElements(Element[] elements, int defaultSize, int defaultSpacing, int availableSpace, int startPixelRow, Axis axis) {
        int numElements = elements.length;
        int softMinSpace = numElements * defaultSize + numElements * defaultSpacing;
        boolean hasEnoughSpaceForDefaults = availableSpace >= softMinSpace;
        int newSize = defaultSize, newSpacing = defaultSpacing;
        if (hasEnoughSpaceForDefaults) {
            int remainingSpace = availableSpace - softMinSpace;
            //center the buttons in the available space
            int pad = remainingSpace / 2;
            startPixelRow += pad;
        } else {
            int spacePerButton = availableSpace / numElements;
            double elementSquishFactor = (double) spacePerButton / (defaultSize + defaultSpacing);
            newSize = (int) (elementSquishFactor * defaultSize);
            newSpacing = (int) (elementSquishFactor * defaultSpacing);
        }
        for (int i = 0; i < numElements; i++) {
            Element e = elements[i];
            if (axis == Axis.VERTICAL) {
                e.setHeight(newSize);
                e.setAlignY(Align.TOP);
                e.setY(startPixelRow + newSpacing / 2 + i * (newSize + newSpacing));
            } else if (axis == Axis.HORIZONTAL) {
                e.setWidth(newSize);
                e.setAlignX(Align.LEFT);
                e.setX(startPixelRow + newSpacing / 2 + i * (newSize + newSpacing));
            }
        }
    }
}

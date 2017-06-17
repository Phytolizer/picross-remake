package mygl;

/**
 * Used as a reference to align graphical objects.
 * Recommended usage:<br/><br/>
 * <code>LEFT</code> and <code>TOP</code> should return <code>x</code> and <code>y</code>, respectively, unchanged.<br/>
 * <code>CENTER_HORIZONTAL</code> should make <code>x</code> refer to the x-coordinate of the center of the object.<br/>
 * <code>CENTER_VERTICAL</code> should make <code>y</code> refer to the y-coordinate of the center of the object.<br/>
 * <code>RIGHT</code> should make <code>x</code> refer to the x-coordinate of the rightmost extreme of the object.<br/>
 * <code>BOTTOM</code> should make <code>y</code> refer to the y-coordinate of the bottommost extreme of the object.
 */
public enum Align {
	//horizontal
	LEFT, CENTER_HORIZONTAL, RIGHT,
	//vertical
	TOP, CENTER_VERTICAL, BOTTOM
}

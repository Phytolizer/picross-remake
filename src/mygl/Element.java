package mygl;


/**
 * Created by mario on 4/11/2017.
 */
public class Element {
	protected int x, y;
	protected int width, height;
	protected Align alignX;
	protected Align alignY;
	protected Graphics graphics;
	protected Updater updater;
	/**
	 * False by default. Flag to tell if the current Element should be drawn on screen.
	 */
	protected boolean isVisible = false;

	public Element(Graphics graphics) {
		this.graphics = graphics;
		alignX = Align.CENTER_HORIZONTAL;
		alignY = Align.CENTER_VERTICAL;
		Elements.add(this);
	}

	//region getters and setters
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public Align getAlignX() {
		return alignX;
	}

	public void setAlignX(Align alignX) {
		this.alignX = alignX;
	}

	public Align getAlignY() {
		return alignY;
	}

	public void setAlignY(Align alignY) {
		this.alignY = alignY;
	}
	//endregion

	public void draw() {

	}

	protected void update() {
		if (updater != null && isVisible)
			updater.update();
	}

	/**
	 * Gives the Element an Updater. <br/>The Updater is called every frame with the Element as a parameter,
	 * and the implementation provided when this setter is used will be run. This may cause performance issues
	 * in large programs.
	 *
	 * @param updateAction the implemented Updater to be called when this Element updates
	 */
	public void setOnUpdateAction(Updater updateAction) {
		updater = updateAction;
	}
}

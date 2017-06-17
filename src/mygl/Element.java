package mygl;

/**
 * Created by mario on 4/11/2017.
 */
public class Element {
	protected int x, y;
	protected int width, height;
	protected Graphics graphics;

	public Element() {

	}

	public Element(Graphics graphics) {
		this.graphics = graphics;
	}

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

	public void draw() {

	}
}

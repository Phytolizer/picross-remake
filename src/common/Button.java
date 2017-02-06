package common;

import java.awt.*;

import static java.awt.Color.*;

/**
 * @author onContentStop
 */
public class Button {
	private boolean clicking, mouseOver;
	private final Color CLEAR = new Color(0, 0, 0, 0);
	private final Color HOVERING = new Color(0, 0, 0, 64);
	private final Color CLICKING = new Color(0, 0, 0, 128);


	private Color color, coverColor;
	private int width, height;
	private String text;
	public Button() {
		color = white;
		coverColor = CLEAR;
		text = "";
		width = 100;
		height = 100;
	}
	public Button(int width, int height) {
		color = white;
		coverColor = CLEAR;
		text = "";
		this.width = width;
		this.height = height;
	}
	public Button(int width, int height, Color color1) {
		color = color1;
		coverColor = CLEAR;
		text = "";
		this.width = width;
		this.height = height;
	}
	public void setHovering(boolean hovering) {
		mouseOver = hovering;
		coverColor = hovering ? HOVERING : CLEAR;
	}
	public void setClicking(boolean clicking1) {
		clicking = clicking1;
		coverColor = clicking1 ? CLICKING : (mouseOver ? HOVERING : CLEAR);
	}
	public void draw(int x, int y, Graphics graphics) {
		Graphics2D art = graphics.getGraphics();
		int mouseX = graphics.getFrame().mouseX;
		int mouseY = graphics.getFrame().mouseY;
		Font f = graphics.getFont().deriveFont(graphics.getFont().getStyle(), getBestFontSize(graphics.getFont(), art));
		art.setFont(f);

		//check bounds
		setHovering(isInBounds(mouseX, mouseY, x - width / 2, y - height / 2));
		setClicking(graphics.getFrame().clicking());

		//actual drawing
		art.setColor(color);
		DrawingTools.fillRectAround(x, y, width, height, art);
		art.setColor(coverColor);
		DrawingTools.fillRectAround(x, y, width, height, art);
		art.setColor(Color.black);
		DrawingTools.drawRectAround(x, y, width, height, art);
		DrawingTools.drawTextAround(f, text, x, y, art);
	}
	private int getBestFontSize(Font f, Graphics2D art) {
		int width = 0;
		double widthProportion = 6d / 7d;
		int i;
		for(i = 1; width < widthProportion * this.width; i++) {
			f = f.deriveFont(f.getStyle(), i);
			FontMetrics fm = art.getFontMetrics(f);
			width = fm.stringWidth(text);
			if (width == 0)
				return 0;
		}
		return i;
	}
	private boolean isInBounds(int x1, int y1, int x2, int y2) {
		return x1 > x2 && y1 > y2 && x1 < (x2 + width) && y1 < (y2 + height);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}

package mygl;

import java.awt.*;

import mygl.Graphics;

import static java.awt.Color.white;

/**
 * @author onContentStop
 */
public class ButtonElement extends Element {
	private final Color CLEAR = new Color(0, 0, 0, 0);
	private final Color HOVERING = new Color(0, 0, 0, 64);
	private final Color CLICKING = new Color(0, 0, 0, 128);
	private boolean clicking, mouseOver;
	private Color color;
	private Color coverColor;
	private Color textColor = Color.black;
	private String text;
	private ButtonListener clickListener;

	public ButtonElement(Graphics graphics) {
		super(graphics);
		color = white;
		coverColor = CLEAR;
		text = "";
		x = 0;
		y = 0;
		width = 100;
		height = 100;
		alignX = Align.CENTER_HORIZONTAL;
		alignY = Align.CENTER_VERTICAL;
	}

	public ButtonElement(int x, int y, int width, int height, Graphics graphics) {
		super(graphics);
		color = white;
		coverColor = CLEAR;
		text = "";
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		alignX = Align.CENTER_HORIZONTAL;
		alignY = Align.CENTER_VERTICAL;
	}

	public ButtonElement(int x, int y, int width, int height, Color backgroundColor, Graphics graphics) {
		super(graphics);
		color = backgroundColor;
		coverColor = CLEAR;
		text = "";
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		alignX = Align.CENTER_HORIZONTAL;
		alignY = Align.CENTER_VERTICAL;
	}

	public void setHovering(boolean hovering) {
		mouseOver = hovering;
		coverColor = hovering ? HOVERING : CLEAR;
	}

	public void setClicking(boolean clicking) {
		this.clicking = clicking;
		coverColor = clicking ? CLICKING : (mouseOver ? HOVERING : CLEAR);
	}

	private int getBestFontSize(Font f, Graphics2D context) {
		int width = 0;
		double fillProportion = 2d / 3d;
		int i;
		for (i = 1; width < fillProportion * this.width; i++) {
			f = f.deriveFont(f.getStyle(), i);
			FontMetrics fm = context.getFontMetrics(f);
			width = fm.stringWidth(text);
			if (width == 0)
				return 0;
		}
		if (f.getSize() > height) {
			return (int) (fillProportion * height);
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

	private void onClick() {
		if (clickListener != null) {
			clickListener.onClick();
		}
	}

	public void setClickListener(ButtonListener buttonListener) {
		clickListener = buttonListener;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void draw() {
		Graphics2D graphics2D = graphics.getGraphics2D();
		int mouseX = graphics.getFrame().mouseX;
		int mouseY = graphics.getFrame().mouseY;
		Font f = graphics.getFont().deriveFont(
				graphics.getFont().getStyle(),
				getBestFontSize(graphics.getFont(),
						graphics2D));
		graphics2D.setFont(f);

		//check bounds
		setHovering(isInBounds(mouseX, mouseY, x - width / 2, y - height / 2));
		if (!graphics.getFrame().clicking() && clicking) {
			onClick();
		}
		setClicking(mouseOver && graphics.getFrame().clicking());

		//actual drawing
		graphics2D.setColor(color);
		DrawingTools.fillRect(x, y, width, height, alignX, alignY, graphics2D);
		graphics2D.setColor(coverColor);
		DrawingTools.fillRect(x, y, width, height, alignX, alignY, graphics2D);
		graphics2D.setColor(textColor);
		DrawingTools.drawRect(x, y, width, height, alignX, alignY, graphics2D);
		int centerX = DrawingTools.getTrueX(x, width, alignX) + width / 2;
		int centerY = DrawingTools.getTrueY(y, height, alignY) + height / 2;
		DrawingTools.drawTextAround(f, text, centerX, centerY, graphics2D);
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
}

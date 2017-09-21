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
	private Point clickStart;
	private boolean lmbDown = false;
	private int fontSize;
	private int maxFontSize = -1;

	public ButtonElement(Graphics graphics) {
		super(graphics);
		color = white;
		coverColor = CLEAR;
		text = "";
		x = 0;
		y = 0;
		width = 100;
		height = 100;
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
	}

	public ButtonElement(int x, int y, int width, int height, Color backgroundColor, Graphics graphics) {
		super(graphics);
		color = backgroundColor;
		if(Colors.getLuminance(color) < 128) {
			setTextColor(Color.white);
		}
		coverColor = CLEAR;
		text = "";
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public void setText(String text) {
		this.text = text;
	}

	public void setColor(Color color) {
		this.color = color;
		if(Colors.getLuminance(color) < 128) {
			setTextColor(Color.white);
		} else {
			setTextColor(Color.black);
		}
	}

	public void onClick() {
		if (clickListener != null) {
			clickListener.onClick();
		}
	}

	public void setClickListener(ButtonListener buttonListener) {
		clickListener = buttonListener;
	}

	public void draw() {
		Graphics2D graphics2D = graphics.getGraphics2D();
		int mouseX = graphics.getFrame().mouseX;
		int mouseY = graphics.getFrame().mouseY;
		fontSize = getBestFontSize(graphics.getFont(), graphics2D);
		if(maxFontSize > -1 && fontSize > maxFontSize) {
			fontSize = maxFontSize;
		}
		Font f = graphics.getFont().deriveFont(
				Font.PLAIN,
				fontSize);
		graphics2D.setFont(f);

		int trueX = getTrueX();
		int trueY = getTrueY();
		//check bounds
		if (!lmbDown && graphics.getFrame().clicking()) {
			clickStart = new Point(mouseX, mouseY);
		}
		lmbDown = graphics.getFrame().clicking();
		setHovering(isInBounds(mouseX, mouseY, trueX, trueY));
		if (!graphics.getFrame().clicking() && clicking) {
			onClick();
		}
		setClicking(clickStart != null && isInBounds(clickStart, trueX, trueY) && mouseOver && graphics.getFrame().clicking());

		//actual drawing
		graphics2D.setColor(color);
		DrawingTools.fillRect(x, y, width, height, alignX, alignY, graphics2D);
		graphics2D.setColor(coverColor);
		DrawingTools.fillRect(x, y, width, height, alignX, alignY, graphics2D);
		graphics2D.setColor(textColor);
		DrawingTools.drawRect(x, y, width, height, alignX, alignY, graphics2D);
		int centerX = trueX + width / 2;
		int centerY = trueY + height / 2;
		DrawingTools.drawTextAround(f, text, centerX, centerY, graphics2D);
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public void setMaxFontSize(int maxFontSize) {
		this.maxFontSize = maxFontSize;
	}

	public int getFontSize() {
		return fontSize;
	}
}

package common;

import java.awt.*;

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
		color = Color.white;
		coverColor = CLEAR;
		text = "";
		width = 100;
		height = 100;
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
		Font f = graphics.getFont().deriveFont(getBestFontSize(graphics.getFont()));
		art.setColor(color);
		DrawingTools.fillRectAround(x, y, width, height, art);
		art.setColor(coverColor);
		DrawingTools.fillRectAround(x, y, width, height, art);
		art.setColor(Color.black);
		DrawingTools.drawRectAround(x, y, width, height, art);
		DrawingTools.drawTextAround(f, text, x, y, art);
	}
	private int getBestFontSize(Font f) {
		int width;
		int i = 1;
		do {
			f = f.deriveFont(i);
			FontMetrics fm = new Frame().getGraphics().getFontMetrics(f);
			width = fm.stringWidth(text);
			if (width == 0)
				return 0;
			i++;
		} while(width < 2 * this.width / 3);
		return width;
	}
}

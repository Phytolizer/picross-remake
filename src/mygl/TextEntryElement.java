package mygl;

import java.awt.*;

import mygl.Graphics;

import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * @author onContentStop
 */
public class TextEntryElement extends Element {
	/**
	 * The cursor will flash once every <code>flashPeriod</code> milliseconds.
	 */
	private final short flashPeriod = 750;
	private String text;
	private int cursorPos;
	private int width, height;
	private int padding;
	private Timer flashTimer;
	private boolean hasFocus;
	private int centerX, centerY;
	private float fontSize;
	private boolean visible;

	public TextEntryElement(int cx, int cy, int len, int ht, Graphics graphics) {//TODO implement Align with initialization of Elements
		super(graphics);
		text = "";
		cursorPos = 0;
		flashTimer = new Timer();
		new Thread(flashTimer).start();
		flashTimer.start();
		height = ht;
		width = len;
		padding = 3;
		hasFocus = false;
		centerX = cx;
		centerY = cy;
		fontSize = height - 2 * padding - 6;
		visible = false;
	}

	public TextEntryElement(int len, int ht, int cx, int cy, String startText, Graphics graphics) {
		super(graphics);
		text = startText;
		cursorPos = startText.length();
		flashTimer = new Timer();
		new Thread(flashTimer).start();
		flashTimer.start();
		height = ht;
		width = len;
		padding = 3;
		hasFocus = false;
		centerX = cx;
		centerY = cy;
		fontSize = height - 2 * padding - 6;
		visible = false;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void type(char c) {
		text = text.substring(0, cursorPos) + c + text.substring(cursorPos);
		cursorPos++;
	}

	public void erase() {
		if (text.length() > 0 && hasFocus) {
			text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
			cursorPos--;
		}
	}

	public void delete() {
		if (text.length() > 0 && hasFocus) {
			text = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
		}
	}

	public void moveCursorTo(int newPos) {
		if (0 <= newPos && newPos <= text.length())
			cursorPos = newPos;
	}

	public void shiftCursor(int shiftAmt) {
		if (0 <= cursorPos + shiftAmt && cursorPos + shiftAmt <= text.length()) {
			cursorPos += shiftAmt;
		}
	}

	private void trimText(FontMetrics fm) {
		for (int i = 1; i < text.length(); i++) {
			if (fm.stringWidth(text.substring(0, i)) > width - 2 * padding) {
				text = text.substring(0, i - 1);
			}
		}
	}

	public void setHasFocus(boolean focus) {
		hasFocus = focus;
	}

	public boolean hasFocus() {
		return hasFocus;
	}

	public boolean isInBounds(int x, int y) {
		return x > centerX - width / 2 && x < centerX + width / 2 && y > centerY - height / 2 && y < centerY + height / 2;
	}

	public void setWidth(int w) {
		width = w;
	}

	public void draw() {
		Graphics2D g2d = graphics.getGraphics2D();
		if (visible) {
			int rectX = centerX - width / 2;
			int rectY = centerY - height / 2;
			g2d.setColor(Color.black);
			g2d.fillRect(rectX, rectY, width, height);
			rectX += padding;
			rectY += padding;
			g2d.setColor(Color.white);
			g2d.fillRect(rectX, rectY, width - 2 * padding, height - 2 * padding);
			Font f = g2d.getFont().deriveFont(fontSize);
			g2d.setFont(f);
			FontMetrics fm = g2d.getFontMetrics();
			trimText(fm);
			if (cursorPos > text.length())
				cursorPos = text.length();
			int cursorX = 0;
			if (cursorPos > 0)
				cursorX = fm.stringWidth(text.substring(0, this.cursorPos)) + 2;
			g2d.setColor(Color.black);
			if (hasFocus && flashTimer.getMS() % flashPeriod < flashPeriod / 2) {
				//draw cursor
				int x = centerX - width / 2 + padding + cursorX;
				g2d.drawLine(x, rectY + height - 2 * padding - 3 - (int) fontSize, x, rectY + height - 2 * padding - 3);
			}
			g2d.drawString(text, rectX + 3, rectY + height - 2 * padding - 6);
		}
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public void setCenterX(int cx) {
		centerX = cx;
	}

	public void handleKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		String modifiers = KeyEvent.getKeyModifiersText(e.getModifiers());

		switch (keyCode) {
			case KeyEvent.VK_BACK_SPACE:
				erase();
				break;
			case KeyEvent.VK_SHIFT:

				break;
			case KeyEvent.VK_LEFT:
				shiftCursor(-1);
				break;
			case KeyEvent.VK_RIGHT:
				shiftCursor(1);
				break;
			case KeyEvent.VK_DELETE:
				delete();
				break;
			default:
				if (modifiers.contains("Shift")) {
					type(Character.toUpperCase(keyChar));
				} else if (Objects.equals(modifiers, "") && keyChar != KeyEvent.CHAR_UNDEFINED) {
					type(keyChar);
				}
		}
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

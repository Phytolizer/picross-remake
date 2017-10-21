package picross;

import mygl.KeyInterface;

import java.awt.event.KeyEvent;

/**
 * Created by mario on 2/2/2017.
 */
public class KeyListener implements KeyInterface {
	private GameWindow graphics;

	private char[] lowerCaseAlphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	private char[] upperCaseAlphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	public KeyListener(GameWindow g) {
		graphics = g;
	}

	@Override
	public void pressKey(KeyEvent e) {
		char keyChar = e.getKeyChar();
		int keyCode = e.getKeyCode();
		System.out.println("You pressed " + keyChar);
	}

	@Override
	public void releaseKey(KeyEvent e) {

	}
}

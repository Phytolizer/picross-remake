package picross;

import mygl.KeyInterface;

import java.awt.event.KeyEvent;

/**
 * Created by mario on 2/2/2017.
 */
public class KeyListener implements KeyInterface {
	private GameWindow graphics;

	public KeyListener(GameWindow g) {
		graphics = g;
	}

	@Override
	public void pressKey(KeyEvent e) {

	}

	@Override
	public void releaseKey(KeyEvent e) {

	}
}

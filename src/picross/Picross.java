package picross;

import mygl.Background;
import mygl.Timer;

/**
 * @author onContentStop
 */
public class Picross {
	public static final String VERSION_NUMBER = "2.0.0";
	public static Timer colorTimer;

	public static void main(String[] args) {
		GameWindow window = new GameWindow(null);
		KeyListener kl = new KeyListener(window);
		window.setKeyListener(kl);
		window.setVisible(true);
		new Thread(window).start();
	}
}

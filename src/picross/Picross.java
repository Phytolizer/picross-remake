package picross;

import common.Background;
import common.Timer;

/**
 * @author onContentStop
 */
public class Picross {
	public static final String VERSION_NUMBER = "2.0.0";
	public static Timer colorTimer;
	public static void main(String[] args) {
		GameWindow window = new GameWindow(null);
		window.setVisible(true);
		new Thread(window).start();
		colorTimer = new Timer();
		Background.setTimer(colorTimer);
		new Thread(colorTimer).start();
	}
}

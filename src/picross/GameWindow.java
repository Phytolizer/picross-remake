package picross;

import common.Background;
import common.DrawingTools;
import common.Graphics;
import common.Button;

import java.awt.*;

/**
 * @author onContentStop
 */
public class GameWindow extends Graphics {
	private final int maxFPS = 144;
	private final int TOP_BAR_HEIGHT = 30;
	private final Button b;

	public GameWindow(KeyListener kl) {
		super("Picross");
		frame.setKeyHandler(kl);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		b = new Button(200, 100);
		b.setText("Start Gayme");
	}
	@Override
	public void run(){
		while(running) {
			updateSize();
			Background.updateColor();
			draw();
			try {
				Thread.sleep((long) (1000d / (double)maxFPS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void draw() {
		startDraw();

		//draw background - this should ALWAYS be first in the draw!
		art.setColor(Background.getCurrentColor());
		art.fillRect(0, 0, width, height);
		setFont(new Font("Arial", Font.BOLD, 50));
		art.setColor(Color.black);
		DrawingTools.drawCenteredText(f, "PICROSS", width / 2, TOP_BAR_HEIGHT + 60, art);
		//region debug mouse position
		/*setFont(new Font("Arial", Font.PLAIN, 20));
		DrawingTools.drawCenteredText(f, "" + frame.mouseX + ", " + frame.mouseY, width / 2, height / 2, art);*/
		//endregion
		b.draw(width / 2, height / 2, this);

		endDraw();
	}
}

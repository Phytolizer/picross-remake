package picross;

import mygl.*;
import mygl.Graphics;

import java.awt.*;

/**
 * @author onContentStop
 */
public class GameWindow extends Graphics {
	private final int maxFPS = 144;
	private final int TOP_BAR_HEIGHT = 30;
	private Background background;
	private ButtonElement b;

	public GameWindow(KeyListener kl) {
		super("Picross");
		frame.setKeyHandler(kl);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		Timer bgTimer = new Timer();
		new Thread(bgTimer).start();
		bgTimer.start();
		background = new Background(100, bgTimer, 10000);
		initButtons();
	}

	private void initButtons() {
		b = new ButtonElement(width / 2, height / 2, 200, 100, this); //TODO add ability to make Button dynamically centered in the window
		b.setText("Start Gayme");
		b.setColor(Color.GREEN);
		b.setClickListener(() -> {
			System.out.println("You clicked the button. You Win!");
		});
	}

	@Override
	public void runActions() {
		updateSize();
		background.update();
		draw();
		try {
			Thread.sleep((long) (1000d / (double) maxFPS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void drawActions() {
		//draw background - this should ALWAYS be first in the draw!
		graphics2D.setColor(background.getCurrentColor());
		graphics2D.fillRect(0, 0, width, height);
		setFont(new Font("Arial", Font.BOLD, 50));
		graphics2D.setColor(Color.black);
		DrawingTools.drawCenteredText(f, "PICROSS", width / 2, TOP_BAR_HEIGHT + 60, graphics2D);
		//region debug mouse position
		/*setFont(new Font("Arial", Font.PLAIN, 20));
		DrawingTools.drawCenteredText(f, "" + frame.mouseX + ", " + frame.mouseY, width / 2, height / 2, art);*/
		//endregion
		b.draw();
	}
}

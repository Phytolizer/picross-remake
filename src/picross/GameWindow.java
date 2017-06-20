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
	private ButtonElement bStartGame;
	private ButtonElement bAnotherOne;

	public GameWindow(KeyListener kl) {
		super("Picross"); //Sets things up. If you want details, look in Graphics, it's too much to explain here.
		frame.setKeyHandler(kl); //TODO a really good solution for key handling that makes everything look pretty
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); //Maximizes the frame on screen.
		Timer bgTimer = new Timer();
		new Thread(bgTimer).start();
		bgTimer.start();
		background = new Background(100, bgTimer, 10000); //This background will choose random colors and shift between them smoothly every 10 seconds.
		initButtons();
	}

	private void initButtons() {
		int menuButtonHeight = 100;
		int menuButtonPad = 25;
		int necessaryTopPad = 120;
		int numMenuButtons = 2;
		//bStartGame is a perfectly centered ButtonElement. No matter what, B will be at the center of the screen.
		bStartGame = new ButtonElement(width / 2, height / 2, 200, menuButtonHeight, this); //Initializes the button with a position, size and graphics context.
		bStartGame.setText("Start Gayme"); //The text to display on the button goes here. The size of this text will be determined automatically by a process unknown to humankind.
		bStartGame.setColor(Color.GREEN); //This is the color that will be used on the background of the button, behind the text and inside the borders.
		bStartGame.setClickListener(() -> {
			System.out.println("You clicked the button. You Win!"); //This code will be executed whenever the button registers a click.
			bStartGame.setVisible(false);                                    //A click occurs when the left mouse button is released on top of the visible button element.
		});
		bStartGame.setAlignY(Align.TOP); //sets the button to be drawn from the top down rather than out from the center, i.e its y-coordinate is that of its top border
		bStartGame.setOnUpdateAction(() -> { //the update action of bStartGame also moves all subsequent buttons in the main menu, to prevent unnecessary recalculation
			int necessaryHeight = necessaryTopPad + numMenuButtons * menuButtonHeight + (numMenuButtons - 1) * menuButtonPad;
			int newMenuButtonHeight, newMenuButtonPad;
			int clearSpace = height - necessaryTopPad - numMenuButtons * menuButtonHeight - (numMenuButtons - 1) * menuButtonPad;
			int buttonSpace = height - necessaryTopPad;
			int spacePerButton = buttonSpace / numMenuButtons;
			double changeFactor = (double) spacePerButton / (menuButtonHeight + menuButtonPad);
			newMenuButtonHeight = (int) (changeFactor * menuButtonHeight);
			newMenuButtonPad = (int) (changeFactor * menuButtonPad);
			bStartGame.setX(width / 2);
			if (height < necessaryHeight) {
				bStartGame.setHeight(newMenuButtonHeight);
				bStartGame.setY(necessaryTopPad);
			} else {
				bStartGame.setY(necessaryTopPad + clearSpace / numMenuButtons);
			}
			bAnotherOne.setX(width / 2);
			if(height < necessaryHeight) {
				bAnotherOne.setHeight(newMenuButtonHeight);
				bAnotherOne.setY(bStartGame.getY() + newMenuButtonHeight + newMenuButtonPad);
			} else {
				bAnotherOne.setY(bStartGame.getY() + menuButtonHeight + menuButtonPad);
			}
		});
		bStartGame.setVisible(true); //Now the button will be drawn and updated on screen!
		bAnotherOne = new ButtonElement(width / 2, height / 2 + menuButtonHeight + menuButtonPad, 200, menuButtonHeight, this);
		bAnotherOne.setText("Leaderboard");
		bAnotherOne.setColor(Color.ORANGE);
		bAnotherOne.setAlignY(Align.TOP);
		bAnotherOne.setVisible(true);
	}

	@Override
	public void runActions() {
		updateSize(); //TODO move this to Graphics, it's such a necessity that I should have done it yesterday.
		background.update(); //This allows the background color to change continuously. (Discretely, but with small enough steps it looks continuous.)
		draw(); //I'm not explaining this. Just no. There is no way the function is not clear. It draws stuff.
		try {
			//This should cause the framerate to max out at maxFPS, though in reality it probably won't reach that value because my code bad.
			Thread.sleep((long) (1000d / (double) maxFPS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void drawActions() {
		//draw background - this should ALWAYS be first in the draw! I mean duh, it goes in the back...
		//if you don't have a background, things will look weird when the window is resized because of how that's done.
		graphics2D.setColor(background.getCurrentColor());
		graphics2D.fillRect(0, 0, width, height);
		setFont(new Font("Arial", Font.BOLD, 50));
		graphics2D.setColor(Color.black);
		DrawingTools.drawCenteredText(f, "PICROSS", width / 2, TOP_BAR_HEIGHT + 60, graphics2D);
		//There are some debug tools here. Use them, or don't. I don't really care.
		//region debug mouse position
		/*setFont(new Font("Arial", Font.PLAIN, 20));
		DrawingTools.drawCenteredText(f, "" + frame.mouseX + ", " + frame.mouseY, width / 2, height / 2, art);*/
		//endregion
		//bStartGame.draw();
		//^ Hey look, you don't have to do this anymore!
	}
}

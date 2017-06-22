package picross;

import mygl.*;
import mygl.Graphics;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.awt.Color.*;

/**
 * @author onContentStop
 */
public class GameWindow extends Graphics {
	private final int maxFPS = 144;
	private final int TOP_BAR_HEIGHT = 30;
	private Background background;
	private ButtonElement bStartGame;
	private ButtonElement bLeaderboard;
	private ButtonElement bCreator;
	private ButtonElement bControls;
	private ButtonElement bQuitGame;

	public GameWindow(KeyListener kl) {
		super("Picross"); //Sets things up. If you want details, look in Graphics, it's too much to explain here.
		frame.setKeyHandler(kl); //TODO a really good solution for key handling that makes everything look pretty
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); //Maximizes the frame on screen.
		Timer bgTimer = new Timer();
		new Thread(bgTimer).start();
		bgTimer.start();
		background = new Background(100, bgTimer, 10000); //This background will choose random colors and shift between them smoothly every 10 seconds.
		initButtons();
		setFrameSleepInterval((short) ((double) 1000 / maxFPS));
	}

	private void initButtons() {
		int menuButtonHeight = 100;
		int menuButtonPad = 25;
		int necessaryTopPad = 120;
		int numMenuButtons = 5;
		//bStartGame is a perfectly centered ButtonElement. No matter what, B will be at the center of the screen.
		bStartGame = new ButtonElement(width / 2, height / 2, 200, menuButtonHeight, this); //Initializes the button with a position, size and graphics context.
		bStartGame.setText("Start Gayme"); //The text to display on the button goes here. The size of this text will be determined automatically by a process unknown to humankind.
		bStartGame.setColor(green); //This is the color that will be used on the background of the button, behind the text and inside the borders.
		bStartGame.setClickListener(() -> {
			System.out.println("You clicked the button. You Win!"); //This code will be executed whenever the button registers a click.
			bStartGame.setVisible(false);                                    //A click occurs when the left mouse button is released on top of the visible button element.
		});
		bStartGame.setAlignY(Align.TOP); //sets the button to be drawn from the top down rather than out from the center, i.e its y-coordinate is that of its top border
		bStartGame.setOnUpdateAction(() -> { //the update action of bStartGame also moves all subsequent buttons in the main menu, to prevent unnecessary recalculation
			int necessaryHeight = necessaryTopPad + numMenuButtons * menuButtonHeight + (numMenuButtons) * menuButtonPad;
			int newMenuButtonHeight = menuButtonHeight, newMenuButtonPad = menuButtonPad;
			int clearSpace = height - necessaryTopPad - numMenuButtons * menuButtonHeight - (numMenuButtons) * menuButtonPad;
			int buttonSpace = height - necessaryTopPad;
			int spacePerButton = buttonSpace / numMenuButtons;
			double changeFactor = (double) spacePerButton / (menuButtonHeight + menuButtonPad);
			bStartGame.setX(width / 2);
			if (height < necessaryHeight) {
				newMenuButtonHeight = (int) (changeFactor * menuButtonHeight);
				newMenuButtonPad = (int) (changeFactor * menuButtonPad);
				bStartGame.setY(necessaryTopPad);
			} else {
				bStartGame.setY(necessaryTopPad + clearSpace / 2);
			}
			bStartGame.setHeight(newMenuButtonHeight);

			bLeaderboard.setX(width / 2);
			bLeaderboard.setHeight(newMenuButtonHeight);
			bLeaderboard.setY(bStartGame.getY() + newMenuButtonHeight + newMenuButtonPad);

			bCreator.setX(width / 2);
			bCreator.setHeight(newMenuButtonHeight);
			bCreator.setY(bStartGame.getY() + 2 * (newMenuButtonHeight + newMenuButtonPad));

			bControls.setX(width / 2);
			bControls.setHeight(newMenuButtonHeight);
			bControls.setY(bStartGame.getY() + 3 * (newMenuButtonHeight + newMenuButtonPad));

			bQuitGame.setX(width / 2);
			bQuitGame.setHeight(newMenuButtonHeight);
			bQuitGame.setY(bStartGame.getY() + 4 * (newMenuButtonHeight + newMenuButtonPad));
		});/*Note that when bStartGame is not visible, *none* of the menu buttons will be moved because their recalculation depends on bStartGame being updated.
			This should not be a problem as long as bStartGame is visible when the other buttons should be.
			If this is a problem, pls fix.
		*/
		bStartGame.setVisible(true); //Now the button will be drawn and updated on screen!

		bLeaderboard = new ButtonElement(width / 2, height / 2 + menuButtonHeight + menuButtonPad, 200, menuButtonHeight, this);
		bLeaderboard.setText("Leaderboard");
		bLeaderboard.setColor(orange);
		bLeaderboard.setAlignY(Align.TOP);
		bLeaderboard.setClickListener(() -> {
			displayStatusNoBG("Opening in browser...");
			try {
				Desktop.getDesktop().browse(new URL("https://westonreed.com/picross/leaderboard.php").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		bLeaderboard.setVisible(true);

		bCreator = new ButtonElement(0, 0, 200, menuButtonHeight, this);//init x and y don't matter because they are updated constantly anyway
		bCreator.setText("Puzzle Creator");
		bCreator.setColor(yellow);
		bCreator.setAlignY(Align.TOP);
		bCreator.setVisible(true);

		bControls = new ButtonElement(0, 0, 200, menuButtonHeight, this);
		bControls.setText("Controls Menu");
		bControls.setColor(blue);
		bControls.setAlignY(Align.TOP);
		bControls.setVisible(true);

		bQuitGame = new ButtonElement(0, 0, 200, menuButtonHeight, this);
		bQuitGame.setText("Quit Game");
		bQuitGame.setColor(red);
		bQuitGame.setAlignY(Align.TOP);
		bQuitGame.setClickListener(() -> {
			System.out.println("Quitting normally...");
			windowClosing(null);
			windowClosed(null);
		});
		bQuitGame.setVisible(true);
	}

	@Override
	public void runActions() {
		background.update(); //This allows the background color to change continuously. (Discretely, but with small enough steps it looks continuous.)
		draw(); //I'm not explaining this. Just no. There is no way the function is not clear. It draws stuff.
	}

	protected void drawActions() {
		//draw background - this should ALWAYS be first in the draw! I mean duh, it goes in the back...
		//if you don't have a background, things will look weird when the window is resized because of how that's done.
		graphics2D.setColor(background.getCurrentColor());
		graphics2D.fillRect(0, 0, width, height);
		setFont(new Font("Arial", Font.BOLD, 50));
		graphics2D.setColor(black);
		DrawingTools.drawCenteredText(f, "PICROSS", width / 2, TOP_BAR_HEIGHT + 60, graphics2D);
		//There are some debug tools here. Use them, or don't. I don't really care.
		//region debug mouse position
		/*setFont(new Font("Arial", Font.PLAIN, 20));
		DrawingTools.drawCenteredText(f, "" + frame.mouseX + ", " + frame.mouseY, width / 2, height / 2, art);*/
		//endregion
		//bStartGame.draw();
		//^ Hey look, you don't have to do this anymore!
	}

	private void displayStatusNoBG(String message) {
		setFont(new Font("Arial", Font.BOLD, 50));
		graphics2D.setColor(black);
		DrawingTools.drawCenteredText(f, message, width / 2, height / 2, graphics2D);
		setFont(f.deriveFont(Font.PLAIN));
	}
}

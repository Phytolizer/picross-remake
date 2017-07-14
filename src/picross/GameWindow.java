package picross;

import mygl.*;
import mygl.Graphics;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import static java.awt.Color.*;

/**
 * @author onContentStop
 */
public class GameWindow extends Graphics {
	//Please alphabetize both types *and* fields within a certain type.
	//region final variables
	private final int MAX_FPS = 144;
	private final String VERSION = "2.0.0";
	//endregion
	//region regular fields
	/**
	 * The background used throughout the game. Uses the {@link Background} class to change colors.
	 */
	private Background background;
	/**
	 * Calls {@link GameWindow#popWindow()} when clicked. Only visible in certain windows.
	 */
	private ButtonElement bBack;
	/**
	 * Visible on the main menu, will launch the puzzle creator when clicked.
	 */
	private ButtonElement bCreator;
	/**
	 * Visible on the main menu, will call {@link GameWindow#pushWindow(Window) pushWindow} with {@link Window#CONTROLS} when clicked.
	 */
	private ButtonElement bControls;
	/**
	 * Visible on the main menu, will open the browser to <a href="https://westonreed.com/picross/leaderboard.php">https://westonreed.com/picross/leaderboard.php</a> when clicked.
	 */
	private ButtonElement bLeaderboard;
	/**
	 * Visible in the gamemode selection screen, will call {@link GameWindow#pushWindow(Window) pushWindow} with {@link Window#LOAD} when clicked.
	 */
	private ButtonElement bLoadPuzzle;
	/**
	 * Visible on the main menu, will quit the game when clicked.
	 */
	private ButtonElement bQuitGame;
	/**
	 * Visible in the gamemode selection screen, will call {@link GameWindow#pushWindow(Window) pushWindow} with {@link Window#SIZE} when clicked.
	 */
	private ButtonElement bRandomPuzzle;
	/**
	 * Visible in the main menu, will call {@link GameWindow#pushWindow(Window) pushWindow} with {@link Window#GAMEMODE} when clicked.
	 * <br/>
	 * Also responsible for calculating the position of all other buttons in the main menu.
	 */
	private ButtonElement bStartGame;
	/**
	 * Contains all Elements that should be displayed on screen with certain Windows. This is used to
	 * manage which Elements are visible when a particular Window is on the top of the windowStack. All Elements
	 * should be added to this list with a specific Window, and can be added to multiple <code>Window</code>s.
	 */
	private ElementList elements_by_window;
	/**
	 * Contains the current window stack that has been opened so far. Several buttons push to this stack to switch to another window,
	 *  but bBack will return to the previous window in the stack.
	 */
	private Stack<Window> windowStack;
	/**
	 * Stores the value of the currently visible window.
	 */
	private Window currWindow;
	//endregion

	/**
	 * Initializes the game's main window.
	 * @param kl A key listener to use for key handling
	 */
	public GameWindow(KeyListener kl) {
		//Sets things up through its parent class
		super("Picross");
		//An ugly but working method for key handling. Ew.
		frame.setKeyHandler(kl); //TODO a really good solution for key handling that makes everything look pretty
		//Maximizes the frame on screen.
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		//Times the color changing on the background.
		Timer bgTimer = new Timer();
		//This thread doesn't need to be monitored or stopped until the program is quit.
		new Thread(bgTimer).start();
		//Starts the timer. Duh.
		bgTimer.start();
		//This background will choose random colors and shift between them smoothly every 10 seconds.
		background = new Background(100, bgTimer, 10000);
		//Initialize the element organizer.
		elements_by_window = new ElementList();
		//Initialize all ButtonElements that appear in the game. Period.
		initButtons();
		//Sets the time to pause between frames. This will never actually reach MAX_FPS, but we can dream.
		setFrameSleepInterval((short) ((double) 1000 / MAX_FPS));
		//This tells elements_by_window to show the Elements of the MAIN window, while also calling Window.MAIN's draw function (see: drawActions)
		pushWindow(Window.MAIN);
	}

	private void initButtons() {
		int menuButtonHeight = 100;
		int menuButtonPad = 25;
		int necessaryTopPad = 120;
		int numMenuButtons = 5;
		//bStartGame is a perfectly centered ButtonElement. No matter what, B will be at the center of the screen.
		bStartGame = new ButtonElement(width / 2, height / 2, 200, menuButtonHeight, this); //Initializes the button with a position, size and graphics context.
		bStartGame.setText("Start Game"); //The text to display on the button goes here. The size of this text will be determined automatically by a process unknown to humankind.
		bStartGame.setColor(green); //This is the color that will be used on the background of the button, behind the text and inside the borders.
		bStartGame.setClickListener(() -> {
			pushWindow(Window.GAMEMODE);
		});
		bStartGame.setAlignY(Align.TOP); //sets the button to be drawn from the top down rather than out from the center, i.e its y-coordinate is that of its top border
		bStartGame.setOnUpdateAction(() -> { //the update action of bStartGame also moves all subsequent buttons in the main menu, to prevent unnecessary recalculation
			//The minimum height required for all buttons to fit at their natural size.
			int necessaryHeight = necessaryTopPad + numMenuButtons * menuButtonHeight + (numMenuButtons) * menuButtonPad;
			//The altered button height for if height < necessaryHeight
			int newMenuButtonHeight = menuButtonHeight;
			//Altered space between buttons if height < necessaryHeight
			int newMenuButtonPad = menuButtonPad;
			//The space left over when buttons take up their maximum space, not including the space for the title
			int clearSpace = height - necessaryTopPad - numMenuButtons * menuButtonHeight - (numMenuButtons) * menuButtonPad;
			//The amount of space available for the buttons to fill
			int buttonSpace = height - necessaryTopPad;
			//The amount of space each button has
			int spacePerButton = buttonSpace / numMenuButtons;
			//The factor by which each button's height and pad should be altered to fit in the available space
			double changeFactor = (double) spacePerButton / (menuButtonHeight + menuButtonPad);
			bStartGame.setX(width / 2);
			if (height < necessaryHeight) {
				//Changes the buttons' height to fit in available space, and also changes the amount of space between them in proportion.
				newMenuButtonHeight = (int) (changeFactor * menuButtonHeight);
				newMenuButtonPad = (int) (changeFactor * menuButtonPad);
				//bStartGame is on top, so it is put as high as it is allowed.
				bStartGame.setY(necessaryTopPad);
			} else {
				//bStartGame is on top, so it is placed in a way that will center the buttons nicely on screen.
				//clearSpace is the amount of space left over beyond the buttons' total height and the space the title takes up.
				bStartGame.setY(necessaryTopPad + clearSpace / 2);
			}
			bStartGame.setHeight(newMenuButtonHeight);

			//Now, each other menu button in turn is resized to newMenuButtonHeight and moved to their respective positions.
			bLeaderboard.setX(width / 2);
			bLeaderboard.setHeight(newMenuButtonHeight);
			bLeaderboard.setY(bStartGame.getY() + newMenuButtonHeight + newMenuButtonPad);

			bCreator.setX(width / 2);
			bCreator.setHeight(newMenuButtonHeight);
			bCreator.setY(bStartGame.getY() + 2 * (newMenuButtonHeight + newMenuButtonPad));

			bControls.setX(width / 2);
			bControls.setHeight(newMenuButtonHeight);
			//The number 3 here refers to the number of buttons above bControls. Same applies for other buttons in this menu.
			bControls.setY(bStartGame.getY() + 3 * (newMenuButtonHeight + newMenuButtonPad));

			bQuitGame.setX(width / 2);
			bQuitGame.setHeight(newMenuButtonHeight);
			bQuitGame.setY(bStartGame.getY() + 4 * (newMenuButtonHeight + newMenuButtonPad));
		});/*Note that when bStartGame is not visible, *none* of the menu buttons will be moved because their recalculation depends on bStartGame being updated.
			This should not be a problem as long as bStartGame is visible when the other buttons should be.
			If this is a problem, pls fix.
		*/
		//Whenever the Window on top of the windowStack is Window.MAIN, bStartGame (and the other menu buttons) will be forcibly made visible.
		//If the Window is *anything* else, they are forced to be invisible (when a ButtonElement is invisible, it does not update and therefore takes up
		//minimal processing power.
		elements_by_window.add(Window.MAIN, bStartGame);
		//bStartGame.setVisible(true);
		//The above line is not necessary because bStartGame is made visible when pushWindow is called during the init sequence.

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
		elements_by_window.add(Window.MAIN, bLeaderboard);

		bCreator = new ButtonElement(0, 0, 200, menuButtonHeight, this);//initial x and y don't matter because they are updated each frame anyway
		bCreator.setText("Creator");
		bCreator.setColor(yellow);
		bCreator.setAlignY(Align.TOP);
		elements_by_window.add(Window.MAIN, bCreator);
		bCreator.setClickListener(() -> {
			//TODO launch the puzzle creator
		});

		bControls = new ButtonElement(0, 0, 200, menuButtonHeight, this);
		bControls.setText("Controls");
		bControls.setColor(blue);
		bControls.setAlignY(Align.TOP);
		bControls.setClickListener(() -> {
			pushWindow(Window.CONTROLS);
		});
		elements_by_window.add(Window.MAIN, bControls);

		bQuitGame = new ButtonElement(0, 0, 200, menuButtonHeight, this);
		bQuitGame.setText("Quit Game");
		bQuitGame.setColor(red);
		bQuitGame.setAlignY(Align.TOP);
		bQuitGame.setClickListener(() -> {
			System.out.println("Quitting normally...");
			quitGame();
		});
		elements_by_window.add(Window.MAIN, bQuitGame);

		bBack = new ButtonElement(20, 20 + WINDOW_BAR_HEIGHT, 50, 50, this);
		bBack.setText("Back");
		bBack.setColor(red);
		bBack.setAlignY(Align.TOP);
		bBack.setAlignX(Align.LEFT);
		bBack.setClickListener(this::popWindow);
		elements_by_window.add(Window.SIZE, bBack);
		elements_by_window.add(Window.CONTROLS, bBack);
		elements_by_window.add(Window.GAMEMODE, bBack);

		bRandomPuzzle = new ButtonElement(0, 120, 200, 100, this);
		bRandomPuzzle.setText("Random Mode");
		bRandomPuzzle.setColor(yellow);
		bRandomPuzzle.setAlignY(Align.TOP);
		//bRandomPuzzle will move itself and bLoadPuzzle simultaneously. Even though bLoadPuzzle is not initialized at this
		//point, it will be by the time any Element updates, so it is safe to reference it here.
		bRandomPuzzle.setOnUpdateAction(() -> {
			bRandomPuzzle.setX(width / 2);
		});
		bRandomPuzzle.setClickListener(() -> {
			pushWindow(Window.SIZE);
		});
		elements_by_window.add(Window.GAMEMODE, bRandomPuzzle);

		bLoadPuzzle = new ButtonElement(0, 250, 200, 100, this);

	}

	@Override
	public void runActions() {
		background.update(); //This allows the background color to change continuously. (Discretely, but with small enough steps it looks continuous.)
		draw();
	}

	protected void drawActions() {
		//draw background - this should ALWAYS be first in the draw! I mean duh, it goes in the back...
		//if you don't have a background, things will look weird when the window is resized because the resizing algorithm isn't perfect.
		graphics2D.setColor(background.getCurrentColor());
		graphics2D.fillRect(0, 0, width, height);
		currWindow.draw(this);

		//debug tools
		//region debug mouse position
		/*setFont(new Font("Arial", Font.PLAIN, 20));
		DrawingTools.drawCenteredText(f, "" + frame.mouseX + ", " + frame.mouseY, width / 2, height / 2, art);*/
		//endregion

		//bStartGame.draw();
		//^ This is now covered by the Graphics class, which calls Elements.draw().
	}

	private void displayStatusNoBG(String message) {
		setFont(new Font("Arial", Font.BOLD, 50));
		graphics2D.setColor(black);
		DrawingTools.drawCenteredText(f, message, width / 2, height / 2, graphics2D);
		setFont(f.deriveFont(Font.PLAIN));
	}

	private void pushWindow(Window window) {
		if(windowStack == null) {
			windowStack = new Stack<>();
		}
		if (windowStack.contains(window)) {
			throw new IllegalStateException("Tried to push a window to the stack that already was in the stack!");
		}
		windowStack.push(window);
		currWindow = window;
		elements_by_window.setWindow(window);
	}

	private Window popWindow() {
		if(windowStack.size() == 1) {
			System.out.println("Quitting because the window stack was emptied.");
			quitGame();
		}
		if(windowStack.empty()) {
			throw new IllegalStateException("Tried to pop a window from an empty stack! Please notify the developer if you get this message.");
		}
		Window oldWindow = windowStack.pop();
		currWindow = windowStack.peek();
		elements_by_window.setWindow(currWindow);
		return oldWindow;
	}

	private void quitGame() {
		windowClosing(null);
		windowClosed(null);
	}
}

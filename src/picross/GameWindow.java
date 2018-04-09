package picross;

import bgusolver.JCIndependantSolver;
import mygl.*;
import mygl.Graphics;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static java.awt.Color.*;

/**
 * @author onContentStop
 */
public class GameWindow extends Graphics {
    //Please alphabetize both types *and* fields within a certain type.
    //region final variables
    private final int MAX_FPS = 144;
    protected final int MAX_PUZZLE_SIZE = 25;
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
    private ButtonElement bDecSizeX;
    private ButtonElement bDecSizeY;
    private ButtonElement bExit;
    private ButtonElement bGeneratePuzzle;
    private ButtonElement bIncSizeX;
    private ButtonElement bIncSizeY;
    /**
     * Visible on the main menu, will open the browser to <a href="https://westonreed.com/picross/leaderboard.php">https://westonreed.com/picross/leaderboard.php</a> when clicked.
     */
    private ButtonElement bLeaderboard;
    /**
     * Visible in the gamemode selection screen, will call {@link GameWindow#pushWindow(Window) pushWindow} with {@link Window#LOAD} when clicked.
     */
    private ButtonElement bLoadPuzzle;
    private ButtonElement bPause;
    /**
     * Visible on the main menu, will quit the game when clicked.
     */
    private ButtonElement bQuitGame;
    /**
     * Visible in the gamemode selection screen, will call {@link GameWindow#pushWindow(Window) pushWindow} with {@link Window#SIZE} when clicked.
     */
    private ButtonElement bRandomPuzzle;
    private ButtonElement bResume;
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
    private GridElement puzzleGrid;
    private int puzzleSizeX;
    private int puzzleSizeY;
    private KeyListener keyListener;
    private Map<String, String> prefs;
    private MistakeCounterElement mistakeCounter;
    private ScrollAreaElement puzzleSizeXScrollZone;
    private ScrollAreaElement puzzleSizeYScrollZone;
    /**
     * Contains the current window stack that has been opened so far. Several buttons push to this stack to switch to another window,
     * but bBack will return to the previous window in the stack.
     */
    private Stack<Window> windowStack;
    private TextElement theLetterX;
    private TextElement timerDisplay;
    private TextElement xSize;
    private TextElement ySize;
    private Thread timerThread;
    private Timer gameTimer;
    /**
     * Stores the value of the currently visible window.
     */
    private Window currWindow;
    //endregion

    /**
     * Initializes the game's main window.
     *
     * @param kl A key listener to use for key handling
     */
    public GameWindow(KeyListener kl) {
        //Sets things up through its parent class
        super("Picross");
        loadFromPrefs();
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
        background = new Background(128, bgTimer, 10000);
        //Initialize the element organizer.
        elements_by_window = new ElementList();
        //Initialize all ButtonElements that appear in the game. Period.
        initButtons();
        //Sets the time to pause between frames. This will never actually reach MAX_FPS, but we can dream.
        setFrameSleepInterval((short) ((double) 1000 / MAX_FPS));
        //This tells elements_by_window to show the Elements of the MAIN window, while also calling Window.MAIN's draw function (see: drawActions)
        pushWindow(Window.MAIN);
    }

    private void loadFromPrefs() {
        prefs = Prefs.read();
        for (Object o : prefs.entrySet()) {
            HashMap.Entry entry = (HashMap.Entry) o;
            if (entry.getKey().equals("puzzle_size_x")) {
                int sx = Integer.parseInt((String) entry.getValue());
                if (sx > 0 && sx <= MAX_PUZZLE_SIZE) {
                    puzzleSizeX = sx;
                }
            } else if (entry.getKey().equals("puzzle_size_y")) {
                int sy = Integer.parseInt((String) entry.getValue());
                if (sy > 0 && sy < MAX_PUZZLE_SIZE) {
                    puzzleSizeY = sy;
                }
            }
        }
    }

    private void initButtons() {
        int menuButtonHeight = 100;
        int menuButtonPad = 25;
        int necessaryTopPad = WINDOW_BAR_HEIGHT + 60;
        //bStartGame is a perfectly centered ButtonElement. No matter what, B will be at the center of the screen.
        bStartGame = new ButtonElement(width / 2, height / 2, 200, menuButtonHeight, this); //Initializes the button with a position, size and graphics context.
        bStartGame.setText("Start Game"); //The text to display on the button goes here. The size of this text will be determined automatically by a process unknown to humankind.
        bStartGame.setColor(green); //This is the color that will be used on the background of the button, behind the text and inside the borders.
        bStartGame.setClickListener(() -> {
            pushWindow(Window.GAMEMODE);
        });
        bStartGame.setOnUpdateAction(() -> {
            ButtonElement[] menuButtons = {bStartGame, bLeaderboard, bCreator, bControls, bQuitGame};
            for (ButtonElement be : menuButtons) {
                be.setX(width / 2);
            }
            int availableSpace = height - necessaryTopPad;
            Elements.centerAndSpaceElements(menuButtons, menuButtonHeight, menuButtonPad, availableSpace, necessaryTopPad, Axis.VERTICAL);
            if (bLeaderboard != null && bLeaderboard.getFontSize() > 0) {
                int fs = bLeaderboard.getFontSize();
                bStartGame.setMaxFontSize(fs);
                bCreator.setMaxFontSize(fs);
                bControls.setMaxFontSize(fs);
                bQuitGame.setMaxFontSize(fs);
            }
        });/*Note that when bStartGame is not visible, *none* of the menu buttons will be moved because their recalculation depends on bStartGame being updated.
			This should not be a problem as long as bStartGame is visible when the other buttons should be.
			If this is a problem, pls fix.
		*/
        //Whenever the Window on top of the windowStack is Window.MAIN, bStartGame (and the other menu buttons) will be forcibly made visible.
        //If the Window is *anything* else, they are forced to be invisible (when a ButtonElement is invisible, it does not update and therefore takes up
        //minimal processing power.
        elements_by_window.add(bStartGame, Window.MAIN);
        //bStartGame.setVisible(true);
        //The above line is not necessary because bStartGame is made visible when pushWindow is called during the init sequence.

        bLeaderboard = new ButtonElement(width / 2, height / 2 + menuButtonHeight + menuButtonPad, 200, menuButtonHeight, this);
        bLeaderboard.setText("Leaderboard");
        bLeaderboard.setColor(orange);
        bLeaderboard.setClickListener(() -> {
            displayStatusNoBG("Opening in browser...");
            try {
                Desktop.getDesktop().browse(new URL("https://westonreed.com/picross/leaderboard.php").toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        elements_by_window.add(bLeaderboard, Window.MAIN);

        bCreator = new ButtonElement(0, 0, 200, menuButtonHeight, this);//initial x and y don't matter because they are updated each frame anyway
        bCreator.setText("Creator");
        bCreator.setColor(yellow);
        elements_by_window.add(bCreator, Window.MAIN);
        bCreator.setClickListener(() -> {
            //TODO launch the puzzle creator
        });

        bControls = new ButtonElement(0, 0, 200, menuButtonHeight, this);
        bControls.setText("Controls");
        bControls.setColor(new Color(0x007FFF));
        if (bLeaderboard.getFontSize() > 0) {
            bControls.setMaxFontSize(bLeaderboard.getFontSize());
        }
        bControls.setClickListener(() -> {
            pushWindow(Window.CONTROLS);
        });
        elements_by_window.add(bControls, Window.MAIN);

        bQuitGame = new ButtonElement(0, 0, 200, menuButtonHeight, this);
        bQuitGame.setText("Quit Game");
        bQuitGame.setColor(red);
        if (bLeaderboard.getFontSize() > 0) {
            bQuitGame.setMaxFontSize(bLeaderboard.getFontSize());
        }
        bQuitGame.setClickListener(() -> {
            System.out.println("Quitting normally...");
            quitGame();
        });
        elements_by_window.add(bQuitGame, Window.MAIN);

        bBack = new ButtonElement(20, 20 + WINDOW_BAR_HEIGHT, 50, 50, this);
        bBack.setText("Back");
        bBack.setColor(red);
        bBack.setAlignY(Align.TOP);
        bBack.setAlignX(Align.LEFT);
        bBack.setClickListener(this::popWindow);
        elements_by_window.add(bBack, Window.SIZE);
        elements_by_window.add(bBack, Window.CONTROLS);
        elements_by_window.add(bBack, Window.GAMEMODE);
        elements_by_window.add(bBack, Window.LOAD);

        bRandomPuzzle = new ButtonElement(0, 120, 200, 100, this);
        bRandomPuzzle.setText("Random Mode");
        bRandomPuzzle.setColor(yellow);
        //bRandomPuzzle will move itself and bLoadPuzzle simultaneously. Even though bLoadPuzzle is not initialized at this
        //point, it will be by the time any Element updates, so it is safe to reference it here.
        bRandomPuzzle.setOnUpdateAction(() -> {
            bRandomPuzzle.setY(height / 2);
            int gamemodeButtonWidth = 250;
            int gamemodeButtonPad = 100;
            int availableSpace = width;
            Element[] gamemodeButtons = {bRandomPuzzle, bLoadPuzzle};
            Elements.centerAndSpaceElements(gamemodeButtons, gamemodeButtonWidth, gamemodeButtonPad, availableSpace, 0, Axis.HORIZONTAL);
            if (bRandomPuzzle.getFontSize() > 0) {
                bLoadPuzzle.setMaxFontSize(bRandomPuzzle.getFontSize());
            }
        });
        bRandomPuzzle.setClickListener(() -> {
            pushWindow(Window.SIZE);
        });
        elements_by_window.add(bRandomPuzzle, Window.GAMEMODE);

        bLoadPuzzle = new ButtonElement(0, 250, 200, 100, this);
        bLoadPuzzle.setText("Load Puzzle");
        bLoadPuzzle.setColor(green);
        bLoadPuzzle.setOnUpdateAction(() -> {
            bLoadPuzzle.setY(height / 2);
        });
        bLoadPuzzle.setClickListener(() -> {
            pushWindow(Window.LOAD);
        });
        elements_by_window.add(bLoadPuzzle, Window.GAMEMODE);

        bIncSizeX = new ButtonElement(this);
        bIncSizeX.setText("Λ");
        bIncSizeX.setColor(white);
        bIncSizeX.setOnUpdateAction(() -> {
            Element dummyBlankElement = new Element(this);
            dummyBlankElement.setWidth(100);
            dummyBlankElement.setHeight(50);
            int bottomButtonSpace = 75;
            int verticalSpace = height - necessaryTopPad - bottomButtonSpace;
            int horizontalSpace = width;
            int idealWidth = 100;
            int idealXSpacing = 25;
            int idealHeight = 50;
            int idealYSpacing = 25;
            Element[] topRow = {bIncSizeX, dummyBlankElement, bIncSizeY};
            Elements.centerAndSpaceElements(topRow, idealWidth, idealXSpacing, horizontalSpace, 0, Axis.HORIZONTAL);
            Element[] middleRow = {xSize, theLetterX, ySize};
            Elements.centerAndSpaceElements(middleRow, idealWidth, idealXSpacing, horizontalSpace, 0, Axis.HORIZONTAL);
            Element[] bottomRow = {bDecSizeX, dummyBlankElement, bDecSizeY};
            Elements.centerAndSpaceElements(bottomRow, idealWidth, idealXSpacing, horizontalSpace, 0, Axis.HORIZONTAL);
            Element[] leftCol = {bIncSizeX, xSize, bDecSizeX};
            Elements.centerAndSpaceElements(leftCol, idealHeight, idealYSpacing, verticalSpace, necessaryTopPad, Axis.VERTICAL);
            Element[] middleCol = {dummyBlankElement, theLetterX, dummyBlankElement};
            Elements.centerAndSpaceElements(middleCol, idealHeight, idealYSpacing, verticalSpace, necessaryTopPad, Axis.VERTICAL);
            Element[] rightCol = {bIncSizeY, ySize, bDecSizeY};
            Elements.centerAndSpaceElements(rightCol, idealHeight, idealYSpacing, verticalSpace, necessaryTopPad, Axis.VERTICAL);
        });
        bIncSizeX.setClickListener(() -> {
            if (puzzleSizeX + 1 <= MAX_PUZZLE_SIZE) {
                puzzleSizeX++;
            }
        });
        elements_by_window.add(bIncSizeX, Window.SIZE);

        xSize = new TextElement(this);
        xSize.setText(Integer.toString(puzzleSizeX));
        xSize.setOnUpdateAction(() -> {
            xSize.setText(Integer.toString(puzzleSizeX));
            bIncSizeX.setVisible(puzzleSizeX < MAX_PUZZLE_SIZE);
            bDecSizeX.setVisible(puzzleSizeX > 1);
        });
        elements_by_window.add(xSize, Window.SIZE);

        puzzleSizeXScrollZone = new ScrollAreaElement(this);
        puzzleSizeXScrollZone.setAlignX(Align.LEFT);
        puzzleSizeXScrollZone.setAlignY(Align.TOP);
        puzzleSizeXScrollZone.setOnUpdateAction(() -> {
            int spaceBetweenButtons = bDecSizeX.getTrueY() - bIncSizeX.getTrueY() - bIncSizeX.getHeight();
            puzzleSizeXScrollZone.setWidth(bIncSizeX.getWidth());
            puzzleSizeXScrollZone.setHeight(spaceBetweenButtons);
            puzzleSizeXScrollZone.setX(bIncSizeX.getTrueX());
            puzzleSizeXScrollZone.setY(bIncSizeX.getY() + bIncSizeX.getHeight());
        });
        puzzleSizeXScrollZone.setOnScrollAction(scrollAmt -> {
            while (scrollAmt != 0) {
                if (scrollAmt > 0) {
                    if (puzzleSizeX + 1 <= MAX_PUZZLE_SIZE) {
                        puzzleSizeX++;
                    }
                    scrollAmt--;
                } else {
                    if (puzzleSizeX - 1 > 0) {
                        puzzleSizeX--;
                    }
                    scrollAmt++;
                }
            }
        });
        elements_by_window.add(puzzleSizeXScrollZone, Window.SIZE);

        bDecSizeX = new ButtonElement(this);
        bDecSizeX.setText("V");
        bDecSizeX.setColor(white);
        bDecSizeX.setClickListener(() -> {
            if (puzzleSizeX - 1 > 0) {
                puzzleSizeX--;
            }
        });
        elements_by_window.add(bDecSizeX, Window.SIZE);

        theLetterX = new TextElement(this);
        theLetterX.setText("x");
        elements_by_window.add(theLetterX, Window.SIZE);

        bIncSizeY = new ButtonElement(this);
        bIncSizeY.setText("Λ");
        bIncSizeY.setColor(white);
        bIncSizeY.setClickListener(() -> {
            if (puzzleSizeY + 1 <= MAX_PUZZLE_SIZE) {
                puzzleSizeY++;
            }
        });
        elements_by_window.add(bIncSizeY, Window.SIZE);

        ySize = new TextElement(this);
        ySize.setText(Integer.toString(puzzleSizeY));
        ySize.setOnUpdateAction(() -> {
            ySize.setText(Integer.toString(puzzleSizeY));
            bIncSizeY.setVisible(puzzleSizeY < MAX_PUZZLE_SIZE);
            bDecSizeY.setVisible(puzzleSizeY > 1);
        });
        elements_by_window.add(ySize, Window.SIZE);

        puzzleSizeYScrollZone = new ScrollAreaElement(this);
        puzzleSizeYScrollZone.setAlignX(Align.LEFT);
        puzzleSizeYScrollZone.setAlignY(Align.TOP);
        puzzleSizeYScrollZone.setOnUpdateAction(() -> {
            int spaceBetweenButtons = bDecSizeY.getTrueY() - bIncSizeY.getTrueY() - bIncSizeY.getHeight();
            puzzleSizeYScrollZone.setWidth(bIncSizeY.getWidth());
            puzzleSizeYScrollZone.setHeight(spaceBetweenButtons);
            puzzleSizeYScrollZone.setX(bIncSizeY.getTrueX());
            puzzleSizeYScrollZone.setY(bIncSizeY.getTrueY() + bIncSizeY.getHeight());
        });
        puzzleSizeYScrollZone.setOnScrollAction(scrollAmt -> {
            while (scrollAmt != 0) {
                if (scrollAmt > 0) {
                    if (puzzleSizeY + 1 <= MAX_PUZZLE_SIZE) {
                        puzzleSizeY++;
                    }
                    scrollAmt--;
                } else {
                    if (puzzleSizeY - 1 > 0) {
                        puzzleSizeY--;
                    }
                    scrollAmt++;
                }
            }
        });
        elements_by_window.add(puzzleSizeYScrollZone, Window.SIZE);

        bDecSizeY = new ButtonElement(this);
        bDecSizeY.setText("V");
        bDecSizeY.setColor(white);
        bDecSizeY.setClickListener(() -> {
            if (puzzleSizeY - 1 > 0) {
                puzzleSizeY--;
            }
        });
        elements_by_window.add(bDecSizeY, Window.SIZE);

        bGeneratePuzzle = new ButtonElement(this);
        bGeneratePuzzle.setText("Let's do this!");
        bGeneratePuzzle.setColor(green);
        bGeneratePuzzle.setWidth(200);
        bGeneratePuzzle.setHeight(60);
        bGeneratePuzzle.setAlignY(Align.BOTTOM);
        bGeneratePuzzle.setOnUpdateAction(() -> {
            bGeneratePuzzle.setX(width / 2);
            bGeneratePuzzle.setY(height - 7);
        });
        bGeneratePuzzle.setClickListener(() -> {
            puzzleGrid = new GridElement(new int[]{puzzleSizeX, puzzleSizeY}, this);
            puzzleGrid.generate();
            puzzleGrid.setAlignX(Align.LEFT);
            puzzleGrid.setAlignY(Align.TOP);
            puzzleGrid.setOnUpdateAction(() -> {
                puzzleGrid.setWidth(width - (80 + 55 + 40 + 80));
                puzzleGrid.setX(80 + 55 + 40);
                puzzleGrid.setHeight(height - 80 - WINDOW_BAR_HEIGHT - 55 - 40);
                puzzleGrid.setY(WINDOW_BAR_HEIGHT + 40);
                Point boxLoc = puzzleGrid.convertToBoxCoords(frame.mouseX, frame.mouseY);
                if (puzzleGrid.checkBoxBounds(boxLoc)) {
                    if (frame.clicking()) {
                        if (frame.getMouseButton() == 1) {
                            Boolean result = puzzleGrid.checkAndReveal(boxLoc);
                            if (result != null) {
                                if (!result) {
                                    try {
                                        mistakeCounter.addMistake();
                                    } catch (GameOverException e) {
                                        pushWindow(Window.LOSE);
                                        gameTimer.reset();
                                    }
                                }
                            }
                        } else if (frame.getMouseButton() == 3) {
                            puzzleGrid.mark(boxLoc);
                        }
                    } else if(!puzzleGrid.canMark(boxLoc)) {
                        puzzleGrid.allowMark();
                    }
                }
            });
            elements_by_window.add(puzzleGrid, Window.GAME);
            gameTimer = new Timer();
            gameTimer.begin();
            timerThread = new Thread(gameTimer);
            timerDisplay = new TextElement(this);
            timerDisplay.setAlignX(Align.RIGHT);
            timerDisplay.setAlignY(Align.BOTTOM);
            timerDisplay.setWidth(200);
            timerDisplay.setHeight(30);
            timerDisplay.setOnUpdateAction(() -> {
                timerDisplay.setX(width - 10);
                timerDisplay.setY(height - 10);
                timerDisplay.setText(gameTimer.toString(false));
            });
            elements_by_window.add(timerDisplay, Window.GAME);
            pushWindow(Window.GAME);
            timerThread.start();
        });
        elements_by_window.add(bGeneratePuzzle, Window.SIZE);

        bPause = new ButtonElement(this);
        bPause.setText("Pause");
        bPause.setColor(yellow);
        bPause.setWidth(80);
        bPause.setHeight(80);
        bPause.setX(55);
        bPause.setY(WINDOW_BAR_HEIGHT + 55);
        bPause.setClickListener(() -> {
            pushWindow(Window.PAUSE);
            gameTimer.pause();
        });
        elements_by_window.add(bPause, Window.GAME);

        int MISTAKE_COUNTER_DEFAULT_WIDTH = 200;
        mistakeCounter = new MistakeCounterElement(this);
        mistakeCounter.setAlignX(Align.RIGHT);
        mistakeCounter.setAlignY(Align.CENTER_VERTICAL);
        mistakeCounter.setWidth(MISTAKE_COUNTER_DEFAULT_WIDTH);
        mistakeCounter.setHeight(45);
        mistakeCounter.setOnUpdateAction(() -> {
            mistakeCounter.setX(width - 80);
            mistakeCounter.setY(height - 80 - 55 / 2);
            if (puzzleGrid.getWidth() < MISTAKE_COUNTER_DEFAULT_WIDTH) {
                mistakeCounter.setWidth(puzzleGrid.getWidth());
            } else {
                mistakeCounter.setWidth(MISTAKE_COUNTER_DEFAULT_WIDTH);
            }
        });
        elements_by_window.add(mistakeCounter, Window.GAME);
        elements_by_window.add(mistakeCounter.mistakes, Window.GAME);

        bExit = new ButtonElement(this);
        bExit.setText("Exit to Menu");
        bExit.setColor(red);
        bExit.setWidth(200);
        bExit.setHeight(60);
        bExit.setAlignY(Align.CENTER_VERTICAL);
        bExit.setOnUpdateAction(() -> bExit.setY(height / 2));
        bExit.setClickListener(() -> {
            gameTimer.reset();
            while (currWindow != Window.MAIN) {
                popWindow();
            }
        });
        elements_by_window.add(bExit, Window.PAUSE);

        bResume = new ButtonElement(this);
        Element[] pauseMenuButtons = {bResume, bExit};
        bResume.setText("Resume");
        bResume.setColor(yellow);
        bResume.setWidth(200);
        bResume.setHeight(60);
        bResume.setAlignX(Align.CENTER_HORIZONTAL);
        bResume.setAlignY(Align.CENTER_VERTICAL);
        bResume.setOnUpdateAction(() -> {
            bResume.setX(width / 2);
            bResume.setY(height / 2);
            Elements.centerAndSpaceElements(pauseMenuButtons, 200, 40, width - 40, 20, Axis.HORIZONTAL);
        });
        bResume.setClickListener(() -> {
            popWindow();
            gameTimer.resume();
        });
        elements_by_window.add(bResume, Window.PAUSE);
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
        if (windowStack == null) {
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
        if (windowStack.size() == 1) {
            System.out.println("Quitting because the window stack was emptied.");
            quitGame();
        }
        if (windowStack.empty()) {
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

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
        frame.setKeyHandler(keyListener);
    }
}

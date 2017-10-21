package mygl;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

/**
 * @author onContentStop
 */
public class Graphics implements Runnable, WindowListener, WindowFocusListener {
	/**
	 * The height of the title bar in most operating systems. If this isn't true for all operating systems, tell me.
	 */
	public static final int WINDOW_BAR_HEIGHT = 30;
	/**
	 * The width of the {@link Graphics#frame} that Graphics owns. Should be updated whenever the frame is resized.
	 */
	public int width = 800;
	/**
	 * The height of the {@link Graphics#frame} that Graphics owns. Should be updated whenever the frame is resized.
	 */
	public int height = 600;
	/**
	 * A {@link BetterFrame frame} that will be used to show the graphics on screen. Uses a modified version of the {@link Frame} class.
	 */
	protected BetterFrame frame;
	/**
	 * The {@link Font} to be used when drawing text. This is placed in a variable so that it is easier to access than using graphics2D.getFont()
	 */
	protected Font f;
	/**
	 * This is what everything is actually drawn onto. It is resized to fit its {@link Graphics#frame} frequently.
	 */
	protected Graphics2D graphics2D;
	/**
	 * A flag that stores whether this Graphics instance is meant to be active.
	 */
	private boolean running;
	/**
	 * A flag that stores whether the Graphics has stopped drawing so that the {@link Graphics#frame} can be removed.
	 */
	private boolean done;
	/**
	 * A flag that stores whether the {@link Graphics#frame} is currently being shown on screen. If it is not, nothing will be drawn.
	 */
	private boolean visible;
	/**
	 * An {@link Image} that {@link Graphics#graphics2D} is pasted onto before being shown on screen. This prevents flickering, as would be seen
	 * if the graphics2D was pushed directly to the screen.
	 */
	private Image imgBuffer;
	/**
	 * The length of time between frames.
	 */
	private short sleepInterval;

	/**
	 * Creates a new Graphics environment with the default title "Frame".
	 */
	public Graphics() {
		frame = new BetterFrame("Frame", new Dimension(width, height));
		initializeCommonVariables();
	}

	/**
	 * Creates a new Graphics environment with a custom title.
	 * @param title the title to use for the frame
	 */
	public Graphics(String title) {
		frame = new BetterFrame(title, new Dimension(width, height));
		initializeCommonVariables();
	}

	/**
	 * Sets up the important things in Graphics.
	 * <br/>
	 * Starts by setting the flags {@link Graphics#running} to true and {@link Graphics#done} to false.
	 * <br/>
	 * Then determines the current monitor's width and height in order to center itself on screen.
	 * <br/>
	 * Adds some listeners to the {@link Graphics#frame} so that it knows when it is being closed, minimized, or lost focus.
	 * <br/>
	 * Ensures that it is not visible by default (i.e until it is explicitly set to be).
	 * <br/>
	 * Creates the first instance of the image buffer.
	 */
	private void initializeCommonVariables() {
		running = true;
		done = false;
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int mon_width = gd.getDisplayMode().getWidth();
		int mon_height = gd.getDisplayMode().getHeight();
		frame.setLocation(mon_width / 2 - width / 2, mon_height / 2 - height / 2);
		frame.addWindowListener(this);
		frame.addWindowFocusListener(this);
		visible = false;
		sleepInterval = 10;
		imgBuffer = frame.createImage(width, height);
	}

	@Override
	public void run() {
		while (running) {
			mouseActions();
			runActions();
			Elements.update();
			updateSize();
			try {
				Thread.sleep(sleepInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void mouseActions() {

	}

	protected void runActions() {

	}

	public void updateSize() {
		if (width != frame.getWidth())
			width = frame.getWidth();
		if (height != frame.getHeight())
			height = frame.getHeight();
	}

	public void startDraw() {
		graphics2D = (Graphics2D) imgBuffer.getGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setFont(new Font("Arial", Font.PLAIN, 50));
		f = graphics2D.getFont();
	}

	public void endDraw() {
		graphics2D = (Graphics2D) frame.getGraphics();
		if (graphics2D != null) {
			imgBuffer = Resizer.PROGRESSIVE_BILINEAR.resize((BufferedImage) imgBuffer, width, height);
			graphics2D.drawImage(imgBuffer, 0, 0, width, height, 0, 0, width, height, null);
			graphics2D.dispose();
		}
	}

	protected void draw() {
		startDraw();
		drawActions();
		Elements.draw();
		endDraw();
	}

	protected void drawActions() {

	}

	public BetterFrame getFrame() {
		return frame;
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
		if (visible)
			imgBuffer = frame.createImage(width, height);
		this.visible = visible;
	}

	public Graphics2D getGraphics2D() {
		return graphics2D;
	}

	public Font getFont() {
		return f;
	}

	public void setFont(Font font) {
		graphics2D.setFont(font);
		f = font;
	}

	//region unused overrides
	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		frame.setVisible(false);
		running = false;
		frame.dispose();
		done = true;
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (done)
			System.exit(0);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			//e1.printStackTrace();
		}
	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}


	@Override
	public void windowGainedFocus(WindowEvent e) {

	}

	@Override
	public void windowLostFocus(WindowEvent e) {

	}
	//endregion

	public void setFrameSleepInterval(short sleepInterval) {
		this.sleepInterval = sleepInterval;
	}

	public void handleKey(KeyEvent e) {

	}
}

package mygl;

import java.time.Duration;

import static java.time.Duration.ZERO;

/**
 * @author onContentStop
 */

@SuppressWarnings("InfiniteLoopStatement")
public class Timer implements Runnable {
	/**
	 * The time to reset to when the Timer is reset.
	 */
	private Duration startTime;
	/**
	 * The time currently stored in the Timer.
	 */
	private Duration currentTime;
	/**
	 * Whether or not the Timer object is currently running.
	 */
	private boolean running;
	/**
	 * Whether or not the Timer object is adding time when it updates (if false, the Timer will subtract <code>delay</code> from its time every <code>delay</code> milliseconds.
	 */
	private boolean countingUp;
	/**
	 * The amount of time between updates to the current time, in milliseconds
	 */
	private int delay;

	/**
	 * Timer constructor. Counting up by default.
	 */
	public Timer() {
		running = false;
		startTime = ZERO;
		currentTime = ZERO;
		countingUp = true;
		delay = 10;
	}

	/**
	 * Timer constructor, with an option for changing the frequency of updates
	 *
	 * @param delay the amount of time between updates to the current time, in milliseconds
	 */
	public Timer(int delay) {
		running = false;
		startTime = ZERO;
		currentTime = ZERO;
		countingUp = true;
		this.delay = delay;
	}

	/**
	 * Timer constructor, with options for counting up or down and an initial time.
	 *
	 * @param countUp whether or not the Timer should count up
	 * @param length  the initial amount of time, in milliseconds
	 */
	public Timer(boolean countUp, int length) {
		running = false;
		if (!countUp) {
			startTime = Duration.ofMillis(length);
			currentTime = Duration.ofMillis(length);
		} else {
			startTime = ZERO;
			currentTime = Duration.ofMillis(length);
		}
		countingUp = countUp;
		delay = 10;

	}

	@Override
	public void run() {
		while (true) {
			if (running)
				if (countingUp) {
					currentTime = currentTime.plusMillis(delay);
				} else {
					currentTime = currentTime.minusMillis(delay);
					if (currentTime.toMillis() <= 0) {
						running = false;
					}
				}

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks the current amount of time on the Timer.
	 *
	 * @return the time in hours, truncated
	 */
	public int getHours() {
		return (int) (currentTime.getSeconds() / 3600.0);
	}

	/**
	 * Checks the current amount of time on the Timer.
	 *
	 * @return the time in seconds, truncated
	 */
	public int getSeconds() {
		return (int) currentTime.getSeconds();
	}

	/**
	 * Checks the current amount of time on the Timer.
	 *
	 * @return the time in milliseconds
	 */
	public int getMS() {
		return (int) currentTime.toMillis();
	}

	/**
	 * Adds time to the Timer.
	 *
	 * @param seconds the number of seconds to add
	 */
	public void addSeconds(int seconds) {
		currentTime = currentTime.plusSeconds(seconds);
	}

	/**
	 * Adds time to the Timer.
	 *
	 * @param millis the number of milliseconds to add
	 */
	public void addMS(int millis) {
		currentTime = currentTime.plusMillis(millis);
	}

	/**
	 * Represents the current time on the Timer as a String, in h:mm:ss.(milliseconds) with the option to
	 *
	 * @param zeroes whether or not the String should include leading zeroes
	 * @return the time as a String
	 */
	public String toString(boolean zeroes) {
		int ms = (int) currentTime.toMillis();
		int seconds = ms / 1000;
		int minutes = seconds / 60;
		int hours = minutes / 60;
		seconds %= 60;
		minutes %= 60;
		String out = "";
		int numSeparators;
		if (!zeroes) {
			if (hours > 0) {
				numSeparators = 4;
			} else if (minutes > 0) {
				numSeparators = 3;
			} else if (seconds > 0) {
				numSeparators = 2;
			} else {
				numSeparators = 1;
			}
			switch (numSeparators) {
				case 4:
					out = "" + hours + ":" + (minutes < 10 ? '0' : "") + minutes + ':' + (seconds < 10 ? '0' : "") + seconds + '.' + (ms / 10 % 100 < 10 ? '0' : "") + ms / 10 % 100;
					break;
				case 3:
					out = "" + minutes + ':' + (seconds < 10 ? '0' : "") + seconds + '.' + (ms / 10 % 100 < 10 ? '0' : "") + ms / 10 % 100;
					break;
				case 2:
					out = "" + seconds + '.' + (ms / 10 % 100 < 10 ? '0' : "") + ms / 10 % 100 + " s";
					break;
				case 1:
					out = "" + ms % 1000 + " ms";
			}
			return out;
		} else
			return ("" + hours + ':' + (minutes < 10 ? '0' : "") + minutes + ':' + (seconds < 10 ? '0' : "") + seconds + '.' + (ms / 10 % 100 < 10 ? '0' : "") + (ms / 10 % 100));
	}

	/**
	 * Deprecated. Use <code>toString(boolean)</code> instead, an argument of <code>true</code> has the same functionality as this method.
	 *
	 * @return the time as a String
	 */
	public String toString() {
		int ms = (int) currentTime.toMillis();
		int seconds = ms / 1000;
		int minutes = seconds / 60;
		int hours = minutes / 60;
		seconds %= 60;
		minutes %= 60;
		ms /= 10;
		ms %= 100;
		return ("" + hours + ':' + (minutes < 10 ? '0' : "") + minutes + ':' + (seconds < 10 ? '0' : "") + seconds + '.' + (ms < 10 ? '0' : "") + (ms));
	}

	/**
	 * Halts the Timer, but does not reset it.
	 */
	public void pause() {
		running = false;
	}

	/**
	 * Sets <code>running</code> to true without resetting the Timer.
	 */
	public void resume() {
		running = true;
	}

	/**
	 * Sets <code>running</code> to true without resetting the Timer.
	 */
	public void start() {
		running = true;
	}

	/**
	 * Returns the Timer to its initial state, and stops it from running.
	 */
	public void reset() {
		currentTime = startTime;
		running = false;
	}

	/**
	 * Returns the Timer to its initial state, and runs it immediately.
	 */
	public void begin() {
		currentTime = startTime;
		running = true;
	}

	/**
	 * Returns the Timer to its initial state, and runs it immediately.
	 */
	public void restart() {
		currentTime = startTime;
		running = true;
	}

	/**
	 * May be used to give a different initial time to a countdown timer, and resets it in the process.
	 *
	 * @param length the length of time to begin the countdown at
	 */
	public void restart(int length) {
		if (!countingUp)
			startTime = Duration.ofMillis(length);
		else
			startTime = ZERO;
		running = true;
	}

	/**
	 * Checks if the Timer is currently running
	 *
	 * @return the status of the timer
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Changes the frequency at which the Timer updates.
	 *
	 * @param delay the time to wait, in milliseconds, before adding to <code>currentTime</code>
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Checks if the Timer is counting up or down.
	 *
	 * @return the direction that the Timer is taking
	 */
	public boolean countingUp() {
		return countingUp;
	}
}

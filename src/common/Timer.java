package common;

import java.time.Duration;

import static java.time.Duration.ZERO;

/**
 * @author onContentStop
 */

@SuppressWarnings("InfiniteLoopStatement")
public class Timer implements Runnable {
	private Duration startTime, currentTime;
	private boolean running;
	private boolean countingUp;
	private int delay;

	public Timer() {
		running = false;
		startTime = ZERO;
		currentTime = ZERO;
		countingUp = true;
		delay = 10;
	}

	public Timer(int delay) {
		running = false;
		startTime = ZERO;
		currentTime = ZERO;
		countingUp = true;
		this.delay = delay;
	}

	public Timer(boolean countUp, int length) {
		running = false;
		if (!countUp) {
			startTime = Duration.ofMillis(length);
			currentTime = Duration.ofMillis(length);
		} else {
			startTime = ZERO;
			currentTime = ZERO;
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

	public int getHours() {
		return (int) (currentTime.getSeconds() / 3600.0);
	}

	public int getSeconds() {
		return (int) currentTime.getSeconds();
	}

	public int getMS() {
		return (int) currentTime.toMillis();
	}

	public void addSeconds(int seconds) {
		currentTime = currentTime.plusSeconds(seconds);
	}

	public void addMS(int millis) {
		currentTime = currentTime.plusMillis(millis);
	}

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

	public void pause() {
		running = false;
	}

	public void resume() {
		running = true;
	}

	public void start() {
		running = true;
	}

	public void reset() {
		currentTime = startTime;
		running = false;
	}

	public void begin() {
		currentTime = startTime;
		running = true;
	}

	public void restart() {
		currentTime = startTime;
		running = true;
	}

	public void restart(int length) {
		if (!countingUp)
			startTime = Duration.ofMillis(length);
		else
			startTime = ZERO;
		running = true;
	}

	public boolean isRunning() {
		return running;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public boolean countingUp() {
		return countingUp;
	}
}

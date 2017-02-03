package common;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * @author onContentStop
 */
public class Motion {
	public static final int MODE_EXPONENTIAL = 0;
	public static final int MODE_LINEAR = 1;
	public static final int MODE_QUADRATIC = 2;
	public static final int MODE_LOGISTIC = 3;
	public static int getNumberBetween(int mode, int n1, int n2, double percentage) throws InvalidArgumentException {
		double progressFunction = 0;//this will be changed in any valid mode, otherwise an error will be thrown
		switch(mode) {
			case MODE_EXPONENTIAL:
				progressFunction = Math.pow(100, percentage - 1);
				break;
			case MODE_LINEAR:
				progressFunction = percentage;
				break;
			case MODE_QUADRATIC:
				progressFunction = Math.pow(percentage, 2);
				break;
			case MODE_LOGISTIC:
				progressFunction = 1.06 / (1d + Math.pow(Constants.e, -3.5 * (2 * percentage - 1))) - 0.03;
				break;
			default:
				throw new InvalidArgumentException(new String[]{"Mode given does not exist: " + mode});
		}
		long out = Math.round((double)n1 * (1 - progressFunction) + (double) n2 * progressFunction);
		if(percentage > 1)
			return n2;
		return (int)out;
	}
}

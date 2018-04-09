package mygl;

/**
 * @author onContentStop
 */
public class Motion {
    public static final int MODE_EXPONENTIAL = 0;
    public static final int MODE_LINEAR = 1;
    public static final int MODE_QUADRATIC = 2;
    public static final int MODE_LOGISTIC = 3;

    public static int getNumberBetween(MotionMode mode, int n1, int n2, double percentage) throws IllegalArgumentException {
        double progressFunction = 0;//this will be changed in any valid mode, otherwise an error will be thrown
        switch (mode) {
            case EXPONENTIAL:
                progressFunction = Math.pow(100, percentage - 1);
                break;
            case LINEAR:
                progressFunction = percentage;
                break;
            case QUADRATIC:
                progressFunction = Math.pow(percentage, 2);
                break;
            case LOGISTIC:
                progressFunction = 1.06 / (1d + Math.pow(Constants.e, -3.5 * (2 * percentage - 1))) - 0.03;
                break;
            case CIRCLE_QUADRANT:
                progressFunction = Math.sqrt(-1d * Math.pow(percentage - 1, 2) + 1);
                break;
        }
        long out = Math.round((double) n1 * (1 - progressFunction) + (double) n2 * progressFunction);
        if (percentage > 1)
            return n2;
        return (int) out;
    }
}

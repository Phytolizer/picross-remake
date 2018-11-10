package bgusolver;

/**
 * saves the output of the line solver, for later use
 */
public class JCLineSolverState {

    private int maxPos;
    private byte[] line;
    private byte[][] sol;
    private boolean[][] memoized;

    public byte[] getLine() {
        return line;
    }

    public void setLine(byte[] line) {
        this.line = line;
    }

    public byte[][] getSol() {
        return sol;
    }

    public void setSol(byte[][] sol) {
        this.sol = sol;
    }

    public boolean[][] getMemoized() {
        return memoized;
    }

    public void setMemoized(boolean[][] memoized) {
        this.memoized = memoized;
    }

    public JCLineSolverState(byte[] line, byte[][] sol, boolean[][] memoized) {
        this.line = line;
        this.sol = sol;
        this.memoized = memoized;
    }

    public void updateState(byte[] line, byte[][] sol, boolean[][] memoized) {
        this.line = line;
        this.sol = sol;
        this.memoized = memoized;
    }

    public int getMaxPos() {
        return maxPos;
    }

    public void setMaxPos(int maxPos) {
        this.maxPos = maxPos;
    }

}

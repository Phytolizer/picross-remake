package bgusolver;

import java.util.Arrays;


/**
 * solves a single line using dynamic programming.
 */


public class JCSolveSingleLineTOPDOWN {


    // used to enumerate cell colors
    private static final byte WHITE = 0;
    private static final byte BLACK = 1;
    private static final byte UNKNOWN = 2;
    private static final byte BOTH_COLORS = 3;

    /**
     * used to enumerate where the solution originated from in our dynamic programming matrix
     * we will use this info to reconstruct the solution from the matrix
     */
    private static final byte NO_SOL = 0;
    private static final byte SOL_SPACE = 1; //there's a solution where this position is a space
    private static final byte SOL_BLOCK = 2; //there's a solution where this position is the end of a block
    private static final byte SOL_BOTH = 3; //there's a solution where this position is either space or end of block

    public byte[] line; // contents of the line's cells
    public byte[] blocks; // blocks ("clues") next to the line

    private boolean[] sol; // partial solution matrix
    private boolean[] memoized; // indicates if we've previously calculated this position

    private int matRows;
    private int matCols;
    private int[] blockSums;

    private byte[] solvedLine;
    private byte[] origLine;
    private byte[] row;
    private byte[] col;

    public boolean hasSolution;
    public int numCellsSolved;

    //			private int[][] matrixStats; // used to generate statistics of which matrix cells were popular
    private byte[] solvedRow;
    private byte[] solvedCol;

    private int numUnknownCells; // number of cells which are initially unknown
    private int numBothColorCells; // number of cells which we determined that can be of both colors
    private int count;

    public JCSolveSingleLineTOPDOWN(byte[] line, byte[] blocks, int maxDimensions, int numRows, int numCols) {
        this.line = line;
        this.blocks = blocks;
        this.sol = null;
        hasSolution = true;

        matRows = maxDimensions + 1;
        matCols = (maxDimensions / 2) + 2;

        this.sol = new boolean[matRows * matCols];
        this.memoized = new boolean[matRows * matCols];
			
			/* we define our internal representation of a line to be one cell larger then the original
				this is done to avoid an edge case later in our recursive formula */
        this.row = new byte[numCols + 1];
        this.solvedRow = new byte[numCols + 1];
        this.col = new byte[numRows + 1];
        this.solvedCol = new byte[numRows + 1];

        //this.matrixStats = new int[maxDimensions+1][(maxDimensions / 2) + 2];

    }

    /**
     * updates the solver, with the information of the line we're interested in solving
     *
     * @param line      line's contents (current cell colors)
     * @param blocks    blocks next to the line (clues)
     * @param blockSums partial sums of the blocks
     * @param isRow     "true" if this is a row, false if a column
     * @param initSol   initial sol[] matrix, if we've previously solved a similar line - we'll take part of the old matrix
     * @param initMem   initial memoized[] matrix, if we've previously solved a similar line - we'll take part of the old matrix
     * @param maxPos    tells us the maximum position to copy from the initSol/initMem matrices
     */
    public void updateSolver(byte[] line, byte[] blocks, int blockSums[], boolean isRow, byte[][] initSol, boolean[][] initMem, int maxPos) {

        numCellsSolved = 0;

        origLine = line.clone();
        if (isRow) {
            this.line = row;
            solvedLine = solvedRow;
        } else {
            this.line = col;
            solvedLine = solvedCol;
        }

        System.arraycopy(line, 0, this.line, 0, line.length);
        solvedLine = this.line.clone();

        this.blocks = blocks;
        this.blockSums = blockSums;

        Arrays.fill(memoized, false);

        hasSolution = true;

    }

    public byte[] getLine() {
        return this.line;
    }

    public byte[] getBlocks() {
        return this.blocks;
    }

    /**
     * solve a partially filled line.
     *
     * @return the solved line
     */
    public byte[] solveLine() {

        // fill in the solution matrix. this will also create the solved line in "solvedLine"
        fillMatrixTopDown(line.length - 1, blocks.length);

        // next, we check that the line has a solution
        this.hasSolution = sol[getMatIndex(line.length - 1, blocks.length)];
        if (!this.hasSolution) {
            return null; // no solution, we return null
        }

        //reconSolution(line.length-1, blocks.length);

        // convert indexes of the solved line (turn "3" into "2"), and count the number of solved cells
        for (int i = 0; i < origLine.length; i++) {

            if ((origLine[i] == UNKNOWN) && (solvedLine[i] < UNKNOWN)) {
                numCellsSolved++;
                origLine[i] = solvedLine[i];
            }
        }

        // return the solution
        return origLine;
    }


    /**
     * fills the solution matrix in a top-down
     * using memoization to determine if a recursive call has already been calculated
     *
     * @param position position of cell we're currently trying to fill
     * @param job      current job of the cell
     */
    private void fillMatrixTopDown(int position, int job) {

        boolean whiteAns, blackAns, edgeAns;

        if ((position < 0) || (job < 0)) {
            return;
        }

        /* if we have too many jobs to fit this line segment, we can stop the recursion and return false */
        if (position < blockSums[job]) {
            setSol(position, job, false);
            return;
        }
        /* base case */

        // reached the end of the line

        if (position == 0) {
            if ((job == 0) && (line[position] != BLACK)) {
                setSol(position, job, true);
                setLineCell(position, WHITE);
            } else {
                setSol(position, job, false);
            }

            return;
        }

        // finished filling all jobs (can still fill whitespace)
        if (job == 0) {
            if ((line[position] != BLACK) && getSol(position - 1, job)) {
                setSol(position, job, true);
                setLineCell(position, WHITE);
            } else {
                setSol(position, job, false);
            }
            return;
        }

        /* recursive case */

        if (line[position] == BLACK) // current cell is BLACK
        {
            setSol(position, job, false); // cant place a block if the cell is black
        } else { // current cell is either white or unknown
            whiteAns = getSol(position - 1, job); // set cell white and continue
            blackAns = (canPlaceBlock(position - blocks[job - 1], blocks[job - 1]) && getSol(position - blocks[job - 1] - 1, job - 1)); // set cell white, place the current block and continue

            if (whiteAns) {
                setLineCell(position, WHITE);
                if (blackAns) { // both space and block
                    setSol(position, job, true);
                    setLineBlock(position - blocks[job - 1], position);
                } else // space, but not block
                {
                    setSol(position, job, true);
                }

            } else {
                if (blackAns) { // block, but not space
                    setSol(position, job, true);
                    setLineBlock(position - blocks[job - 1], position);
                } else {
                    setSol(position, job, false); // no solution
                }
            }
        }

    }

    /**
     * check if we can place a block of a specific length in this position
     * we check that our partial solution does not negate the line's partial solution
     *
     * @param position position to place block at
     * @param length   length of block
     * @return "true" if block can be placed, "false otherwise
     */
    private boolean canPlaceBlock(int position, int length) {
        if (position < 0) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (line[position + i] == 0) {
                return false;
            }
        }

        // if no negations were found, the block can be placed
        return true;
    }

    /**
     * sets a cell in the solution matrix
     */
    public void setLineCell(int position, byte value) {

        switch (solvedLine[position]) {
            case BOTH_COLORS:
                break;
            case UNKNOWN: {
                solvedLine[position] = value;
                break;
            }
            default: {
                if (solvedLine[position] != value) {
//						System.out.println("orig cellcolor: "+solvedLine[position]);
//						System.out.println("setting cell "+position+" as both colors");
                    solvedLine[position] = BOTH_COLORS;
                    numBothColorCells++;
                }
                break;
            }
        }
    }

    /**
     * sets a block in the solution matrix. all cells are painted black, except the endPos which is white.
     *
     * @param startPos position to start paintin
     * @param endPos   position to stop painting
     */
    private void setLineBlock(int startPos, int endPos) {
        // set blacks
        for (int i = startPos; i < endPos; i++) {

            switch (solvedLine[i]) {
                case BOTH_COLORS:
                    break;
                case UNKNOWN: {
                    solvedLine[i] = BLACK;
                    break;
                }
                case WHITE: {
                    solvedLine[i] = BOTH_COLORS;
                    numBothColorCells++;
                    break;
                }
                default:
                    break;
            }

        }

        // set endPos to be white
        switch (solvedLine[endPos]) {
            case BOTH_COLORS:
                break;
            case UNKNOWN: {
                solvedLine[endPos] = WHITE;
                break;
            }
            case BLACK: {
                solvedLine[endPos] = BOTH_COLORS;
                numBothColorCells++;
                break;
            }
            default:
                break;
        }
    }


    /**
     * sets a value in the solution matrix
     * also sets cell as TRUE in the memoization matrix (so we wont calculate this value recursively anymore)
     */
    private void setSol(int position, int job, boolean value) {
        if (position < 0) {
            return;
        }

        sol[getMatIndex(position, job)] = value;
        memoized[getMatIndex(position, job)] = true;
    }

    /**
     * gets the value from the solution matrix
     * if the value is not memoized yet, we calculate it recursively
     *
     * @return value of pos,job in the solution matrix
     */
    private boolean getSol(int position, int job) {

        if (position == -1) {
            // finished placing the last block, exacly at the beginning of the line.
            return job == 0;
        }

        if (!memoized[getMatIndex(position, job)]) {
            fillMatrixTopDown(position, job);
        }

        return sol[getMatIndex(position, job)];

    }

    public boolean hasSolution() {
        return this.hasSolution;
    }
		
		/*
		public void printStats(){
			double sum = 0;

			System.out.println("getSol");
			sum = 0;
			for (int i=0; i < this.matrixStats.length; i++)
				for (int j=0; j < this.matrixStats[0].length; j++)
					sum += (double)this.matrixStats[i][j];
			
			System.out.println(sum);
			for (int i=0; i < this.matrixStats.length; i++){
				for (int j=0; j < this.matrixStats[0].length; j++)
					System.out.print((this.matrixStats[i][j]/sum * 100)+" ");
				System.out.println();
				
			}
			
		}
		*/

    /**
     * calculates the partial sum of the blocks. this is used later to determine if we can fit some blocks in the space left on the line
     *
     * @return partial sum of the blocks
     */
    public int[] calcBlockSum(byte[] blocks) {
        int[] blockSum = new int[blocks.length + 1];
        if (blockSum.length > 1) {
            blockSum[1] = blocks[0];
        }

        for (int j = 2; j < blocks.length + 1; j++) {
            blockSum[j] = (blockSum[j - 1] + blocks[j - 1]);
        }

        for (int j = 1; j < blockSum.length; j++) {
            blockSum[j] += j - 2;
        }

        return blockSum;
    }

    /**
     * reconstructs the solution, according to the dynamic programming matrix (sol[])
     * we assume that the sol[] matrix is already full
     */
		/*
		public void reconSolution(int position, int job){
			boolean curr;
			
			if ((position < 0) || (job < 0))
				return;
			
			if (memRecon[getMatIndex(position, job)]) // if we already reconstructed this part, don't do it again
				return;
			
			memRecon[getMatIndex(position, job)] = true;
			curr = getSol(position, job);
			
			switch (curr) {
				case NO_SOL: break;
				case SOL_SPACE: {
					reconSolution(position-1, job);
					setLineCell(position, WHITE);
					break;
				}
				case SOL_BLOCK: {
					reconSolution(position-blocks[job-1]-1, job-1);
					setLineBlock(position-blocks[job-1], position);
					break;
				}
				case SOL_BOTH: {
				
					reconSolution(position-1, job);
					reconSolution(position-blocks[job-1]-1, job-1);
					setLineCell(position, WHITE);
					setLineBlock(position-blocks[job-1], position);		
					break;
				}
				
				default:
					break;
			}
		}
		*/

    /**
     * find the first cell which is different, between two lines
     *
     * @param line0 first line
     * @param line1 second line
     * @return the position of the last cell which is still the same between the two matrices. if first cell is different return -1. if all are the same we return the line's length - 1
     */
    public static int firstDiffCell(byte[] line0, byte[] line1) {
        for (int i = 0; i < line0.length; i++) {
            if (line0[i] != line1[i]) {
                return (i - 1);
            }
        }

        return line0.length - 1;
    }

    public static boolean[][] cloneBoolMat(boolean[][] original) {
        boolean[][] ans = new boolean[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            ans[i] = original[i].clone();
        }
        return ans;
    }

    public static byte[][] cloneByteMat(byte[][] original) {
        byte[][] ans = new byte[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            ans[i] = original[i].clone();
        }
        return ans;
    }

//		public byte[][] getSolClone(){
//			return cloneByteMat(sol);
//		}
//		
//		public boolean[][] getMemClone(){
//			return cloneBoolMat(memoized);
//		}

    public static void main(String[] args) {
        byte[] line = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        byte[] blocks = {1, 1, 1};
        byte[] ans;
        byte[][] sol;
        boolean[][] memoized;
        JCSolveSingleLineTOPDOWN ssl = new JCSolveSingleLineTOPDOWN(null, null, line.length, line.length, line.length);
        ssl.updateSolver(line, blocks, ssl.calcBlockSum(blocks), true, null, null, 0);
        ans = ssl.solveLine();
        System.out.println(Arrays.toString(ans));

//			sol = ssl.cloneByteMat(ssl.sol);
//			memoized = ssl.cloneBoolMat(ssl.memoized);

        //ssl.updateSolver(newLine, blocks, ssl.calcBlockSum(blocks), true, null, null, 0);
//			ssl.updateSolver(newLine, blocks, ssl.calcBlockSum(blocks), true, sol, memoized, firstDiffCell(line, newLine));
//			ans = ssl.solveLine();

        System.out.println(Arrays.toString(ans));
    }

    /**
     * convert the 2D matrix address into a 1D address
     */
    public final int getMatIndex(int row, int col) {
        return row * this.matCols + col;
    }
}
	

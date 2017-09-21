package bgusolver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * line solver, using the new Dynamic Line Solving algorithm
 *
 * @author Administrator
 */
public class JCIndependantSolver {
	private byte[][] oldBoard;
	private byte[][] board;
	private byte[][] newBoard;
	private byte[][] rowBlocks;
	private byte[][] colBlocks;
	private byte[][] boardBeforeIteration;
	private JCSolveSingleLineTOPDOWN ssl;
	private JCDynamicLineSolver dls;
	private boolean lastMoveWasGuess;
	private int startScore;
	private boolean[][][] alreadyProbed;
	private int currScore;
	private int maxScore;
	private int countImmidiateNeigbours;
	private int countChangedCells;
	private int countTwoKnown;
	private int countOneKnown;
	private int prevBestRow;
	private int prevBestCol;
	private int bestRow;
	private int bestCol;
	private int nodes;
	private boolean cons;
	private int bestColor;
	private int decisions;
	private int numSolutions;
	private int depth;
	private int maxDepth;
	private byte[][] sol;

	private int maxSolutions;
	private ArrayList<byte[][]> solutionList;
	private ArrayList<JCBoardState> searchState;
	private long startTime;
	private int countGuessing;
	private int timeout;
	private long endTime;
	private int minDepth;


	public JCIndependantSolver(byte[][] board, byte[][] rowBlocks, byte[][] colBlocks) {
		this.board = board;
		this.rowBlocks = rowBlocks;
		this.colBlocks = colBlocks;

		this.ssl = new JCSolveSingleLineTOPDOWN(null, null, Math.max(board.length, board[0].length), board.length, board[0].length);
		this.dls = new JCDynamicLineSolver(board, rowBlocks, colBlocks, board.length, board[0].length, ssl);

		alreadyProbed = new boolean[2][board.length][board[0].length];
		oldBoard = new byte[board.length][board[0].length];
		boardBeforeIteration = new byte[board.length][board[0].length];
		newBoard = new byte[board.length][board[0].length];
		searchState = new ArrayList<JCBoardState>();
		this.maxSolutions = 2;

	}

	public JCIndependantSolver(String fileName) {
		readFromFile(fileName);
		this.board = new byte[rowBlocks.length][colBlocks.length];
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				board[i][j] = 2;

		this.ssl = new JCSolveSingleLineTOPDOWN(null, null, Math.max(board.length, board[0].length), board.length, board[0].length);
		this.dls = new JCDynamicLineSolver(board, rowBlocks, colBlocks, board.length, board[0].length, ssl);

		alreadyProbed = new boolean[2][board.length][board[0].length];
		oldBoard = new byte[board.length][board[0].length];
		boardBeforeIteration = new byte[board.length][board[0].length];
		newBoard = new byte[board.length][board[0].length];
		searchState = new ArrayList<JCBoardState>();
		this.maxSolutions = 2;
		this.minDepth = 999;
	}


	public boolean initSearch() {

		solutionList = new ArrayList<byte[][]>();
		boolean ans;
		ans = solveBoard(false, 0, 0);

		if (isSolved(board)) {
			numSolutions++;
			sol = new byte[board.length][board[0].length];
			multiArrayCopy(board, sol);
			solutionList.add(sol);
			minDepth = 0;
			return true;
		}

		JCBoardState nextState = new JCBoardState(bestRow, bestCol, bestColor);
		board[bestRow][bestCol] = (byte) bestColor;
		searchState.add(nextState);
		//System.out.println("guessing   first: "+bestRow +" "+bestCol+" "+bestColor);
		ans = search();

		return ans;
	}

	public boolean search() {

		boolean ans;
		JCBoardState nextState;

		// update the maximum depth reached during the search
		depth = searchState.size();
		if (depth > maxDepth)
			maxDepth = depth;

		// check if solution limit has been reached
		if ((maxSolutions > 0) && (numSolutions >= maxSolutions))
			return true;

		// check if timeout has occured
		if ((timeout > 0) && ((System.currentTimeMillis() - startTime) / 1000 > timeout))
			return true;

		// check if we finished our search
		if (searchState.isEmpty())
			return true;

		JCBoardState currState = searchState.get(searchState.size() - 1);


		// limit depth
//		if (depth > 48) {
//			searchState.remove(searchState.size() - 1);
//			depth--;
//			currState.revertChanges(board);
//			return true;
//		}

		// guess first according to the heuristic
		//System.out.println("num cells solved: "+calcScore(board));
		ans = solveBoard(true, currState.row, currState.col);

		if (isSolved(board) && ans) {
			sol = new byte[board.length][board[0].length];
			multiArrayCopy(board, sol);
			solutionList.add(sol);
			numSolutions++;
			if (depth < minDepth)
				minDepth = depth;
			//System.out.println(depth);
		}

		if ((maxSolutions > 0) && (numSolutions >= maxSolutions))
			return true;

		currState.setChangedCells(boardBeforeIteration, board);
		//System.out.println("guessing first: "+currState.row +" "+currState.col+" "+currState.color);
		if (ans && !isSolved(board)) {
			currState.guessedFirstColor = true;
			nextState = new JCBoardState(bestRow, bestCol, bestColor);
			board[bestRow][bestCol] = (byte) bestColor;
			searchState.add(nextState);
			search();
		}
		if ((maxSolutions > 0) && (numSolutions >= maxSolutions))
			return true;

		// try guessing the opposite color
		currState.revertChanges(board);

		board[currState.row][currState.col] = (byte) (1 - currState.color); // set opposite color
		currState.guessedBothColors = true;
		ans = solveBoard(true, currState.row, currState.col);

		if ((maxSolutions > 0) && (numSolutions >= maxSolutions))
			return true;

		if (isSolved(board) && ans) {
			sol = new byte[board.length][board[0].length];
			multiArrayCopy(board, sol);
			solutionList.add(sol);
			numSolutions++;
			if (depth < minDepth)
				minDepth = depth;
			//System.out.println(depth);
		}

		currState.setChangedCells(boardBeforeIteration, board);
		//System.out.println("guessing opposite: "+currState.row +" "+currState.col+" "+(1 - currState.color));
		if (ans && !isSolved(board)) {
			nextState = new JCBoardState(bestRow, bestCol, bestColor);
			board[bestRow][bestCol] = (byte) bestColor;
			searchState.add(nextState);
			search();
		}

		searchState.remove(searchState.size() - 1);
		depth--;
		currState.revertChanges(board);
		return true;

	}

	/**
	 * solves the crossword board, using contradictions only (no depth search)
	 *
	 * @return
	 */
	public boolean solveBoard(boolean initCell, int rowInit, int colInit) {

		boolean ans;

		multiArrayCopy(board, oldBoard);
		multiArrayCopy(board, boardBeforeIteration);

		// first, solve the board without any guessing
		dls.updateSolver(board);
		dls.solveCrosswordByLines(initCell, rowInit, colInit);

		lastMoveWasGuess = false;
		while (!lastMoveWasGuess) {

			decisions++;

			lastMoveWasGuess = true;

			for (int row = 0; row < board.length; row++)
				for (int col = 0; col < board[0].length; col++) {
					alreadyProbed[0][row][col] = false;
					alreadyProbed[1][row][col] = false;
				}

			prevBestRow = bestRow;
			prevBestCol = bestCol;

			currScore = 0;
			maxScore = 0;

			// first, we try the cells surrounding our last guess. they should be good candidates
			if (probeImmidiateNeighbours()) {
				countImmidiateNeigbours++;
				lastMoveWasGuess = false;
			}
			// probe neighbours of cells, that were changed in the last iteration.
			else if (probeChangedCells()) {
				countChangedCells++;
				lastMoveWasGuess = false;
			}
			// probe all cells with at least 2 neighbours
			else if (probeKnownNeighbours(2, 4)) {
				countTwoKnown++;
				lastMoveWasGuess = false;
			}
			// probe all cells with 1 neighbour
			else if (probeKnownNeighbours(1, 1)) {
				countOneKnown++;
				lastMoveWasGuess = false;
			}

			// if probing fails - we stop the iteration and guess a cell
			if (lastMoveWasGuess) {
				countGuessing++;
				break;
			}

			// make the correct guess and run the line solver.
			multiArrayCopy(board, oldBoard);
			board[bestRow][bestCol] = (byte) (1 - bestColor); // guess the opposite color (because this color created a contradiction
			dls.updateSolver(board);

			ans = dls.solveCrosswordByLines(true, bestRow, bestCol);

			//if a real contradiction was found - board is not solvable! (since we know the other color also leads to a contradiction - that is why we chose it in the first place
			if (!ans) {
				return false;
			}
		}

		return true;
	}

	/**
	 * calculates the score of a board. the score is the number of solved cell in the board.
	 *
	 * @return
	 */
	private int calcScore(byte[][] board) {
		int score = 0;
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if (board[i][j] != 2)
					score++;
		return score;
	}

	/**
	 * calculates the number of known (i.e solved) neighbours of a cell
	 *
	 * @param row the cell's row
	 * @param col the cell's column
	 * @return number of neighbouring cells which have their value
	 */
	private final int numKnownNeighbours(byte[][] board, int row, int col) {

		int count = 0;
		if ((row == 0) || board[row - 1][col] != 2)
			count++;
		if ((row == board.length - 1) || board[row + 1][col] != 2)
			count++;
		if ((col == 0) || board[row][col - 1] != 2)
			count++;
		if ((col == board[0].length - 1) || board[row][col + 1] != 2)
			count++;
		return count;
	}

	private boolean probeImmidiateNeighbours() {
		if (prevBestRow != -1)
			if (probeNeighbours(prevBestRow, prevBestCol, (byte) 0))
				return true;
			else if (probeNeighbours(prevBestRow, prevBestCol, (byte) 1))
				return true;
		return false;
	}

	/**
	 * probe cells that were changed in the last iteration
	 *
	 * @return true, if a contradiction was found
	 */
	private boolean probeChangedCells() {

		// probe with WHITE
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if ((oldBoard[i][j] == 2) && (board[i][j] != 2)) { // if the cell was solved in the last iteration
					if (probeNeighbours(i, j, (byte) 0))
						return true;
				}

		// probe with BLACK

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if ((oldBoard[i][j] == 2) && (board[i][j] != 2)) { // if the cell was solved in the last iteration
					if (probeNeighbours(i, j, (byte) 1))
						return true;
				}

		return false;
	}

	/**
	 * probe cells with a number of known neighbours (=solved cells) within a range, including the boundaries
	 *
	 * @param min minimum number of neighbours
	 * @param max maximum number of neighbours
	 * @return true if a contradiction was found
	 */
	private boolean probeKnownNeighbours(int min, int max) {
		int numNeighbours;
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++) {
				numNeighbours = numKnownNeighbours(board, i, j);
				if ((numNeighbours >= min) && (numNeighbours <= max)) {
					if (probeVariable(i, j, (byte) 0)) return true;
					if (probeVariable(i, j, (byte) 1)) return true;
				}
			}
		return false;
	}

	/**
	 * probe neighbours with a specific color
	 *
	 * @param row, col variables index
	 * @param color color to probe with. color 2 means we try both colors - and search for a DOUBLE contridiction
	 * @return
	 */
	private boolean probeNeighbours(int row, int col, byte color) {

		// if color is 2 - it means we try to find a double contradiction (both with black and with white)
		if (color == 2) {
			if (probeVariable(row - 1, col, (byte) 0) && probeVariable(row - 1, col, (byte) 0)) {
				//System.out.println("double");
				return true;

			}
			if (probeVariable(row + 1, col, (byte) 0) && probeVariable(row + 1, col, (byte) 0)) {
				//System.out.println("double");
				return true;
			}
			if (probeVariable(row, col - 1, (byte) 0) && probeVariable(row, col - 1, (byte) 0)) {
				//System.out.println("double");
				return true;
			}
			if (probeVariable(row, col + 1, (byte) 0) && probeVariable(row, col + 1, (byte) 0)) {
				//System.out.println("double");
				return true;
			}
		} else {
			if (probeVariable(row - 1, col, color)) return true;
			if (probeVariable(row + 1, col, color)) return true;
			if (probeVariable(row, col - 1, color)) return true;
			if (probeVariable(row, col + 1, color)) return true;
		}
		return false;

	}

	/**
	 * guess a cell, and apply the line solver. if a contradiction is reached - return true.
	 *
	 * @param row   row of cell to test
	 * @param col   col of cell to test
	 * @param color color of cell to test
	 * @return true if a contradiction was found, false otherwise
	 */
	private boolean probeVariable(int row, int col, byte color) {

		if ((row < 0) || (row >= board.length) || (col < 0) || (col >= board[0].length)
				|| alreadyProbed[color][row][col])
			return false;

		if (board[row][col] != 2) // if the cell's color is already known
			return false;

		multiArrayCopy(board, newBoard);

		// make the guess
		newBoard[row][col] = color;

		// solve using the line solver
		dls.updateSolver(newBoard);
		cons = dls.solveCrosswordByLines(true, row, col);

		alreadyProbed[color][row][col] = true;
		nodes++;

		// if a contradiction has been reached
		if (!cons) {
			bestRow = row;
			bestCol = col;
			bestColor = color;
			return true;
		}

		// if a contradiction was not found - calculate score and update if maximum is found
		currScore = dls.getNumCellsSolved();
		if ((currScore >= maxScore)) {
			bestRow = row;
			bestCol = col;
			bestColor = color;
			maxScore = currScore;
		}

		return false;
	}


	public void readFromFile(String filename) {

		String lines[] = new String[100];

		int[] dimensions = new int[2];

		/* read from file args[0] or qcp.txt */
		try {

			BufferedReader in = new BufferedReader(new FileReader(filename));
			String str;

			str = in.readLine();

			Pattern pat = Pattern.compile(" ");
			String[] result = pat.split(str);

			int current = 0;
			for (int j = 0; j < result.length; j++)
				try {
					int currentNo = new Integer(result[j]);
					dimensions[current++] = currentNo;
				} catch (Exception ex) {

				}

			lines = new String[dimensions[0] + dimensions[1]];

			int n = 0;

			while ((str = in.readLine()) != null && n < lines.length) {
				lines[n] = str;
				n++;
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("I can not find file " + filename);
		} catch (IOException e) {
			System.err.println("Something is wrong with file" + filename);
		}

		rowBlocks = new byte[dimensions[1]][];
		colBlocks = new byte[dimensions[0]][];

		// Transforms strings into ints
		for (int i = 0; i < lines.length; i++) {

			Pattern pat = Pattern.compile(" ");
			String[] result = pat.split(lines[i]);

			byte[] sequence = new byte[result.length];

			int current = 0;
			for (int j = 0; j < result.length; j++)
				try {
					sequence[current++] = Byte.valueOf(result[j]);
				} catch (Exception ex) {
				}

			// check if sequence already exists. if so, we'll use the same pointer for the "blocks" array
			// we do this because we'll want to compare block addresses when taking out elements from the hash
			boolean foundPrev = false;
			if (i < rowBlocks.length) {
				for (int k = 0; k < i; k++)
					if (Arrays.equals(rowBlocks[k], sequence)) {
						rowBlocks[i] = rowBlocks[k];
						foundPrev = true;
						break;
					}
				if (!foundPrev)
					rowBlocks[i] = sequence;
			} else {
				for (int k = rowBlocks.length; k < i - rowBlocks.length; k++)
					if (Arrays.equals(colBlocks[k], sequence)) {
						colBlocks[i - rowBlocks.length] = colBlocks[k];
						foundPrev = true;
						break;
					}
				if (!foundPrev)
					colBlocks[i - rowBlocks.length] = sequence;
			}
		}

	}

	public static int main(String[] args) {

		return runCommandLineMode(args);
	}

	byte[][] getBoardContents() {
		return board;
	}

	/**
	 * sets the search timeout value.
	 * after this time has elapsed, search will stop
	 *
	 * @param value timeout value, in seconds.
	 */
	public void setTimeout(int value) {
		this.timeout = value;
	}

	/**
	 * sets the number of maximum solutions to search for. when this number is reached, search will stop
	 *
	 * @param value max solutions to search for
	 */
	public void setMaxSolutions(int value) {
		this.maxSolutions = value;
	}


	/**
	 * deep copy of a two dimensional array
	 *
	 * @param source      source array to copy from.
	 * @param destination destination array to copy to.
	 */
	public void multiArrayCopy(byte[][] source, byte[][] destination) {
		for (int a = 0; a < source.length; a++) {
			System.arraycopy(source[a], 0, destination[a], 0, source[a].length);
		}
	}

	/**
	 * returns true is the board is solved. false otherwise
	 *
	 * @param board
	 * @return
	 */
	private boolean isSolved(byte[][] board) {
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if (board[i][j] == 2)
					return false;
		return true;
	}

	/**
	 * verifies that the solution list is valid
	 * checks that all solutions are CORRECT and UNIQUE (i.e the list does not contain the same solution twice)
	 *
	 * @param solutionList solution list to test
	 * @return "true" if the solution list has been found valid.
	 */
	private boolean verifySolutions(ArrayList<byte[][]> solutionList) {
		byte[][] sol;
		for (int i = 0; i < solutionList.size(); i++) {
			sol = solutionList.get(i);

			// verify that solution is CORRECT
			if (!verifySolution(sol, rowBlocks, colBlocks))
				return false;

			// verify that solution is unique - does not appear previously in the list
			for (int j = 0; j < i; j++)
				if (boardsEqual(sol, solutionList.get(j)))
					return false;
		}

		return true;
	}

	/**
	 * verifies that a solution of the board is correct
	 * it is done by reconstruction the blocks according to the board's contents, and comparing them to the original blocks of the board
	 *
	 * @param board     board to test
	 * @param rowBlocks row clues for each row
	 * @param colBlocks column clues for each column
	 * @return
	 */
	private boolean verifySolution(byte[][] board, byte[][] rowBlocks, byte[][] colBlocks) {
		for (int row = 0; row < board.length; row++) {
			if (!Arrays.toString(rowBlocks[row]).equals(Arrays.toString(generateRowClues(board[row]))))
				return false;
		}

		for (int col = 0; col < board[0].length; col++) {
			if (!Arrays.toString(colBlocks[col]).equals(Arrays.toString(generateRowClues(convertCol(col, board)))))
				return false;
		}

		return true;
	}

	/**
	 * converts a board column into a row
	 */
	private static byte[] convertCol(int selectedLine, byte[][] board) {
		byte[] tempRow = new byte[board.length];
		for (int i = 0; i < board.length; i++)
			tempRow[i] = board[i][selectedLine];
		return tempRow;
	}

	/**
	 * receives a nonogram line, and returns the clues (eg. the numbers next to each line)
	 */
	private static byte[] generateRowClues(byte[] line) {
		byte[] temp = new byte[line.length];

		int currClueLength = 0;
		int currClueIndex = 0;
		for (int i = 0; i < line.length; i++) {

			if (line[i] == 0) {
				if (currClueLength > 0) {
					temp[currClueIndex] = (byte) currClueLength;
					currClueIndex++;
					currClueLength = 0;
				}
			} else {
				currClueLength++;
				if (i == (line.length - 1)) {
					temp[currClueIndex] = (byte) currClueLength;
					currClueIndex++;
				}
			}
		}

		// empty row - generate a "0" block, to match the clues.
		if (currClueIndex == 0) {
			temp[currClueIndex] = 0;
			currClueIndex++;
		}
		byte[] ans = new byte[currClueIndex];
		System.arraycopy(temp, 0, ans, 0, currClueIndex);
		return ans;
	}

	/**
	 * checks if two boards are equal (all cells have the same values)
	 */
	private static boolean boardsEqual(byte[][] first, byte[][] second) {
		if (first.length != second.length) return false;
		if (first[0].length != second[0].length) return false;

		for (int i = 0; i < first.length; i++)
			for (int j = 0; j < second.length; j++)
				try {
					if (first[i][j] != second[i][j])
						return false;
				} catch(ArrayIndexOutOfBoundsException aioobe) {
					aioobe.printStackTrace();
					return false;
				}

		return true;
	}

	/**
	 * It executes the program which solves this simple problem.
	 *
	 * @param args no arguments are read.
	 */
	public static int runCommandLineMode(String args[]) {

		String filename = null;
		int maxSolutions = 2;
		int timeoutValue = 0;
		boolean shortPrint = false;
		long startTime = System.currentTimeMillis();
		// parse command line arguments 
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-file")) {
				if (i == (args.length - 1))
					System.out.println("Please specify a file name");
				else
					filename = args[i + 1];
			} else if (args[i].equals("-maxsolutions")) {
				if (i == (args.length - 1))
					System.out.println("Please specify the maximum number of solutions");
				else
					try {
						maxSolutions = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						System.out.println("Could not parse number of solutions");
					}
			} else if (args[i].equals("-timeout")) {
				if (i == (args.length - 1))
					System.out.println("Please specify the timeout value");
				else
					try {
						timeoutValue = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						System.out.println("Could not parse the timeout value");
					}
			} else if (args[i].equals("-shortprint")) {
				shortPrint = true;
			} else if (args[i].equals("-help")) {
				printHelpMenu();
			}
		}
		int out = -1;
		// run the solver
		if (filename != null)
			out = runBoardFromFile(filename, maxSolutions, timeoutValue, shortPrint, startTime);
		else
			printHelpMenu();
		return out;
	}

	private static void printHelpMenu() {
		System.out.println(
				"\nBGU Nonogram solver\n" +
						"-------------------\n\n" +
						"Command line options:\n" +
						"-help \t\t Displays the help menu.\n" +
						"-file \t\t Load nonogram from file. The file should be in .nin format.\n" +
						"-maxsolutions \t Sets the maximum number of solutions that the solver should search for (after finding them, search will stop). Default value is 2. Entering 0 will search for all solutions.\n" +
						"-timeout \t Set the timeout value (in seconds). When this time limit is reached, the search will stop.\n" +
						"-shortprint \t Prints the solution details in a short, single line format.\n" +
						"\n"
		);

	}

	private static int runBoardFromFile(String filename, int maxSolutions, int timeoutValue, boolean shortPrint, long startTime) {
		//if (!shortPrint)
			//System.out.println("solving nonogram: " + filename);

		JCIndependantSolver ndls = new JCIndependantSolver(filename);
		ndls.setStartTime(startTime);
		ndls.setMaxSolutions(maxSolutions);
		ndls.setTimeout(timeoutValue);

		ndls.initSearch();
//		ndls.ssl.printStats();
//		ndls.dls.printLIHist();

		ndls.setEndTime(System.currentTimeMillis());

		//ndls.printSolutionInfo(shortPrint);

		boolean ans = ndls.verifySolutions(ndls.solutionList);
		if(!ans)
			System.out.println("solutions is wrong!");
		return (ndls.numSolutions);
	}

	private void printSolutionInfo(boolean shortPrint) {
		String delim;
		if (shortPrint)
			delim = "\t";
		else
			delim = "\n";

		StringBuffer buf = new StringBuffer();

		buf.append("Solutions : ").append(this.numSolutions).append(delim);
		buf.append("Time: ").append(this.endTime - this.startTime).append(delim);
		buf.append("Nodes : ").append(this.nodes).append(delim);
		buf.append("Max depth: ").append(this.maxDepth).append(delim);
		buf.append("Min solution depth: ").append(this.minDepth).append(delim);
		buf.append("Decisions : ").append(this.decisions).append(delim);
		buf.append("Count immediate neighbours: ").append(this.countImmidiateNeigbours).append(delim);
		buf.append("Count changed cells: ").append(this.countChangedCells).append(delim);
		buf.append("Count two known: ").append(this.countTwoKnown).append(delim);
		buf.append("Count one known: ").append(this.countOneKnown).append(delim);
		buf.append("Count guesses: ").append(this.countGuessing).append(delim);
		buf.append("LineSolver hash hits: ").append(this.dls.getHits()).append(delim);
		buf.append("LineSolver hash misses: ").append(this.dls.getMisses()).append(delim);

		System.out.println(buf);

		if (!shortPrint && !solutionList.isEmpty())
			printMatrix(solutionList.get(0));
	}

	private void setEndTime(long endTime) {
		this.endTime = endTime;

	}

	private void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void printMatrix(byte[][] matrix) {

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] == 1)
					System.out.print("#");
				else if (matrix[i][j] == 0)
					System.out.print(" ");
				else
					System.out.print("?");
			}
			System.out.println("");
		}
	}

	/**
	 * returns a solution if one exists.
	 */
	public byte[][] getSolution() {
		return (solutionList.get(0));
	}

	/**
	 * returns true is a solution exists, false otherwise
	 */
	public boolean hasSolution() {
		return !solutionList.isEmpty();
	}
}

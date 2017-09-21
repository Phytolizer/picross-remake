package bgusolver; /**
 * This class solves a JCW puzzle line-by-line. each line is solved by a dynamic
 * progamming algorithm.
 * @author ben raziel
 *
 */
//import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
public class JCDynamicLineSolver {
	
	public static final boolean ROW_REQUEST = true;
	public static final boolean COL_REQUEST = false;
	// Variabes
	private JCSolveSingleLineTOPDOWN ssl;
	private byte boardContents[][];
	private PriorityQueue<JCLineRequest> linesToSolve;
	private int numRows, numCols;
	private byte rowBlocks[][], columnBlocks[][];
	private int rowBlockSums[][], columnBlockSums[][];
	
	private int numCellsSolved;
	private int numLinesTried;
	private int rowCellsSolved[], colCellsSolved[]; // number of cells already solved for each row/col
	private JCLineRequest rowLineRequests[], colLineRequests[];
	private LinkedHashMap<JCLineHashKey, JCLineHashValue> hashTable;
	private JCLineHashKey hashKey;
	private JCLineHashValue hashValue;
	private byte[] solvedLine;
	private int hits;
	private int misses;
	private byte[] tempRow;
	
	public double time1, time2, time3;
	private byte[] line;
	private byte[] blocks;
	byte[] temp;
	private StringBuilder sb;
	private int[] blockSums;
	
	private int count;
	private int trueCount, falseCount;
	int iterCount;
	
	/**
	 * getBoardContents - getter
	 * return the board's contents (status of every cell) 
	 */
	public byte[][] getBoardContents(){
		return (byte[][])boardContents.clone();
	}
	
	/** 
	 * getNumRows - getter
	 * returns the number of rows in the crossword
	 */
	public int getNumRows(){
		return this.numRows;
	}
	
	/** 
	 * getNumCols - getter
	 * returns the number of columns in the crossword
	 */
	public int getNumCols(){
		return this.numRows;
	}
	
	/**
	 * gets the number of hits - lines solved from the hash
	 */
	public double getHits(){
		return this.hits;
	}
	
	
	/**
	 * gets the number of misses - lines which were not solved by looking up in hash
	 */
	public double getMisses(){
		return this.misses;
	}
	
	
	/**
	 * setGuess - sets the guess flag on/off
	 * used for logging purposes - so that we'll be able to log if lineSolver is currently
	 * solving a board based on a guess or not
	 */
//	public void setGuess(boolean mode, int color){
//		this.guess = mode;
//		this.guessColor = color;
//	}
//	
	public int getNumCellsSolved(){
		return this.numCellsSolved;
	}
	
	public JCDynamicLineSolver(byte boardContents[][], byte rowBlocks[][], byte columnBlocks[][], int numRows, int numCols, JCSolveSingleLineTOPDOWN ssl){
		this(boardContents, rowBlocks, columnBlocks, numRows, numCols, 0, ssl);
	}
	
	/**
	 * Default constructor
	 * @param boardContents describes the state of each pixel on the board (empty, full or unknown)
	 * @param rowBlocks describes the blocks that should be placed in each row
	 * @param columnBlocks describes the blocks that should be placed in each column
	 * @param numRows number of rows
	 * @param numCols number of columns
	 * @param solver the solver
	 * @param animateDelay the delay time between each line
	 */
	JCDynamicLineSolver(byte boardContents[][], byte rowBlocks[][], byte columnBlocks[][], int numRows, int numCols, int animateDelay, JCSolveSingleLineTOPDOWN ssl){
		
		linesToSolve = new PriorityQueue<JCLineRequest>(100, new JCLineRequestComperator());
		this.numRows = numRows;
		this.numCols = numCols;
		
		this.rowBlocks = rowBlocks;
		this.columnBlocks = columnBlocks;
		
		this.rowBlockSums = new int[rowBlocks.length][];
		this.columnBlockSums = new int[columnBlocks.length][];
		
		// calculate partial row block sums, and column block sums
		for (int i=0; i < rowBlocks.length; i++){
			this.rowBlockSums[i] = ssl.calcBlockSum(rowBlocks[i]);
		}
			
		for (int i=0; i < columnBlocks.length; i++){
			this.columnBlockSums[i] = ssl.calcBlockSum(columnBlocks[i]);
		}

		if (boardContents != null)
		this.boardContents = (byte[][])boardContents.clone();
		this.ssl = ssl;
		
		final int MAX_ENTRIES = 200000;
		
		this.hashTable = new LinkedHashMap(MAX_ENTRIES+1, .75F, false)  
        {  
            protected boolean removeEldestEntry(Map.Entry eldest)  
            {  
                   return size() > MAX_ENTRIES;                                   
             }  
        };  
		tempRow = new byte[numRows];
		sb = new StringBuilder();

		rowLineRequests = new JCLineRequest[numRows];
		colLineRequests = new JCLineRequest[numCols];
		
		for (int i=0; i < numRows; i++)
			rowLineRequests[i] = new JCLineRequest(ROW_REQUEST, i);
		
		for (int j=0; j < numCols; j++)
			colLineRequests[j] = new JCLineRequest(COL_REQUEST, j);
	}
	
	public void updateSolver(byte boardContents[][]){
		this.boardContents = boardContents;
		this.numCellsSolved = 0;

	}
	/**
	 * Solves the JC line by line, each time a line gives a new info, then the affected cols/rows 
	 * will be checked again until no more data could be revealed from the puzzle.
	 * @param initWithCell - start the solver using only row/col supplied in the lineRequests queue. this is useful when making a guess
	 * @param row - row to start solving
	 * @param col - col to start solving
	 */
	public boolean solveCrosswordByLines(boolean initWithCell, int row, int col){
		
		iterCount++;
		boolean result = true;
		
		if (initWithCell){
			//System.out.println("solving, init cell: "+row+ ","+col);
			
			rowLineRequests[row].incNumCellsChanged();
			colLineRequests[col].incNumCellsChanged();
			
			// insert row and col to the priority queue
			linesToSolve.add(rowLineRequests[row]);
			linesToSolve.add(colLineRequests[col]);
		}
		
		else {
			// Adding all rows
			for (int i = 0; i < numRows; i++) {
				linesToSolve.add(rowLineRequests[i]);
			}
			
			// Adding all cols
			for (int i = 0; i < numCols; i++) {
				linesToSolve.add(colLineRequests[i]);
			}
		}
		
		// As long as we have lines to process
		while (!linesToSolve.isEmpty()) {
			
			JCLineRequest current = linesToSolve.remove();
			
			// Solve the row or col
			if (current.isRow){
				
				rowLineRequests[current.lineNumber].setNumCellsChanged(0);
				result = solveLine(ROW_REQUEST, current.lineNumber);
			}
			else{
				colLineRequests[current.lineNumber].setNumCellsChanged(0);
				result = solveLine(COL_REQUEST, current.lineNumber);
			}
			
			// If there is no solution, stop the process.
			if (!result) {
				linesToSolve.clear();
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Solves a row and then update the board
	 * @param isRow "true" if we're solving a row, "false" if we're solving a col 
	 * @param selectedLine which row/col to solve
	 * @return true is a solution was found, false if line cant be solved
	 */
	private boolean solveLine(boolean isRow, int selectedLine) {
		
		
		this.numLinesTried++;
		
		if (isRow){
			line = boardContents[selectedLine];
			blocks = rowBlocks[selectedLine];
			blockSums = rowBlockSums[selectedLine];
		}
		else {
			line = convertCol(selectedLine);
			blocks = columnBlocks[selectedLine];
			blockSums = columnBlockSums[selectedLine];
		}
		
		hashKey = new JCLineHashKey(line, blocks);
		hashValue = hashTable.get(hashKey);
		if (hashValue != null){
			
			hits++;

			// line doesn't have a solution
			if (!hashValue.hasSolution)
				return false;
			
			// line has a solution
			solvedLine = hashValue.solvedLine;
			if (hashValue.numCellsSolved != 0){
				updateBoard(isRow, selectedLine, solvedLine);	
				this.numCellsSolved += hashValue.numCellsSolved;
			}
			
			return true;
		
		}
		else {
			
			misses++;
			
			ssl.updateSolver(line, blocks, blockSums, isRow, null, null, 0);
			
			solvedLine = ssl.solveLine();				
						
			// line doesn't have a solution, or no new information was revealed about the line by solving it
			if (!ssl.hasSolution || ssl.numCellsSolved == 0){
				hashTable.put(hashKey, new JCLineHashValue(null, ssl.hasSolution, 0));
				return ssl.hasSolution;
			}
			
			// line has a solution, which reveals new information about the line
			updateBoard(isRow, selectedLine, solvedLine);
			this.numCellsSolved += ssl.numCellsSolved;

			hashTable.put(hashKey, new JCLineHashValue((byte[])solvedLine.clone(), true, ssl.numCellsSolved));
			return true;	
			
			}
	}

	/**
	 * Updates a row/col, according to the solution of the line solver.
	 * It also adds tasks to the line solver - if we determined a row cell to be black/white
	 * then we add a column task to this cell
	 * @param isRow "true" if we're solving a row, "false" if we're solving a col 
	 * @param selectedLine which row/col to solve
	 * @param solvedLine the answer from the line solver
	 */
	private void updateBoard(boolean isRow, int selectedLine, byte[] solvedLine) {
		
		if (isRow){
			for (int i=0; i<solvedLine.length; i++)		
				if ((solvedLine[i] != 2) && (boardContents[selectedLine][i] == 2)) { // if we solved a cell
						
						boardContents[selectedLine][i] = solvedLine[i];	// update the row
						
	    				// Adding a job for the queue - since new data was found.
	    				addLine(false, i);
				}
		}
		
		else {
			for (int i=0; i<solvedLine.length; i++)			
				if ((solvedLine[i] != 2) && (boardContents[i][selectedLine] == 2)) {
					
					boardContents[i][selectedLine] = solvedLine[i];
					
    				// Adding a job for the queue - since new data was found.
    				addLine(true, i);
				}
		}
		
		// If there was an update, show it.
//		if ((animateDelay != 0) && (ans > 0)) {
//			try {
//    			// update the crossword table, and repaint it
//    			//solver.updateCrossWordTable(boardContents);
//    			//solver.repaint();
//				Thread.sleep(animateDelay);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	/**
	 * adds a line to the linesToSolve queue (and updates the rating)
	 * @param b
	 * @param i
	 */
	private void addLine(boolean isRow, int lineNumber) {
		
		JCLineHashKey key;
		JCLineHashValue val;
		if (isRow){
			// if the row is already in the queue: remove it, increase rating and re-insert it.
			if (linesToSolve.remove(rowLineRequests[lineNumber]))
				rowLineRequests[lineNumber].incNumCellsChanged();
			/*
			// calc hash value for row
			line = boardContents[lineNumber];
			blocks = rowBlocks[lineNumber];
			key = new JCLineHashKey(line, blocks);
			val = hashTable.get(key);
			rowLineRequests[lineNumber].calcRating(val);
			*/
			
			linesToSolve.add(rowLineRequests[lineNumber]);
		} 
		
		else { // same thing, for columns

			if (linesToSolve.remove(colLineRequests[lineNumber]))
				colLineRequests[lineNumber].incNumCellsChanged();
			
			/*
			// calc hash value for col
			line = convertCol(lineNumber);
			blocks = columnBlocks[lineNumber];
			key = new JCLineHashKey(line, blocks);
			val = hashTable.get(key);
			colLineRequests[lineNumber].calcRating(val);
			*/
			
			linesToSolve.add(colLineRequests[lineNumber]);
		}
							
	}

	/**
	 * Converts a board column vector into a row vector
	 * @param selectedLine column to convert
	 * @return a row vector
	 */
	private byte[] convertCol(int selectedLine) {
		for(int i=0; i<numRows; i++)
			tempRow[i] = boardContents[i][selectedLine];
		return tempRow;
	}
	
	public int[] getRowCellsSolved() {
		return this.rowCellsSolved;
	}
	
	public int[] getColCellsSolved() {
		return this.colCellsSolved;
	}
	
	/**
	 * the Arrays.toString() method, simply without the brackets, and creating one string
	 * this is done to speed up hashing
	 */
    private final String lineToString(byte[] line, byte[] blocks) {
    		
    		int i;
            sb.setLength(0);
            for (i = 0; i < blocks.length; i++){
                sb.append((char)blocks[i]);
            	// we need to seperate blocks, because we can have a clue which takes two chars!
                //b.append((char)65);
            }
            // we append a seperator between blocks and row. we need this to avoid weird mistakes
            sb.append((char)66);
            
    		for (i = 0; i < line.length; i++){
    			sb.append((char)line[i]);
    		}

    		//System.out.println(b.toString());
    		return sb.toString();

        }
    
    private final void printLineStats(byte[] line, byte[] blocks, boolean newInfo){
    	int lineLength = line.length;
    	int numBlocks = blocks.length;
    	int sumBlocks = 0;
    	int largest = 0;
    	count++;
    	for (byte b: blocks){
    		sumBlocks += b;
    		if (b > largest)
    			largest = b;
    	}
    	float avgBlocks = (float)sumBlocks / (float)numBlocks;
    	int numCellsWhite = 0;
    	int numCellsBlack = 0;
    	int numCellsUnknown = 0;
    	for (int cell = 0; cell < line.length; cell++)
    		switch (line[cell]) {
			case 0: {numCellsWhite++; break;}
			case 1: {numCellsBlack++; break;}
			case 2: {numCellsUnknown++; break;}
			default: break;
			}
    	
    	int firstBlackIndex = -1;
    	int lastBlackIndex = -1;
    	for (int cell = 0; cell < line.length; cell++){
    		if (line[cell] == 1){
    			firstBlackIndex = cell;
    			break;
    		}
    	}
    	
    	for (int cell = line.length -1; cell >= 0; cell--){
    		if (line[cell] == 1){
    			lastBlackIndex = cell;
    			break;
    		}
    	}
    	
    	System.out.println(	
    						//Arrays.toString(blocks)+"\t"+
    						//Arrays.toString(line)+"\t"+
    						count +"\t"+
    						lineLength +"\t"+ 
    						numBlocks +"\t"+ 
    						largest +"\t"+ 
    						sumBlocks +"\t"+ 
    						avgBlocks +"\t"+ 
    						numCellsWhite +"\t"+ 
    						numCellsBlack +"\t"+ 
    						numCellsUnknown +"\t"+
    						firstBlackIndex +"\t"+
    						lastBlackIndex +"\t"+
    						newInfo
    						);
    						
    	
    	
    	
    }
	 
}

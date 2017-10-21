package bgusolver;

/**
 * used to store information about a solved line
 * @author ben
 */
public class JCLineHashValue {
	// the contents of the solved line
	public byte[] solvedLine;
	
	// indicates if the line has a solution (or not)
	public boolean hasSolution;
	
	// indicates the number of solved cells
	public int numCellsSolved;

	public JCLineHashValue(byte[] solvedLine, boolean hasSolution, int numCellsSolved){
		this.solvedLine = solvedLine;
		this.hasSolution = hasSolution;
		this.numCellsSolved = numCellsSolved;
	}

}

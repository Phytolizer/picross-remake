package bgusolver;

import javax.naming.ldap.HasControls;

public class JCLineRequest {

	public int lineNumber;
	public boolean isRow;
	public int numCellsChanged;
	public int rating;
	
	public JCLineRequest(boolean isRow, int lineNumber) {
		this.lineNumber = lineNumber;
		this.isRow = isRow;
		this.numCellsChanged = 0;
	}
	
	public int getNumCellsChanged() {
		return this.numCellsChanged;
	}
	
	public void setNumCellsChanged(int num) {
		this.numCellsChanged = num;
	}
	
	public void incNumCellsChanged(){
		this.numCellsChanged++;
	}
	
	
	/**
	 * calculates the rating of a line, according to the formula
	 * 1000 * H * S + N
	 * where
	 * H = 1 if line is in the hash, 0 otherwise
	 * S = number of cells solved by the linesolver for this line (only for lines in the hash)
	 * N = number of cells solved since we last tried to solve this line
	 * @return the line's rating
	 */
	public void calcRating(JCLineHashValue hashValue){
		if (hashValue != null){
			if (hashValue.numCellsSolved == 0)
				this.rating = -1;
			else
				this.rating = 1000*hashValue.numCellsSolved + this.numCellsChanged;
			
		}
		else
			this.rating = this.numCellsChanged;
	}
	
	
	/**
	 * used to compare two Line requests. notice that we DO NOT compare that numCellsChanged are equal
	 * @param other lineRequest to compare with.
	 * @return "true" if the requests are of the same line, false otherwise
	 */
	public boolean equals (JCLineRequest other){
		return ((this.lineNumber == other.lineNumber) && (this.isRow == other.isRow));
	}
}

package bgusolver;

import java.util.ArrayList;

/**
 * used to store the state of the board, when doing a DFS search
 */
public class JCBoardState {

    // tells us if we've already guessed black/white or not
    public boolean guessedBothColors;
    public boolean guessedFirstColor;
    public int row, col, color;

    /**
     * cells that were changed by our guess. in order to reconstruct the board before the guess
     * we need to turn all these cells back to "unknown" (2).
     */
    private ArrayList<short[]> changedCells;

    public JCBoardState(int row, int col, int color) {
        changedCells = new ArrayList<>();
        this.row = row;
        this.col = col;
        this.color = color;
        addCell(row, col);
    }

    /**
     * given two board, this will generate the changedCells list (all cells that were determined in the "after" board
     * compared to the "before" board.
     *
     * @param before board before iteration
     * @param after  board after iteration
     */
    public void setChangedCells(byte[][] before, byte[][] after) {

        changedCells = new ArrayList<>();
        for (int i = 0; i < before.length; i++) {
            for (int j = 0; j < before[0].length; j++) {
                if ((before[i][j] == 2) && (after[i][j] != 2)) {
                    addCell(i, j);
                }
            }
        }
        addCell(this.row, this.col);
    }

    public void addCell(int row, int col) {
        short[] pair = {(short) row, (short) col};
        changedCells.add(pair);
    }

    /**
     * reverts all changes done to the board during the current iteration
     * sets all changed cells back to "unknown"
     */
    public void revertChanges(byte[][] board) {
        for (short[] cell : changedCells) {
            board[cell[0]][cell[1]] = 2;
        }
        changedCells.clear();
    }

    public void clearChangedCells() {
        changedCells.clear();
    }

    public ArrayList getChangedCells() {
        return changedCells;
    }

}

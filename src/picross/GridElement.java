package picross;

import bgusolver.JCIndependantSolver;
import mygl.Element;
import mygl.Graphics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GridElement extends Element {
	private final double SOLUTION_DENSITY = 0.5;
	private int sizeX, sizeY;
	private Box[][] contents;
	private Box[][] solution;
	private Clue[] rowClues;
	private Clue[] colClues;


	public GridElement(int[] size, Graphics graphics) {
		super(graphics);
		if(size.length != 2) {
			throw new IllegalArgumentException("\'size\' must be of length 2, in the order: x, y");
		}
		sizeX = size[0];
		sizeY = size[1];
		contents = new Box[sizeY][sizeX];
		solution = new Box[sizeY][sizeX];
		for (int i = 0; i < contents.length; i++) {
			for (int i1 = 0; i1 < contents[i].length; i1++) {
				contents[i][i1] = new Box();
				solution[i][i1] = new Box();
			}
		}
		rowClues = new Clue[sizeY];
		for (int i = 0; i < rowClues.length; i++) {
			rowClues[i] = new Clue(solution[i], ClueType.ROW);
		}
		colClues = new Clue[sizeX];
		for(int i = 0; i < colClues.length; i++) {
			Box[] col = new Box[solution.length];
			for (int i1 = 0; i1 < solution.length; i1++) {
				Box[] row = solution[i1];
				col[i1] = row[i];
			}
			colClues[i] = new Clue(col, ClueType.COL);
		}
	}

	public void generate() {
		try {
			Random random = new Random();

			int numSolutions;
			do {
				for(Box[] row : solution) {
					for(Box b : row) {
						b.setState((random.nextDouble() < SOLUTION_DENSITY) ? BoxState.CORRECT  : BoxState.INCORRECT);
					}
				}
				for(Clue c : rowClues) {
					c.refresh();
				}
				for(Clue c : colClues) {
					c.refresh();
				}
				FileWriter fw = new FileWriter("clues.nin");
				fw.write("" + sizeX + " " + sizeY + "\n");
				for (Clue c : rowClues) {
					fw.write(c.getClue() + "\n");
				}
				for (Clue c : colClues) {
					fw.write(c.getClue() + "\n");
				}
				fw.close();
				numSolutions = JCIndependantSolver.main(new String[]{"-file", "clues.nin"});
				System.out.print("Solutions: " + numSolutions);
				switch (numSolutions) {
					case 0:
						System.out.println(" (not solvable)");
						break;
					case 1:
						System.out.println(" (uniquely solvable!)");
						break;
					default:
						System.out.println(" (not uniquely solvable)");
				}
			} while(numSolutions != 1);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generate(File file) {

	}


}

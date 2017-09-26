package picross;

import bgusolver.JCIndependantSolver;
import mygl.DrawingTools;
import mygl.Element;
import mygl.Graphics;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static java.awt.Color.*;

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
			new File("clues.nin").delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generate(File file) {

	}

	public void draw() {
		Graphics2D graphics2D = graphics.getGraphics2D();
		graphics2D.setColor(white);
		DrawingTools.fillRect(x, y, width, height, alignX, alignY, graphics2D);
	}

	private void adjustWidthAndHeight() {
		//TODO adjust width and height so the following is true:
		/*
		 * Grid and clues are completely within the bounding box
		 * Clues take up a percentage of the available space OR clues have a constant font size which scales the grid to fit in the remaining space
		 */

		//Depending on the look, this may change to a ratio which would decrease size of clues and grid together
		//When using a ratio, clues should have a maximum font size and a minimum font size that both will break the ratio but preserve normalcy
		float clueFontSize = 12;
		Graphics2D graphics2D = graphics.getGraphics2D();
		Font clueFont = graphics2D.getFont().deriveFont(clueFontSize);
		FontMetrics fm = graphics2D.getFontMetrics(clueFont);
		//This is stored in a variable to properly space out vertical clues.
		int spaceWidth = fm.stringWidth(" ");
		String[] rowClueStrings = new String[rowClues.length];
		for (int i = 0; i < rowClues.length; i++) {
			rowClueStrings[i] = rowClues[i].getFormattedClue();
		}
		String maxRowClueString = "";
		for(String s : rowClueStrings) {
			if(s.length() > maxRowClueString.length()) {
				maxRowClueString = s;
			}
		}
		int horizontalClueWidth = fm.stringWidth(maxRowClueString + " ");

	}

	public void setWidth(int width) {
		this.width = width;
		adjustWidthAndHeight();
	}

}

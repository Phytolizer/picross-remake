package picross;

import bgusolver.JCIndependantSolver;
import mygl.DrawingTools;
import mygl.Element;
import mygl.Graphics;
import mygl.Align;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import static java.awt.Color.*;

public class GridElement extends Element {
	private final double SOLUTION_DENSITY = 0.5;
	private int sizeX, sizeY;
	private Box[][] contents;
	private Box[][] solution;
	private Clue[] rowClues;
	private Clue[] colClues;
	private int gridWidth;
	private int gridHeight;
	private int gridXOffset = 0;
	private int gridYOffset = 0;
	private int horizontalClueWidth = 0;
	private int verticalClueHeight = 0;
	private float clueFontSize = 12;

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
		gridWidth = 0;
		gridHeight = 0;
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
		if(gridWidth == 0 || gridHeight == 0) {
			return;
		}
//		System.out.println("Are you even doing anything?");
		Graphics2D graphics2D = graphics.getGraphics2D();
		graphics2D.setColor(white);
		DrawingTools.fillRect(x + horizontalClueWidth + gridXOffset, y + verticalClueHeight + gridYOffset, gridWidth, gridHeight, alignX, alignY, graphics2D);
		graphics2D.setColor(black);
		DrawingTools.drawRect(x, y, width, height, alignX, alignY, graphics2D);
		double squareWidth = (double)gridWidth / sizeX;
		FontMetrics fm = graphics2D.getFontMetrics(graphics.getFont().deriveFont(clueFontSize));
		//draw clues
		for(int i = 0; i < sizeY; i++) {
			int currentYPos = y + verticalClueHeight + gridYOffset + (int)(squareWidth / 2 + squareWidth * i);
			DrawingTools.drawText(graphics.getFont().deriveFont(clueFontSize), rowClues[i].getFormattedClue() + " ", x + horizontalClueWidth + gridXOffset + 5, currentYPos, Align.RIGHT, Align.CENTER_VERTICAL, graphics2D);
//			System.out.println("Drawing " + rowClues[i].getFormattedClue());
		}
		for(int i = 0; i < sizeX; i++) {
			int currentXPos = x + horizontalClueWidth + gridXOffset + (int)(squareWidth / 2 + squareWidth * i);
			for(int j = 0; j < colClues[i].getRawClue().length; j++) {
				int currentYPos = y + verticalClueHeight + gridYOffset - fm.getHeight() * j;
				DrawingTools.drawText(graphics.getFont().deriveFont(clueFontSize), Integer.toString(colClues[i].getRawClue()[j]), currentXPos, currentYPos, Align.CENTER_HORIZONTAL, Align.BOTTOM, graphics2D);
			}
		}
		//fill squares based on their state
		for(int i = 0; i < sizeY; i++) {
			for(int j = 0; j < sizeX; j++) {
				Box currentSquare = solution[i][j];
				int currentSquareX = x + horizontalClueWidth + gridXOffset + (int)(squareWidth * j);
				int currentSquareY = y + verticalClueHeight + gridYOffset + (int)(squareWidth * i);
				int xDistanceFromEdge = (int)(squareWidth * 0.1);
				// ensure error from rounding for square is the same as that for the grid lines, taking squareWidth itself does not give a graphically accurate value
				int accurateSquareWidthX = (int)(squareWidth * (j + 1)) - (int)(squareWidth * j);
				int accurateSquareWidthY = (int)(squareWidth * (i + 1)) - (int)(squareWidth * i);
				switch(currentSquare.getState()) {
					case EMPTY:
						break;
					case MARKED:
						//draw an x
						graphics2D.setColor(black);
						graphics2D.drawLine(currentSquareX + xDistanceFromEdge, currentSquareY + xDistanceFromEdge, currentSquareX + (int)squareWidth - xDistanceFromEdge, currentSquareY + (int)squareWidth - xDistanceFromEdge);
						graphics2D.drawLine(currentSquareX + xDistanceFromEdge, currentSquareY + (int)squareWidth - xDistanceFromEdge, currentSquareX + (int)squareWidth - xDistanceFromEdge, currentSquareY + xDistanceFromEdge);
						break;
					case CORRECT:
						graphics2D.setColor(green);
						graphics2D.fillRect(currentSquareX, currentSquareY, (int)accurateSquareWidthX, (int)accurateSquareWidthY);
						break;
					case INCORRECT:
						graphics2D.setColor(red);
						graphics2D.fillRect(currentSquareX, currentSquareY, accurateSquareWidthX, accurateSquareWidthY);
						//draw an X
						graphics2D.setColor(black);
						graphics2D.drawLine(currentSquareX + xDistanceFromEdge, currentSquareY + xDistanceFromEdge, currentSquareX + (int)squareWidth - xDistanceFromEdge, currentSquareY + (int)squareWidth - xDistanceFromEdge);
						graphics2D.drawLine(currentSquareX + xDistanceFromEdge, currentSquareY + (int)squareWidth - xDistanceFromEdge, currentSquareX + (int)squareWidth - xDistanceFromEdge, currentSquareY + xDistanceFromEdge);
				}
			}
		}
		graphics2D.setColor(black);
		//draw grid lines
		DrawingTools.drawRect(x + horizontalClueWidth + gridXOffset, y + verticalClueHeight + gridYOffset, gridWidth, gridHeight, alignX, alignY, graphics2D);
		for(int i = 1; i < sizeX; i++) {
			int currentXPos = x + horizontalClueWidth + gridXOffset + (int)(squareWidth * i);
			graphics2D.drawLine(currentXPos, y + verticalClueHeight + gridYOffset, currentXPos, y + verticalClueHeight + gridYOffset + gridHeight);
		}
		for(int i = 1; i < sizeY; i++) {
			int currentYPos = y + verticalClueHeight + gridYOffset + (int)(squareWidth * i);
			graphics2D.drawLine(x + horizontalClueWidth + gridXOffset, currentYPos, x + horizontalClueWidth + gridXOffset + gridWidth, currentYPos);
		}
	}

	private void adjustWidthAndHeight() {
		//TODO adjust width and height so the following is true:
		/*
		 * Grid and clues are completely within the bounding box
		 * Clues take up a percentage of the available space OR clues have a constant font size which scales the grid to fit in the remaining space
		 */

		//Depending on the look, this may change to a ratio which would decrease size of clues and grid together
		//When using a ratio, clues should have a maximum font size and a minimum font size that both will break the ratio but preserve normalcy
		clueFontSize = 12;
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
//		System.out.println("maxRowClueString = " + maxRowClueString);
		horizontalClueWidth = fm.stringWidth(maxRowClueString + " ");
		List<int[]> colClueValues = new ArrayList<>();
		int maxColClueLength = 0;
		for(Clue c : colClues) {
			colClueValues.add(c.getRawClue());
		}
		for(int[] clue : colClueValues) {
			if(clue.length > maxColClueLength) {
				maxColClueLength = clue.length;
			}
		}
		verticalClueHeight = fm.getHeight() * maxColClueLength;
//		System.out.println("Clue width: " + horizontalClueWidth + "px");
//		System.out.println("Clue height: " + verticalClueHeight + "px");
		double gridWidthHeightRatio = (double) sizeX / sizeY;
		double availableWidthHeightRatio = (double) (width - horizontalClueWidth) / (height - verticalClueHeight);
		if(availableWidthHeightRatio > gridWidthHeightRatio) {
			//the only constraint is height
			gridHeight = height - verticalClueHeight;
			gridWidth = (int)(gridHeight * gridWidthHeightRatio);
			gridXOffset = (width - horizontalClueWidth - gridWidth) / 2;
			gridYOffset = 0;
		} else {
			//constraint is width
			gridWidth = width - horizontalClueWidth;
			gridHeight = (int)((double)gridWidth / gridWidthHeightRatio);
			gridYOffset = (height - verticalClueHeight - gridHeight) / 2;
			gridXOffset = 0;
		}
	}

	public void setWidth(int width) {
		this.width = width;
		adjustWidthAndHeight();
	}

	public void setHeight(int height) {
		this.height = height;
		adjustWidthAndHeight();
	}
}

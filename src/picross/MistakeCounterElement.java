package picross;

import mygl.*;
import mygl.Graphics;

import java.awt.*;

import static java.awt.Color.*;

public class MistakeCounterElement extends Element {
    private final int MAX_MISTAKES = 5;
    private int numMistakes;
    TextElement[] mistakes;

    public MistakeCounterElement(Graphics graphics) {
        super(graphics);
        numMistakes = 0;
        mistakes = new TextElement[MAX_MISTAKES];
        for (int i = 0; i < mistakes.length; i++) {
            mistakes[i] = new TextElement(graphics);
            mistakes[i].setText("X");
            mistakes[i].setAlignY(Align.CENTER_VERTICAL);
            mistakes[i].setHeight(30);
        }
        mistakes[0].setOnUpdateAction(() -> {
            Elements.centerAndSpaceElements(mistakes, 20, 10, width - 20, getTrueX() + 10, Axis.HORIZONTAL);
            setMistakePositions();
        });
    }

    public void draw() {
        Graphics2D g2d = graphics.getGraphics2D();
        g2d.setColor(black);
        DrawingTools.drawRect(x, y, width, height, alignX, alignY, g2d);
        for(int i = 0; i < mistakes.length; i++) {
            mistakes[i].setColor(i < numMistakes ? red : gray);
        }
    }

    private void setMistakePositions() {
        for(TextElement mistake : mistakes) {
            mistake.setY(getTrueY() + height / 2);
        }
    }

    public void addMistake() throws GameOverException {
        numMistakes++;
        if(numMistakes == MAX_MISTAKES) {
            // fail
            throw new GameOverException("Player made too many mistakes");
        }
    }
}

package mygl;

import java.awt.*;

import static java.awt.Color.*;

public class TextElement extends Element {
    private String text;
    private Color color;

    public TextElement(Graphics graphics) {
        super(graphics);
        text = "";
        color = black;
    }

    private int getBestFontSize(Font f, Graphics2D context) {
        return FontSize.getBestFontSize(width, height, text, f, context, 1);
    }

    public void draw() {
        Graphics2D graphics2D = graphics.getGraphics2D();
        Font f = graphics.getFont().deriveFont(
                Font.PLAIN,
                getBestFontSize(graphics.getFont(), graphics2D)
        );
        graphics2D.setFont(f);
        DrawingTools.drawText(f, text, x, y, alignX, alignY, graphics2D);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

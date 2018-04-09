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
        context.setColor(color);
        int width = 0;
        double fillProportion = 1;
        int i;
        for (i = 1; width < fillProportion * this.width; i++) {
            f = f.deriveFont(f.getStyle(), i);
            FontMetrics fm = context.getFontMetrics(f);
            width = fm.stringWidth(text);
            if (width == 0)
                return 0;
        }
        if (f.getSize() > height) {
            return (int) (fillProportion * height);
        }
        return i;
    }

    public void draw() {
        Graphics2D graphics2D = graphics.getGraphics2D();
        Font f = graphics.getFont().deriveFont(
                Font.PLAIN,
                getBestFontSize(graphics.getFont(),
                        graphics2D));
        graphics2D.setFont(f);

        int trueX = DrawingTools.getTrueX(x, width, alignX);
        int trueY = DrawingTools.getTrueY(y, height, alignY);
        int centerX = trueX + width / 2;
        int centerY = trueY + height / 2;
//        DrawingTools.drawTextAround(f, text, centerX, centerY, graphics2D);
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

package mygl;

import java.awt.*;

public class FontSize {
    public static int getBestFontSize(int width, int height, String text, Font font, Graphics2D context, double fillProportion) {
        int w = 0;
        int i;
        for (i = 1; w < fillProportion * width; i++) {
            font = font.deriveFont(font.getStyle(), i);
            FontMetrics fm = context.getFontMetrics(font);
            w = fm.stringWidth(text);
            if (w == 0)
                return 0;
        }
        if (font.getSize() > height) {
            return (int) (fillProportion * height);
        }
        return i;
    }
}

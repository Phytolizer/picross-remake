package mygl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author onContentStop
 */
public class DrawingTools {

    /**
     * Prints a string centered at (mouseX, mouseY).
     *
     * @param f   font, analyzed to center text exactly
     * @param s   string to print
     * @param x   mouseX-value of the center in pixels
     * @param y   number of pixels from top of canvas where the *bottom* of the string should go
     * @param art canvas to paint final string
     */
    public static void drawCenteredText(Font f, String s, int x, int y, Graphics2D art) {
        int len = art.getFontMetrics(f).stringWidth(s);
        art.drawString(s, x - len / 2, y);
    }

    /**
     * Prints a string right-aligned to the point (mouseX, mouseY).
     *
     * @param f   font, analyzed to find leftmost pixel of printed text
     * @param s   string to print
     * @param x   mouseX-value of the string end in pixels
     * @param y   number of pixels from top of canvas where the *bottom* of the string should go
     * @param art canvas to paint final string
     */
    public static void drawRightText(Font f, String s, int x, int y, Graphics2D art) {
        art.setFont(f);
        int len = art.getFontMetrics(f).stringWidth(s);
        art.drawString(s, x - len - 10, y);
    }

    public static void drawText(Font f, String s, int x, int y, Align alignX, Align alignY, Graphics2D graphics2D) {
        graphics2D.setFont(f);
        FontMetrics fm = graphics2D.getFontMetrics(f);
        Rectangle2D bounds = fm.getStringBounds(s, graphics2D);
        double height = bounds.getHeight();
        double width = bounds.getWidth();
        int newX = getTrueX(x, (int) width, alignX);
        int newY = getStringTrueY(y, (int) height, alignY);
        newY -= fm.getDescent() * 3 / 4;
        graphics2D.drawString(s, newX, newY);
    }

    public static void drawTextAround(Font f, String str, int x, int y, Graphics2D g2d) {
        FontMetrics metrics = g2d.getFontMetrics(f);
        Rectangle2D rect = metrics.getStringBounds(str, g2d);
        int height = (int) rect.getHeight();
        int width = (int) rect.getWidth();
        int new_x = x - width / 2;
        int new_y = y + height / 2 - metrics.getDescent() * 3 / 4;//.75 seems to be a good middle ground here (.5 too high, 1 too low)
        g2d.drawString(str, new_x, new_y);
    }

    public static void drawRectAround(int x, int y, int sizeX, int sizeY, Graphics2D g2d) {
        int realX = x - sizeX / 2;
        int realY = y - sizeY / 2;
        g2d.drawRect(realX, realY, sizeX, sizeY);
    }

    public static void drawRect(int x, int y, int sizeX, int sizeY, Align alignX, Align alignY, Graphics2D g2d) {
        int realX = getTrueX(x, sizeX, alignX);
        int realY = getTrueY(y, sizeY, alignY);
        g2d.drawRect(realX, realY, sizeX, sizeY);
    }

    public static void fillRectAround(int x, int y, int sizeX, int sizeY, Graphics2D g2d) {
        int realX = x - sizeX / 2;
        int realY = y - sizeY / 2;
        g2d.fillRect(realX, realY, sizeX, sizeY);
    }

    public static void fillRect(double x, double y, double sizeX, double sizeY, Align alignX, Align alignY, Graphics2D g2d) {
        double realX = getTrueX(x, sizeX, alignX);
        double realY = getTrueY(y, sizeY, alignY);
        int x1 = (int) Math.round(realX);
        int y1 = (int) Math.round(realY);
        int sx = (int) Math.round(sizeX);
        int sy = (int) Math.round(sizeY);
        g2d.fillRect(x1, y1, sx, sy);
    }

    public static void fillRect(int x, int y, int sizeX, int sizeY, Align alignX, Align alignY, Graphics2D g2d) {
        int realX = getTrueX(x, sizeX, alignX);
        int realY = getTrueY(y, sizeY, alignY);
        g2d.fillRect(realX, realY, sizeX, sizeY);
    }

    public static void drawImage(String filePath, int x, int y, int width, int height, Graphics2D g2d) {
        drawImage(filePath, x, y, width, height, Align.LEFT, Align.TOP, g2d);
    }

    public static void drawImage(String filePath, int x, int y, Graphics2D g2d) {
        drawImage(filePath, x, y, Align.LEFT, Align.TOP, g2d);
    }

    public static void drawImage(String filePath, int x, int y, double scale, Graphics2D g2d) {
        drawImage(filePath, x, y, scale, Align.LEFT, Align.TOP, g2d);
    }

    public static void drawImage(String filePath, int x, int y, double scale, Align alignX, Align alignY, Graphics2D g2d) {
        BufferedImage image;
        File imageFile = new File(filePath);
        image = getImageFromFile(imageFile);
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            int newWidth = (int) (scale * width);
            int newHeight = (int) (scale * height);
            drawImage(image, x, y, newWidth, newHeight, alignX, alignY, g2d);
        }
    }

    public static void drawImage(String filePath, int x, int y, int width, int height, Align alignX, Align alignY, Graphics2D g2d) {
        BufferedImage image;
        File imageFile = new File(filePath);
        image = getImageFromFile(imageFile);
        if (image != null) {
            drawImage(image, x, y, width, height, alignX, alignY, g2d);
        }
    }

    public static void drawImage(String filePath, int x, int y, Align alignX, Align alignY, Graphics2D g2d) {
        BufferedImage image;
        File imageFile = new File(filePath);
        image = getImageFromFile(imageFile);
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            drawImage(image, x, y, width, height, alignX, alignY, g2d);
        }
    }

    public static void drawImage(BufferedImage image, int x, int y, int width, int height, Align alignX, Align alignY, Graphics2D g2d) {
        int newX = getTrueX(x, width, alignX);
        int newY = getTrueY(y, height, alignY);
        g2d.drawImage(image, newX, newY, width, height, (img, infoflags, x_, y_, width_, height_) -> false);
    }

    public static Dimension getImageSize(String filePath) {
        BufferedImage image;
        File imageFile = new File(filePath);
        image = getImageFromFile(imageFile);
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return null;
    }

    public static BufferedImage getImageFromFile(File imageFile) {
        try {
            if (!imageFile.exists()) {
                System.out.println("Image file not found: " + imageFile.getPath());
            } else {
                return ImageIO.read(imageFile);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static int getTrueX(int x, int width, Align align) {
        int newX;
        switch (align) {
            case LEFT:
                newX = x;
                break;
            case CENTER_HORIZONTAL:
                newX = x - width / 2;
                break;
            case RIGHT:
                newX = x - width;
                break;
            default:
                throw new IllegalArgumentException("Cannot use Align." + align.toString() + " for horizontal position");
        }
        return newX;
    }

    public static double getTrueX(double x, double width, Align align) {
        double newX;
        switch (align) {
            case LEFT:
                newX = x;
                break;
            case CENTER_HORIZONTAL:
                newX = x - width / 2;
                break;
            case RIGHT:
                newX = x - width;
                break;
            default:
                throw new IllegalArgumentException("Cannot use Align." + align.toString() + " for horizontal position");
        }
        return newX;
    }

    public static int getTrueY(int y, int height, Align align) {
        int newY;
        switch (align) {
            case TOP:
                newY = y;
                break;
            case CENTER_VERTICAL:
                newY = y - height / 2;
                break;
            case BOTTOM:
                newY = y - height;
                break;
            default:
                throw new IllegalArgumentException("Cannot use Align." + align.toString() + " for vertical position");
        }
        return newY;
    }

    public static double getTrueY(double y, double height, Align align) {
        double newY;
        switch (align) {
            case TOP:
                newY = y;
                break;
            case CENTER_VERTICAL:
                newY = y - height / 2;
                break;
            case BOTTOM:
                newY = y - height;
                break;
            default:
                throw new IllegalArgumentException("Cannot use Align." + align.toString() + " for vertical position");
        }
        return newY;
    }

    public static int getStringTrueY(int y, int height, Align align) {
        int newY;
        switch (align) {
            case TOP:
                newY = y + height;
                break;
            case CENTER_VERTICAL:
                newY = y + height / 2;
                break;
            case BOTTOM:
                newY = y;
                break;
            default:
                throw new IllegalArgumentException("Cannot use Align." + align.toString() + " for vertical position");
        }
        return newY;
    }

}

package picross;

import mygl.BetterFrame;
import mygl.DrawingTools;
import mygl.Element;
import mygl.Graphics;

public class ScrollAreaElement extends Element {
    private ScrollListener scrollListener;

    public ScrollAreaElement(Graphics graphics) {
        super(graphics);
    }

    public void draw() {
        int mouseX = graphics.getFrame().mouseX;
        int mouseY = graphics.getFrame().mouseY;
        int trueX = getTrueX();
        int trueY = getTrueY();
        if (scrollListener != null) {
            if (graphics.getFrame().scrollAmt != 0) {
                if (isInBounds(mouseX, mouseY, trueX, trueY)) {
                    scrollListener.onScroll(graphics.getFrame().scrollAmt);
                }
            }
        }
        //visualize scroll area for debugging
//		graphics.getGraphics2D().drawRect(trueX, trueY, width, height);
    }

    public void setOnScrollAction(ScrollListener scrollAction) {
        scrollListener = scrollAction;
    }
}

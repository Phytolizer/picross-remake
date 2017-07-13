package picross;

import mygl.DrawingTools;
import mygl.Graphics;

import java.awt.*;

import static java.awt.Color.black;

/**
 * Created by kyle on 7/12/17.
 */
public enum Window {
	MAIN {
		public void draw(Graphics g) {
			Graphics2D graphics2D = g.getGraphics2D();
			g.setFont(new Font("Arial", Font.BOLD, 50));
			graphics2D.setColor(black);
			DrawingTools.drawCenteredText(g.getFont(), "PICROSS", g.width / 2, g.WINDOW_BAR_HEIGHT + 60, graphics2D);
		}
	}, CONTROLS {
		public void draw(Graphics g) {
			Graphics2D graphics2D = g.getGraphics2D();
			g.setFont(new Font("Arial", Font.BOLD, 50));
			graphics2D.setColor(black);
			DrawingTools.drawCenteredText(g.getFont(), "CONTROLS", g.width / 2, g.WINDOW_BAR_HEIGHT + 60, graphics2D);
		}
	}, SIZE {
		public void draw(Graphics g) {
			Graphics2D graphics2D = g.getGraphics2D();
			g.setFont(new Font("Arial", Font.BOLD, 50));
			graphics2D.setColor(black);
			DrawingTools.drawCenteredText(g.getFont(), "PUZZLE SIZE", g.width / 2, g.WINDOW_BAR_HEIGHT + 60, graphics2D);
		}
	}, GAME {
		public void draw(Graphics g) {

		}
	};

	public void draw(Graphics g) {

	}
}

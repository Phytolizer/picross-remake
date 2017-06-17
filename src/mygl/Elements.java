package mygl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyle on 6/16/17.
 */
public class Elements {
	private static List<Element> all_elements = new ArrayList<>();
	public static void update() {
		for(Element e : all_elements) {
			e.update();
		}
	}
	public static void add(Element e) {
		all_elements.add(e);
	}
	public static void draw() {
		for(Element e : all_elements) {
			if(e.isVisible()) {
				e.draw();
			}
		}
	}
}

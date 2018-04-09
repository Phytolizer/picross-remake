package picross;

import mygl.Element;

import java.util.*;

/**
 * Created by kyle on 7/12/17.
 */
public class ElementList {
    private HashMap<Window, List<Element>> elements;

    public ElementList() {
        elements = new HashMap<>();
        Window[] windows = Window.values();
        for (Window window : windows) {
            elements.put(window, new ArrayList<>());
        }
    }

    public void add(Element e, Window w) {
        List<Element> elementsInWindow = elements.get(w);
        elementsInWindow.add(e);
        elements.put(w, elementsInWindow);
    }

    public void add(Element[] elems, Window w) {
        List<Element> elementsInWindow = elements.get(w);
        elementsInWindow.addAll(Arrays.asList(elems));
        elements.put(w, elementsInWindow);
    }

    //Is this an optimal method for switching to another window?
    public void setWindow(Window w) {
        //Sets all elements not assigned to w to be invisible.
        for (Window window : Window.values()) {
            if (window == w) {
                continue;
            }
            for (Element e : elements.get(window)) {
                e.setVisible(false);
            }
        }
        //Sets all elements assigned to w to be visible.
        for (Element e : elements.get(w)) {
            e.setVisible(true);
        }
    }
}

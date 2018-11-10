package bgusolver;

import java.util.Comparator;

/**
 * compares two line requests objects
 */
public class JCLineRequestComperator implements Comparator<JCLineRequest> {

    public int compare(JCLineRequest lr0, JCLineRequest lr1) {
        return lr1.numCellsChanged - lr0.numCellsChanged;
//		return lr1.rating - lr0.rating;
    }

}

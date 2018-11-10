package picross;

import java.util.ArrayList;
import java.util.List;

public class Clue {
    private int[] clue;
    private final ClueType clueType;
    private final Box[] line;

    public Clue(Box[] line, ClueType type) {
        clueType = type;
        this.line = line;
        clue = createClue(line);
    }

    private int[] createClue(Box[] line) {
        List<Integer> out = new ArrayList<>();
        int streak = 0;
        for (Box aLine : line) {
            if (aLine.getState() == BoxState.INCORRECT && streak > 0) {
                out.add(streak);
                streak = 0;
            } else if (aLine.getState() == BoxState.CORRECT) {
                streak++;
            }
        }
        if (streak > 0) {
            out.add(streak);
        }
        if(out.size() == 0) {
            out.add(0);
        }
        return out.stream().mapToInt(i -> i).toArray();
    }

    public String getClue() {
        StringBuilder out = new StringBuilder();
        for (int c : clue) {
            out.append(c).append(" ");
        }
        if (out.length() > 0)
            return out.toString().substring(0, out.length() - 1);
        return "";
    }

    public String getFormattedClue() {
        StringBuilder out = new StringBuilder();
        for (int c : clue) {
            out.append(c).append(clueType == ClueType.ROW ? ' ' : '\n');
        }
        return out.toString();
    }

    public int[] getRawClue() {
        return clue;
    }

    public void refresh() {
        clue = createClue(line);
    }
}

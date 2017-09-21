package picross;

import java.util.ArrayList;
import java.util.List;

public class Clue {
	private int[] clue;
	private ClueType clueType;
	private Box[] line;
	public Clue(Box[] line, ClueType type) {
		clueType = type;
		this.line = line;
		clue = createClue(line);
	}

	private int[] createClue(Box[] line) {
		List<Integer> out = new ArrayList<>();
		int streak = 0;
		for(int i = 0; i < line.length; i++) {
			if(line[i].getState() == BoxState.INCORRECT && streak > 0) {
				out.add(streak);
				streak = 0;
			} else if (line[i].getState() == BoxState.CORRECT) {
				streak++;
			}
		}
		if(streak > 0) {
			out.add(streak);
		}
		return out.stream().mapToInt(i -> i).toArray();
	}

	public String getClue() {
		StringBuilder out = new StringBuilder();
		for(int c : clue) {
			out.append("" + c + " ");
		}
		if(out.length() > 0)
			return out.toString().substring(0, out.length() - 1);
		return "";
	}

	public String getFormattedClue() {
		StringBuilder out = new StringBuilder();
		for(int c : clue) {
			out.append("" + c + (clueType == ClueType.ROW ? ' ' : '\n'));
		}
		return out.toString();
	}

	public void refresh() {
		clue = createClue(line);
	}
}

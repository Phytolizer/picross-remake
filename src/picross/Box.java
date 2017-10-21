package picross;

import static picross.BoxState.EMPTY;

public class Box {
	private BoxState state;

	public Box() {
		state = EMPTY;
	}

	public void setState(BoxState boxState) {
		state = boxState;
	}

	public BoxState getState() {
		return state;
	}

	public String toString() {
		switch(state) {
			case EMPTY:
				return "0";
			case CORRECT:
				return "1";
			case INCORRECT:
				return "2";
			case MARKED:
				return "3";
		}
		return "-1";
	}
}

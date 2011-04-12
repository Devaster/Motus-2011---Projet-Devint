package control;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ScoreChangedEvent extends EventObject {

	private int score;
	
	public ScoreChangedEvent(Object source, int newScore) {
		super(source);
		score = newScore;
	}
	
	public int getScore() {
		return score;
	}
	
}

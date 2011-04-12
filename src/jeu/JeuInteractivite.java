package jeu;

import control.GameAnswerEvent;
import control.GameAnswerListener;
import control.ScoreChangedListener;
import control.TimeChangedListener;

public interface JeuInteractivite {
	
	public void startHorloge();
	public void submit(String proposal);
	public void jocker();
	public void changeOfWord();
	public int giveCurrentScore();
	
	public void addGameAnswerListener(GameAnswerListener listener);
	public void addTimeChangedListener(TimeChangedListener listener);
	public void addScoreChangedListener(ScoreChangedListener listener);
	public void removeGameAnswerListener(GameAnswerListener listener);
	public void removeTimeChangedListener(TimeChangedListener listener);
	public void removeScoreChangedListener(ScoreChangedListener listener);
	public void fireGameAnswerListener(GameAnswerEvent e);
	public void fireScoreChangedListener(int score);
	
}

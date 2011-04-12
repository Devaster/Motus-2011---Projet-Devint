package jeu;

import java.util.Timer;

import javax.swing.event.EventListenerList;

import score.Score;

import control.GameAnswerEvent.AnswerType;
import control.GameAnswerEvent;
import control.GameAnswerListener;
import control.ScoreChangedEvent;
import control.ScoreChangedListener;
import control.TimeChangedListener;

public class JeuIndividuel extends Jeu implements JeuInteractivite {
	
	private EventListenerList liste;
	private HorlogeInteractive horloge;
	private Timer timer;
	
	public JeuIndividuel(int longueur, int nbMots, int nbEssais) {
		super(longueur,nbMots,nbEssais);
		liste = new EventListenerList();
		horloge = new HorlogeInteractive();
		timer = new Timer();
	}
	
	public boolean endGame() {
		boolean b = super.endGame();
		if(b) {
			fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.END_GAME));
			timer.cancel();
		}
		return b;
	}
	
	public boolean cantProposeAgain() {
		boolean b = super.cantProposeAgain();
		if(b) {
			fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.NO_MORE_TRY,currentWord(),currentDef()));
			nextWord();
		}
		return b;
	}
	
	public boolean hasFound() {
		boolean b = super.hasFound();
		if(b) {
			fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.WORD_FOUND,currentWord(),currentDef()));
			nextWord();
		}
		return b;
	}

	public void reset() {
		super.reset();
		fireScoreChangedListener(0);
	}
	
	@Override
	public void startHorloge() {
	
		timer.schedule(horloge,1000,1000);
		
	}

	@Override
	public void submit(String proposal) {
		
		compare(proposal);
		if(!(hasFound()||cantProposeAgain())) {
			fireGameAnswerListener(new GameAnswerEvent(this,results));
			fireScoreChangedListener(giveCurrentScore());
		}
		endGame();
		
	}

	@Override
	public void jocker() {
		
		int n = giveBonus();
		char c = (char) (n%256);
		if(!(hasFound()||cantProposeAgain())) fireGameAnswerListener(new GameAnswerEvent(this,(n-c)/256,c,results));
		endGame();
		
	}

	@Override
	public void changeOfWord() {
		
		fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.NO_MORE_TRY,currentWord(),currentDef()));
		nextWord();
		endGame();
		
	}
	
	public int giveCurrentScore() {
		int g = 0, m = 0, b = 0;
		for(int i = 0; i < results.length; i++) {
			if(results[i]==-1) m++;
			if(results[i]==0) b++;
			if(results[i]==1) g++;
		}
		return Score.claculateScore(b+m+g, g, b, horloge.getHeure()*3600+horloge.getMin()*60+horloge.getSec());
	}
	
	public void addGameAnswerListener(GameAnswerListener listener) {
		liste.add(GameAnswerListener.class,listener);
	}
	
	public void removeGameAnswerListener(GameAnswerListener listener) {
		liste.remove(GameAnswerListener.class, listener);
	}
	
	public void fireGameAnswerListener(GameAnswerEvent e) {
		for(GameAnswerListener listener : liste.getListeners(GameAnswerListener.class)) {
			listener.dealWith(e);
		}
	}
	
	public void addTimeChangedListener(TimeChangedListener listener) {
		horloge.addTimeChangedListener(listener);
	}
	
	public void removeTimeChangedListener(TimeChangedListener listener) {
		horloge.removeTimeChangedListener(listener);
	}
	
	public void addScoreChangedListener(ScoreChangedListener listener) {
		liste.add(ScoreChangedListener.class, listener);
	}
	
	public void removeScoreChangedListener(ScoreChangedListener listener) {
		liste.remove(ScoreChangedListener.class, listener);
	}
	
	public void fireScoreChangedListener(int score) {
		ScoreChangedEvent e = new ScoreChangedEvent(this,score);
		for(ScoreChangedListener listener : liste.getListeners(ScoreChangedListener.class)) {
			listener.update(e);
		}
	}
	
}

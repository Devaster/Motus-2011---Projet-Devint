package jeu;

import java.util.Timer;

import javax.swing.event.EventListenerList;

import score.Score;

import control.GameAnswerEvent;
import control.GameAnswerEvent.AnswerType;
import control.GameAnswerListener;
import control.PlayerChangedEvent;
import control.PlayerChangedListener;
import control.ScoreChangedEvent;
import control.ScoreChangedListener;
import control.TimeChangedListener;

public class JeuDuels extends Jeu implements JeuInteractivite {
	
	private EventListenerList liste;
	private HorlogeInteractive horloge;
	private Timer timer;
	private int currentPlayer;
	private int numPlayer;
	
	public JeuDuels(int longueur, int nbMots, int nbEssais) {
		super(longueur,nbMots,nbEssais);
		liste = new EventListenerList();
		horloge = new HorlogeInteractive();
		timer = new Timer();
		currentPlayer = 0;
		numPlayer = 1;
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
			if(numPlayer==2) {
				fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.NO_MORE_TRY,currentWord(),currentDef()));
				nextWord();
				numPlayer=1;
				firePlayerChangedListener(Long.MAX_VALUE,true);
			}
			else {
				numPlayer++;
				reset();
				firePlayerChangedListener(0,true);
			}
					
		}
		
		
		return b;
	}
	
	public boolean hasFound() {
		boolean b = super.hasFound();
		if(b) {
			fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.WORD_FOUND,currentWord(),currentDef()));
			nextWord();
			numPlayer = 1;
			if(!endGame()) firePlayerChangedListener(Long.MAX_VALUE,true);
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
		
		super.compare(proposal);
		if(!(hasFound()||cantProposeAgain())) {
			fireGameAnswerListener(new GameAnswerEvent(this,results));
			fireScoreChangedListener(giveCurrentScore());
		}
		
	}


	@Override
	public void jocker() {
		
		int n = giveBonus();
		char c = (char) (n%256);
		if(!(hasFound()||cantProposeAgain())) fireGameAnswerListener(new GameAnswerEvent(this,(n-c)/256,c,results));
		
	}


	@Override
	public void changeOfWord() {
		
		if(numPlayer==2) {
			fireGameAnswerListener(new GameAnswerEvent(this,AnswerType.NO_MORE_TRY,currentWord(),currentDef()));
			nextWord();
			numPlayer = 1;
			if(!endGame()) firePlayerChangedListener(Long.MAX_VALUE,true);
		}
		else {
			numPlayer++;
			reset();
			firePlayerChangedListener(0,true);
		}
		
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


	@Override
	public void addGameAnswerListener(GameAnswerListener listener) {
		
		liste.add(GameAnswerListener.class,listener);
		
	}


	@Override
	public void addTimeChangedListener(TimeChangedListener listener) {
		
		horloge.addTimeChangedListener(listener);
		
	}


	@Override
	public void removeGameAnswerListener(GameAnswerListener listener) {
		
		liste.remove(GameAnswerListener.class, listener);
		
	}


	@Override
	public void removeTimeChangedListener(TimeChangedListener listener) {
		
		horloge.removeTimeChangedListener(listener);
		
	}


	@Override
	public void fireGameAnswerListener(GameAnswerEvent e) {
		
		for(GameAnswerListener listener : liste.getListeners(GameAnswerListener.class)) {
			listener.dealWith(e);
		}
		
	}
	
	public void addPlayerChangedListener(PlayerChangedListener listener) {
		liste.add(PlayerChangedListener.class, listener);
	}
	
	public void removePlayerChangedListener(PlayerChangedListener listener) {
		liste.remove(PlayerChangedListener.class, listener);
	}
	
	public void firePlayerChangedListener(long delay, boolean b) {
		currentPlayer++;
		currentPlayer%=2;
		for(PlayerChangedListener listener : liste.getListeners(PlayerChangedListener.class)) listener.signalChangement(new PlayerChangedEvent(this,currentPlayer+1,delay,b));
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

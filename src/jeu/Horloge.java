package jeu;

import java.util.TimerTask;

public class Horloge extends TimerTask {

	protected int heure,min,sec;
	
	public Horloge() {
		heure = 0;
		min = 0;
		sec = 0;
	}
	
	public void run() {
		
		sec++;
		
		min+=sec/60;
		heure+=min/60;
		
		sec%=60;
		min%=60;
		
		
	}
	
	public int getHeure() {
		return heure;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getSec() {
		return sec;
	}

	
	
}

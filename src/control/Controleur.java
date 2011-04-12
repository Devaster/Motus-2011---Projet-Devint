package control;

import vues.DuelWindow;
import vues.GameType;
import vues.GameWindow;
import vues.StartMenu;
import jeu.JeuDuels;
import jeu.JeuIndividuel;
import jeu.JeuInteractivite;

public class Controleur {
	
	private JeuInteractivite jeu;
	private StartMenu menu;
	
	public Controleur() {
		
		menu = new StartMenu(this);
	}
	
	public void launch(GameType type,int longueur,int nbMots, int essais) {
		
		GameWindow window = null;
		
		switch(type) {
		
		case SIMPLE:
			jeu = new JeuIndividuel(longueur,nbMots,essais);
			window = new GameWindow(essais-1,longueur,this);
			break;
		case DOUBLE:
			jeu = new JeuDuels(longueur,nbMots,essais);
			window = new DuelWindow(essais-1,longueur,this);
			((JeuDuels) jeu).addPlayerChangedListener((PlayerChangedListener) window);
			break;
		case DOUBLE_TEAM:
			break;
		
		}
		
		jeu.startHorloge();
		
		jeu.addGameAnswerListener(window);
		jeu.addTimeChangedListener(window);
		jeu.addScoreChangedListener(window);
		
	}
	
	public void submit(String proposal) {
		
		jeu.submit(proposal);
		
	}
	
	public void jocker() {
		
		jeu.jocker();
		
	}
	
	public void goOn() {
		
		jeu.changeOfWord();
	}
	
	public void retour() {
		menu.setVisible(true);
		menu.sing();
	}
	
	public int askPlayer() {
		return ((JeuDuels) jeu).getCurrentPlayer();
	}

}

package jeu;

import java.util.List;


public class Equipe  {

	List<Joueur> joueurs;
	
	public Equipe(List<Joueur> joueurs) {
		this.joueurs = joueurs;
	}
	
	public List<Joueur> getPlayers() {
		return joueurs;
	}
}

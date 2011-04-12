package tournament;

/**
 * Main
 * 
 * @author @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 */

import java.util.ArrayList;
import java.util.List;

public final class Main {
	
	public static String dir = "/home/julien/workspace/Tournament_Management/ressources/";
	// Les listes qui seront utilisées par toutes les méthodes d'ajout et de suppression d'éléments
	public static List<Arbitre> refList = new ArrayList<Arbitre>();
	public static List<Joueur> playerList = new ArrayList<Joueur>();
	public static List<Equipe> teamList = new ArrayList<Equipe>();
	// La liste des épreuves sera construite à partir d'un fichier texte
	public static List<Epreuve> competList = null;
	
	public static Joueur getPlayerByID(int id) {
		for (Joueur player : playerList) {
			if (player.getPlayerID() == id)
				return player;
		}
		System.out.println("Joueur inexistant");
		return null;
	}
	
	public static Arbitre getRefByID(int id) {
		for (Arbitre ref : refList) {
			if (ref.getRefID() == id)
				return ref;
		}
		System.out.println("Arbitre inexistant");
		return null;
	}
	
	public static Equipe getTeamByID(int id) {
		for (Equipe team : teamList) {
			if (team.getTeamID() == id)
				return team;
		}
		System.out.println("Equipe inexistante");
		return null;
	}
	
	public static Epreuve getCompetByID(int id) {
		for (Epreuve compet : competList) {
			if (compet.getCompetID() == id)
				return compet;
		}
		System.out.println("Epreuve inexistante");
		return null;
	}

	/**
	 * Lancement du programme
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Chargement des épreuves depuis le catalogue d'épreuves dans le
		// fichier texte "catalogue.txt"
		// java est dans le répertoire bin quand il exécute le main
		String cat = dir + "catalogue.txt";
		competList = Tournament.loadCompets(cat);
		// On a créé des épreuves mais aucune équipe n'est encore inscrite pour
		// l'instant.
		Menu menu = new Menu();
		menu.run();
	}

}

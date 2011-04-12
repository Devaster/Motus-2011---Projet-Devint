package tournament;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static tournament.Xml.*;

/**
 * Classe Menu, permettant de proposer a l'utilisateur un menu et des sous menus
 * en fonction de ses choix. La classe contient aussi des methodes pour lire
 * l'entree de l'utilisateur et pour creer un participant et un score.
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * @version 1.0
 */

public class Menu {

	static Connection DBconn;
	static Tournament t;
	static String user;

	/**
	 * Constructeur de Menu.
	 */
	public Menu() {
	}

	/**
	 * Lance le menu
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public void run() {
		if (!login())
			return;
		loadingScreen();
		mainMenu();
	}

	/**
	 * Ecran de login qui s'affiche au lancement de l'application
	 * 
	 * @return true si l'utilisateur a entré un bon couple login et mot de passe
	 *         false si l'utilisateur a échoué trois fois consécutivement
	 */
	private boolean login() {
		System.out.println("== Gestion de tournoi ==");
		int tries = 0;

		DBconn = BD.createConnection();

		while (tries < 3) {
			System.out.println("Entrez votre login : ");
			String login = readUserEntryString();
			System.out.println("Entrez votre mot de passe : ");
			String mdp = readUserEntryString();

			user = connexionBDD(login, mdp);
			if (!(user == null)) { // Si requête concluante
				return true;
			} else {
				System.out
						.println("Erreur lors de l'identification... Réessayez...\n\n");
				tries++;
			}
		}
		return false;
	}

	/**
	 * Demande à la base de données si un utilisateur existe avec ce nom et mot
	 * de passe
	 * 
	 * @param login
	 *            L'identifiant de l'utilisateur
	 * @param mdp
	 *            Le mot de passe
	 * @return Le nom de l'arbitre s'il y en a un, null sinon
	 */
	private String connexionBDD(String login, String mdp) {

		String request = "SELECT * FROM Staff WHERE login = '" + login
				+ "' AND mdp = '" + mdp + "';";
		ResultSet result = BD.createGenericResultSet(DBconn, request);

		try {
			if (result.next()) {
				return result.getString("nom");
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Ecran de chargement
	 * 
	 * @throws SQLException
	 */
	private void loadingScreen() {
		System.out.println("== Chargement des données ==");
		System.out.println("Sélectionner une méthode de chargement : ");
		System.out.println("1 : Depuis les données XML");
		System.out.println("2 : Depuis la base de données SQL");
		System.out.print("3 : Utiliser de nouvelles données ");
		System.out
				.println("(Attention, ceci effacera les anciennes données !)");

		int choice;
		do {
			System.out.println("\nEntrez votre choix : ");
			choice = readUserEntryInt(1, 3);
		} while (choice == 0);

		switch (choice) {
		case 1:
			System.out.println("Chargement depuis XML !");
			t = new Tournament(readCompetsFromXmlFile("competitions.xml"));
			break;
		case 2:
			System.out.println("Chargement depuis SQL !");
			// TODO
			break;
		case 3:
			System.out.println("Nouvelles données !");
			// On vide la Data Base
			/*try {
				BD.ViderBase(DBconn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			t = new Tournament(Main.competList);
			break;
		}
	}

	/**
	 * Menu principal de l'application. Affiche les différentes parties de
	 * l'application et permet à l'utilisateur de gérer le tournoi
	 * 
	 * @throws IOException
	 */
	private void mainMenu() {
		boolean quit = false;
		while (!quit) {
			System.out.println("==== Menu principal ====");
			System.out.println("1 : Gérer les équipes");
			System.out.println("2 : Gérer les joueurs");
			System.out.println("3 : Gérer les arbitres");
			System.out.println("4 : Gérer les épreuves");
			System.out.println("5 : Sauvegarder");
			System.out.println("6 : Quitter");
			System.out.println("7 : Lancer le tournoi");
			int choice;
			do {
				System.out.print("\nEntrez votre choix : ");
				choice = readUserEntryInt(1, 7);
			} while (choice == 0);
			switch (choice) {
			case 1:
				gestionEquipe();
				break;
			case 2:
				gestionJoueur();
				break;
			case 3:
				gestionArbitre();
				break;
			case 4:
				gestionEpreuve();
				break;
			case 5:
				// Sauvegarde XML
				saveAllXml();
				break;
			case 6:
				quit = true;
				// Deconnexion de la Data Base
				//BD.deconnection(DBconn);
				break;
			case 7:
				try {
					t.feuilleDeRoute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// écrit les feuilles de route de chaque équipe participante
				// dans un fichier txt
				t.playTournament();
				// demande à l'arbitre de chaque match du tournoi de rentrer le
				// vainqueur
				System.out.println(t.getGlobalRanking() + "\n\n"
						+ t.getRankingByType());
				// affiche à lécran les classements du tournoi
				saveCompets("competitions.xml");
				break;
			}
		}
	}

	private void gestionEpreuve() {
		boolean quit = false;
		while (!quit) {
			System.out.println("==== Gestion des épreuves ====");
			System.out.println("1 : Lister les épreuves");
			System.out.println("2 : Inscrire équipe à une épreuve");
			System.out.println("3 : Retour au menu principal");
			int choice;
			do {
				System.out.print("\nEntrez votre choix : ");
				choice = readUserEntryInt(1, 3);
			} while (choice == 0);
			switch (choice) {
			case 1:
				listerEpreuve();
				break;
			case 2:
				inscrireEquipe();
				break;
			case 3:
				quit = true;
				mainMenu();
				break;
			}
		}

	}

	private void inscrireEquipe() {
		System.out.println("- Inscription d'une équipe à une épreuve-");
		Equipe eq = null;
		while (eq == null) {
			listerEquipe();
			System.out.print("Entrez le numéro de l'équipe : ");
			int id = readUserEntryInt();
			eq = Main.getTeamByID(id);
		}
		Epreuve ep = null;
		while (ep == null) {
			listerEpreuve();
			System.out.print("Entrez le numéro de la discipline : ");
			int id = readUserEntryInt();
			ep = Main.getCompetByID(id);
		}
		// Ajout données
		eq.ajouterEpreuveToTeamSelection(ep.getNom());
		// Ajout XML
		saveTeams("teams.xml");
		saveCompets("competitions.xml");
		// Ajout SQL
		//BD.InscrireEquipeEpreuve(DBconn, eq, ep);
	}

	private void listerEpreuve() {
		System.out.println("- Liste des disciplines - ");
		for (Epreuve e : t.getCompet()) {
			System.out.println("-- " + e.getCompetID() + " : " + e.getNom());
		}
	}

	private void gestionArbitre() {
		boolean quit = false;
		while (!quit) {
			System.out.println("==== Gestion des arbitres ====");
			System.out.println("1 : Ajouter un arbitre");
			System.out.println("2 : Supprimer un arbitre");
			System.out.println("3 : Lister les arbitres");
			System.out.println("4 : Retour au menu principal");

			int choice;
			do {
				System.out.print("\nEntrez votre choix : ");
				choice = readUserEntryInt(1, 4);
			} while (choice == 0);
			switch (choice) {
			case 1:
				ajoutArbitre();
				break;
			case 2:
				supprimerArbitre();
				break;
			case 3:
				listerArbitre();
				break;
			case 4:
				quit = true;
				mainMenu();
				break;
			}
		}

	}

	private void listerArbitre() {
		System.out.println("- Liste des arbitres - ");
		for (Arbitre a : Main.refList) {
			System.out.println("#" + a.getRefID() + " : " + a.getNom() + ", "
					+ a.getSkill());
		}

	}

	private void supprimerArbitre() {
		System.out.println("- Suppression d'un arbitre - ");

		Arbitre old = null;
		while (old == null) {
			listerArbitre();
			System.out.print("Entrez le numéro de l'arbitre : ");
			int idOld = readUserEntryInt();
			old = Main.getRefByID(idOld);
		}

		// Suppression des données
		for (Epreuve compet : t.getCompet())
			compet.removeRefFromPool(old);
		// Suppression XML
		saveRefs("referees.xml");
		// Suppression SQL
		//BD.SupprimerArbritre(DBconn, old);

	}

	private void ajoutArbitre() {
		System.out.println("- Ajout d'un arbitre - ");
		System.out.print("Entrez le nom : ");
		String name = readUserEntryString();

		String skill = null;
		while (skill == null) {
			listerEpreuve();
			System.out.print("Entrez le numéro de la discipline : ");
			int idSkill = readUserEntryInt();
			skill = t.getEpreuveById(idSkill);
		}

		System.out.print("Login de l'arbitre : ");
		String login = readUserEntryString();
		System.out.print("Mdp de l'arbitre : ");
		String mdp = readUserEntryString();

		Arbitre nouveau = new Arbitre(name, skill);

		// Ajout dans les données
		for (Epreuve compet : t.getCompet())
			compet.addRefToPool(nouveau);
		// Ajout XML
		saveRefs("referees.xml");
		// Ajout SQL
		//BD.AjouterArbritre(DBconn, nouveau, login, mdp);
	}

	private void gestionJoueur() {
		boolean quit = false;
		while (!quit) {
			System.out.println("==== Gestion des joueurs ====");
			System.out.println("1 : Ajouter un joueur");
			System.out.println("2 : Inscrire joueur dans une équipe");
			System.out.println("3 : Supprimer un joueur");
			System.out.println("4 : Lister les joueurs");
			System.out.println("5 : Retour au menu principal");

			int choice;
			do {
				System.out.print("\nEntrez votre choix : ");
				choice = readUserEntryInt(1, 4);
			} while (choice == 0);
			switch (choice) {
			case 1:
				ajoutJoueur();
				break;
			case 2:
				inscrireJoueur();
				break;
			case 3:
				supprimerJoueur();
				break;
			case 4:
				listerJoueur();
				break;
			case 5:
				quit = true;
				mainMenu();
				break;
			}
		}

	}

	private void ajoutJoueur() {
		System.out.println("- Ajouter un joueur - ");
		System.out.print("Entrez le nom du joueur : ");
		String name = readUserEntryString();
		List<String> skills = new ArrayList<String>();
		do {
			System.out.print("Entrez une compétence : ");
			skills.add(readUserEntryString());
			System.out.print("Une autre compétence (o/n) ? ");
		} while (readUserEntryString().equals("o"));
		Joueur newJ = new Joueur(name, skills);
		// Ajout données
		Main.playerList.add(newJ);
		// Ajout XML
		savePlayers("players.xml");
		// Ajout SQL
		//BD.AjouterJoueur(DBconn, newJ);
	}

	private void inscrireJoueur() {

		System.out.println("- Inscription d'un joueur dans une équipe -");

		Joueur j = null;
		while (j == null) {
			listerJoueur();
			System.out.print("Entrez le numéro du joueur : ");
			int id = readUserEntryInt();
			j = Main.getPlayerByID(id);
		}

		Equipe eq = null;
		while (eq == null) {
			listerEquipe();
			System.out.print("Entrez le numéro de l'équipe : ");
			int id = readUserEntryInt();
			eq = Main.getTeamByID(id);
		}
		// Ajout données
		eq.ajouterMembre(j);
		// Ajout XML
		savePlayers("players.xml");
		saveTeams("teams.xml");
		// Ajout SQL
		//BD.InscrireJoueurEquipe(DBconn, j, eq);
	}

	private void listerJoueur() {
		System.out.println("- Liste des joueurs -");
		for (Joueur j : Main.playerList)
			System.out.println("#" + j.getPlayerID() + " " + j.toString());
	}

	private void supprimerJoueur() {
		System.out.println("- Supprimer un joueur - ");
		Joueur j = null;
		while (j == null) {
			listerJoueur();
			System.out.print("ID du joueur a supprimer : ");
			int id = readUserEntryInt();
			j = Main.getPlayerByID(id);
		}
		// Suppression des données
		Main.playerList.remove(j);
		// Suppression XML
		savePlayers("players.xml");
		// Suppression SQL
		//BD.SupprimerJoueur(DBconn, j);

	}

	private void gestionEquipe() {
		boolean quit = false;
		while (!quit) {
			System.out.println("==== Gestion des équipes ====");
			System.out.println("1 : Ajouter une équipe");
			System.out.println("2 : Supprimer une équipe");
			System.out.println("3 : Lister les équipes");
			System.out.println("4 : Lister participants d'une équipe");
			System.out.println("5 : Sélectionner les épreuves pour une équipe");
			System.out.println("6 : Retour au menu principal");

			int choice;
			do {
				System.out.print("\nEntrez votre choix : ");
				choice = readUserEntryInt(1, 6);
			} while (choice == 0);
			switch (choice) {
			case 1:
				ajoutEquipe();
				break;
			case 2:
				supprimerEquipe();
				break;
			case 3:
				listerEquipe();
				break;
			case 4:
				listerMembres();
				break;
			case 5:
				selectCompets();
				break;
			case 6:
				quit = true;
				mainMenu();
				break;
			}
		}

	}

	private void selectCompets() {
		System.out
				.println("Quelle équipe veut faire sa sélection d'épreuves ?");
		listerEquipe();
		int teamID = readUserEntryInt();
		Equipe team = Main.getTeamByID(teamID);
		System.out.println("- Sélection de 5 épreuves exactement -");
		Epreuve selected = null;
		String selectedCompetName = null;
		int choice;
		boolean again = false;
		int n = 5;
		while (again || n != 0) {
			System.out.println("Choisissez une épreuve parmi la liste");
			listerEpreuve();
			choice = readUserEntryInt();
			selected = Main.getCompetByID(choice);
			selectedCompetName = selected.getNom();
			if (team.ajouterEpreuveToTeamSelection(selectedCompetName)) {
				again = true;
				n--;
				System.out.println("Plus que " + n + " épreuves à choisir");
			} else {
				again = false;
				System.out
						.println("L'épreuve n'a pu être sélectionnée.\nAucun joueur n'a la compétence requise pour participer à cette épreuve.");
			}
		}
	}

	private void ajoutEquipe() {
		System.out.println("- Ajouter une équipe -");
		System.out.print("Nom de l'équipe : ");
		String name = readUserEntryString();
		Equipe newE = new Equipe(name, null, null, null);
		// Ajout données
		Main.teamList.add(newE);
		// Ajout XML
		saveTeams("teams.xml");
		// Ajout SQL
		//BD.AjouterEquipe(DBconn, newE);
	}

	private void listerMembres() {
		System.out.println("- Liste des membres d'une équipe -");

		listerEquipe();

		Equipe toChoose = null;
		while (toChoose == null) {
			System.out.print("ID de l'équipe à afficher : ");
			int id = readUserEntryInt();
			toChoose = Main.getTeamByID(id);
		}

		for (Joueur j : toChoose.getMembres()) {
			System.out.println("#" + j.getPlayerID() + " : " + j.getNom());
		}
	}

	private void listerEquipe() {
		System.out.println("- Liste des équipes - ");
		for (Equipe e : Main.teamList) {
			System.out.println("#" + e.getTeamID() + " : " + e.getNom());
		}

	}

	private void supprimerEquipe() {
		System.out.println("- Supprimer une équipe -");
		listerEquipe();

		Equipe toDelete = null;
		while (toDelete == null) {
			System.out.print("ID de l'équipe à supprimer : ");
			int id = readUserEntryInt();
			toDelete = Main.getTeamByID(id);
		}

		// Suppression données
		Main.teamList.remove(toDelete);
		// Suppression XML
		saveTeams("teams.xml");
		// Suppression SQL
		//BD.SupprimerEquipe(DBconn, toDelete);
	}

	/**
	 * Methode permettant de lire l'entree de l'utilisateur lorsque celle ci est
	 * une chaine de caractères
	 * 
	 * @return String
	 */
	public static String readUserEntryString() {
		Scanner scanner = new Scanner(System.in);
		String tempString = "";
		try {
			tempString = scanner.nextLine();
		} catch (Exception e) {
			System.out.println("Error, Try again");
			readUserEntryString();
		}
		return tempString;
	}

	/**
	 * Methode permettant de lire l'entree de l'utilisateur lorsque celle ci est
	 * un entier
	 * 
	 * @return int
	 */
	public static int readUserEntryInt() {
		Scanner scanner = new Scanner(System.in);
		int tempNumber = 0;
		try {
			tempNumber = scanner.nextInt();

		} catch (Exception e) {
			System.out.println("Erreur de recuperation. Veuillez recommencer");
			readUserEntryInt();
		}

		return tempNumber;
	}

	/**
	 * Methode permettant de lire l'entree de l'utilisateur lorsque celle ci est
	 * un entier en verifiant que l'entier fait bien partie des choix proposés
	 * 
	 * @param min
	 *            int
	 * @param max
	 *            int
	 * @return int
	 */
	public static int readUserEntryInt(int min, int max) {
		Scanner scanner = new Scanner(System.in);
		int tempNumber = 0;
		try {
			tempNumber = scanner.nextInt();

		} catch (Exception e) {
			System.out.println("Erreur de recuperation.");
		}

		if (tempNumber < min || tempNumber > max)
			tempNumber = 0;

		return tempNumber;
	}
}

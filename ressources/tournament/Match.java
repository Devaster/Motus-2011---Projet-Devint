package tournament;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import static tournament.BD.*;

/**
 * Classe Match : représente un match, avec ses deux équipes, son arbitre et son
 * gagnant
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
public class Match {

	private static int nofMatches = 0;
	private int matchID;
	private int numero;
	private String epreuve;
	private Equipe equipeA;
	private Equipe equipeB;
	private Equipe vainqueur;
	private Arbitre ref;

	/**
	 * Constructeur
	 * 
	 * @param uneEpreuve
	 * @param equipA
	 * @param equipB
	 */
	public Match(int numeroMatch, String uneEpreuve, Equipe equipA,
			Equipe equipB, Arbitre myRef) {
		matchID = ++nofMatches;
		// tester les parametres
		if (uneEpreuve == null || equipA == null || equipB == null
				|| numeroMatch <= 0 || myRef == null)
			throw new NullPointerException(
					"Un ou des parametres du constructeur de Match est null");

		// renseigner les attributs
		epreuve = uneEpreuve;
		equipeA = equipA;
		equipeB = equipB;
		numero = numeroMatch;
		ref = myRef;
	}

	public int getMatchID() {
		return matchID;
	}

	/**
	 * retourne l'epreuve concernée par le match
	 * 
	 * @return
	 */
	public String getEpreuve() {
		return epreuve;
	}

	/**
	 * retourne l'equipeA
	 * 
	 * @return
	 */
	public Equipe getEquipeA() {
		return equipeA;
	}

	/**
	 * retourne l'equipeB
	 * 
	 * @return
	 */
	public Equipe getEquipeB() {
		return equipeB;
	}

	/**
	 * 
	 * @return
	 */
	public Arbitre getRef() {
		return ref;
	}

	/**
	 * retourne le vainqueur du match
	 * 
	 * @return
	 */
	public Equipe getVainqueur() {
		return vainqueur;
	}

	/**
	 * retourne le perdant du match
	 * 
	 * @return
	 */
	public Equipe getPerdant() {
		// si le gagnant est null : retourner null
		if (vainqueur == null)
			return null;
		// renvoyer le perdant
		if (vainqueur == equipeA)
			return equipeB;
		return equipeA;
	}

	/**
	 * retourne le numéro du match
	 * 
	 * @return
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * Joue le match et demande à l'arbitre de s'identifier avant de renseigner
	 * l'équipe gagnante
	 * 
	 * @param leTour
	 */
	public void jouer(int leTour) {
		// Vérifier l'identité de l'arbitre inscrite dans la DB
		String realLogin = "";
		String log = "";
		String realPasswd = "";
		String pass = "";
		boolean ok = false;
		// On cherche le login et le password dans la DB en fonction de
		// l'identifiant de l'arbitre du match
		try {
			ResultSet rs = createGenericResultSet(Menu.DBconn,
					"select login, mdp from Staff where id_Staff = "
							+ getRef().getRefID());
			if (rs == null)
				System.out.println("L'arbitre n'est pas dans la Data Base !");
			while (rs.next()) {
				realLogin = rs.getString(1);
				realPasswd = rs.getString(2);
			}
		} catch (SQLException e) { // if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		// On va demander à l'arbitre de s'identifier
		Scanner sc = new Scanner(System.in);
		do {
			System.out.println("login :");
			do {
				System.out.println("> ");
				if (sc.hasNext()) {
					log = sc.next();
				}
			} while (log == null);
			sc.nextLine();
			System.out.println("password :");
			do {
				System.out.println("> ");
				if (sc.hasNext()) {
					pass = sc.next();
				}
			} while (pass == null);
			sc.nextLine();
			// Vérification de l'identité de l'arbitre
			ok = realLogin == log && realPasswd == pass;
		} while (!ok);
		// Il peut entrer le score du match
		String teamA = equipeA.getNom();
		String teamB = equipeB.getNom();
		System.out.println("Which team has won the match ?\n\tTeam \'A\' : "
				+ teamA + "type \'A\' or Team \'B\' : " + teamB
				+ "type \'B\' ?");
		String response = "";
		do {
			System.out.println("> ");
			if (sc.hasNext()) {
				response = sc.next();
			}
		} while (!response.equalsIgnoreCase("A")
				&& !response.equalsIgnoreCase("B"));
		sc.nextLine();
		int victoireA = 0;
		int victoireB = 0;
		if (response.equalsIgnoreCase("A")) {
			vainqueur = equipeA;
			victoireA++;
		} else {
			vainqueur = equipeB;
			victoireB++;
		}
		// mettre à jour le score des equipes dans cette epreuve
		if (leTour == 1) {
			equipeA.ajouterScore(new Score(epreuve, 0, victoireA, 0));
			equipeB.ajouterScore(new Score(epreuve, 0, victoireB, 0));
		} else {
			Score scoreA = equipeA.getScore(epreuve);
			Score scoreB = equipeB.getScore(epreuve);
			// si le score est null pour cette epreuve : le créer et l'ajouter à
			// l'equipe
			if (scoreA == null) {
				scoreA = new Score(epreuve, 0, 0, 0);
				equipeA.ajouterScore(scoreA);
			}
			if (scoreB == null) {
				scoreB = new Score(epreuve, 0, 0, 0);
				equipeB.ajouterScore(scoreB);
			}
			// mettre à jour les scores des equipes
			ref.ajoutScore(victoireA, this, scoreA);
			ref.ajoutScore(victoireB, this, scoreB);
		}
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		// créer la variable
		StringBuilder resultat = new StringBuilder();
		// afficher le match
		String winA = "";
		String winB = "";
		if (vainqueur == equipeA)
			winA = "(win)";
		else
			winB = "(win)";
		resultat.append("|--- " + equipeA.getNom() + winA + " vs "
				+ equipeB.getNom() + winB + " ---|");
		return resultat.toString();
	}
}

package tournament;

import java.util.*;

/**
 * Classe equipe : représente une equipe avec ses joueurs et ses scores pour
 * chaque epreuve
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
public class Equipe {

	public static final int MAXPLAYERSPERTEAM = 5;
	public static final int NOFEPREUVEPERTEAM = 5;
	
	private static int nofTeams = 0;
	private int teamID;
	private String nom;
	private List<String> epreuveSelection;
	private List<Joueur> membres;
	private List<Score> scores;

	/**
	 * Constructeur : si liste des scores est nulle : création d'une liste vide
	 * si liste des membres est nulle : création d'une liste vide
	 */
	public Equipe(String unNom, List<String> selection,
			List<Joueur> desMembres, List<Score> desScores) {
		teamID = ++nofTeams;
		// tester le parametre de nom
		if (unNom == null)
			throw new NullPointerException(
					"Le parametre du constructeur de Equipe contenant le nom de l'equipe est null");

		// mise en place de la liste vide de scores si la parametre est null
		if (desScores == null)
			scores = new ArrayList<Score>();
		else
			scores = desScores;
		// mise en place de la liste vide des membres si la parametre est
		// null
		if (desMembres == null)
			membres = new ArrayList<Joueur>();
		else {
			membres = desMembres;
			// test du nombre de joueurs par equipe
			int i = 0;
			Iterator<Joueur> it = desMembres.iterator();
			while (it.hasNext()) {
				i++;
				Joueur j = it.next();
				if (i > 5) {
					System.out
							.println("Le joueur numero "
									+ i
									+ "a été supprimé car le nombre maximum de joueurs par équipe ne doit pas excéder "
									+ MAXPLAYERSPERTEAM + ".");
					desMembres.remove(j);
				}
			}
		}
		// remplir les autres champs
		nom = unNom;
		// On crée une liste d'épreuves choisies par les équipes vide pour
		// l'instant
		if (selection == null)
			epreuveSelection = new ArrayList<String>();
		else
			epreuveSelection = selection;
	}

	public Equipe(int id, String unNom, List<String> selection,
			List<Joueur> desMembres, List<Score> desScores) {
		this(unNom, selection, desMembres, desScores);
		teamID = id;
	}

	public int getTeamID() {
		return teamID;
	}

	/**
	 * 
	 * @return la selection d'épreuves de l'équipe
	 */
	public List<String> getSelectionOfEpreuves() {
		return epreuveSelection;
	}

	/**
	 * retourne le nom de l'equipe
	 * 
	 * @return nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * retourne une copie de la liste des membres de l'equipe
	 * 
	 * @return
	 */
	public List<Joueur> getMembres() {
		return (List<Joueur>) membres;
	}

	/**
	 * retourne une copie de la liste des scores de l'equipe
	 * 
	 * @return
	 */
	public List<Score> getScores() {
		return (List<Score>) scores;// .clone();
	}

	/**
	 * retourne le score de l'equipe pour une epreuve donnée
	 * 
	 * @param epreuve
	 * @return le score de l'equipe si il y en a un, null sinon
	 */
	public Score getScore(String epreuve) {
		// parcourir la liste des scores jusqu'à atteindre l'epreuve voulue
		for (Score leScore : scores) {
			// si c'est le score de l'epreuve
			if (leScore.getEpreuve().equals(epreuve))
				return leScore;
		}
		return null;
	}

	/**
	 * Ajoute un membre à une equipe
	 * 
	 * @param nouveau
	 * @return true si bien ajouté, false sinon
	 */
	public boolean ajouterMembre(Joueur nouveau) {
		// tester le parametre
		if (nouveau == null)
			return false;
		// tester si le joueur est déjà dans l'équipe
		if (membres.contains(nouveau)) {
			System.out.println("Ce joueur est déjà inscrit dans l'équipe");
			return false;
		}

		// test du nombre de joueurs dans l equipe
		if (membres.size() != MAXPLAYERSPERTEAM) {
			membres.add(nouveau);
			return true;
		} else {
			System.out
					.println("Le nombre de joueurs dans l'équipe est déjà de "
							+ MAXPLAYERSPERTEAM + ".");
			return false;
		}
	}

	public boolean removePlayer(Joueur player) {
		if (membres.remove(player))
			return true;
		return false;
	}

	/**
	 * ajoute un score à la liste des scores de l'equipe
	 * 
	 * @param resultat
	 * @return
	 */
	public boolean ajouterScore(Score resultat) {
		// tester le parametre
		if (resultat == null)
			return false;

		// tester si un score pour cette epreuve est deja présent ds la liste
		Score aEnlever = null;
		for (Score score : scores) {
			if (score.getEpreuve().equals(resultat.getEpreuve())) {
				aEnlever = score;
				break;
			}
		}
		scores.remove(aEnlever);

		// ajouter le score
		scores.add(resultat);
		return true;
	}

	/**
	 * Test d'association d'une équipe à une épreuve
	 * 
	 * @param epreuveName
	 * @return true si un joueur a la compétence associée à l'épreuve, false si
	 *         aucun n'a la competence ou si la sélection de 5 épreuves est
	 *         terminée
	 */
	public boolean ajouterEpreuveToTeamSelection(String epreuveName) {
		// On teste si au moins des joueurs a la compétence requise pour
		// participer à l'épreuve.
		Iterator<Joueur> it = membres.iterator();
		while (it.hasNext()) {
			Joueur j = it.next();
			List<String> skills = j.getSkills();
			String skill = null;
			for (int i = 0; i < skills.size(); i++) {
				skill = skills.get(i).toString();
				if (skill.equalsIgnoreCase(epreuveName)) {
					if (this.epreuveSelection.size() < NOFEPREUVEPERTEAM) {
						this.epreuveSelection.add(epreuveName);
						return true;
					} else {
						System.out.println("Sélection terminée!");
						return false;
					}
				}
			}
		}
		// A ce point aucun joueur n'a la compétence requise.
		return false;
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		// créer la variable resultat
		//
		StringBuilder resultat = new StringBuilder();

		resultat.append(nom + " : \n");
		// afficher chaque joueur
		//
		for (Joueur j : membres)
			resultat.append(j + " ");

		resultat.append(".\nresultats :");

		// afficher le score
		//
		for (Score sc : scores)
			resultat.append(sc + " ");

		resultat.append(".");

		// retourner le resultat
		//
		return resultat.toString();
	}

	
}

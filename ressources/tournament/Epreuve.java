package tournament;

import java.util.*;

import tournament.Match;

/**
 * Classe Epreuve : représente une epreuve avec ses equipes et ses matches
 * simule l'exécution du tournoi
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
public class Epreuve {

	private static int nofepr = 0; // compteur d'épreuves
	private int competID;
	private String nom;
	private String type;
	private boolean individuelle;
	private int duree; // en minutes
	private String hourStart;
	private int pointsGivenToTheWinner;
	private List<Equipe> equipes;
	private Map<Integer, List<Match>> matches;
	// <numéro du tour, liste des matches du tour>
	private PouleArbitres refPool;

	public Epreuve(String nomEpreuve, String type, boolean isIndividuelle,
			int duree, String heureDebut, int pointsWin,
			List<Equipe> lesEquipes, HashMap<Integer, List<Match>> lesMatches,
			PouleArbitres myRefPool) {
		this.competID = ++nofepr;
		if (nomEpreuve == null)
			throw new NullPointerException(
					"Vous devez au moins specifier le nom de l epreuve !");
		this.nom = nomEpreuve;
		this.type = type;
		this.individuelle = isIndividuelle;
		this.duree = duree;
		this.hourStart = heureDebut;
		this.pointsGivenToTheWinner = pointsWin;
		// si la liste de equipes est null : en créer une
		if (lesEquipes == null)
			equipes = new ArrayList<Equipe>();
		else {
			if (!individuelle)
				equipes = lesEquipes;
			else {
				// Comme l'épreuve est individuelle, on ne garde qu'un joueur
				// par équipe tiré au hasard.
				Random r = new Random();
				// On crée une nouvelle liste d'équipes composees d'un seul
				// joueur pour chaque épreuve individuelle
				List<Equipe> teamList = new ArrayList<Equipe>();
				Iterator<Equipe> it = lesEquipes.iterator();
				while (it.hasNext()) {
					Equipe e = it.next();
					int randIndex = r.nextInt(Equipe.MAXPLAYERSPERTEAM);
					Joueur j = e.getMembres().get(randIndex);
					List<Joueur> playerList = new ArrayList<Joueur>();
					playerList.add(j);
					Equipe newTeam = new Equipe(e.getNom(),
							e.getSelectionOfEpreuves(), playerList,
							e.getScores());
					teamList.add(newTeam);
				}
				equipes = teamList;
			}
		}
		// si la liste de matches est null : en créer une
		if (lesMatches == null)
			matches = new HashMap<Integer, List<Match>>();
		else
			matches = lesMatches;
		if (myRefPool == null)
			refPool = new PouleArbitres(Main.refList);
		else
			refPool = myRefPool;
	}

	public Epreuve(int id, String nomEpreuve, String type,
			boolean isIndividuelle, int duree, String heureDebut,
			int pointsWin, List<Equipe> lesEquipes,
			HashMap<Integer, List<Match>> lesMatches, PouleArbitres myRefPool) {
		this(nomEpreuve, type, isIndividuelle, duree, heureDebut, pointsWin,
				lesEquipes, lesMatches, myRefPool);
		competID = id;
	}

	public int getCompetID() {
		return competID;
	}

	/**
	 * retourne le nom de l'epreuve
	 * 
	 * @return
	 */
	public String getNom() {
		return nom;
	}

	public String getType() {
		return type;
	}

	public boolean isIndividuelle() {
		return individuelle;
	}

	public int getDuree() {
		return duree;
	}

	public String getHourStart() {
		return hourStart;
	}

	public int getPointsWinner() {
		return pointsGivenToTheWinner;
	}

	/**
	 * retourne les equipes participantes à l'epreuves
	 * 
	 * @return
	 */
	public List<Equipe> getEquipes() {
		return equipes;
	}

	/**
	 * retourne les matches de l'epreuves
	 * 
	 * @return
	 */
	public Map<Integer, List<Match>> getMatches() {
		return (Map<Integer, List<Match>>) matches;
	}

	/**
	 * retourne les matches de l'epreuves pour un tour donné
	 * 
	 * @param int tour
	 * @return la liste des matches d'un tour d'une épreuve
	 */
	public List<Match> getMatches(int tour) {
		// tester que le tour existe dans le dico
		if (!matches.containsKey(new Integer(tour)))
			return null;
		return (List<Match>) matches.get(new Integer(tour));// .clone();
	}

	public PouleArbitres getRefPool() {
		return refPool;
	}

	public boolean addRefToPool(Arbitre ref) {
		if (ref.getSkill().equalsIgnoreCase(getNom())) {
			if (refPool.addRef(ref))
				return true;
		}
		return false;
	}

	public boolean removeRefFromPool(Arbitre ref) {
		if (refPool.removeRef(ref))
			return true;
		return false;
	}

	/**
	 * ajoute une equipe à la liste des equipes
	 * 
	 * @param nouvelle
	 *            équipe
	 * @return true si bien inscrite
	 */
	public boolean inscrireEquipe(Equipe nouvelle) {
		// tester le parametre
		if (nouvelle == null)
			return false;
		// tester si l'equipe est deja inscrite
		if (equipes.contains(nouvelle)) {
			System.out.println("Cette équipe est déjà inscrite à l'épreuve.");
			return false;
		}
		// On inscrit l'équipe seulement si dans sa sélection d'épreuves figure
		// cette épreuve (this)
		List<String> selection = nouvelle.getSelectionOfEpreuves();
		for (int i = 0; i < Equipe.NOFEPREUVEPERTEAM; i++) {
			if (selection.get(i).toString().equalsIgnoreCase(this.nom)) {
				equipes.add(nouvelle);
				return true;
			}
		}
		// A ce point l'équipe n'a pas sélectionné cette épreuve.
		System.out
				.println("Cette équipe n'a pas sélectionné cette épreuve.\nElle ne sera donc pas inscrite ici.");
		return false;
	}

	public boolean desinscrireEquipe(Equipe e) {
		if (equipes.remove(e))
			return true;
		return false;
	}

	/**
	 * Ajoute un match à la liste des matches de l'epreuve
	 * 
	 * @param nouveau
	 *            : match a ajouter
	 * @param tour
	 *            : tour du match
	 * @return
	 */
	public boolean ajouterMatch(Match nouveau, int tour) { // private ?
		// tester les parametres
		if (nouveau == null || tour <= 0)
			return false;
		Integer leTour = new Integer(tour);
		// tester si le tour existe dans le dico
		if (!matches.containsKey(leTour)) {
			// créer le tour
			matches.put(leTour, new ArrayList<Match>());
		}
		// tester si le match est deja présent ds la liste de ce tour
		if (matches.get(leTour).contains(nouveau))
			return false;
		// ajouter le match
		matches.get(leTour).add(nouveau);
		return true;
	}

	/**
	 * permet de créer la liste de matches pour un tour en fonction du nombre
	 * d'equipes restantes
	 * 
	 * @param leTour
	 */
	public void creerTour(int leTour) {
		// tester le parametre
		if (leTour <= 0)
			return;
		// melanger la liste des equipes
		Collections.shuffle(equipes);
		// Tester que le dico ne contient pas de liste pour ce tour
		Integer tr = new Integer(leTour);
		if (matches.containsKey(tr))
			matches.remove(tr);
		// parcourir la liste pour créer les matches
		Iterator<Equipe> itEquipe = equipes.iterator();
		int i = 1;
		while (itEquipe.hasNext()) {
			// obtenir la premiere equipe
			Equipe equipe1 = itEquipe.next();
			// vérifier qu'il y a une deuxieme equipe
			// Si pas de 2ème équipe, finale déjà jouée !
			if (itEquipe.hasNext()) {
				// obtenir la deuxieme
				Equipe equipe2 = itEquipe.next();
				// créer le match
				Match leMatch = new Match(i, this.getNom(), equipe1, equipe2,
						refPool.chooseRandomlyRef(this.getNom()));
				i++;
				// ajouter le match à la liste
				this.ajouterMatch(leMatch, leTour);
			}
		}
	}

	/**
	 * execute tous les matches de l'epreuve : enchaine les matches avec le
	 * gagnant de chaque match...
	 * 
	 */
	public Equipe jouer() {
		// On crée une liste sauvegarde de toutes les équipes participant à
		// l'épreuve
		// puisque les équipes perdantes vont être supprimées dès qu'elles
		// perdent,
		// ceci pour créer les rounds de l'épreuve
		List<Equipe> backupTeamList = new ArrayList<Equipe>();
		boolean termine = false;
		// créer les matches pour le 1er tour
		this.creerTour(1);
		// parcourir les tours
		for (int i = 1; !termine; i++) {
			// obtenir le numero du tour
			Integer leTour = new Integer(i);
			// obtenir la liste des matches de ce tour
			List<Match> lesMatches = matches.get(leTour);
			// tester la liste
			if (lesMatches == null)
				throw new NullPointerException(
						"La liste des matches pour le tour " + leTour
								+ " est null");
			// si la liste ne contient que deux equipes
			if (equipes.size() == 2) {
				// C'est la finale : jouer le match
				Iterator<Match> itFinal = lesMatches.iterator();
				Match finale = itFinal.next();
				finale.jouer(i);
				// enlever le perdant de la liste
				matches.get(leTour).remove(finale.getPerdant());
				termine = true;
				this.calculerClassement();
				Equipe winnerTeam = finale.getVainqueur();
				// L'arbitre attribue le nombre de points à l'équipe finaliste
				// gagnante
				Arbitre ref = finale.getRef();
				Score sc = winnerTeam.getScore(this.getNom());
				ref.setPoints(this.getPointsWinner(), finale, sc);
				// Il ne reste plus que les deux équipes finalistes dans
				// 'equipes'
				// On restaure la liste d'équipes comme au début de l'épreuve
				// mais avec les les scores qui ont été mis à jour bien sûr
				backupTeamList.add(winnerTeam);
				backupTeamList.add(finale.getPerdant());
				equipes = backupTeamList;
				// On renvoie l'équipe gagnante de l'épreuve : sortie de la
				// méthode
				return winnerTeam;
			} else {
				// sinon, jouer les matches de ce tour
				Iterator<Match> it = lesMatches.iterator();
				while (it.hasNext()) {
					// obtenir le match
					Match leMatch = it.next();
					// jouer le premier match
					leMatch.jouer(i);
					// enlever le perdant de la liste des equipes
					// et l'ajouter à la liste de sauvegarde
					Equipe loser = leMatch.getPerdant();
					backupTeamList.add(loser);
					equipes.remove(loser);
				}
			}
			// créer les matches pour le tour suivant
			this.creerTour(i + 1);
		}
		// retour impossible (normalement, la finale est jouée...)
		return null;
	}

	/**
	 * Calculer le classement pour chaque equipe
	 * 
	 */
	private void calculerClassement() {
		// obtenir le nombre de tour
		//
		int nbTour = matches.size();
		Integer tour = 0;

		// parcourir le dico des matches joués
		//
		Iterator<Integer> it = matches.keySet().iterator();

		while (it.hasNext()) {
			tour = it.next();

			List<Match> liste = matches.get(tour);

			// pour chaque tour : calculer le classement des perdant
			//
			for (Match match : liste) {
				// si c'est la finale
				//
				if (tour.intValue() == nbTour) {
					// le vainqueur est premier, et le perdant deuxieme
					//
					match.getPerdant().getScore(this.getNom()).setClassement(2);
					match.getVainqueur().getScore(this.getNom())
							.setClassement(1);
				} else {
					// sinon, calculer le classement du perdant
					//
					match.getPerdant().getScore(this.getNom())
							.setClassement(nbTour - tour.intValue() + 2);
				}
			}
		}
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		// créer le support du resultat
		//
		StringBuilder resultat = new StringBuilder("Competition : " + nom
				+ "\n");

		// afficher le tableau des matches
		//
		for (int i = 1; i <= matches.size(); i++) {
			resultat.append("*** round : " + String.valueOf(i) + " teams : ");

			// obtenir la liste des matches du tour
			//
			List<Match> matchList = matches.get(new Integer(i));
			// resultat.append("("+matchList.size()+")");
			for (Match match : matchList) {
				resultat.append(match);
			}
			resultat.append("\n");
		}
		return resultat.toString();
	}

	
}

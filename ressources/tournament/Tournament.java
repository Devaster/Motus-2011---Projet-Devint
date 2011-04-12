package tournament;

/**
 * Tournoi Polytech : ensemble d'épreuves
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Tournament {

	private static int nofTournaments = 0;
	private int id;
	private List<Epreuve> competitions;

	/**
	 * Constructeur prenant en parametre une liste d epreuves
	 * 
	 * @param myCompet
	 */
	public Tournament(List<Epreuve> myCompet) {
		id = ++nofTournaments;
		if (myCompet == null) {
			competitions = new ArrayList<Epreuve>();
		}
		competitions = myCompet;
	}

	public Tournament(int ID, List<Epreuve> myCompet) {
		this(myCompet);
		id = ID;
	}

	public int getTournamentID() {
		return id;
	}

	/**
	 * 
	 * @return la liste des epreuves
	 */
	public List<Epreuve> getCompet() {
		return competitions;
	}

	/**
	 * Recherche une épreuve dans la liste
	 * 
	 * @return Le nom de l'épreuve voulue
	 */
	public String getEpreuveById(int id) {
		for (Epreuve e : competitions) {
			if (e.getCompetID() == id)
				return e.getNom();
		}
		return null;
	}

	/**
	 * méthode d'ajout d'épreuve
	 * 
	 * @param compet
	 */
	public void addCompetition(Epreuve compet) {
		competitions.add(compet);
	}

	/**
	 * @return equipes gagnantes de chaque epreuve du tournoi
	 */
	public List<Equipe> playTournament() {
		List<Equipe> l = new ArrayList<Equipe>();
		for (int i = 0; i < competitions.size(); i++) {
			l.add(competitions.get(i).jouer());
		}
		return l;
	}

	/**
	 * 
	 * @param compets
	 * @return classement global d'une liste d'épreuves
	 */
	public String getRanking(List<Epreuve> compets) {
		StringBuilder sb = new StringBuilder();
		// Map<Nom Equipe, Nombre total de points>
		Map<String, Integer> pointsPerTeamMap = new HashMap<String, Integer>();
		// On recupere la liste des épreuves qui contiennent les équipes
		for (int i = 0; i < compets.size(); i++) {
			Epreuve compet = compets.get(i);
			List<Equipe> teams = compet.getEquipes();
			for (int j = 0; j < teams.size(); j++) {
				Equipe team = teams.get(j);
				String name = team.getNom();
				int nofPoints = team.getScore(compet.getNom()).getNbPoints();
				if (pointsPerTeamMap.containsKey(name))
					nofPoints += pointsPerTeamMap.get(name);
				pointsPerTeamMap.put(name, new Integer(nofPoints));
			}
		}
		// La map doit maintenant contenir toutes les équipes du tournoi avec le
		// total des points
		Iterator<String> it = pointsPerTeamMap.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next();
			int points = pointsPerTeamMap.get(name).intValue();
			sb.append("Equipe \"" + name + "\" scores " + points + " points.\n");
		}
		return sb.toString();
	}

	/**
	 * 
	 * @return classement général
	 */
	public String getGlobalRanking() {
		StringBuilder sb = new StringBuilder(
				"--- Polytech'Tournament Global Ranking ---\n");
		sb.append(getRanking(competitions));
		return sb.toString();
	}

	/**
	 * 
	 * @return classement par type d'épreuves
	 */
	public String getRankingByType() {
		StringBuilder sb = new StringBuilder(
				"--- Polytech'Tournament Ranking Per Type Of Competition ---\n");
		Map<String, List<Epreuve>> map = new HashMap<String, List<Epreuve>>();
		for (int i = 0; i < competitions.size(); i++) {
			Epreuve compet = competitions.get(i);
			String typeOfCompet = compet.getType();
			List<Epreuve> list = null;
			if (!map.containsKey(typeOfCompet))
				list = new ArrayList<Epreuve>();
			else
				list = map.get(typeOfCompet);
			list.add(compet);
			map.put(typeOfCompet, list);
		}
		// On a une map de catégories d'épreuves
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String type = it.next();
			List<Epreuve> list = map.get(type);
			sb.append("\tType of competition : " + type + "\n");
			sb.append(getRanking(list));
		}
		return sb.toString();
	}

	/**
	 * Retourne la liste des épreuves construites à partir du fichier
	 * 'catalogue.txt'
	 * 
	 * @param catalogueFile
	 * @return liste d'épreuves
	 * @throws Exception
	 */
	public static List<Epreuve> loadCompets(String cat)
			throws Exception {
		List<Epreuve> competList = new ArrayList<Epreuve>();
		int nofLines = Catalog.getNbLignes(cat);
		int nofCompets = nofLines - 3;
		System.out.println("Lecture du catalogue d'épreuves...\n");
		List<String> args = Catalog.readCatalogue(cat);
		int j = 0;
		for (int i = 0; i < nofCompets; i++) {
			int numCompet = Integer.valueOf(args.get(j));
			String nameCompet = args.get(j + 1);
			String type = args.get(j + 2);
			boolean individuelle = true;
			if (args.get(j + 3).equalsIgnoreCase("collective"))
				individuelle = false;
			int duree = Integer.valueOf(args.get(j + 4));
			String hourStart = args.get(j + 5);
			int points = Integer.valueOf(args.get(j + 6));
			// On crée l'épreuve et on l'ajoute à la liste.
			Epreuve e = new Epreuve(numCompet, nameCompet, type,
					individuelle, duree, hourStart, points, null, null, null);
			System.out.print("..." + e);
			competList.add(e);
			j += 7;// 7 arguments par ligne
		}
		System.out.print("\n");
		return competList;
	}

	/**
	 * Méthode de création de feuilles de route pour chaque équipe du tournoi
	 * dans des fichiers texte
	 * 
	 * @throws IOException
	 */
	public void feuilleDeRoute() throws IOException {
		List<Equipe> equipes = new ArrayList<Equipe>();
		Map<String, String> hourByName = new HashMap<String, String>();
		for (Epreuve ep : competitions) {
			hourByName.put(ep.getNom(), ep.getHourStart());
			for (Equipe eq : ep.getEquipes()) {
				if (!equipes.contains(eq))
					equipes.add(eq);
			}
		}
		for (Equipe e : equipes) {
			String filename = e.getNom() + "_FeuilleDeRoute.txt";
			FileWriter file = new FileWriter(filename);
			StringBuilder sb = new StringBuilder(
					"Polytech Tournament - 01/04/2011\n");
			// RDV a l heure de la premiere epreuve de l equipe
			// heure au format francais ex : 17h15
			String h = null;
			int hourRDV = 24;
			int minRDV = 60;
			List<String> sel = e.getSelectionOfEpreuves();
			for (String s : sel) {
				int hour = Integer.valueOf(hourByName.get(s).substring(0, 2));
				int min = Integer.valueOf(hourByName.get(s).substring(3,
						s.length()));
				if (hour < hourRDV || min < minRDV) {
					hourRDV = hour;
					minRDV = min;
				}
			}
			h = String.valueOf(hourRDV).concat("h")
					.concat(String.valueOf(minRDV));
			sb.append("RDV : " + h + "\n");
			sb.append("--------------------------------------------------------------------------------\n");
			sb.append("Equipe : " + e.getNom() + "\n" + "Joueurs : ");
			String players = "";
			List<Joueur> members = e.getMembres();
			for (Joueur j : members)
				players += j.getNom() + " ";
			sb.append(players + "\n" + "Epreuves :\n");
			for (String compet : sel)
				sb.append(hourByName.get(compet) + " : " + compet + "\n");
			file.write(sb.toString());
			file.flush();
			file.close();
		}
	}

	/**
	 * @return String representation of the tournament affichage du tournoi
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < competitions.size(); i++) {
			sb.append(competitions.get(i).toString());
		}
		return sb.toString();
	}
}
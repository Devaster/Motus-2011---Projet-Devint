package tournament;

import java.util.List;

/**
 * Classe Joueur
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */

public class Joueur extends Personne {

	

	private static int nofPlayers = 0;
	private int playerID;
	private List<String> skills;

	/**
	 * Constructeur
	 * 
	 * @param s
	 * @param skills
	 */
	public Joueur(String s, List<String> skills) {
		super(s);
		playerID = ++nofPlayers;
		if (skills == null) {
			throw new NullPointerException(
					"Un joueur doit avoir au moins une competence dans un sport");
		}
		this.skills = skills;
	}
	
	public Joueur(int id, String surname, List<String> skills) {
		this(surname,skills);
		playerID = id;
	}

	public int getPlayerID() {
		return playerID;
	}

	/**
	 * 
	 * @return la competence en un sport du joueur
	 */
	public List<String> getSkills() {
		return skills;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("Skills : ");
		for (int i = 0; i < skills.size(); i++)
			sb.append(skills.get(i).toString() + ' ');
		return sb.toString();
	}

	
}
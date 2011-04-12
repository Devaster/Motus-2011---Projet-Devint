package tournament;

/**
 * Représente un arbitre du tournoi
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
public class Arbitre extends Personne {

	private static int nofRefs = 0;
	private int refID;
	private String skill;

	/**
	 * Constructeur appelle le constructeur de Personne
	 * 
	 * @param s
	 */
	public Arbitre(String surname, String skill) {
		super(surname);
		refID = ++nofRefs;
		this.skill = skill;
	}

	public Arbitre(int id, String surname, String skill) {
		this(surname, skill);
		refID = id;
	}

	public int getRefID() {
		return refID;
	}

	/**
	 * 
	 * @return skill of the referee
	 */
	public String getSkill() {
		return skill;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("Skill : ");
		sb.append(this.getSkill() + "\n");
		return sb.toString();
	}

	/**
	 * Ajoute un score à une équipe donnée lié à un match
	 * @param ajout
	 * @param match
	 * @param score
	 */
	public void ajoutScore(int ajout, Match match, Score score) {
		// vérifier que l'arbitre est bien l'arbitre de ce match
		//
		if (match.getRef() != this)
			return;

		// changer le score
		//
		score.setNbVictoire(score.getNbVictoire() + ajout);
	}

	public void setScore(int nouveau, Match match, Score score) {
		// vérifier que l'arbitre est bien l'arbitre de ce match
		if (match.getRef() != this)
			return;
		// changer le score
		score.setNbVictoire(nouveau);
	}

	public void setPoints(int points, Match match, Score score) {
		// On s'assure que seul l'arbitre du match peut modifier le score de
		// l'équipe
		if (match.getRef() != this)
			return;
		score.setNbPoints(points);
	}

	
}
package score;

/**
 * @author Mido
 * 
 */
public class Score {

	private static final long serialVersionUID = 1L;

	private int nbOfLetters;
	private int nbOfGdPos;
	private int nbOfBadPos;
	private long time;

	// private int timeOfGame;

	public Score() {
		this.nbOfLetters = 0;
		this.nbOfGdPos = 0;
		this.nbOfBadPos = 0;
		this.time = 0;
	}

	/**
	 * @param nbOfLetters
	 *            le nombre de lettres du mot
	 * @param nbOfGdPos
	 *            nombre des lettres bien positionneés
	 * @param nbOfBadPos
	 *            nombre des lettre mal positionneés
	 * @param time
	 *            le temps de la partie
	 * @return
	 */
	public static int claculateScore(int nbOfLetters, int nbOfGdPos,
			int nbOfBadPos, long time) {

		int score = 0;
		int coef = 100;

		// traitment si le joueur a trouvé le mot
		//si le joueur trouve le mot en 15 sec il aura + 1000
		if ((nbOfGdPos == nbOfLetters) && ((time <= 15))) {
			score += (10 * coef);
			
			//si le joueur trouve le mot en plus de 15 sec il aura + 100

		} else if ((nbOfGdPos == nbOfLetters) && ((time > 15))) {
			score += coef;
		}

		if (nbOfGdPos >= (nbOfLetters / 2) && (nbOfGdPos != nbOfLetters)) {
			score = score + nbOfGdPos + coef;
		} else if (nbOfGdPos < (nbOfLetters / 2) && (nbOfGdPos != nbOfLetters)) {
			score += coef;
		}

		// traitment les mauvaises lettres
		if (nbOfLetters == nbOfBadPos)
			score = 0;

		else if (nbOfBadPos < (nbOfLetters / 2))
			score += 1;

		else if (nbOfBadPos >= (nbOfLetters / 2))
			score -= 1;

		return score;

	}

	public static void main(String[] args) {
		int let = 4;
		int bad = 0;
		int good = 4;
		long time = 15;
		// Score s = new Score();
		System.out
				.println("Score est :" + claculateScore(let, good, bad, time));
	}

	/**
	 * @return the nbOfLetters
	 */
	public int getNbOfLetters() {
		return nbOfLetters;
	}

	/**
	 * @return the nbOfGdPos
	 */
	public int getNbOfGdPos() {
		return nbOfGdPos;
	}

	/**
	 * @return the nbOfBadPos
	 */
	public int getNbOfBadPos() {
		return nbOfBadPos;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
}
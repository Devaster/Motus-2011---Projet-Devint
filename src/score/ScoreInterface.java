package score;

/**
 * @author Mido
 * 
 */
public interface ScoreInterface {

	/**
	 * put a new score in the list
	 */
	void newScore(int score);

	/**
	 * @return a list of scores
	 */
	int[] getScoreList();

}

package score;

public class Score {
	
	private int id, ranking, finalScore, wordsFound, gameTime;

	public Score(int id, int ranking, int finalScore, int wordsFound,
			int gameTime) {
		this.id = id;
		this.ranking = ranking;
		this.finalScore = finalScore;
		this.wordsFound = wordsFound;
		this.gameTime = gameTime;
	}

	public int getScoreId() {
		return id;
	}
	
	public int getRanking() {
		return ranking;
	}
	
	public int getFinalScore() {
		return finalScore;
	}

	public int getWordsFound() {
		return wordsFound;
	}
	
	public int getGameTime() {
		return gameTime;
	}
	
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	
	public void setFinalScore(int finalScore) {
		this.finalScore = finalScore;
	}

	public void setWordsFound(int wordsFound) {
		this.wordsFound = wordsFound;
	}	

	public void setGameTime(int gameTime) {
		this.gameTime = gameTime;
	}

	@Override
	public String toString() {
		return "Score [finalScore=" + finalScore + ", id=" + id + ", gameTime="
				+ gameTime + ", wordsFound=" + wordsFound + ", ranking="
				+ ranking + "]";
	}
}

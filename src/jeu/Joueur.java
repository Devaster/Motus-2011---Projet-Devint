package jeu;

import score.Score;

public class Joueur {

	public static final int TEACHER = 0, PUPIL = 1;

	private static int cnt = 0;

	private int id, age;
	private String firstname, lastname;
	private Score score;

	public Joueur(int id, String fn, String ln, int age) {
		if (fn != null && ln != null) {
			this.id = id;
			this.firstname = fn;
			this.lastname = ln;
			this.age = age;
			this.score = new Score(this.id, 0, 0, 0, 0);
		}
	}
	
	public int getPlayerId() {
		return id;
	}
	
	public int getPlayerAge() {
		return age;
	}

	public Joueur(String fn, String ln, int age, Score score) {
		this(++cnt, fn, ln, age);
	}

	public String getPlayerName() {
		return firstname + " " + lastname;
	}
	
	public Score getPlayerScore() {
		return score;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Joueur " + id + ": ");
		sb.append(firstname + " " + lastname);
		sb.append(" " + age + " ans\n");
		sb.append(score.toString());
		return sb.toString();
	}
}

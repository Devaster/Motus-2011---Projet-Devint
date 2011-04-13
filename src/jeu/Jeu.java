package jeu;

import java.util.LinkedList;
import java.util.Random;

import dictionnaires.Dictionary;
import dictionnaires.Word;

public class Jeu {

	private int longueur; // longueur des mots
	@SuppressWarnings("unused")
	private int nbMotsPartie; // nombre de mots au cours de la partie
	private int nbEssais; // nombre d'essais permis par mot
	private int nbTentatives;
	private Random random;
	private LinkedList<Word> mots;
	private String lastProposal;

	public Jeu(int longueur, int nbMots, int nbEssais) {
		this.longueur = longueur;
		this.nbMotsPartie = nbMots;
		this.nbEssais = nbEssais;
		random = new Random();
		lastProposal = null;
		nbTentatives = 0;
		Dictionary dico = new Dictionary("../ressources/dictionary.txt");
		Word aux;
		mots = new LinkedList<Word>();
		for (int i = 0; i < nbMots; i++) {
			do {
				aux = dico.pickOutWord();
			} while (aux.getWordName().length() != longueur);
			mots.add(aux);
		}
	}

	public boolean hasFound(String proposal) {
		nbTentatives++;
		boolean b = proposal.equals(mots.getFirst().getWordName());
		if (b) {
			lastProposal = null;
			mots.removeFirst();
		} else
			lastProposal = proposal;
		return b;
	}

	public String currentWord() {
		return mots.getFirst().getWordName();
	}

	public String getLastProposal() {
		return lastProposal;
	}

	public int randomLetter() {
		int n = random.nextInt(longueur);
		if ((lastProposal == null)
				|| (lastProposal.charAt(n) != mots.getFirst().getWordName()
						.charAt(n)))
			return n;
		else
			return randomLetter();

	}

	public boolean NoMoreTry() {
		return nbTentatives == nbEssais;
	}

	public boolean endGame() {
		return mots.isEmpty();
	}

}
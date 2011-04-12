package jeu;

import java.util.LinkedList;
import java.util.Random;

import dictionnaires.Dictionary;
import dictionnaires.Word;

public class Jeu {

	private int longueur;
	private int nbEssais;
	private int nbTentatives;
	private LinkedList<Word> mots;
	private Random random;
	
	protected int[] results;
	
	public Jeu(int longueur, int nbMots, int nbEssais) {
		this.longueur = longueur;
		this.nbEssais = nbEssais;
		
		nbTentatives = 0;
		results = new int[longueur];
		mots = new LinkedList<Word>();
		random = new Random();
		
		Dictionary dico = new Dictionary("../ressources/DicFra.txt");
		Word aux;
		
		for(int i = 0; i < longueur; i++) results[i] = 0;
		
		for(int j = 0; j < nbMots; j++) {
			
			do {
				aux = dico.pickUpWord();
			} while(!((aux.getWordName().length()==longueur)&&(aux.getWordName().matches("[^-]*"))));
			
			mots.add(aux);
		}
		
	}
	
	public boolean endGame() {
		return mots.isEmpty();
	}
	
	public boolean cantProposeAgain() {
		return (nbTentatives==nbEssais);
	}
	
	public boolean compare(String proposal) {
		nbTentatives++;
		String currentWord = currentWord();
		for(int i = 0; i < longueur; i++) {
			if(proposal.charAt(i)==currentWord.charAt(i)) results[i] = 1;
			else {
				if(currentWord.contains(""+proposal.charAt(i))) results[i] = -1;
				else results[i] = 0;
			}
		}
		return proposal.equals(currentWord());
	}
	
	public boolean hasFound() {
		for(int i = 0; i < results.length; i++) {
			if(results[i]!=1) return false;
		}
		return true;
	}
	
	protected String currentWord() {
		return mots.getFirst().getWordName();
	}
	
	protected String currentDef() {
		return mots.getFirst().getWordDefinition();
	}
	
	public void nextWord() {
		reset();
		mots.removeFirst();
	}
	
	protected void reset() {
		nbTentatives = 0;
		for(int i = 0; i < longueur; i++) results[i] = 0;
	}
	
	public int giveBonus() {
		int n = randomLetter();
		results[n] = 1;
		nbTentatives++;
		return 256*n+currentWord().charAt(n);
	}

	private int randomLetter() {
		int n = random.nextInt(longueur);
		return (results[n]==1) ? randomLetter() : n;
	}
	
}
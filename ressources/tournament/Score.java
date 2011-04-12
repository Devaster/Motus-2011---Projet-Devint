package tournament;

import tournament.*;

/**
 * Classe Score : représente un score pour une epreuve donnée
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
@SuppressWarnings("unused")
public class Score {
    private String epreuve;
    private int classement;
    private int nbVictoire;
    private int nbPoints;

    /**
     * Constructeur
     * 
     * @param uneEpreuve
     * @param unClassement
     * @param victoires
     */
    public Score(String uneEpreuve, int unClassement, int victoires, int points) {
        // tester les parametres
        if (uneEpreuve == null || unClassement < 0 || victoires < 0 || points < 0)
            throw new NullPointerException(
                    "Un ou des parametres du constructeur de Score est incorrect");
        // renseigner les attributs
        epreuve = uneEpreuve;
        classement = unClassement;
        nbVictoire = victoires;
        nbPoints = points;
    }

    /**
     * retourne l'epreuve concernée par ce score
     * 
     * @return
     */
    public String getEpreuve() {
        return epreuve;
    }

    /**
     * retourne le classement
     * 
     * @return
     */
    public int getClassement() {
        return classement;
    }

    /**
     * retourne le nombre de victoire
     * 
     * @return
     */
    public int getNbVictoire() {
        return nbVictoire;
    }
    
    public int getNbPoints() {
    	return nbPoints;
    }

    /**
     * Met à jour le classement
     * 
     * @param nouveau
     */
    public void setClassement(int nouveau) {
        classement = nouveau;
    }

    /**
     * Met à jour le nombre de victoire
     * 
     * @param nouveau
     */
    public void setNbVictoire(int nouveau) {
        nbVictoire = nouveau;
    }

    public void setNbPoints(int points) {
    	nbPoints = points;
    }
    
    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder resultat = new StringBuilder();

        // obtenir le suffixe de la position
        //
        String pos = "eme";
        if (classement == 1)
            pos = "er";

        // completer le resultat
        //
        resultat.append(epreuve + "(" + classement + pos + ":" + nbVictoire
                + " victoires " + nbPoints + "points)");

        return resultat.toString();
    }
}

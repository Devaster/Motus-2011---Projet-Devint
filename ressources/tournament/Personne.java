package tournament;

/**
 * Classe abstraite car aucun objet de type Personne ne sera construit
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
public abstract class Personne {

    private String surnom;

    /**
     * Constructeur
     * 
     * @param s
     */
    public Personne(String s) {
        if (s == null) {
            // test du parametre
            throw new NullPointerException(
                    "Il est obligatoire de saisir le nom de la personne !!");
        }
        surnom = s;
    }

    /**
     * 
     * @return le surnom de la personne
     */
    public String getNom() {
        return surnom;
    }

    /**
     * @return String
     */
    public String toString() {
        return "Name : " + surnom + "\n";
    }
}
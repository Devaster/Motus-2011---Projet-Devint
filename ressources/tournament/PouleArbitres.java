package tournament;

/**
 * Classe de la poule d'arbitres du tournoi
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 */
import java.util.*;

public class PouleArbitres {

    private List<Arbitre> listeArbitres;

    /**
     * On crée une liste vide d arbitres
     */
    public PouleArbitres(List<Arbitre> refList) {
        if (refList == null) {
            listeArbitres = new ArrayList<Arbitre>();
        }
        listeArbitres = refList;
    }

    /**
     * 
     * @return la liste d arbitres
     */
    public List<Arbitre> getPouleArbitres() {
        return listeArbitres;
    }

    /**
     * ajoute l arbitre a la liste
     * 
     * @param name
     */
    public boolean addRef(Arbitre ref) {
    	if (listeArbitres.add(ref))
    		return true;
    	return false;
    }
    
    public boolean removeRef(Arbitre ref) {
    	if (listeArbitres.remove(ref))
    		return true;
    	return false;
    }

    /**
     * 
     * @return un arbitre tire au hasard dans la poule ayant la compétence requise
     */
    public Arbitre chooseRandomlyRef(String competence) {
    	List<Arbitre> refList = this.getPouleArbitres();
    	Iterator<Arbitre> it = refList.iterator();
    	while (it.hasNext()) {
    		Arbitre ref = it.next();
    		if (!ref.getSkill().equalsIgnoreCase(competence))
    			refList.remove(ref);
    	}
        int randIndex = (int) (Math.random() * (refList.size() - 1));
        return refList.get(randIndex);
    }
    

    /**
     * @return String
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < listeArbitres.size(); i++) {
            s += listeArbitres.get(i).toString() + "\n";
        }
        return s;
    }
}
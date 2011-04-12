package tournament;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsing du fichier 'catalogue.txt'
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 * 
 */
public class Catalog {

	/**
	 * 
	 * @param unFichier
	 * @return nombre de lignes du fichier passé en paramètre
	 * @throws Exception
	 */
	public static int getNbLignes(String unFichier) throws Exception {
		BufferedReader buffer = new BufferedReader(new FileReader(unFichier));
		int i = 0;
		@SuppressWarnings("unused")
		String str;
		while ((str = buffer.readLine()) != null)
			i++;
		return i;
	}

	/**
	 * 
	 * @param fichier
	 * @return la liste d'arguments destinés à construire les épreuves
	 * @throws Exception
	 */
	public static List<String> readCatalogue(String fichier) throws Exception {
		int nbLignes = getNbLignes(fichier);
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fichier));
			// On ecarte les 2 premières lignes
			String line = br.readLine();
			line = br.readLine();
			// lecture du fichier texte
			for (int i = 0; i < nbLignes - 2; i++) {
				line = br.readLine();
				if (line.startsWith("----"))
					break;
				String delim = "[|]";
				String[] tokens = line.split(delim);
				for (int j = 0; j < tokens.length; j++) {
					list.add(tokens[j]);
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return list;
	}
}

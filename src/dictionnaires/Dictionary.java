package dictionnaires;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * 
 * @author julien
 * 
 */
public class Dictionary extends TreeSet<Word> {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initial capacity of the dictionary
	 */
	public static final int INIT_CAPACITY = 5000;

	/**
	 * 
	 */
	public Dictionary() {
		super();
	}

	/**
	 * 
	 * @param wl
	 */
	public Dictionary(List<Word> wl) {
		super(wl);
	}

	/**
	 * 
	 * @param dictxtfile
	 */
	public Dictionary(String dictxtfile) {
		this(readDictionary(dictxtfile));
	}

	/**
	 * 
	 * @return SortedSet<Word>
	 */
	public SortedSet<Word> getAllWords() {
		return tailSet(first());
	}

	/**
	 * 
	 * @param theme
	 * @return SortedSet<Word>
	 */
	public SortedSet<Word> getWordsByTheme(String theme) {
		SortedSet<Word> coll = tailSet(first());
		Iterator<Word> it = coll.iterator();
		while (it.hasNext())
			if (!it.next().getTheme().equalsIgnoreCase(theme))
				it.remove();
		return coll;
	}
	
	/**
	 * 
	 * @return Word
	 */
	public Word pickUpWord() {
		Word w = null;
		Random r = new Random();
		SortedSet<Word> c = getAllWords();
		int size = c.size();
		int n = r.nextInt(size);
		Iterator<Word> it = c.iterator();
		for (int i=0; i<n; i++)
			if (it.hasNext())
				w = it.next();
		return w;
	}
	
	/**
	 * 
	 * @param theme
	 * @return Word
	 */
	public Word pickUpWordOfTheTheme(String theme) {
		Word w = null;
		Random r = new Random();
		SortedSet<Word> c = getWordsByTheme(theme);
		int size = c.size();
		int n = r.nextInt(size);
		Iterator<Word> it = c.iterator();
		for (int i=0; i<n; i++)
			if (it.hasNext())
				w = it.next();
		return w;
	}
	
	/**
	 * 
	 * @param word
	 * @return boolean
	 */
	public boolean containsWord(Word word) {
		return contains(word);
	}

	/**
	 * 
	 * @param word
	 * @return boolean
	 */
	public boolean addWord(Word word) {
		return add(word);
	}

	/**
	 * 
	 * @param wl
	 * @return boolean
	 */
	public boolean addAllWords(List<Word> wl) {
		return addAll(wl);
	}

	/**
	 * 
	 * @param word
	 * @return boolean
	 */
	public boolean removeWord(Word word) {
		return remove(word);
	}

	/**
	 * 
	 */
	public void removeAllWords() {
		clear();
	}

	/**
	 * 
	 * @param file
	 * @return List
	 */
	private static List<Word> readDictionary(String file) {
		List<Word> list = null; 
		FileReader fr;
		Scanner sc;
		String buf, name, def;
		StringTokenizer stbuf, stdef;
		Word word;
		try {
			list = new ArrayList<Word>(INIT_CAPACITY);
			fr = new FileReader(file);
			sc = new Scanner(fr);
			while (sc.hasNext()) {
				buf = sc.nextLine();
				stbuf = new StringTokenizer(buf, ",");
				name = stbuf.nextToken().replace("\"", "");
				def = stbuf.nextToken().replace("\"", "");
				stdef = new StringTokenizer(def, "\\");
				if (stdef.hasMoreTokens())
					def = stdef.nextToken() + "\n";
				while (stdef.hasMoreTokens())
					def += stdef.nextToken().substring(1) + "\n";
				word = new Word(name, def, null);
				list.add(word);
			}
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}

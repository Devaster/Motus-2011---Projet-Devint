package dictionnaires;

/**
 * 
 * @author julien
 * 
 */
public class Word implements Comparable<Word> {

	/**
	 * Default theme : French Language
	 */
	public static final String DEFAULT_THEME = "langue francaise";

	/**
	 * Word counter
	 */
	static int cnt = 0;

	/**
	 * 
	 */
	int id;
	String name, definition, theme;

	/**
	 * 
	 * @param name
	 * @param definition
	 * @param theme
	 */
	public Word(String name, String definition, String theme) {
		this(++cnt, name, definition, theme);
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param definition
	 * @param theme
	 */
	public Word(int id, String name, String definition, String theme) {
		this.id = id;
		this.name = name;
		this.definition = definition;
		if (theme == null)
			this.theme = DEFAULT_THEME;
		else
			this.theme = theme;
	}

	/**
	 * 
	 * @return id
	 */
	public int getWordId() {
		return id;
	}

	/**
	 * 
	 * @return wordNama
	 */
	public String getWordName() {
		return name;
	}

	/**
	 * 
	 * @return definition
	 */
	public String getWordDefinition() {
		return definition;
	}

	/**
	 * 
	 * @return theme
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * 
	 * @param newDef
	 */
	public void setDef(String newDef) {
		definition = newDef;
	}

	/**
	 * 
	 * @param newTheme
	 */
	public void setTheme(String newTheme) {
		theme = newTheme;
	}

	/**
	 * 
	 * @return String representation of the word
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#" + id + " " + name + "(" + theme + ") : " + definition
				+ "\n");
		return sb.toString();
	}

	/**
	 * 
	 * @param w
	 */
	@Override
	public int compareTo(Word w) {
		return this.name.compareTo(w.name);
	}
}

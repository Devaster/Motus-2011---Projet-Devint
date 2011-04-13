package main;

import static hsqldb.DataBase.*;
import java.sql.Connection;
import hsqldb.DataBase;

/**
 * Sample test client
 * 
 * @author julien
 * 
 */
public class Main {

	/**
	 * 
	 */
	static DataBase myDB;
	static Connection conn;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		myDB = new DataBase();
		conn = createConnection();
		createPlayersTable(conn);
		createScoresTable(conn);
		//dropWordsTable(conn);
		//createWordsTable(conn);
		//Dictionary dic = new Dictionary("./ressources/dictionary.txt");
		//addAllWordsToDB(conn, dic);
		//Dictionary dic2 = retrieveWordsFromBD(conn);
		//System.out.println(dic2);
		//removeAllWordsFromDB(conn);
		//Word w = new Word(30000, "essai", "test micro", "test db");
		//addWordToDB(conn, w);
		//changeWordTheme(conn, w.getWordName(), "new theme");
		//removeWordFromDB(conn, "essai");
		disconnect(conn);
	}
}
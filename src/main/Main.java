package main;

import static hsqldb.DataBase.*;
import java.sql.Connection;
import dictionnaires.Dictionary;
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
		dropWordsTable(conn);
		createWordsTable(conn);
		String dictxt = "/home/julien/workspace/Motus2011/ressources/dictionary.txt";
		Dictionary dic = new Dictionary(dictxt);
		System.out.println(dic);
		addAllWordsToDB(conn, dic);
		Dictionary dic2 = retrieveWordsFromBD(conn);
		System.out.print(dic2);
		disconnect(conn);
	}
}

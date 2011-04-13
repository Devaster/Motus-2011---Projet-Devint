package hsqldb;

import static dictionnaires.Dictionary.INIT_CAPACITY;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import score.Score;

import jeu.Equipe;
import jeu.Joueur;
import dictionnaires.Dictionary;
import dictionnaires.Word;

/**
 * 
 * @author julien
 * 
 */
public class DataBase {

	/**
	 * Empty Constructor
	 */
	public DataBase() {
	}

	/**
	 * Data Base Connect
	 * 
	 * @return connection
	 */
	public static Connection createConnection() {
		Connection conn = null;
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:file:motusdb"; // stand-alone mode
		String username = "julien";
		String password = "";
		try {
			// INSTALL/load the Driver (Vendor specific Code)
			Class.forName(driver).newInstance();
			System.out.println("Driver loading OK.");
			// Connection to the database at URL with user name and password
			conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connection to the DB OK.");
		} catch (ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		} catch (InstantiationException e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException ex) {
			// print err decent error messages
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * Data Base Disconnect
	 * 
	 * @param conn
	 */
	public static void disconnect(Connection conn) {
		try {
			if (conn != null) {
				conn.setAutoCommit(true);
				Statement st = getStatement(conn);
				st.execute("SHUTDOWN");
				System.out.println("HSQLDB stopped.");
				conn.close();
				System.out.println("DB disconnection OK.");
			}
		} catch (SQLException ex) {
			// print err decent error messages
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		}
	}

	/**
	 * Get Statement from the connection
	 * 
	 * @param conn
	 * @return stat
	 */
	public static Statement getStatement(Connection conn) {
		Statement stat = null;
		try {
			stat = conn.createStatement();
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stat;
	}

	/**
	 * Execute and provide the result of a SQL request
	 * 
	 * @param conn
	 *            , query
	 * @return rs
	 */
	public static ResultSet createGenericResultSet(Connection conn, String query) {
		ResultSet rs = null;
		try {
			rs = getStatement(conn).executeQuery(query);
		} catch (SQLException ex) {
			System.err.println("==> SQLException:");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Create table words if not exists
	 * 
	 * @param conn
	 * @return boolean
	 */
	public static boolean createWordsTable(Connection conn) {
		Statement stat = getStatement(conn);
		String query = "CREATE TABLE IF NOT EXISTS words (id INT NOT NULL,"
				+ " name VARCHAR(64) NOT NULL, definition LONGVARCHAR NULL,"
				+ " theme VARCHAR(64) NULL, PRIMARY KEY( id ));";
		boolean status = false;
		try {
			if (stat.executeUpdate(query) == 0) {
				status = true;
				System.out.println("Table \'words\' created.");
			}
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Create table players if not exists
	 * 
	 * @param conn
	 * @return boolean
	 */
	public static boolean createPlayersTable(Connection conn) {
		Statement stat = getStatement(conn);
		String query = "CREATE TABLE IF NOT EXISTS players (id INT NOT NULL,"
				+ " name VARCHAR(64) NOT NULL, age INT NULL,"
				+ " scoreId INT NOT NULL, PRIMARY KEY( id ));";
		boolean status = false;
		try {
			if (stat.executeUpdate(query) == 0) {
				status = true;
				System.out.println("Table \'players\' created.");
			}
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Create table scores if not exists
	 * 
	 * @param conn
	 * @return boolean
	 */
	public static boolean createScoresTable(Connection conn) {
		Statement stat = getStatement(conn);
		String query = "CREATE TABLE IF NOT EXISTS scores (id INT NOT NULL,"
				+ " ranking INT NULL, finalScore INT NULL, wordsFound INT NULL,"
				+ " gameTime INT NULL, PRIMARY KEY( id ));";
		boolean status = false;
		try {
			if (stat.executeUpdate(query) == 0) {
				status = true;
				System.out.println("Table \'scores\' created.");
			}
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Fill the words table in the DB from a dictionary loaded in the RAM
	 * 
	 * @param conn
	 * @param dic
	 * @return boolean
	 */
	public static boolean addAllWordsToDB(Connection conn, Dictionary dic) {
		SortedSet<Word> words = dic.getAllWords();
		int cnt = 0;
		Iterator<Word> it = words.iterator();
		Word word;
		while (it.hasNext()) {
			word = it.next();
			if (addWordToDB(conn, word))
				System.out.println(++cnt);
		}
		return cnt == words.size();
	}

	/**
	 * Introduction of a new word in the DB
	 * 
	 * @param conn
	 * @param word
	 * @return status
	 */
	public static boolean addWordToDB(Connection conn, Word word) {
		int id = word.getWordId();
		String name = word.getWordName().replace('\'', ' ');
		String def = word.getWordDefinition().replace('\'', ' ');
		String theme = word.getTheme().replace('\'', ' ');
		String query = "INSERT INTO words (id, name, definition, theme) VALUES ("
				+ id + ",'" + name + "', '" + def + "', '" + theme + "');";
		Statement stat = getStatement(conn);
		boolean status = false;
		try {
			if (stat.executeUpdate(query) != 0)
				status = true;
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Add a player record in the DB
	 * 
	 * @param conn
	 * @param word
	 * @return status
	 */
	public static boolean addPlayerToDB(Connection conn, Joueur player) {
		int id = player.getPlayerId();
		String name = player.getPlayerName().replace('\'', ' ');
		int age = player.getPlayerAge();
		int scoreId = player.getPlayerScore().getScoreId();
		String query = "INSERT INTO words (id, name, age, scoreId) VALUES ("
				+ id + ",'" + name + "', " + age + ", " + scoreId + ");";
		Statement stat = getStatement(conn);
		boolean status = false;
		try {
			if (stat.executeUpdate(query) != 0)
				status = true;
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Add all the players of a given team in the DB
	 * 
	 * @param conn
	 * @param team
	 * @return boolean
	 */
	public static boolean addTeamToDB(Connection conn, Equipe team) {
		int cnt = 0;
		for (Joueur player : team.getPlayers())
			if (addPlayerToDB(conn, player))
				cnt++;
		return cnt == team.getPlayers().size();
	}

	/**
	 * Add a score record in the DB
	 * 
	 * @param conn
	 * @param word
	 * @return status
	 */
	public static boolean addScoreToDB(Connection conn, Score score) {
		int id = score.getScoreId();
		int ranking = score.getRanking();
		int finalScore = score.getFinalScore();
		int wordsFound = score.getWordsFound();
		int gameTime = score.getGameTime();
		String query = "INSERT INTO scores (id, ranking, finalScore, wordsFound, gameTime) VALUES ("
				+ id
				+ ","
				+ ranking
				+ ", "
				+ finalScore
				+ ", "
				+ wordsFound
				+ ", " + gameTime + ");";
		Statement stat = getStatement(conn);
		boolean status = false;
		try {
			if (stat.executeUpdate(query) != 0)
				status = true;
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Update the score of a given player in the DB
	 * 
	 * @param conn
	 * @param newScore
	 * @param player
	 * @return int
	 */
	public int updateScore(Connection conn, Score newScore, Joueur player) {
		String query = "UPDATE scores SET ranking = " + newScore.getRanking()
				+ ", finalScore = " + newScore.getFinalScore()
				+ ", worsFound = " + newScore.getWordsFound() + ", gameTime = "
				+ newScore.getGameTime() + " WHERE id = "
				+ player.getPlayerId() + ";";
		Statement stat = getStatement(conn);
		int status = 0;
		try {
			status = stat.executeUpdate(query);
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * clears the words table (Use with caution)
	 * 
	 * @param conn
	 * @return boolean
	 */
	public static boolean removeAllWordsFromDB(Connection conn) {
		String query = "DELETE FROM words;";
		Statement stat = getStatement(conn);
		boolean status = false;
		try {
			if (stat.execute(query))
				status = true;
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Remove the words table from the DB
	 * 
	 * @param conn
	 * @return boolean
	 */
	public static boolean dropWordsTable(Connection conn) {
		String query = "DROP TABLE words;";
		Statement stat = getStatement(conn);
		boolean status = false;
		try {
			if (!stat.execute(query)) {
				status = true;
				System.out.println("Table \'words\' deleted.");
			}
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Remove a single word in the table
	 * 
	 * @param conn
	 * @param wordName
	 * @return status
	 */
	public static boolean removeWordFromDB(Connection conn, String wordName) {
		String query = "DELETE FROM words WHERE name = '" + wordName + "';";
		Statement stat = getStatement(conn);
		boolean status = false;
		try {
			if (stat.executeUpdate(query) != 0)
				status = true;
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Get all the words from the DB
	 * 
	 * @param conn
	 * @return words
	 */
	public static Dictionary retrieveWordsFromBD(Connection conn) {
		List<Word> words = new ArrayList<Word>(INIT_CAPACITY);
		int cnt = 0;
		try {
			String query = "SELECT * FROM words;";
			ResultSet rs = createGenericResultSet(conn, query);
			while (rs.next()) { // read the result set
				Word word = new Word(rs.getInt("id"), rs.getString("name"),
						rs.getString("definition"), rs.getString("theme"));
				words.add(word);
				System.out.println(++cnt);
			}
		} catch (SQLException ex) {
			System.err.println("==> SQLException:");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Dictionary(words);
	}

	/**
	 * Get all the words of the theme given
	 * 
	 * @param conn
	 * @param theme
	 * @return words
	 */
	public static List<Word> retrieveWordsByThemeFromBD(Connection conn,
			String theme) {
		List<Word> words = new ArrayList<Word>();
		try {
			String query = "SELECT * FROM words WHERE theme = '" + theme + "';";
			ResultSet rs = createGenericResultSet(conn, query);
			while (rs.next()) { // read the result set
				Word word = new Word(rs.getInt("id"), rs.getString("name"),
						rs.getString("definition"), rs.getString("theme"));
				words.add(word);
			}
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return words;
	}

	/**
	 * Change a word's definition
	 * 
	 * @param conn
	 * @param wordName
	 * @param newDef
	 * @return status
	 */
	public static int changeWordDef(Connection conn, String wordName,
			String newDef) {
		String query = "UPDATE words SET definition = '"
				+ newDef.replace('\'', ' ') + "' WHERE name = '" + wordName
				+ "';";
		Statement stat = getStatement(conn);
		int status = 0;
		try {
			status = stat.executeUpdate(query);
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Change a word's theme
	 * 
	 * @param conn
	 * @param wordName
	 * @param newTheme
	 * @return status
	 */
	public static int changeWordTheme(Connection conn, String wordName,
			String newTheme) {
		String query = "UPDATE words SET theme = '"
				+ newTheme.replace('\'', ' ') + "' WHERE name = '" + wordName
				+ "';";
		Statement stat = getStatement(conn);
		int status = 0;
		try {
			status = stat.executeUpdate(query);
		} catch (SQLException ex) {
			System.err.println("==> SQLException: ");
			while (ex != null) {
				System.err.println("Message:" + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("ErrorCode: " + ex.getErrorCode());
				ex = ex.getNextException();
				System.err.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
}
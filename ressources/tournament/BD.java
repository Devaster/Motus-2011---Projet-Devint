package tournament;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Classe Data Base
 * 
 * @author Faure-Vidal Laurène, Ochi Ghazi, Jeddi Kévin, Michel Julien
 */
public class BD {

	/**
	 * Constructeur par defaut de la classe BD
	 * 
	 * @param
	 */
	public BD() {
	}

	/**
	 * Active l'utilisation des clés étrangères
	 * 
	 * @param Connection
	 * @return
	 */
	public static void SetForeignKeys(Connection conn) {
		String query = "pragma foreign_keys=ON;";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connection à la Base de données
	 * 
	 * @param
	 * @return Connection
	 */
	public static Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager
					.getConnection("jdbc:sqlite:/home/julien/workspace/Tournament_Management/ressources/BaseDeDonnees.db");
			SetForeignKeys(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Déconnection à la Base de données et Commit réalisé
	 * 
	 * @param Connection
	 * @return
	 */
	public static void deconnection(Connection conn) {
		try {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			// connection close failed.
			System.err.println(e);
		}
	}

	/**
	 * Récupération du Statement à partir de la connection
	 * 
	 * @param Connection
	 * @return Statement
	 */
	public static Statement getStatement(Connection conn) {
		Statement stat = null;
		try {
			stat = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stat;
	}

	/**
	 * Exécute et génére le résultat d'une requête SQLite
	 * 
	 * @param Connection
	 *            , String
	 * @return ResultSet
	 */
	public static ResultSet createGenericResultSet(Connection conn, String query) {
		ResultSet rs = null;
		try {
			rs = getStatement(conn).executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Vide la base
	 * 
	 * @param Connection
	 * @return
	 * @throws SQLException
	 */
	public static void ViderBase(Connection conn) throws SQLException {
		ResultSet rs = null;
		try {
			rs = conn.createStatement().executeQuery(
					"SELECT name FROM sqlite_master WHERE type='table'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList<String> AS = new ArrayList<String>();

		while (rs.next()) {
			AS.add(rs.getString(1));
		}

		for (String s : AS) {
			try {
				conn.createStatement().execute("DELETE FROM " + s);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Insertion d'un nouveau joueur
	 * 
	 * @param Connection
	 *            , Joueur
	 * @return
	 */
	public static void AjouterJoueur(Connection conn, Joueur J) {
		String query = "INSERT INTO Participant VALUES(" + J.getPlayerID()
				+ ", '" + J.getNom() + "');";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insertion d'une compétence
	 * 
	 * @param Connection
	 *            , Joueur
	 * @return
	 */
	public static void AjouterCompetence(Connection conn, Joueur J, Epreuve Ep) {
		String query = "INSERT INTO Competence VALUES(" + J.getPlayerID()
				+ ", " + Ep.getCompetID() + ");";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insertion d'une nouvelle équipe
	 * 
	 * @param Connection
	 *            , Equipe
	 * @return
	 */
	public static void AjouterEquipe(Connection conn, Equipe EQ) {
		String query = "INSERT INTO EQUIPE VALUES(" + EQ.getTeamID() + ", '"
				+ EQ.getNom() + "');";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insertion d'un nouvel arbitre
	 * 
	 * @param Connection
	 *            , Arbitre, String, String
	 * @return
	 */
	public static void AjouterArbritre(Connection conn, Arbitre Arb,
			String login, String MDP) {
		String query = "INSERT INTO Staff VALUES(" + Arb.getRefID() + ", '"
				+ Arb.getNom() + 0 + ", '" + login + "', '" + MDP + "');";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insertion d'un nouveau tournoi
	 * 
	 * @param Connection
	 *            , Tournament, String
	 * @return
	 */
	public static void AjouterTournoi(Connection conn, Tournament Tour,
			String nom) {
		String query = "INSERT INTO Tournoi VALUES(" + Tour.getTournamentID()
				+ ", '" + nom + "', NULL);";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insertion d'une nouvelle épreuve
	 * 
	 * @param Connection
	 *            , Epreuve
	 * @return
	 */
	public static void AjouterEpreuve(Connection conn, Epreuve Ep) {
		String query = "INSERT INTO Epreuve VALUES(" + Ep.getCompetID() + ", '"
				+ Ep.getNom() + "', '" + Ep.getType() + "', " + Ep.getDuree()
				+ ", " + Ep.getPointsWinner() + ", '" + Ep.getHourStart()
				+ "');";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inscription d'une nouvelle equipe à un tournoi
	 * 
	 * @param Connection
	 *            , Equipe, Tournament
	 * @return
	 */
	public static void InscrireEquipeTournoi(Connection conn, Equipe Eq,
			Tournament T) {
		String query = "INSERT INTO Equipe_Tournoi VALUES(" + Eq.getTeamID()
				+ ", " + T.getTournamentID() + ",NULL);";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inscription d'un nouvel arbitre à une epreuve
	 * 
	 * @param Connection
	 *            , Arbitre, Epreuve
	 * @return
	 */
	public static void InscrireArbitreEpreuve(Connection conn, Arbitre Arb,
			Epreuve Ep) {
		String query = "INSERT INTO Epreuve_Staff VALUES(" + Ep.getCompetID()
				+ ", " + Arb.getRefID() + ");";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inscription d'un nouveau joueur à une equipe
	 * 
	 * @param Connection
	 *            , Joueur, Equipe
	 * @return
	 */
	public static void InscrireJoueurEquipe(Connection conn, Joueur J, Equipe Eq) {
		String query = "Insert into PARTICIPANT_EQUIPE Values("
				+ J.getPlayerID() + ", " + Eq.getTeamID() + ");";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inscription d'une nouvelle équipe à une epreuve
	 * 
	 * @param Connection
	 *            , Equipe, Epreuve
	 * @return
	 */
	public static void InscrireEquipeEpreuve(Connection conn, Equipe Eq,
			Epreuve Ep) {
		String query = "INSERT INTO Epreuve_Equipe VALUES(" + Ep.getCompetID()
				+ ", " + Eq.getTeamID() + ");";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inscription d'un nouveau match
	 * 
	 * @param Connection
	 *            , Match, Equipe, Equipe, Arbitre, Tournament
	 * @return
	 */
	public static void CreerMatch(Connection conn, Match M, Equipe EqA,
			Equipe EqB, Arbitre Arb, Tournament T) {
		String query = "INSERT INTO Match VALUES(" + M.getMatchID() + ", "
				+ EqA.getTeamID() + ", " + EqB.getTeamID() + ", "
				+ Arb.getRefID() + ", " + T.getTournamentID() + ", NULL);";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inscription d'un nouveau match
	 * 
	 * @param Connection
	 *            , Match, Equipe, Equipe, Arbitre, Tournament
	 * @return
	 */
	public static void SupprimerJoueur(Connection conn, Joueur J) {
		String query = "DELETE FROM PARTICIPANT where id_Part = "
				+ J.getPlayerID() + ";";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Suppression d'une équipe
	 * 
	 * @param Connection
	 *            , Equipe
	 * @return
	 */
	public static void SupprimerEquipe(Connection conn, Equipe EQ) {
		String query = "DELETE FROM EQUIPE where id_Eq = " + EQ.getTeamID()
				+ ";";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Suppression d'un arbitre
	 * 
	 * @param Connection
	 *            , Arbitre
	 * @return
	 */
	public static void SupprimerArbritre(Connection conn, Arbitre Arb) {
		String query = "DELETE FROM Staff where id_Staff = " + Arb.getRefID()
				+ ";";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Suppression d'un tournoi
	 * 
	 * @param Connection
	 *            , Tournament
	 * @return
	 */
	public static void SupprimerTournoi(Connection conn, Tournament Tour) {
		String query = "DELETE FROM Tournoi where id_Tour = "
				+ Tour.getTournamentID() + ";";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Suppression d'une épreuve
	 * 
	 * @param Connection
	 *            , Epreuve
	 * @return
	 */
	public static void SupprimerEpreuve(Connection conn, Epreuve Ep) {
		String query = "DELETE FROM EPREUVE where id_Ep = " + Ep.getCompetID()
				+ ";";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ajoute du score d'un match
	 * 
	 * @param Connection
	 *            , Match, int
	 * @return
	 */
	public static void AffecterGagnantMatch(Connection conn, Match M,
			int IDEquipeGagnante) {
		String query = "UPDATE Match SET gagnant = " + IDEquipeGagnante
				+ " WHERE id_Match = " + M.getMatchID() + ";";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ajout des points d'une équipe
	 * 
	 * @param Connection
	 *            , Equipe, Tournament, int
	 * @return
	 */
	public static void AffecterPointsEquipeTournoi(Connection conn, Equipe Eq,
			Tournament T, int Points) {
		String query = "UPDATE Equipe_Tournoi SET points = " + Points
				+ " WHERE id_Eq = " + Eq.getTeamID() + "AND id_Tour = "
				+ T.getTournamentID() + " ;";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modification d'un joueur
	 * 
	 * @param Connection
	 *            , Joueur
	 * 
	 * @return
	 */
	public static void modifierJoueur(Connection conn, Joueur J) {
		String query = "UPDATE Participant SET nom = '" + J.getNom()
				+ "' WHERE id_Part = " + J.getPlayerID() + " ;";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modification tous les joueurs
	 * 
	 * @param Connection
	 *            , List<Joueur>
	 * 
	 * @return
	 */
	public static void modifierAllJoueur(Connection conn, List<Joueur> MyList) {
		for (int i = 0; i < MyList.size(); ++i) {
			Joueur J = MyList.get(i);
			modifierJoueur(conn, J);
		}
	}

	/**
	 * Modification d'un Arbitre
	 * 
	 * @param Connection
	 *            , Arbitre
	 * 
	 * @return
	 */
	public static void modifierArbitre(Connection conn, Arbitre Arb) {
		String query = "UPDATE Staff SET nom = '" + Arb.getNom()
				+ "' WHERE id_Staff = " + Arb.getRefID() + " ;";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modification de tous les Arbitres
	 * 
	 * @param Connection
	 *            , List<Arbitre>
	 * 
	 * @return
	 */
	public static void modifierAllArbitre(Connection conn, List<Arbitre> MyList) {
		for (int i = 0; i < MyList.size(); ++i) {
			Arbitre Arb = MyList.get(i);
			modifierArbitre(conn, Arb);
		}
	}

	/**
	 * Modification d'une Equipe
	 * 
	 * @param Connection
	 *            , Equipe
	 * 
	 * @return
	 */
	public static void modifierEquipe(Connection conn, Equipe Eq) {
		String query = "UPDATE Equipe SET nom = '" + Eq.getNom()
				+ "' WHERE id_Eq = " + Eq.getTeamID() + " ;";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modification de toutes les Equipes
	 * 
	 * @param Connection
	 *            , List<Equipe>
	 * 
	 * @return
	 */
	public static void modifierAllEquipe(Connection conn, List<Equipe> MyList) {
		for (int i = 0; i < MyList.size(); ++i) {
			Equipe Eq = MyList.get(i);
			modifierEquipe(conn, Eq);
		}
	}

	/**
	 * Modification d'une Epreuve
	 * 
	 * @param Connection
	 *            , Epreuve
	 * 
	 * @return
	 */
	public static void modifierEpreuve(Connection conn, Epreuve Ep) {
		String query = "UPDATE Epreuve SET nom = '" + Ep.getNom()
				+ "' , type = " + Ep.getType() + " , duree = " + Ep.getDuree()
				+ " , points = " + Ep.getPointsWinner() + " , heure = '"
				+ Ep.getHourStart() + "' WHERE id_Ep = " + Ep.getCompetID()
				+ " ;";
		Statement stat = getStatement(conn);
		try {
			stat.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modification de toutes les Epreuves
	 * 
	 * @param Connection
	 *            , List<Epreuve>
	 * 
	 * @return
	 */
	public static void modifierAllEpreuve(Connection conn, List<Epreuve> MyList) {
		for (int i = 0; i < MyList.size(); ++i) {
			Epreuve Ep = MyList.get(i);
			modifierEpreuve(conn, Ep);
		}
	}

	/**
	 * Mise a jour de toutes la base
	 * 
	 * @param Connection
	 *            , List<Equipe>, List<Joueur>, List<Arbitre> lArb,
	 *            List<Epreuve>
	 * 
	 * @return
	 */
	public static void MAJBD(Connection conn, List<Equipe> LEq,
			List<Joueur> Lj, List<Arbitre> LArb, List<Epreuve> LEp) {
		modifierAllJoueur(conn, Lj);
		modifierAllEquipe(conn, LEq);
		modifierAllArbitre(conn, LArb);
		modifierAllEpreuve(conn, LEp);
	}

	/**
	 * Recupérer tous les arbitres
	 * 
	 * @param Connection
	 * 
	 * @return List<Arbitre>
	 */
	public static List<Arbitre> RecupArbitre(Connection conn) {
		List<Arbitre> LArb = new ArrayList<Arbitre>();
		try {
			ResultSet rs = createGenericResultSet(conn, "select * from Staff");

			while (rs.next()) { // read the result set
				Arbitre Arb = new Arbitre(rs.getInt("id_Staff"),
						rs.getString("nom"), "");
				LArb.add(Arb);
			}
		} catch (SQLException e) { // if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return LArb;
	}

	/**
	 * Recupérer tous les joueurs
	 * 
	 * @param Connection
	 * 
	 * @return List<Joueur>
	 */
	public static List<Joueur> RecupJoueur(Connection conn) {
		List<Joueur> LJ = new ArrayList<Joueur>();
		try {
			ResultSet rs = createGenericResultSet(conn,
					"select * from Participant");

			while (rs.next()) { // read the result set
				Joueur J = new Joueur(rs.getInt("id_Part"),
						rs.getString("nom"), null);
				LJ.add(J);
			}
		} catch (SQLException e) { // if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return LJ;
	}

	/**
	 * Recupérer toutes les équipes
	 * 
	 * @param Connection
	 * 
	 * @return List<Equipe>
	 */
	public static List<Equipe> RecupEquipe(Connection conn) {
		List<Equipe> LEq = new ArrayList<Equipe>();
		try {
			ResultSet rs = createGenericResultSet(conn, "select * from Equipe");

			while (rs.next()) { // read the result set
				Equipe Eq = new Equipe(rs.getInt("id_Eq"), rs.getString("nom"),
						null, null, null);
				LEq.add(Eq);
			}
		} catch (SQLException e) { // if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return LEq;
	}

	/**
	 * Recupérer toutes les tournois
	 * 
	 * @param Connection
	 * 
	 * @return List<Tournament>
	 */
	public static List<Tournament> RecupTournoi(Connection conn) {
		List<Tournament> LT = new ArrayList<Tournament>();
		try {
			ResultSet rs = createGenericResultSet(conn,
					"select DISTINCT id_Tour from Tournoi order by id_Tour");
			while (rs.next()) { // read the result set
				Tournament T = new Tournament(rs.getInt("id_Tour"), null);
				LT.add(T);
			}
		} catch (SQLException e) { // if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return LT;
	}

	/*
	 * public static void main(String[] args) throws ClassNotFoundException { /*
	 * // load the sqlite-JDBC driver using the current class loader
	 * Class.forName("org.sqlite.JDBC");
	 * 
	 * Connection connection = null; try { // create a database connection
	 * connection = createConnection();
	 * 
	 * Statement stat = getStatement(connection);
	 * 
	 * ResultSet rs = createGenericResultSet(connection,
	 * "select * from Equipe"); while (rs.next()) { // read the result set
	 * System.out.println("nom de l'équipe = " + rs.getString("nom"));
	 * System.out.println("id de l'équipe = " + rs.getInt("id_Eq")); } } catch
	 * (SQLException e) { // if the error message is "out of memory", // it
	 * probably means no database file is found
	 * System.err.println(e.getMessage()); } finally { try { if (connection !=
	 * null) connection.setAutoCommit(true); connection.close(); } catch
	 * (SQLException e) { // connection close failed. System.err.println(e); } }
	 * 
	 * 
	 * Class.forName("org.sqlite.JDBC");
	 * 
	 * Connection connection = null; // create a database connection connection
	 * = createConnection(); ArrayList<String> L = new ArrayList<String>();
	 * Joueur J = new Joueur("ZIDANE", L); AjouterJoueur(connection, J); try {
	 * if (connection != null) connection.setAutoCommit(true);
	 * connection.close(); } catch (SQLException e) { // connection close
	 * failed. System.err.println(e); } }
	 */
}

package com.scub.foundation.tutorial.hsqldb;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
/**
 * Classe de test du tutoriel sur HSQLDB
 *
 * @author Nicolas Prouteau
 *
 */
public class GestionnaireUtilisateurTest {
 
    // instance statique de la classe gestionnaireUtilisateur que l'on va utiliser pour faire appel aux méthodes
    private static GestionnaireUtilisateurs gestionnaireUtilisateurs = new GestionnaireUtilisateurs();
 
    /**
     * Fonction de test des fonctionnalités d'HSQLDB
     *
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        // On se connecte à la base
        gestionnaireUtilisateurs.connexionDB();
        // On créer une table users
        // On commence par créer la requête SQL
        String requeteTableUsers = "CREATE TABLE users ( idUser INTEGER IDENTITY, login VARCHAR(256), password VARCHAR(256))";
        // Puis on l'execute
        gestionnaireUtilisateurs.executerUpdate(requeteTableUsers);
 
        // On insère ensuite quelques utilisateurs
        String requeteUser1 = "INSERT INTO users(login,password) VALUES('straumat', 'straumat16')";
        String requeteUser2 = "INSERT INTO users(login,password) VALUES('jgoncalves', 'jgoncalves16')";
        String requeteUser3 = "INSERT INTO users(login,password) VALUES('sgoumard', 'sgoumard16')";
 
        // Puis on l'execute
        gestionnaireUtilisateurs.executerUpdate(requeteUser1);
        gestionnaireUtilisateurs.executerUpdate(requeteUser2);
        gestionnaireUtilisateurs.executerUpdate(requeteUser3);
 
        // On créé la requête SQL
        String requeteSelectUsers = "SELECT * FROM users";
 
        // Puis on l'execute
        ResultSet resultat = gestionnaireUtilisateurs.executerRequete(requeteSelectUsers);
 
        // On parcours le résultat de la requête et on l'affiche dans la console
        System.out.println("idUser\t\tlogin\t\t\tpassword");
        while(resultat.next()){
            System.out.println(resultat.getString("idUser") + "\t\t" + resultat.getString("login") + "\t\t" + resultat.getString("password"));
        }
    }
}
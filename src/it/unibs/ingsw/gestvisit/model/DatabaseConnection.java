package src.it.unibs.ingsw.gestvisit.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/gestione_visite";
    private static final String USER = "root"; // Cambia se hai impostato un altro utente
    private static final String PASSWORD = ""; // Cambia se hai impostato una password

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Errore di connessione al database: " + e.getMessage());
            return null;
        }
    }

}

package src.model.db;

import src.model.Utente;
import src.view.ConsoleView;
import src.controller.ThreadPoolController;
import java.sql.*;
import java.util.concurrent.ExecutorService;

public abstract class DatabaseManager {
    protected ConsoleView consoleView;
    protected ExecutorService executorService;

    public DatabaseManager(ThreadPoolController threadPoolManager) {
        this.executorService = threadPoolManager.createThreadPool(4);
        this.consoleView = new ConsoleView();
    }

    protected boolean recordEsiste(String sql, Object... parametri) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametri.length; i++) {
                pstmt.setObject(i + 1, parametri[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            consoleView.mostraErrore("Errore verifica record: " + e.getMessage());
        }
        return false;
    }

    //Metodo per aggiungere in utenti unificati
    protected void aggiungiUtenteUnificato(Utente utente) {
        String nome = utente.getNome();
        String cognome = utente.getCognome();
        String email = utente.getEmail();
        String password = utente.getPassword();
        String tipoUtente = utente.getClass().getSimpleName();
    
        String inserisciSqlUtentiUnificati = "INSERT INTO utenti_unificati (nome, cognome, email, password, tipo_utente) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(inserisciSqlUtentiUnificati)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, tipoUtente);
            pstmt.executeUpdate();
            consoleView.mostraMessaggio("Utente aggiunto con successo nella tabella 'utenti_unificati'.");
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta dell'utente nella tabella 'utenti_unificati': " + e.getMessage());
        }
    }
}

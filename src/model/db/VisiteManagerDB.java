package src.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import src.controller.ThreadPoolManager;

import src.model.DatabaseConnection;
import src.model.Luogo;
import src.model.Visite;
import src.model.Volontario;

public class VisiteManagerDB extends DatabaseManager {
    private ConcurrentHashMap<Integer, Visite> visiteMap = new ConcurrentHashMap<>();

    public VisiteManagerDB(ThreadPoolManager threadPoolManager) {
        super(threadPoolManager);
        caricaVisite();
    }

    //Logiche delle visite--------------------------------------------------
    // Metodo per caricare un luogo nel database e memorizzarlo nella HashMap
    private void caricaVisite() {
        String sql = "SELECT id, luogo, tipo_visita, volontario, data, stato, max_persone FROM visite";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            synchronized (visiteMap) {
                visiteMap.clear();
                while (rs.next()) {
                    int id = rs.getInt("id"); // ID della visita
                    String luogo = rs.getString("luogo");
                    String tipoVisita = rs.getString("tipo_visita");
                    String volontario = rs.getString("volontario");
                    LocalDate data = rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null; // Converte la data in LocalDate
                    int maxPersone = rs.getInt("max_persone");
                    String stato = rs.getString("stato");

                    // Usa il costruttore completo di Visite
                    Visite visita = new Visite(id, luogo, tipoVisita, volontario, data, maxPersone, stato);
                    visiteMap.putIfAbsent(id, visita);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento delle visite: " + e.getMessage());
        }
    }

    // Metodo per aggiungere una visita al database
    private void aggiungiVisita(Visite visita) {
        String inserisciSql = "INSERT INTO visite (luogo, tipo_visita, volontario, data, stato, max_persone) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(inserisciSql)) {
    
                pstmt.setString(1, visita.getLuogo());
                pstmt.setString(2, visita.getTipoVisita());
                pstmt.setString(3, visita.getVolontario());
                pstmt.setDate(4, visita.getData() != null ? java.sql.Date.valueOf(visita.getData()) : null);
                pstmt.setString(5, visita.getStato());
                pstmt.setInt(6, visita.getMaxPersone());
                pstmt.executeUpdate();
    
                consoleView.mostraMessaggio("Visita aggiunta con successo.");
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiunta della visita: " + e.getMessage());
            }
    }

    // Metodo per aggiornare una visita specifica
    public void aggiornaVisita(int visitaId, Visite visitaAggiornata) {
        String sql = "UPDATE visite SET luogo = ?, tipo_visita = ?, volontario = ?, data = ?, stato = ?, max_persone = ? WHERE id = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, visitaAggiornata.getLuogo());
                pstmt.setString(2, visitaAggiornata.getTipoVisita());
                pstmt.setString(3, visitaAggiornata.getVolontario());
                pstmt.setDate(4, visitaAggiornata.getData() != null ? java.sql.Date.valueOf(visitaAggiornata.getData()) : null);
                pstmt.setString(5, visitaAggiornata.getStato());
                pstmt.setInt(6, visitaAggiornata.getMaxPersone());
                pstmt.setInt(7, visitaId);

                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.err.println("Visita aggiornata con successo.");
                } else {
                    System.err.println("Errore: Nessuna visita trovata con l'ID specificato.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento della visita: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare il numero massimo di persone per tutte le visite
    public void aggiornaMaxPersonePerVisita(int maxPersonePerVisita) {
        String sql = "UPDATE visite SET max_persone = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maxPersonePerVisita);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.err.println("Numero massimo di persone per visita aggiornato con successo.");
                } else {
                    System.err.println("Nessuna visita trovata da aggiornare.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento del numero massimo di persone per visita: " + e.getMessage());
            }
        });
    }

    public void aggiungiNuovaVisita(Visite nuovaVisita) {
        String verificaSql = "SELECT 1 FROM visite WHERE luogo = ? AND data = ? AND volontario = ?";
        if(!recordEsiste(verificaSql, nuovaVisita.getLuogo(), nuovaVisita.getData(), nuovaVisita.getVolontario())){
            consoleView.mostraMessaggio("La visita non esiste già. Procedo con l'aggiunta.");
            aggiungiVisita(nuovaVisita);
        }
        else{
            consoleView.mostraMessaggio("La visita esiste già. Non posso aggiungerla.");
            return;
        }
    }

    // Metodo per recuperare il numero massimo di persone per visita dal database
    public int getMaxPersoneDefault() {
        String sql = "SELECT max_persone FROM visite";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            if (rs.next()) {
                return rs.getInt("max_persone"); // Restituisce il numero massimo di persone
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del numero massimo di persone: " + e.getMessage());
        }
        return 10; // Valore di default se non trovato nel database
    }

}

package src.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import src.controller.ThreadPoolController;
import src.model.Volontario;

public class VolontariManager extends DatabaseManager {

    private ConcurrentHashMap<String, Volontario> volontariMap = new ConcurrentHashMap<>();

    public VolontariManager(ThreadPoolController threadPoolManager) {
        super(threadPoolManager);
        caricaVolontari();
    }

    // Metodo per sincronizzare i volontari
    public void sincronizzaVolontari() {
        for (Volontario volontario : volontariMap.values()) {
            aggiungiVolontario(volontario);
            aggiornaPswVolontario(volontario.getEmail(), volontario.getPassword());
        }
        consoleView.mostraMessaggio("Sincronizzazione dei volontari completata.");
    }

    //Logiche dei volontari--------------------------------------------------
    // Metodo per caricare i volontari dal database e memorizzarli nella HashMap
    protected void caricaVolontari() {
        String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (volontariMap) {
                volontariMap.clear();
                while (rs.next()) {
                    String email = rs.getString("email");
                    String tipiDiVisite = rs.getString("tipi_di_visite");
                    List<String> listaTipiVisite = new ArrayList<>();
                                    if (tipiDiVisite != null && !tipiDiVisite.isEmpty()) {
                    listaTipiVisite = Arrays.asList(tipiDiVisite.split(","));
                }
                    Volontario volontario = new Volontario(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            email,
                            rs.getString("password"),
                            listaTipiVisite
                    );
                    volontariMap.putIfAbsent(email, volontario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento dei volontari: " + e.getMessage());
        }
    }

    // Metodo per aggiungere un volontario al database
    protected void aggiungiVolontario(Volontario volontario) {
        String inserisciSqlVolontari = "INSERT INTO volontari (nome, cognome, email, password, tipi_di_visite, password_modificata) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(inserisciSqlVolontari)) {
            pstmt.setString(1, volontario.getNome());
            pstmt.setString(2, volontario.getCognome());
            pstmt.setString(3, volontario.getEmail());
            pstmt.setString(4, volontario.getPassword());
            pstmt.setString(5, String.join(",", volontario.getTipiDiVisite()));
            pstmt.setBoolean(6, false);
            pstmt.executeUpdate();
            consoleView.mostraMessaggio("Volontario aggiunto con successo nella tabella 'volontari'.");
    
            // Aggiungi anche nella tabella 'utenti_unificati'
            aggiungiUtenteUnificato(volontario);
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta del volontario: " + e.getMessage());
        }
    }

    // Metodo per aggiornare un volontario nel database
    public void aggiornaPswVolontario(String email, String nuovaPassword) {
        String sqlVolontari = "UPDATE volontari SET password = ?, password_modificata = ? WHERE email = ?";
        String sqlUtentiUnificati = "UPDATE utenti_unificati SET password = ?, password_modificata = ? WHERE email = ?";
    
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Aggiorna la tabella "volontari"
                try (PreparedStatement pstmtVolontari = conn.prepareStatement(sqlVolontari)) {
                    pstmtVolontari.setString(1, nuovaPassword);
                    pstmtVolontari.setBoolean(2, true); // Imposta password_modificata a true
                    pstmtVolontari.setString(3, email);
                    int rowsUpdatedVolontari = pstmtVolontari.executeUpdate();
    
                    if (rowsUpdatedVolontari > 0) {
                        System.err.println("Password aggiornata con successo nella tabella 'volontari'.");
                    } else {
                        System.err.println("Errore: Nessun volontario trovato con l'email specificata.");
                    }
                }
    
                // Aggiorna la tabella "utenti_unificati"
                try (PreparedStatement pstmtUtenti = conn.prepareStatement(sqlUtentiUnificati)) {
                    pstmtUtenti.setString(1, nuovaPassword);
                    pstmtUtenti.setBoolean(2, true); // Imposta password_modificata a true
                    pstmtUtenti.setString(3, email);
                    int rowsUpdatedUtenti = pstmtUtenti.executeUpdate();
    
                    if (rowsUpdatedUtenti > 0) {
                        System.err.println("Password aggiornata con successo nella tabella 'utenti_unificati'.");
                    } else {
                        System.err.println("Errore: Nessun utente trovato con l'email specificata nella tabella 'utenti_unificati'.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento della password: " + e.getMessage());
            }
        });
    }

    public void aggiornaDisponibilitaVolontario(String email, String disponibilita) {
        String sql = "UPDATE volontari SET disponibilita = ? WHERE email = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, disponibilita);
                pstmt.setString(2, email);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Disponibilità aggiornata con successo per il volontario " + email);
                } else {
                    System.out.println("Nessun volontario trovato con l'email " + email);
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento della disponibilità: " + e.getMessage());
            }
        });
    }

    public void aggiungiNuovoVolontario(Volontario nuovoVolontario) {
        String verificaSql = "SELECT 1 FROM volontari WHERE email = ?";
        if(!recordEsiste(verificaSql, nuovoVolontario.getEmail())){
            aggiungiVolontario(nuovoVolontario);
            consoleView.mostraMessaggio("Volontario aggiunto con successo.");
        } else {
            consoleView.mostraMessaggio("Il volontario con email " + nuovoVolontario.getEmail() + " esiste già.");
        }
    }

    public ConcurrentHashMap<String, Volontario> getVolontariMap() {
        return volontariMap;
    }
    
    public void setVolontariMap(ConcurrentHashMap<String, Volontario> volontariMap) {
        this.volontariMap = volontariMap;
    }

    // public static VolontariManager getInstance() {
    //     return new VolontariManager(ThreadPoolController.getInstance());
    // }

}

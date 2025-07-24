package src.model.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import src.controller.ThreadPoolController;
import src.model.Configuratore;

public class ConfiguratoriManager extends DatabaseManager {
    private ConcurrentHashMap<String, Configuratore> configuratoriMap = new ConcurrentHashMap<>();

    public ConfiguratoriManager(ThreadPoolController threadPoolManager) {
        super(threadPoolManager);
        caricaConfiguratori();
    }
    // Metodo per sincronizzare i configuratori
    public void sincronizzaConfiguratori() {
        for (Configuratore configuratore : configuratoriMap.values()) {
            aggiornaConfiguratore(configuratore.getEmail(), configuratore);
        }
        consoleView.mostraMessaggio("Sincronizzazione dei configuratori completata.");
    }
    
    //Logiche dei configuratori--------------------------------------------------
    // Metodo per caricare i configuratori dal database e memorizzarli nella HashMap
    protected void caricaConfiguratori() {
        String sql = "SELECT nome, cognome, email, password FROM configuratori";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (configuratoriMap) {
                configuratoriMap.clear();
                while (rs.next()) {
                    String email = rs.getString("email");
                    Configuratore configuratore = new Configuratore(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            email,
                            rs.getString("password")
                    );
                    configuratoriMap.putIfAbsent(email, configuratore);
                }
            }
        } catch (SQLException e) {
            consoleView.mostraMessaggio("Errore durante il caricamento dei configuratori: " + e.getMessage());
        }
    }

    // Metodo per aggiungere un configuratore al database
    protected void aggiungiConfiguratore(Configuratore configuratore) {
        String inserisciSqlConfiguratori = "INSERT INTO configuratori (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(inserisciSqlConfiguratori)) {
            pstmt.setString(1, configuratore.getNome());
            pstmt.setString(2, configuratore.getCognome());
            pstmt.setString(3, configuratore.getEmail());
            pstmt.setString(4, configuratore.getPassword());
            pstmt.executeUpdate();
            consoleView.mostraMessaggio("Configuratore aggiunto con successo nella tabella 'configuratori'.");
    
            // Aggiungi anche nella tabella 'utenti_unificati'
            aggiungiUtenteUnificato(configuratore);
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta del configuratore: " + e.getMessage());
        }
    }

    // Metodo per aggiornare un configuratore nel database
    public void aggiornaConfiguratore(String email, Configuratore configuratoreAggiornato) {
        String sqlConfiguratori = "UPDATE configuratori SET nome = ?, cognome = ?, password = ?, email = ? WHERE email = ?";
        String sqlUtentiUnificati = "UPDATE utenti_unificati SET nome = ?, cognome = ?, password = ?, email = ? WHERE email = ?";
    
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Aggiorna la tabella "configuratori"
                try (PreparedStatement pstmtConfiguratori = conn.prepareStatement(sqlConfiguratori)) {
                    pstmtConfiguratori.setString(1, configuratoreAggiornato.getNome());
                    pstmtConfiguratori.setString(2, configuratoreAggiornato.getCognome());
                    pstmtConfiguratori.setString(3, configuratoreAggiornato.getPassword());
                    pstmtConfiguratori.setString(4, configuratoreAggiornato.getEmail()); // Nuova email
                    pstmtConfiguratori.setString(5, email); // Email corrente
                    int rowsUpdatedConfiguratori = pstmtConfiguratori.executeUpdate();
    
                    if (rowsUpdatedConfiguratori > 0) {
                        System.err.println("Configuratore aggiornato con successo nella tabella 'configuratori'.");
                    } else {
                        System.err.println("Errore: Nessun configuratore trovato con l'email specificata.");
                    }
                }
    
                // Aggiorna la tabella "utenti_unificati"
                try (PreparedStatement pstmtUtentiUnificati = conn.prepareStatement(sqlUtentiUnificati)) {
                    pstmtUtentiUnificati.setString(1, configuratoreAggiornato.getNome());
                    pstmtUtentiUnificati.setString(2, configuratoreAggiornato.getCognome());
                    pstmtUtentiUnificati.setString(3, configuratoreAggiornato.getPassword());
                    pstmtUtentiUnificati.setString(4, configuratoreAggiornato.getEmail()); // Nuova email
                    pstmtUtentiUnificati.setString(5, email); // Email corrente
                    int rowsUpdatedUtentiUnificati = pstmtUtentiUnificati.executeUpdate();
    
                    if (rowsUpdatedUtentiUnificati > 0) {
                        System.err.println("Configuratore aggiornato con successo nella tabella 'utenti_unificati'.");
                    } else {
                        System.err.println("Errore: Nessun utente trovato con l'email specificata nella tabella 'utenti_unificati'.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento del configuratore: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare un volontario nel database
    public void aggiornaPswConfiguratore(String email, String nuovaPassword) {
        String sqlConfiguratori = "UPDATE configuratori SET password = ?, password_modificata = ? WHERE email = ?";
        String sqlUtentiUnificati = "UPDATE utenti_unificati SET password = ?, password_modificata = ? WHERE email = ?";
    
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Aggiorna la tabella "configuratori"
                try (PreparedStatement pstmtConfiguratori = conn.prepareStatement(sqlConfiguratori)) {
                    pstmtConfiguratori.setString(1, nuovaPassword);
                    pstmtConfiguratori.setBoolean(2, true); // Imposta password_modificata a true
                    pstmtConfiguratori.setString(3, email);
                    int rowsUpdatedConfiguratori = pstmtConfiguratori.executeUpdate();

                    if (rowsUpdatedConfiguratori > 0) {
                        System.err.println("Password aggiornata con successo nella tabella 'configuratori'.");
                    } else {
                        System.err.println("Errore: Nessun configuratore trovato con l'email specificata.");
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
                        System.err.println("Errore: Nessun utente trovato con l'email specificata.");
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

    public void aggiungiNuovoConf(Configuratore nuovoConfiguratore) {
        String verificaSql = "SELECT 1 FROM configuratori WHERE email = ?";
        if(!recordEsiste(verificaSql, nuovoConfiguratore.getEmail())){
            consoleView.mostraMessaggio("Il configuratore non esiste già. Procedo con l'aggiunta.");
            aggiungiConfiguratore(nuovoConfiguratore);
        }
        else{
            consoleView.mostraMessaggio("Il configuratore esiste già.");
            return;
        }
    }

    public ConcurrentHashMap<String, Configuratore> getConfiguratoriMap() {
        return configuratoriMap;
    }
    
    public void setConfiguratoriMap(ConcurrentHashMap<String, Configuratore> configuratoriMap) {
        this.configuratoriMap = configuratoriMap;
    }
    // public static ConfiguratoriManager getInstance() {
    //     return new ConfiguratoriManager(ThreadPoolController.getInstance());
    // }

}

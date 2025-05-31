package src.model.db;

import src.controller.ThreadPoolController;
import src.model.TemporaryCredential;
import src.view.ConsoleView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class DatabaseUpdater {
    // HashMap per memorizzare i dati sincronizzati
    // private ConcurrentHashMap<String, Volontario> volontariMap = VolontariManager.getInstance().getVolontariMap();
    // private ConcurrentHashMap<String, Configuratore> configuratoriMap = ConfiguratoriManager.getInstance().getConfiguratoriMap();
    // private ConcurrentHashMap<String, Luogo> luoghiMap = LuoghiManager.getInstance().getLuoghiMap();
    // private ConcurrentHashMap<Integer, Visite> visiteMap = VisiteManagerDB.getInstance().getVisiteMap();
    private ConcurrentHashMap<String, TemporaryCredential> temporaryCredentials = new ConcurrentHashMap<>();
    private ConsoleView consoleView = new ConsoleView();
    
    private final VolontariManager volontariManager = VolontariManager.getInstance();
    private final ConfiguratoriManager configuratoriManager = ConfiguratoriManager.getInstance();
    private final LuoghiManager luoghiManager = LuoghiManager.getInstance();
    private final VisiteManagerDB visiteManagerDB = VisiteManagerDB.getInstance();
    private final ExecutorService executorService = ThreadPoolController.getInstance().createThreadPool(4); // Inizializza il thread pool
    private Thread aggiornamentoThread;
    private volatile boolean eseguiAggiornamento = true; // Variabile per controllare il ciclo

    public DatabaseUpdater() {}

    //Logiche Thread------------------------------------------------------------------
    // Metodo per sincronizzare i dati dal database in un thread separato
    public void sincronizzaDalDatabase() {
        executorService.submit(() -> {
            if (eseguiAggiornamento) {
                try {
                    // Logica per sincronizzare i dati dal database
                    volontariManager.caricaVolontari();
                    configuratoriManager.caricaConfiguratori();
                    luoghiManager.caricaLuoghi();
                    visiteManagerDB.caricaVisite();
                } catch (Exception e) {
                    System.err.println("Errore durante la sincronizzazione dal database: " + e.getMessage());
                }
            } else {
                return;
            }            
        });
    }

    // Metodo per avviare la sincronizzazione periodica con un ciclo e sleep
    public void avviaSincronizzazioneConSleep() {
        eseguiAggiornamento = true; // Assicura che il ciclo sia attivo
        aggiornamentoThread = new Thread(() -> {
            while (eseguiAggiornamento) {
                try {
                    sincronizzaDalDatabase();
                    Thread.sleep(5000); // Pausa di 5 secondi
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
                    break; // Esci dal ciclo se il thread è interrotto
                }
            }
        });
        aggiornamentoThread.start();
    }

    // Metodo per fermare la sincronizzazione periodica con un ciclo e sleep
    public void arrestaSincronizzazioneConSleep() {
        eseguiAggiornamento = false; // Ferma il ciclo
        if (aggiornamentoThread != null) {
            aggiornamentoThread.interrupt(); // Interrompe il thread se è in attesa
            try {
                aggiornamentoThread.join(); // Attende la terminazione del thread
            } catch (InterruptedException e) {
                System.err.println("Errore durante l'arresto del thread di aggiornamento.");
                Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
            }
        }
    }

    // Metodo per verificare se un record esiste nel database
    private boolean recordEsiste(String sql, Object... parametri) {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            for (int i = 0; i < parametri.length; i++) {
                pstmt.setObject(i + 1, parametri[i]);
            }
    
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Restituisce true se il record esiste
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la verifica dell'esistenza del record: " + e.getMessage());
        }
        return false;
    }



    //Logiche per Credenziali Temporanee--------------------------------------------------
    public void caricaCredenzialiTemporanee() {
        String sql = "SELECT username, password FROM credenziali_temporanee";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                temporaryCredentials.put(username, new TemporaryCredential(username, password));
            }
    
            consoleView.mostraMessaggio("Credenziali temporanee caricate con successo.");
        } catch (SQLException e) {
            consoleView.mostraMessaggio("Errore durante il caricamento delle credenziali temporanee: " + e.getMessage());
        }
    }

    


//Getters e Setters--------------------------------------------------
    public String getTipoUtente(String email, String password){
        String tipo_utente = null; // Inizializza a null per evitare NullPointerException
        String sql = "SELECT tipo_utente FROM utenti_unificati WHERE email = ? AND password = ?";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            // Imposta i parametri della query
            pstmt.setString(1, email);
            pstmt.setString(2, password);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tipo_utente = rs.getString("tipo_utente"); // Recupera il tipo_utente
                }
            }
        } catch (SQLException e) {
            consoleView.mostraMessaggio("Errore durante la verifica delle credenziali: " + e.getMessage());
        }
        return tipo_utente; // Restituisce il tipo_utente o null se non trovato
    }

    public boolean isPasswordModificata(String email) {
        String sql = "SELECT password_modificata FROM utenti_unificati WHERE email = ?";
        boolean passwordModificata = false; // Valore di default
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            // Imposta il parametro della query
            pstmt.setString(1, email);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    passwordModificata = rs.getBoolean("password_modificata"); // Recupera il valore del campo password_modificata
                } else {
                    consoleView.mostraMessaggio("Nessun record trovato per l'email: " + email);
                }
            }
        } catch (SQLException e) {
            consoleView.mostraMessaggio("Errore durante la verifica del campo password_modificata: " + e.getMessage());
        }
    
        return passwordModificata;
    }

    public ConcurrentHashMap<String, TemporaryCredential> getTemporaryCredentials() {
        return temporaryCredentials;
    }

    public static DatabaseUpdater getInstance() {
        return new DatabaseUpdater();
    }
}
package src.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import src.controller.ThreadPoolController;
import src.model.Luogo;

public class LuoghiManager extends DatabaseManager {
    private ConcurrentHashMap<String, Luogo> luoghiMap = new ConcurrentHashMap<>();

    public LuoghiManager(ThreadPoolController threadPoolManager) {
        super(threadPoolManager);
        caricaLuoghi();
    }
    
    // Metodo per sincronizzare i luoghi
    public void sincronizzaLuoghi() {
        for (Luogo luogo : luoghiMap.values()) {
            aggiungiLuogo(luogo);
            aggiornaLuogo(luogo.getNome(), luogo);
        }
        consoleView.mostraMessaggio("Sincronizzazione dei luoghi completata.");
    }

    //Logiche dei luoghi--------------------------------------------------
    // Metodo per caricare i luoghi dal database e memorizzarli nella HashMap
    protected void caricaLuoghi() {
        String sql = "SELECT nome, descrizione FROM luoghi";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (luoghiMap) {
                luoghiMap.clear();
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    Luogo luogo = new Luogo(
                            nome,
                            rs.getString("descrizione")
                    );
                    luoghiMap.putIfAbsent(nome, luogo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento dei luoghi: " + e.getMessage());
        }
    }

    // Metodo per aggiornare un luogo nel database
    private void aggiornaLuogo(String nome, Luogo luogoAggiornato) {
        String sql = "UPDATE luoghi SET descrizione = ? WHERE nome = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogoAggiornato.getDescrizione());
                pstmt.setString(2, nome);
    
                int rowsUpdated = pstmt.executeUpdate();
    
                if (rowsUpdated > 0) {
                    System.err.println("Luogo aggiornato con successo.");
                } else {
                    System.err.println("Errore: Nessun luogo trovato con il nome specificato.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento del luogo: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiungere un luogo al database
    private void aggiungiLuogo(Luogo luogo) {
        String inserisciSql = "INSERT INTO luoghi (nome, descrizione) VALUES (?, ?)";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(inserisciSql)) {
    
                pstmt.setString(1, luogo.getNome());
                pstmt.setString(2, luogo.getDescrizione());
                pstmt.executeUpdate();
    
                consoleView.mostraMessaggio("Luogo aggiunto con successo.");
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiunta del luogo: " + e.getMessage());
            }
    }

    public void aggiungiNuovoLuogo(Luogo nuovoLuogo) {
        String verificaSql = "SELECT 1 FROM luoghi WHERE nome = ?";
        if(!recordEsiste(verificaSql, nuovoLuogo.getNome())){
            consoleView.mostraMessaggio("Il luogo non esiste già. Procedo con l'aggiunta.");
            aggiungiLuogo(nuovoLuogo);
        } else {
            consoleView.mostraMessaggio("Il luogo esiste già.");
            return;
        }
    }

    public ConcurrentHashMap<String, Luogo> getLuoghiMap() {
        return luoghiMap;
    }
    
    public void setLuoghiMap(ConcurrentHashMap<String, Luogo> luoghiMap) {
        this.luoghiMap = luoghiMap;
    }

    public static LuoghiManager getInstance() {
        return new LuoghiManager(ThreadPoolController.getInstance());
    }

}

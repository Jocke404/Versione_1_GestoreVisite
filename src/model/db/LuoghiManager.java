package src.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import src.controller.ThreadPoolController;
import src.model.Luogo;
import src.model.TipiVisita;

public class LuoghiManager extends DatabaseManager {
    private ConcurrentHashMap<String, Luogo> luoghiMap = new ConcurrentHashMap<>();

    public LuoghiManager(ThreadPoolController threadPoolManager) {
        super(threadPoolManager);
        caricaLuoghi();
    }
    
    protected void caricaLuoghi() {
        String sql = "SELECT nome, descrizione, collocazione, tipi_di_visita FROM luoghi";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (luoghiMap) {
                luoghiMap.clear();
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String tipiVisitaStr = rs.getString("tipi_di_visita");
                    List<TipiVisita> tipiVisitaList = new java.util.ArrayList<>();
                    if (tipiVisitaStr != null && !tipiVisitaStr.trim().isEmpty()) {
                        tipiVisitaList = Arrays.stream(tipiVisitaStr.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(s -> {
                                try {
                                    return TipiVisita.valueOf(s.toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Tipo visita non valido: " + s);
                                    return null;
                                }
                            })
                            .filter(t -> t != null)
                            .toList();
                    }
                    Luogo luogo = new Luogo(
                        nome,
                        rs.getString("descrizione"),
                        rs.getString("collocazione"),
                        tipiVisitaList
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
        String sql = "UPDATE luoghi SET descrizione = ?, collocazione = ?, tipi_di_visita = ? WHERE nome = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogoAggiornato.getDescrizione());
                pstmt.setString(2, luogoAggiornato.getCollocazione());
                String tipiVisitaStr = String.join(",", luogoAggiornato.getTipiVisita().stream().map(TipiVisita::name).toList());
                pstmt.setString(3, tipiVisitaStr);
                pstmt.setString(4, nome);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                consoleIO.mostraMessaggio("Errore durante l'aggiornamento del luogo: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiungere un luogo al database
    private void aggiungiLuogo(Luogo luogo) {
        String inserisciSql = "INSERT INTO luoghi (nome, descrizione, collocazione, tipi_di_visita) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(inserisciSql)) {
    
                pstmt.setString(1, luogo.getNome());
                pstmt.setString(2, luogo.getDescrizione());
                pstmt.setString(3, luogo.getCollocazione());
                String tipiVisitaStr = String.join(",", luogo.getTipiVisita().stream().map(TipiVisita::name).toList());
                pstmt.setString(4, tipiVisitaStr);
                pstmt.executeUpdate();
    
                consoleIO.mostraMessaggio("Luogo aggiunto con successo.");
            } catch (SQLException e) {
                consoleIO.mostraMessaggio("Errore durante l'aggiunta del luogo: " + e.getMessage());
            }
    }

    public void aggiungiNuovoLuogo(Luogo nuovoLuogo) {
        String verificaSql = "SELECT 1 FROM luoghi WHERE nome = ?";
        if(!recordEsiste(verificaSql, nuovoLuogo.getNome())){
            consoleIO.mostraMessaggio("Il luogo non esiste già. Procedo con l'aggiunta.");
            aggiungiLuogo(nuovoLuogo);
        } else {
            consoleIO.mostraMessaggio("Il luogo esiste già.");
            return;
        }
    }

    private void rimuoviLuogoDalDatabase(Luogo luogoDaEliminare) {
        String sql = "DELETE FROM luoghi WHERE nome = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogoDaEliminare.getNome());
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    consoleIO.mostraMessaggio("Luogo rimosso con successo.");
                } else {
                    consoleIO.mostraMessaggio("Errore: Nessun luogo trovato con il nome specificato.");
                }
            } catch (SQLException e) {
                consoleIO.mostraMessaggio("Errore durante la rimozione del luogo: " + e.getMessage());
            }
        });
    }

    public ConcurrentHashMap<String, Luogo> getLuoghiMap() {
        return luoghiMap;
    }
    
    public void setLuoghiMap(ConcurrentHashMap<String, Luogo> luoghiMap) {
        this.luoghiMap = luoghiMap;
    }

    public void rimuoviLuogo(Luogo luogoDaEliminare) {
        rimuoviLuogoDalDatabase(luogoDaEliminare);
        luoghiMap.remove(luogoDaEliminare.getNome());
    }

    public void aggiornaLuoghi(Luogo luogo) {
        aggiornaLuogo(luogo.getNome(), luogo);
    }

}

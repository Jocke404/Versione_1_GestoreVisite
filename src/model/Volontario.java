package src.model;

import java.util.List;

public class Volontario extends Utente {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private List<TipiVisita> tipiDiVisite; // Tipi di visite a cui il volontario è assegnato

    // Costruttore, getter e setter
    public Volontario(String nome, String cognome, String email, String password, List<TipiVisita> tipiDiVisite) {
        super(email, password, nome, cognome);
        this.tipiDiVisite = tipiDiVisite;
    }
    
    public List<TipiVisita> getTipiDiVisite() {
        return tipiDiVisite;
    }
}

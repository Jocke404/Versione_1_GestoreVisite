package src.model;

import java.util.List;

public class Volontario extends Utente {
    private List<TipiVisita> tipiDiVisite; // Tipi di visite a cui il volontario è assegnato

    // Costruttore, getter e setter
    public Volontario(String nome, String cognome, String email, String password, List<TipiVisita> tipiDiVisite) {
        super(email, password, nome, cognome);
        this.tipiDiVisite = tipiDiVisite;
    }
    
    public List<TipiVisita> getTipiDiVisite() {
        return tipiDiVisite;
    }

    public void setTipiDiVisite(List<TipiVisita> tipiDiVisite) {
        this.tipiDiVisite = tipiDiVisite;
    }
    
    public void aggiungiTipoVisita(TipiVisita tipoVisita) {
        if (!this.tipiDiVisite.contains(tipoVisita)) {
            this.tipiDiVisite.add(tipoVisita);
        }
    }
    
    public void rimuoviTipoVisita(TipiVisita tipoVisita) {
        this.tipiDiVisite.remove(tipoVisita);
    }

    public boolean contieneTipoVisita(TipiVisita tipoVisita) {
        return this.tipiDiVisite.contains(tipoVisita);
    }

}

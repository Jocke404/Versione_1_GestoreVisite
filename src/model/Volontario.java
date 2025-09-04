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
    
    // public void setEmail(String email) {
    //     this.email = email;
    // }

    // public String getPassword() {
    //     return password;
    // }

    // public void setPassword(String password) {
    //     this.password = password;
    // }

    // @Override
    // public String toString() {
    //     return "Volontario [nome=" + nome + ", cognome=" + cognome + ", email=" + email + ", tipiDiVisite=" + tipiDiVisite + "]";
    // }
}

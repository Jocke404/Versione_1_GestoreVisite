package src.model;

public class Volontario implements Utente {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String tipiDiVisite; // Tipi di visite a cui il volontario è assegnato

    // Costruttore, getter e setter
    public Volontario(String nome, String cognome, String email, String password, String tipiDiVisite) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.tipiDiVisite = tipiDiVisite;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getEmail() {
        return email;
    }
    
    @Override
    public String getCognome() {
        return cognome;
    }
    
    public String getTipiDiVisite() {
        return tipiDiVisite;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Volontario [nome=" + nome + ", cognome=" + cognome + ", email=" + email + ", tipiDiVisite=" + tipiDiVisite + "]";
    }
}

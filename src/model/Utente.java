package src.model;

// public interface Utente {
//     String getEmail();
//     String getNome();
//     String getCognome();
//     String getPassword();
    

public abstract class Utente {
    private String email;
    private String password;
    private String nome;
    private String cognome;

    public Utente(String email, String password, String nome, String cognome) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [nome=" + nome + ", cognome=" + cognome + ", email=" + email + "]";
    }

    
    // private String nome;
    // private String cognome;
    // private String email;
    // private String password;

    // public Utente(String nome, String cognome, String email, String password) {
    //     this.nome = nome;
    //     this.cognome = cognome; // assuming an empty default, can modify as per requirements
    //     this.email = email;
    //     this.password = password;
    // }

    // public void setNome(String nome) {
    //     this.nome = nome; // changed method to set the nome
    // }

    // public void setCognome(String cognome) {
    //     this.cognome = cognome; // added method to set the cognome
    // }

    // public String getNome() {
    //     return nome;
    // }

    // public String getCognome() {
    //     return cognome;
    // }

    // public String getEmail() {
    //     return email; // added method to get the email
    // }

    // public String getPassword() {
    //     return password; // added method to get the password
    // }


    // @Override
    // public String toString() {
    //     return nome + " " + cognome;
    // }
}

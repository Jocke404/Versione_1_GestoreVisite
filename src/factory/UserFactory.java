package src.factory;
import java.util.List;

import src.model.Configuratore;
import src.model.Utente;
import src.model.Visitatore;
import src.model.Volontario;


public class UserFactory {
    
    public static final String VOLONTARIO = "Volontario";
    public static final String CONFIGURATORE = "Configuratore";
    public static final String VISITATORE = "Visitatore";

    private UserFactory() {
        // Prevent instantiation
    }

    public static Utente createUser(String userType, String email, String password,  String nome, String cognome, List<String> tipodiVisita) {
        switch (userType) {
            case VOLONTARIO:
                return new Volontario(nome, cognome, email, password, tipodiVisita);
            case CONFIGURATORE:
                return new Configuratore(email, password, nome, cognome);
            case VISITATORE:
                return new Visitatore(email, password, nome, cognome);
            default:
                throw new IllegalArgumentException("Tipo di utente sconosciuto: " + userType);
        }
    }
}

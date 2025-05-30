package src.factory;

public class UserFactory {
    
    public static final String VOLONTARIO = "Volontario";
    public static final String CONFIGURATORE = "Configuratore";
    public static final String VISITATORE = "Visitatore";

    private UserFactory() {
        // Prevent instantiation
    }

    public static String getUserType(String userType) {
        switch (userType) {
            case VOLONTARIO:
                return VOLONTARIO;
            case CONFIGURATORE:
                return CONFIGURATORE;
            case VISITATORE:
                return VISITATORE;
            default:
                throw new IllegalArgumentException("Tipo di utente sconosciuto: " + userType);
        }
    }
}

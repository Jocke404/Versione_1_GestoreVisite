package src.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lib.InputDati;
import src.view.ConsoleView;
import src.factory.UserFactory;
import src.model.db.*;

public class CredentialManager {

    private static DatabaseUpdater databaseUpdater = DatabaseUpdater.getInstance(); ;
    private static VolontariManager volontariManager = VolontariManager.getInstance();
    private static ConfiguratoriManager configuratoriManager = ConfiguratoriManager.getInstance();
    private static UserFactory userFactory;
    private ConcurrentHashMap<String, Volontario> volontariMap = volontariManager.getVolontariMap();


    private Volontario volontarioCorrente = null;
    private Configuratore configuratoreCorrente = null;
    private ConsoleView consoleView = new ConsoleView();
    
    public CredentialManager() {}

    //Autenticazione-------------------------------------------------------------------------
    
    // public Utente autentica() {
    //     String email = InputDati.leggiStringaNonVuota("Inserisci (email): ");
    //     String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
    //     String tipoUtente = verificaCredenziali(email, password);
    //     boolean credenzialiModificate = isPasswordModificata(email);
    //     // String name = volontariMap.get(email) != null ? volontariMap.get(email).getNome() : configuratoriManager.getConfiguratoriMap().get(email).getNome();
    //     // String cognome = volontariMap.get(email) != null ? volontariMap.get(email).getCognome() : configuratoriManager.getConfiguratoriMap().get(email).getCognome();

    //     if (tipoUtente == null) {
    //         consoleView.mostraMessaggio("Credenziali non valide.");
    //         return null;
    //     }

    //     // userFactory.createUser(tipoUtente, email, name, cognome);
    
    //     switch (tipoUtente) {
    //         case "Volontario":
    //             consoleView.mostraMessaggio("Accesso come Volontario.");
    //             Volontario volontario = volontariManager.getVolontariMap().get(email);
                
    //             if (volontario == null) {
    //                 consoleView.mostraMessaggio("Errore: volontario non trovato.");
    //                 return null;
    //             }
    
    //             if (!credenzialiModificate) {
    //                 consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
    //                 salvaNuovaPasswordVol(volontario);
    //             }
    //             return volontario;
                
    //         case "Configuratore":
    //             consoleView.mostraMessaggio("Accesso come Configuratore.");
    //             Configuratore configuratore = configuratoriManager.getConfiguratoriMap().get(email);
    //             if (configuratore == null) {
    //                 consoleView.mostraMessaggio("Errore: configuratore non trovato.");
    //                 return null;
    //             }
    //             return configuratore;
    
    //         default:
    //             consoleView.mostraMessaggio("Ruolo non riconosciuto: " + tipoUtente);
    //             return null;
    //     }
    // }

    public Utente autentica() {
        String email = InputDati.leggiStringaNonVuota("email: ");
        String password = InputDati.leggiStringaNonVuota("password: ");
        String tipoUtente = estraiTipoUtente(email, password);

        if (tipoUtente == null) {
            consoleView.mostraMessaggio("Credenziali non valide.");
            return null;
        }

        // Recupera i dati necessari per creare l'utente
        String nome = null;
        String cognome = null;
        List<String> tipidiVisite = null; // Inizializza a null per evitare NullPointerException

        switch (tipoUtente) {
            case UserFactory.VOLONTARIO:
                Volontario volontario = volontariManager.getVolontariMap().get(email);
                if (volontario == null) {
                    consoleView.mostraMessaggio("Errore: volontario non trovato.");
                    return null;
                }
                nome = volontario.getNome();
                cognome = volontario.getCognome();
                tipidiVisite = volontario.getTipiDiVisite();

                // Controlla se la password è temporanea
                if (!isPasswordModificata(email)) {
                    consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    salvaNuovaPassword(volontario);
                }
                break;

            case UserFactory.CONFIGURATORE:
                Configuratore configuratore = configuratoriManager.getConfiguratoriMap().get(email);
                if (configuratore == null) {
                    consoleView.mostraMessaggio("Errore: configuratore non trovato.");
                    return null;
                }
                if (isPasswordModificata(email)) {
                    consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    salvaNuovaPassword(configuratore);
                }
                nome = configuratore.getNome();
                cognome = configuratore.getCognome();
                break;

            default:
                consoleView.mostraMessaggio("Ruolo non riconosciuto: " + tipoUtente);
                return null;
        }

        // Usa la UserFactory per creare l'utente
        return UserFactory.createUser(tipoUtente, email, password, nome, cognome, tipidiVisite);
    }

    public void caricaCredenzialiTemporanee() {
        databaseUpdater.getTemporaryCredentials();
    }

    public void salvaNuovaPassword(Utente utente) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        
        // Aggiorna la password nella HashMap
        utente.setPassword(nuovaPassword);
        if (utente instanceof Volontario) {
            volontariManager.getVolontariMap().put(utente.getEmail(), (Volontario) utente);
            // Sincronizza con il database
            volontariManager.aggiornaPswVolontario(utente.getEmail(), nuovaPassword);
        } else if (utente instanceof Configuratore) {
            configuratoriManager.getConfiguratoriMap().put(utente.getEmail(), (Configuratore) utente);
            // Sincronizza con il database
            configuratoriManager.aggiornaPswConfiguratore(utente.getEmail(), nuovaPassword);
        }
    }

    public void salvaNuoveCredenzialiConf() {
        // Raccogli i dati del nuovo configuratore
        String newEmail = InputDati.leggiStringaNonVuota("Inserisci la nuova email: ");
        String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
        String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
        
        // Crea un nuovo oggetto Configuratore
        Configuratore updatedConfiguratore = new Configuratore(name, surname, newEmail, newPassword);
        
        // Aggiorna la HashMap
        configuratoriManager.getConfiguratoriMap().put(newEmail, updatedConfiguratore);
    
        // Sincronizza con il database
        configuratoriManager.aggiungiNuovoConf(updatedConfiguratore);
    }

    // Restituisci il tipo_utente dell'utente o null se non autenticato
    public String estraiTipoUtente(String email, String password) {
        String tipo_utente = databaseUpdater.getTipoUtente(email, password);
        return tipo_utente;
    }

    // Controlla se la password è stata modificata
    public boolean isPasswordModificata(String email) {
        Boolean passwordModificata = databaseUpdater.isPasswordModificata(email);
        return passwordModificata;
    }

    public static CredentialManager getInstance() {
        return new CredentialManager();
    }
}

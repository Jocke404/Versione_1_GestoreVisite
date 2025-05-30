package src.model;

import lib.InputDati;
import src.model.db.DatabaseUpdater;
import src.view.ConsoleView;
import src.model.db.*;

public class CredentialManager {

    private static DatabaseUpdater databaseUpdater = DatabaseUpdater.getInstance(); ;
    private static VolontariManager volontariManager = VolontariManager.getInstance();
    private static ConfiguratoriManager configuratoriManager = ConfiguratoriManager.getInstance();


    private Volontario volontarioCorrente = null;
    private Configuratore configuratoreCorrente = null;
    private ConsoleView consoleView = new ConsoleView();
    
    public CredentialManager() {}

    //Autenticazione-------------------------------------------------------------------------
    public Utente autentica() {
        String email = InputDati.leggiStringaNonVuota("Inserisci (email): ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
        String tipoUtente = verificaCredenziali(email, password);
        boolean credenzialiModificate = isPasswordModificata(email);

        if (tipoUtente == null) {
            consoleView.mostraMessaggio("Credenziali non valide.");
            return null;
        }
    
        switch (tipoUtente) {
            case "Volontario":
                consoleView.mostraMessaggio("Accesso come Volontario.");
                Volontario volontario = volontariManager.getVolontariMap().get(email);
                
                if (volontario == null) {
                    consoleView.mostraMessaggio("Errore: volontario non trovato.");
                    return null;
                }
    
                if (!credenzialiModificate) {
                    consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    salvaNuovaPasswordVol(volontario);
                }
                return volontario;
                
            case "Configuratore":
                consoleView.mostraMessaggio("Accesso come Configuratore.");
                Configuratore configuratore = configuratoriManager.getConfiguratoriMap().get(email);
                if (configuratore == null) {
                    consoleView.mostraMessaggio("Errore: configuratore non trovato.");
                    return null;
                }
                return configuratore;
    
            default:
                consoleView.mostraMessaggio("Ruolo non riconosciuto: " + tipoUtente);
                return null;
        }
    }

    public void caricaCredenzialiTemporanee() {
        databaseUpdater.getTemporaryCredentials();
    }

    public void salvaNuovaPasswordVol(Volontario volontario) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        
        // Aggiorna la password nella HashMap
        volontario.setPassword(nuovaPassword);
        volontariManager.getVolontariMap().put(volontario.getEmail(), volontario);

        // Sincronizza con il database
        volontariManager.aggiornaPswVolontario(volontario.getEmail(), nuovaPassword);
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
    public String verificaCredenziali(String email, String password) {
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

package src.model;

import java.util.List;

import lib.InputDati;
import src.view.ConsoleView;
import src.factory.UserFactory;
import src.model.db.*;

public class CredentialManager {

    private final DatabaseUpdater databaseUpdater;
    private final VolontariManager volontariManager;
    private final ConfiguratoriManager configuratoriManager;


    private Volontario volontarioCorrente = null;
    private Configuratore configuratoreCorrente = null;
    private ConsoleView consoleView = new ConsoleView();
    
    public CredentialManager(DatabaseUpdater databaseUpdater, VolontariManager volontariManager, ConfiguratoriManager configuratoriManager) {
        this.databaseUpdater = databaseUpdater;
        this.volontariManager = volontariManager;
        this.configuratoriManager = configuratoriManager;
    }

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
        List<String> tipidiVisite = null; 

        switch (tipoUtente) {
            case UserFactory.VOLONTARIO:
                volontarioCorrente = volontariManager.getVolontariMap().get(email);
                if (volontarioCorrente == null) {
                    consoleView.mostraMessaggio("Errore: volontario non trovato.");
                    return null;
                }
                nome = volontarioCorrente.getNome();
                cognome = volontarioCorrente.getCognome();
                tipidiVisite = volontarioCorrente.getTipiDiVisite();

                // Controlla se la password è temporanea
                if (!isPasswordModificata(email)) {
                    consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    salvaNuovaPassword(volontarioCorrente);
                }
                break;

            case UserFactory.CONFIGURATORE:
                if (email.equals("admin@example.com") && password.equals("admin123")) {
                    consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    Configuratore newConfig = salvaNuoveCredenzialiConf();
                    nome = newConfig.getNome();
                    cognome = newConfig.getCognome();
                } else {
                    configuratoreCorrente = configuratoriManager.getConfiguratoriMap().get(email);
                    nome = configuratoreCorrente.getNome();
                    cognome = configuratoreCorrente.getCognome();
                }
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

    private void salvaNuovaPassword(Utente utente) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        
        // Aggiorna la password nella HashMap
        utente.setPassword(nuovaPassword);
        if (utente instanceof Volontario) {
            volontariManager.getVolontariMap().put(utente.getEmail(), (Volontario) utente);
            // Sincronizza con il database
            volontariManager.aggiornaPswVolontario(utente.getEmail(), nuovaPassword);
        }
    }

    private Configuratore salvaNuoveCredenzialiConf() {
        // Raccogli i dati del nuovo configuratore        
        String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
        String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
        String newEmail = InputDati.leggiStringaNonVuota("Inserisci la nuova email: ");
        String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");

        // Crea un nuovo oggetto Configuratore
        Configuratore newConfiguratore = (Configuratore) UserFactory.createUser(UserFactory.CONFIGURATORE, newEmail, newPassword, name, surname,null);

        // Aggiorna la HashMap
        configuratoriManager.getConfiguratoriMap().put(newEmail, newConfiguratore);

        // Sincronizza con il database
        configuratoriManager.aggiungiNuovoConf(newConfiguratore);

        return newConfiguratore;
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

}

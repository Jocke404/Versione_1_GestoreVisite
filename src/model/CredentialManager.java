package src.model;

import java.util.List;

import src.factory.UserFactory;
import src.model.db.*;

public class CredentialManager {

    private final DatabaseUpdater databaseUpdater;
    private final VolontariManager volontariManager;
    private final ConfiguratoriManager configuratoriManager;
    private final FruitoreManager fruitoreManager;
    private DatabaseManager databaseManager;



    private Volontario volontarioCorrente = null;
    private Configuratore configuratoreCorrente = null;
    private Fruitore fruitoreCorrente = null;

    public CredentialManager(DatabaseUpdater databaseUpdater, VolontariManager volontariManager, ConfiguratoriManager configuratoriManager, FruitoreManager fruitoreManager) {
        this.databaseUpdater = databaseUpdater;
        this.volontariManager = volontariManager;
        this.configuratoriManager = configuratoriManager;
        this.fruitoreManager = fruitoreManager;
    }

    public Utente autentica(String email, String password) {
        String tipoUtente = estraiTipoUtente(email, password);

        if (tipoUtente == null) {
            return null;
        }

        String nome = null;
        String cognome = null;
        List<TipiVisita> tipidiVisite = null;

        switch (tipoUtente) {
            case UserFactory.VOLONTARIO:
                volontarioCorrente = volontariManager.getVolontariMap().get(email);
                if (volontarioCorrente == null) {
                    return null;
                }
                nome = volontarioCorrente.getNome();
                cognome = volontarioCorrente.getCognome();
                tipidiVisite = volontarioCorrente.getTipiDiVisite();
                break;

            case UserFactory.CONFIGURATORE:
                configuratoreCorrente = configuratoriManager.getConfiguratoriMap().get(email);
                if (configuratoreCorrente == null) {
                    return null;
                }
                nome = configuratoreCorrente.getNome();
                cognome = configuratoreCorrente.getCognome();
                break;

            case UserFactory.FRUITORE:
                fruitoreCorrente = fruitoreManager.getFruitoriMap().get(email);
                if (fruitoreCorrente == null) {
                    return null;
                }
                nome = fruitoreCorrente.getNome();
                cognome = fruitoreCorrente.getCognome();
                break;

            default:
                return null;
        }

        return UserFactory.createUser(tipoUtente, email, password, nome, cognome, tipidiVisite);
    }

    // public Utente autentica() {
    //     String email = InputDati.leggiStringaNonVuota("email: ");
    //     String password = InputDati.leggiStringaNonVuota("password: ");
    //     String tipoUtente = estraiTipoUtente(email, password);

    //     if (tipoUtente == null) {
    //         consoleIO.mostraMessaggio("Credenziali non valide.");
    //         return null;
    //     }

    //     // Recupera i dati necessari per creare l'utente
    //     String nome = null;
    //     String cognome = null;
    //     List<TipiVisita> tipidiVisite = null; 

    //     switch (tipoUtente) {
    //         case UserFactory.VOLONTARIO:
    //             volontarioCorrente = volontariManager.getVolontariMap().get(email);
    //             if (volontarioCorrente == null) {
    //                 consoleIO.mostraMessaggio("Errore: volontario non trovato.");
    //                 return null;
    //             }
    //             nome = volontarioCorrente.getNome();
    //             cognome = volontarioCorrente.getCognome();
    //             tipidiVisite = volontarioCorrente.getTipiDiVisite();

    //             // Controlla se la password è temporanea
    //             if (!isPasswordModificata(email)) {
    //                 consoleIO.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
    //                 salvaNuovaPassword(volontarioCorrente);
    //             }
    //             break;

    //         case UserFactory.CONFIGURATORE:
    //             if (email.equals("admin@example.com") && password.equals("admin123")) {
    //                 consoleIO.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
    //                 Configuratore newConfig = (Configuratore) salvaNuoveCredenziali(tipoUtente);
    //                 nome = newConfig.getNome();
    //                 cognome = newConfig.getCognome();
    //             } else {
    //                 configuratoreCorrente = configuratoriManager.getConfiguratoriMap().get(email);
    //                 nome = configuratoreCorrente.getNome();
    //                 cognome = configuratoreCorrente.getCognome();
    //             }
    //             break;
    //         case UserFactory.FRUITORE:
    //             if (fruitoreManager.getFruitoriMap().isEmpty() || !fruitoreManager.getFruitoriMap().containsKey(email)) {
    //                 consoleIO.mostraMessaggio("Benvenuto nuovo fruitore! Devi creare un account.");
    //                 Fruitore newFruitore = (Fruitore) salvaNuoveCredenziali(tipoUtente);
    //                 fruitoreCorrente = newFruitore;
    //                 nome = newFruitore.getNome();
    //                 cognome = newFruitore.getCognome();
    //             } else {
    //                 fruitoreCorrente = fruitoreManager.getFruitoriMap().get(email);
    //                 nome = fruitoreCorrente.getNome();
    //                 cognome = fruitoreCorrente.getCognome();
    //             }
    //             break;
    //         default:
    //             consoleIO.mostraMessaggio("Ruolo non riconosciuto: " + tipoUtente);
    //             return null;
    //     }

    //     // Usa la UserFactory per creare l'utente
    //     return UserFactory.createUser(tipoUtente, email, password, nome, cognome, tipidiVisite);
    // }

    public void caricaCredenzialiTemporanee() {
        databaseUpdater.getTemporaryCredentials();
    }

    public void aggiornaPasswordUtente(Utente utente, String nuovaPassword) {
        utente.setPassword(nuovaPassword);
        if (utente instanceof Volontario) {
            volontariManager.getVolontariMap().put(utente.getEmail(), (Volontario) utente);
            volontariManager.aggiornaPswVolontario(utente.getEmail(), nuovaPassword);
        }
        else if (utente instanceof Configuratore) {
            configuratoriManager.getConfiguratoriMap().put(utente.getEmail(), (Configuratore) utente);
            configuratoriManager.aggiornaPswConfiguratore(utente.getEmail(), nuovaPassword);
        } else if (utente instanceof Fruitore) {
            fruitoreManager.getFruitoriMap().put(utente.getEmail(), (Fruitore) utente);
            fruitoreManager.aggiornaPswFruitore(utente.getEmail(), nuovaPassword);
        }
    }

    public Utente creaNuoveCredenziali(String tipoUtente, String name, String surname, String newEmail, String newPassword) {
        Utente nuovoUtente = UserFactory.createUser(tipoUtente, newEmail, newPassword, name, surname, null);

        switch (tipoUtente) {
            case UserFactory.CONFIGURATORE:
                configuratoriManager.getConfiguratoriMap().put(newEmail, (Configuratore) nuovoUtente);
                configuratoriManager.aggiungiNuovoConf((Configuratore) nuovoUtente);
                break;
            case UserFactory.FRUITORE:
                fruitoreManager.getFruitoriMap().put(newEmail, (Fruitore) nuovoUtente);
                fruitoreManager.aggiungiNuovoFruitore((Fruitore) nuovoUtente);
                break;
        }
        return nuovoUtente;
    }

    // private void salvaNuovaPassword(Utente utente) {    
    //     // Inserisci la nuova password
    //     String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        
    //     // Aggiorna la password nella HashMap
    //     utente.setPassword(nuovaPassword);
    //     if (utente instanceof Volontario) {
    //         volontariManager.getVolontariMap().put(utente.getEmail(), (Volontario) utente);
    //         // Sincronizza con il database
    //         volontariManager.aggiornaPswVolontario(utente.getEmail(), nuovaPassword);
    //     }
    // }

    // private Utente salvaNuoveCredenziali(String tipoUtente) {
    //     String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
    //     String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
    //     String newEmail = InputDati.leggiStringaNonVuota("Inserisci la nuova email: ");
    //     String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");

    //     Utente nuovoUtente = UserFactory.createUser(tipoUtente, newEmail, newPassword, name, surname, null);

    //     switch (tipoUtente) {
    //         case UserFactory.CONFIGURATORE:
    //             configuratoriManager.getConfiguratoriMap().put(newEmail, (Configuratore) nuovoUtente);
    //             configuratoriManager.aggiungiNuovoConf((Configuratore) nuovoUtente);
    //             break;
    //         case UserFactory.FRUITORE:
    //             fruitoreManager.getFruitoriMap().put(newEmail, (Fruitore) nuovoUtente);
    //             fruitoreManager.aggiungiNuovoFruitore((Fruitore) nuovoUtente);
    //             break;
    //     }

    //     return nuovoUtente;
    // }

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

    public boolean isEmailPresente(String email) {
        return databaseUpdater.isEmailPresente(email);
    }

    public void aggiornaEmailUtente(Utente utente, String nuovaEmail) {
        utente.setEmail(nuovaEmail);
        if (utente instanceof Volontario) {
            volontariManager.getVolontariMap().remove(utente.getEmail());
            volontariManager.getVolontariMap().put(nuovaEmail, (Volontario) utente);
            databaseManager.aggiornaEmail(utente, nuovaEmail);
        }
        else if (utente instanceof Configuratore) {
            configuratoriManager.getConfiguratoriMap().remove(utente.getEmail());
            configuratoriManager.getConfiguratoriMap().put(nuovaEmail, (Configuratore) utente);
            databaseManager.aggiornaEmail(utente, nuovaEmail);
        } else if (utente instanceof Fruitore) {
            fruitoreManager.getFruitoriMap().remove(utente.getEmail());
            fruitoreManager.getFruitoriMap().put(nuovaEmail, (Fruitore) utente);
            databaseManager.aggiornaEmail(utente, nuovaEmail);
        }
    }

}

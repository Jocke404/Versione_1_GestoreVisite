package src.controller;

import src.view.*;
import src.model.*;


public class AuthenticationController {
    private final CredentialManager credentialManager;
    private final ConsoleView consoleView;    
    private Utente utenteLoggato;

    public AuthenticationController(
        CredentialManager credentialManager,
        ConsoleView consoleView
    ) {
        this.credentialManager = credentialManager;
        this.consoleView = consoleView;
    }

    public boolean autentica() {
        String email = consoleView.chiediEmail();
        boolean emailPresente = credentialManager.isEmailPresente(email);
        String password = consoleView.chiediPassword();

        if (emailPresente) {
            Utente utente = credentialManager.autentica(email, password);

            if (utente == null) {
                consoleView.mostraMessaggio("Credenziali non valide.");
                return false;
            }

            // Gestione password temporanea
            if (!credentialManager.isPasswordModificata(email)) {
                consoleView.mostraMessaggio("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                boolean emailCorretta = consoleView.chiediConfermaEmail(email);
                if (!emailCorretta) {
                    String nuovaEmail = consoleView.chiediNuovaEmail(credentialManager);
                    credentialManager.aggiornaEmailUtente(utente, nuovaEmail);
                    consoleView.mostraMessaggio("Email aggiornata con successo.");
                }
                modificaPasswordUtente(utente);
            }

            this.utenteLoggato = utente;
            return true;
        } else {
            consoleView.mostraMessaggio("Email non registrata. Procedi con la creazione di un nuovo account.");
            String tipoUtente = credentialManager.estraiTipoUtente(email, password);
            Utente nuovoUtente = creaNuovoUtente(tipoUtente);
            this.utenteLoggato = nuovoUtente;
            return true;
        }
    }

    public void modificaPasswordUtente(Utente utente) {
        String nuovaPassword = consoleView.chiediPassword();
        credentialManager.aggiornaPasswordUtente(utente, nuovaPassword);
        consoleView.mostraMessaggio("Password aggiornata con successo.");
    }

    public Utente creaNuovoUtente(String tipoUtente) {
        String name = consoleView.chiediNome();
        String surname = consoleView.chiediCognome();
        String newEmail = consoleView.chiediNuovaEmail(credentialManager);
        String newPassword = consoleView.chiediPassword();
        Utente nuovoUtente = credentialManager.creaNuoveCredenziali(tipoUtente, name, surname, newEmail, newPassword);
        return nuovoUtente;
    }

    public Utente getUtenteCorrente() {
        return utenteLoggato;
    }
}

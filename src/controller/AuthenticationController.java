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
        Utente utenteBase = credentialManager.autentica();
        utenteLoggato = utenteBase;

        if (!validaAutenticazioneBase(utenteBase)) {
            return false;
        }

        return true;
    }

    private boolean validaAutenticazioneBase(Utente utenteBase) {
        if (utenteBase == null) {
            consoleView.mostraMessaggio("Autenticazione fallita.");
            // resetUtenti();
            return false;
        }
        return true;
    }

    // private void resetUtenti() {
    //     this.utenteCorrente = null;
    //     this.volontarioCorrente = null;
    //     this.configuratoreCorrente = null;
    // }



    public Utente getUtenteCorrente() {
        return utenteLoggato;
    }
}

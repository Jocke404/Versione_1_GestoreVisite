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

        if (!validaAutenticazioneBase(utenteLoggato)) {
            return false;
        }

        return true;
    }

    private boolean validaAutenticazioneBase(Utente utenteLoggato) {
        if (utenteLoggato == null) {
            consoleView.mostraMessaggio("Autenticazione fallita.");
            // resetUtenti();
            return false;
        }
        return true;
    }

    public Utente getUtenteCorrente() {
        return utenteLoggato;
    }
}

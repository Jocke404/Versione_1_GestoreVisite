package src.controller;

import src.model.db.*;
import src.view.*;
import src.model.*;


public class AuthenticationController {
    private final CredentialManager credentialManager;
    private final ConsoleView consoleView;
    private final VolontariManager volontariManager;
    private final ConfiguratoriManager configuratoriManager;
    // private final CredentialManager credentialManager = CredentialManager.getInstance();
    // private final ConsoleView consoleView = new ConsoleView();
    // private final VolontariManager volontariManager = VolontariManager.getInstance();
    // private final ConfiguratoriManager configuratoriManager = ConfiguratoriManager.getInstance();

    private Volontario volontarioCorrente;
    private Configuratore configuratoreCorrente;
    private Utente utenteCorrente;
    private Utente utenteLoggato;

    private final VolontariController volontariController;
    private final ConfiguratoriController configuratoriController;
    // private final VolontariController volontariController = VolontariController.getInstance();
    // private final ConfiguratoriController configuratoriController = ConfiguratoriController.getInstance();

    // public AuthenticationController() {}
    public AuthenticationController(
        CredentialManager credentialManager,
        ConsoleView consoleView,
        VolontariManager volontariManager,
        ConfiguratoriManager configuratoriManager,
        VolontariController volontariController,
        ConfiguratoriController configuratoriController
    ) {
        this.credentialManager = credentialManager;
        this.consoleView = consoleView;
        this.volontariManager = volontariManager;
        this.configuratoriManager = configuratoriManager;
        this.volontariController = volontariController;
        this.configuratoriController = configuratoriController;
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
            resetUtenti();
            return false;
        }
        return true;
    }

    private void resetUtenti() {
        this.utenteCorrente = null;
        this.volontarioCorrente = null;
        this.configuratoreCorrente = null;
    }



    public Utente getUtenteCorrente() {
        return utenteLoggato;
    }
}

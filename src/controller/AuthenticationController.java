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

        Menu menu = creaMenuPerUtente(utenteBase);
        if (menu != null) {
            menu.mostraMenu();
            return true;
        }

        return false;
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

    private Menu creaMenuPerUtente(Utente utenteBase) {
        String email = utenteBase.getEmail();

        if (utenteBase instanceof Volontario) {
            return creaMenuVolontario(email);
        } else if (utenteBase instanceof Configuratore) {
            return creaMenuConfiguratore(email);
        } else {
            consoleView.mostraMessaggio("Tipo utente non riconosciuto.");
            resetUtenti();
            return null;
        }
    }

    private Menu creaMenuVolontario(String email) {
        this.volontarioCorrente = volontariManager.getVolontariMap().get(email);
        if (this.volontarioCorrente == null) {
            consoleView.mostraMessaggio("Errore: dati volontario non trovati nel database.");
            resetUtenti();
            return null;
        }
        this.utenteCorrente = this.volontarioCorrente;
        return new MenuVolontario(volontariController);
    }

    private Menu creaMenuConfiguratore(String email) {
        this.configuratoreCorrente = configuratoriManager.getConfiguratoriMap().get(email);
        if (this.configuratoreCorrente == null) {
            consoleView.mostraMessaggio("Errore: dati configuratore non trovati nel database.");
            resetUtenti();
            return null;
        }
        this.utenteCorrente = this.configuratoreCorrente;
        return new MenuConfiguratore(configuratoriController);
    }

    public Utente getUtenteCorrente() {
        return utenteLoggato;
    }

    // public static AuthenticationController getInstance() {
    //     return new AuthenticationController();
    // }
}

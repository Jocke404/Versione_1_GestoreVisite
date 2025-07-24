package src.controller;

import src.model.*;
import src.model.db.*;
import src.view.*;


public class MasterController {

    public Volontario volontarioCorrente;
    public Configuratore configuratoreCorrente;
    public Utente utenteCorrente;

    private final DatabaseUpdater databaseUpdater;
    private final AuthenticationController authenticationController;
    private final ConfiguratoriController configuratoriController;
    private final VolontariController volontariController;
    private final VisiteController visiteController;
    private final LuoghiController luoghiController;
    private final ThreadPoolController threadPoolController = ThreadPoolController.getInstance();
    private Boolean isAuth = false;

    public MasterController() {
        // Istanzia manager e utility
        VolontariManager volontariManager = new VolontariManager(threadPoolController);
        ConfiguratoriManager configuratoriManager = new ConfiguratoriManager(threadPoolController);
        LuoghiManager luoghiManager = new LuoghiManager(threadPoolController);
        VisiteManager visiteManager = new VisiteManager(threadPoolController);
        AggiuntaUtilita aggiuntaUtilita = new AggiuntaUtilita();
        ModificaUtilita modificaUtilita = new ModificaUtilita();
        ViewUtilita viewUtilita = new ViewUtilita();
        ConsoleView consoleView = new ConsoleView();
        CredentialManager credentialManager = new CredentialManager();

        // Istanzia controller
        volontariController = new VolontariController(volontariManager, aggiuntaUtilita, viewUtilita, null);
        configuratoriController = new ConfiguratoriController(aggiuntaUtilita, modificaUtilita, viewUtilita, configuratoriManager);
        visiteController = new VisiteController(visiteManager, viewUtilita);
        luoghiController = new LuoghiController(luoghiManager, aggiuntaUtilita, viewUtilita);

        authenticationController = new AuthenticationController(
            credentialManager, consoleView, volontariManager, configuratoriManager, volontariController, configuratoriController
        );
        databaseUpdater = new DatabaseUpdater();
    }


    public void stopExecutorService() {
        threadPoolController.shutdownAll();
    }

    public boolean autentica() {
        return isAuth = authenticationController.autentica();
    }

    public Utente utenteAutenticato(){
        if (isAuth) {
            return utenteCorrente = authenticationController.getUtenteCorrente();
        } else {
            throw new IllegalStateException("Nessun utente autenticato. Effettua prima l'autenticazione.");
        }
    }

    // public static MasterController getInstance() {
    //     return new MasterController();
    // }
    
}

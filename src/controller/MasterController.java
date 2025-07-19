package src.controller;

import src.model.*;
import src.model.db.DatabaseUpdater;


public class MasterController {

    public Volontario volontarioCorrente; // Volontario corrente
    public Configuratore configuratoreCorrente; // Configuratore corrente
    public Utente utenteCorrente; // Utente corrente (Volontario o Configuratore)
    private ConfiguratoriController configuratoriController = ConfiguratoriController.getInstance();
    private VolontariController volontariController = VolontariController.getInstance();
    private VisiteController visiteController = VisiteController.getInstance();
    private LuoghiController luoghiController = LuoghiController.getInstance();
    //Gestione Thread-------------------------------------------------------------------------
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(); // Inizializza il database updater con il gestore del thread pool
    private final AuthenticationController authenticationController = AuthenticationController.getInstance();
    private static final ThreadPoolController threadPoolController = ThreadPoolController.getInstance();
    private Boolean isAuth = false;

    public MasterController(){
        // threadPoolController.startDatabaseSync();
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

    public static MasterController getInstance() {
        return new MasterController();
    }
    
}

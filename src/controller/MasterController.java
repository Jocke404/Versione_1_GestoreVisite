package src.controller;

import src.model.*;


public class MasterController {

    public Volontario volontarioCorrente; // Volontario corrente
    public Configuratore configuratoreCorrente; // Configuratore corrente
    public Utente utenteCorrente; // Utente corrente (Volontario o Configuratore)
    private ConfiguratoriController configuratoriController = ConfiguratoriController.getInstance();
    private VolontariController volontariController = VolontariController.getInstance();
    private VisiteController visiteController = VisiteController.getInstance();
    private LuoghiController luoghiController = LuoghiController.getInstance();
    //Gestione Thread-------------------------------------------------------------------------
    private final AuthenticationController authenticationController = AuthenticationController.getInstance();
    private static final ThreadPoolController threadPoolController = ThreadPoolController.getInstance();
    private Boolean isAuth = false;

    public MasterController(){
        threadPoolController.startDatabaseSync();
    }

    public void stopExecutorService() {
        threadPoolController.shutdownAll();
    }

    public boolean autentica() {
        return isAuth = authenticationController.autentica();
    }

    public void 

    public void menu(){
        if (isAuth) {
            if (utenteCorrente instanceof Volontario) {
                volontariController.menuVolontario();
            } else if (utenteCorrente instanceof Configuratore) {
                configuratoriController.menuConfiguratore();
            }
        } else {
            System.out.println("Autenticazione non riuscita. Riprova.");
        }
    }

    public static MasterController getInstance() {
        return new MasterController();
    }
    
}

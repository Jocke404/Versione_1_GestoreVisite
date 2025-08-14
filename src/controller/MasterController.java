package src.controller;

import src.factory.UserFactory;
import src.model.*;
import src.model.db.*;
import src.view.*;


public class MasterController {

    public Volontario volontarioCorrente;
    public Configuratore configuratoreCorrente;
    public Utente utenteCorrente;
    private final ThreadPoolController threadPoolController = ThreadPoolController.getInstance();
    private AuthenticationController authenticationController;
    private Boolean isAuth = false;


    public MasterController(){}

    public static MasterController createApp() {

        ThreadPoolController threadPoolController = ThreadPoolController.getInstance();
        VolontariManager volontariManager = new VolontariManager(threadPoolController);
        ConfiguratoriManager configuratoriManager = new ConfiguratoriManager(threadPoolController);
        LuoghiManager luoghiManager = new LuoghiManager(threadPoolController);
        VisiteManagerDB visiteManager = new VisiteManagerDB(threadPoolController);
        DatabaseUpdater databaseUpdater = new DatabaseUpdater(volontariManager, configuratoriManager, luoghiManager, visiteManager);
        AggiuntaUtilita aggiuntaUtilita = new AggiuntaUtilita(volontariManager, luoghiManager, visiteManager);
        ModificaUtilita modificaUtilita = new ModificaUtilita(visiteManager);
        VisiteController visiteController = new VisiteController(visiteManager);
        ViewUtilita viewUtilita = ViewUtilita.getInstance();
        LuoghiController luoghiController = new LuoghiController(luoghiManager, aggiuntaUtilita, viewUtilita);

        ConsoleView consoleView = new ConsoleView();
        CredentialManager credentialManager = new CredentialManager(
            databaseUpdater, volontariManager, configuratoriManager
        );

        VolontariController volontariController = new VolontariController(volontariManager, aggiuntaUtilita, viewUtilita, volontarioCorrente);
        ConfiguratoriController configuratoriController = new ConfiguratoriController(
            aggiuntaUtilita, modificaUtilita, viewUtilita, volontariController, luoghiController, visiteController
        );
        
        
        AuthenticationController authenticationController = new AuthenticationController(
            credentialManager, consoleView, volontariManager, configuratoriManager, volontariController, configuratoriController
        );

        MasterController masterController = new MasterController();
        masterController.authenticationController = authenticationController;
        masterController.volontariController = volontariController;
        return masterController;
    }

   
    public void stopExecutorService() {
        threadPoolController.shutdownAll();
    }

    public boolean autentica() {
        return isAuth = authenticationController.autentica();
    }

    public void showMenu() {
        if (isAuth) {
            utenteCorrente = authenticationController.getUtenteCorrente();
            System.out.println("Benvenuto " + utenteCorrente.getNome() + "!");  
        } else {
            System.out.println("Accesso negato. Effettua prima l'autenticazione.");
        }
    }

    private Menu creaMenuPerUtente(Utente utenteBase) {
        String email = utenteBase.getEmail();

        if (utenteBase instanceof Volontario) {
            return new MenuVolontario(volontariController);
        } else if (utenteBase instanceof Configuratore) {
            return new MenuConfiguratore(configuratoriController);
        } else {
            consoleView.mostraMessaggio("Tipo utente non riconosciuto.");
            resetUtenti();
            return null;
        }
    }

    public void startApp() {
        if (autentica()) {
            showMenu();
            Menu menu = creaMenuPerUtente(utenteCorrente);
            if (menu != null) {
                menu.mostraMenu();
            } else {
                consoleView.mostraMessaggio("Errore nella creazione del menu per l'utente.");
            }
        } else {
            consoleView.mostraMessaggio("Autenticazione fallita. Riprova.");
        }
    }
}

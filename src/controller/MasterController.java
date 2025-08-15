package src.controller;

import java.util.concurrent.ExecutorService;

import src.factory.MenuFactory;
import src.factory.UserFactory;
import src.model.*;
import src.model.db.*;
import src.view.*;


public class MasterController {

    public Volontario volontarioCorrente;
    public Configuratore configuratoreCorrente;
    public Utente utenteCorrente;
    private ThreadPoolController threadPoolController = ThreadPoolController.getInstance();
    private AuthenticationController authenticationController;
    private VolontariController volontariController;
    private ConfiguratoriController configuratoriController;
    private MenuFactory menuFactory = new MenuFactory();
    private DatabaseUpdater databaseUpdater;

    private Boolean isAuth = false;


    public MasterController(){}

    public MasterController createApp() {

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
        masterController.configuratoriController = configuratoriController;
        return masterController;
    }

    public void startApp() {
        if (autentica()) {
            showMenu();
            aggiornaDatabaseAsync();
        }
    }

    private void aggiornaDatabaseAsync() {
        ExecutorService executor = threadPoolController.createThreadPool(4);
        executor.submit(()->{
            databaseUpdater.sincronizzaDalDatabase();
        });
    
    }

    public void stopExecutorService() {
        threadPoolController.shutdownAll();
    }

    private boolean autentica() {
        isAuth = authenticationController.autentica();
        if (isAuth)
            utenteCorrente = authenticationController.getUtenteCorrente();
        else
            utenteCorrente = null;
        return isAuth;
    }

    private void showMenu() {
        Menu menu = null;
        if (isAuth) {
            utenteCorrente = authenticationController.getUtenteCorrente();
            System.out.println("Benvenuto " + utenteCorrente.getNome() + "!");
            if (utenteCorrente instanceof Volontario) 
                menu = menuFactory.creaMenuVolontario(volontariController);
            else if (utenteCorrente instanceof Configuratore) 
                menu = menuFactory.creaMenuConfiguratore(configuratoriController);
        } else {
            System.out.println("Accesso negato. Effettua prima l'autenticazione.");
        }
        if (menu != null) {
            menu.mostraMenu();
        } else {
            System.out.println("Errore nella creazione del menu.");
        }
    }


}

package src.controller;

import java.util.concurrent.ExecutorService;

import src.factory.MenuFactory;
import src.model.*;
import src.model.db.*;
import src.view.*;


public class MasterController {

    public Volontario volontarioCorrente;
    public Configuratore configuratoreCorrente;
    public Fruitore fruitoreCorrente;
    public Utente utenteCorrente;
    private ThreadPoolController threadPoolController;
    private VolontariManager volontariManager;
    private ConfiguratoriManager configuratoriManager;
    private FruitoreManager fruitoreManager;
    private LuoghiManager luoghiManager;
    private VisiteManagerDB visiteManager;
    private PrenotazioneManager prenotazioneManager;
    private DatabaseUpdater databaseUpdater;
    private AggiuntaUtilita aggiuntaUtilita;
    private ModificaUtilita modificaUtilita;
    private AuthenticationController authenticationController;
    private VolontariController volontariController;
    private ConfiguratoriController configuratoriController;
    private FruitoreController fruitoreController;
    private LuoghiController luoghiController;
    private VisiteController visiteController;
    private MenuFactory menuFactory = new MenuFactory();
    private ConsoleView consoleView = new ConsoleView();



    private ViewUtilita viewUtilita;




    private Boolean isAuth = false;


    public MasterController(){}

    public MasterController createApp() {

        threadPoolController = ThreadPoolController.getInstance();
        volontariManager = new VolontariManager(threadPoolController);
        configuratoriManager = new ConfiguratoriManager(threadPoolController);
        fruitoreManager = new FruitoreManager(threadPoolController);
        luoghiManager = new LuoghiManager(threadPoolController);
        visiteManager = new VisiteManagerDB(threadPoolController);
        prenotazioneManager = new PrenotazioneManager(threadPoolController, visiteManager, fruitoreManager);
        databaseUpdater = new DatabaseUpdater(volontariManager, configuratoriManager, luoghiManager, visiteManager);
        aggiuntaUtilita = new AggiuntaUtilita(volontariManager, luoghiManager, visiteManager, prenotazioneManager);
        modificaUtilita = new ModificaUtilita(visiteManager);
        viewUtilita = ViewUtilita.getInstance();
        visiteController = new VisiteController(visiteManager);
        luoghiController = new LuoghiController(luoghiManager, aggiuntaUtilita, viewUtilita);
        

        ConsoleView consoleView = new ConsoleView();
        CredentialManager credentialManager = new CredentialManager(
            databaseUpdater, volontariManager, configuratoriManager, fruitoreManager);
        
        AuthenticationController authenticationController = new AuthenticationController(
            credentialManager, consoleView);

        MasterController masterController = this;
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
        if (isAuth) {
            utenteCorrente = authenticationController.getUtenteCorrente();
            volontariController = new VolontariController(volontariManager, aggiuntaUtilita, viewUtilita, volontarioCorrente);
            configuratoriController = new ConfiguratoriController(aggiuntaUtilita, modificaUtilita, viewUtilita, volontariController, luoghiController, visiteController);
            fruitoreController = new FruitoreController(fruitoreManager, aggiuntaUtilita, viewUtilita, fruitoreCorrente, visiteController, null);
        } else {
            utenteCorrente = null;
        }
        return isAuth;
    }

    private void showMenu() {
        Menu menu = null;
        if (isAuth) {
            //utenteCorrente = authenticationController.getUtenteCorrente();
            System.out.println("Buongiorno " + utenteCorrente.getNome() + "!");
            if (utenteCorrente instanceof Volontario){
                volontariController.volontarioCorrente = (Volontario) utenteCorrente;  
                menu = menuFactory.creaMenuVolontario(volontariController);
            } else if (utenteCorrente instanceof Configuratore){
                if(!modificaUtilita.isAmbitoConfigurato()){
                    modificaUtilita.scegliAmbitoTerritoriale();
                    modificaUtilita.salvaAmbitoTerritoriale();
                } else {
                    modificaUtilita.caricaAmbitoTerritoriale();
                }
                menu = menuFactory.creaMenuConfiguratore(configuratoriController);
            } else if (utenteCorrente instanceof Fruitore){
                fruitoreController.fruitoreCorrente = (Fruitore) utenteCorrente;
                menu = menuFactory.creaMenuFruitore(fruitoreController);
            } else {
                consoleView.mostraMessaggio("Errore: tipo di utente non riconosciuto.");
            }
        } else {
            consoleView.mostraMessaggio("Accesso negato. Effettua prima l'autenticazione.");
        }
        if (menu != null) {
            menu.mostraMenu();
        } else {
            consoleView.mostraMessaggio("Errore nella creazione del menu.");
        }
    }


}

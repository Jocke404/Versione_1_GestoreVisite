package src.it.unibs.ingsw.gestvisit.controller;

//import it.unibs.mylib.*;
import it.unibs.mylib.InputDati;
// Ensure this import matches the actual package of AggiuntaUtilita
import src.it.unibs.ingsw.gestvisit.model.*;
import src.it.unibs.ingsw.gestvisit.view.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class VisitManager {

    public Volontario volontarioCorrente; // Volontario corrente
    public Configuratore configuratoreCorrente; // Configuratore corrente
    public Utente utenteCorrente; // Utente corrente (Volontario o Configuratore)

    // Attributi------------------------------------------------------------------------------  
    private final ThreadPoolManager threadPoolManager = new ThreadPoolManager(); // Inizializza il gestore del thread pool
    private final ExecutorService executorService /* = Executors.newFixedThreadPool(4)*/; // Pool con thread caching
    private final DatabaseUpdater databaseUpdater /*= new DatabaseUpdater(executorService)*/;
    private final CredentialManager credentialManager /*= new CredentialManager(databaseUpdater)*/;
    private final ModificaUtilita modificaUtilita /*= new ModificaUtilita(databaseUpdater)*/; // Inizializza ModificaUtilita con databaseUpdater
    private final ViewUtilita viewUtilita /*= new ViewUtilita(databaseUpdater)*/; // Inizializza ViewUtilita con databaseUpdater
    private final AggiuntaUtilita addUtilita /*= new AggiuntaUtilita(databaseUpdater)*/; // Inizializza AggiuntaUtilita con databaseUpdater

    //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {

        executorService = threadPoolManager.createThreadPool(4);
        databaseUpdater = new DatabaseUpdater(executorService); // Inizializza DatabaseUpdater con executorService
        credentialManager = new CredentialManager(databaseUpdater); // Inizializza CredentialManager con databaseUpdater
        modificaUtilita = new ModificaUtilita(databaseUpdater); // Inizializza ModificaUtilita con databaseUpdater
        viewUtilita = new ViewUtilita(databaseUpdater); // Inizializza ViewUtilita con databaseUpdater
        addUtilita = new AggiuntaUtilita(databaseUpdater); // Inizializza AggiuntaUtilita con databaseUpdater

        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
        // Avvia il thread di aggiornamento periodico
        databaseUpdater.avviaSincronizzazioneConSleep(); // Esegui ogni 5 secondi
        System.out.println("Thread di aggiornamento avviato.");
    }

    public void stopExecutorService() {

        threadPoolManager.shutdownAll();

        // executorService.shutdown();
        // try {
        //     if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
        //         executorService.shutdownNow();
        //     }
        // } catch (InterruptedException e) {
        //     executorService.shutdownNow();
        //     Thread.currentThread().interrupt();
        // }
        // databaseUpdater.arrestaSincronizzazioneConSleep();
        // System.out.println("ExecutorService arrestato.");
        
    }


    //Autenticazione-------------------------------------------------------------------------
    public void autentica() {
        Utente utente = credentialManager.autentica();
        Menu menu = null; // Inizializza il menu a null

        if (utente instanceof Volontario) {
            volontarioCorrente = (Volontario) utente;
            menu = new MenuVolontario();
        } else if (utente instanceof Configuratore) {
            configuratoreCorrente = (Configuratore) utente;
            menu = new MenuConfiguratore();
        } else {
            System.out.println("Autenticazione fallita.");
        }
        menu.mostraMenu(); // Mostra il menu corrispondente
    }

    
    //Logiche per i luoghi-------------------------------------------------------------------------
    public void aggiungiLuogo() {
        addUtilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        viewUtilita.stampaLuoghi();
    }

    //Logiche per i volontari-------------------------------------------------------------------------
    public void aggiungiVolontario() {
        addUtilita.aggiungiVolontario();
    }

    public void mostraVolontari() {
        viewUtilita.stampaVolontari();
    }

    //Logiche per le visite-------------------------------------------------------------------------
    public void mostraVisite() {        
        viewUtilita.stampaVisite();
    }
    
    public void visualizzaVisitePerStato(){
        viewUtilita.stampaVisitePerStato ();
    }

    public void modificaNumeroMaxPersonePerVisita() {
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        modificaUtilita.modificaMaxPersone(numeroMax);
        System.out.println("Numero massimo di persone per visita modificato a: " + numeroMax);
            
    }

    public void modificaDataVisita() {        
        modificaUtilita.modificaDataVisita();
    }

    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        addUtilita.aggiungiVisita();
    }

    public void modificaStatoVisita() {
        modificaUtilita.modificaStatoVisita();
    }

    public void visualizzaArchivioStorico() {
        viewUtilita.stampaArchivioStorico();
    } 

    public void visualizzaVisiteVolontario(){
        viewUtilita.stampaVisiteVolontario();
    }

    public Utente getTipoUtente(){
        return utenteCorrente;
    }
    
}

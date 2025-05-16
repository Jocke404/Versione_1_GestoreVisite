package src.controller;

import java.util.Locale;
import src.model.*;
import src.view.*;


public class VisitManager {

    public Volontario volontarioCorrente; // Volontario corrente
    public Configuratore configuratoreCorrente; // Configuratore corrente
    public Utente utenteCorrente; // Utente corrente (Volontario o Configuratore)


    // Attributi------------------------------------------------------------------------------  
    private final ThreadPoolManager threadPoolManager = new ThreadPoolManager(); // Inizializza il gestore del thread pool
    private final DatabaseUpdater databaseUpdater;
    private final CredentialManager credentialManager;
    private final ModificaUtilita modificaUtilita; // Inizializza ModificaUtilita con databaseUpdater
    private final ViewUtilita viewUtilita; // Inizializza ViewUtilita con databaseUpdater
    private final AggiuntaUtilita addUtilita; // Inizializza AggiuntaUtilita con databaseUpdater
    private final ConsoleView consoleView = new ConsoleView(); // Inizializza ConsoleView

    //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {

        databaseUpdater = new DatabaseUpdater(threadPoolManager); // Inizializza DatabaseUpdater con executorService
        credentialManager = new CredentialManager(databaseUpdater); // Inizializza CredentialManager con databaseUpdater
        modificaUtilita = new ModificaUtilita(databaseUpdater); // Inizializza ModificaUtilita con databaseUpdater
        viewUtilita = new ViewUtilita(databaseUpdater); // Inizializza ViewUtilita con databaseUpdater
        addUtilita = new AggiuntaUtilita(databaseUpdater); // Inizializza AggiuntaUtilita con databaseUpdater

        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
        // Avvia il thread di aggiornamento periodico
        databaseUpdater.avviaSincronizzazioneConSleep(); // Esegui ogni 5 secondi
    }

    public void stopExecutorService() {

        databaseUpdater.arrestaSincronizzazioneConSleep(); // Arresta il thread di aggiornamento periodico
        threadPoolManager.shutdownAll();
        
    }

    // Metodo per cambiare il Locale
    public void cambiaLingua() {
        Locale systemLocale = Locale.getDefault();
        MessageProvider.setLocale(systemLocale);
    }


    //Autenticazione-------------------------------------------------------------------------
    public Utente autentica() {
        Utente utenteBase = credentialManager.autentica();
        
        if (!validaAutenticazioneBase(utenteBase)) {
            return null;
        }

        Menu menu = creaMenuPerUtente(utenteBase);
        if (menu != null) {
            menu.mostraMenu();
            return utenteCorrente;
        }
        
        return null;
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
        this.volontarioCorrente = databaseUpdater.getVolontariMap().get(email);
        if (this.volontarioCorrente == null) {
            consoleView.mostraMessaggio("Errore: dati volontario non trovati nel database.");
            resetUtenti();
            return null;
        }
        this.utenteCorrente = this.volontarioCorrente;
        return new MenuVolontario(this);
    }

    private Menu creaMenuConfiguratore(String email) {
        this.configuratoreCorrente = databaseUpdater.getConfiguratoriMap().get(email);
        if (this.configuratoreCorrente == null) {
            consoleView.mostraMessaggio("Errore: dati configuratore non trovati nel database.");
            resetUtenti();
            return null;
        }
        this.utenteCorrente = this.configuratoreCorrente;
        return new MenuConfiguratore(this);
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

    public void inserisciDisponibilitaVolontario() {
        addUtilita.inserisciDisponibilitaVolontario(volontarioCorrente);
    }

    public void mostraVolontari() {
        viewUtilita.stampaVolontari();
    }
    
    public void visualizzaVisiteVolontario(){
        viewUtilita.stampaVisiteVolontario((Volontario)utenteCorrente);
    }

    //Logiche per le visite-------------------------------------------------------------------------
    public void mostraVisite() {        
        viewUtilita.stampaVisite();
    }
    
    public void visualizzaVisitePerStato(){
        viewUtilita.stampaVisitePerStato ();
    }

    public void modificaNumeroMaxPersonePerVisita() {
        modificaUtilita.modificaMaxPersone();
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

    public Utente getTipoUtente(){
        return utenteCorrente;
    }
    
}

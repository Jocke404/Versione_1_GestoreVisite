package src.controller;

import java.util.ArrayList;
import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.ModificaUtilita;
import src.model.Visita;
import src.model.db.VisiteManagerDB;
import src.model.db.VolontariManager;
import src.view.ViewUtilita;
import src.model.AmbitoTerritoriale;
import src.view.ConsoleView;

public class ConfiguratoriController {
    private final AggiuntaUtilita addUtilita;
    private final ModificaUtilita modificaUtilita;
    private final ViewUtilita viewUtilita;
    private final AmbitoTerritoriale ambitoTerritoriale = new AmbitoTerritoriale();

    private final VolontariController volontariController;
    private final LuoghiController luoghiController;
    private final VisiteController visiteController;
    private final VisiteManagerDB visiteManagerDB = new VisiteManagerDB(ThreadPoolController.getInstance());
    private final VolontariManager volontariManager = new VolontariManager(ThreadPoolController.getInstance());
    private ConsoleView consoleView = new ConsoleView();

    public ConfiguratoriController(
        AggiuntaUtilita addUtilita, 
        ModificaUtilita modificaUtilita, 
        ViewUtilita viewUtilita, 
        VolontariController volontariController,
        LuoghiController luoghiController,
        VisiteController visiteController
    ) {
        this.addUtilita = addUtilita;
        this.modificaUtilita = modificaUtilita;
        this.viewUtilita = viewUtilita;
        this.volontariController = volontariController;
        this.luoghiController = luoghiController;
        this.visiteController = visiteController;
    }

    public void aggiungiVolontario() {
        addUtilita.aggiungiVolontario();
    }

    public void mostraVolontari() {
        viewUtilita.stampaVolontari(volontariController);
    }

    public void aggiungiLuogo() {
        addUtilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        viewUtilita.stampaLuoghi(luoghiController);
    }

    public void mostraVisite() {
        viewUtilita.stampaVisite(visiteController);
    }
    
    public void visualizzaVisitePerStato(){
        viewUtilita.stampaVisitePerStato();
    }

    public void modificaNumeroMaxPersonePerVisita() {
        modificaUtilita.modificaMaxPersone();
    }

    public void modificaDataVisita() {
        modificaUtilita.modificaDataVisita();
    }

    public void aggiungiVisita() {
        addUtilita.aggiungiVisita();
    }

    // public void modificaStatoVisita() {
    //     modificaUtilita.modificaStatoVisita();
    // }

    public void modificaStatoVisita() {
        List<Visita> visite = new ArrayList<>(visiteManagerDB.getVisiteMap().values());
        if (visite.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
        consoleView.mostraVisiteDisponibili(visite);

        int scelta = consoleView.chiediSelezioneVisita(visite.size());
        Visita visitaSelezionata = visite.get(scelta - 1);

        String statoOriginale = visitaSelezionata.getStato();
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        String nuovoStato = consoleView.chiediNuovoStato(stati);

        if (consoleView.chiediConfermaModifica(statoOriginale, nuovoStato)) {
            boolean successo = modificaUtilita.aggiornaStatoVisita(visitaSelezionata.getId(), nuovoStato);
            if (successo) {
                consoleView.mostraMessaggio("Stato della visita aggiornato con successo.");
            } else {
                consoleView.mostraMessaggio("Errore nell'aggiornamento dello stato.");
            }
        } else {
            consoleView.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
        }
    }

    public void visualizzaArchivioStorico() {
        viewUtilita.stampaArchivioStorico(visiteController);
    }

    public void aggiungiDatePrecluse() {
        addUtilita.aggiungiDatePrecluse();
    }

    public void mostraDatePrecluse() {
        viewUtilita.stampaDatePrecluse(visiteController);
    }

    public void eliminaDatePrecluse() {
        modificaUtilita.eliminaDatePrecluse();
    }

    public void mostraAmbitoTerritoriale() {
        viewUtilita.stampaAmbitoTerritoriale(ambitoTerritoriale);
    }

    public void stampaTipiVisitaPerLuogo(){
       viewUtilita.stampaTipiVisitaPerLuogo(luoghiController);
    }

    public void eliminaLuogo() {
        modificaUtilita.eliminaLuogo(luoghiController);
    }

    public void modificaLuogo() {
        modificaUtilita.modificaLuogo(luoghiController);
    }

    public void eliminaVolontario() {
        modificaUtilita.eliminaVolontario(volontariController);
    }

    public void aggiungiVolontariATipoVisita(){
        addUtilita.aggiungiVolontariATipoVisita();
    }

    public void rimuoviVolontariDaTipoVisita(){
        addUtilita.rimuoviVolontariDaTipoVisita();
    }

    public void visualizzaVolontariPerTipoVisita(){
        viewUtilita.visualizzaVolontariPerTipoVisita(volontariManager);
    }

    public void eliminaVisita() {
        modificaUtilita.eliminaVisita(visiteController);
    }

    public void modificaNumeroPersoneIscrivibili() {
        modificaUtilita.modificaNumeroPersoneIscrivibili();
    }
}

package src.controller;

import src.model.AggiuntaUtilita;
import src.model.ModificaUtilita;
import src.model.db.VisiteManagerDB;
import src.model.db.VolontariManager;
import src.view.ViewUtilita;

public class ConfiguratoriController {
    private final AggiuntaUtilita addUtilita;
    private final ModificaUtilita modificaUtilita;
    private final ViewUtilita viewUtilita;

    private final VolontariController volontariController;
    private final LuoghiController luoghiController;
    private final VisiteController visiteController;
    private final VisiteManagerDB visiteManagerDB = new VisiteManagerDB(ThreadPoolController.getInstance());
    private final VolontariManager volontariManager = new VolontariManager(ThreadPoolController.getInstance());

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

    public void modificaStatoVisita() {
        modificaUtilita.modificaStatoVisita();
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
        viewUtilita.stampaAmbitoTerritoriale(modificaUtilita);
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

    public void modificaNumeroPersoneIscivibili() {
        modificaUtilita.modificaNumeroPersoneIscivibili();
    }
}

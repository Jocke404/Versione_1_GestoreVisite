package src.controller;

import src.model.AggiuntaUtilita;
import src.model.ModificaUtilita;
import src.model.db.ConfiguratoriManager;
import src.view.ViewUtilita;

public class ConfiguratoriController {
    private final AggiuntaUtilita addUtilita;
    private final ModificaUtilita modificaUtilita;
    private final ViewUtilita viewUtilita;

    private final VolontariController volontariController;
    private final LuoghiController luoghiController;
    private final VisiteController visiteController;
    // private final AggiuntaUtilita addUtilita = AggiuntaUtilita.getInstance();
    // private final ModificaUtilita modificaUtilita = ModificaUtilita.getInstance();
    // private final ViewUtilita viewUtilita = ViewUtilita.getInstance();
    // private final ConfiguratoriManager configuratoriManager = ConfiguratoriManager.getInstance();

    // public ConfiguratoriController() {}
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
        viewUtilita.stampaVisitePerStato(visiteController);
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

    // public static ConfiguratoriController getInstance() {
    //     return new ConfiguratoriController();
    // }
}

package src.controller;

import src.model.AggiuntaUtilita;
import src.model.ModificaUtilita;
import src.model.db.ConfiguratoriManager;
import src.view.ViewUtilita;

public class ConfiguratoriController {
    private final AggiuntaUtilita addUtilita = AggiuntaUtilita.getInstance();
    private final ModificaUtilita modificaUtilita = ModificaUtilita.getInstance();
    private final ViewUtilita viewUtilita = ViewUtilita.getInstance();
    private final ConfiguratoriManager configuratoriManager = ConfiguratoriManager.getInstance();

    public ConfiguratoriController() {}

    public void aggiungiVolontario() {
        addUtilita.aggiungiVolontario();
    }

    public void mostraVolontari() {
        viewUtilita.stampaVolontari(VolontariController.getInstance());
    }

    public void aggiungiLuogo() {
        addUtilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        viewUtilita.stampaLuoghi(LuoghiController.getInstance());
    }

    public void mostraVisite() {        
        viewUtilita.stampaVisite(VisiteController.getInstance());
    }
    
    public void visualizzaVisitePerStato(){
        viewUtilita.stampaVisitePerStato(VisiteController.getInstance());
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
        viewUtilita.stampaArchivioStorico(VisiteController.getInstance());
    } 

    public static ConfiguratoriController getInstance() {
        return new ConfiguratoriController();
    }
}

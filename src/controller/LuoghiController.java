package src.controller;

import src.model.Luogo;
import src.model.db.LuoghiManager;
import src.model.AggiuntaUtilita;
import src.view.ViewUtilita;

import java.util.List;

public class LuoghiController {
    private final LuoghiManager luoghiManager;
    private final AggiuntaUtilita addUtilita;
    private final ViewUtilita viewUtilita;
    // private static final LuoghiManager luoghiManager = LuoghiManager.getInstance();
    // private final AggiuntaUtilita addUtilita = AggiuntaUtilita.getInstance(); // Utilità per l'aggiunta di dati
    // private final ViewUtilita viewUtilita = ViewUtilita.getInstance();

    // public LuoghiController() {}
    public LuoghiController(LuoghiManager luoghiManager, AggiuntaUtilita addUtilita, ViewUtilita viewUtilita) {
        this.luoghiManager = luoghiManager;
        this.addUtilita = addUtilita;
        this.viewUtilita = viewUtilita;
    }

    public void aggiungiLuogo() {
        addUtilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        viewUtilita.stampaLuoghi(this);
    }

    public List<Luogo> getLuoghi() {
        return List.copyOf(luoghiManager.getLuoghiMap().values());
    }

    public void eliminaLuogo(Luogo luogoDaEliminare) {
        luoghiManager.rimuoviLuogo(luogoDaEliminare);
    }

    public void aggiornaLuoghi(Luogo luogoDaModificare) {
        luoghiManager.aggiornaLuoghi(luogoDaModificare);
    }


}

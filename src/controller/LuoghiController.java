package src.controller;

import src.model.Luogo;
import src.model.db.LuoghiManager;
import src.model.AggiuntaUtilita;
import src.view.ViewUtilita;

import java.util.List;

public class LuoghiController {
    private static final LuoghiManager luoghiManager = LuoghiManager.getInstance();
    private final AggiuntaUtilita addUtilita = AggiuntaUtilita.getInstance(); // Utilità per l'aggiunta di dati
    private final ViewUtilita viewUtilita = ViewUtilita.getInstance();

    public LuoghiController() {}

    public void aggiungiLuogo() {
        addUtilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        viewUtilita.stampaLuoghi(getInstance());
    }

    public List<Luogo> getLuoghi() {
        return List.copyOf(luoghiManager.getLuoghiMap().values());
    }

    public static LuoghiController getInstance() {
        return new LuoghiController();
    }

}

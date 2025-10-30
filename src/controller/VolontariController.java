package src.controller;

import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.ValidatoreVisite;
import src.model.Volontario;
import src.view.ViewUtilita;
import src.model.db.VolontariManager;

public class VolontariController {
    private final VolontariManager volontariManager;
    Volontario volontarioCorrente;

    public VolontariController(VolontariManager volontariManager, AggiuntaUtilita addUtilita, 
                                Volontario volontarioCorrente, ValidatoreVisite validatore, 
                                ViewUtilita viewUtilita) {
        this.volontariManager = volontariManager;
        this.volontarioCorrente = volontarioCorrente;
    }

    public List<Volontario> getVolontari() {
        return List.copyOf(volontariManager.getVolontariMap().values());
    }
}
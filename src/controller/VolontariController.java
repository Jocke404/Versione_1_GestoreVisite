package src.controller;

import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.Volontario;
import src.view.MenuVolontario;
import src.view.ViewUtilita;
import src.model.db.VolontariManager;

public class VolontariController {
    private final VolontariManager volontariManager;
    private final AggiuntaUtilita addUtilita;
    private final ViewUtilita viewUtilita;
    Volontario volontarioCorrente;

    public VolontariController(VolontariManager volontariManager, AggiuntaUtilita addUtilita, ViewUtilita viewUtilita, Volontario volontarioCorrente) {
        this.volontariManager = volontariManager;
        this.addUtilita = addUtilita;
        this.viewUtilita = viewUtilita;
        this.volontarioCorrente = volontarioCorrente;
    }

    public void inserisciDisponibilitaVolontario() {
        addUtilita.inserisciDisponibilitaVolontario(volontarioCorrente);
    }
    
    public void visualizzaVisiteVolontario(){
        viewUtilita.stampaVisiteVolontario(volontarioCorrente);
    }

    public List<Volontario> getVolontari() {
        return List.copyOf(volontariManager.getVolontariMap().values());
    }

    public void menuVolontario() {
        MenuVolontario menuVolontario = new MenuVolontario(this);
        menuVolontario.mostraMenu();
    }
}
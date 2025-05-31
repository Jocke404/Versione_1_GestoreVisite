package src.controller;

import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.Volontario;
import src.view.MenuVolontario;
import src.view.ViewUtilita;
import src.model.db.VolontariManager;

public class VolontariController {
    private static final VolontariManager volontariManager = VolontariManager.getInstance();
    private final AggiuntaUtilita addUtilita = AggiuntaUtilita.getInstance(); // Utilità per l'aggiunta di dati
    private final ViewUtilita viewUtilita = ViewUtilita.getInstance();
    private Volontario volontarioCorrente = (Volontario) MasterController.getInstance().utenteAutenticato();

    public VolontariController(){}

    public void inserisciDisponibilitaVolontario() {
        addUtilita.inserisciDisponibilitaVolontario(volontarioCorrente);
    }
    
    public void visualizzaVisiteVolontario(){
        viewUtilita.stampaVisiteVolontario(volontarioCorrente);
    }

    public List<Volontario> getVolontari() {
        return List.copyOf(volontariManager.getVolontariMap().values());
    }

    public static VolontariController getInstance() {
        return new VolontariController();
    }

    public void menuVolontario() {
        MenuVolontario menuVolontario = new MenuVolontario(this);
        menuVolontario.mostraMenu();
    }
}
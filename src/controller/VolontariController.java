package src.controller;

import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.Volontario;
import src.model.db.VisiteManagerDB;
import src.view.ConsoleView;
import src.view.ViewUtilita;
import src.model.db.VolontariManager;

public class VolontariController {
    private static final VolontariManager volontariManager = VolontariManager.getInstance();
    private final AggiuntaUtilita addUtilita = AggiuntaUtilita.getInstance(); // Utilità per l'aggiunta di dati
    private final ViewUtilita viewUtilita = ViewUtilita.getInstance();
    private final MasterController masterController = MasterController.getInstance();

    public VolontariController(){}

    public void inserisciDisponibilitaVolontario() {
        addUtilita.inserisciDisponibilitaVolontario(volontarioCorrente);
    }
    
    public void visualizzaVisiteVolontario(){
        viewUtilita.stampaVisiteVolontario((Volontario)utenteCorrente);
    }

    public List<Volontario> getVolontari() {
        return List.copyOf(volontariManager.getVolontariMap().values());
    }

    public static VolontariController getInstance() {
        return new VolontariController();
    }
}
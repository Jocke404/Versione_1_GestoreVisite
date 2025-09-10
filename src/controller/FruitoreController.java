package src.controller;

import src.model.AggiuntaUtilita;
import src.model.Fruitore;
import src.model.db.FruitoreManager;
import src.model.db.PrenotazioneManager;
import src.view.ViewUtilita;



public class FruitoreController {
    private final FruitoreManager visitatoriManager;
    private final AggiuntaUtilita addUtilita;
    private final ViewUtilita viewUtilita;
    Fruitore fruitoreCorrente;
    private final VisiteController visiteController;
    private final PrenotazioneManager prenotazioneManager;



    public FruitoreController(FruitoreManager visitatoriManager, AggiuntaUtilita addUtilita, ViewUtilita viewUtilita, 
                            Fruitore fruitoreCorrente, VisiteController visiteController, PrenotazioneManager prenotazioneManager) {
        this.visitatoriManager = visitatoriManager;
        this.addUtilita = addUtilita;
        this.viewUtilita = viewUtilita;
        this.fruitoreCorrente = fruitoreCorrente;
        this.visiteController = visiteController;
        this.prenotazioneManager = prenotazioneManager;
    }

    public void mostraVisiteDisponibili() {
        viewUtilita.visualizzaVisiteDisponibili();
    }

    public void prenotaVisita() {
        addUtilita.prenotaVisita(fruitoreCorrente);
    }

    public Object visualizzaMiePrenotazioni() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visualizzaMiePrenotazioni'");
    }

    public Object cancellaPrenotazione() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancellaPrenotazione'");
    }


}

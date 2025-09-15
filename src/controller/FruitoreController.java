package src.controller;

import src.model.AggiuntaUtilita;
import src.model.Fruitore;
import src.model.ModificaUtilita;
import src.model.db.FruitoreManager;
import src.model.db.PrenotazioneManager;
import src.view.ViewUtilita;



public class FruitoreController {
    private final AggiuntaUtilita addUtilita;
    private final ViewUtilita viewUtilita;
    private final ModificaUtilita modificaUtilita;
    Fruitore fruitoreCorrente;
    private final PrenotazioneManager prenotazioneManager;

    

    public FruitoreController(FruitoreManager fruitoreManager, AggiuntaUtilita addUtilita, ViewUtilita viewUtilita, ModificaUtilita modificaUtilita,
                            Fruitore fruitoreCorrente, PrenotazioneManager prenotazioneManager) {
        this.modificaUtilita = modificaUtilita;
        this.addUtilita = addUtilita;
        this.viewUtilita = viewUtilita;
        this.fruitoreCorrente = fruitoreCorrente;
        this.prenotazioneManager = prenotazioneManager;
    }

    public void mostraVisiteDisponibili() {
        viewUtilita.visualizzaVisiteDisponibili();
    }

    public void prenotaVisita() {
        addUtilita.prenotaVisita(fruitoreCorrente);
    }

    public void visualizzaMiePrenotazioni() {
        viewUtilita.visualizzaPrenotazioni(fruitoreCorrente, prenotazioneManager);
    }

    public void cancellaPrenotazione() {
        modificaUtilita.cancellaPrenotazione(fruitoreCorrente, prenotazioneManager);
    }


}

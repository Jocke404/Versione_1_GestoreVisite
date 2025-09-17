package src.controller;

import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.Fruitore;
import src.model.ModificaUtilita;
import src.model.Prenotazione;
import src.model.db.FruitoreManager;
import src.model.db.PrenotazioneManager;
import src.view.ViewUtilita;
import src.view.ConsoleView;



public class FruitoreController {
    private final AggiuntaUtilita addUtilita;
    private final ViewUtilita viewUtilita;
    private final ModificaUtilita modificaUtilita;
    Fruitore fruitoreCorrente;
    private final PrenotazioneManager prenotazioneManager;
    private final ConsoleView consoleView = new ConsoleView();

    

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

    // public void cancellaPrenotazione() {
    //     modificaUtilita.cancellaPrenotazione(fruitoreCorrente, prenotazioneManager);
    // }

    public void cancellaPrenotazione() {
        List<Prenotazione> prenotazioni = prenotazioneManager.miePrenotazioni(fruitoreCorrente);
        if (prenotazioni.isEmpty()) {
            consoleView.mostraMessaggio("Non hai prenotazioni da cancellare.");
            return;
        }
        int scelta = consoleView.chiediSelezionePrenotazione(prenotazioni);
        Prenotazione prenotazioneDaCancellare = prenotazioni.get(scelta);
        if (consoleView.chiediConfermaCancellazionePrenotazione(prenotazioneDaCancellare)) {
            boolean successo = modificaUtilita.cancellaPrenotazione(prenotazioneDaCancellare, prenotazioneManager);
            consoleView.mostraRisultatoCancellazionePrenotazione(successo);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }


}

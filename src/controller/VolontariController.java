package src.controller;

import java.io.Console;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import src.model.AggiuntaUtilita;
import src.model.Disponibilita;
import src.model.ValidatoreVisite;
import src.model.Volontario;
import src.view.ConsoleIO;
import src.view.MenuVolontario;
import src.view.ViewUtilita;
import src.model.db.VolontariManager;

public class VolontariController {
    private final VolontariManager volontariManager;
    private final AggiuntaUtilita addUtilita;
    private final ConsoleIO consoleIO;
    private final Disponibilita disponibilitaManager = new Disponibilita();
    Volontario volontarioCorrente;
    private final ValidatoreVisite validatore;
    private final ViewUtilita viewUtilita;

    public VolontariController(VolontariManager volontariManager, AggiuntaUtilita addUtilita, 
                                ConsoleIO consoleIO, Volontario volontarioCorrente, ValidatoreVisite validatore, 
                                ViewUtilita viewUtilita) {
        this.volontariManager = volontariManager;
        this.addUtilita = addUtilita;
        this.consoleIO = consoleIO;
        this.volontarioCorrente = volontarioCorrente;
        this.validatore = validatore;
        this.viewUtilita = viewUtilita;
    }

    public void raccogliDisponibilitaVolontario() {
        YearMonth ym = YearMonth.now().plusMonths(1);
        List<Integer> giorniDisponibili = validatore.trovaGiorniDisponibili(volontarioCorrente, ym);
        if (giorniDisponibili.isEmpty()) {
            consoleIO.mostraMessaggio("Non ci sono giorni disponibili per dichiarare la disponibilità.");
            return;
        }
        consoleIO.mostraCalendarioMese(ym, giorniDisponibili);
        List<Integer> giorniSelezionati = consoleIO.chiediGiorniDisponibili(ym, new ArrayList<>(giorniDisponibili));
        List<LocalDate> dateDisponibili = validatore.filtraDateDisponibili(giorniSelezionati, ym);
        disponibilitaManager.salvaDisponibilita(volontarioCorrente.getEmail(), dateDisponibili);
        // Passa dateDisponibili al Model per il salvataggio
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

    public void eliminaVolontario(Volontario volontarioDaEliminare) {
        volontariManager.eliminaVolontario(volontarioDaEliminare);
    }

}
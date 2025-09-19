package src.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.InputDati;
import src.model.AggiuntaUtilita;
import src.model.ModificaUtilita;
import src.model.TipiVisita;
import src.model.Visita;
import src.model.Volontario;
import src.model.db.LuoghiManager;
import src.model.db.VisiteManagerDB;
import src.model.db.VolontariManager;
import src.view.ViewUtilita;
import src.model.AmbitoTerritoriale;
import src.model.Luogo;
import src.view.ConsoleView;

public class ConfiguratoriController {
    private final AggiuntaUtilita addUtilita;
    private final ModificaUtilita modificaUtilita;
    private final ViewUtilita viewUtilita;
    private final AmbitoTerritoriale ambitoTerritoriale = new AmbitoTerritoriale();

    private final VolontariController volontariController;
    private final LuoghiController luoghiController;
    private final VisiteController visiteController;
    private final VisiteManagerDB visiteManagerDB = new VisiteManagerDB(ThreadPoolController.getInstance());
    private final VolontariManager volontariManager = new VolontariManager(ThreadPoolController.getInstance());
    private final LuoghiManager luoghiManager = new LuoghiManager(ThreadPoolController.getInstance());
    private ConsoleView consoleView = new ConsoleView();

    public ConfiguratoriController(
        AggiuntaUtilita addUtilita, 
        ModificaUtilita modificaUtilita, 
        ViewUtilita viewUtilita, 
        VolontariController volontariController,
        LuoghiController luoghiController,
        VisiteController visiteController
    ) {
        this.addUtilita = addUtilita;
        this.modificaUtilita = modificaUtilita;
        this.viewUtilita = viewUtilita;
        this.volontariController = volontariController;
        this.luoghiController = luoghiController;
        this.visiteController = visiteController;
    }

    // public void aggiungiVolontario() {
    //     addUtilita.aggiungiVolontario();
    // }
    public void aggiungiVolontario() {
        // Ottieni i dati tramite la View
        Volontario nuovoVolontario = consoleView.chiediDatiNuovoVolontario();
        if (nuovoVolontario != null && InputDati.yesOrNo("Vuoi confermare e aggiungere il volontario?")) {
            addUtilita.aggiungiVolontario(nuovoVolontario);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    public void mostraVolontari() {
        viewUtilita.stampaVolontari(volontariController);
    }

    // public void aggiungiLuogo() {
    //     addUtilita.aggiungiLuogo();
    // }
    public void aggiungiLuogo() {
        // Ottieni i dati tramite la View
        Luogo nuovoLuogo = consoleView.chiediDatiNuovoLuogo(ambitoTerritoriale);
        if (nuovoLuogo != null && InputDati.yesOrNo("Vuoi confermare e aggiungere il luogo?")) {
            addUtilita.aggiungiLuogo(nuovoLuogo);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    public void mostraLuoghi() {
        viewUtilita.stampaLuoghi(luoghiController);
    }

    public void mostraVisite() {
        viewUtilita.stampaVisite(visiteController);
    }
    
    public void visualizzaVisitePerStato(){
        viewUtilita.stampaVisitePerStato();
    }

    // public void modificaNumeroMaxPersonePerVisita() {
    //     modificaUtilita.modificaMaxPersone();
    // }
    public void modificaMaxPersone() {
        int numeroMax = consoleView.chiediNumeroMaxPersone();
        if (consoleView.chiediConfermaNumeroMax(numeroMax)) {
            boolean successo = modificaUtilita.aggiornaMaxPersone(numeroMax);
            consoleView.mostraRisultatoAggiornamentoMaxPersone(successo, numeroMax);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaDataVisita() {
    //     modificaUtilita.modificaDataVisita();
    // }
    public void modificaDataVisita() {
        List<Visita> visite = new ArrayList<>(visiteManagerDB.getVisiteMap().values());
        if (visite.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
        int scelta = consoleView.chiediSelezioneVisita(visite);
        Visita visitaSelezionata = visite.get(scelta);
        LocalDate dataOriginale = visitaSelezionata.getData();
        LocalDate nuovaData = consoleView.chiediNuovaDataVisita(dataOriginale);
        if (consoleView.chiediConfermaModificaData(dataOriginale, nuovaData)) {
            boolean successo = modificaUtilita.aggiornaDataVisita(visitaSelezionata.getId(), nuovaData);
            consoleView.mostraRisultatoModificaData(successo);
        } else {
            consoleView.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
        }
    }

    // public void aggiungiVisita() {
    //     addUtilita.aggiungiVisita();
    // }

    public void aggiungiVisita() {
        // List<Luogo> luoghi = new ArrayList<>(luoghiManager.getLuoghiMap().values());
        // List<TipiVisita> tipiVisita = visiteManagerDB.getTipiVisitaList();
        // List<Volontario> volontari = new ArrayList<>(volontariManager.getVolontariMap().values());
        // int maxPersone = visiteManagerDB.getMaxPersone();

        // Visita nuovaVisita = consoleView.chiediDatiNuovaVisita(luoghi, tipiVisita, volontari, maxPersone);
        Visita nuovaVisita = null;
        if (consoleView.chiediAnnullaOperazione())
            return;

        if (InputDati.yesOrNo("Vuoi pianificare la visita usando le disponibilità dei volontari? (s/n)")) {
            nuovaVisita = consoleView.pianificazioneGuidata(visiteManagerDB, volontariManager, luoghiManager);
        } else {
            nuovaVisita = consoleView.pianificazioneLibera(visiteManagerDB, volontariManager, luoghiManager);
        }


        if (nuovaVisita != null && InputDati.yesOrNo("Vuoi confermare e aggiungere la visita?")) {
            boolean successo = addUtilita.aggiungiVisita(nuovaVisita);
            if (successo) {
                consoleView.mostraMessaggio("Visita aggiunta con successo!");
            } else {
                consoleView.mostraMessaggio("Errore nell'aggiunta della visita.");
            }
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaStatoVisita() {
    //     modificaUtilita.modificaStatoVisita();
    // }

    public void modificaStatoVisita() {
        List<Visita> visite = new ArrayList<>(visiteManagerDB.getVisiteMap().values());
        if (visite.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
        consoleView.mostraVisiteDisponibili(visite);

        int scelta = consoleView.chiediSelezioneVisita(visite.size());
        Visita visitaSelezionata = visite.get(scelta - 1);

        String statoOriginale = visitaSelezionata.getStato();
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        String nuovoStato = consoleView.chiediNuovoStato(stati);

        if (consoleView.chiediConfermaModifica(statoOriginale, nuovoStato)) {
            boolean successo = modificaUtilita.aggiornaStatoVisita(visitaSelezionata.getId(), nuovoStato);
            if (successo) {
                consoleView.mostraMessaggio("Stato della visita aggiornato con successo.");
            } else {
                consoleView.mostraMessaggio("Errore nell'aggiornamento dello stato.");
            }
        } else {
            consoleView.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
        }
    }

    public void visualizzaArchivioStorico() {
        viewUtilita.stampaArchivioStorico(visiteController);
    }

    public void aggiungiDatePrecluse() {
        addUtilita.aggiungiDatePrecluse();
    }

    public void mostraDatePrecluse() {
        viewUtilita.stampaDatePrecluse(visiteController);
    }

    // public void eliminaDatePrecluse() {
    //     modificaUtilita.eliminaDatePrecluse();
    // }

    public void eliminaDatePrecluse() {
        List<Map.Entry<LocalDate, String>> datePrecluse = visiteManagerDB.getDatePrecluseMap().entrySet().stream().toList();
        if (datePrecluse.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono date precluse da eliminare.");
            return;
        }
        int scelta = consoleView.chiediDataPreclusaDaEliminare(datePrecluse);
        LocalDate dataDaEliminare = datePrecluse.get(scelta).getKey();
        if (consoleView.chiediConfermaEliminazioneData(dataDaEliminare)) {
            boolean successo = modificaUtilita.eliminaDataPreclusa(dataDaEliminare);
            consoleView.mostraRisultatoEliminazioneData(successo);
        } else {
            consoleView.mostraRisultatoEliminazioneData(false);
        }
    }

    public void mostraAmbitoTerritoriale() {
        viewUtilita.stampaAmbitoTerritoriale(ambitoTerritoriale);
    }

    public void stampaTipiVisitaPerLuogo(){
       viewUtilita.stampaTipiVisitaPerLuogo(luoghiController);
    }

    // public void eliminaLuogo() {
    //     modificaUtilita.eliminaLuogo(luoghiController);
    // }

    public void eliminaLuogo() {
        List<Luogo> luoghi = luoghiController.getLuoghi();
        if (luoghi.isEmpty()) {
            consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
            return;
        }
        int scelta = consoleView.chiediSelezioneLuogo(luoghi);
        Luogo luogoDaEliminare = luoghi.get(scelta);
        if (consoleView.chiediConfermaEliminazioneLuogo(luogoDaEliminare)) {
            modificaUtilita.eliminaLuogo(luogoDaEliminare, luoghiController);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaLuogo() {
    //     modificaUtilita.modificaLuogo(luoghiController);
    // }

    public void modificaLuogo() {
        List<Luogo> luoghi = luoghiController.getLuoghi();
        if (luoghi.isEmpty()) {
            consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
            return;
        }
        int scelta = consoleView.chiediSelezioneLuogo(luoghi);
        Luogo luogoDaModificare = luoghi.get(scelta);

        String nuovoNome = consoleView.chiediNuovoNomeLuogo(luogoDaModificare.getNome());
        String nuovaDescrizione = consoleView.chiediNuovaDescrizioneLuogo(luogoDaModificare.getDescrizione());
        String nuovaCollocazione = consoleView.chiediNuovaCollocazioneLuogo(luogoDaModificare.getCollocazione());
        List<TipiVisita> nuoviTipi = consoleView.chiediNuoviTipiVisita(luogoDaModificare.getTipiVisita());

        consoleView.mostraConfrontoLuogo(luogoDaModificare, nuovoNome, nuovaDescrizione, nuovaCollocazione, nuoviTipi);

        if (InputDati.yesOrNo("Vuoi confermare e salvare le modifiche?")) {
            modificaUtilita.aggiornaLuogo(luogoDaModificare, nuovoNome, nuovaDescrizione, nuovaCollocazione, nuoviTipi, luoghiController);
        } else {
            consoleView.mostraMessaggio("Modifiche annullate. Nessun cambiamento effettuato.");
        }
    }

    // public void eliminaVolontario() {
    //     modificaUtilita.eliminaVolontario(volontariController);
    // }

    public void eliminaVolontario() {
        List<Volontario> volontari = volontariController.getVolontari();
        if (volontari.isEmpty()) {
            consoleView.mostraMessaggio("Nessun volontario disponibile per la modifica.");
            return;
        }
        int scelta = consoleView.chiediSelezioneVolontario(volontari);
        Volontario volontarioDaEliminare = volontari.get(scelta);
        if (consoleView.chiediConfermaEliminazioneVolontario(volontarioDaEliminare)) {
            modificaUtilita.eliminaVolontario(volontarioDaEliminare, volontariController);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    public void aggiungiVolontariATipoVisita(){
        addUtilita.aggiungiVolontariATipoVisita();
    }

    public void rimuoviVolontariDaTipoVisita(){
        addUtilita.rimuoviVolontariDaTipoVisita();
    }

    public void visualizzaVolontariPerTipoVisita(){
        viewUtilita.visualizzaVolontariPerTipoVisita(volontariManager);
    }

    // public void eliminaVisita() {
    //     modificaUtilita.eliminaVisita(visiteController);
    // }

    public void eliminaVisita() {
        List<Visita> visite = visiteController.getVisite();
        if (visite.isEmpty()) {
            consoleView.mostraMessaggio("Nessuna visita disponibile per la modifica.");
            return;
        }
        int scelta = consoleView.chiediSelezioneVisita(visite);
        Visita visitaDaEliminare = visite.get(scelta);
        if (consoleView.chiediConfermaEliminazioneVisita(visitaDaEliminare)) {
            modificaUtilita.eliminaVisita(visitaDaEliminare, visiteController);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaNumeroPersoneIscrivibili() {
    //     modificaUtilita.modificaNumeroPersoneIscrivibili();
    // }

    public void modificaNumeroPersoneIscrivibili() {
        int numeroMax = consoleView.chiediNumeroMaxPersone();
        if (consoleView.chiediConfermaNumeroMax(numeroMax)) {
            boolean successo = modificaUtilita.aggiornaNumeroPersoneIscrivibili(numeroMax);
            consoleView.mostraRisultatoAggiornamentoNumeroMax(successo, numeroMax);
        } else {
            consoleView.mostraMessaggio("Operazione annullata.");
        }
    }
}

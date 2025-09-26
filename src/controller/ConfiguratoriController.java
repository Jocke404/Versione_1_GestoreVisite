package src.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import src.view.ConsoleIO;

public class ConfiguratoriController {
    private final AggiuntaUtilita addUtilita;
    private final ModificaUtilita modificaUtilita;
    private final ViewUtilita viewUtilita;
    private final AmbitoTerritoriale ambitoTerritoriale = new AmbitoTerritoriale();

    private final VolontariController volontariController;
    private final LuoghiController luoghiController;
    private final VisiteController visiteController;
    private ConsoleIO consoleIO = new ConsoleIO();

    private VolontariManager volontariManager;
    private LuoghiManager luoghiManager;
    private VisiteManagerDB visiteManagerDB;


    public ConfiguratoriController(
        AggiuntaUtilita addUtilita, 
        ModificaUtilita modificaUtilita, 
        ViewUtilita viewUtilita, 
        VolontariController volontariController,
        LuoghiController luoghiController,
        VisiteController visiteController,
        VisiteManagerDB visiteManagerDB,
        VolontariManager volontariManager,
        LuoghiManager luoghiManager
    ) {
        this.addUtilita = addUtilita;
        this.modificaUtilita = modificaUtilita;
        this.viewUtilita = viewUtilita;
        this.volontariController = volontariController;
        this.luoghiController = luoghiController;
        this.visiteController = visiteController;
        this.visiteManagerDB = visiteManagerDB;
        this.volontariManager = volontariManager;   
        this.luoghiManager = luoghiManager;
    }

    // public void aggiungiVolontario() {
    //     addUtilita.aggiungiVolontario();
    // }
    public void aggiungiVolontario() {
        consoleIO.mostraElencoConOggetti(volontariManager.getVolontariMap().values().stream().toList());
        // Ottieni i dati tramite la View
        Volontario nuovoVolontario = consoleIO.chiediDatiNuovoVolontario();
        if (nuovoVolontario != null && InputDati.yesOrNo("Vuoi confermare e aggiungere il volontario?")) {
            addUtilita.aggiungiVolontario(nuovoVolontario);
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
        }
    }

    public void mostraVolontari() {
        viewUtilita.stampaVolontari(volontariController);
    }

    // public void aggiungiLuogo() {
    //     addUtilita.aggiungiLuogo();
    // }
    public void aggiungiLuogo() {
        consoleIO.mostraElencoConOggetti(luoghiManager.getLuoghiMap().values().stream().toList());
        // Ottieni i dati tramite la View
        Luogo nuovoLuogo = consoleIO.chiediDatiNuovoLuogo(ambitoTerritoriale);
        if (nuovoLuogo != null && InputDati.yesOrNo("Vuoi confermare e aggiungere il luogo?")) {
            addUtilita.aggiungiLuogo(nuovoLuogo);
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
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
        int numeroMax = consoleIO.chiediNumeroMaxPersone();
        if (consoleIO.chiediConfermaNumeroMax(numeroMax)) {
            boolean successo = modificaUtilita.aggiornaMaxPersone(numeroMax);
            consoleIO.mostraRisultatoAggiornamentoMaxPersone(successo, numeroMax);
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaDataVisita() {
    //     modificaUtilita.modificaDataVisita();
    // }
    public void modificaDataVisita() {
        List<Visita> visite = new ArrayList<>(visiteManagerDB.getVisiteMap().values());
        if (visite.isEmpty()) {
            consoleIO.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
        int scelta = consoleIO.chiediSelezioneVisita(visite);
        Visita visitaSelezionata = visite.get(scelta);
        LocalDate dataOriginale = visitaSelezionata.getData();
        LocalDate nuovaData = consoleIO.chiediNuovaDataVisita(dataOriginale);
        if (consoleIO.chiediConfermaModificaData(dataOriginale, nuovaData)) {
            boolean successo = modificaUtilita.aggiornaDataVisita(visitaSelezionata.getId(), nuovaData);
            consoleIO.mostraRisultatoModificaData(successo);
        } else {
            consoleIO.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
        }
    }

    // public void aggiungiVisita() {
    //     addUtilita.aggiungiVisita();
    // }

    public void aggiungiVisita() {
        consoleIO.mostraElencoConOggetti(visiteManagerDB.getVisiteMap().values().stream().toList());
        Visita nuovaVisita = null;
        if (consoleIO.chiediAnnullaOperazione())
            return;

        if (InputDati.yesOrNo("Vuoi pianificare la visita usando le disponibilità dei volontari? (s/n)")) {
            nuovaVisita = consoleIO.pianificazioneGuidata(visiteManagerDB, volontariManager, luoghiManager);
        } else {
            nuovaVisita = consoleIO.pianificazioneLibera(visiteManagerDB, volontariManager, luoghiManager);
        }


        if (nuovaVisita != null && InputDati.yesOrNo("Vuoi confermare e aggiungere la visita?")) {
            boolean successo = addUtilita.aggiungiVisita(nuovaVisita);
            if (successo) {
                consoleIO.mostraMessaggio("Visita aggiunta con successo!");
            } else {
                consoleIO.mostraMessaggio("Errore nell'aggiunta della visita.");
            }
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaStatoVisita() {
    //     modificaUtilita.modificaStatoVisita();
    // }

    public void modificaStatoVisita() {
        List<Visita> visite = new ArrayList<>(visiteManagerDB.getVisiteMap().values());
        if (visite.isEmpty()) {
            consoleIO.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
        consoleIO.mostraVisiteDisponibili(visite);

        int scelta = consoleIO.chiediSelezioneVisita(visite.size());
        Visita visitaSelezionata = visite.get(scelta - 1);

        String statoOriginale = visitaSelezionata.getStato();
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        String nuovoStato = consoleIO.chiediNuovoStato(stati);

        if (consoleIO.chiediConfermaModifica(statoOriginale, nuovoStato)) {
            boolean successo = modificaUtilita.aggiornaStatoVisita(visitaSelezionata.getId(), nuovoStato);
            if (successo) {
                consoleIO.mostraMessaggio("Stato della visita aggiornato con successo.");
            } else {
                consoleIO.mostraMessaggio("Errore nell'aggiornamento dello stato.");
            }
        } else {
            consoleIO.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
        }
    }

    public void visualizzaArchivioStorico() {
        viewUtilita.stampaArchivioStorico(visiteController);
    }

    // public void aggiungiDatePrecluse() {
    //     addUtilita.aggiungiDatePrecluse();
    // }

    public void aggiungiDatePrecluse() {
        consoleIO.mostraElencoConOggetti(visiteManagerDB.getDatePrecluseMap().entrySet().stream().toList());
        boolean continua = true;
        do {
            if (consoleIO.chiediAnnullaOperazione()) {
                break;
            }
            LocalDate data = consoleIO.chiediDataPreclusa();
            if (data != null) {
                String motivo = consoleIO.chiediMotivoPreclusione(data);
                addUtilita.aggiungiDataPreclusa(data, motivo);
                consoleIO.mostraMessaggio("Data preclusa aggiunta con successo.");
            }
        } while (continua);
    }

    public void mostraDatePrecluse() {
        viewUtilita.stampaDatePrecluse(visiteController);
    }

    // public void eliminaDatePrecluse() {
    //     modificaUtilita.eliminaDatePrecluse();
    // }

    public void eliminaDatePrecluse() {
        ConcurrentHashMap<LocalDate, String> datePrecluse = visiteManagerDB.getDatePrecluseMap();
        if (datePrecluse.isEmpty()) {
            consoleIO.mostraMessaggio("Non ci sono date precluse da eliminare.");
            return;
        }
        int scelta = consoleIO.chiediDataPreclusaDaEliminare(datePrecluse);
        LocalDate dataDaEliminare = new ArrayList<>(datePrecluse.keySet()).get(scelta);
        if (consoleIO.chiediConfermaEliminazioneData(dataDaEliminare)) {
            boolean successo = modificaUtilita.eliminaDataPreclusa(dataDaEliminare);
            consoleIO.mostraRisultatoEliminazioneData(successo);
        } else {
            consoleIO.mostraRisultatoEliminazioneData(false);
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
            consoleIO.mostraMessaggio("Nessun luogo disponibile per la modifica.");
            return;
        }
        int scelta = consoleIO.chiediSelezioneLuogo(luoghi);
        Luogo luogoDaEliminare = luoghi.get(scelta);
        if (consoleIO.chiediConfermaEliminazioneLuogo(luogoDaEliminare)) {
            modificaUtilita.eliminaLuogo(luogoDaEliminare, luoghiController);
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaLuogo() {
    //     modificaUtilita.modificaLuogo(luoghiController);
    // }

    public void modificaLuogo() {
        List<Luogo> luoghi = luoghiController.getLuoghi();
        if (luoghi.isEmpty()) {
            consoleIO.mostraMessaggio("Nessun luogo disponibile per la modifica.");
            return;
        }
        int scelta = consoleIO.chiediSelezioneLuogo(luoghi);
        Luogo luogoDaModificare = luoghi.get(scelta);

        String nuovoNome = consoleIO.chiediNuovoNomeLuogo(luogoDaModificare.getNome());
        String nuovaDescrizione = consoleIO.chiediNuovaDescrizioneLuogo(luogoDaModificare.getDescrizione());
        String nuovaCollocazione = consoleIO.chiediNuovaCollocazioneLuogo(luogoDaModificare.getCollocazione());
        List<TipiVisita> nuoviTipi = consoleIO.chiediNuoviTipiVisita(luogoDaModificare.getTipiVisita());

        consoleIO.mostraConfrontoLuogo(luogoDaModificare, nuovoNome, nuovaDescrizione, nuovaCollocazione, nuoviTipi);

        if (InputDati.yesOrNo("Vuoi confermare e salvare le modifiche?")) {
            modificaUtilita.aggiornaLuogo(luogoDaModificare, nuovoNome, nuovaDescrizione, nuovaCollocazione, nuoviTipi, luoghiController);
        } else {
            consoleIO.mostraMessaggio("Modifiche annullate. Nessun cambiamento effettuato.");
        }
    }

    // public void eliminaVolontario() {
    //     modificaUtilita.eliminaVolontario(volontariController);
    // }

    public void eliminaVolontario() {
        List<Volontario> volontari = volontariController.getVolontari();
        if (volontari.isEmpty()) {
            consoleIO.mostraMessaggio("Nessun volontario disponibile per la modifica.");
            return;
        }
        List<Volontario> selezionati = consoleIO.chiediVolontariMultipli(volontari);
        for (Volontario volontarioDaEliminare : selezionati) {
            if (consoleIO.chiediConfermaEliminazioneVolontario(volontarioDaEliminare)) {
                modificaUtilita.eliminaVolontario(volontarioDaEliminare, volontariController);
            } else {
                consoleIO.mostraMessaggio("Operazione annullata.");
            }
        }
    }

    // public void aggiungiVolontariATipoVisita(){
    //     addUtilita.aggiungiVolontariATipoVisita();
    // }

    public void aggiungiVolontariATipoVisita() {
        if (consoleIO.chiediAnnullaOperazione()) return;

        Map<String, Volontario> volontariMap = volontariManager.getVolontariMap();
        List<TipiVisita> tipiVisitaList = visiteManagerDB.getTipiVisitaList();

        if (tipiVisitaList.isEmpty()) {
            consoleIO.mostraMessaggio("Nessun tipo di visita disponibile.");
            return;
        }
        TipiVisita tipoVisitaScelto = consoleIO.chiediTipoVisita(tipiVisitaList);

        if (volontariMap.isEmpty()) {
            consoleIO.mostraMessaggio("Nessun volontario disponibile.");
            return;
        }
        List<Volontario> volontariDisponibili = new ArrayList<>(volontariMap.values());
        List<Volontario> volontariSelezionati = consoleIO.chiediVolontariMultipli(volontariDisponibili);

        addUtilita.assegnaTipoVisitaAVolontari(volontariSelezionati, tipoVisitaScelto);
        consoleIO.mostraMessaggio("Tipo di visita " + tipoVisitaScelto + " assegnato a " + volontariSelezionati.size() + " volontari.");
    }



    // public void rimuoviVolontariDaTipoVisita(){
    //     addUtilita.rimuoviVolontariDaTipoVisita();
    // }
    public void rimuoviVolontariDaTipoVisita() {
        if (consoleIO.chiediAnnullaOperazione()) return;

        Map<String, Volontario> volontariMap = volontariManager.getVolontariMap();
        List<TipiVisita> tipiVisitaList = visiteManagerDB.getTipiVisitaList();

        if (tipiVisitaList.isEmpty()) {
            consoleIO.mostraMessaggio("Nessun tipo di visita disponibile.");
            return;
        }
        TipiVisita tipoVisitaScelto = consoleIO.chiediTipoVisita(tipiVisitaList);

        List<Volontario> volontariConTipoVisita = volontariMap.values().stream()
            .filter(v -> v.getTipiDiVisite().contains(tipoVisitaScelto))
            .toList();

        if (volontariConTipoVisita.isEmpty()) {
            consoleIO.mostraMessaggio("Nessun volontario ha questo tipo di visita assegnato.");
            return;
        }
        List<Volontario> volontariSelezionati = consoleIO.chiediVolontariMultipli(volontariConTipoVisita);

        addUtilita.rimuoviTipoVisitaDaVolontari(volontariSelezionati, tipoVisitaScelto);
        consoleIO.mostraMessaggio("Tipo di visita " + tipoVisitaScelto + " rimosso da " + volontariSelezionati.size() + " volontari.");
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
            consoleIO.mostraMessaggio("Nessuna visita disponibile per la modifica.");
            return;
        }
        int scelta = consoleIO.chiediSelezioneVisita(visite);
        Visita visitaDaEliminare = visite.get(scelta);
        if (consoleIO.chiediConfermaEliminazioneVisita(visitaDaEliminare)) {
            modificaUtilita.eliminaVisita(visitaDaEliminare, visiteController);
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
        }
    }

    // public void modificaNumeroPersoneIscrivibili() {
    //     modificaUtilita.modificaNumeroPersoneIscrivibili();
    // }

    public void modificaNumeroPersoneIscrivibili() {
        int numeroMax = consoleIO.chiediNumeroMaxPersone();
        if (consoleIO.chiediConfermaNumeroMax(numeroMax)) {
            boolean successo = modificaUtilita.aggiornaNumeroPersoneIscrivibili(numeroMax);
            consoleIO.mostraRisultatoAggiornamentoNumeroMax(successo, numeroMax);
        } else {
            consoleIO.mostraMessaggio("Operazione annullata.");
        }
    }
}

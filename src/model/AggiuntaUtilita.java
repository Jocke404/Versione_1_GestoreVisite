package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.*;
import java.time.format.TextStyle;

import src.model.db.*;

import lib.InputDati;
import src.view.ConsoleView;
import src.view.ViewUtilita;



public class AggiuntaUtilita {

    // private final DatabaseUpdater databaseUpdater;
    private final VolontariManager volontariManager;
    private final LuoghiManager luoghiManager;
    private final VisiteManagerDB visiteManagerDB;
    ConcurrentHashMap<String, Luogo> luoghiMap;
    ConcurrentHashMap<String, Volontario> volontariMap;
    ConcurrentHashMap<String, TipiVisita> tipiVisitaMap = TipiVisita.getTipiVisitaMap();
    List<TipiVisita> tipiVisitaList;
    ConcurrentHashMap<Integer, Visita> visiteMap;
    ConcurrentHashMap<LocalDate, String> datePrecluseMap;
    private final ValidatoreVisite validatoreVisite;
    private final ViewUtilita viewUtilita = ViewUtilita.getInstance();
    private final ModificaUtilita modificaUtilita;
    private int maxPersoneIscrivibili;
    private AmbitoTerritoriale ambitoTerritoriale = new AmbitoTerritoriale();
    

    private final ConsoleView consoleView = new ConsoleView();
    private final Map<String, List<LocalDate>> disponibilitaVolontari = new ConcurrentHashMap<>();
    private final PrenotazioneManager prenotazioneManager;

    public AggiuntaUtilita(VolontariManager volontariManager, LuoghiManager luoghiManager, VisiteManagerDB visiteManagerDB, PrenotazioneManager prenotazioneManager) {
        this.volontariManager = volontariManager;
        this.luoghiManager = luoghiManager;
        this.visiteManagerDB = visiteManagerDB;
        this.luoghiMap = luoghiManager.getLuoghiMap();
        this.volontariMap = volontariManager.getVolontariMap();
        this.visiteMap = visiteManagerDB.getVisiteMap();
        this.tipiVisitaList = visiteManagerDB.getTipiVisitaList();
        this.validatoreVisite = new ValidatoreVisite(visiteManagerDB);
        this.prenotazioneManager = prenotazioneManager;
        this.modificaUtilita = new ModificaUtilita(visiteManagerDB);
    }

    public boolean aggiungiVisita(Visita nuovaVisita) {
        visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        visiteMap.put(nuovaVisita.getId(), nuovaVisita);
        return true;
    }

    // public void aggiungiVisita() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     if (InputDati.yesOrNo("Vuoi pianificare la visita usando le disponibilità dei volontari? (s/n)")) {
    //         pianificazioneGuidata();
    //     } else {
    //         pianificazioneLibera();
    //     }
    // }

    public void aggiungiVolontario(Volontario nuovoVolontario) {
        volontariMap.putIfAbsent(nuovoVolontario.getEmail(), nuovoVolontario);
        volontariManager.aggiungiNuovoVolontario(nuovoVolontario);
    }

    // // Metodo per aggiungere un volontario
    // public void aggiungiVolontario() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;
    //     String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
    //     String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
    //     String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
    //     String password = InputDati.leggiStringaNonVuota("inserire la password: ");
    //     consoleView.mostraElencoConOggetti(tipiVisitaMap.values().stream().toList());
    //     List<TipiVisita> tipiVisitaSelezionati = new ArrayList<>();
    //     boolean aggiungiAltri = true;

    //     while (aggiungiAltri) {
    //         int tipoIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere: ", 1, tipiVisitaMap.size()) - 1;
    //         TipiVisita tipoSelezionato = tipiVisitaMap.values().stream().toList().get(tipoIndex);
    //         tipiVisitaSelezionati.add(tipoSelezionato);

    //         aggiungiAltri = InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?");
    //     }

    //     Volontario nuovoVolontario = new Volontario(nome, cognome, email, password, tipiVisitaSelezionati);

    //     volontariMap.putIfAbsent(email, nuovoVolontario);

    //     volontariManager.aggiungiNuovoVolontario(nuovoVolontario);
    // }


    public void aggiungiLuogo(Luogo nuovoLuogo) {
        luoghiMap.putIfAbsent(nuovoLuogo.getNome(), nuovoLuogo);
        luoghiManager.aggiungiNuovoLuogo(nuovoLuogo);
    }

    // // Metodo per aggiungere un luogo
    // public void aggiungiLuogo() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;
    //     consoleView.mostraElencoConOggetti(luoghiMap.values().stream().toList());
    //     String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
    //     String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");
    //     consoleView.mostraElencoConOggetti(ambitoTerritoriale.getAmbitoTerritoriale());
    //     int luogoIndex = InputDati.leggiIntero("inserire la collocazione del luogo: ", 1, ambitoTerritoriale.getAmbitoTerritoriale().size()) - 1;
    //     String collocazione = ambitoTerritoriale.getAmbitoTerritoriale().get(luogoIndex);
    //     consoleView.mostraElencoConOggetti(tipiVisitaMap.values().stream().toList());
    //     List<TipiVisita> tipiVisitaSelezionati = new ArrayList<>();
    //     boolean aggiungiAltri = true;

    //     while (aggiungiAltri) {
    //         int tipoIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere: ", 1, tipiVisitaMap.size()) - 1;
    //         TipiVisita tipoSelezionato = tipiVisitaMap.values().stream().toList().get(tipoIndex);
    //         tipiVisitaSelezionati.add(tipoSelezionato);

    //         aggiungiAltri = InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?");
    //     }

    //     Luogo nuovoLuogo = new Luogo(nome, descrizione, collocazione, tipiVisitaSelezionati);
    //     luoghiMap.putIfAbsent(nome, nuovoLuogo);
    //     consoleView.mostraMessaggio("Luogo già esistente.");
    //     luoghiManager.aggiungiNuovoLuogo(nuovoLuogo);
    //     consoleView.mostraMessaggio("Luogo aggiunto: " + nuovoLuogo);
    // }


    public void aggiungiDataPreclusa(LocalDate data, String motivo) {
        visiteManagerDB.aggiungiNuovaDataPreclusa(data, motivo);
    }
    // public void aggiungiDatePrecluse() {
    //     boolean continua = true;
    //     do {
    //         if (consoleView.chiediAnnullaOperazione()){
    //             continua = false;
    //             break;
    //         }
    //         LocalDate data = InputDati.leggiData("Inserisci la data da aggiungere alle date precluse: ");
    //         if (data != null) {
    //             String motivo = InputDati.leggiStringa("Inserisci il motivo della preclusione per la data " + data + ": ");
    //             visiteManagerDB.aggiungiNuovaDataPreclusa(data, motivo);
    //         }
    //     } while (continua);
    // }



    public void prenotaVisita(Fruitore fruitoreCorrente) {
        if (consoleView.chiediAnnullaOperazione())
            return;

        List<Visita> visiteDisponibili = new ArrayList<>();
        for (Visita visita : visiteMap.values()) {
            if ((visita.getStato().equalsIgnoreCase("Proposta") || visita.getStato().equalsIgnoreCase("Confermata"))
                && visita.getPostiDisponibili() > 0) {
                visiteDisponibili.add(visita);
            }
        }

        if (visiteDisponibili.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili per la prenotazione.");
            return;
        }

        consoleView.mostraElencoConOggetti(visiteDisponibili);
        int scelta = InputDati.leggiIntero("Seleziona la visita da prenotare: ", 1, visiteDisponibili.size());

        maxPersoneIscrivibili = modificaUtilita.caricaNumeroPersoneIscrivibili();
        int numPersone = InputDati.leggiIntero("Quante persone vuoi prenotare? ", 1, Math.min(visiteDisponibili.get(scelta - 1).getPostiDisponibili(), maxPersoneIscrivibili));

        prenotazioneManager.creaPrenotazione(fruitoreCorrente, visiteDisponibili.get(scelta - 1), numPersone);
        
    }


    public void assegnaTipoVisitaAVolontari(List<Volontario> volontari, TipiVisita tipoVisita) {
        for (Volontario volontario : volontari) {
            volontariManager.aggiungiTipoVisitaAVolontari(volontario.getEmail(), tipoVisita);
        }
    }
    // public void aggiungiVolontariATipoVisita(){
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //         if (tipiVisitaList.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun tipo di visita disponibile.");
    //             return ;
    //         }

    //         consoleView.mostraMessaggio ("Seleziona il tipo di visita");
    //         consoleView.mostraElencoConOggetti(tipiVisitaList);
    //         int tipoIndex = InputDati.leggiIntero ("Seleziona il numero del tipo di visita: ", 1, tipiVisitaList.size()) -1;
    //         TipiVisita tipoVisitaScelto = tipiVisitaList.get(tipoIndex);

    //         //mostra i volontari disponbili
    //         if (volontariMap.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun volontario disponibile.");
    //             return;
    //         }

    //         consoleView.mostraMessaggio ("Seleziona i volontari da assegnare a questo tipo di visita:");
    //         consoleView.mostraElencoConOggetti(volontariMap.values().stream().toList());
    //         List<Volontario> volontariDisponibili = new ArrayList<>(volontariMap.values());

    //         //permette selezione multipla
    //         consoleView.mostraMessaggio("Inserisci i numeri dei volontari separati da virgola (es. 1,3,5):");
    //         String input = InputDati.leggiStringaNonVuota("Volontari selezionati:");
    //         String[] numeri = input.split(",");
    //         List<Volontario> volontariSelezionati = new ArrayList<>();
    //         for (String numero : numeri){
    //             try{
    //                 int index = Integer.parseInt(numero.trim()) -1;
    //                 if (index >=0 && index < volontariDisponibili.size()){
    //                     volontariSelezionati.add(volontariDisponibili.get(index));
    //                 }
    //             } catch (NumberFormatException e){
    //                 consoleView.mostraMessaggio("Input non valido: " + numero);
    //             }
    //         }

    //         //Assegna il tipo di visita ai volontari selezionati
    //         for (Volontario volontario : volontariSelezionati){
    //             volontariManager.aggiungiTipoVisitaAVolontari(volontario.getEmail(), tipoVisitaScelto);
    //         }

    //     consoleView.mostraMessaggio("Tipo di visita " + tipoVisitaScelto + " assegnato a " + volontariSelezionati.size() + " volontari.");
    // }


    public void rimuoviTipoVisitaDaVolontari(List<Volontario> volontari, TipiVisita tipoVisita) {
        for (Volontario volontario : volontari) {
            volontariManager.rimuoviTipoVisitaDaVolontario(volontario.getEmail(), tipoVisita);
        }
    }
//    // Metodo per rimuovere volontari da un tipo di visita
//     public void rimuoviVolontariDaTipoVisita() {
//         if (consoleView.chiediAnnullaOperazione())
//             return;

//         if (tipiVisitaList.isEmpty()) {
//             consoleView.mostraMessaggio("Nessun tipo di visita disponibile.");
//             return;
//         }

//         consoleView.mostraMessaggio("Seleziona il tipo di visita da cui rimuovere volontari:");
//         consoleView.mostraElencoConOggetti(tipiVisitaList);
//         int tipoIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita: ", 1, tipiVisitaList.size()) - 1;
//         TipiVisita tipoVisitaScelto = tipiVisitaList.get(tipoIndex);

//         // Trova i volontari che hanno questo tipo di visita
//         List<Volontario> volontariConTipoVisita = new ArrayList<>();
//         for (Volontario volontario : volontariMap.values()) {
//             if (volontario.getTipiDiVisite().contains(tipoVisitaScelto)) {
//                 volontariConTipoVisita.add(volontario);
//             }
//         }

//         if (volontariConTipoVisita.isEmpty()) {
//             consoleView.mostraMessaggio("Nessun volontario ha questo tipo di visita assegnato.");
//             return;
//         }

//         consoleView.mostraMessaggio("Volontari con il tipo di visita " + tipoVisitaScelto + ":");
//         consoleView.mostraElencoConOggetti(volontariConTipoVisita);

//         // Permette selezione multipla
//         consoleView.mostraMessaggio("Inserisci i numeri dei volontari da rimuovere separati da virgola (es. 1,3,5):");
//         String input = InputDati.leggiStringaNonVuota("Volontari selezionati:");
//         String[] numeri = input.split(",");
//         List<Volontario> volontariSelezionati = new ArrayList<>();
        
//         for (String numero : numeri) {
//             try {
//                 int index = Integer.parseInt(numero.trim()) - 1;
//                 if (index >= 0 && index < volontariConTipoVisita.size()) {
//                     volontariSelezionati.add(volontariConTipoVisita.get(index));
//                 }
//             } catch (NumberFormatException e) {
//                 consoleView.mostraMessaggio("Input non valido: " + numero);
//             }
//         }

//         // Rimuovi il tipo di visita dai volontari selezionati
//         for (Volontario volontario : volontariSelezionati) {
//             volontariManager.rimuoviTipoVisitaDaVolontario(volontario.getEmail(), tipoVisitaScelto);
//         }

//         consoleView.mostraMessaggio("Tipo di visita " + tipoVisitaScelto + " rimosso da " + volontariSelezionati.size() + " volontari.");
//     }

}
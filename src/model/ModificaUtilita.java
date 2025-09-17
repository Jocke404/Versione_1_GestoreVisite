package src.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import src.model.db.PrenotazioneManager;
import src.model.db.VisiteManagerDB;
import src.controller.LuoghiController;
import src.controller.VisiteController;
import src.controller.VolontariController;
import lib.InputDati;
import src.view.ConsoleView;

public class ModificaUtilita {

    private final VisiteManagerDB visiteManagerDB;
    private final ConsoleView consoleView = new ConsoleView();
    private static final String NUMERO_PERSONE_FILE = "src/utility/max_persone_iscrivibili.config";


    public ModificaUtilita(VisiteManagerDB visiteManagerDB) {
        this.visiteManagerDB = visiteManagerDB;
    }

    public boolean aggiornaStatoVisita(int visitaId, String nuovoStato) {
        Visita visita = visiteManagerDB.getVisiteMap().get(visitaId);
        if (visita == null) return false;
        visita.setStato(nuovoStato);
        visiteManagerDB.aggiornaVisita(visitaId, visita);
        return true;
    }

    // public void modificaStatoVisita() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();

    //     if (visiteMap.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
    //         return;
    //     }

    //     List<Map.Entry<Integer, Visita>> visiteList = visiteMap.entrySet().stream().toList();

    //     consoleView.mostraMessaggio("Visite disponibili:");
    //     for (int i = 0; i < visiteList.size(); i++) {
    //         Map.Entry<Integer, Visita> entry = visiteList.get(i);
    //         Visita visita = entry.getValue();
    //         consoleView.mostraMessaggio(visita.toString());
    //     }

    //     int sceltaVisita = InputDati.leggiIntero("Seleziona la visita da modificare: ", 1, visiteList.size());
    //     int visitaId = visiteList.get(sceltaVisita - 1).getKey();
    //     Visita visitaSelezionata = visiteMap.get(visitaId);

    //     String statoOriginale = visitaSelezionata.getStato();

    //     String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
    //     consoleView.mostraMessaggio("Stati disponibili:");
    //     for (int i = 0; i < stati.length; i++) {
    //         System.out.printf("%d. %s%n", i + 1, stati[i]);
    //     }

    //     int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
    //     String nuovoStato = stati[sceltaStato];

    //     // Mostra confronto tra prima e dopo
    //     consoleView.mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
    //     consoleView.mostraMessaggio("Stato: " + statoOriginale + " -> " + nuovoStato);

    //     if (InputDati.yesOrNo("Vuoi confermare e salvare la modifica dello stato?")) {
    //         visitaSelezionata.setStato(nuovoStato);
    //         visiteManagerDB.aggiornaVisita(visitaId, visitaSelezionata);
    //         consoleView.mostraMessaggio("Stato della visita aggiornato con successo.");
    //     } else {
    //         consoleView.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
    //     }
    // }

    public boolean aggiornaDataVisita(int visitaId, LocalDate nuovaData) {
        Visita visita = visiteManagerDB.getVisiteMap().get(visitaId);
        if (visita == null) return false;
        visita.setData(nuovaData);
        visiteManagerDB.aggiornaVisita(visitaId, visita);
        return true;
    }

    // public void modificaDataVisita() {
    //     ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();

    //     if (visiteMap.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
    //         return;
    //     }

    //     consoleView.mostraMessaggio("Visite disponibili:");
    //     for (Map.Entry<Integer, Visita> entry : visiteMap.entrySet()) {
    //         Visita visita = entry.getValue();
    //         consoleView.mostraMessaggio(visita.toString());
    //     }

    //     int visitaId = InputDati.leggiIntero("Seleziona l'ID della visita da modificare: ");
    //     if (!visiteMap.containsKey(visitaId)) {
    //         consoleView.mostraMessaggio("ID visita non valido.");
    //         return;
    //     }

    //     Visita visitaSelezionata = visiteMap.get(visitaId);
    //     LocalDate dataOriginale = visitaSelezionata.getData();

    //     int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ", LocalDate.now().getYear(), 2100);
    //     int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ", 1, 12);
    //     int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ", 1, LocalDate.of(anno, mese, 1).lengthOfMonth());
    //     LocalDate nuovaData = LocalDate.of(anno, mese, giorno);

    //     // Mostra confronto tra prima e dopo
    //     consoleView.mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
    //     consoleView.mostraMessaggio("Data: " + (dataOriginale != null ? dataOriginale : "Nessuna data") + " -> " + nuovaData);

    //     if (InputDati.yesOrNo("Vuoi confermare e salvare la modifica della data?")) {
    //         visitaSelezionata.setData(nuovaData);
    //         visiteManagerDB.aggiornaVisita(visitaId, visitaSelezionata);
    //         consoleView.mostraMessaggio("Data della visita aggiornata con successo.");
    //     } else {
    //         consoleView.mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
    //     }
    // }

    // // Metodo per modificare la data di una visita
    // public void modificaDataVisita() {
    //     ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();

    //     if (visiteMap.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
    //         return;
    //     }

    //     consoleView.mostraMessaggio("Visite disponibili:");
    //     for (Map.Entry<Integer, Visita> entry : visiteMap.entrySet()) {
    //         Visita visita = entry.getValue();
    //         System.out.printf("%d. Luogo: %s, Tipi Visita: %s, Volontario: %s, Data: %s%n",
    //                 entry.getKey(), visita.getLuogo(), visita.getTipiVisitaString(), visita.getVolontario(),
    //                 visita.getData() != null ? visita.getData() : "Nessuna data");
    //     }

    //     int visitaId = InputDati.leggiIntero("Seleziona l'ID della visita da modificare: ");
    //     if (!visiteMap.containsKey(visitaId)) {
    //         consoleView.mostraMessaggio("ID visita non valido.");
    //         return;
    //     }

    //     int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ", LocalDate.now().getYear(), 2100);
    //     int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ", 1, 12);
    //     int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ", 1, LocalDate.of(anno, mese, 1).lengthOfMonth());
    //     LocalDate nuovaData = LocalDate.of(anno, mese, giorno);

    //     Visita visitaAggiornata = visiteMap.get(visitaId);
    //     visitaAggiornata.setData(nuovaData);

    //     visiteManagerDB.aggiornaVisita(visitaId, visitaAggiornata);
    //     consoleView.mostraMessaggio("Data della visita aggiornata con successo.");
    // }

    // // Metodo per visualizzare le visite in base allo stato
    // public void modificaStatoVisita() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();
    
    //     if (visiteMap.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
    //         return;
    //     }
    
    //     // Converti le voci della mappa in una lista per avere un ordine stabile e un accesso tramite indice
    //     List<Map.Entry<Integer, Visita>> visiteList = visiteMap.entrySet().stream().toList();

    //     consoleView.mostraMessaggio("Visite disponibili:");
    //     for (int i = 0; i < visiteList.size(); i++) {
    //         Map.Entry<Integer, Visita> entry = visiteList.get(i);
    //         Visita visita = entry.getValue();
    //         System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Stato: %s, Data: %s, Ora Inizio: %s, Durata: %d minuti%n",
    //                 i + 1, visita.getLuogo(), visita.getTipiVisita(), visita.getStato(),
    //                 visita.getData() != null ? visita.getData() : "Nessuna data",
    //                 visita.getOraInizio() != null ? visita.getOraInizio() : "Nessuna ora",
    //                 visita.getDurataMinuti());
    //     }
    
    //     int sceltaVisita = InputDati.leggiIntero("Seleziona la visita da modificare: ", 1, visiteList.size());
        
    //     // Recupera l'ID reale della visita dalla lista usando l'indice scelto dall'utente
    //     int visitaId = visiteList.get(sceltaVisita - 1).getKey();
    
    //     String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
    //     consoleView.mostraMessaggio("Stati disponibili:");
    //     for (int i = 0; i < stati.length; i++) {
    //         System.out.printf("%d. %s%n", i + 1, stati[i]);
    //     }
    
    //     int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
    //     String nuovoStato = stati[sceltaStato];
    
    //     Visita visitaAggiornata = visiteMap.get(visitaId);
    //     visitaAggiornata.setStato(nuovoStato);
    
    //     // Aggiorna la visita nel database
    //     visiteManagerDB.aggiornaVisita(visitaId, visitaAggiornata);
    //     consoleView.mostraMessaggio("Stato della visita aggiornato con successo.");
    // }


    public boolean aggiornaMaxPersone(int numeroMax) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NUMERO_PERSONE_FILE))) {
            writer.write(String.valueOf(numeroMax));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // public void modificaMaxPersone() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;
    //     int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
    //     if(InputDati.yesOrNo("Sei sicuro di voler impostare il numero massimo di persone per visita a " + numeroMax + "?")) {
    //         visiteManagerDB.aggiornaMaxPersone(numeroMax);
    //         consoleView.mostraMessaggio("Numero massimo di persone per visita aggiornato a: " + numeroMax);
    //     } else {
    //         consoleView.mostraMessaggio("Operazione annullata.");
    //     }
    // }

    public boolean eliminaDataPreclusa(LocalDate data) {
        visiteManagerDB.eliminaData(data);
        return true;
    }

    // public void eliminaDatePrecluse() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     List<Map.Entry<LocalDate, String>> datePrecluse = visiteManagerDB.getDatePrecluseMap().entrySet().stream().toList();

    //     if (datePrecluse.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono date precluse da eliminare.");
    //         return;
    //     }
    
    //     consoleView.mostraMessaggio("Date precluse disponibili:");

    //     for (int i = 0; i < datePrecluse.size(); i++) {
    //         Map.Entry<LocalDate, String> entry = datePrecluse.get(i);
    //         System.out.printf("%d. Data: %s, Motivo: %s%n", i + 1, entry.getKey(), entry.getValue());
    //     }
    
    //     int scelta = InputDati.leggiIntero("Seleziona la data preclusa da eliminare: ", 1, datePrecluse.size()) - 1;
    //     LocalDate dataDaEliminare = datePrecluse.get(scelta).getKey();
    //     if (InputDati.yesOrNo("Sei sicuro di voler eliminare la data preclusa: " + dataDaEliminare + "?")) {
    //         visiteManagerDB.eliminaData(dataDaEliminare);
    //         consoleView.mostraMessaggio("Data preclusa eliminata con successo.");
    //     } else {
    //         consoleView.mostraMessaggio("Operazione annullata.");
    //     }
    // }

    public void eliminaLuogo(Luogo luogo, LuoghiController luoghiController) {
        luoghiController.eliminaLuogo(luogo);
    }

    // public void eliminaLuogo(LuoghiController luoghiController) {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;
    //     do {
    //         List<Luogo> luoghi = luoghiController.getLuoghi();
    //         if (luoghi.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
    //             return;
    //         }
        
    //         consoleView.mostraMessaggio("Luoghi disponibili:");
    //         consoleView.mostraElencoConOggetti(luoghi);
    //         int scelta = InputDati.leggiIntero("Seleziona il luogo da eliminare: ", 1, luoghi.size()) - 1;
    //         Luogo luogoDaEliminare = luoghi.get(scelta);
            
    //         if (InputDati.yesOrNo("Sei sicuro di voler eliminare il luogo: " + luogoDaEliminare.getNome() + "?. QUESTA AZIONE ELIMINERA' ANCHE LE VISITE AD ESSO COLLEGATE")) {
    //             luoghiController.eliminaLuogo(luogoDaEliminare);
    //         } else {
    //             consoleView.mostraMessaggio("Operazione annullata.");
    //         }
    //         if (luoghiController.getLuoghi().isEmpty()) {
    //             consoleView.mostraMessaggio("Non ci sono più luoghi disponibili.");
    //             break;
    //         }
    //     } while (InputDati.yesOrNo("Vuoi eliminare un altro luogo? "));
    // }

    public void aggiornaLuogo(Luogo luogo, String nuovoNome, String nuovaDescrizione, 
                            String nuovaCollocazione, List<TipiVisita> nuoviTipi, LuoghiController luoghiController) {
        if (!nuovoNome.isEmpty()) luogo.setName(nuovoNome);
        if (!nuovaDescrizione.isEmpty()) luogo.setDescrizione(nuovaDescrizione);
        if (!nuovaCollocazione.isEmpty()) luogo.setCollocazione(nuovaCollocazione);
        luogo.setTipiVisita(nuoviTipi);
        luoghiController.aggiornaLuoghi(luogo);
    }

    // public void modificaLuogo(LuoghiController luoghiController) {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     List<Luogo> luoghi = luoghiController.getLuoghi();
    //     if (luoghi.isEmpty()) {
    //         consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
    //         return;
    //     }

    //     consoleView.mostraMessaggio("Luoghi disponibili:");
    //     consoleView.mostraElencoConOggetti(luoghi);
    //     int scelta = InputDati.leggiIntero("Seleziona il luogo da modificare: ", 1, luoghi.size()) - 1;
    //     Luogo luogoDaModificare = luoghi.get(scelta);

    //     // Salva i dati originali per il confronto
    //     String nomeOriginale = luogoDaModificare.getNome();
    //     String descrizioneOriginale = luogoDaModificare.getDescrizione();
    //     String collocazioneOriginale = luogoDaModificare.getCollocazione();
    //     List<TipiVisita> tipiOriginali = new ArrayList<>(luogoDaModificare.getTipiVisita() != null ? luogoDaModificare.getTipiVisita() : new ArrayList<>());

    //     // Modifiche temporanee
    //     String nuovoNome = InputDati.leggiStringa("Inserisci il nuovo nome del luogo (lascia vuoto per mantenere il valore attuale: " + nomeOriginale + "): ");
    //     String nuovaDescrizione = InputDati.leggiStringa("Inserisci la nuova descrizione del luogo (lascia vuoto per mantenere il valore attuale): " + descrizioneOriginale + "): ");
    //     String nuovaCollocazione = InputDati.leggiStringa("Inserisci la nuova collocazione del luogo (lascia vuoto per mantenere il valore attuale): " + collocazioneOriginale + "): ");

    //     List<TipiVisita> nuoviTipi = new ArrayList<>(tipiOriginali);

    //     // FASE 1: Eliminazione tipi di visita
    //     if (!nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare uno o più tipi di visita attuali?")) {
    //         boolean eliminaAltro;
    //         do {
    //             consoleView.mostraMessaggio("Tipi di visita attuali:");
    //             consoleView.mostraElencoConOggetti(nuoviTipi);
    //             int sceltaElimina = InputDati.leggiIntero("Seleziona il numero del tipo di visita da eliminare (oppure 0 per terminare): ", 0, nuoviTipi.size());
    //             if (sceltaElimina == 0) {
    //                 break;
    //             }
    //             TipiVisita tipoDaEliminare = nuoviTipi.get(sceltaElimina - 1);
    //             nuoviTipi.remove(tipoDaEliminare);
    //             eliminaAltro = !nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare un altro tipo di visita?");
    //         } while (eliminaAltro);
    //     }

    //     // FASE 2: Aggiunta tipi di visita
    //     List<TipiVisita> tipiDisponibili = new ArrayList<>(List.of(TipiVisita.values()));
    //     tipiDisponibili.removeAll(nuoviTipi);

    //     if (!tipiDisponibili.isEmpty() && InputDati.yesOrNo("Vuoi aggiungere nuovi tipi di visita?")) {
    //         while (!tipiDisponibili.isEmpty()) {
    //             consoleView.mostraMessaggio("Tipi di visita che puoi ancora aggiungere:");
    //             consoleView.mostraElencoConOggetti(tipiDisponibili);

    //             int sceltaTipi = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere (oppure 0 per terminare): ", 0, tipiDisponibili.size());
    //             if (sceltaTipi == 0) {
    //                 break;
    //             }
    //             TipiVisita tipoScelto = tipiDisponibili.get(sceltaTipi - 1);
    //             nuoviTipi.add(tipoScelto);
    //             tipiDisponibili.remove(tipoScelto);

    //             if (!tipiDisponibili.isEmpty()) {
    //                 if (!InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?")) {
    //                     break;
    //                 }
    //             }
    //         }
    //     }

    //     // Mostra confronto tra prima e dopo
    //     consoleView.mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
    //     consoleView.mostraMessaggio("Nome: " + nomeOriginale + " -> " + (nuovoNome.isEmpty() ? nomeOriginale : nuovoNome));
    //     consoleView.mostraMessaggio("Descrizione: " + descrizioneOriginale + " -> " + (nuovaDescrizione.isEmpty() ? descrizioneOriginale : nuovaDescrizione));
    //     consoleView.mostraMessaggio("Collocazione: " + collocazioneOriginale + " -> " + (nuovaCollocazione.isEmpty() ? collocazioneOriginale : nuovaCollocazione));
    //     consoleView.mostraMessaggio("Tipi di visita: " + tipiOriginali + " -> " + nuoviTipi);

    //     // Conferma finale
    //     if (InputDati.yesOrNo("Vuoi confermare e salvare le modifiche?")) {
    //         if (!nuovoNome.isEmpty()) {
    //             luogoDaModificare.setName(nuovoNome);
    //         }
    //         if (!nuovaDescrizione.isEmpty()) {
    //             luogoDaModificare.setDescrizione(nuovaDescrizione);
    //         }
    //         if (!nuovaCollocazione.isEmpty()) {
    //             luogoDaModificare.setCollocazione(nuovaCollocazione);
    //         }
    //         luogoDaModificare.setTipiVisita(nuoviTipi);
    //         luoghiController.aggiornaLuoghi(luogoDaModificare);
    //         consoleView.mostraMessaggio("Modifiche salvate con successo.");
    //     } else {
    //         consoleView.mostraMessaggio("Modifiche annullate. Nessun cambiamento effettuato.");
    //     }
    // }

    // public void modificaLuogo(LuoghiController luoghiController) {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     List<Luogo> luoghi = luoghiController.getLuoghi();
    //     if (luoghi.isEmpty()) {
    //         consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
    //         return;
    //     }

    //     consoleView.mostraMessaggio("Luoghi disponibili:");
    //     consoleView.mostraElencoConOggetti(luoghi);
    //     int scelta = InputDati.leggiIntero("Seleziona il luogo da modificare: ", 1, luoghi.size()) - 1;
    //     Luogo luogoDaModificare = luoghi.get(scelta);
        
    //     String nuovoNome = InputDati.leggiStringa("Inserisci il nuovo nome del luogo (lascia vuoto per mantenere il valore attuale: " + luogoDaModificare.getNome() + "): ");
    //     if (!nuovoNome.isEmpty()) {
    //         luogoDaModificare.setName(nuovoNome);
    //     }

    //     String nuovaDescrizione = InputDati.leggiStringa("Inserisci la nuova descrizione del luogo (lascia vuoto per mantenere il valore attuale): " + luogoDaModificare.getDescrizione() + "): ");
    //     if (!nuovaDescrizione.isEmpty()) {
    //         luogoDaModificare.setDescrizione(nuovaDescrizione);
    //     }

    //     String nuovaCollocazione = InputDati.leggiStringa("Inserisci la nuova collocazione del luogo (lascia vuoto per mantenere il valore attuale): " + luogoDaModificare.getCollocazione() + "): ");
    //     if (!nuovaCollocazione.isEmpty()) {
    //         luogoDaModificare.setCollocazione(nuovaCollocazione);
    //     }
    //     consoleView.mostraMessaggio("Tipi di visite attuali: " + luogoDaModificare.getTipiVisita());
    //     List<TipiVisita> tipiAttuali = luogoDaModificare.getTipiVisita();
    //     if (tipiAttuali == null) {
    //         tipiAttuali = new ArrayList<>();
    //     }

    //     List<TipiVisita> nuoviTipi = new ArrayList<>(tipiAttuali);

    //     // FASE 1: Eliminazione tipi di visita
    //     if (!nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare uno o più tipi di visita attuali?")) {
    //         boolean eliminaAltro;
    //         do {
    //             consoleView.mostraMessaggio("Tipi di visita attuali:");
    //             consoleView.mostraElencoConOggetti(nuoviTipi);
    //             int sceltaElimina = InputDati.leggiIntero("Seleziona il numero del tipo di visita da eliminare (oppure 0 per terminare): ", 0, nuoviTipi.size());
    //             if (sceltaElimina == 0) {
    //                 break;
    //             }
    //             TipiVisita tipoDaEliminare = nuoviTipi.get(sceltaElimina - 1);
    //             nuoviTipi.remove(tipoDaEliminare);
    //             eliminaAltro = !nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare un altro tipo di visita?");
    //         } while (eliminaAltro);
    //     }

    //     // FASE 2: Aggiunta tipi di visita
    //     List<TipiVisita> tipiDisponibili = new ArrayList<>(List.of(TipiVisita.values()));
    //     tipiDisponibili.removeAll(nuoviTipi);

    //     if (!tipiDisponibili.isEmpty() && InputDati.yesOrNo("Vuoi aggiungere nuovi tipi di visita?")) {
    //         while (!tipiDisponibili.isEmpty()) {
    //             consoleView.mostraMessaggio("Tipi di visita che puoi ancora aggiungere:");
    //             consoleView.mostraElencoConOggetti(tipiDisponibili);

    //             int sceltaTipi = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere (oppure 0 per terminare): ", 0, tipiDisponibili.size());
    //             if (sceltaTipi == 0) {
    //                 break;
    //             }
    //             TipiVisita tipoScelto = tipiDisponibili.get(sceltaTipi - 1);
    //             nuoviTipi.add(tipoScelto);
    //             tipiDisponibili.remove(tipoScelto);

    //             if (!tipiDisponibili.isEmpty()) {
    //                 if (!InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?")) {
    //                     break;
    //                 }
    //             }
    //         }
    //     }

    //     luogoDaModificare.setTipiVisita(nuoviTipi);

    //     // Modifica il luogo
    //     luoghiController.aggiornaLuoghi(luogoDaModificare);
    // }

    public void eliminaVolontario(Volontario volontario, VolontariController volontariController) {
        volontariController.eliminaVolontario(volontario);
    }

    // public void eliminaVolontario(VolontariController volontariController) {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;
    //     do {
    //         List<Volontario> volontari = volontariController.getVolontari();
    //         if (volontari.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun volontario disponibile per la modifica.");
    //             return;
    //         }
    //         consoleView.mostraMessaggio("Volontari disponibili:");
    //         consoleView.mostraElencoConOggetti(volontari);
    //         int scelta = InputDati.leggiIntero("Seleziona il volontario da eliminare: ", 1, volontari.size()) - 1;
    //         Volontario volontarioDaEliminare = volontari.get(scelta);

    //         if (InputDati.yesOrNo("Sei sicuro di voler eliminare il volontario: " + volontarioDaEliminare.getNome())) {
    //             volontariController.eliminaVolontario(volontarioDaEliminare);
    //         }
    //         if (volontariController.getVolontari().isEmpty()) {
    //             consoleView.mostraMessaggio("Non ci sono più volontari disponibili.");
    //             break;
    //         }
    //     } while (InputDati.yesOrNo("Vuoi eliminare un altro volontario? "));
    // }

    public boolean cancellaPrenotazione(Prenotazione prenotazione, PrenotazioneManager prenotazioneManager) {
        return prenotazioneManager.rimuoviPrenotazione(prenotazione);
    }

    // public void cancellaPrenotazione(Fruitore fruitoreCorrente, PrenotazioneManager prenotazioneManager) {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;
    //     List<Prenotazione> prenotazioni = prenotazioneManager.miePrenotazioni(fruitoreCorrente);
    //     if (prenotazioni.isEmpty()) {
    //         consoleView.mostraMessaggio("Non hai prenotazioni da cancellare.");
    //         return;
    //     }

    //     consoleView.mostraMessaggio("Le tue prenotazioni:");
    //     consoleView.mostraElencoConOggetti(prenotazioni);
    //     int scelta = InputDati.leggiIntero("Seleziona la prenotazione da cancellare: ", 1, prenotazioni.size()) - 1;
    //     Prenotazione prenotazioneDaCancellare = prenotazioni.get(scelta);

    //     if (InputDati.yesOrNo("Sei sicuro di voler cancellare la prenotazione con codice: " + prenotazioneDaCancellare.getCodicePrenotazione() + "?")) {
    //         boolean successo = prenotazioneManager.rimuoviPrenotazione(prenotazioneDaCancellare);
    //         if (successo) {
    //             consoleView.mostraMessaggio("Prenotazione cancellata con successo.");
    //         } else {
    //             consoleView.mostraMessaggio("Errore nella cancellazione della prenotazione.");
    //         }
    //     } else {
    //         consoleView.mostraMessaggio("Operazione annullata.");
    //     }
    // }


    public void eliminaVisita(Visita visita, VisiteController visiteController) {
        visiteController.eliminaVisita(visita);
    }

    // public void eliminaVisita(VisiteController visiteController) {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     ConcurrentHashMap<Integer, Visita> visiteMap = visiteController.getVisiteMap();

    //     if (visiteMap.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono visite disponibili da eliminare.");
    //         return;
    //     }

    //     consoleView.mostraMessaggio("Visite disponibili:");
    //     consoleView.mostraElencoConOggetti(visiteMap.values().stream().toList());

    //     int visitaSelect = InputDati.leggiIntero("Seleziona la visita da eliminare: ", 1, visiteMap.size()) - 1;
    //     Visita visitaSelezionata = visiteMap.values().stream().toList().get(visitaSelect);

    //     if (InputDati.yesOrNo("Sei sicuro di voler eliminare la visita con ID: " + visitaSelezionata.getId() + "? Questa azione non può essere annullata.")) {
    //         visiteManagerDB.eliminaVisita(visitaSelezionata);
    //     }
    // }

    // Leggi il valore dal file (chiamalo all'avvio)
    public int caricaNumeroPersoneIscrivibili() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NUMERO_PERSONE_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            // Se il file non esiste o c'è errore, ritorna un valore di default
            return 10;
        }
        return 10;
    }    
    
    // Metodo per modificare il numero massimo
    public void modificaNumeroPersoneIscrivibili() {
        if (consoleView.chiediAnnullaOperazione())
            return;
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone iscrivibili per visita: ", 1);
        aggiornaNumeroPersoneIscrivibili(numeroMax);
        consoleView.mostraMessaggio("Numero massimo di persone iscrivibili per visita aggiornato a: " + numeroMax);
    }

    public boolean aggiornaNumeroPersoneIscrivibili(int numeroMax) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NUMERO_PERSONE_FILE))) {
            writer.write(String.valueOf(numeroMax));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}

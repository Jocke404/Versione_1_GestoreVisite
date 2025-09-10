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
import src.model.db.VisiteManagerDB;
import src.controller.LuoghiController;
import src.controller.VolontariController;
import src.factory.UserFactory;
import lib.InputDati;
import src.view.ConsoleView;

public class ModificaUtilita {

    private final VisiteManagerDB visiteManagerDB;
    private final ConsoleView consoleView = new ConsoleView();
    private static final String AMBITO_FILE = "src/utility/ambito_territoriale.config";
    private Set<String> ambitoTerritoriale = new HashSet<>();

    public ModificaUtilita(VisiteManagerDB visiteManagerDB) {
        this.visiteManagerDB = visiteManagerDB;
    }
    
    public void verificaOAggiornaAmbitoTerritoriale() {
        if (!isAmbitoConfigurato()) {
            scegliAmbitoTerritoriale();
            salvaAmbitoTerritoriale();
        } else {
            caricaAmbitoTerritoriale();
        }
    }

    // Metodo per modificare la data di una visita
    public void modificaDataVisita() {
        ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();

        if (visiteMap.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }

        consoleView.mostraMessaggio("Visite disponibili:");
        for (Map.Entry<Integer, Visita> entry : visiteMap.entrySet()) {
            Visita visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipi Visita: %s, Volontario: %s, Data: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipiVisitaString(), visita.getVolontario(),
                    visita.getData() != null ? visita.getData() : "Nessuna data");
        }

        int visitaId = InputDati.leggiIntero("Seleziona l'ID della visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            consoleView.mostraMessaggio("ID visita non valido.");
            return;
        }

        int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ", LocalDate.now().getYear(), 2100);
        int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ", 1, 12);
        int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ", 1, LocalDate.of(anno, mese, 1).lengthOfMonth());
        LocalDate nuovaData = LocalDate.of(anno, mese, giorno);

        Visita visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setData(nuovaData);

        visiteManagerDB.aggiornaVisita(visitaId, visitaAggiornata);
        consoleView.mostraMessaggio("Data della visita aggiornata con successo.");
    }

    // Metodo per impostare il numero massimo di persone per visita
    public void modificaMaxPersone() {
        if (consoleView.chiediAnnullaOperazione())
            return;
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        visiteManagerDB.aggiornaMaxPersone(numeroMax);
        consoleView.mostraMessaggio("Numero massimo di persone per visita aggiornato a: " + numeroMax);
    }

    // Metodo per visualizzare le visite in base allo stato
    public void modificaStatoVisita() {
        if (consoleView.chiediAnnullaOperazione())
            return;

        ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();
    
        if (visiteMap.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
    
        // Converti le voci della mappa in una lista per avere un ordine stabile e un accesso tramite indice
        List<Map.Entry<Integer, Visita>> visiteList = visiteMap.entrySet().stream().toList();

        consoleView.mostraMessaggio("Visite disponibili:");
        for (int i = 0; i < visiteList.size(); i++) {
            Map.Entry<Integer, Visita> entry = visiteList.get(i);
            Visita visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Stato: %s, Data: %s, Ora Inizio: %s, Durata: %d minuti%n",
                    i + 1, visita.getLuogo(), visita.getTipiVisita(), visita.getStato(),
                    visita.getData() != null ? visita.getData() : "Nessuna data",
                    visita.getOraInizio() != null ? visita.getOraInizio() : "Nessuna ora",
                    visita.getDurataMinuti());
        }
    
        int sceltaVisita = InputDati.leggiIntero("Seleziona la visita da modificare: ", 1, visiteList.size());
        
        // Recupera l'ID reale della visita dalla lista usando l'indice scelto dall'utente
        int visitaId = visiteList.get(sceltaVisita - 1).getKey();
    
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        consoleView.mostraMessaggio("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }
    
        int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
        String nuovoStato = stati[sceltaStato];
    
        Visita visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setStato(nuovoStato);
    
        // Aggiorna la visita nel database
        visiteManagerDB.aggiornaVisita(visitaId, visitaAggiornata);
        consoleView.mostraMessaggio("Stato della visita aggiornato con successo.");
    }

    public void eliminaDatePrecluse() {
        if (consoleView.chiediAnnullaOperazione())
            return;

        List<Map.Entry<LocalDate, String>> datePrecluse = visiteManagerDB.getDatePrecluseMap().entrySet().stream().toList();

        if (datePrecluse.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono date precluse da eliminare.");
            return;
        }
    
        consoleView.mostraMessaggio("Date precluse disponibili:");

        for (int i = 0; i < datePrecluse.size(); i++) {
            Map.Entry<LocalDate, String> entry = datePrecluse.get(i);
            System.out.printf("%d. Data: %s, Motivo: %s%n", i + 1, entry.getKey(), entry.getValue());
        }
    
        int scelta = InputDati.leggiIntero("Seleziona la data preclusa da eliminare: ", 1, datePrecluse.size()) - 1;
        LocalDate dataDaEliminare = datePrecluse.get(scelta).getKey();
        visiteManagerDB.eliminaData(dataDaEliminare);
    }


    // Controlla se l'ambito è già stato configurato
    public boolean isAmbitoConfigurato() {
        File file = new File(AMBITO_FILE);
        return file.exists();
    }

    // Metodo per la scelta dell'ambito territoriale (uno o più comuni)
    public void scegliAmbitoTerritoriale() {
        consoleView.mostraMessaggio("Configurazione ambito territoriale (inserisci uno o più comuni).");
        boolean aggiungiAltro = true;
        while (aggiungiAltro) {
            String comune = InputDati.leggiStringaNonVuota("Inserisci il nome del comune: ");
            ambitoTerritoriale.add(comune);
            aggiungiAltro = InputDati.yesOrNo("Vuoi aggiungere un altro comune? (s/n): ");
        }
        consoleView.mostraMessaggio("Ambito territoriale configurato: " + ambitoTerritoriale);
    }

    // Salva l'ambito territoriale su file
    public void salvaAmbitoTerritoriale() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AMBITO_FILE))) {
            for (String comune : ambitoTerritoriale) {
                writer.write(comune);
                writer.newLine();
            }
        } catch (IOException e) {
            consoleView.mostraMessaggio("Errore nel salvataggio dell'ambito territoriale.");
        }
    }

    // Carica l'ambito territoriale da file
    public void caricaAmbitoTerritoriale() {
        ambitoTerritoriale.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(AMBITO_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ambitoTerritoriale.add(line.trim());
            }
        } catch (IOException e) {
            consoleView.mostraMessaggio("Errore nel caricamento dell'ambito territoriale.");
        }
    }

    // Metodo pubblico per ottenere l'ambito territoriale
    public Set<String> getAmbitoTerritoriale() {
        if (ambitoTerritoriale.isEmpty()) {
            caricaAmbitoTerritoriale();
        }
        return new HashSet<>(ambitoTerritoriale);
    }

    public void eliminaLuogo(LuoghiController luoghiController) {
        if (consoleView.chiediAnnullaOperazione())
            return;
        do {
            List<Luogo> luoghi = luoghiController.getLuoghi();
            if (luoghi.isEmpty()) {
                consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
                return;
            }
        
            consoleView.mostraMessaggio("Luoghi disponibili:");
            consoleView.mostraElencoConOggetti(luoghi);
            int scelta = InputDati.leggiIntero("Seleziona il luogo da eliminare: ", 1, luoghi.size()) - 1;
            Luogo luogoDaEliminare = luoghi.get(scelta);
            
            if (InputDati.yesOrNo("Sei sicuro di voler eliminare il luogo: " + luogoDaEliminare.getNome() + "?. QUESTA AZIONE ELIMINERA' ANCHE LE VISITE AD ESSO COLLEGATE")) {
                luoghiController.eliminaLuogo(luogoDaEliminare);
            } else {
                consoleView.mostraMessaggio("Operazione annullata.");
            }
            if (luoghiController.getLuoghi().isEmpty()) {
                consoleView.mostraMessaggio("Non ci sono più luoghi disponibili.");
                break;
            }
        } while (InputDati.yesOrNo("Vuoi eliminare un altro luogo? "));
    }

    public void modificaLuogo(LuoghiController luoghiController) {
        if (consoleView.chiediAnnullaOperazione())
            return;

        List<Luogo> luoghi = luoghiController.getLuoghi();
        if (luoghi.isEmpty()) {
            consoleView.mostraMessaggio("Nessun luogo disponibile per la modifica.");
            return;
        }

        consoleView.mostraMessaggio("Luoghi disponibili:");
        consoleView.mostraElencoConOggetti(luoghi);
        int scelta = InputDati.leggiIntero("Seleziona il luogo da modificare: ", 1, luoghi.size()) - 1;
        Luogo luogoDaModificare = luoghi.get(scelta);
        
        String nuovoNome = InputDati.leggiStringa("Inserisci il nuovo nome del luogo (lascia vuoto per mantenere il valore attuale: " + luogoDaModificare.getNome() + "): ");
        if (!nuovoNome.isEmpty()) {
            luogoDaModificare.setName(nuovoNome);
        }

        String nuovaDescrizione = InputDati.leggiStringa("Inserisci la nuova descrizione del luogo (lascia vuoto per mantenere il valore attuale): " + luogoDaModificare.getDescrizione() + "): ");
        if (!nuovaDescrizione.isEmpty()) {
            luogoDaModificare.setDescrizione(nuovaDescrizione);
        }

        String nuovaCollocazione = InputDati.leggiStringa("Inserisci la nuova collocazione del luogo (lascia vuoto per mantenere il valore attuale): " + luogoDaModificare.getCollocazione() + "): ");
        if (!nuovaCollocazione.isEmpty()) {
            luogoDaModificare.setCollocazione(nuovaCollocazione);
        }
        consoleView.mostraMessaggio("Tipi di visite attuali: " + luogoDaModificare.getTipiVisita());
        List<TipiVisita> tipiAttuali = luogoDaModificare.getTipiVisita();
        if (tipiAttuali == null) {
            tipiAttuali = new ArrayList<>();
        }

        List<TipiVisita> nuoviTipi = new ArrayList<>(tipiAttuali);

        // FASE 1: Eliminazione tipi di visita
        if (!nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare uno o più tipi di visita attuali?")) {
            boolean eliminaAltro;
            do {
                consoleView.mostraMessaggio("Tipi di visita attuali:");
                consoleView.mostraElencoConOggetti(nuoviTipi);
                int sceltaElimina = InputDati.leggiIntero("Seleziona il numero del tipo di visita da eliminare (oppure 0 per terminare): ", 0, nuoviTipi.size());
                if (sceltaElimina == 0) {
                    break;
                }
                TipiVisita tipoDaEliminare = nuoviTipi.get(sceltaElimina - 1);
                nuoviTipi.remove(tipoDaEliminare);
                eliminaAltro = !nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare un altro tipo di visita?");
            } while (eliminaAltro);
        }

        // FASE 2: Aggiunta tipi di visita
        List<TipiVisita> tipiDisponibili = new ArrayList<>(List.of(TipiVisita.values()));
        tipiDisponibili.removeAll(nuoviTipi);

        if (!tipiDisponibili.isEmpty() && InputDati.yesOrNo("Vuoi aggiungere nuovi tipi di visita?")) {
            while (!tipiDisponibili.isEmpty()) {
                consoleView.mostraMessaggio("Tipi di visita che puoi ancora aggiungere:");
                consoleView.mostraElencoConOggetti(tipiDisponibili);

                int sceltaTipi = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere (oppure 0 per terminare): ", 0, tipiDisponibili.size());
                if (sceltaTipi == 0) {
                    break;
                }
                TipiVisita tipoScelto = tipiDisponibili.get(sceltaTipi - 1);
                nuoviTipi.add(tipoScelto);
                tipiDisponibili.remove(tipoScelto);

                if (!tipiDisponibili.isEmpty()) {
                    if (!InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?")) {
                        break;
                    }
                }
            }
        }

        luogoDaModificare.setTipiVisita(nuoviTipi);

        // Modifica il luogo
        luoghiController.aggiornaLuoghi(luogoDaModificare);
    }

    public void eliminaVolontario(VolontariController volontariController) {
        if (consoleView.chiediAnnullaOperazione())
            return;
        do {
            List<Volontario> volontari = volontariController.getVolontari();
            if (volontari.isEmpty()) {
                consoleView.mostraMessaggio("Nessun volontario disponibile per la modifica.");
                return;
            }
            consoleView.mostraMessaggio("Volontari disponibili:");
            consoleView.mostraElencoConOggetti(volontari);
            int scelta = InputDati.leggiIntero("Seleziona il volontario da eliminare: ", 1, volontari.size()) - 1;
            Volontario volontarioDaEliminare = volontari.get(scelta);

            if (InputDati.yesOrNo("Sei sicuro di voler eliminare il volontario: " + volontarioDaEliminare.getNome())) {
                volontariController.eliminaVolontario(volontarioDaEliminare);
            }
            if (volontariController.getVolontari().isEmpty()) {
                consoleView.mostraMessaggio("Non ci sono più volontari disponibili.");
                break;
            }
        } while (InputDati.yesOrNo("Vuoi eliminare un altro volontario? "));
    }

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
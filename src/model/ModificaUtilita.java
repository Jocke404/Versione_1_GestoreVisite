package src.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import src.model.db.VisiteManagerDB;
import src.controller.LuoghiController;
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
        ConcurrentHashMap<Integer, Visite> visiteMap = visiteManagerDB.getVisiteMap();

        if (visiteMap.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }

        consoleView.mostraMessaggio("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
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

        Visite visitaAggiornata = visiteMap.get(visitaId);
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

        ConcurrentHashMap<Integer, Visite> visiteMap = visiteManagerDB.getVisiteMap();
    
        if (visiteMap.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono visite disponibili da modificare.");
            return;
        }
    
        consoleView.mostraMessaggio("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Stato: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getStato());
        }
    
        int visitaId = InputDati.leggiIntero("Seleziona la visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            consoleView.mostraMessaggio("Visita non valida.");
            return;
        }
    
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        consoleView.mostraMessaggio("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }
    
        int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
        String nuovoStato = stati[sceltaStato];
    
        Visite visitaAggiornata = visiteMap.get(visitaId);
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

        // Chiedi i nuovi dati per il luogo
        String nuovoNome = InputDati.leggiStringaNonVuota("Inserisci il nuovo nome del luogo (lascia vuoto per mantenere il valore attuale: " + luogoDaModificare.getNome() + "): ");
        String nuovaDescrizione = InputDati.leggiStringaNonVuota("Inserisci la nuova descrizione del luogo (lascia vuoto per mantenere il valore attuale): " + luogoDaModificare.getDescrizione() + ": ");
        String nuovaCollocazione = InputDati.leggiStringaNonVuota("Inserisci la nuova collocazione del luogo (lascia vuoto per mantenere il valore attuale): " + luogoDaModificare.getCollocazione() + ": ");

        luogoDaModificare.setName(nuovoNome);
        luogoDaModificare.setDescrizione(nuovaDescrizione);
        luogoDaModificare.setCollocazione(nuovaCollocazione);

        // Modifica il luogo
        luoghiController.aggiornaLuoghi(luogoDaModificare);
    }

}
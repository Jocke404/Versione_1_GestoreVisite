package src.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import src.model.db.VisiteManagerDB;


import lib.InputDati;
import src.view.ConsoleView;

public class ModificaUtilita {

    private static final VisiteManagerDB visiteManagerDB = VisiteManagerDB.getInstance();
    private final ConsoleView consoleView = new ConsoleView();

    public ModificaUtilita() {}

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
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        visiteManagerDB.aggiornaMaxPersonePerVisita(numeroMax);
        consoleView.mostraMessaggio("Numero massimo di persone per visita aggiornato a: " + numeroMax);
    }

    // Metodo per visualizzare le visite in base allo stato
    public void modificaStatoVisita() {
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

    public static ModificaUtilita getInstance() {
        return new ModificaUtilita();
    }

}
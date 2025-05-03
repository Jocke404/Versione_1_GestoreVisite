package src.it.unibs.ingsw.gestvisit.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unibs.mylib.InputDati;
import src.it.unibs.ingsw.gestvisit.model.Visite;

public class ModificaUtilita {

    private final DatabaseUpdater databaseUpdater;

    public ModificaUtilita(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

    // Metodo per modificare la data di una visita
    public void modificaDataVisita() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili da modificare.");
            return;
        }

        System.out.println("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                    visita.getData() != null ? visita.getData() : "Nessuna data");
        }

        int visitaId = InputDati.leggiIntero("Seleziona l'ID della visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            System.out.println("ID visita non valido.");
            return;
        }

        int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ", LocalDate.now().getYear(), 2100);
        int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ", 1, 12);
        int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ", 1, LocalDate.of(anno, mese, 1).lengthOfMonth());
        LocalDate nuovaData = LocalDate.of(anno, mese, giorno);

        Visite visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setData(nuovaData);

        databaseUpdater.aggiornaVisita(visitaId, visitaAggiornata);
        System.out.println("Data della visita aggiornata con successo.");
    }

    // Metodo per impostare il numero massimo di persone per visita
    public void modificaMaxPersone(int maxPersonePerVisita) {
        databaseUpdater.aggiornaMaxPersonePerVisita(maxPersonePerVisita);
        System.out.println("Numero massimo di persone per visita aggiornato a: " + maxPersonePerVisita);
    }

    // Metodo per visualizzare le visite in base allo stato
    public void modificaStatoVisita() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
    
        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili da modificare.");
            return;
        }
    
        System.out.println("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Stato: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getStato());
        }
    
        int visitaId = InputDati.leggiIntero("Seleziona la visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            System.out.println("Visita non valida.");
            return;
        }
    
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        System.out.println("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }
    
        int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
        String nuovoStato = stati[sceltaStato];
    
        Visite visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setStato(nuovoStato);
    
        // Aggiorna la visita nel database
        databaseUpdater.aggiornaVisita(visitaId, visitaAggiornata);
        System.out.println("Stato della visita aggiornato con successo.");
    }

}
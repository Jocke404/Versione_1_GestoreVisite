package src.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lib.InputDati;

import src.controller.VisitManager;
import src.model.*;
import src.model.db.DatabaseUpdater;

public class ViewUtilita {
    private final DatabaseUpdater databaseUpdater;

    public ViewUtilita(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

        // Metodo per stampare i luoghi
    public void stampaLuoghi() {
        ConcurrentHashMap<String, Luogo> luoghiMap = databaseUpdater.getLuoghiMap();

        if (luoghiMap.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }

        System.out.println("Luoghi:");
        for (Luogo luogo : luoghiMap.values()) {
            System.out.println("Nome: " + luogo.getNome());
            System.out.println("Descrizione: " + luogo.getDescrizione());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare i volontari
    public void stampaVolontari() {
        ConcurrentHashMap<String, Volontario> volontariMap = databaseUpdater.getVolontariMap();

        if (volontariMap.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }

        System.out.println("Volontari:");
        for (Volontario volontario : volontariMap.values()) {
            System.out.println("Nome: " + volontario.getNome() + " " + volontario.getCognome());
            System.out.println("Email: " + volontario.getEmail());
            System.out.println("Tipi di Visite: " + volontario.getTipiDiVisite());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare le visite
    public void stampaVisite() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Nessuna visita disponibile.");
            return;
        }

        System.out.println("Visite:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.println("ID: " + entry.getKey());
            System.out.println("Luogo: " + visita.getLuogo());
            System.out.println("Tipo Visita: " + visita.getTipoVisita());
            System.out.println("Volontario: " + visita.getVolontario());
            System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
            System.out.println("-------------------------");
        }
    }

    // Metodo per visualizzare le visite per stato
    public void stampaVisitePerStato() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili.");
            return;
        }

        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        System.out.println("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }

        int sceltaStato = InputDati.leggiIntero("Seleziona lo stato da visualizzare: ", 1, stati.length) - 1;
        String statoScelto = stati[sceltaStato];

        System.out.printf("Visite in stato '%s':%n", statoScelto);
        for (Visite visita : visiteMap.values()) {
            if (visita.getStato().equalsIgnoreCase(statoScelto)) {
                System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                        visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                        visita.getData() != null ? visita.getData() : "Nessuna data");
            }
        }
    }

    // Metodo per visualizzare l'archivio storico delle visite
    public void stampaArchivioStorico() {
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili nell'archivio storico.");
            return;
        }

        System.out.println("Archivio storico delle visite effettuate:");
        for (Visite visita : visiteMap.values()) {
            if ("Effettuata".equalsIgnoreCase(visita.getStato())) {
                System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                        visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                        visita.getData() != null ? visita.getData() : "Nessuna data");
            }
        }
    }


    // Metodo per visualizzare le visite assegnate a un volontario
    public void stampaVisiteVolontario(Volontario volontario) {
        
        if (volontario == null) {
            System.out.println("Errore: nessun volontario fornito");
            return;
        }
        System.out.println("Visite assegnate a " + volontario.getNome() + " " + volontario.getCognome() + ":");
    
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
        boolean visiteTrovate = false;
    
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            if (visita.getVolontario().equals(volontario.getNome() + " " + volontario.getCognome())) {
                System.out.println("ID: " + entry.getKey());
                System.out.println("Luogo: " + visita.getLuogo());
                System.out.println("Tipo Visita: " + visita.getTipoVisita());
                System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
                System.out.println("Stato: " + visita.getStato());
                System.out.println("-------------------------");
                visiteTrovate = true;
            }
        }
    
        if (!visiteTrovate) {
            System.out.println("Nessuna visita assegnata al volontario.");
        }
    }


}
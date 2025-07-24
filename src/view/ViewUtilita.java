package src.view;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lib.InputDati;
import src.controller.VolontariController;
import src.model.Luogo;
import src.model.Visite;
import src.model.Volontario;
import src.controller.VisiteController;
import src.controller.LuoghiController;

public class ViewUtilita {

    private ConcurrentHashMap<Integer, Visite> visiteMap = VisiteController.getVisiteMap();

    private static ViewUtilita instance;

    private ViewUtilita() {}

    public static ViewUtilita getInstance() {
        if (instance == null) {
            instance = new ViewUtilita();
        }
        return instance;
    }

    // Metodo per stampare i luoghi
    public void stampaLuoghi(LuoghiController luoghiController) {
        List<Luogo> luoghi = luoghiController.getLuoghi();
        if (luoghi.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }

        System.out.println("Luoghi:");
        for (Luogo luogo : luoghi) {
            System.out.println(luogo.getNome());
        }
    }

    // Metodo per stampare i volontari
    public void stampaVolontari(VolontariController volontariController) {
        List<Volontario> volontari = volontariController.getVolontari();
        if (volontari.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }

        System.out.println("Volontari:");
        for (Volontario volontario : volontari) {
            System.out.println("Nome: " + volontario.getNome() + " " + volontario.getCognome());
            System.out.println("Email: " + volontario.getEmail());
            System.out.println("Tipi di Visite: " + volontario.getTipiDiVisite());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare le visite
    public void stampaVisite(VisiteController visiteController) {
        List<Visite> visite = visiteController.getVisite();
        if (visite.isEmpty()) {
            System.out.println("Nessuna visita disponibile.");
            return;
        }

        System.out.println("Visite:");
        for (Visite visita : visite) {
            System.out.println("Luogo: " + visita.getLuogo());
            System.out.println("Tipo Visita: " + visita.getTipoVisita());
            System.out.println("Volontario: " + visita.getVolontario());
            System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
            System.out.println("Stato: " + visita.getStato());
            System.out.println("-------------------------");
        }
    }

    // Metodo per visualizzare le visite per stato
    public void stampaVisitePerStato(VisiteController visiteController) {
        
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
    public void stampaArchivioStorico(VisiteController visiteController) {

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
        if (visiteMap.isEmpty()) {
            System.out.println("Nessuna visita disponibile.");
            return;
        }

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
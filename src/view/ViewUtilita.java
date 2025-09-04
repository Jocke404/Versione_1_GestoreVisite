package src.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lib.InputDati;
import src.controller.VolontariController;
import src.model.Luogo;
import src.model.ModificaUtilita;
import src.model.Visita;
import src.model.Volontario;
import src.model.db.VisiteManagerDB;
import src.controller.VisiteController;
import src.controller.LuoghiController;
import src.controller.ThreadPoolController;

public class ViewUtilita {

    private ConcurrentHashMap<Integer, Visita> visiteMap = new VisiteManagerDB(ThreadPoolController.getInstance()).getVisiteMap();
    private final ConsoleView consoleView = new ConsoleView();
    
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
        consoleView.mostraElencoConOggetti(luoghi);
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
        List<Visita> visite = visiteController.getVisite();
        if (visite.isEmpty()) {
            System.out.println("Nessuna visita disponibile.");
            return;
        }

        System.out.println("Visite:");
        for (Visita visita : visite) {
            System.out.println("Luogo: " + visita.getLuogo());
            System.out.println("Tipo Visita: " + visita.getTipoVisita());
            System.out.println("Volontario: " + visita.getVolontario());
            System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
            System.out.println("Stato: " + visita.getStato());
            System.out.println("-------------------------");
        }
    }

    // Metodo per visualizzare le visite per stato
    public void stampaVisitePerStato() {
        if(consoleView.chiediAnnullaOperazione())
            return; 
        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili.");
            return;
        }

        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata"};
        System.out.println("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }

        int sceltaStato = InputDati.leggiIntero("Seleziona lo stato da visualizzare: ", 1, stati.length) - 1;
        String statoScelto = stati[sceltaStato];

        System.out.printf("Visite in stato '%s':%n", statoScelto);
        for (Visita visita : visiteMap.values()) {
            if (visita.getStato().equalsIgnoreCase(statoScelto)) {
                System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s, Ora Inizio: %s, Durata: %d minuti%n",
                        visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                        visita.getData() != null ? visita.getData() : "Nessuna data",
                        visita.getOraInizio() != null ? visita.getOraInizio() : "Nessuna ora",
                        visita.getDurataMinuti());
            }
        }
    }

    // Metodo per visualizzare l'archivio storico delle visite
    public void stampaArchivioStorico(VisiteController visiteController) {
        ConcurrentHashMap<Integer, Visita> visiteMap = visiteController.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili nell'archivio storico.");
            return;
        }

        System.out.println("Archivio storico delle visite effettuate:");
        for (Visita visita : visiteMap.values()) {
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
    
        for (Map.Entry<Integer, Visita> entry : visiteMap.entrySet()) {
            Visita visita = entry.getValue();
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

    public void stampaDatePrecluse(VisiteController visiteController) {
        List<Map.Entry<LocalDate, String>> datePrecluseMap = visiteController.getDatePrecluse();
        if (datePrecluseMap.isEmpty()) {
            System.out.println("Nessuna data preclusa disponibile.");
            return;
        }

        System.out.println("Date Precluse:");
        for (Map.Entry<LocalDate, String> entry : datePrecluseMap) {
            System.out.printf("Data: %s, Motivo: %s%n", entry.getKey(), entry.getValue());
        }
    }

  // Metodo per visualizzare l'ambito territoriale
    public void stampaAmbitoTerritoriale(ModificaUtilita modificaUtilita) {
        Set<String> ambito = modificaUtilita.getAmbitoTerritoriale();
        if (ambito.isEmpty()) {
            System.out.println("Ambito territoriale non configurato.");
        } else {
            System.out.println("Ambito territoriale:");
            for (String comune : ambito) {
                System.out.println("- " + comune);
            }
        }
    }

    //Metodo per visualizzare i tipi di visita per luogo
    public void stampaTipiVisitaPerLuogo(VisiteController visiteController) {
        List<Visita> visite = visiteController.getVisite();
        if (visite.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }

        Map<String, List<Visita>> visitePerLuogo =new HashMap<>();
        for (Visita visita : visite) {
            String luogo = visita.getLuogo();
            visitePerLuogo.computeIfAbsent(luogo, k -> new ArrayList<>()).add(visita);
        }

        System.out.println ("Tipi di visita per luogo:");

        for (Map.Entry<String, List<Visita>> entry : visitePerLuogo.entrySet()) {
            String luogo = entry.getKey();
            List<Visita> visiteNelLuogo = entry.getValue();
            System.out.println("Luogo: " + luogo);
            System.out.println("Tipi di Visita:");
            
            Set<String> tipiVisitaUnici = new HashSet<>();
            for (Visita visita : visiteNelLuogo) {
                String tipoVisita = visita.getTipoVisitaString();
                tipiVisitaUnici.add(tipoVisita);
            }

            if (tipiVisitaUnici. isEmpty()) {
                System.out.println("  Nessun tipo di visita disponibile.");
            } else {
                int counter = 1;
                for (String tipo : tipiVisitaUnici) {
                    System.out.println("  " + counter + ". " + tipo);
                    counter++;
                }
            }

            System.out.println("-------------------------");
        }
    }

    
}
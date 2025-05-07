package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;

import it.unibs.mylib.InputDati;



public class AggiuntaUtilita {

    private final DatabaseUpdater databaseUpdater;

    public AggiuntaUtilita(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }
    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        ConcurrentHashMap<String, Luogo> luoghiMap = databaseUpdater.getLuoghiMap();
        ConcurrentHashMap<String, Volontario> volontariMap = databaseUpdater.getVolontariMap();
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
    
        if (luoghiMap.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }
    
        System.out.println("Elenco dei luoghi disponibili:");
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        for (int i = 0; i < luoghiNomi.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, luoghiNomi.get(i));
        }
    
        int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        String luogoNomeScelto = luoghiNomi.get(luogoIndex);
    
        String tipoVisitaScelto = InputDati.leggiStringaNonVuota("Inserisci il tipo di visita: ");
    
        if (volontariMap.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }
    
        System.out.println("\nElenco dei volontari disponibili:");
        List<Volontario> volontariNomi = new ArrayList<>(volontariMap.values());//TODO: controllare se è giusto
        for (int i = 0; i < volontariNomi.size(); i++) {
            System.out.printf("%d. %s %s%n", i + 1, volontariNomi.get(i).getNome(), volontariNomi.get(i).getCognome());
        }

        // Chiedi all'utente di selezionare un volontario
        int volontarioIndex = InputDati.leggiIntero("Seleziona il numero del volontario: ", 1, volontariNomi.size()) - 1;
        String volontarioNomeScelto = volontariNomi.get(volontarioIndex).getNome() + " " + volontariNomi.get(volontarioIndex).getCognome();
    
        LocalDate dataVisita;
        if (InputDati.yesOrNo("Vuoi inserire una data personale? ")) {
            int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
            int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
            int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
            dataVisita = LocalDate.of(anno, mese, giorno);
        } else {
            LocalDate oggi = LocalDate.now();
            YearMonth meseTarget = YearMonth.of(oggi.getYear(), oggi.getMonth().plus(3));
            List<LocalDate> dateValide = new ArrayList<>();
    
            for (int giorno = 1; giorno <= meseTarget.lengthOfMonth(); giorno++) {
                LocalDate data = meseTarget.atDay(giorno);
                if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    dateValide.add(data);
                }
            }
    
            System.out.println("\nDate disponibili per la visita:");
            for (int i = 0; i < dateValide.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, dateValide.get(i));
            }
    
            int dataIndex = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            dataVisita = dateValide.get(dataIndex);
        }
    
        int maxPersone = databaseUpdater.getMaxPersoneDefault();
        String stato = "Proposta"; // Stato iniziale della visita
    
        // Genera un ID univoco per la visita
        int id = visiteMap.size() + 1;
    
        // Crea l'oggetto Visite utilizzando il costruttore completo
        Visite nuovaVisita = new Visite(id, luogoNomeScelto, tipoVisitaScelto, volontarioNomeScelto, dataVisita, maxPersone, stato);
        visiteMap.put(id, nuovaVisita);

        databaseUpdater.aggiungiNuovaVisita(nuovaVisita);
    
        System.out.println("Visita assegnata con successo per la data " + dataVisita + "!");
    }

    // Metodo per aggiungere un volontario
    public void aggiungiVolontario() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String nomeUtente = email;
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        
        Volontario nuovoVolontario = new Volontario(nome, cognome, email, nomeUtente, password);
        // Aggiungi il volontario alla HashMap
        databaseUpdater.getVolontariMap().putIfAbsent(email, nuovoVolontario);

        // Sincronizza con il database
        databaseUpdater.aggiungiNuovoVolontario(nuovoVolontario);
    }

    // Metodo per aggiungere un luogo
    public void aggiungiLuogo() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");

        Luogo nuovoLuogo = new Luogo(nome, descrizione);
        databaseUpdater.getLuoghiMap().putIfAbsent(nome, nuovoLuogo);
        databaseUpdater.aggiungiNuovoLuogo(nuovoLuogo);  
        System.out.println("Luogo aggiunto: " + nuovoLuogo);
    }
}
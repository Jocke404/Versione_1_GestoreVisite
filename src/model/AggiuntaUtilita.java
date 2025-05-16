package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.DayOfWeek;

import lib.InputDati;
import src.view.ConsoleView;


public class AggiuntaUtilita {

    private final DatabaseUpdater databaseUpdater;
    private final ConsoleView consoleView = new ConsoleView();
    private final Map<String, List<String>> disponibilitaVolontari = new ConcurrentHashMap<>();

    public AggiuntaUtilita(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }
    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        ConcurrentHashMap<String, Luogo> luoghiMap = databaseUpdater.getLuoghiMap();
        ConcurrentHashMap<String, Volontario> volontariMap = databaseUpdater.getVolontariMap();
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();
    
        if (luoghiMap.isEmpty()) {
            consoleView.mostraMessaggio("aggiungi_visita.nessun_luogo_disponibile");
            return;
        }
    
        consoleView.mostraMessaggio("Elenco dei luoghi disponibili:");
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        for (int i = 0; i < luoghiNomi.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, luoghiNomi.get(i));
        }
    
        int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        String luogoNomeScelto = luoghiNomi.get(luogoIndex);
    
        String tipoVisitaScelto = InputDati.leggiStringaNonVuota("Inserisci il tipo di visita: ");
    
        if (volontariMap.isEmpty()) {
            consoleView.mostraMessaggio("Nessun volontario disponibile.");
            return;
        }
    
        consoleView.mostraMessaggio("\nElenco dei volontari disponibili:");
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
    
            consoleView.mostraMessaggio("\nDate disponibili per la visita:");
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
    
        consoleView.mostraMessaggio("Visita assegnata con successo per la data " + dataVisita + "!");
    }

    // Metodo per aggiungere un volontario
    public void aggiungiVolontario() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String nomeUtente = email;
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        List<String> tipiDiVisite = new ArrayList<>();
        // Chiedi all'utente di inserire i tipi di visita

        
        Volontario nuovoVolontario = new Volontario(nome, cognome, email, nomeUtente, tipiDiVisite);
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
        consoleView.mostraMessaggio("Luogo aggiunto: " + nuovoLuogo);
    }

    public void inserisciDisponibilitaVolontario(Volontario volontario) {
        LocalDate oggi = LocalDate.now();
        int giornoCorrente = oggi.getDayOfMonth();

        // Permetti solo tra il 1 e il 15 del mese
        //if (giornoCorrente < 1 || giornoCorrente > 15) {
        // System.out.println("Puoi inserire la disponibilità solo tra il 1 e il 15 del mese precedente a quello di interesse.");
            //return;
        //}

        LocalDate meseProssimo = oggi.plusMonths(1);
        YearMonth ym = YearMonth.of(meseProssimo.getYear(), meseProssimo.getMonthValue());
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        // Ottieni i tipi di visita associati al volontario
        List<String> tipiVisitaVolontario = volontario.getTipiDiVisite();

        System.out.println("Calendario del mese di " + meseProssimo.getMonth() + " " + meseProssimo.getYear() + ":");
        System.out.println("Giorno\tGiorno della settimana");

        List<Integer> giorniDisponibili = new ArrayList<>();
        for (int giorno = 1; giorno <= ym.lengthOfMonth(); giorno++) {
            LocalDate data = ym.atDay(giorno);
            // Giorno della settimana in italiano
            String giornoSettimana = data.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);

            boolean visitaProgrammato = visiteMap.values().stream()
                .anyMatch(v -> v.getData() != null && v.getData().equals(data));

            boolean tipoVisitaConsentito = tipiVisitaVolontario.stream()
                .anyMatch(tipo -> isTipoVisitaProgrammabileInGiorno(tipo, data.getDayOfWeek().toString()));

            boolean disponibile = !visitaProgrammato && tipoVisitaConsentito;

            if (disponibile) {
                System.out.printf("%02d\t%s%n", giorno, giornoSettimana);
                giorniDisponibili.add(giorno);
            }
        }

        if (giorniDisponibili.isEmpty()) {
            System.out.println("Non ci sono giorni disponibili per dichiarare la disponibilità.");
            return;
        }

        System.out.println("Inserisci le date (formato: DD) separate da virgola tra quelle disponibili:");
        String input = InputDati.leggiStringaNonVuota("Date: ");
        String[] dateArray = input.split(",");
        List<String> dateDisponibili = new ArrayList<>();
        for (String data : dateArray) {
            String trimmed = data.trim();
            try {
                int giorno = Integer.parseInt(trimmed);
                if (!giorniDisponibili.contains(giorno)) {
                    System.out.println("Il giorno " + trimmed + " non è disponibile.");
                    continue;
                }
                dateDisponibili.add(trimmed);
            } catch (NumberFormatException e) {
                System.out.println("Formato data non valido: " + trimmed);
            }
        }
        disponibilitaVolontari.put(volontario.getEmail(), dateDisponibili);
        System.out.println("Disponibilità salvata solo in memoria!");
        System.out.println("Date salvate per " + volontario.getEmail() + ": " + dateDisponibili);
    }

        // Funzione di esempio: da personalizzare in base alla tua logica di associazione tipo-visita/giorno
    private boolean isTipoVisitaProgrammabileInGiorno(String tipoVisita, String giornoSettimana) {
        // Esempio: tutte le visite sono programmabili tutti i giorni tranne DOMENICA
        return !giornoSettimana.equals("SUNDAY");
    }
}
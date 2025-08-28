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
import src.model.db.*;

import lib.InputDati;
import src.view.ConsoleView;


public class AggiuntaUtilita {

    // private final DatabaseUpdater databaseUpdater;
    private final VolontariManager volontariManager;
    private final LuoghiManager luoghiManager;
    private final VisiteManagerDB visiteManagerDB;
    ConcurrentHashMap<String, Luogo> luoghiMap;
    ConcurrentHashMap<String, Volontario> volontariMap;
    ConcurrentHashMap<String, TipiVisita> tipiVisitaMap;
    ConcurrentHashMap<Integer, Visite> visiteMap;
    ConcurrentHashMap<LocalDate, String> datePrecluseMap;

    private final ConsoleView consoleView = new ConsoleView();
    private final Map<String, List<String>> disponibilitaVolontari = new ConcurrentHashMap<>();

    public AggiuntaUtilita(VolontariManager volontariManager, LuoghiManager luoghiManager, VisiteManagerDB visiteManagerDB) {
        this.volontariManager = volontariManager;
        this.luoghiManager = luoghiManager;
        this.visiteManagerDB = visiteManagerDB;
        this.luoghiMap = luoghiManager.getLuoghiMap();
        this.volontariMap = volontariManager.getVolontariMap();
        this.visiteMap = visiteManagerDB.getVisiteMap();
    }

    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        if (consoleView.chiediAnnullaOperazione())
            return;
    
        if (luoghiMap.isEmpty()) {
            consoleView.mostraMessaggio("aggiungi_visita.nessun_luogo_disponibile");
            return;
        }
    
        consoleView.mostraMessaggio("Elenco dei luoghi disponibili:");
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        consoleView.mostraElenco(luoghiNomi);
        int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        String luogoNomeScelto = luoghiNomi.get(luogoIndex);

        List<TipiVisita> tipoVisita = new ArrayList<>(visiteMap.values().stream()
                .map(Visite::getTipoVisita)
                .distinct()
                .toList());
        consoleView.mostraElencoConOggetti(tipoVisita);
        int tipoVisitaIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita: ", 1, tipoVisita.size()) - 1;
        TipiVisita tipoVisitaScelto = tipoVisita.get(tipoVisitaIndex);

        if (volontariMap.isEmpty()) {
            consoleView.mostraMessaggio("Nessun volontario disponibile.");
            return;
        }
    
        consoleView.mostraMessaggio("\nElenco dei volontari disponibili:");
        List<Volontario> volontariNomi = new ArrayList<>(volontariMap.values());
        consoleView.mostraElencoConOggetti(volontariNomi);
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
            consoleView.mostraElencoConOggetti(dateValide);
            int dataIndex = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            dataVisita = dateValide.get(dataIndex);
        }
    
        int maxPersone = visiteManagerDB.getMaxPersone();
        String stato = "Proposta"; // Stato iniziale della visita
    
        // Genera un ID univoco per la visita
        int id = visiteMap.size() + 1;
    
        // Crea l'oggetto Visite utilizzando il costruttore completo
        Visite nuovaVisita = new Visite(id, luogoNomeScelto, tipoVisitaScelto, volontarioNomeScelto, dataVisita, maxPersone, stato);
        visiteMap.put(id, nuovaVisita);

        visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
    
        consoleView.mostraMessaggio("Visita assegnata con successo per la data " + dataVisita + "!");
    }

    // Metodo per aggiungere un volontario
    public void aggiungiVolontario() {
        if (consoleView.chiediAnnullaOperazione())
            return;
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        List<String> tipiDiVisite = new ArrayList<>();
        
        Volontario nuovoVolontario = new Volontario(nome, cognome, email, password, tipiDiVisite);
        
        volontariMap.putIfAbsent(email, nuovoVolontario);

        volontariManager.aggiungiNuovoVolontario(nuovoVolontario);
    }

    // Metodo per aggiungere un luogo
    public void aggiungiLuogo() {
        if (consoleView.chiediAnnullaOperazione())
            return;
        
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");

        Luogo nuovoLuogo = new Luogo(nome, descrizione);
        luoghiMap.putIfAbsent(nome, nuovoLuogo);
        luoghiManager.aggiungiNuovoLuogo(nuovoLuogo);  
        consoleView.mostraMessaggio("Luogo aggiunto: " + nuovoLuogo);
    }

    public void inserisciDisponibilitaVolontario(Volontario volontario) {
        if (volontario == null) {
            consoleView.mostraMessaggio("Errore: volontario non valido");
            return;
        }
        if (consoleView.chiediAnnullaOperazione()) {
            return;
        }
        LocalDate oggi = LocalDate.now();
        LocalDate meseProssimo = oggi.plusMonths(1);
        YearMonth ym = YearMonth.of(meseProssimo.getYear(), meseProssimo.getMonthValue());
        
        List<Integer> giorniDisponibili = trovaGiorniDisponibili(volontario, meseProssimo, ym);
        
        if (giorniDisponibili.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono giorni disponibili per dichiarare la disponibilità.");
            return;
        }

        List<String> dateDisponibili = raccogliDateDisponibili(giorniDisponibili);
        salvaDisponibilita(volontario, dateDisponibili);
    }

    private List<Integer> trovaGiorniDisponibili(Volontario volontario, LocalDate meseProssimo, YearMonth ym) {
        List<Integer> giorniDisponibili = new ArrayList<>();
        List<String> tipiVisitaVolontario = volontario.getTipiDiVisite();

        consoleView.mostraMessaggio("Calendario del mese di " + meseProssimo.getMonth() + " " + meseProssimo.getYear() + ":");
        consoleView.mostraMessaggio("Giorno\tGiorno della settimana");

        for (int giorno = 1; giorno <= ym.lengthOfMonth(); giorno++) {
            LocalDate data = ym.atDay(giorno);
            String giornoSettimana = data.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);

            if (isGiornoDisponibile(data, visiteMap, tipiVisitaVolontario)) {
                System.out.printf("%02d\t%s%n", giorno, giornoSettimana);
                giorniDisponibili.add(giorno);
            }
        }
        
        return giorniDisponibili;
    }

    private boolean isGiornoDisponibile(LocalDate data, ConcurrentHashMap<Integer, Visite> visiteMap, 
                                    List<String> tipiVisitaVolontario) {
        boolean visitaProgrammata = visiteMap.values().stream()
            .anyMatch(v -> v.getData() != null && v.getData().equals(data));

        boolean tipoVisitaConsentito = tipiVisitaVolontario.stream()
            .anyMatch(tipo -> isTipoVisitaProgrammabileInGiorno(tipo, data.getDayOfWeek().toString()));

        return !visitaProgrammata && tipoVisitaConsentito;
    }

    private List<String> raccogliDateDisponibili(List<Integer> giorniDisponibili) {
        consoleView.mostraMessaggio("Inserisci le date (formato: DD) separate da virgola tra quelle disponibili:");
        String input = InputDati.leggiStringaNonVuota("Date: ");
        String[] dateArray = input.split(",");
        List<String> dateDisponibili = new ArrayList<>();

        for (String data : dateArray) {
            String trimmed = data.trim();
            try {
                int giorno = Integer.parseInt(trimmed);
                if (!giorniDisponibili.contains(giorno)) {
                    consoleView.mostraMessaggio("Il giorno " + trimmed + " non è disponibile.");
                    continue;
                }
                dateDisponibili.add(trimmed);
            } catch (NumberFormatException e) {
                consoleView.mostraMessaggio("Formato data non valido: " + trimmed);
            }
        }
        return dateDisponibili;
    }

    private void salvaDisponibilita(Volontario volontario, List<String> dateDisponibili) {
        disponibilitaVolontari.put(volontario.getEmail(), dateDisponibili);
        consoleView.mostraMessaggio("Disponibilità salvata solo in memoria!");
        consoleView.mostraMessaggio("Date salvate per " + volontario.getEmail() + ": " + dateDisponibili);
    }

    private boolean isTipoVisitaProgrammabileInGiorno(String tipoVisita, String giornoSettimana) {
        String tipo = tipoVisita.trim().toLowerCase();
        String giorno = giornoSettimana.trim().toUpperCase();

        switch (tipo) {
            case "ENOGASTRONOMICA":
                // Venerdì, Sabato, Domenica
                return giorno.equals("FRIDAY") || giorno.equals("SATURDAY") || giorno.equals("SUNDAY");
            case "LABBAMBINI":
                // Lunedì, Martedì, Mercoledì, Giovedì
                return giorno.equals("MONDAY") || giorno.equals("TUESDAY") || giorno.equals("WEDNESDAY") || giorno.equals("THURSDAY");
            case "STORICA":
                // Martedì, Giovedì, Sabato
                return giorno.equals("TUESDAY") || giorno.equals("THURSDAY") || giorno.equals("SATURDAY");
            case "SCIENTIFICA":
                // Mercoledì, Venerdì, Domenica
                return giorno.equals("WEDNESDAY") || giorno.equals("FRIDAY") || giorno.equals("SUNDAY");
            default:
                // Se il tipo non è riconosciuto, non permette nessun giorno
                return false;
        }
    }

    public void aggiungiDatePrecluse() {
        boolean continua = true;
        do {
            if (consoleView.chiediAnnullaOperazione()){
                continua = false;
                break;
            }
            LocalDate data = InputDati.leggiData("Inserisci la data da aggiungere alle date precluse: ");
            if (data != null) {
                String motivo = InputDati.leggiStringa("Inserisci il motivo della preclusione per la data " + data + ": ");
                visiteManagerDB.aggiungiNuovaDataPreclusa(data, motivo);
            }
        } while (continua);
    }
}
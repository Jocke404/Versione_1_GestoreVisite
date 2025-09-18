package src.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import src.model.db.VisiteManagerDB;
import src.view.ConsoleView;

public class ValidatoreVisite {
    private VisiteManagerDB visiteManager;
    private ConcurrentHashMap<Integer, Visita> visiteMap = new ConcurrentHashMap<>();
    private ConsoleView consoleView = new ConsoleView();

    public ValidatoreVisite(VisiteManagerDB visiteManager) {
        this.visiteManager = visiteManager;
        this.visiteMap = visiteManager.getVisiteMap();
    }

    /**
     * Verifica se un volontario è già impegnato in un'altra visita nello stesso orario
     * @param volontarioEmail Email del volontario da verificare
     * @param dataVisita Data della nuova visita
     * @param oraInizio Ora di inizio della nuova visita
     * @param durataMinuti Durata in minuti della nuova visita
     * @return true se il volontario è disponibile, false se è già impegnato
     */
    public boolean isVolontarioDisponibile(String volontarioEmail, LocalDate dataVisita, 
                                         LocalTime oraInizio, int durataMinuti) {
        // Calcola ora di fine della nuova visita
        LocalTime oraFine = oraInizio.plusMinutes(durataMinuti);
        
        // Ottieni tutte le visite del volontario nella stessa data
        List<Visita> visiteVolontario = visiteManager.getVisiteMap().values().stream()
                .filter(v -> v.getVolontario() != null && 
                             v.getVolontario().contains(volontarioEmail) && // Cerca per email nel campo volontario
                             v.getData().equals(dataVisita) &&
                             !v.getStato().equals("Cancellata")) // Escludi visite cancellate
                .collect(Collectors.toList());
        
        // Verifica sovrapposizioni con visite esistenti
        for (Visita visitaEsistente : visiteVolontario) {
            if (visitaEsistente.getOraInizio() == null) continue;
            
            LocalTime inizioEsistente = visitaEsistente.getOraInizio();
            LocalTime fineEsistente = visitaEsistente.getOraInizio()
                                        .plusMinutes(visitaEsistente.getDurataMinuti());
            
            // Controlla sovrapposizione temporale
            if (siSovrappongono(oraInizio, oraFine, inizioEsistente, fineEsistente)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Verifica se due intervalli temporali si sovrappongono
     */
    private boolean siSovrappongono(LocalTime inizio1, LocalTime fine1, 
                                  LocalTime inizio2, LocalTime fine2) {
        return (inizio1.isBefore(fine2) && fine1.isAfter(inizio2));
    }

    /**
     * Metodo completo per validare l'assegnazione di un volontario a una visita
     * @param visita La visita da validare
     * @param volontarioEmail Email del volontario
     * @return Messaggio di validazione (vuoto se valido, messaggio errore altrimenti)
     */
    public String validaAssegnazioneVolontario(Visita visita, String volontarioEmail) {
        if (visita.getOraInizio() == null) {
            return "La visita deve avere un orario di inizio";
        }
        
        if (!isVolontarioDisponibile(volontarioEmail, visita.getData(), 
                                    visita.getOraInizio(), visita.getDurataMinuti())) {
            return "Il volontario è già impegnato in un'altra visita nello stesso orario";
        }
        
        // Altri controlli opzionali
        if (visita.getOraInizio().isBefore(LocalTime.of(9, 0))) {
            return "Orario troppo presto (minimo 09:00)";
        }
        
        LocalTime oraFine = visita.getOraInizio().plusMinutes(visita.getDurataMinuti());
        if (oraFine.isAfter(LocalTime.of(19, 0))) {
            return "Orario troppo tardo (massimo 19:00)";
        }
        
        return ""; // Validazione superata
    }


    public boolean validaVisita(Visita nuovaVisita){
        List<Visita> visiteEsistenti = visiteMap.values().stream()
                .filter(v -> v.getData().equals(nuovaVisita.getData()) && 
                             v.getLuogo().equals(nuovaVisita.getLuogo()))
                .collect(Collectors.toList());
        
        // Verifica sovrapposizione con ogni visita esistente
        for (Visita visitaEsistente : visiteEsistenti) {
            if (siSovrappongono(nuovaVisita.getOraInizio(), nuovaVisita.getOraInizio().plusMinutes(nuovaVisita.getDurataMinuti()),
                                visitaEsistente.getOraInizio(), visitaEsistente.getOraInizio().plusMinutes(visitaEsistente.getDurataMinuti()))) {
                return false;
            }
        }
        
        return true;
    }

    public List<LocalTime> trovaSlotDisponibili(LocalDate data, String luogo, int durataMinuti) {
        List<Visita> visiteGiorno = visiteMap.values().stream()
                .filter(v -> v.getData().equals(data) && v.getLuogo().equals(luogo))
                .collect(Collectors.toList());
        
        List<LocalTime> slotDisponibili = new ArrayList<>();
        final LocalTime INIZIO_GIORNATA = LocalTime.of(9, 0);
        final LocalTime FINE_GIORNATA = LocalTime.of(19, 0);
        final LocalTime ULTIMO_ORARIO_CONSENTITO = LocalTime.of(17, 40);
        
        // Verifica se la durata è compatibile con l'orario di chiusura
        if (INIZIO_GIORNATA.plusMinutes(durataMinuti).isAfter(FINE_GIORNATA)) {
            consoleView.mostraErrore("Durata troppo lunga: la visita non rientra nell'orario di apertura");
            return slotDisponibili; // Lista vuota
        }
        
        LocalTime slotCorrente = INIZIO_GIORNATA;
        
        while (slotCorrente.isBefore(ULTIMO_ORARIO_CONSENTITO)) {
            LocalTime fineVisita = slotCorrente.plusMinutes(durataMinuti);
            
            // Controllo 1: la visita deve finire entro le 19:00
            if (fineVisita.isAfter(FINE_GIORNATA)) {
                // Salta questo slot e passa al successivo
                slotCorrente = slotCorrente.plusMinutes(30);
                continue;
            }
            
            // Controllo 2: nessuna visita può iniziare dopo le 17:40
            if (slotCorrente.isAfter(ULTIMO_ORARIO_CONSENTITO)) {
                break;
            }
            
            boolean slotLibero = true;
            
            // Controllo 3: verifica sovrapposizione con visite esistenti
            Visita visitaTemp = new Visita(-1, luogo, List.of(), "", data, 0, "", slotCorrente, durataMinuti, 0);
            
            for (Visita visitaEsistente : visiteGiorno) {
                if (siSovrappongono(visitaTemp.getOraInizio(), visitaTemp.getOraInizio().plusMinutes(visitaTemp.getDurataMinuti()),
                                    visitaEsistente.getOraInizio(), visitaEsistente.getOraInizio().plusMinutes(visitaEsistente.getDurataMinuti()))) {
                    slotLibero = false;
                    break;
                }
            }
            
            if (slotLibero) {
                slotDisponibili.add(slotCorrente);
            }
            
            slotCorrente = slotCorrente.plusMinutes(30); // Check ogni 30 minuti
        }
        
        return slotDisponibili;
    }

    // public List<Integer> trovaGiorniDisponibili(Volontario volontario, LocalDate meseProssimo, YearMonth ym) {
    //     List<Integer> giorniDisponibili = new ArrayList<>();
    //     List<TipiVisita> tipiVisitaVolontario = volontario.getTipiDiVisite();

    //     consoleView.mostraMessaggio("Calendario del mese di " + meseProssimo.getMonth() + " " + meseProssimo.getYear() + ":");
    //     consoleView.mostraMessaggio("Giorno\tGiorno della settimana");

    //     for (int giorno = 1; giorno <= ym.lengthOfMonth(); giorno++) {
    //         LocalDate data = ym.atDay(giorno);
    //         String giornoSettimana = data.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);

    //         if (isGiornoDisponibile(data, visiteMap, tipiVisitaVolontario)) {
    //             System.out.printf("%02d\t%s%n", giorno, giornoSettimana);
    //             giorniDisponibili.add(giorno);
    //         }
    //     }
        
    //     return giorniDisponibili;
    // }
    public List<Integer> trovaGiorniDisponibili(Volontario volontario, YearMonth ym) {
        List<Integer> giorniDisponibili = new ArrayList<>();
        List<TipiVisita> tipiVisitaVolontario = volontario.getTipiDiVisite();

        for (int giorno = 1; giorno <= ym.lengthOfMonth(); giorno++) {
            LocalDate data = ym.atDay(giorno);
            if (isGiornoDisponibile(data, visiteMap, tipiVisitaVolontario)) {
                giorniDisponibili.add(giorno);
            }
        }
        return giorniDisponibili;
    }

    private boolean isGiornoDisponibile(LocalDate data, ConcurrentHashMap<Integer, Visita> visiteMap, 
                                    List<TipiVisita> tipiVisitaVolontario) {
        boolean visitaProgrammata = visiteMap.values().stream()
            .anyMatch(v -> v.getData() != null && v.getData().equals(data));

        boolean tipoVisitaConsentito = tipiVisitaVolontario.stream()
            .anyMatch(tipo -> isTipoVisitaProgrammabileInGiorno(tipo, data.getDayOfWeek().toString()));

        return !visitaProgrammata && tipoVisitaConsentito;
    }

    // public List<LocalDate> raccogliDateDisponibili(List<Integer> giorniDisponibili, YearMonth ym) {
    //     List<LocalDate> dateDisponibili = new ArrayList<>();

    //     consoleView.mostraMessaggio("Seleziona i giorni in cui sei disponibile per il mese di " + 
    //                             ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN) + " " + ym.getYear() + ":");
        
    //     boolean continua = true;
    //     do {
    //         consoleView.mostraMessaggio("Giorni disponibili: " + giorniDisponibili);
    //         int giorno = InputDati.leggiIntero("Inserisci il giorno da aggiungere (0 per terminare): ", 0, ym.lengthOfMonth());
            
    //         if (giorno == 0) {
    //             continua = false;
    //             break;
    //         }
            
    //         if (giorniDisponibili.contains(giorno)) {
    //             LocalDate data = ym.atDay(giorno);
    //             if (!dateDisponibili.contains(data)) {
    //                 dateDisponibili.add(data);
    //                 consoleView.mostraMessaggio("Data " + data + " aggiunta alle tue disponibilità.");
    //                 giorniDisponibili.remove(Integer.valueOf(giorno)); // Rimuovi dalla lista dei disponibili
    //             } else {
    //                 consoleView.mostraMessaggio("Hai già inserito questa data.");
    //             }
    //         } else {
    //             consoleView.mostraMessaggio("Giorno non disponibile o già selezionato. Scegli un giorno valido.");
    //         }
            
    //         if (giorniDisponibili.isEmpty()) {
    //             consoleView.mostraMessaggio("Hai selezionato tutti i giorni disponibili.");
    //             continua = false;
    //         }
    //     } while (continua);

    //     return dateDisponibili;
    // }
    public List<LocalDate> filtraDateDisponibili(List<Integer> giorniSelezionati, YearMonth ym) {
        List<LocalDate> dateDisponibili = new ArrayList<>();
        for (Integer giorno : giorniSelezionati) {
            dateDisponibili.add(ym.atDay(giorno));
        }
        return dateDisponibili;
    }

    private boolean isTipoVisitaProgrammabileInGiorno(TipiVisita tipoVisita, String giornoSettimana) {
        String tipo = tipoVisita.toString().trim().toLowerCase();
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

}

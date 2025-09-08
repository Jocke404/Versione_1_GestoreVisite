package src.model;

import java.time.LocalDate;
import java.time.LocalTime;
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
            Visita visitaTemp = new Visita(-1, luogo, List.of(), "", data, 0, "", slotCorrente, durataMinuti);
            
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

}

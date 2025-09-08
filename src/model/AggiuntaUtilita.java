package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.*;
import java.time.format.TextStyle;

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
    ConcurrentHashMap<String, TipiVisita> tipiVisitaMap = TipiVisita.getTipiVisitaMap();
    ConcurrentHashMap<Integer, Visita> visiteMap;
    ConcurrentHashMap<LocalDate, String> datePrecluseMap;
    private final List<Integer> durataList = List.of(30, 60, 90, 120);
    private final ValidatoreVisite validatoreVisite;

    private final ConsoleView consoleView = new ConsoleView();
    private final Map<String, List<LocalDate>> disponibilitaVolontari = new ConcurrentHashMap<>();

    public AggiuntaUtilita(VolontariManager volontariManager, LuoghiManager luoghiManager, VisiteManagerDB visiteManagerDB) {
        this.volontariManager = volontariManager;
        this.luoghiManager = luoghiManager;
        this.visiteManagerDB = visiteManagerDB;
        this.luoghiMap = luoghiManager.getLuoghiMap();
        this.volontariMap = volontariManager.getVolontariMap();
        this.visiteMap = visiteManagerDB.getVisiteMap();
        this.validatoreVisite = new ValidatoreVisite(visiteManagerDB);
    }

    public void aggiungiVisita() {
        if (consoleView.chiediAnnullaOperazione())
            return;

        if (InputDati.yesOrNo("Vuoi pianificare la visita usando le disponibilità dei volontari? (s/n)")) {
            pianificazioneGuidata();
        } else {
            pianificazioneLibera();
        }
    }

    // --- METODI DI SUPPORTO ---
    private void pianificazioneGuidata() {
        sincronizzaDisponibilitaVolontari();
        int durata = InputDati.leggiIntero("Durata in minuti: ", 30, 300);
        LocalTime orario = InputDati.leggiOra("Orario di inizio (HH:MM): ");
        List<String> volontariConDisp = getVolontariConDisponibilita();
        if (volontariConDisp.isEmpty()) {
            consoleView.mostraMessaggio("Nessun volontario ha inserito disponibilità.");
            return;
        }
        Volontario volontario = scegliVolontario(volontariConDisp);
        if (volontario == null) return;

        List<LocalDate> dateDisp = disponibilitaVolontari.get(volontario.getEmail());
        if (dateDisp.isEmpty()) return;

        LocalDate data = scegliDataDisponibile(dateDisp);
        if (data == null) return;

        TipiVisita tipoScelto = scegliTipoVisita(volontario.getTipiDiVisite());
        if (tipoScelto == null) return;

        String luogoScelto = scegliLuogoCompatibile(tipoScelto);
        if (luogoScelto == null) return;

        int nuovoId = visiteMap.size() + 1;
        Visita nuovaVisita = new Visita(nuovoId, luogoScelto, List.of(tipoScelto), volontario.getNome() + " " + volontario.getCognome(),
                                        data, visiteManagerDB.getMaxPersone(), "Proposta", orario, durata);
        visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        dateDisp.remove(String.valueOf(data.getDayOfMonth()));
        disponibilitaVolontari.put(volontario.getEmail(), dateDisp);
        salvaStatoERaccolta(disponibilitaVolontari, "RACCOLTA_APERTA");
        consoleView.mostraMessaggio("Nuova visita creata e assegnata!");
    }

    private void pianificazioneLibera() {
        String luogoNomeScelto = scegliLuogo();
        if (luogoNomeScelto == null) return;

        Luogo luogoSceltoObj = luoghiMap.get(luogoNomeScelto);
        List<TipiVisita> tipiVisita = luogoSceltoObj.getTipiVisita();
        if (tipiVisita == null || tipiVisita.isEmpty()) {
            consoleView.mostraMessaggio("Nessun tipo di visita disponibile per questo luogo.");
            return;
        }
        List<TipiVisita> tipiVisitaScelti = scegliTipiVisitaMultipli(tipiVisita);
        if (tipiVisitaScelti.isEmpty()) {
            consoleView.mostraMessaggio("Non hai selezionato alcun tipo di visita. La visita non verrà creata.");
            return;
        }

        Volontario volontario = scegliVolontario(new ArrayList<>(volontariMap.keySet()));
        if (volontario == null) return;
        String volontarioNomeScelto = volontario.getNome() + " " + volontario.getCognome();

        LocalDate dataVisita = scegliDataVisita();
        if (dataVisita == null) return;

        int id = visiteMap.size() + 1;
        int maxPersone = visiteManagerDB.getMaxPersone();
        String stato = "Proposta";
        LocalTime oraInizio = null;
        int durataMinuti = 0;
        Visita nuovaVisita = new Visita(id, luogoNomeScelto, tipiVisitaScelti, volontarioNomeScelto, dataVisita, maxPersone, stato, oraInizio, durataMinuti);

        if (InputDati.yesOrNo("Vuoi scegliere un orario specifico per la visita?")) {
            do {
                oraInizio = InputDati.leggiOra("Inserisci l'ora di inizio della visita (formato HH:MM): ");
                durataMinuti = scegliDurata();
                nuovaVisita.setOraInizio(oraInizio);
                nuovaVisita.setDurataMinuti(durataMinuti);
                if (validatoreVisite.validaVisita(nuovaVisita)) {
                    consoleView.mostraMessaggio("Visita valida.");
                } else {
                    consoleView.mostraMessaggio("Visita non valida per l'orario selezionato.");
                }
            } while (!validatoreVisite.validaVisita(nuovaVisita));
            visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        } else {
            durataMinuti = scegliDurata();
            List<LocalTime> slotDisponibili = validatoreVisite.trovaSlotDisponibili(dataVisita, luogoNomeScelto, durataMinuti);
            if (slotDisponibili.isEmpty()) {
                consoleView.mostraMessaggio("Nessuno slot disponibile per la data selezionata.");
                return;
            }
            consoleView.mostraMessaggio("\nSlot orari disponibili:");
            consoleView.mostraElencoConOggetti(slotDisponibili);
            int slotIndex = InputDati.leggiIntero("Seleziona il numero dello slot orario: ", 1, slotDisponibili.size()) - 1;
            oraInizio = slotDisponibili.get(slotIndex);
            nuovaVisita.setOraInizio(oraInizio);
            nuovaVisita.setDurataMinuti(durataMinuti);
            visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        }
        visiteMap.put(id, nuovaVisita);
        consoleView.mostraMessaggio("Visita assegnata con successo per la data " + dataVisita + "!");
    }

    // --- METODI DI SUPPORTO PURI ---

    private List<String> getVolontariConDisponibilita() {
        List<String> volontariConDisp = new ArrayList<>();

        for (Entry<String, List<LocalDate>> entry : disponibilitaVolontari.entrySet()) {
            if (!entry.getValue().isEmpty()) volontariConDisp.add(entry.getKey());
        }
        return volontariConDisp;
    }

    private Volontario scegliVolontario(List<String> emails) {
        if (emails.isEmpty()) return null;
        consoleView.mostraMessaggio("Volontari disponibili:");
        for (int i = 0; i < emails.size(); i++) {
            Volontario v = volontariMap.get(emails.get(i));
            consoleView.mostraMessaggio((i + 1) + ". " + v.getNome() + " " + v.getCognome() + " (" + emails.get(i) + ")");
        }
        int idxVol = InputDati.leggiIntero("Scegli il volontario (0 per uscire): ", 0, emails.size()) - 1;
        if (idxVol == -1) return null;
        return volontariMap.get(emails.get(idxVol));
    }

    private LocalDate scegliDataDisponibile(List<LocalDate> dateDisp) {
        if (dateDisp.isEmpty()) return null;
        consoleView.mostraMessaggio("Date disponibili:");
        consoleView.mostraElencoConOggetti(dateDisp);
        int idxData = InputDati.leggiIntero("Scegli la data (0 per uscire): ", 0, dateDisp.size()) - 1;
        if (idxData == -1) return null;
        LocalDate oggi = LocalDate.now();
        YearMonth ym = YearMonth.of(oggi.plusMonths(1).getYear(), oggi.plusMonths(1).getMonthValue());
        try {
            int giorno = dateDisp.get(idxData).getDayOfMonth();
            return ym.atDay(giorno);
        } catch (Exception e) {
            consoleView.mostraMessaggio("Formato data non valido: " + dateDisp.get(idxData));
            return null;
        }
    }

    private TipiVisita scegliTipoVisita(List<TipiVisita> tipi) {
        if (tipi == null || tipi.isEmpty()) return null;
        consoleView.mostraMessaggio("Tipi di visita disponibili:");
        for (int i = 0; i < tipi.size(); i++) {
            consoleView.mostraMessaggio((i + 1) + ". " + tipi.get(i));
        }
        int idxTipo = InputDati.leggiIntero("Scegli il tipo di visita (0 per uscire): ", 0, tipi.size()) - 1;
        if (idxTipo == -1) return null;
        return tipi.get(idxTipo);
    }

    private String scegliLuogoCompatibile(TipiVisita tipoScelto) {
        List<String> luoghiCompatibili = new ArrayList<>();
        for (Luogo luogo : luoghiMap.values()) {
            if (luogo.getTipiVisita() != null && luogo.getTipiVisita().stream().anyMatch(tv -> tv.equals(tipoScelto))) {
                luoghiCompatibili.add(luogo.getNome());
            }
        }
        if (luoghiCompatibili.isEmpty()) {
            consoleView.mostraMessaggio("Nessun luogo compatibile con il tipo di visita scelto.");
            return null;
        }
        consoleView.mostraMessaggio("Luoghi compatibili:");
        consoleView.mostraElenco(luoghiCompatibili);
        int idxLuogo = InputDati.leggiIntero("Scegli il luogo (0 per uscire): ", 0, luoghiCompatibili.size()) - 1;
        if (idxLuogo == -1) return null;
        return luoghiCompatibili.get(idxLuogo);
    }

    private String scegliLuogo() {
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        if (luoghiNomi.isEmpty()) return null;
        consoleView.mostraMessaggio("Elenco dei luoghi disponibili:");
        consoleView.mostraElenco(luoghiNomi);
        int idxLuogo = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        return luoghiNomi.get(idxLuogo);
    }

    private List<TipiVisita> scegliTipiVisitaMultipli(List<TipiVisita> tipiVisita) {
        List<TipiVisita> tipiVisitaDisponibili = new ArrayList<>(tipiVisita);
        List<TipiVisita> tipiVisitaScelti = new ArrayList<>();
        while (!tipiVisitaDisponibili.isEmpty()) {
            consoleView.mostraMessaggio("Tipi di visita disponibili:");
            consoleView.mostraElencoConOggetti(tipiVisitaDisponibili);
            int idxTipo = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere (0 per terminare): ", 0, tipiVisitaDisponibili.size());
            if (idxTipo == 0) break;
            TipiVisita tipoVisitaScelto = tipiVisitaDisponibili.get(idxTipo - 1);
            tipiVisitaScelti.add(tipoVisitaScelto);
            tipiVisitaDisponibili.remove(tipoVisitaScelto);
            if (!tipiVisitaDisponibili.isEmpty()) {
                if (!InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?")) {
                    break;
                }
            }
        }
        return tipiVisitaScelti;
    }

    private LocalDate scegliDataVisita() {
        if (InputDati.yesOrNo("Vuoi inserire una data personale? ")) {
            int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
            int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
            int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
            return LocalDate.of(anno, mese, giorno);
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
            int idxData = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            return dateValide.get(idxData);
        }
    }

    private int scegliDurata() {
        if (InputDati.yesOrNo("Vuoi inserire la durata della visita?")) {
            return InputDati.leggiIntero("Inserisci la durata della visita in minuti: ", 1, 480);
        } else {
            consoleView.mostraElencoConOggetti(durataList);
            int durataIndex = InputDati.leggiIntero("Seleziona il numero della durata della visita: ", 1, durataList.size()) - 1;
            return durataList.get(durataIndex);
        }
    }

    // public void aggiungiVisita() {
    //     if (consoleView.chiediAnnullaOperazione())
    //         return;

    //     if (InputDati.yesOrNo("Vuoi pianificare la visita usando le disponibilità dei volontari? (s/n)")) {
    //         // Pianificazione guidata
    //         sincronizzaDisponibilitaVolontari();
    //         List<String> volontariConDisp = new ArrayList<>();
    //         for (Map.Entry<String, List<String>> entry : disponibilitaVolontari.entrySet()) {
    //             if (!entry.getValue().isEmpty()) volontariConDisp.add(entry.getKey());
    //             }
    //         if (volontariConDisp.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun volontario ha inserito disponibilità.");
    //             return;
    //         }
    //         // Scegli volontario
    //         consoleView.mostraMessaggio("Volontari disponibili:");
    //             for (int i = 0; i < volontariConDisp.size(); i++) {
    //                 Volontario v = volontariMap.get(volontariConDisp.get(i));
    //                 consoleView.mostraMessaggio((i + 1) + ". " + v.getNome() + " " + v.getCognome() + " (" + volontariConDisp.get(i) + ")");
    //             }
    //         int idxVol = InputDati.leggiIntero("Scegli il volontario (0 per uscire): ", 0, volontariConDisp.size()) - 1;
    //         if (idxVol == -1) return;
    //         Volontario volontario = volontariMap.get(volontariConDisp.get(idxVol));
    //         List<String> dateDisp = disponibilitaVolontari.get(volontario.getEmail());
    //         if (dateDisp.isEmpty()) return;
    //         // Scegli data
    //         consoleView.mostraMessaggio("Date disponibili:");
    //         consoleView.mostraElenco(dateDisp);
    //         int idxData = InputDati.leggiIntero("Scegli la data (0 per uscire): ", 0, dateDisp.size()) - 1;
    //         if (idxData == -1) return;
    //         String dataStr = dateDisp.get(idxData);
    //         LocalDate oggi = LocalDate.now();
    //         YearMonth ym = YearMonth.of(oggi.plusMonths(1).getYear(), oggi.plusMonths(1).getMonthValue());
    //         LocalDate data;
    //         try {
    //             int giorno = Integer.parseInt(dataStr);
    //             data = ym.atDay(giorno);
    //         } catch (Exception e) {
    //             consoleView.mostraMessaggio("Formato data non valido: " + dataStr);
    //             return;
    //         }
    //         // Scegli tipo visita
    //         List<TipiVisita> tipi = volontario.getTipiDiVisite();
    //         if (tipi == null || tipi.isEmpty()) return;
    //         consoleView.mostraMessaggio("Tipi di visita disponibili:");
    //         for (int i = 0; i < tipi.size(); i++) {
    //             consoleView.mostraMessaggio((i + 1) + ". " + tipi.get(i));
    //         }
    //         int idxTipo = InputDati.leggiIntero("Scegli il tipo di visita (0 per uscire): ", 0, tipi.size()) - 1;
    //         if (idxTipo == -1) return;
    //         TipiVisita tipoScelto = tipi.get(idxTipo);
    //         // Scegli luogo compatibile
    //         List<String> luoghiCompatibili = new ArrayList<>();
    //         for (Luogo luogo : luoghiMap.values()) {
    //             if (luogo.getTipiVisita() != null && luogo.getTipiVisita().stream().anyMatch(tv -> tv.equals(tipoScelto))) {
    //                 luoghiCompatibili.add(luogo.getNome());
    //             }
    //         }
    //         if (luoghiCompatibili.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun luogo compatibile con il tipo di visita scelto.");
    //             return;
    //         }
    //         consoleView.mostraMessaggio("Luoghi compatibili:");
    //         consoleView.mostraElenco(luoghiCompatibili);
    //         int idxLuogo = InputDati.leggiIntero("Scegli il luogo (0 per uscire): ", 0, luoghiCompatibili.size()) - 1;
    //         if (idxLuogo == -1) return;
    //         String luogoScelto = luoghiCompatibili.get(idxLuogo);
    //         int durata = InputDati.leggiIntero("Durata in minuti: ", 30, 300);
    //         LocalTime orario = InputDati.leggiOra("Orario di inizio (HH:MM): ");
    //         int nuovoId = visiteMap.size() + 1;
    //         Visita nuovaVisita = new Visita(nuovoId, luogoScelto, List.of(tipoScelto), volontario.getNome() + " " + volontario.getCognome(), 
    //                                         data, visiteManagerDB.getMaxPersone(), "Proposta", orario, durata);
    //         visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
    //         dateDisp.remove(dataStr);
    //         disponibilitaVolontari.put(volontario.getEmail(), dateDisp);
    //         salvaStatoERaccolta(disponibilitaVolontari, "RACCOLTA_APERTA");
    //         consoleView.mostraMessaggio("Nuova visita creata e assegnata!");
    //     } else {
    //         // Pianificazione libera
    //         List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
    //         if (luoghiNomi.isEmpty()) return;
    //         consoleView.mostraMessaggio("Elenco dei luoghi disponibili:");
    //         consoleView.mostraElenco(luoghiNomi);
    //         int idxLuogo = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
    //         String luogoNomeScelto = luoghiNomi.get(idxLuogo);

    //         Luogo luogoSceltoObj = luoghiMap.get(luogoNomeScelto);
    //         List<TipiVisita> tipiVisita = luogoSceltoObj.getTipiVisita();
    //         if (tipiVisita == null || tipiVisita.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun tipo di visita disponibile per questo luogo.");
    //             return;
    //         }
    //         consoleView.mostraMessaggio("Tipi di visita disponibili:");
    //         consoleView.mostraElencoConOggetti(tipiVisita);
    //         List<TipiVisita> tipiVisitaScelti = new ArrayList<>();
    //         boolean aggiungiAltri = true;

    //         do {
    //             int idxTipo = InputDati.leggiIntero("Seleziona il numero del tipo di visita: ", 1, tipiVisita.size()) - 1;
    //             TipiVisita tipoVisitaScelto = tipiVisita.get(idxTipo);

    //             if (!tipiVisitaScelti.contains(tipoVisitaScelto)) {
    //                 tipiVisitaScelti.add(tipoVisitaScelto);
    //             } else {
    //                 consoleView.mostraMessaggio("Hai già selezionato questo tipo di visita.");
    //             }

    //             aggiungiAltri = InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?");
    //         } while (aggiungiAltri);

    //         consoleView.mostraMessaggio("Tipi di visita selezionati: " + tipiVisitaScelti);

    //         List<String> volontariEmails = new ArrayList<>(volontariMap.keySet());
    //         if (volontariEmails.isEmpty()) return;
    //         consoleView.mostraMessaggio("Volontari disponibili:");
    //         for (int i = 0; i < volontariEmails.size(); i++) {
    //             Volontario v = volontariMap.get(volontariEmails.get(i));
    //             consoleView.mostraMessaggio((i + 1) + ". " + v.getNome() + " " + v.getCognome() + " (" + volontariEmails.get(i) + ")");
    //         }
    //         int idxVol = InputDati.leggiIntero("Scegli il volontario (0 per uscire): ", 0, volontariEmails.size()) - 1;
    //         if (idxVol == -1) return;
    //         Volontario volontario = volontariMap.get(volontariEmails.get(idxVol));
    //         String volontarioNomeScelto = volontario.getNome() + " " + volontario.getCognome();

    //         LocalDate dataVisita;
    //         if (InputDati.yesOrNo("Vuoi inserire una data personale? ")) {
    //             int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
    //             int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
    //             int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
    //             dataVisita = LocalDate.of(anno, mese, giorno);
    //         } else {
    //             LocalDate oggi = LocalDate.now();
    //             YearMonth meseTarget = YearMonth.of(oggi.getYear(), oggi.getMonth().plus(3));
    //             List<LocalDate> dateValide = new ArrayList<>();
    //             for (int giorno = 1; giorno <= meseTarget.lengthOfMonth(); giorno++) {
    //                 LocalDate data = meseTarget.atDay(giorno);
    //                 if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
    //                     dateValide.add(data);
    //                 }
    //             }
    //             consoleView.mostraMessaggio("\nDate disponibili per la visita:");
    //             consoleView.mostraElencoConOggetti(dateValide);
    //             int idxData = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
    //             dataVisita = dateValide.get(idxData);
    //         }

    //         int id = visiteMap.size() + 1;
    //         int maxPersone = visiteManagerDB.getMaxPersone();
    //         String stato = "Proposta";
    //         LocalTime oraInizio = null;
    //         int durataMinuti = 0;
    //         Visita nuovaVisita = new Visita(id, luogoNomeScelto, tipiVisitaScelti, volontarioNomeScelto, dataVisita, maxPersone, stato, oraInizio, durataMinuti);

    //         if (InputDati.yesOrNo("Vuoi scegliere un orario specifico per la visita?")) {
    //             do {
    //                 oraInizio = InputDati.leggiOra("Inserisci l'ora di inizio della visita (formato HH:MM): ");
    //                 if (InputDati.yesOrNo("Vuoi inserire la durata della visita?")) {
    //                     durataMinuti = InputDati.leggiIntero("Inserisci la durata della visita in minuti: ", 1, 480);
    //                 } else {
    //                     consoleView.mostraElencoConOggetti(durataList);
    //                     int durataIndex = InputDati.leggiIntero("Seleziona il numero della durata della visita: ", 1, durataList.size()) - 1;
    //                     durataMinuti = durataList.get(durataIndex);
    //                 }
    //                 nuovaVisita.setOraInizio(oraInizio);
    //                 nuovaVisita.setDurataMinuti(durataMinuti);
    //                 if (visiteManagerDB.validaVisita(nuovaVisita)) {
    //                     consoleView.mostraMessaggio("Visita valida.");
    //                 } else {
    //                     consoleView.mostraMessaggio("Visita non valida per l'orario selezionato.");
    //                 }
    //             } while (!visiteManagerDB.validaVisita(nuovaVisita));
    //             visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
    //         } else {
    //             if (InputDati.yesOrNo("Vuoi inserire la durata della visita?")) {
    //                 durataMinuti = InputDati.leggiIntero("Inserisci la durata della visita in minuti: ", 1, 480);
    //             } else {
    //                 consoleView.mostraElencoConOggetti(durataList);
    //                 int durataIndex = InputDati.leggiIntero("Seleziona il numero della durata della visita: ", 1, durataList.size()) - 1;
    //                 durataMinuti = durataList.get(durataIndex);
    //             }
    //             List<LocalTime> slotDisponibili = visiteManagerDB.trovaSlotDisponibili(dataVisita, luogoNomeScelto, durataMinuti);
    //             if (slotDisponibili.isEmpty()) {
    //                 consoleView.mostraMessaggio("Nessuno slot disponibile per la data selezionata.");
    //                 return;
    //             }
    //             consoleView.mostraMessaggio("\nSlot orari disponibili:");
    //             consoleView.mostraElencoConOggetti(slotDisponibili);
    //             int slotIndex = InputDati.leggiIntero("Seleziona il numero dello slot orario: ", 1, slotDisponibili.size()) - 1;
    //             oraInizio = slotDisponibili.get(slotIndex);
    //             nuovaVisita.setOraInizio(oraInizio);
    //             nuovaVisita.setDurataMinuti(durataMinuti);
    //             visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
    //         }
    //         visiteMap.put(id, nuovaVisita);
    //         consoleView.mostraMessaggio("Visita assegnata con successo per la data " + dataVisita + "!");
    //     }
    // }

    // Metodo per aggiungere un volontario
    public void aggiungiVolontario() {
        if (consoleView.chiediAnnullaOperazione())
            return;
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        consoleView.mostraElencoConOggetti(tipiVisitaMap.values().stream().toList());
        List<TipiVisita> tipiVisitaSelezionati = new ArrayList<>();
        boolean aggiungiAltri = true;

        while (aggiungiAltri) {
            int tipoIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere: ", 1, tipiVisitaMap.size()) - 1;
            TipiVisita tipoSelezionato = tipiVisitaMap.values().stream().toList().get(tipoIndex);
            tipiVisitaSelezionati.add(tipoSelezionato);

            aggiungiAltri = InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?");
        }

        Volontario nuovoVolontario = new Volontario(nome, cognome, email, password, tipiVisitaSelezionati);

        volontariMap.putIfAbsent(email, nuovoVolontario);

        volontariManager.aggiungiNuovoVolontario(nuovoVolontario);
    }

    // Metodo per aggiungere un luogo
    public void aggiungiLuogo() {
        if (consoleView.chiediAnnullaOperazione())
            return;
        
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");
        String collocazione = InputDati.leggiStringaNonVuota("inserire la collocazione del luogo: ");
        consoleView.mostraElencoConOggetti(tipiVisitaMap.values().stream().toList());
        List<TipiVisita> tipiVisitaSelezionati = new ArrayList<>();
        boolean aggiungiAltri = true;

        while (aggiungiAltri) {
            int tipoIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere: ", 1, tipiVisitaMap.size()) - 1;
            TipiVisita tipoSelezionato = tipiVisitaMap.values().stream().toList().get(tipoIndex);
            tipiVisitaSelezionati.add(tipoSelezionato);

            aggiungiAltri = InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?");
        }

        Luogo nuovoLuogo = new Luogo(nome, descrizione, collocazione, tipiVisitaSelezionati);
        luoghiMap.putIfAbsent(nome, nuovoLuogo);
        consoleView.mostraMessaggio("Luogo già esistente.");
        luoghiManager.aggiungiNuovoLuogo(nuovoLuogo);
        consoleView.mostraMessaggio("Luogo aggiunto: " + nuovoLuogo);
    }

    public void inserisciDisponibilitaVolontario(Volontario volontario) {
        if (consoleView.chiediAnnullaOperazione()) {
            return;
        }
        
        // Controllo se siamo oltre il giorno 15 del mese corrente
        LocalDate oggi = LocalDate.now();
        if (oggi.getDayOfMonth() > 15) {
            consoleView.mostraMessaggio("La raccolta disponibilità è chiusa. Il termine era il giorno 15 del mese.");
            return;
        }
        
        LocalDate meseProssimo = oggi.plusMonths(1);
        YearMonth ym = YearMonth.of(meseProssimo.getYear(), meseProssimo.getMonthValue());
        
        List<Integer> giorniDisponibili = trovaGiorniDisponibili(volontario, meseProssimo, ym);
        
        if (giorniDisponibili.isEmpty()) {
            consoleView.mostraMessaggio("Non ci sono giorni disponibili per dichiarare la disponibilità.");
            return;
        }

        List<LocalDate> dateDisponibili = raccogliDateDisponibili(giorniDisponibili, ym);
        salvaDisponibilita(volontario, dateDisponibili);
    }

    // public void inserisciDisponibilitaVolontario(Volontario volontario) {
    //     if (consoleView.chiediAnnullaOperazione()) {
    //         return;
    //     }
    //     LocalDate oggi = LocalDate.now();
    //     LocalDate meseProssimo = oggi.plusMonths(1);
    //     YearMonth ym = YearMonth.of(meseProssimo.getYear(), meseProssimo.getMonthValue());
        
    //     List<Integer> giorniDisponibili = trovaGiorniDisponibili(volontario, meseProssimo, ym);
        
    //     if (giorniDisponibili.isEmpty()) {
    //         consoleView.mostraMessaggio("Non ci sono giorni disponibili per dichiarare la disponibilità.");
    //         return;
    //     }

    //     List<LocalDate> dateDisponibili = raccogliDateDisponibili(giorniDisponibili);
    //     salvaDisponibilita(volontario, dateDisponibili);
    // }

    private List<Integer> trovaGiorniDisponibili(Volontario volontario, LocalDate meseProssimo, YearMonth ym) {
        List<Integer> giorniDisponibili = new ArrayList<>();
        List<TipiVisita> tipiVisitaVolontario = volontario.getTipiDiVisite();

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

    private boolean isGiornoDisponibile(LocalDate data, ConcurrentHashMap<Integer, Visita> visiteMap, 
                                    List<TipiVisita> tipiVisitaVolontario) {
        boolean visitaProgrammata = visiteMap.values().stream()
            .anyMatch(v -> v.getData() != null && v.getData().equals(data));

        boolean tipoVisitaConsentito = tipiVisitaVolontario.stream()
            .anyMatch(tipo -> isTipoVisitaProgrammabileInGiorno(tipo, data.getDayOfWeek().toString()));

        return !visitaProgrammata && tipoVisitaConsentito;
    }

    private List<LocalDate> raccogliDateDisponibili(List<Integer> giorniDisponibili, YearMonth ym) {
        List<LocalDate> dateDisponibili = new ArrayList<>();

        consoleView.mostraMessaggio("Seleziona i giorni in cui sei disponibile per il mese di " + 
                                ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN) + " " + ym.getYear() + ":");
        
        boolean continua = true;
        do {
            consoleView.mostraMessaggio("Giorni disponibili: " + giorniDisponibili);
            int giorno = InputDati.leggiIntero("Inserisci il giorno da aggiungere (0 per terminare): ", 0, ym.lengthOfMonth());
            
            if (giorno == 0) {
                continua = false;
                break;
            }
            
            if (giorniDisponibili.contains(giorno)) {
                LocalDate data = ym.atDay(giorno);
                if (!dateDisponibili.contains(data)) {
                    dateDisponibili.add(data);
                    consoleView.mostraMessaggio("Data " + data + " aggiunta alle tue disponibilità.");
                    giorniDisponibili.remove(Integer.valueOf(giorno)); // Rimuovi dalla lista dei disponibili
                } else {
                    consoleView.mostraMessaggio("Hai già inserito questa data.");
                }
            } else {
                consoleView.mostraMessaggio("Giorno non disponibile o già selezionato. Scegli un giorno valido.");
            }
            
            if (giorniDisponibili.isEmpty()) {
                consoleView.mostraMessaggio("Hai selezionato tutti i giorni disponibili.");
                continua = false;
            }
        } while (continua);

        return dateDisponibili;
    }

    // private List<LocalDate> raccogliDateDisponibili(List<Integer> giorniDisponibili) {
    //     List<LocalDate> dateDisponibili = new ArrayList<>();
    //     LocalDate oggi = LocalDate.now();
    //     YearMonth ym = YearMonth.of(oggi.plusMonths(1).getYear(), oggi.plusMonths(1).getMonthValue());

    //     boolean continua = true;
    //     do {
    //         int giorno = InputDati.leggiIntero("Inserisci il giorno (1-" + ym.lengthOfMonth() + ") da aggiungere alle tue disponibilità (0 per terminare): ", 0, ym.lengthOfMonth());
    //         if (giorno == 0) {
    //             continua = false;
    //             break;
    //         }
    //         if (giorniDisponibili.contains(giorno)) {
    //             LocalDate data = ym.atDay(giorno);
    //             if (!dateDisponibili.contains(data)) {
    //                 dateDisponibili.add(data);
    //                 consoleView.mostraMessaggio("Data " + data + " aggiunta alle tue disponibilità.");
    //             } else {
    //                 consoleView.mostraMessaggio("Hai già inserito questa data.");
    //             }
    //         } else {
    //             consoleView.mostraMessaggio("Giorno non disponibile. Scegli un giorno valido.");
    //         }
    //     } while (continua);

    //     return dateDisponibili; 
    // }

    private void salvaDisponibilita(Volontario volontario, List<LocalDate> dateDisponibili) {
        disponibilitaVolontari.put(volontario.getEmail(), dateDisponibili);
        
        // Salva su file stato_raccolta.txt
        salvaStatoERaccolta(disponibilitaVolontari, "RACCOLTA_APERTA");
        
        consoleView.mostraMessaggio("Disponibilità salvata per " + volontario.getEmail() + ": " + dateDisponibili);
    }


    // private void salvaDisponibilita(Volontario volontario, List<LocalDate> dateDisponibili) {
    //     disponibilitaVolontari.put(volontario.getEmail(), dateDisponibili);
    //     // Salva anche su file stato_raccolta.txt (stato ciclo lasciato invariato)
    //     // Recupera il tipo di visita scelto (se uno solo, lo usa; se più di uno, chiede all'utente)
    //     List<TipiVisita> tipi = volontario.getTipiDiVisite();
    //     String tipoScelto = (tipi.size() == 1) ? tipi.get(0).toString() : "";
    //     if (tipi.size() > 1) {
    //         consoleView.mostraMessaggio("Tipi di visita disponibili:");
    //         for (int i = 0; i < tipi.size(); i++) {
    //             System.out.printf("%d. %s\n", i + 1, tipi.get(i));
    //         }
    //         int idx = InputDati.leggiIntero("Scegli il tipo di visita da associare alle date: ", 1, tipi.size()) - 1;
    //         tipoScelto = tipi.get(idx).toString();
    //     }
    //     // Salva nel file: email:tipo,data1,data2,...;
    //     Map<String, List<LocalDate>> mappaPerFile = new ConcurrentHashMap<>();
    //     for (Map.Entry<String, List<LocalDate>> entry : disponibilitaVolontari.entrySet()) {
    //         if (entry.getKey().equals(volontario.getEmail())) {
    //             List<LocalDate> lista = new ArrayList<>();
    //             lista.add(tipoScelto);
    //             for (LocalDate data : dateDisponibili) {
    //                 lista.add(data);
    //             }
    //             mappaPerFile.put(entry.getKey(), lista);
    //         } else {
    //             mappaPerFile.put(entry.getKey(), entry.getValue());
    //         }
    //     }
    //     salvaStatoERaccolta(mappaPerFile, "RACCOLTA_APERTA");
    //     consoleView.mostraMessaggio("Disponibilità salvata!");
    //     consoleView.mostraMessaggio("Date salvate per " + volontario.getEmail() + ": " + dateDisponibili);
    // }

    public void gestisciVolontariSenzaDisponibilita() {
        LocalDate oggi = LocalDate.now();
        
        // Se siamo dopo il giorno 15, segna tutti i volontari che non hanno inserito disponibilità
        if (oggi.getDayOfMonth() > 15) {
            for (Volontario volontario : volontariMap.values()) {
                if (!disponibilitaVolontari.containsKey(volontario.getEmail())) {
                    // Volontario senza disponibilità -> lista vuota
                    disponibilitaVolontari.put(volontario.getEmail(), new ArrayList<>());
                }
            }
            salvaStatoERaccolta(disponibilitaVolontari, "RACCOLTA_CHIUSA");
        }
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

    // Sincronizza la mappa delle disponibilità con il file stato_raccolta.txt
    private void sincronizzaDisponibilitaVolontari() {
        Map<String, List<LocalDate>> dalFile = leggiDisponibilitaDaFile();
        disponibilitaVolontari.clear();
        disponibilitaVolontari.putAll(dalFile);
    }

    // Salva lo stato raccolta e le disponibilità dei volontari su stato_raccolta.txt

    public static void salvaStatoERaccolta(Map<String, List<LocalDate>> disponibilita, String statoCiclo) {
        try {
            File file = new File("src/utility/stato_raccolta.txt");
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file, "UTF-8")) {
                // Scrivi lo stato del ciclo
                writer.println("stato_ciclo=" + statoCiclo);
                
                // Scrivi le disponibilità dei volontari
                writer.print("disponibilita_volontari=");
                boolean first = true;
                
                for (Map.Entry<String, List<LocalDate>> entry : disponibilita.entrySet()) {
                    if (!first) {
                        writer.print(";");
                    }
                    first = false;
                    
                    writer.print(entry.getKey() + ":");
                    
                    // Scrivi le date in formato yyyy-MM-dd
                    boolean firstDate = true;
                    for (LocalDate data : entry.getValue()) {
                        if (!firstDate) {
                            writer.print(",");
                        }
                        firstDate = false;
                        writer.print(data.toString());
                    }
                }
                writer.println();
            }
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio di stato_raccolta.txt: " + e.getMessage());
        }
    }

    public static Map<String, List<LocalDate>> leggiDisponibilitaDaFile() {
        Map<String, List<LocalDate>> disp = new ConcurrentHashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("src/utility/stato_raccolta.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("disponibilita_volontari=")) {
                    String data = line.substring("disponibilita_volontari=".length());
                    String[] volontariParts = data.split(";");
                    
                    for (String volontarioPart : volontariParts) {
                        if (volontarioPart.trim().isEmpty()) continue;
                        
                        String[] emailDateParts = volontarioPart.split(":");
                        if (emailDateParts.length == 2) {
                            String email = emailDateParts[0].trim();
                            List<LocalDate> date = new ArrayList<>();
                            
                            if (!emailDateParts[1].trim().isEmpty()) {
                                String[] dateParts = emailDateParts[1].split(",");
                                for (String dateStr : dateParts) {
                                    if (!dateStr.trim().isEmpty()) {
                                        date.add(LocalDate.parse(dateStr.trim()));
                                    }
                                }
                            }
                            
                            disp.put(email, date);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nella lettura di stato_raccolta.txt: " + e.getMessage());
        }
        
        return disp;
    }

}
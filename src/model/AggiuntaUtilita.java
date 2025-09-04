package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    ConcurrentHashMap<String, TipiVisita> tipiVisitaMap;
    ConcurrentHashMap<Integer, Visita> visiteMap;
    ConcurrentHashMap<LocalDate, String> datePrecluseMap;
    private final List<Integer> durataList = List.of(30, 60, 90, 120);

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
    
        consoleView.mostraMessaggio("Elenco dei luoghi disponibili:");
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        consoleView.mostraElenco(luoghiNomi);
        int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        String luogoNomeScelto = luoghiNomi.get(luogoIndex);

        List<TipiVisita> tipoVisita = new ArrayList<>(visiteMap.values().stream()
                .map(Visita::getTipoVisita)
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
        int id = visiteMap.size() + 1;
        int maxPersone = visiteManagerDB.getMaxPersone();
        String stato = "Proposta"; // Stato iniziale della visita
        LocalTime oraInizio = null;
        int durataMinuti = 0;
        Visita nuovaVisita = new Visita(id, luogoNomeScelto, tipoVisitaScelto, volontarioNomeScelto, dataVisita, maxPersone, stato, oraInizio, durataMinuti);
        
        if (InputDati.yesOrNo("Vuoi scegliere un orario specifico per la visita?")) {
            do{
                oraInizio = InputDati.leggiOra("Inserisci l'ora di inizio della visita (formato HH:MM): ");
                if(InputDati.yesOrNo("Vuoi inserire la durata della visita?")) {
                    durataMinuti = InputDati.leggiIntero("Inserisci la durata della visita in minuti: ", 1, 480);
                } else {
                    consoleView.mostraElencoConOggetti(durataList);
                    int durataIndex = InputDati.leggiIntero("Seleziona il numero della durata della visita: ", 1, durataList.size()) - 1;
                    durataMinuti = durataList.get(durataIndex);
                }   
                nuovaVisita.setOraInizio(oraInizio);
                nuovaVisita.setDurataMinuti(durataMinuti);
                if (visiteManagerDB.validaVisita(nuovaVisita)) {
                    consoleView.mostraMessaggio("Visita valida.");
                } else {
                    consoleView.mostraMessaggio("Visita non valida per l'orario selezionato.");
                }
            } while (!visiteManagerDB.validaVisita(nuovaVisita));
            visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        } else {
            durataMinuti = InputDati.leggiIntero("Inserisci la durata della visita in minuti: ", 1, 480);
            List<LocalTime> slotDisponibili = visiteManagerDB.trovaSlotDisponibili(dataVisita, luogoNomeScelto, durataMinuti);
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
        }

        
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

    private boolean isGiornoDisponibile(LocalDate data, ConcurrentHashMap<Integer, Visita> visiteMap, 
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
        // Salva anche su file stato_raccolta.txt (stato ciclo lasciato invariato)
        // Recupera il tipo di visita scelto (se uno solo, lo usa; se più di uno, chiede all'utente)
        List<String> tipi = volontario.getTipiDiVisite();
        String tipoScelto = (tipi.size() == 1) ? tipi.get(0) : "";
        if (tipi.size() > 1) {
            consoleView.mostraMessaggio("Tipi di visita disponibili:");
            for (int i = 0; i < tipi.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, tipi.get(i));
            }
            int idx = InputDati.leggiIntero("Scegli il tipo di visita da associare alle date: ", 1, tipi.size()) - 1;
            tipoScelto = tipi.get(idx);
        }
        // Salva nel file: email:tipo,data1,data2,...;
        Map<String, List<String>> mappaPerFile = new java.util.HashMap<>();
        for (Map.Entry<String, List<String>> entry : disponibilitaVolontari.entrySet()) {
            if (entry.getKey().equals(volontario.getEmail())) {
                List<String> lista = new ArrayList<>();
                lista.add(tipoScelto);
                lista.addAll(dateDisponibili);
                mappaPerFile.put(entry.getKey(), lista);
            } else {
                mappaPerFile.put(entry.getKey(), entry.getValue());
            }
        }
        salvaStatoERaccolta(mappaPerFile, "RACCOLTA_APERTA");
        consoleView.mostraMessaggio("Disponibilità salvata!");
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

    // Sincronizza la mappa delle disponibilità con il file stato_raccolta.txt
    public void sincronizzaDisponibilitaVolontari() {
        Map<String, List<String>> dalFile = leggiDisponibilitaDaFile();
        disponibilitaVolontari.clear();
        disponibilitaVolontari.putAll(dalFile);
    }

//Gia' presente in ViewUtilita
    // // Mostra le visite assegnate a un volontario
    // public void mostraVisiteAssegnateAlVolontario(Volontario volontario) {
    //     String nomeCompleto = volontario.getNome() + " " + volontario.getCognome();
    //     boolean trovata = false;
    //     for (Visita visita : visiteMap.values()) {
    //         if (visita.getVolontario().equalsIgnoreCase(nomeCompleto)) {
    //             if(LocalDate.now().isBefore(visita.getData())){
    //             consoleView.mostraMessaggio("Visita: " + visita.getTipoVisita() + ", Luogo: " + visita.getLuogo() + ", Data: " + visita.getData() + ", Stato: " + visita.getStato());
    //             trovata = true;
    //             }
    //         }
    //     }
    //     if (!trovata) {
    //         consoleView.mostraMessaggio("Nessuna visita assegnata al volontario.");
    //     }
    // }
    
    // // Produzione manuale del piano visite da parte del configuratore
    // public void produzionePianoVisiteManuale() {
    //     // Consenti la produzione solo se la raccolta è chiusa
    //     try {
    //         java.lang.reflect.Method getStatoCiclo = databaseUpdater.getClass().getMethod("getStatoCiclo");
    //         Object statoCiclo = getStatoCiclo.invoke(databaseUpdater);
    //         if (!"RACCOLTA_CHIUSA".equals(statoCiclo.toString())) {
    //             consoleView.mostraMessaggio("Devi prima chiudere la raccolta delle disponibilità per produrre il piano visite.");
    //             return;
    //         }
    //     } catch (Exception e) {
    //         // Se non esiste il metodo, non bloccare nulla
    //     }
    //     // --- SINCRONIZZAZIONE: carica le disponibilità dei volontari dal database/file ---
    //     sincronizzaDisponibilitaVolontari();

    //     // 1. Mostra solo volontari con disponibilità
    //     List<String> volontariConDisponibilita = new ArrayList<>();
    //     for (Map.Entry<String, List<String>> entry : disponibilitaVolontari.entrySet()) {
    //         // Filtra le date: solo stringhe che corrispondono a gg-mm-aaaa
    //         List<String> soloDate = new ArrayList<>();
    //         for (String d : entry.getValue()) {
    //             if (d.matches("\\d{2}-\\d{2}-\\d{4}")) {
    //                 soloDate.add(d);
    //             }
    //         }
    //         if (!soloDate.isEmpty()) {
    //             disponibilitaVolontari.put(entry.getKey(), soloDate);
    //             volontariConDisponibilita.add(entry.getKey());
    //         }
    //     }
    //     if (volontariConDisponibilita.isEmpty()) {
    //         consoleView.mostraMessaggio("Nessun volontario ha inserito disponibilità.");
    //         return;
    //     }
    //     while (!volontariConDisponibilita.isEmpty()) {
    //         consoleView.mostraMessaggio("Volontari con disponibilità:");
    //         for (int i = 0; i < volontariConDisponibilita.size(); i++) {
    //             String email = volontariConDisponibilita.get(i);
    //             Volontario v = volontariMap.get(email);
    //             System.out.printf("%d. %s %s (%s)\n", i + 1, v.getNome(), v.getCognome(), email);
    //         }
    //         int idxVol = InputDati.leggiIntero("Scegli il volontario da pianificare (0 per uscire): ", 0, volontariConDisponibilita.size()) - 1;
    //         if (idxVol == -1) return;
    //         String emailVol = volontariConDisponibilita.get(idxVol);
    //         Volontario volontario = volontariMap.get(emailVol);
    //         List<String> dateDisp = disponibilitaVolontari.get(emailVol);
    //         if (dateDisp.isEmpty()) {
    //             consoleView.mostraMessaggio("Questo volontario non ha date disponibili.");
    //             volontariConDisponibilita.remove(emailVol);
    //             continue;
    //         }
    //         List<String> tipiCompatibili = new ArrayList<>(volontario.getTipiDiVisite());
    //         if (tipiCompatibili == null || tipiCompatibili.isEmpty()) {
    //             consoleView.mostraMessaggio("Nessun tipo di visita compatibile per questo volontario.");
    //             volontariConDisponibilita.remove(emailVol);
    //             continue;
    //         }
    //         List<String> tipiVolontario = new ArrayList<>(tipiCompatibili);
    //         while (!tipiVolontario.isEmpty()) {
    //             consoleView.mostraMessaggio("Tipi di visita disponibili per il volontario:");
    //             for (int i = 0; i < tipiVolontario.size(); i++) {
    //                 System.out.printf("%d. %s\n", i + 1, tipiVolontario.get(i));
    //             }
    //             int idxTipo = InputDati.leggiIntero("Scegli il tipo di visita (0 per tornare indietro): ", 0, tipiVolontario.size()) - 1;
    //             if (idxTipo == -1) break;
    //             String tipoScelto = tipiVolontario.get(idxTipo);
    //             boolean pianificatoPerTipo = false;
    //             while (true) {
    //                 if (dateDisp.isEmpty()) {
    //                     consoleView.mostraMessaggio("Tutte le date di questo volontario sono state pianificate.");
    //                     break;
    //                 }
    //                 // Mostra solo le date numeriche (escludi eventuali stringhe che corrispondono al tipo di visita)
    //                 consoleView.mostraMessaggio("Date disponibili del volontario:");
    //                 consoleView.mostraElenco(dateDisp);
    //                 int inputDate = InputDati.leggiIntero("Seleziona la data da pianificare (numero, 0 per tornare indietro): ", 0, dateDisp.size()) - 1;
    //                 if (inputDate == -1) break;
    //                 String dataStr = dateDisp.get(inputDate);
    //                 LocalDate data;
    //                 try {
    //                     String[] parts = dataStr.contains("-") ? dataStr.split("-") : dataStr.split("/");
    //                     int giorno = Integer.parseInt(parts[0]);
    //                     int mese = Integer.parseInt(parts[1]);
    //                     int anno = (parts.length > 2) ? Integer.parseInt(parts[2]) : LocalDate.now().getYear();
    //                     data = LocalDate.of(anno, mese, giorno);
    //                 } catch (Exception e) {
    //                     consoleView.mostraMessaggio("Formato data non valido: " + dataStr);
    //                     continue;
    //                 }
    //                 List<String> luoghiCompatibili = new ArrayList<>();
    //                 for (Luogo luogo : luoghiMap.values()) {
    //                     if (luogo.getTipiVisita() != null && luogo.getTipiVisita().contains(tipoScelto)) {
    //                         luoghiCompatibili.add(luogo.getNome());
    //                     }
    //                 }
    //                 if (luoghiCompatibili.isEmpty()) {
    //                     consoleView.mostraMessaggio("Nessun luogo compatibile con il tipo di visita scelto.");
    //                     continue;
    //                 }
    //                 consoleView.mostraMessaggio("Luoghi compatibili per la data " + data + ":");
    //                 consoleView.mostraElenco(luoghiCompatibili);
    //                 int idxLuogo = InputDati.leggiIntero("Scegli il luogo per la data " + data + " (0 per annullare): ", 0, luoghiCompatibili.size()) - 1;
    //                 if (idxLuogo == -1) continue;
    //                 String luogoScelto = luoghiCompatibili.get(idxLuogo);
    //                 int durata = InputDati.leggiIntero("Inserisci la durata della visita (in minuti) per la data " + data + ": ", 30, 300);
    //                 int id = visiteMap.size() + 1;
    //                 Visita nuovaVisita = new Visita(id, luogoScelto, tipoScelto, volontario.getNome() + " " + volontario.getCognome(), data, visiteManagerDB.getMaxPersone(), "Proposta", null, durata);
    //                 visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
    //                 consoleView.mostraMessaggio("Visita assegnata e salvata per " + volontario.getNome() + " il " + data + "!");
    //                 dateDisp.remove(dataStr);
    //                 disponibilitaVolontari.put(emailVol, dateDisp);
    //                 salvaStatoERaccolta(disponibilitaVolontari, "RACCOLTA_APERTA");
    //                 pianificatoPerTipo = true;
    //                 // Dopo aver pianificato una visita, torna subito al menu delle date disponibili
    //             }
    //             if (pianificatoPerTipo) {
    //                 tipiVolontario.remove(tipoScelto);
    //             }
    //         }
    //         if (tipiVolontario.isEmpty() || dateDisp.isEmpty()) {
    //             volontariConDisponibilita.remove(emailVol);
    //         }
    //     }
    // // Messaggio finale senza riferimenti a mese/anno (che potrebbero essere errati)
    // consoleView.mostraMessaggio("Piano visite completato per tutti i volontari. Puoi ora riaprire la raccolta delle date.");
    // // Torna al menu principale del configuratore
    // return;
    // }

    private void assegnaNuovaVisita(Volontario volontario, List<String> dateDisponibili, List<String> tipiVolontario) {
        // Selezione tipo visita
        String tipoScelto = selezionaTipoVisita(tipiVolontario);
        if (tipoScelto == null) return;

        // Selezione data
        String dataScelta = selezionaData(dateDisponibili);
        if (dataScelta == null) return;

        LocalDate data = parseData(dataScelta);
        if (data == null) return;

        // Selezione luogo
        String luogoScelto = selezionaLuogo(tipoScelto);
        if (luogoScelto == null) return;

        // Selezione orario e durata
        LocalTime orario = selezionaOrario(data, luogoScelto);
        if (orario == null) return;

        int durata = InputDati.leggiIntero("Durata in minuti: ", 30, 300);

        // Creazione nuova visita
        int nuovoId = visiteMap.size() + 1;
        Visita nuovaVisita = new Visita(nuovoId, luogoScelto, tipoScelto, 
            volontario.getNome() + " " + volontario.getCognome(), data, 
            visiteManagerDB.getMaxPersone(), "Confermata", orario, durata);

        visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);

        // Aggiorna disponibilità
        dateDisponibili.remove(dataScelta);
        disponibilitaVolontari.put(volontario.getEmail(), dateDisponibili);
        salvaStatoERaccolta(disponibilitaVolontari, "RACCOLTA_APERTA");

        consoleView.mostraMessaggio("Nuova visita creata e assegnata!");
}

        // Salva lo stato raccolta e le disponibilità dei volontari su stato_raccolta.txt
    public static void salvaStatoERaccolta(Map<String, List<String>> disponibilita, String statoCiclo) {
        try {
            // Leggi tutte le righe esistenti
            List<String> lines = new ArrayList<>();
            File file = new File("src/utility/stato_raccolta.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("disponibilita_volontari=")) {
                            lines.add(line);
                        }
                    }
                }
            }
            // Ricostruisci la riga delle disponibilità
            StringBuilder disp = new StringBuilder();
            disp.append("disponibilita_volontari=");
            boolean first = true;
            for (Map.Entry<String, List<String>> entry : disponibilita.entrySet()) {
                if (!first) disp.append(";");
                first = false;
                disp.append(entry.getKey()).append(":").append(String.join(",", entry.getValue()));
            }
            disp.append(";");
            // Sovrascrivi il file con tutte le righe precedenti + la nuova riga delle disponibilità
            try (java.io.PrintWriter writer = new java.io.PrintWriter("src/utility/stato_raccolta.txt", "UTF-8")) {
                for (String l : lines) writer.println(l);
                writer.println(disp.toString());
            }
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio di stato_raccolta.txt: " + e.getMessage());
        }
    }

    // Legge le disponibilità dei volontari da stato_raccolta.txt
    public static Map<String, List<String>> leggiDisponibilitaDaFile() {
        Map<String, List<String>> disp = new ConcurrentHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/utility/stato_raccolta.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("disponibilita_volontari=")) {
                    String data = line.substring("disponibilita_volontari=".length());
                    String[] parts = data.split(";");
                    for (String part : parts) {
                        if (part.trim().isEmpty()) continue;
                        String[] kv = part.split(":");
                        if (kv.length == 2) {
                            String email = kv[0].trim();
                            List<String> giorni = new ArrayList<>();
                            for (String g : kv[1].split(",")) {
                                if (!g.trim().isEmpty()) giorni.add(g.trim());
                            }
                            disp.put(email, giorni);
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
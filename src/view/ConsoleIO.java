package src.view;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import lib.InputDati;
import src.model.AmbitoTerritoriale;
import src.model.CredentialManager;
import src.model.Luogo;
import src.model.Prenotazione;
import src.model.TipiVisita;
import src.model.Visita;
import src.model.Volontario;
import src.model.db.VisiteManagerDB;
import src.model.ValidatoreVisite;
import src.model.db.LuoghiManager;
import src.model.db.VolontariManager;

public class ConsoleIO implements View{

    private final Map<String, List<LocalDate>> disponibilitaVolontari = new ConcurrentHashMap<>();
    private final List<Integer> durataList = List.of(30, 60, 90, 120);

    public void mostraMessaggio(String messaggio) {
        System.out.println(messaggio);
    }

    public void mostraErrore(String errore) {
        System.err.println(errore);
    }

    public boolean chiediAnnullaOperazione() {
        return InputDati.yesOrNo("Vuoi annullare e tornare indietro? ");
    }

    public void mostraElenco(List<String> elementi) {
        for (int i = 0; i < elementi.size(); i++) {
            System.out.println("----------");
            System.out.printf("%d. %s%n", i + 1, elementi.get(i));
            System.out.println("----------\n");
        }
    }

    public void mostraElencoConOggetti(List<?> oggetti) {
        for (int i = 0; i < oggetti.size(); i++) {
            System.out.println("========================================");
            System.out.printf("%d.\n%s%n", i + 1, oggetti.get(i).toString());
            System.out.println("========================================\n");
        }
    }

        public void mostraVisiteDisponibili(List<Visita> visite) {
        for (Visita visita : visite) {
            mostraMessaggio(visita.toString());
        }
    }

    public String chiediEmail() {
        return InputDati.leggiStringaNonVuota("email: ");
    }

    public String chiediPassword() {
        return InputDati.leggiStringaNonVuota("password: ");
    }

    public String chiediNome() {
        return InputDati.leggiStringaNonVuota("Inserisci il nome: ");
    }

    public String chiediCognome() {
        return InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
    }

    public String chiediNuovaEmail(CredentialManager credentialManager) {
        String email;
        do {
            email = InputDati.leggiStringaNonVuota("Inserisci la nuova email: ");
            if (!isEmailValida(email)) {
                mostraMessaggio("Formato email non valido. Riprova.");
                continue;
            }
            if (credentialManager.isEmailPresente(email)) {
                mostraMessaggio("Questa email è già registrata. Inseriscine una diversa.");
                continue;
            }
            break;
        } while (true);
        return email;
    }

    private boolean isEmailValida(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean chiediConfermaEmail(String email) {
        return InputDati.yesOrNo("La tua email registrata è: " + email + ". Vuoi mantenerla?");
    }

    //VISITE---------------------------------------------------------------------------------------------------------------
    public int chiediSelezioneVisita(int max) {
        return InputDati.leggiIntero("Seleziona la visita da modificare: ", 1, max);
    }

    public String chiediNuovoStato(String[] stati) {
        mostraMessaggio("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }
        int scelta = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
        return stati[scelta];
    }

    // Selezione di un tipo di visita
    public TipiVisita chiediTipoVisita(List<TipiVisita> tipiVisitaList) {
        mostraMessaggio("Seleziona il tipo di visita:");
        mostraElencoConOggetti(tipiVisitaList);
        int tipoIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita: ", 1, tipiVisitaList.size()) - 1;
        return tipiVisitaList.get(tipoIndex);
    }

    // public Visita chiediDatiNuovaVisita(List<Luogo> luoghi, List<TipiVisita> tipiVisita, List<Volontario> volontari, int maxPersone) {
    //     // Chiedi luogo
    //     mostraMessaggio("Elenco dei luoghi disponibili:");
    //     mostraElencoConOggetti(luoghi);
    //     int idxLuogo = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghi.size()) - 1;
    //     Luogo luogoScelto = luoghi.get(idxLuogo);

    //     // Chiedi tipi visita
    //     mostraMessaggio("Tipi di visita disponibili:");
    //     mostraElencoConOggetti(tipiVisita);
    //     List<TipiVisita> tipiVisitaScelti = new ArrayList<>();
    //     do {
    //         int idxTipo = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere: ", 1, tipiVisita.size()) - 1;
    //         TipiVisita tipoScelto = tipiVisita.get(idxTipo);
    //         tipiVisitaScelti.add(tipoScelto);
    //     } while (InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?"));

    //     // Chiedi volontario
    //     mostraMessaggio("Volontari disponibili:");
    //     mostraElencoConOggetti(volontari);
    //     int idxVol = InputDati.leggiIntero("Seleziona il volontario: ", 1, volontari.size()) - 1;
    //     Volontario volontarioScelto = volontari.get(idxVol);

    //     // Chiedi data
    //     LocalDate dataVisita = InputDati.leggiData("Inserisci la data della visita (formato YYYY-MM-DD): ");

    //     // Chiedi orario e durata
    //     LocalTime oraInizio = InputDati.leggiOra("Inserisci l'ora di inizio della visita (formato HH:MM): ");
    //     int durataMinuti = InputDati.leggiIntero("Durata in minuti: ", 30, 300);

    //     int id = visiteMap.size() + 1;
    //     String stato = "Proposta";

    //     return new Visita(id, luogoScelto.getNome(), tipiVisitaScelti, volontarioScelto.getNome() + " " + volontarioScelto.getCognome(),
    //                     dataVisita, maxPersone, stato, oraInizio, durataMinuti, maxPersone);
    // }

    // --- METODI DI SUPPORTO ---
    public Visita pianificazioneGuidata(VisiteManagerDB visiteManagerDB, VolontariManager volontariManager, LuoghiManager luoghiManager) {

        ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();
        int durata = InputDati.leggiIntero("Durata in minuti: ", 30, 300);
        LocalTime orario = InputDati.leggiOra("Orario di inizio (HH:MM): ");
        List<String> volontariConDisp = getVolontariConDisponibilita();
        if (volontariConDisp.isEmpty()) {
            mostraMessaggio("Nessun volontario ha inserito disponibilità.");
            return null;
        }
        Volontario volontario = scegliVolontario(volontariConDisp, volontariManager);
        if (volontario == null) return null;

        List<LocalDate> dateDisp = disponibilitaVolontari.get(volontario.getEmail());
        if (dateDisp.isEmpty()) return null;

        LocalDate data = scegliDataDisponibile(dateDisp);
        if (data == null) return null;

        TipiVisita tipoScelto = scegliTipoVisita(volontario.getTipiDiVisite());
        if (tipoScelto == null) return null;

        String luogoScelto = scegliLuogoCompatibile(tipoScelto, luoghiManager);
        if (luogoScelto == null) return null;

        int nuovoId = visiteMap.size() + 1;
        Visita nuovaVisita = new Visita(nuovoId, luogoScelto, List.of(tipoScelto), volontario.getNome() + " " + volontario.getCognome(),
                                        data, visiteManagerDB.getMaxPersone(), "Proposta", orario, durata, visiteManagerDB.getMaxPersone());
        dateDisp.remove(String.valueOf(data.getDayOfMonth()));
        disponibilitaVolontari.put(volontario.getEmail(), dateDisp);
        return nuovaVisita;
    }

    public Visita pianificazioneLibera(VisiteManagerDB visiteManagerDB, VolontariManager volontariManager, LuoghiManager luoghiManager) {
        String luogoNomeScelto = scegliLuogo(luoghiManager);
        if (luogoNomeScelto == null) return null;
        ConcurrentHashMap<String, Luogo> luoghiMap = luoghiManager.getLuoghiMap();
        ConcurrentHashMap<String, Volontario> volontariMap = volontariManager.getVolontariMap();
        ConcurrentHashMap<Integer, Visita> visiteMap = visiteManagerDB.getVisiteMap();
        ValidatoreVisite validatoreVisite = new ValidatoreVisite(visiteManagerDB);
        Luogo luogoSceltoObj = luoghiMap.get(luogoNomeScelto);
        List<TipiVisita> tipiVisita = luogoSceltoObj.getTipiVisita();
        if (tipiVisita == null || tipiVisita.isEmpty()) {
            mostraMessaggio("Nessun tipo di visita disponibile per questo luogo.");
            return null;
        }
        List<TipiVisita> tipiVisitaScelti = scegliTipiVisitaMultipli(tipiVisita);
        if (tipiVisitaScelti.isEmpty()) {
            mostraMessaggio("Non hai selezionato alcun tipo di visita. La visita non verrà creata.");
            return null;
        }

        Volontario volontario = scegliVolontario(new ArrayList<>(volontariMap.keySet()), volontariManager);
        if (volontario == null) return null;
        String volontarioNomeScelto = volontario.getNome() + " " + volontario.getCognome();

        LocalDate dataVisita = scegliDataVisita();
        if (dataVisita == null) return null;

        int id = visiteMap.size() + 1;
        int maxPersone = visiteManagerDB.getMaxPersone();
        String stato = "Proposta";
        LocalTime oraInizio = null;
        int durataMinuti = 0;
        Visita nuovaVisita = new Visita(id, luogoNomeScelto, tipiVisitaScelti, volontarioNomeScelto, dataVisita, maxPersone, stato, oraInizio, durataMinuti, maxPersone);

        if (InputDati.yesOrNo("Vuoi scegliere un orario specifico per la visita?")) {
            do {
                oraInizio = InputDati.leggiOra("Inserisci l'ora di inizio della visita (formato HH:MM): ");
                durataMinuti = scegliDurata();
                nuovaVisita.setOraInizio(oraInizio);
                nuovaVisita.setDurataMinuti(durataMinuti);
                if (validatoreVisite.validaVisita(nuovaVisita)) {
                    mostraMessaggio("Visita valida.");
                } else {
                    mostraMessaggio("Visita non valida per l'orario selezionato.");
                }
            } while (!validatoreVisite.validaVisita(nuovaVisita));
            visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        } else {
            durataMinuti = scegliDurata();
            List<LocalTime> slotDisponibili = validatoreVisite.trovaSlotDisponibili(dataVisita, luogoNomeScelto, durataMinuti);
            if (slotDisponibili.isEmpty()) {
                mostraMessaggio("Nessuno slot disponibile per la data selezionata.");
                return null;
            }
            mostraMessaggio("\nSlot orari disponibili:");
            mostraElencoConOggetti(slotDisponibili);
            int slotIndex = InputDati.leggiIntero("Seleziona il numero dello slot orario: ", 1, slotDisponibili.size()) - 1;
            oraInizio = slotDisponibili.get(slotIndex);
            nuovaVisita.setOraInizio(oraInizio);
            nuovaVisita.setDurataMinuti(durataMinuti);
        }
        visiteMap.put(id, nuovaVisita);
        return nuovaVisita;
    }

    // --- METODI DI SUPPORTO PURI ---
    private List<String> getVolontariConDisponibilita() {
        List<String> volontariConDisp = new ArrayList<>();

        for (Entry<String, List<LocalDate>> entry : disponibilitaVolontari.entrySet()) {
            if (!entry.getValue().isEmpty()) volontariConDisp.add(entry.getKey());
        }
        return volontariConDisp;
    }

    private Volontario scegliVolontario(List<String> emails, VolontariManager volontariManager) {
        if (emails.isEmpty()) return null;
        mostraMessaggio("Volontari disponibili:");
        for (int i = 0; i < emails.size(); i++) {
            Volontario v = volontariManager.getVolontariMap().get(emails.get(i));
            mostraMessaggio((i + 1) + ". " + v.getNome() + " " + v.getCognome() + " (" + emails.get(i) + ")");
        }
        int idxVol = InputDati.leggiIntero("Scegli il volontario (0 per uscire): ", 0, emails.size()) - 1;
        if (idxVol == -1) return null;
        return volontariManager.getVolontariMap().get(emails.get(idxVol));
    }

    private LocalDate scegliDataDisponibile(List<LocalDate> dateDisp) {
        if (dateDisp.isEmpty()) return null;
        mostraMessaggio("Date disponibili:");
        mostraElencoConOggetti(dateDisp);
        int idxData = InputDati.leggiIntero("Scegli la data (0 per uscire): ", 0, dateDisp.size()) - 1;
        if (idxData == -1) return null;
        LocalDate oggi = LocalDate.now();
        YearMonth ym = YearMonth.of(oggi.plusMonths(1).getYear(), oggi.plusMonths(1).getMonthValue());
        try {
            int giorno = dateDisp.get(idxData).getDayOfMonth();
            return ym.atDay(giorno);
        } catch (Exception e) {
            mostraMessaggio("Formato data non valido: " + dateDisp.get(idxData));
            return null;
        }
    }

    private TipiVisita scegliTipoVisita(List<TipiVisita> tipi) {
        if (tipi == null || tipi.isEmpty()) return null;
        mostraMessaggio("Tipi di visita disponibili:");
        for (int i = 0; i < tipi.size(); i++) {
            mostraMessaggio((i + 1) + ". " + tipi.get(i));
        }
        int idxTipo = InputDati.leggiIntero("Scegli il tipo di visita (0 per uscire): ", 0, tipi.size()) - 1;
        if (idxTipo == -1) return null;
        return tipi.get(idxTipo);
    }

    private String scegliLuogoCompatibile(TipiVisita tipoScelto, LuoghiManager luoghiManager) {
        List<String> luoghiCompatibili = new ArrayList<>();
        for (Luogo luogo : luoghiManager.getLuoghiMap().values()) {
            if (luogo.getTipiVisita() != null && luogo.getTipiVisita().stream().anyMatch(tv -> tv.equals(tipoScelto))) {
                luoghiCompatibili.add(luogo.getNome());
            }
        }
        if (luoghiCompatibili.isEmpty()) {
            mostraMessaggio("Nessun luogo compatibile con il tipo di visita scelto.");
            return null;
        }
        mostraMessaggio("Luoghi compatibili:");
        mostraElenco(luoghiCompatibili);
        int idxLuogo = InputDati.leggiIntero("Scegli il luogo (0 per uscire): ", 0, luoghiCompatibili.size()) - 1;
        if (idxLuogo == -1) return null;
        return luoghiCompatibili.get(idxLuogo);
    }

    private String scegliLuogo(LuoghiManager luoghiManager) {
        List<String> luoghiNomi = new ArrayList<>(luoghiManager.getLuoghiMap().keySet());
        if (luoghiNomi.isEmpty()) return null;
        mostraMessaggio("Elenco dei luoghi disponibili:");
        mostraElenco(luoghiNomi);
        int idxLuogo = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        return luoghiNomi.get(idxLuogo);
    }

    private List<TipiVisita> scegliTipiVisitaMultipli(List<TipiVisita> tipiVisita) {
        List<TipiVisita> tipiVisitaDisponibili = new ArrayList<>(tipiVisita);
        List<TipiVisita> tipiVisitaScelti = new ArrayList<>();
        while (!tipiVisitaDisponibili.isEmpty()) {
            mostraMessaggio("Tipi di visita disponibili:");
            mostraElencoConOggetti(tipiVisitaDisponibili);
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
            mostraMessaggio("\nDate disponibili per la visita:");
            mostraElencoConOggetti(dateValide);
            int idxData = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            return dateValide.get(idxData);
        }
    }

    private int scegliDurata() {
        if (InputDati.yesOrNo("Vuoi inserire la durata della visita?")) {
            return InputDati.leggiIntero("Inserisci la durata della visita in minuti: ", 1, 480);
        } else {
            mostraElencoConOggetti(durataList);
            int durataIndex = InputDati.leggiIntero("Seleziona il numero della durata della visita: ", 1, durataList.size()) - 1;
            return durataList.get(durataIndex);
        }
    }

    public boolean chiediConfermaModifica(String statoOriginale, String nuovoStato) {
        mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
        mostraMessaggio("Stato: " + statoOriginale + " -> " + nuovoStato);
        return InputDati.yesOrNo("Vuoi confermare e salvare la modifica dello stato?");
    }

    public int chiediSelezioneVisita(List<Visita> visite) {
        mostraMessaggio("Visite disponibili:");
        mostraElencoConOggetti(visite);
        return InputDati.leggiIntero("Seleziona la visita da eliminare: ", 1, visite.size()) - 1;
    }

    public boolean chiediConfermaEliminazioneVisita(Visita visita) {
        return InputDati.yesOrNo("Sei sicuro di voler eliminare la visita con ID: " + visita.getId() + "? Questa azione non può essere annullata.");
    }

    public LocalDate chiediNuovaDataVisita(LocalDate dataOriginale) {
        int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ", LocalDate.now().getYear(), 2100);
        int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ", 1, 12);
        int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ", 1, LocalDate.of(anno, mese, 1).lengthOfMonth());
        return LocalDate.of(anno, mese, giorno);
    }

    public boolean chiediConfermaModificaData(LocalDate dataOriginale, LocalDate nuovaData) {
        mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
        mostraMessaggio("Data: " + (dataOriginale != null ? dataOriginale : "Nessuna data") + " -> " + nuovaData);
        return InputDati.yesOrNo("Vuoi confermare e salvare la modifica della data?");
    }

    public void mostraRisultatoModificaData(boolean successo) {
        if (successo) {
            mostraMessaggio("Data della visita aggiornata con successo.");
        } else {
            mostraMessaggio("Modifica annullata. Nessun cambiamento effettuato.");
        }
    }

    //MAX PERSONE---------------------------------------------------------------------------------------------------------------
    public int chiediNumeroMaxPersone() {
        return InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
    }

    public boolean chiediConfermaNumeroMax(int numeroMax) {
        return InputDati.yesOrNo("Sei sicuro di voler impostare il numero massimo di persone per visita a " + numeroMax + "?");
    }

    public void mostraRisultatoAggiornamentoMaxPersone(boolean successo, int numeroMax) {
        if (successo) {
            mostraMessaggio("Numero massimo di persone per visita aggiornato a: " + numeroMax);
        } else {
            mostraMessaggio("Errore nel salvataggio del numero massimo di persone iscrivibili.");
        }
    }

    //DATE PRECLUSE---------------------------------------------------------------------------------------------------------------
    public int chiediDataPreclusaDaEliminare(ConcurrentHashMap<LocalDate, String> datePrecluse) {
        int i = 1;
        mostraMessaggio("Date precluse disponibili:");
        for (Map.Entry<LocalDate, String> entry : datePrecluse.entrySet()) {
            System.out.printf("%d\tData: %s ---> Motivo: %s%n", i++, entry.getKey(), entry.getValue());
        }
        return InputDati.leggiIntero("Seleziona la data preclusa da eliminare: ", 1, datePrecluse.size()) - 1;
    }

    public boolean chiediConfermaEliminazioneData(LocalDate data) {
        return InputDati.yesOrNo("Sei sicuro di voler eliminare la data preclusa: " + data + "?");
    }

    public void mostraRisultatoEliminazioneData(boolean successo) {
        if (successo) {
            mostraMessaggio("Data preclusa eliminata con successo.");
        } else {
            mostraMessaggio("Operazione annullata o errore.");
        }
    }

    public LocalDate chiediDataPreclusa() {
        return InputDati.leggiData("Inserisci la data da aggiungere alle date precluse: ");
    }

    public String chiediMotivoPreclusione(LocalDate data) {
        return InputDati.leggiStringa("Inserisci il motivo della preclusione per la data " + data + ": ");
    }

    //LUOGHI---------------------------------------------------------------------------------------------------------------
    public int chiediSelezioneLuogo(List<Luogo> luoghi) {
        mostraMessaggio("Luoghi disponibili:");
        mostraElencoConOggetti(luoghi);
        return InputDati.leggiIntero("Seleziona il luogo: ", 1, luoghi.size()) - 1;
    }

    public Luogo chiediDatiNuovoLuogo(AmbitoTerritoriale ambitoTerritoriale) {
        String nome = InputDati.leggiStringaNonVuota("Inserisci il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("Inserisci la descrizione del luogo: ");
        mostraElencoConOggetti(ambitoTerritoriale.getAmbitoTerritoriale());
        int luogoIndex = InputDati.leggiIntero("inserire la collocazione del luogo: ", 1, ambitoTerritoriale.getAmbitoTerritoriale().size()) - 1;
        String collocazione = ambitoTerritoriale.getAmbitoTerritoriale().get(luogoIndex);
        List<TipiVisita> tipiVisita = chiediNuoviTipiVisita(new ArrayList<>());
        return new Luogo(nome, descrizione, collocazione, tipiVisita);
    }

    public boolean chiediConfermaEliminazioneLuogo(Luogo luogo) {
        return InputDati.yesOrNo("Sei sicuro di voler eliminare il luogo: " + luogo.getNome() + "? QUESTA AZIONE ELIMINERA' ANCHE LE VISITE AD ESSO COLLEGATE");
    }

    public String chiediNuovoNomeLuogo(String nomeAttuale) {
        return InputDati.leggiStringa("Inserisci il nuovo nome del luogo (lascia vuoto per mantenere il valore attuale: " + nomeAttuale + "): ");
    }

    public String chiediNuovaDescrizioneLuogo(String descrizioneAttuale) {
        return InputDati.leggiStringa("Inserisci la nuova descrizione del luogo (lascia vuoto per mantenere il valore attuale): " + descrizioneAttuale + "): ");
    }

    public String chiediNuovaCollocazioneLuogo(String collocazioneAttuale) {
        return InputDati.leggiStringa("Inserisci la nuova collocazione del luogo (lascia vuoto per mantenere il valore attuale): " + collocazioneAttuale + "): ");
    }

    public List<TipiVisita> chiediNuoviTipiVisita(List<TipiVisita> tipiAttuali) {
        
        List<TipiVisita> nuoviTipi = new ArrayList<>(tipiAttuali);
        // FASE 1: Eliminazione tipi di visita
        if (!nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare uno o più tipi di visita attuali?")) {
            boolean eliminaAltro;
            do {
                mostraMessaggio("Tipi di visita attuali:");
                mostraElencoConOggetti(nuoviTipi);
                int sceltaElimina = InputDati.leggiIntero("Seleziona il numero del tipo di visita da eliminare (oppure 0 per terminare): ", 0, nuoviTipi.size());
                if (sceltaElimina == 0) {
                    break;
                }
                TipiVisita tipoDaEliminare = nuoviTipi.get(sceltaElimina - 1);
                nuoviTipi.remove(tipoDaEliminare);
                eliminaAltro = !nuoviTipi.isEmpty() && InputDati.yesOrNo("Vuoi eliminare un altro tipo di visita?");
            } while (eliminaAltro);
        }

        // FASE 2: Aggiunta tipi di visita
        List<TipiVisita> tipiDisponibili = new ArrayList<>(List.of(TipiVisita.values()));
        tipiDisponibili.removeAll(nuoviTipi);

        if (!tipiDisponibili.isEmpty() && InputDati.yesOrNo("Vuoi aggiungere nuovi tipi di visita?")) {
            while (!tipiDisponibili.isEmpty()) {
                mostraMessaggio("Tipi di visita che puoi ancora aggiungere:");
                mostraElencoConOggetti(tipiDisponibili);

                int sceltaTipi = InputDati.leggiIntero("Seleziona il numero del tipo di visita da aggiungere (oppure 0 per terminare): ", 0, tipiDisponibili.size());
                if (sceltaTipi == 0) {
                    break;
                }
                TipiVisita tipoScelto = tipiDisponibili.get(sceltaTipi - 1);
                nuoviTipi.add(tipoScelto);
                tipiDisponibili.remove(tipoScelto);

                if (!tipiDisponibili.isEmpty()) {
                    if (!InputDati.yesOrNo("Vuoi aggiungere un altro tipo di visita?")) {
                        break;
                    }
                }
            }
        }
        return tipiAttuali; // Placeholder
    }

    public void mostraConfrontoLuogo(Luogo luogo, String nuovoNome, String nuovaDescrizione, String nuovaCollocazione, List<TipiVisita> nuoviTipi) {
        mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
        mostraMessaggio("Nome: " + luogo.getNome() + " -> " + (nuovoNome.isEmpty() ? luogo.getNome() : nuovoNome));
        mostraMessaggio("Descrizione: " + luogo.getDescrizione() + " -> " + (nuovaDescrizione.isEmpty() ? luogo.getDescrizione() : nuovaDescrizione));
        mostraMessaggio("Collocazione: " + luogo.getCollocazione() + " -> " + (nuovaCollocazione.isEmpty() ? luogo.getCollocazione() : nuovaCollocazione));
        mostraMessaggio("Tipi di visita: " + luogo.getTipiVisita() + " -> " + nuoviTipi);
    }

    //VOLONTARI---------------------------------------------------------------------------------------------------------------
    // public int chiediSelezioneVolontario(List<Volontario> volontari) {
    //     mostraMessaggio("Volontari disponibili:");
    //     mostraElencoConOggetti(volontari);
    //     return InputDati.leggiIntero("Seleziona il volontario da eliminare: ", 1, volontari.size()) - 1;
    // }

    public boolean chiediConfermaEliminazioneVolontario(Volontario volontario) {
        return InputDati.yesOrNo("Sei sicuro di voler eliminare il volontario: " + volontario.getNome() + "?");
    }

    public Volontario chiediDatiNuovoVolontario() {
        String nome = InputDati.leggiStringaNonVuota("Inserisci il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
        String email = InputDati.leggiStringaNonVuota("Inserisci l'email: ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
        List<TipiVisita> tipiVisita = chiediNuoviTipiVisita(new ArrayList<>());
        return new Volontario(nome, cognome, email, password, tipiVisita);
    }

    
    // Selezione multipla di volontari
    public List<Volontario> chiediVolontariMultipli(List<Volontario> volontariDisponibili) {
        mostraMessaggio("Seleziona i volontari (inserisci i numeri separati da virgola, es. 1,3,5):");
        mostraElencoConOggetti(volontariDisponibili);
        String input = InputDati.leggiStringaNonVuota("Volontari selezionati:");
        String[] numeri = input.split(",");
        List<Volontario> selezionati = new ArrayList<>();
        for (String numero : numeri) {
            try {
                int index = Integer.parseInt(numero.trim()) - 1;
                if (index >= 0 && index < volontariDisponibili.size()) {
                    selezionati.add(volontariDisponibili.get(index));
                }
            } catch (NumberFormatException e) {
                mostraMessaggio("Input non valido: " + numero);
            }
        }
        return selezionati;
    }


    //PRENOTAZIONI---------------------------------------------------------------------------------------------------------------
    public int chiediNumeroMaxPersoneIscrivibili() {
        return InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone iscrivibili per visita: ", 1);
    }

    public void mostraRisultatoAggiornamentoNumeroMax(boolean successo, int numeroMax) {
        if (successo) {
            mostraMessaggio("Numero massimo di persone iscrivibili per visita aggiornato a: " + numeroMax);
        } else {
            mostraMessaggio("Errore nel salvataggio del numero massimo di persone iscrivibili.");
        }
    }

    public int chiediSelezionePrenotazione(List<Prenotazione> prenotazioni) {
        mostraMessaggio("Le tue prenotazioni:");
        mostraElencoConOggetti(prenotazioni);
        return InputDati.leggiIntero("Seleziona la prenotazione da cancellare: ", 1, prenotazioni.size()) - 1;
    }

    public boolean chiediConfermaCancellazionePrenotazione(Prenotazione prenotazione) {
        return InputDati.yesOrNo("Sei sicuro di voler cancellare la prenotazione con codice: " + prenotazione.getCodicePrenotazione() + "?");
    }

    public void mostraRisultatoCancellazionePrenotazione(boolean successo) {
        if (successo) {
            mostraMessaggio("Prenotazione cancellata con successo.");
        } else {
            mostraMessaggio("Errore nella cancellazione della prenotazione.");
        }
    }

    //DATE DISPONIBILITA'---------------------------------------------------------------------------------------------------------------
    public void mostraCalendarioMese(YearMonth ym, List<Integer> giorniDisponibili) {
        System.out.println("Calendario del mese di " + ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN) + " " + ym.getYear() + ":");
        System.out.println("Giorno\tGiorno della settimana");
        for (Integer giorno : giorniDisponibili) {
            LocalDate data = ym.atDay(giorno);
            String giornoSettimana = data.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            System.out.printf("%02d\t%s%n", giorno, giornoSettimana);
        }
    }

    public List<Integer> chiediGiorniDisponibili(YearMonth ym, List<Integer> giorniDisponibili) {
        List<Integer> giorniSelezionati = new ArrayList<>();
        boolean continua = true;
        do {
            System.out.println("Giorni disponibili: " + giorniDisponibili);
            int giorno = InputDati.leggiIntero("Inserisci il giorno da aggiungere (0 per terminare): ", 0, ym.lengthOfMonth());
            if (giorno == 0) {
                continua = false;
            } else if (giorniDisponibili.contains(giorno) && !giorniSelezionati.contains(giorno)) {
                giorniSelezionati.add(giorno);
                giorniDisponibili.remove(Integer.valueOf(giorno));
                System.out.println("Giorno " + giorno + " aggiunto alle tue disponibilità.");
            } else {
                System.out.println("Giorno non disponibile o già selezionato. Scegli un giorno valido.");
            }
            if (giorniDisponibili.isEmpty()) {
                System.out.println("Hai selezionato tutti i giorni disponibili.");
                continua = false;
            }
        } while (continua);
        return giorniSelezionati;
    }
}

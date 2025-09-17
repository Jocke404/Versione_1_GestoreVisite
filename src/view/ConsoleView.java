package src.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lib.InputDati;
import src.model.Luogo;
import src.model.Prenotazione;
import src.model.TipiVisita;
import src.model.Visita;
import src.model.Volontario;

public class ConsoleView implements View{

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

    //VISITE
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

    public boolean chiediConfermaModifica(String statoOriginale, String nuovoStato) {
        mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
        mostraMessaggio("Stato: " + statoOriginale + " -> " + nuovoStato);
        return InputDati.yesOrNo("Vuoi confermare e salvare la modifica dello stato?");
    }
    //MAX PERSONE
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

    //DATE PRECLUSE
    public int chiediDataPreclusaDaEliminare(List<Map.Entry<LocalDate, String>> datePrecluse) {
        mostraMessaggio("Date precluse disponibili:");
        for (int i = 0; i < datePrecluse.size(); i++) {
            Map.Entry<LocalDate, String> entry = datePrecluse.get(i);
            System.out.printf("%d. Data: %s, Motivo: %s%n", i + 1, entry.getKey(), entry.getValue());
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

    //LUOGHI
    public int chiediSelezioneLuogo(List<Luogo> luoghi) {
        mostraMessaggio("Luoghi disponibili:");
        mostraElencoConOggetti(luoghi);
        return InputDati.leggiIntero("Seleziona il luogo: ", 1, luoghi.size()) - 1;
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
        // Puoi implementare una logica simile a quella che hai già, per aggiungere/rimuovere tipi
        return tipiAttuali; // Placeholder
    }

    public void mostraConfrontoLuogo(Luogo luogo, String nuovoNome, String nuovaDescrizione, String nuovaCollocazione, List<TipiVisita> nuoviTipi) {
        mostraMessaggio("\n--- CONFRONTO MODIFICHE ---");
        mostraMessaggio("Nome: " + luogo.getNome() + " -> " + (nuovoNome.isEmpty() ? luogo.getNome() : nuovoNome));
        mostraMessaggio("Descrizione: " + luogo.getDescrizione() + " -> " + (nuovaDescrizione.isEmpty() ? luogo.getDescrizione() : nuovaDescrizione));
        mostraMessaggio("Collocazione: " + luogo.getCollocazione() + " -> " + (nuovaCollocazione.isEmpty() ? luogo.getCollocazione() : nuovaCollocazione));
        mostraMessaggio("Tipi di visita: " + luogo.getTipiVisita() + " -> " + nuoviTipi);
    }

    //VOLONTARI
    public int chiediSelezioneVolontario(List<Volontario> volontari) {
        mostraMessaggio("Volontari disponibili:");
        mostraElencoConOggetti(volontari);
        return InputDati.leggiIntero("Seleziona il volontario da eliminare: ", 1, volontari.size()) - 1;
    }

    public boolean chiediConfermaEliminazioneVolontario(Volontario volontario) {
        return InputDati.yesOrNo("Sei sicuro di voler eliminare il volontario: " + volontario.getNome() + "?");
    }

    //VISITE
    public int chiediSelezioneVisita(List<Visita> visite) {
        mostraMessaggio("Visite disponibili:");
        mostraElencoConOggetti(visite);
        return InputDati.leggiIntero("Seleziona la visita da eliminare: ", 1, visite.size()) - 1;
    }

    public boolean chiediConfermaEliminazioneVisita(Visita visita) {
        return InputDati.yesOrNo("Sei sicuro di voler eliminare la visita con ID: " + visita.getId() + "? Questa azione non può essere annullata.");
    }

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
}

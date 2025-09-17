package src.view;

import java.util.List;

import lib.InputDati;
import src.model.Visita;

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
}

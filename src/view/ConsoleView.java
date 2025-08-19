package src.view;

import java.util.List;

import lib.InputDati;

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
            System.out.printf("%d. %s%n", i + 1, elementi.get(i));
        }
    }

    public void mostraElencoConOggetti(List<?> oggetti) {
        for (int i = 0; i < oggetti.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, oggetti.get(i).toString());
        }
    }
}

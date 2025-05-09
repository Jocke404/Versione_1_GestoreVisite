package src.view;

import lib.MyMenu;
import src.controller.VisitManager;

import java.time.LocalDate;

public class MenuVolontario implements Menu {
    private VisitManager visitManager = new VisitManager(); // Inizializza il VisitManager
    private static final String[] OPZIONI_VOLONTARIO = {
        "Visualizza visite assegnate",
        "Inserisci disponibilità"
    };

    @Override
    public void mostraMenu() {
        // Inizializza il menu con le opzioni disponibili
        boolean goOn = true;
        System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", OPZIONI_VOLONTARIO);
            int chosed = menu.scegli();

            if (chosed != 0) {
                if (chosed == 1) {
                    //Logica per visualizzare le visite assegnate al volontario
                    visitManager.visualizzaVisiteVolontario();
                } else if (chosed == 2) {
                    //Logica per inserire le disponibilità del volontario
                    //VisitManager.inserisciDisponibilita();
                } else if (chosed == 4) {
                    //TODO
                } else if (chosed == 0) {
                    goOn = false;
                }
            } else
                goOn = false;
        } while (goOn);
    }
}

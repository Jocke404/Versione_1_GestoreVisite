package src.it.unibs.ingsw.gestvisit.view;

import java.time.LocalDate;

import it.unibs.mylib.MyMenu;
import src.it.unibs.ingsw.gestvisit.controller.VisitManager;

public class MenuConfiguratore implements Menu {
    private static final String[] SELECT = {"Aggiungi Luogo", "Aggiungi Volontario", "Aggiungi Visita", 
    "Visualizza Luoghi", "Visualizza Volontari", "Visualizza Visite", 
    "Modifica numero massimo di persone per visita", 
    "Modifica stato della visita", "Visualizza visite per stato", "Visualizza archivio storico"};
    private VisitManager visitManager = new VisitManager();

    @Override
    public void mostraMenu() {
        // Inizializza il menu con le opzioni disponibili
        boolean goOn = true;
        System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", SELECT);
            int chosed = menu.scegli();

            if (chosed != 0) {
                if (chosed == 1) {
                    visitManager.aggiungiLuogo();
                } else if (chosed == 2) {
                    visitManager.aggiungiVolontario();
                } else if (chosed == 3) {
                    visitManager.aggiungiVisita();
                } else if (chosed == 4) {
                    visitManager.mostraLuoghi();
                } else if (chosed == 5) {
                    visitManager.mostraVolontari();
                } else if (chosed == 6) {
                    visitManager.mostraVisite();
                } else if (chosed == 7) {
                    visitManager.modificaNumeroMaxPersonePerVisita();
                } else if (chosed == 8) {
                    visitManager.modificaStatoVisita();
                }else if (chosed == 9) {
                    visitManager.visualizzaVisitePerStato();
                }else if (chosed == 10) {
                    visitManager.visualizzaArchivioStorico();
                }else if(chosed == 11){
                    
                }else if (chosed == 0) {
                    goOn = false;
                }
            } else
                goOn = false;
        } while (goOn);
    }

}

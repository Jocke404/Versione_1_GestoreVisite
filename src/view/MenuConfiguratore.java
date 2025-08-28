package src.view;

import java.time.LocalDate;

import lib.MyMenu;
import src.controller.ConfiguratoriController;

public class MenuConfiguratore implements Menu {
    private static final String[] SELECT = {"Aggiungi Luogo", "Aggiungi Volontario", "Aggiungi Visita", 
    "Aggiungi Date Precluse", "Visualizza Luoghi", "Visualizza Volontari", "Visualizza Visite", 
    "Visualizza Date Precluse", "Modifica numero massimo di persone per visita",
    "Modifica stato della visita", "Elimina date precluse", "Visualizza visite per stato", "Visualizza archivio storico"};
    private final ConfiguratoriController configuratoriController; // Riferimento all'istanza di ConfiguratoriController che ha creato questo menu

    public MenuConfiguratore(ConfiguratoriController configuratoriController) {
        this.configuratoriController = configuratoriController;
    }
    
    @Override
    public void mostraMenu() {
        // Inizializza il menu con le opzioni disponibili
        boolean goOn = true;
        System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", SELECT);
            int chosed = menu.scegli();

            switch (chosed) {
                case 1 -> configuratoriController.aggiungiLuogo();
                case 2 -> configuratoriController.aggiungiVolontario();
                case 3 -> configuratoriController.aggiungiVisita();
                case 4 -> configuratoriController.aggiungiDatePrecluse();
                case 5 -> configuratoriController.mostraLuoghi();
                case 6 -> configuratoriController.mostraVolontari();
                case 7 -> configuratoriController.mostraVisite();
                case 8 -> configuratoriController.mostraDatePrecluse();
                case 9 -> configuratoriController.modificaNumeroMaxPersonePerVisita();
                case 10 -> configuratoriController.modificaStatoVisita();
                case 11 -> configuratoriController.eliminaDatePrecluse();
                case 12 -> configuratoriController.visualizzaVisitePerStato();
                case 13 -> configuratoriController.visualizzaArchivioStorico();
                case 0 -> goOn = false; // Esce dal menu
                default -> System.out.println("Opzione non valida.");
            }
        } while (goOn);
    }
}

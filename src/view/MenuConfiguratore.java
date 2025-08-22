package src.view;

import java.time.LocalDate;

import javax.swing.JOptionPane;

import lib.MyMenu;
import src.controller.ConfiguratoriController;
import src.controller.MasterController;

public class MenuConfiguratore implements Menu {
    private static final String[] SELECT = {"Aggiungi Luogo", "Aggiungi Volontario", "Aggiungi Visita", 
    "Visualizza Luoghi", "Visualizza Volontari", "Visualizza Visite", 
    "Modifica numero massimo di persone per visita",
    "Modifica stato della visita", "Visualizza visite per stato", "Visualizza archivio storico"};
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
                case 8 -> configuratoriController.modificaNumeroMaxPersonePerVisita();
                case 9 -> configuratoriController.modificaStatoVisita();
                case 10 -> configuratoriController.visualizzaVisitePerStato();
                case 11 -> configuratoriController.visualizzaArchivioStorico();
                case 0 -> goOn = false; // Esce dal menu
                default -> System.out.println("Opzione non valida.");
            }
        } while (goOn);
    }
}

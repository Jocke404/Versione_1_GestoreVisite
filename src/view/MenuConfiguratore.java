package src.view;

import java.time.LocalDate;

import lib.MyMenu;
import src.controller.ConfiguratoriController;

public class MenuConfiguratore implements Menu {
    private static final String[] SELECT = {"Aggiungi Luogo", "Aggiungi Volontario", "Aggiungi Visita", 
    "Aggiungi Date Precluse", "Visualizza Luoghi", "Visualizza Volontari", "Visualizza Visite", 
    "Visualizza Date Precluse", "Mostra Ambito Territoriale", "Modifica numero massimo di persone per visita",
    "Modifica stato della visita", "Modifica luogo", "Elimina date precluse", "Elimina luogo", "Elimina volontario", "Visualizza visite per stato", "Visualizza archivio storico", "Stampa Tipi Visita per Luogo"};
    private final ConfiguratoriController configuratoriController;

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
                case 9 -> configuratoriController.mostraAmbitoTerritoriale();
                case 10 -> configuratoriController.modificaNumeroMaxPersonePerVisita();
                case 11 -> configuratoriController.modificaStatoVisita();
                case 12 -> configuratoriController.modificaLuogo();
                case 13 -> configuratoriController.eliminaDatePrecluse();
                case 14 -> configuratoriController.eliminaLuogo();
                case 15 -> configuratoriController.eliminaVolontario();
                case 16 -> configuratoriController.visualizzaVisitePerStato();
                case 17 -> configuratoriController.visualizzaArchivioStorico();
                case 18 -> configuratoriController.stampaTipiVisitaPerLuogo();
                case 0 -> goOn = false;
                default -> System.out.println("Opzione non valida.");
            }
        } while (goOn);
    }
}

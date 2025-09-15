package src.view;

import java.time.LocalDate;

import lib.MyMenu;
import src.controller.ConfiguratoriController;

public class MenuConfiguratore implements Menu {
    private static final String[] MENU = {
        "Gestione Configurazioni", "Gestione Luoghi", 
        "Gestione Visite", "Gestione Volontari"
    };

    private static final String [] SOTTOMENU_CONFIGURAZIONI={
        "Aggiungi date precluse", "Visualizza date precluse", 
        "Visualizza ambito territoriale", "Modifica numero massimo persone per visita",
        "Modifica numero persone iscrivibili da un fruitore", "Elimina date precluse"
    };
    
    private static final String [] SOTTOMENU_VOLONTARI={
        "Aggiungi Volontario", "Aggiungi volontari a un tipo di visita",
        "Rimuovi volontari da un tipo di visita", "Visualizza tutti i Volontari",
        "Visualizza volontari per tipo di visita",  
    };

    private static final String [] SOTTOMENU_VISITE={
        "Aggiungi Visita", "Visualizza Visite", 
        "Modifica stato della visita", "Visualizza visite per stato", 
        "Visualizza archivio storico", "Stampa Tipi Visita per Luogo",
    };

    private static final String [] SOTTOMENU_LUOGHI={
        "Aggiungi Luogo", "Visualizza Luoghi", 
        "Modifica Luogo", "Elimina Luogo"
    };

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
            MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", MENU);
            int chosed = menu.scegli();

            switch (chosed) {
                case 1 -> sottoMenuConfig();
                case 2 -> sottoMenuLuoghi();
                case 3 -> sottoMenuVisite();
                case 4 -> sottomenuVolontari();
                
                case 0 -> goOn = false;
                default -> System.out.println("Opzione non valida.");
            }
        } while (goOn);
    }

    private void sottoMenuConfig(){
        boolean tornaIndietro = false;

        do{
            MyMenu sottomenu = new MyMenu ("GESTIONE CONFIGURAZIONI", SOTTOMENU_CONFIGURAZIONI);
            int sceltaSottomenu = sottomenu.scegli();

            switch (sceltaSottomenu){
                case 1 -> configuratoriController.aggiungiDatePrecluse();
                case 2 -> configuratoriController.mostraDatePrecluse();
                case 3 -> configuratoriController.mostraAmbitoTerritoriale();
                case 4 -> configuratoriController.modificaNumeroMaxPersonePerVisita();
                case 5 -> configuratoriController.modificaNumeroPersoneIscrivibili();
                case 6 -> configuratoriController.eliminaDatePrecluse();

                case 0 -> tornaIndietro = true;
                default -> System.out.println("Opzione non valida.");
            }
        } while (!tornaIndietro);
    }
    
    private void sottoMenuLuoghi(){
            boolean tornaIndietro = false;

            do{
                MyMenu sottomenu = new MyMenu ("GESTIONE LUOGHI", SOTTOMENU_LUOGHI);
                
                int sceltaSottomenu = sottomenu.scegli();

                switch (sceltaSottomenu){
                    case 1 -> configuratoriController.aggiungiLuogo();
                    case 2 -> configuratoriController.mostraLuoghi();
                    case 3 -> configuratoriController.modificaLuogo();
                    case 4 -> configuratoriController.eliminaLuogo();

                    case 0 -> tornaIndietro = true;
                    default -> System.out.println("Opzione non valida.");
                }
            } while (!tornaIndietro);
    }

    private void sottoMenuVisite(){
        boolean tornaIndietro = false;

        do{
            MyMenu sottomenu = new MyMenu ("GESTIONE VISITE", SOTTOMENU_VISITE);
            
            int sceltaSottomenu = sottomenu.scegli();

            switch (sceltaSottomenu){
                case 1 -> configuratoriController.aggiungiVisita();
                case 2 -> configuratoriController.mostraVisite();
                case 3 -> configuratoriController.modificaStatoVisita();
                case 4 -> configuratoriController.visualizzaVisitePerStato();
                case 5 -> configuratoriController.visualizzaArchivioStorico();
                case 6 -> configuratoriController.stampaTipiVisitaPerLuogo();
                case 7 -> configuratoriController.eliminaVisita();


                case 0 -> tornaIndietro = true;
                default -> System.out.println("Opzione non valida.");
            }
        } while (!tornaIndietro);
    }

    private void sottomenuVolontari(){
        boolean tornaIndietro = false;

        do{
            MyMenu sottomenu = new MyMenu ("GESTIONE VOLONTARI", SOTTOMENU_VOLONTARI);
            
            int sceltaSottomenu = sottomenu.scegli();

            switch (sceltaSottomenu){
                case 1 -> configuratoriController.aggiungiVolontario();
                case 2 -> configuratoriController.aggiungiVolontariATipoVisita();
                case 3 -> configuratoriController.rimuoviVolontariDaTipoVisita();
                case 4 -> configuratoriController.mostraVolontari();
                case 5 -> configuratoriController.visualizzaVolontariPerTipoVisita();
                case 6 -> configuratoriController.eliminaVolontario();

                case 0 -> tornaIndietro = true;
                default -> System.out.println("Opzione non valida.");
            }
        } while (!tornaIndietro);
    }

}

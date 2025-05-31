package src.view;

import lib.MyMenu;
import src.controller.VolontariController;

import java.time.LocalDate;

public class MenuVolontario implements Menu {
    private final VolontariController volontariController; // Updated controller reference
    private static final String[] OPZIONI_VOLONTARIO = {
        "Visualizza visite assegnate",
        "Inserisci disponibilità"
    };

    public MenuVolontario(VolontariController volontariController) {
        this.volontariController = volontariController;
    }

    @Override
    public void mostraMenu() {
        boolean goOn = true;
        System.out.printf("Oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", OPZIONI_VOLONTARIO);
            int chosed = menu.scegli();

            switch (chosed) {
                case 1 -> volontariController.visualizzaVisiteVolontario();
                case 2 -> volontariController.inserisciDisponibilitaVolontario();
                case 0 -> goOn = false;
                default -> System.out.println("Opzione non valida.");
            }
        } while (goOn);
    }
}



// package src.view;

// import lib.MyMenu;
// import src.controller.VisitManager;

// import java.time.LocalDate;

// public class MenuVolontario implements Menu {
//     private final VisitManager visitManager; // Riferimento all'istanza di VisitManager che ha creato questo menu
//     private static final String[] OPZIONI_VOLONTARIO = {
//         "Visualizza visite assegnate",
//         "Inserisci disponibilità"
//     };

//     @Override
//     public void mostraMenu() {
//         this.visitManager = new VisitManager(); // Inizializza l'istanza di VisitManager
//         boolean goOn = true;
//         System.out.printf("Oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
//         do {
//             MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", OPZIONI_VOLONTARIO);
//             int scelta = menu.scegli();

//             switch (scelta) {
//                 case 1 -> visualizzaVisiteVolontario();
//                 case 2 -> inserisciDisponibilita();
//                 case 0 -> goOn = false;
//                 default -> System.out.println("Opzione non valida.");
//             }
//         } while (goOn);
//     }
//     public MenuVolontario(VisitManager visitManager) {
//         this.visitManager = visitManager;
//         // Inizializza il menu con le opzioni disponibili
//         boolean goOn = true;
//         System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
//         do {
//             MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", OPZIONI_VOLONTARIO);
//             int chosed = menu.scegli();

//             if (chosed != 0) {
//                 if (chosed == 1) {
//                     //Logica per visualizzare le visite assegnate al volontario
//                     visitManager.visualizzaVisiteVolontario();
//                 } else if (chosed == 2) {
//                     //Logica per inserire le disponibilità del volontario
//                     visitManager.inserisciDisponibilitaVolontario();
//                 } else if (chosed == 4) {
//                     //TODO
//                 } else if (chosed == 0) {
//                     goOn = false;
//                 }
//             } else
//                 goOn = false;
//         } while (goOn);
//     }
// }

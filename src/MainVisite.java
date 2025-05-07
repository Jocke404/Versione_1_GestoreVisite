package src;

import src.controller.VisitManager;

public class MainVisite {
    public static void main(String[] args) {
        VisitManager manager = new VisitManager();

        //manager.leggiCredenziali();
        try {
            manager.autentica();
        } finally {
            // Arresta il thread di aggiornamento prima di uscire
            manager.stopExecutorService();
            
        }
        //System.exit(0); // Forza la terminazione del programma   
    }
}

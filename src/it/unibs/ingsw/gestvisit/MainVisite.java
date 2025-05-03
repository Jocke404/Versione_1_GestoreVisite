package src.it.unibs.ingsw.gestvisit;

import src.it.unibs.ingsw.gestvisit.controller.VisitManager;

public class MainVisite {
    public static void main(String[] args) {
        VisitManager manager = new VisitManager();

        //manager.leggiCredenziali();
        try {
            manager.autentica();
        } finally {
            // Arresta il thread di aggiornamento prima di uscire
            manager.stopExecutorService();
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        }
        //System.exit(0); // Forza la terminazione del programma   
    }
}

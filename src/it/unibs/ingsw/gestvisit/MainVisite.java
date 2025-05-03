package src.it.unibs.ingsw.gestvisit;

import src.it.unibs.ingsw.gestvisit.controller.VisitManager;

public class MainVisite {
    public static void main(String[] args) {
        VisitManager manager = new VisitManager();
        //manager.leggiCredenziali();
        manager.autentica();

        // Arresta il thread di aggiornamento prima di uscire
        manager.stopExecutorService();
          
    }
}

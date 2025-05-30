package src;

import src.controller.AuthenticationController;
import src.controller.MasterController;

public class MainVisite {
    public static void main(String[] args) {
        AuthenticationController auth = AuthenticationController.getInstance();
        MasterController master = MasterController.getInstance();

        try {
            auth.autentica();
        } finally {
            // Arresta il thread di aggiornamento prima di uscire
            master.stopExecutorService();
            
        }
    }
}

package src.factory;

import src.view.Menu;
import src.view.MenuVolontario;
import src.view.MenuConfiguratore;
import src.controller.VolontariController;
import src.controller.ConfiguratoriController;

public class MenuFactory {
    public Menu creaMenuVolontario(VolontariController volontariController) {
        return new MenuVolontario(volontariController);
    }

    public Menu creaMenuConfiguratore(ConfiguratoriController configuratoriController) {
        return new MenuConfiguratore(configuratoriController);
    }
}
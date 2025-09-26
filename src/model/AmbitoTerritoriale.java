package src.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import src.view.ConsoleIO;
import lib.InputDati;

public class AmbitoTerritoriale {

private static final String AMBITO_FILE = "src/utility/ambito_territoriale.config";
    private Set<String> ambitoTerritoriale = new HashSet<>();
    private final ConsoleIO consoleIO = new ConsoleIO();

    public void verificaOAggiornaAmbitoTerritoriale() {
        if (!isAmbitoConfigurato()) {
            scegliAmbitoTerritoriale();
            salvaAmbitoTerritoriale();
        } else {
            caricaAmbitoTerritoriale();
        }
    }

    public boolean isAmbitoConfigurato() {
        File file = new File(AMBITO_FILE);
        return file.exists();
    }

    public void scegliAmbitoTerritoriale() {
        consoleIO.mostraMessaggio("Configurazione ambito territoriale (inserisci uno o più comuni).");
        do {
            String comune = InputDati.leggiStringaNonVuota("Inserisci il nome del comune: ");
            ambitoTerritoriale.add(comune);
        } while (InputDati.yesOrNo("Vuoi aggiungere un altro comune? (s/n): "));
        salvaAmbitoTerritoriale();
        consoleIO.mostraMessaggio("Ambito territoriale configurato: " + ambitoTerritoriale);
    }

    private void salvaAmbitoTerritoriale() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AMBITO_FILE))) {
            for (String comune : ambitoTerritoriale) {
                writer.write(comune);
                writer.newLine();
            }
        } catch (IOException e) {
            consoleIO.mostraMessaggio("Errore nel salvataggio dell'ambito territoriale.");
        }
    }

    public void caricaAmbitoTerritoriale() {
        ambitoTerritoriale.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(AMBITO_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ambitoTerritoriale.add(line.trim());
            }
        } catch (IOException e) {
            consoleIO.mostraMessaggio("Errore nel caricamento dell'ambito territoriale.");
        }
    }

    public List<String> getAmbitoTerritoriale() {
        if (ambitoTerritoriale.isEmpty()) {
            caricaAmbitoTerritoriale();
        }
        return new ArrayList<>(ambitoTerritoriale);
    }

}

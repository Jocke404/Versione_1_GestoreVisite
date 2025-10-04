package src.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import src.model.db.VisiteManagerDB;
import src.controller.LuoghiController;
import src.controller.VisiteController;
import src.controller.VolontariController;
import lib.InputDati;
import src.view.ConsoleIO;

public class ModificaUtilita {

    private final VisiteManagerDB visiteManagerDB;
    private final ConsoleIO consoleIO = new ConsoleIO();
    private static final String NUMERO_PERSONE_FILE = "src/utility/max_persone_iscrivibili.config";


    public ModificaUtilita(VisiteManagerDB visiteManagerDB) {
        this.visiteManagerDB = visiteManagerDB;
    }

    public boolean aggiornaStatoVisita(int visitaId, String nuovoStato) {
        Visita visita = visiteManagerDB.getVisiteMap().get(visitaId);
        if (visita == null) return false;
        visita.setStato(nuovoStato);
        visiteManagerDB.aggiornaVisita(visitaId, visita);
        return true;
    }

    public boolean aggiornaDataVisita(int visitaId, LocalDate nuovaData) {
        Visita visita = visiteManagerDB.getVisiteMap().get(visitaId);
        if (visita == null) return false;
        visita.setData(nuovaData);
        visiteManagerDB.aggiornaVisita(visitaId, visita);
        return true;
    }

    public boolean aggiornaMaxPersone(int numeroMax) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NUMERO_PERSONE_FILE))) {
            writer.write(String.valueOf(numeroMax));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean eliminaDataPreclusa(LocalDate data) {
        visiteManagerDB.eliminaData(data);
        return true;
    }

    public void eliminaLuogo(Luogo luogo, LuoghiController luoghiController) {
        luoghiController.eliminaLuogo(luogo);
    }

    public void aggiornaLuogo(Luogo luogo, String nuovoNome, String nuovaDescrizione, 
                            String nuovaCollocazione, List<TipiVisita> nuoviTipi, LuoghiController luoghiController) {
        if (!nuovoNome.isEmpty()) luogo.setName(nuovoNome);
        if (!nuovaDescrizione.isEmpty()) luogo.setDescrizione(nuovaDescrizione);
        if (!nuovaCollocazione.isEmpty()) luogo.setCollocazione(nuovaCollocazione);
        luogo.setTipiVisita(nuoviTipi);
        luoghiController.aggiornaLuoghi(luogo);
    }

    public void eliminaVolontario(Volontario volontario, VolontariController volontariController) {
        volontariController.eliminaVolontario(volontario);
    }

    public void eliminaVisita(Visita visita, VisiteController visiteController) {
        visiteController.eliminaVisita(visita);
    }

    // Leggi il valore dal file (chiamalo all'avvio)
    public int caricaNumeroPersoneIscrivibili() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NUMERO_PERSONE_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            // Se il file non esiste o c'è errore, ritorna un valore di default
            return 10;
        }
        return 10;
    }    
    
    // Metodo per modificare il numero massimo
    public void modificaNumeroPersoneIscrivibili() {
        if (consoleIO.chiediAnnullaOperazione())
            return;
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone iscrivibili per visita: ", 1);
        aggiornaNumeroPersoneIscrivibili(numeroMax);
        consoleIO.mostraMessaggio("Numero massimo di persone iscrivibili per visita aggiornato a: " + numeroMax);
    }

    public boolean aggiornaNumeroPersoneIscrivibili(int numeroMax) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NUMERO_PERSONE_FILE))) {
            writer.write(String.valueOf(numeroMax));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}

package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.*;
import java.time.format.TextStyle;

import src.model.db.*;

import lib.InputDati;
import src.view.ConsoleIO;
import src.view.ViewUtilita;



public class AggiuntaUtilita {

    // private final DatabaseUpdater databaseUpdater;
    private final VolontariManager volontariManager;
    private final LuoghiManager luoghiManager;
    private final VisiteManagerDB visiteManagerDB;
    ConcurrentHashMap<String, Luogo> luoghiMap;
    ConcurrentHashMap<String, Volontario> volontariMap;
    ConcurrentHashMap<String, TipiVisita> tipiVisitaMap = TipiVisita.getTipiVisitaMap();
    List<TipiVisita> tipiVisitaList;
    ConcurrentHashMap<Integer, Visita> visiteMap;
    ConcurrentHashMap<LocalDate, String> datePrecluseMap;
    private final ValidatoreVisite validatoreVisite;
    private final ViewUtilita viewUtilita = ViewUtilita.getInstance();
    private final ModificaUtilita modificaUtilita;
    private int maxPersoneIscrivibili;
    private AmbitoTerritoriale ambitoTerritoriale = new AmbitoTerritoriale();
    

    private final ConsoleIO consoleIO = new ConsoleIO();
    private final Map<String, List<LocalDate>> disponibilitaVolontari = new ConcurrentHashMap<>();

    public AggiuntaUtilita(VolontariManager volontariManager, LuoghiManager luoghiManager, VisiteManagerDB visiteManagerDB) {
        this.volontariManager = volontariManager;
        this.luoghiManager = luoghiManager;
        this.visiteManagerDB = visiteManagerDB;
        this.luoghiMap = luoghiManager.getLuoghiMap();
        this.volontariMap = volontariManager.getVolontariMap();
        this.visiteMap = visiteManagerDB.getVisiteMap();
        this.tipiVisitaList = visiteManagerDB.getTipiVisitaList();
        this.validatoreVisite = new ValidatoreVisite(visiteManagerDB);
        this.modificaUtilita = new ModificaUtilita(visiteManagerDB);
    }

    public boolean aggiungiVisita(Visita nuovaVisita) {
        visiteManagerDB.aggiungiNuovaVisita(nuovaVisita);
        visiteMap.put(nuovaVisita.getId(), nuovaVisita);
        return true;
    }

    public void aggiungiVolontario(Volontario nuovoVolontario) {
        volontariMap.putIfAbsent(nuovoVolontario.getEmail(), nuovoVolontario);
        volontariManager.aggiungiNuovoVolontario(nuovoVolontario);
    }

    public void aggiungiLuogo(Luogo nuovoLuogo) {
        luoghiMap.putIfAbsent(nuovoLuogo.getNome(), nuovoLuogo);
        luoghiManager.aggiungiNuovoLuogo(nuovoLuogo);
    }

    public void aggiungiDataPreclusa(LocalDate data, String motivo) {
        visiteManagerDB.aggiungiNuovaDataPreclusa(data, motivo);
    }

    public void assegnaTipoVisitaAVolontari(List<Volontario> volontari, TipiVisita tipoVisita) {
        for (Volontario volontario : volontari) {
            volontariManager.aggiungiTipoVisitaAVolontari(volontario.getEmail(), tipoVisita);
        }
    }

    public void rimuoviTipoVisitaDaVolontari(List<Volontario> volontari, TipiVisita tipoVisita) {
        for (Volontario volontario : volontari) {
            volontariManager.rimuoviTipoVisitaDaVolontario(volontario.getEmail(), tipoVisita);
        }
    }
}
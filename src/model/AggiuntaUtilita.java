package src.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.time.*;

import src.model.db.*;



public class AggiuntaUtilita {

    private final VolontariManager volontariManager;
    private final LuoghiManager luoghiManager;
    private final VisiteManagerDB visiteManagerDB;
    ConcurrentHashMap<String, Luogo> luoghiMap;
    ConcurrentHashMap<String, Volontario> volontariMap;
    ConcurrentHashMap<String, TipiVisitaClass> tipiVisitaMap = TipiVisitaClass.getTipiVisitaClassMap();
    List<TipiVisitaClass> tipiVisitaList;
    ConcurrentHashMap<Integer, Visita> visiteMap;
    ConcurrentHashMap<LocalDate, String> datePrecluseMap;

    public AggiuntaUtilita(VolontariManager volontariManager, LuoghiManager luoghiManager,
                            VisiteManagerDB visiteManagerDB) {
        this.volontariManager = volontariManager;
        this.luoghiManager = luoghiManager;
        this.visiteManagerDB = visiteManagerDB;
        this.luoghiMap = luoghiManager.getLuoghiMap();
        this.volontariMap = volontariManager.getVolontariMap();
        this.visiteMap = visiteManagerDB.getVisiteMap();
        this.tipiVisitaList = visiteManagerDB.getTipiVisitaClassList();
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

    public void assegnaTipoVisitaAVolontari(List<Volontario> volontari, TipiVisitaClass tipoVisita) {
        for (Volontario volontario : volontari) {
            volontariManager.aggiungiTipoVisitaAVolontari(volontario.getEmail(), tipoVisita);
        }
    }

    public void rimuoviTipoVisitaDaVolontari(List<Volontario> volontari, TipiVisitaClass tipoVisita) {
        for (Volontario volontario : volontari) {
            volontariManager.rimuoviTipoVisitaDaVolontario(volontario.getEmail(), tipoVisita);
        }
    }

    public void assegnaVisitaAVolontario(Visita visitaSelezionata, Volontario volontarioSelezionato) {
        visiteManagerDB.assegnaVisitaAVolontario(volontarioSelezionato, visitaSelezionata);
    }

    public void aggiungiNuovoTipoVisita(TipiVisitaClass nuovoTipo) {
        visiteManagerDB.addNuovoTipoVisita(nuovoTipo);
    }

    public void rimuoviVisitaDaVolontario(Visita visitaSelezionata, Volontario volontarioSelezionato) {
        visiteManagerDB.rimuoviVisitaDaVolontario(volontarioSelezionato, visitaSelezionata);
    }

}
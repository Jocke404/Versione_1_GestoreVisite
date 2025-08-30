package src.controller;

import src.model.Visite;
import src.model.db.VisiteManagerDB;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VisiteController {
    private final VisiteManagerDB visiteManagerDB;

    public VisiteController(VisiteManagerDB visiteManagerDB) {
        this.visiteManagerDB = visiteManagerDB;
    }

    public List<Visite> getVisite() {
        return List.copyOf(visiteManagerDB.getVisiteMap().values());
    }

    public ConcurrentHashMap<Integer, Visite> getVisiteMap() {
        return visiteManagerDB.getVisiteMap();
    }

    public List<Map.Entry<LocalDate, String>> getDatePrecluse() {
        return List.copyOf(visiteManagerDB.getDatePrecluseMap().entrySet());
    }
}
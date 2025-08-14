package src.controller;

import src.model.Visite;
import src.model.db.VisiteManagerDB;
import java.util.concurrent.ConcurrentHashMap;

import java.util.List;

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
}
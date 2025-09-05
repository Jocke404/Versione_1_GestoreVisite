package src.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public enum TipiVisita {
    STORICA("Visita Storica"),
    SCIENTIFICA("Visita Scientifica"),
    ENOGASTRONOMICA("Visita Enogastronomica"),
    LABBAMBINI("Laboratorio Bambini");

    private final String descrizione;

    TipiVisita(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getNome() {
        return this.name();
    }

    public static List<TipiVisita> fromString(String string) {
        String[] parts = string.split(",");
        return java.util.Arrays.stream(parts)
                .map(String::trim)
                .map(part -> {
                    try {
                        return TipiVisita.valueOf(part);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Valore non valido per TipiVisita: " + part);
                        return null;
                    }
                })
                .filter(tipiVisita -> tipiVisita != null)
                .toList();
    }

    public static ConcurrentHashMap<String, TipiVisita> getTipiVisitaMap() {
        ConcurrentHashMap<String, TipiVisita> map = new ConcurrentHashMap<>();
        for (TipiVisita tipo : TipiVisita.values()) {
            map.put(tipo.getNome(), tipo);
        }
        return map;
    }
}

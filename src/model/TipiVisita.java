package src.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public enum TipiVisita {
    STORICA("Visita Storica, Un percorso guidato alla scoperta della storia e dei monumenti principali della città."),
    SCIENTIFICA("Visita Scientifica, Un'esperienza educativa dedicata alle scienze e alle innovazioni tecnologiche."),
    ENOGASTRONOMICA("Visita Enogastronomica, Un viaggio tra i sapori tipici locali con degustazioni di prodotti tradizionali."),
    LABBAMBINI("Laboratorio Bambini, Attività ludico-didattiche pensate per i più piccoli, con laboratori creativi e giochi.");

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

package src.model;

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
}

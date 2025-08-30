package src.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Visite {
    private int id; // ID univoco della visita
    private String luogo;
    private TipiVisita tipoVisita;
    private String volontario;
    private LocalDate data;
    private int maxPersone; 
    private String stato; 
    private LocalTime oraInizio; 
    private int durataMinuti; 

    // Costruttore completo (inclusi tutti i campi)
    public Visite(int id, String luogo, TipiVisita tipoVisita, String volontario, LocalDate data, int maxPersone, String stato, LocalTime oraInizio, int durataMinuti) {
        this.id = id;
        this.luogo = luogo;
        this.tipoVisita = tipoVisita;
        this.volontario = volontario;
        this.data = data;
        this.maxPersone = maxPersone;
        this.stato = stato;
        this.oraInizio = oraInizio;
        this.durataMinuti = durataMinuti;
    }

    
    public LocalTime getOraInizio() {
        return oraInizio;
    }


    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }


    public int getDurataMinuti() {
        return durataMinuti;
    }


    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }


    // Getter e Setter
    public int getId() {
        return id;
    }

    public String getLuogo() {
        return luogo;
    }

    public TipiVisita getTipoVisita() {
        return tipoVisita;
    }

    public String getTipoVisitaString() {
        return tipoVisita != null ? tipoVisita.getNome() : "";
    }

    public String getVolontario() {
        return volontario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getMaxPersone() {
        return maxPersone;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }
}
   
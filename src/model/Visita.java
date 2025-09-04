package src.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Visita {
    private int id; // ID univoco della visita
    private String luogo;
    private List<TipiVisita> tipiVisita;
    private String volontario;
    private LocalDate data;
    private int maxPersone; 
    private String stato; 
    private LocalTime oraInizio; 
    private int durataMinuti; 

    // Costruttore completo (inclusi tutti i campi)
    public Visita(int id, String luogo, List<TipiVisita> tipiVisita, String volontario, 
                LocalDate data, int maxPersone, String stato, LocalTime oraInizio, int durataMinuti) {
        this.id = id;
        this.luogo = luogo;
        this.tipiVisita = tipiVisita;
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

    public List<TipiVisita> getTipiVisita() {
        return tipiVisita;
    }

    public String getTipiVisitaString() {
        if (tipiVisita == null || tipiVisita.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (TipiVisita tipo : tipiVisita) {
            sb.append(tipo.getNome()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
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
   
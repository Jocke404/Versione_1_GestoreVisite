package src.it.unibs.ingsw.gestvisit.model;

import java.time.LocalDate;

public class Visite {
    private int id; // ID univoco della visita
    private String luogo;
    private String tipoVisita;
    private String volontario;
    private LocalDate data;
    private int maxPersone; // Numero massimo di persone per la visita
    private String stato; // Stato della visita (es. "Proposta", "Confermata", ecc.)

    // Costruttore completo (inclusi tutti i campi)
    public Visite(int id, String luogo, String tipoVisita, String volontario, LocalDate data, int maxPersone, String stato) {
        this.id = id;
        this.luogo = luogo;
        this.tipoVisita = tipoVisita;
        this.volontario = volontario;
        this.data = data;
        this.maxPersone = maxPersone;
        this.stato = stato;
    }

    // Getter e Setter
    public int getId() {
        return id;
    }

    public String getLuogo() {
        return luogo;
    }

    public String getTipoVisita() {
        return tipoVisita;
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

    // Metodo toString per rappresentare l'oggetto come stringa
    // @Override
    // public String toString() {
    //     return "Visite{" +
    //             "id=" + id +
    //             ", luogo='" + luogo + '\'' +
    //             ", tipoVisita='" + tipoVisita + '\'' +
    //             ", volontario='" + volontario + '\'' +
    //             ", data=" + data +
    //             ", maxPersone=" + maxPersone +
    //             ", stato='" + stato + '\'' +
    //             '}';
    // }
}
   
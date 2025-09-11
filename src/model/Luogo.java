package src.model;

import java.util.*;

public class Luogo {
    
    private String nome;
    private String descrizione;
    private String collocazione;
    private List<TipiVisita> tipiVisita; 


    public Luogo(String nome, String descrizione, String collocazione, List<TipiVisita> tipiVisita) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.collocazione = collocazione;
        this.tipiVisita = tipiVisita;
    }

    public List<TipiVisita> getTipiVisita() {
        return tipiVisita;
    }

    public void setTipiVisita(List<TipiVisita> tipiVisita) {
        this.tipiVisita = tipiVisita;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCollocazione() {
        return collocazione;
    }

    public void setName(String nuovoNome) {
        this.nome = nuovoNome;
    }

    public void setDescrizione(String nuovaDescrizione) {
        this.descrizione = nuovaDescrizione;
    }

    public void setCollocazione(String nuovaCollocazione) {
        this.collocazione = nuovaCollocazione;
    }

    @Override
    public String toString() {
        return "Luogo [descrizione=" + descrizione + ", nome=" + nome + "]";
    }

}

package src.model;

import java.util.*;

public class Luogo {
    
    private String nome;
    private String descrizione;
    private String collocazione;
    private List<TipiVisitaClass> tipiVisita; 


    public Luogo(String nome, String descrizione, String collocazione, List<TipiVisitaClass> tipiVisita) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.collocazione = collocazione;
        this.tipiVisita = tipiVisita;
    }

    public List<TipiVisitaClass> getTipiVisitaClass() {
        return tipiVisita;
    }

    public void setTipiVisitaClass(List<TipiVisitaClass> tipiVisita) {
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
        return "Luogo [nome=" + nome + ", descrizione=" + descrizione + ", collocazione=" + collocazione + ", tipiVisita=" + tipiVisita + "]";
    }

}
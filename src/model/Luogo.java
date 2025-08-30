package src.model;
// import it.unibs.mylib.*;

import java.util.*;

public class Luogo {
    
    private String nome;
    private String descrizione;
    private String collocazione;
    private HashMap<String, List<String>> volontari;
    private HashMap<String, List<String>> tipiVisita; 


    public Luogo(String nome, String descrizione, String collocazione) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.collocazione = collocazione;
        this.tipiVisita = new HashMap<>();
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

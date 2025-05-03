package src.it.unibs.ingsw.gestvisit.model;
// import it.unibs.mylib.*;
import it.unibs.mylib.InputDati;

import java.util.*;

public class Luogo {
    
    private String nome;
    private String descrizione;
    private HashMap<String, List<String>> volontari;
    private HashMap<String, List<String>> tipiVisita; 


    public Luogo(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.tipiVisita = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }
    

  /* public void aggiungiTipoVisita(String tipo) {
        tipiVisita.add(tipo);
    } */

    /*public void assegnaVolontario(String volontario) {
        volontari.add(volontario);
    }*/

    public static Luogo creaLuogo(){
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire una descrizione: ");
        return new Luogo(nome, descrizione);
    }

    public static Luogo creaLuogoUtente(String nome, String descrizione) {
        return new Luogo(nome, descrizione);
    }


    @Override
    public String toString() {
        return "Luogo [descrizione=" + descrizione + ", nome=" + nome + "]";
    }

}

package src.test.java.src.model;

import src.model.Visita;
import src.model.TipiVisita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple test class for Visita without JUnit dependencies
 */
public class VisitaSimpleTest {
    
    public static void main(String[] args) {
        VisitaSimpleTest test = new VisitaSimpleTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("Running Visita Tests...");
        
        testVisitaCreation();
        testPostiDisponibili();
        testIsDisponibile();
        testTipiVisitaString();
        testDescrizione();
        testToString();
        testDataTimeHandling();
        testEdgeCases();
        
        System.out.println("All Visita tests completed successfully!");
    }
    
    public void testVisitaCreation() {
        int id = 1;
        String luogo = "Museo di Storia";
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA);
        String volontario = "Mario Rossi";
        LocalDate data = LocalDate.of(2025, 10, 15);
        int maxPersone = 20;
        String stato = "APERTA";
        LocalTime oraInizio = LocalTime.of(10, 30);
        int durataMinuti = 120;
        int postiPrenotati = 5;
        
        Visita visita = new Visita(id, luogo, tipiVisita, volontario, data, maxPersone, stato, oraInizio, durataMinuti, postiPrenotati);
        
        assert visita != null : "Visita should not be null";
        assert visita.getId() == id : "ID should match";
        assert luogo.equals(visita.getLuogo()) : "Luogo should match";
        assert visita.getTipiVisita() != null : "TipiVisita should not be null";
        assert visita.getTipiVisita().size() == 2 : "Should have 2 tipi visita";
        assert volontario.equals(visita.getVolontario()) : "Volontario should match";
        assert data.equals(visita.getData()) : "Data should match";
        assert visita.getMaxPersone() == maxPersone : "MaxPersone should match";
        assert stato.equals(visita.getStato()) : "Stato should match";
        assert oraInizio.equals(visita.getOraInizio()) : "OraInizio should match";
        assert visita.getDurataMinuti() == durataMinuti : "DurataMinuti should match";
        assert visita.getPostiPrenotati() == postiPrenotati : "PostiPrenotati should match";
        
        System.out.println("✓ testVisitaCreation passed");
    }
    
    public void testPostiDisponibili() {
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA);
        Visita visita = new Visita(1, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        
        assert visita.getPostiDisponibili() == 15 : "PostiDisponibili should be 15 (20-5)";
        
        // Test after changing postiPrenotati
        visita.setPostiPrenotati(10);
        assert visita.getPostiDisponibili() == 10 : "PostiDisponibili should be 10 (20-10)";
        
        // Test when fully booked
        visita.setPostiPrenotati(20);
        assert visita.getPostiDisponibili() == 0 : "PostiDisponibili should be 0 when fully booked";
        
        System.out.println("✓ testPostiDisponibili passed");
    }
    
    public void testIsDisponibile() {
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA);
        
        // Test disponibile
        Visita visitaDisponibile = new Visita(1, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        assert visitaDisponibile.isDisponibile() : "Visita should be disponibile when there are free spots";
        
        // Test not disponibile
        Visita visitaNonDisponibile = new Visita(2, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 20);
        assert !visitaNonDisponibile.isDisponibile() : "Visita should not be disponibile when fully booked";
        
        System.out.println("✓ testIsDisponibile passed");
    }
    
    public void testTipiVisitaString() {
        // Test with multiple tipi
        List<TipiVisita> multipleTipi = Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA, TipiVisita.ENOGASTRONOMICA);
        Visita visitaMultiple = new Visita(1, "Museo", multipleTipi, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        String tipiString = visitaMultiple.getTipiVisitaString();
        assert tipiString.contains("STORICA") : "String should contain STORICA";
        assert tipiString.contains("SCIENTIFICA") : "String should contain SCIENTIFICA";
        assert tipiString.contains("ENOGASTRONOMICA") : "String should contain ENOGASTRONOMICA";
        assert !tipiString.endsWith(", ") : "String should not end with comma and space";
        
        // Test with single tipo
        List<TipiVisita> singleTipo = Arrays.asList(TipiVisita.STORICA);
        Visita visitaSingle = new Visita(2, "Museo", singleTipo, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        assert "STORICA".equals(visitaSingle.getTipiVisitaString()) : "String should be exactly 'STORICA'";
        
        // Test with empty list
        List<TipiVisita> emptyTipi = new ArrayList<>();
        Visita visitaEmpty = new Visita(3, "Museo", emptyTipi, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        assert "".equals(visitaEmpty.getTipiVisitaString()) : "String should be empty for empty list";
        
        // Test with null
        Visita visitaNull = new Visita(4, "Museo", null, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        assert "".equals(visitaNull.getTipiVisitaString()) : "String should be empty for null list";
        
        System.out.println("✓ testTipiVisitaString passed");
    }
    
    public void testDescrizione() {
        // Test with tipi visita
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA);
        Visita visitaWithTipi = new Visita(1, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        String descrizione = visitaWithTipi.getDescrizione();
        assert descrizione != null : "Descrizione should not be null";
        assert descrizione.contains("storia") : "Descrizione should contain 'storia' from STORICA";
        
        // Test with empty tipi visita
        List<TipiVisita> emptyTipi = new ArrayList<>();
        Visita visitaEmpty = new Visita(2, "Museo", emptyTipi, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        assert "".equals(visitaEmpty.getDescrizione()) : "Descrizione should be empty for empty tipi";
        
        // Test with null tipi visita
        Visita visitaNull = new Visita(3, "Museo", null, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        assert "".equals(visitaNull.getDescrizione()) : "Descrizione should be empty for null tipi";
        
        System.out.println("✓ testDescrizione passed");
    }
    
    public void testToString() {
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA);
        LocalDate data = LocalDate.of(2025, 10, 15);
        LocalTime oraInizio = LocalTime.of(10, 30);
        
        Visita visita = new Visita(1, "Museo di Storia", tipiVisita, "Mario Rossi", data, 20, "APERTA", oraInizio, 120, 5);
        String toString = visita.toString();
        
        assert toString.contains("STORICA") : "toString should contain tipo visita";
        assert toString.contains("Museo di Storia") : "toString should contain luogo";
        assert toString.contains("2025-10-15") : "toString should contain data";
        assert toString.contains("10:30") : "toString should contain ora";
        assert toString.contains("APERTA") : "toString should contain stato";
        assert toString.contains("120") : "toString should contain durata";
        assert toString.contains("20") : "toString should contain max persone";
        assert toString.contains("15") : "toString should contain posti disponibili";
        
        System.out.println("✓ testToString passed");
    }
    
    public void testDataTimeHandling() {
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA);
        LocalDate originalData = LocalDate.of(2025, 10, 15);
        LocalTime originalOra = LocalTime.of(10, 30);
        
        Visita visita = new Visita(1, "Museo", tipiVisita, "Mario Rossi", originalData, 20, "APERTA", originalOra, 120, 5);
        
        // Test setting new data
        LocalDate newData = LocalDate.of(2025, 11, 20);
        visita.setData(newData);
        assert newData.equals(visita.getData()) : "Data should be updated";
        
        // Test setting new ora
        LocalTime newOra = LocalTime.of(14, 45);
        visita.setOraInizio(newOra);
        assert newOra.equals(visita.getOraInizio()) : "OraInizio should be updated";
        
        // Test setting new durata
        visita.setDurataMinuti(90);
        assert visita.getDurataMinuti() == 90 : "DurataMinuti should be updated";
        
        System.out.println("✓ testDataTimeHandling passed");
    }
    
    public void testEdgeCases() {
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA);
        
        // Test with zero max persone
        Visita visitaZeroMax = new Visita(1, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 0, "APERTA", LocalTime.of(10, 0), 120, 0);
        assert visitaZeroMax.getPostiDisponibili() == 0 : "PostiDisponibili should be 0 when maxPersone is 0";
        assert !visitaZeroMax.isDisponibile() : "Should not be disponibile when maxPersone is 0";
        
        // Test with negative posti prenotati (edge case)
        Visita visitaNegative = new Visita(2, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, -5);
        assert visitaNegative.getPostiDisponibili() == 25 : "PostiDisponibili should be 25 when postiPrenotati is -5";
        
        // Test stato changes
        Visita visitaStato = new Visita(3, "Museo", tipiVisita, "Mario Rossi", LocalDate.now(), 20, "APERTA", LocalTime.of(10, 0), 120, 5);
        visitaStato.setStato("CHIUSA");
        assert "CHIUSA".equals(visitaStato.getStato()) : "Stato should be updated to CHIUSA";
        
        System.out.println("✓ testEdgeCases passed");
    }
}
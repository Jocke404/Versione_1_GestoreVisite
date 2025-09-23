package src.test.java.src.model;

import src.model.TipiVisita;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple test class for TipiVisita enum without JUnit dependencies
 */
public class TipiVisitaSimpleTest {
    
    public static void main(String[] args) {
        TipiVisitaSimpleTest test = new TipiVisitaSimpleTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("Running TipiVisita Tests...");
        
        testEnumValues();
        testGetDescrizione();
        testGetNome();
        testFromStringValid();
        testFromStringInvalid();
        testFromStringMixed();
        testGetTipiVisitaMap();
        
        System.out.println("All TipiVisita tests completed successfully!");
    }
    
    public void testEnumValues() {
        TipiVisita[] values = TipiVisita.values();
        
        assert values.length == 4 : "Should have 4 enum values";
        assert values[0] == TipiVisita.STORICA : "First value should be STORICA";
        assert values[1] == TipiVisita.SCIENTIFICA : "Second value should be SCIENTIFICA";
        assert values[2] == TipiVisita.ENOGASTRONOMICA : "Third value should be ENOGASTRONOMICA";
        assert values[3] == TipiVisita.LABBAMBINI : "Fourth value should be LABBAMBINI";
        
        System.out.println("✓ testEnumValues passed");
    }
    
    public void testGetDescrizione() {
        assert TipiVisita.STORICA.getDescrizione().contains("storia") : "STORICA description should contain 'storia'";
        assert TipiVisita.SCIENTIFICA.getDescrizione().contains("scienze") : "SCIENTIFICA description should contain 'scienze'";
        assert TipiVisita.ENOGASTRONOMICA.getDescrizione().contains("sapori") : "ENOGASTRONOMICA description should contain 'sapori'";
        assert TipiVisita.LABBAMBINI.getDescrizione().contains("bambini") : "LABBAMBINI description should contain 'bambini'";
        
        System.out.println("✓ testGetDescrizione passed");
    }
    
    public void testGetNome() {
        assert "STORICA".equals(TipiVisita.STORICA.getNome()) : "STORICA name should be 'STORICA'";
        assert "SCIENTIFICA".equals(TipiVisita.SCIENTIFICA.getNome()) : "SCIENTIFICA name should be 'SCIENTIFICA'";
        assert "ENOGASTRONOMICA".equals(TipiVisita.ENOGASTRONOMICA.getNome()) : "ENOGASTRONOMICA name should be 'ENOGASTRONOMICA'";
        assert "LABBAMBINI".equals(TipiVisita.LABBAMBINI.getNome()) : "LABBAMBINI name should be 'LABBAMBINI'";
        
        System.out.println("✓ testGetNome passed");
    }
    
    public void testFromStringValid() {
        String input = "STORICA, SCIENTIFICA, ENOGASTRONOMICA";
        List<TipiVisita> result = TipiVisita.fromString(input);
        
        assert result != null : "Result should not be null";
        assert result.size() == 3 : "Should parse 3 valid types";
        assert result.contains(TipiVisita.STORICA) : "Should contain STORICA";
        assert result.contains(TipiVisita.SCIENTIFICA) : "Should contain SCIENTIFICA";
        assert result.contains(TipiVisita.ENOGASTRONOMICA) : "Should contain ENOGASTRONOMICA";
        
        System.out.println("✓ testFromStringValid passed");
    }
    
    public void testFromStringInvalid() {
        String input = "INVALID_TYPE, ANOTHER_INVALID";
        List<TipiVisita> result = TipiVisita.fromString(input);
        
        assert result != null : "Result should not be null";
        assert result.size() == 0 : "Should parse 0 valid types from invalid input";
        
        System.out.println("✓ testFromStringInvalid passed");
    }
    
    public void testFromStringMixed() {
        String input = "STORICA, INVALID_TYPE, SCIENTIFICA";
        List<TipiVisita> result = TipiVisita.fromString(input);
        
        assert result != null : "Result should not be null";
        assert result.size() == 2 : "Should parse 2 valid types from mixed input";
        assert result.contains(TipiVisita.STORICA) : "Should contain STORICA";
        assert result.contains(TipiVisita.SCIENTIFICA) : "Should contain SCIENTIFICA";
        
        System.out.println("✓ testFromStringMixed passed");
    }
    
    public void testGetTipiVisitaMap() {
        ConcurrentHashMap<String, TipiVisita> map = TipiVisita.getTipiVisitaMap();
        
        assert map != null : "Map should not be null";
        assert map.size() == 4 : "Map should contain 4 entries";
        assert map.get("STORICA") == TipiVisita.STORICA : "Map should contain STORICA";
        assert map.get("SCIENTIFICA") == TipiVisita.SCIENTIFICA : "Map should contain SCIENTIFICA";
        assert map.get("ENOGASTRONOMICA") == TipiVisita.ENOGASTRONOMICA : "Map should contain ENOGASTRONOMICA";
        assert map.get("LABBAMBINI") == TipiVisita.LABBAMBINI : "Map should contain LABBAMBINI";
        
        System.out.println("✓ testGetTipiVisitaMap passed");
    }
}
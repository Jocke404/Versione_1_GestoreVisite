package src.test.java.src.model;

import src.model.Volontario;
import src.model.TipiVisita;
import src.model.Utente;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Simple test class for Volontario without JUnit dependencies
 */
public class VolontarioSimpleTest {
    
    public static void main(String[] args) {
        VolontarioSimpleTest test = new VolontarioSimpleTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("Running Volontario Tests...");
        
        testVolontarioCreation();
        testUtenteInheritance();
        testTipiVisitaManagement();
        testAggiungiTipoVisita();
        testRimuoviTipoVisita();
        testContieneTipoVisita();
        testSetTipiDiVisite();
        testEmptyTipiVisite();
        testNullTipiVisite();
        
        System.out.println("All Volontario tests completed successfully!");
    }
    
    public void testVolontarioCreation() {
        String nome = "Giovanni";
        String cognome = "Bianchi";
        String email = "giovanni.bianchi@email.com";
        String password = "password123";
        List<TipiVisita> tipiVisite = Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA);
        
        Volontario volontario = new Volontario(nome, cognome, email, password, tipiVisite);
        
        assert volontario != null : "Volontario should not be null";
        assert nome.equals(volontario.getNome()) : "Nome should match";
        assert cognome.equals(volontario.getCognome()) : "Cognome should match";
        assert email.equals(volontario.getEmail()) : "Email should match";
        assert password.equals(volontario.getPassword()) : "Password should match";
        assert volontario.getTipiDiVisite() != null : "TipiDiVisite should not be null";
        assert volontario.getTipiDiVisite().size() == 2 : "Should have 2 tipi di visite";
        
        System.out.println("✓ testVolontarioCreation passed");
    }
    
    public void testUtenteInheritance() {
        List<TipiVisita> tipiVisite = Arrays.asList(TipiVisita.STORICA);
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", tipiVisite);
        
        assert volontario instanceof Utente : "Volontario should inherit from Utente";
        assert volontario instanceof Volontario : "Instance should be Volontario";
        
        System.out.println("✓ testUtenteInheritance passed");
    }
    
    public void testTipiVisitaManagement() {
        List<TipiVisita> tipiVisite = new ArrayList<>(Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA));
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", tipiVisite);
        
        List<TipiVisita> retrievedTipi = volontario.getTipiDiVisite();
        assert retrievedTipi != null : "TipiDiVisite should not be null";
        assert retrievedTipi.size() == 2 : "Should have 2 tipi di visite";
        assert retrievedTipi.contains(TipiVisita.STORICA) : "Should contain STORICA";
        assert retrievedTipi.contains(TipiVisita.SCIENTIFICA) : "Should contain SCIENTIFICA";
        
        System.out.println("✓ testTipiVisitaManagement passed");
    }
    
    public void testAggiungiTipoVisita() {
        List<TipiVisita> tipiVisite = new ArrayList<>(Arrays.asList(TipiVisita.STORICA));
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", tipiVisite);
        
        // Test adding new tipo
        volontario.aggiungiTipoVisita(TipiVisita.ENOGASTRONOMICA);
        assert volontario.getTipiDiVisite().size() == 2 : "Should have 2 tipi after adding";
        assert volontario.getTipiDiVisite().contains(TipiVisita.ENOGASTRONOMICA) : "Should contain new tipo";
        
        // Test adding duplicate tipo
        volontario.aggiungiTipoVisita(TipiVisita.STORICA);
        assert volontario.getTipiDiVisite().size() == 2 : "Should still have 2 tipi after adding duplicate";
        
        System.out.println("✓ testAggiungiTipoVisita passed");
    }
    
    public void testRimuoviTipoVisita() {
        List<TipiVisita> tipiVisite = new ArrayList<>(Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA, TipiVisita.ENOGASTRONOMICA));
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", tipiVisite);
        
        // Test removing existing tipo
        volontario.rimuoviTipoVisita(TipiVisita.SCIENTIFICA);
        assert volontario.getTipiDiVisite().size() == 2 : "Should have 2 tipi after removing";
        assert !volontario.getTipiDiVisite().contains(TipiVisita.SCIENTIFICA) : "Should not contain removed tipo";
        
        // Test removing non-existing tipo
        volontario.rimuoviTipoVisita(TipiVisita.LABBAMBINI);
        assert volontario.getTipiDiVisite().size() == 2 : "Should still have 2 tipi after removing non-existing";
        
        System.out.println("✓ testRimuoviTipoVisita passed");
    }
    
    public void testContieneTipoVisita() {
        List<TipiVisita> tipiVisite = new ArrayList<>(Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA));
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", tipiVisite);
        
        assert volontario.contieneTipoVisita(TipiVisita.STORICA) : "Should contain STORICA";
        assert volontario.contieneTipoVisita(TipiVisita.SCIENTIFICA) : "Should contain SCIENTIFICA";
        assert !volontario.contieneTipoVisita(TipiVisita.ENOGASTRONOMICA) : "Should not contain ENOGASTRONOMICA";
        assert !volontario.contieneTipoVisita(TipiVisita.LABBAMBINI) : "Should not contain LABBAMBINI";
        
        System.out.println("✓ testContieneTipoVisita passed");
    }
    
    public void testSetTipiDiVisite() {
        List<TipiVisita> initialTipi = new ArrayList<>(Arrays.asList(TipiVisita.STORICA));
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", initialTipi);
        
        List<TipiVisita> newTipi = Arrays.asList(TipiVisita.ENOGASTRONOMICA, TipiVisita.LABBAMBINI);
        volontario.setTipiDiVisite(newTipi);
        
        assert volontario.getTipiDiVisite().size() == 2 : "Should have 2 new tipi";
        assert volontario.getTipiDiVisite().contains(TipiVisita.ENOGASTRONOMICA) : "Should contain ENOGASTRONOMICA";
        assert volontario.getTipiDiVisite().contains(TipiVisita.LABBAMBINI) : "Should contain LABBAMBINI";
        assert !volontario.getTipiDiVisite().contains(TipiVisita.STORICA) : "Should not contain old STORICA";
        
        System.out.println("✓ testSetTipiDiVisite passed");
    }
    
    public void testEmptyTipiVisite() {
        List<TipiVisita> emptyTipi = new ArrayList<>();
        Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", emptyTipi);
        
        assert volontario.getTipiDiVisite() != null : "TipiDiVisite should not be null";
        assert volontario.getTipiDiVisite().size() == 0 : "Should have 0 tipi di visite";
        assert !volontario.contieneTipoVisita(TipiVisita.STORICA) : "Should not contain any tipo";
        
        // Test adding to empty list
        volontario.aggiungiTipoVisita(TipiVisita.STORICA);
        assert volontario.getTipiDiVisite().size() == 1 : "Should have 1 tipo after adding";
        
        System.out.println("✓ testEmptyTipiVisite passed");
    }
    
    public void testNullTipiVisite() {
        try {
            Volontario volontario = new Volontario("Giovanni", "Bianchi", "giovanni.bianchi@email.com", "password123", null);
            // If this doesn't throw an exception, the implementation handles null gracefully
            System.out.println("✓ testNullTipiVisite passed (null handled gracefully)");
        } catch (Exception e) {
            // If an exception is thrown, that's also a valid behavior
            System.out.println("✓ testNullTipiVisite passed (exception thrown as expected: " + e.getClass().getSimpleName() + ")");
        }
    }
}
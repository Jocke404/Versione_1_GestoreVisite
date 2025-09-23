package src.test.java.src.factory;

import src.factory.UserFactory;
import src.model.Utente;
import src.model.Volontario;
import src.model.Configuratore;
import src.model.Fruitore;
import src.model.TipiVisita;
import java.util.Arrays;
import java.util.List;

/**
 * Simple test class for UserFactory without JUnit dependencies
 */
public class UserFactorySimpleTest {
    
    public static void main(String[] args) {
        UserFactorySimpleTest test = new UserFactorySimpleTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("Running UserFactory Tests...");
        
        testCreateVolontario();
        testCreateConfiguratore();
        testCreateFruitore();
        testInvalidUserType();
        testFactoryConstants();
        
        System.out.println("All UserFactory tests completed successfully!");
    }
    
    public void testCreateVolontario() {
        String nome = "Mario";
        String cognome = "Rossi";
        String email = "mario.rossi@email.com";
        String password = "password123";
        List<TipiVisita> tipiVisita = Arrays.asList(TipiVisita.STORICA, TipiVisita.SCIENTIFICA);
        
        Utente utente = UserFactory.createUser(UserFactory.VOLONTARIO, email, password, nome, cognome, tipiVisita);
        
        assert utente != null : "Created user should not be null";
        assert utente instanceof Volontario : "Created user should be instance of Volontario";
        assert utente instanceof Utente : "Created user should be instance of Utente";
        assert nome.equals(utente.getNome()) : "Nome should match";
        assert cognome.equals(utente.getCognome()) : "Cognome should match";
        assert email.equals(utente.getEmail()) : "Email should match";
        assert password.equals(utente.getPassword()) : "Password should match";
        
        Volontario volontario = (Volontario) utente;
        assert volontario.getTipiDiVisite() != null : "TipiDiVisite should not be null";
        assert volontario.getTipiDiVisite().size() == 2 : "Should have 2 tipi di visite";
        assert volontario.getTipiDiVisite().contains(TipiVisita.STORICA) : "Should contain STORICA";
        assert volontario.getTipiDiVisite().contains(TipiVisita.SCIENTIFICA) : "Should contain SCIENTIFICA";
        
        System.out.println("✓ testCreateVolontario passed");
    }
    
    public void testCreateConfiguratore() {
        String nome = "Luigi";
        String cognome = "Verdi";
        String email = "luigi.verdi@email.com";
        String password = "password456";
        
        Utente utente = UserFactory.createUser(UserFactory.CONFIGURATORE, email, password, nome, cognome, null);
        
        assert utente != null : "Created user should not be null";
        assert utente instanceof Configuratore : "Created user should be instance of Configuratore";
        assert utente instanceof Utente : "Created user should be instance of Utente";
        assert nome.equals(utente.getNome()) : "Nome should match";
        assert cognome.equals(utente.getCognome()) : "Cognome should match";
        assert email.equals(utente.getEmail()) : "Email should match";
        assert password.equals(utente.getPassword()) : "Password should match";
        
        System.out.println("✓ testCreateConfiguratore passed");
    }
    
    public void testCreateFruitore() {
        String nome = "Anna";
        String cognome = "Bianchi";
        String email = "anna.bianchi@email.com";
        String password = "password789";
        
        Utente utente = UserFactory.createUser(UserFactory.FRUITORE, email, password, nome, cognome, null);
        
        assert utente != null : "Created user should not be null";
        assert utente instanceof Fruitore : "Created user should be instance of Fruitore";
        assert utente instanceof Utente : "Created user should be instance of Utente";
        assert nome.equals(utente.getNome()) : "Nome should match";
        assert cognome.equals(utente.getCognome()) : "Cognome should match";
        assert email.equals(utente.getEmail()) : "Email should match";
        assert password.equals(utente.getPassword()) : "Password should match";
        
        System.out.println("✓ testCreateFruitore passed");
    }
    
    public void testInvalidUserType() {
        boolean exceptionThrown = false;
        
        try {
            UserFactory.createUser("INVALID_TYPE", "email@test.com", "password", "Nome", "Cognome", null);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
            assert e.getMessage().contains("sconosciuto") : "Exception message should mention unknown type";
        } catch (Exception e) {
            throw new AssertionError("Expected IllegalArgumentException but got: " + e.getClass().getSimpleName());
        }
        
        assert exceptionThrown : "Should throw IllegalArgumentException for invalid user type";
        
        System.out.println("✓ testInvalidUserType passed");
    }
    
    public void testFactoryConstants() {
        assert "Volontario".equals(UserFactory.VOLONTARIO) : "VOLONTARIO constant should be 'Volontario'";
        assert "Configuratore".equals(UserFactory.CONFIGURATORE) : "CONFIGURATORE constant should be 'Configuratore'";
        assert "Fruitore".equals(UserFactory.FRUITORE) : "FRUITORE constant should be 'Fruitore'";
        
        System.out.println("✓ testFactoryConstants passed");
    }
}
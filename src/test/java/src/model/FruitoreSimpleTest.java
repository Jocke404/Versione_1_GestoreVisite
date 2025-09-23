package src.test.java.src.model;

import src.model.Fruitore;
import src.model.Utente;

/**
 * Simple test class for Fruitore without JUnit dependencies
 * Can be run as a standalone test to verify functionality
 */
public class FruitoreSimpleTest {
    
    public static void main(String[] args) {
        FruitoreSimpleTest test = new FruitoreSimpleTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("Running Fruitore Tests...");
        
        testFruitoreCreation();
        testUtenteInheritance();
        testUtenteSetters();
        testToString();
        testNullValueHandling();
        testEmptyStringHandling();
        testSpecialCharacters();
        
        System.out.println("All Fruitore tests completed successfully!");
    }
    
    public void testFruitoreCreation() {
        String nome = "Mario";
        String cognome = "Rossi";
        String email = "mario.rossi@email.com";
        String password = "password123";
        
        Fruitore fruitore = new Fruitore(nome, cognome, email, password);
        
        assert fruitore != null : "Fruitore should not be null";
        assert nome.equals(fruitore.getNome()) : "Nome should match";
        assert cognome.equals(fruitore.getCognome()) : "Cognome should match";
        assert email.equals(fruitore.getEmail()) : "Email should match";
        assert password.equals(fruitore.getPassword()) : "Password should match";
        
        System.out.println("✓ testFruitoreCreation passed");
    }
    
    public void testUtenteInheritance() {
        Fruitore fruitore = new Fruitore("Mario", "Rossi", "mario.rossi@email.com", "password123");
        
        assert fruitore instanceof Utente : "Fruitore should inherit from Utente";
        assert fruitore instanceof Fruitore : "Instance should be Fruitore";
        
        System.out.println("✓ testUtenteInheritance passed");
    }
    
    public void testUtenteSetters() {
        Fruitore fruitore = new Fruitore("Mario", "Rossi", "mario.rossi@email.com", "password123");
        
        String newNome = "Luigi";
        String newCognome = "Verdi";
        String newEmail = "luigi.verdi@email.com";
        String newPassword = "newPassword456";

        fruitore.setNome(newNome);
        fruitore.setCognome(newCognome);
        fruitore.setEmail(newEmail);
        fruitore.setPassword(newPassword);

        assert newNome.equals(fruitore.getNome()) : "Nome should be updated";
        assert newCognome.equals(fruitore.getCognome()) : "Cognome should be updated";
        assert newEmail.equals(fruitore.getEmail()) : "Email should be updated";
        assert newPassword.equals(fruitore.getPassword()) : "Password should be updated";
        
        System.out.println("✓ testUtenteSetters passed");
    }
    
    public void testToString() {
        String nome = "Mario";
        String cognome = "Rossi";
        String email = "mario.rossi@email.com";
        String password = "password123";
        
        Fruitore fruitore = new Fruitore(nome, cognome, email, password);
        
        String expected = "Fruitore{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                '}';
                
        assert expected.equals(fruitore.toString()) : "toString should match expected format";
        
        System.out.println("✓ testToString passed");
    }
    
    public void testNullValueHandling() {
        try {
            Fruitore fruitoreWithNulls = new Fruitore(null, null, null, null);
            assert fruitoreWithNulls != null : "Fruitore should handle null values";
            System.out.println("✓ testNullValueHandling passed");
        } catch (Exception e) {
            throw new AssertionError("Should not throw exception with null values: " + e.getMessage());
        }
    }
    
    public void testEmptyStringHandling() {
        Fruitore fruitoreWithEmptyStrings = new Fruitore("", "", "", "");
        
        assert fruitoreWithEmptyStrings != null : "Fruitore should not be null";
        assert "".equals(fruitoreWithEmptyStrings.getNome()) : "Nome should be empty string";
        assert "".equals(fruitoreWithEmptyStrings.getCognome()) : "Cognome should be empty string";
        assert "".equals(fruitoreWithEmptyStrings.getEmail()) : "Email should be empty string";
        assert "".equals(fruitoreWithEmptyStrings.getPassword()) : "Password should be empty string";
        
        System.out.println("✓ testEmptyStringHandling passed");
    }
    
    public void testSpecialCharacters() {
        String nomeSpecial = "José-André";
        String cognomeSpecial = "O'Connor";
        String emailSpecial = "jose-andre.oconnor@email.com";
        String passwordSpecial = "P@$$w0rd!";

        Fruitore fruitoreSpecial = new Fruitore(nomeSpecial, cognomeSpecial, emailSpecial, passwordSpecial);
        
        assert nomeSpecial.equals(fruitoreSpecial.getNome()) : "Special characters in nome should be preserved";
        assert cognomeSpecial.equals(fruitoreSpecial.getCognome()) : "Special characters in cognome should be preserved";
        assert emailSpecial.equals(fruitoreSpecial.getEmail()) : "Special characters in email should be preserved";
        assert passwordSpecial.equals(fruitoreSpecial.getPassword()) : "Special characters in password should be preserved";
        
        System.out.println("✓ testSpecialCharacters passed");
    }
}
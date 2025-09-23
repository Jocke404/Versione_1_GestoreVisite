package src.test.java.src.model;

/**
 * Test runner to execute all unit tests for the model classes
 * This class runs all test suites and provides a summary of results
 */
public class AllModelTestsRunner {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("RUNNING ALL MODEL UNIT TESTS");
        System.out.println("=".repeat(80));
        
        boolean allTestsPassed = true;
        
        try {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("1. TESTING FRUITORE CLASS");
            System.out.println("=".repeat(40));
            FruitoreSimpleTest.main(new String[]{});
            System.out.println("✅ Fruitore tests completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Fruitore tests failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        
        try {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("2. TESTING TIPI VISITA ENUM");
            System.out.println("=".repeat(40));
            TipiVisitaSimpleTest.main(new String[]{});
            System.out.println("✅ TipiVisita tests completed successfully");
        } catch (Exception e) {
            System.err.println("❌ TipiVisita tests failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        
        try {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("3. TESTING VOLONTARIO CLASS");
            System.out.println("=".repeat(40));
            VolontarioSimpleTest.main(new String[]{});
            System.out.println("✅ Volontario tests completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Volontario tests failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        
        try {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("4. TESTING VISITA CLASS");
            System.out.println("=".repeat(40));
            VisitaSimpleTest.main(new String[]{});
            System.out.println("✅ Visita tests completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Visita tests failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        
        // Summary
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST EXECUTION SUMMARY");
        System.out.println("=".repeat(80));
        
        if (allTestsPassed) {
            System.out.println("🎉 ALL TESTS PASSED SUCCESSFULLY!");
            System.out.println("✅ Fruitore: All tests passed");
            System.out.println("✅ TipiVisita: All tests passed");
            System.out.println("✅ Volontario: All tests passed");
            System.out.println("✅ Visita: All tests passed");
            System.out.println("\nYour model classes are working correctly!");
        } else {
            System.out.println("⚠️  SOME TESTS FAILED!");
            System.out.println("Please check the error messages above for details.");
        }
        
        System.out.println("=".repeat(80));
        
        // Additional information
        System.out.println("\nTest Coverage Information:");
        System.out.println("- Core model classes: Fruitore, Volontario, Visita, TipiVisita");
        System.out.println("- Test types: Constructor validation, getter/setter tests, business logic");
        System.out.println("- Edge cases: Null values, empty collections, boundary conditions");
        System.out.println("- Inheritance testing: Utente class inheritance verification");
        
        System.out.println("\nTo run individual test suites:");
        System.out.println("- java src.test.java.src.model.FruitoreSimpleTest");
        System.out.println("- java src.test.java.src.model.TipiVisitaSimpleTest");
        System.out.println("- java src.test.java.src.model.VolontarioSimpleTest");
        System.out.println("- java src.test.java.src.model.VisitaSimpleTest");
    }
}
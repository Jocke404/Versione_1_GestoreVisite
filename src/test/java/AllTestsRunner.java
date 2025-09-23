package src.test.java;

import src.test.java.src.model.AllModelTestsRunner;
import src.test.java.src.factory.UserFactorySimpleTest;
import src.test.java.src.controller.AuthenticationControllerSimpleTest;

/**
 * Comprehensive test runner for all unit tests in the project
 * This class executes all test suites and provides a complete overview
 */
public class AllTestsRunner {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(100));
        System.out.println("COMPREHENSIVE UNIT TEST SUITE FOR GESTORE VISITE PROJECT");
        System.out.println("=".repeat(100));
        
        boolean allTestsPassed = true;
        int totalTestSuites = 0;
        int passedTestSuites = 0;
        
        // Model Tests
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("SECTION 1: MODEL CLASSES TESTS");
            System.out.println("=".repeat(60));
            AllModelTestsRunner.main(new String[]{});
            passedTestSuites++;
            System.out.println("✅ Model tests section completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Model tests section failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        totalTestSuites++;
        
        // Factory Tests
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("SECTION 2: FACTORY CLASSES TESTS");
            System.out.println("=".repeat(60));
            UserFactorySimpleTest.main(new String[]{});
            passedTestSuites++;
            System.out.println("✅ Factory tests section completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Factory tests section failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        totalTestSuites++;
        
        // Controller Tests
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("SECTION 3: CONTROLLER CLASSES TESTS");
            System.out.println("=".repeat(60));
            AuthenticationControllerSimpleTest.main(new String[]{});
            passedTestSuites++;
            System.out.println("✅ Controller tests section completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Controller tests section failed: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }
        totalTestSuites++;
        
        // Final Summary
        System.out.println("\n" + "=".repeat(100));
        System.out.println("FINAL TEST EXECUTION SUMMARY");
        System.out.println("=".repeat(100));
        
        System.out.println("Test Suites Executed: " + totalTestSuites);
        System.out.println("Test Suites Passed: " + passedTestSuites);
        System.out.println("Test Suites Failed: " + (totalTestSuites - passedTestSuites));
        
        if (allTestsPassed) {
            System.out.println("\n🎉 ALL TEST SUITES PASSED SUCCESSFULLY!");
            System.out.println("✅ Model Classes: Fruitore, Volontario, Visita, TipiVisita");
            System.out.println("✅ Factory Classes: UserFactory");
            System.out.println("✅ Controller Classes: AuthenticationController");
            System.out.println("\n🚀 Your codebase is well-tested and working correctly!");
        } else {
            System.out.println("\n⚠️  SOME TEST SUITES FAILED!");
            System.out.println("Please check the error messages above for details.");
            System.out.println("Success Rate: " + (passedTestSuites * 100 / totalTestSuites) + "%");
        }
        
        // Test Coverage Report
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST COVERAGE REPORT");
        System.out.println("=".repeat(60));
        System.out.println("📊 Test Categories Covered:");
        System.out.println("  • Model Unit Tests: Constructor validation, getters/setters, business logic");
        System.out.println("  • Factory Pattern Tests: Object creation, parameter validation, error handling");
        System.out.println("  • Controller Tests: Basic structure validation, dependency injection compatibility");
        System.out.println("  • Edge Case Testing: Null values, empty collections, boundary conditions");
        System.out.println("  • Inheritance Testing: Abstract class implementations, polymorphism");
        System.out.println("  • Enum Testing: Value validation, string conversion, map generation");
        
        System.out.println("\n📁 Files Tested:");
        System.out.println("  • src/model/Fruitore.java");
        System.out.println("  • src/model/Volontario.java");
        System.out.println("  • src/model/Visita.java");
        System.out.println("  • src/model/TipiVisita.java");
        System.out.println("  • src/factory/UserFactory.java");
        System.out.println("  • src/controller/AuthenticationController.java");
        
        System.out.println("\n🛠️  Test Execution Commands:");
        System.out.println("  • Run all tests: java src.test.java.AllTestsRunner");
        System.out.println("  • Run model tests only: java src.test.java.src.model.AllModelTestsRunner");
        System.out.println("  • Run specific test: java src.test.java.src.model.[TestClass]");
        
        System.out.println("=".repeat(100));
    }
}
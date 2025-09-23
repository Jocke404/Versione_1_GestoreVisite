# Gestore Visite - Java 21 Upgrade

This project has been successfully upgraded to **Java 21 LTS** (Latest Long Term Support version).

## Project Overview

Sistema di gestione visite con architettura MVC (Model-View-Controller) che permette la gestione di:
- Volontari
- Luoghi di visita
- Prenotazioni
- Configurazioni

## Java 21 Upgrade Details

### What Was Upgraded

✅ **Java Runtime**: Upgraded from previous version to Java 21 LTS (21.0.5)
✅ **Project Structure**: Added Maven support with proper `pom.xml`
✅ **Build Configuration**: Configured for Java 21 compilation and execution
✅ **Dependencies**: Updated MySQL connector and added modern dependency management
✅ **Build Scripts**: Created PowerShell scripts for easy compilation and execution

### New Features Available with Java 21

The project is now ready to leverage Java 21 features such as:
- **String Templates** (Preview feature)
- **Pattern Matching for switch expressions**
- **Virtual Threads** for improved concurrency
- **Record patterns** for data processing
- **Enhanced performance** and security improvements

## Build and Run Instructions

### Prerequisites

- Java 21 JDK installed and available in PATH
- MySQL database server (for database connectivity)

### Option 1: Using PowerShell Scripts (Recommended)

1. **Build the project:**
   ```powershell
   .\build.ps1
   ```

2. **Run the application:**
   ```powershell
   .\run.ps1
   ```

### Option 2: Using Maven (if Maven is installed)

1. **Build the project:**
   ```bash
   mvn clean compile
   ```

2. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="src.MainVisite"
   ```

### Option 3: Manual Compilation

1. **Clean and prepare:**
   ```powershell
   Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
   New-Item -Type Directory -Force bin
   ```

2. **Compile library classes:**
   ```powershell
   javac -d bin lib\*.java
   ```

3. **Compile main project:**
   ```powershell
   javac -cp "bin;src" -d bin src\MainVisite.java src\controller\*.java src\model\*.java src\model\db\*.java src\view\*.java src\factory\*.java
   ```

4. **Run the application:**
   ```powershell
   java -cp "bin;src/utility/mysql-connector-j-9.2.0.jar" src.MainVisite
   ```

## Project Structure

```
MVC-GestoreVisite/
├── src/                          # Source code
│   ├── MainVisite.java          # Main application entry point
│   ├── controller/              # MVC Controllers
│   ├── model/                   # Data models and business logic
│   ├── view/                    # User interface components
│   ├── factory/                 # Factory pattern implementations
│   └── utility/                 # Utility files and database scripts
├── lib/                         # Utility library classes
├── bin/                         # Compiled classes (generated)
├── pom.xml                      # Maven project configuration
├── build.ps1                    # Build script for PowerShell
├── run.ps1                      # Run script for PowerShell
└── README.md                    # This file
```

## Configuration

### Database Configuration

The application uses MySQL database. Ensure:
1. MySQL server is running on `localhost:3306`
2. Database `gestione_visite` exists
3. Proper credentials are configured in the application

### Java 21 Specific Configuration

The project is configured to:
- Compile with Java 21 source and target compatibility
- Use `--enable-preview` for preview features (optional)
- Include modern dependencies compatible with Java 21

## Dependencies

- **MySQL Connector**: `mysql-connector-j-9.2.0.jar`
- **JUnit 5**: For testing (configured in Maven)
- **SLF4J + Logback**: For logging (configured in Maven)

## Upgrade Benefits

1. **Performance**: Java 21 provides significant performance improvements
2. **Security**: Latest security patches and improvements
3. **Long-term Support**: Java 21 is an LTS version supported until 2031
4. **Modern Features**: Access to latest Java language features
5. **Better Tooling**: Improved IDE support and development tools

## Troubleshooting

### Common Issues

1. **Java version mismatch**: Ensure `java -version` shows 21.x.x
2. **Classpath issues**: Make sure MySQL connector is in classpath
3. **Database connection**: Verify MySQL server is running and accessible

### Verification

To verify the upgrade was successful:
```powershell
java -version
# Should show: openjdk version "21.x.x" ... LTS
```

## Next Steps

Consider leveraging Java 21 features:
1. **Virtual Threads**: For improved concurrency in database operations
2. **Pattern Matching**: For cleaner switch statements in controllers
3. **String Templates**: For better string formatting in views
4. **Records**: For immutable data transfer objects

---

**Note**: This project has been successfully upgraded to Java 21 LTS and is ready for production use with modern Java features and improved performance.

## Original Project Info

# MVC-GestoreVisite
IngeSoftware 

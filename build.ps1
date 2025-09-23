# Build script for Java 21 project
# This script compiles the project using Java 21 and includes all necessary dependencies

# Clean old compilation
Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
New-Item -Type Directory -Force bin

# Compile lib classes first
Write-Host "Compiling library classes..."
javac -d bin lib\*.java

# Compile main project
Write-Host "Compiling main project..."
javac -cp "bin;src" -d bin src\MainVisite.java src\controller\*.java src\model\*.java src\model\db\*.java src\view\*.java src\factory\*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!"
    Write-Host "To run the application, use:"
    Write-Host "java -cp ""bin;src/utility/mysql-connector-j-9.2.0.jar"" src.MainVisite"
} else {
    Write-Host "Compilation failed!"
}
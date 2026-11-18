@echo off
setlocal
cd /d "%~dp0"

echo ==========================================
echo  Integrated Propellers - local build
echo ==========================================

where java >NUL 2>&1
if errorlevel 1 (
    echo ERROR: Java was not found. Install JDK 21 and set JAVA_HOME.
    pause
    exit /b 1
)

set "MISSING="
for %%F in (
    create-1.21.1-6.0.10.jar
    aeronautics-1.3.2.jar
    simulated-1.3.2.jar
    sable-neoforge-1.21.1-2.0.6.jar
    sable-companion-common-1.21.1-1.6.0.jar
    flywheel-1.0.6.jar
    ponder-1.0.82.jar
    registrate-1.3.0.jar
) do (
    if not exist "libs\%%F" (
        echo ERROR: missing libs\%%F
        set "MISSING=1"
    )
)
if defined MISSING (
    echo.
    echo Put the dependency jars listed in libs\README.md into the libs folder.
    pause
    exit /b 1
)

call gradlew.bat build
if errorlevel 1 (
    echo.
    echo Build failed.
    pause
    exit /b 1
)

echo.
echo Build complete: build\libs
pause
endlocal

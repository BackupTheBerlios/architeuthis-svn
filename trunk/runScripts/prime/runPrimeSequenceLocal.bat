@echo off

REM Führt das lokale Programm zur Berechnung von Primzahlen aus


REM Benötigte Umgebungsvariablen (JAVA, CLASS_FILE_PATH) werden gesetzt
call setup.bat


REM die Grenzen für das Nummern-Intervall, aus dem die Primzahlen ermittelt
REM werden sollen
set LOWER_BORDER=2000000
set UPPER_BORDER=2000000

REM die Parameter für die JVM
set ARGS=
set ARGS=%ARGS% %LOWER_BORDER% %UPPER_BORDER%
set ARGS=%ARGS% n

REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.testenvironment.GeneratePrimes

%JAVA% -cp %CLASS_FILE_PATH% %MAIN% %ARGS%


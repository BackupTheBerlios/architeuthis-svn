@echo off

REM F�hrt das lokale Programm zur Berechnung von Primzahlen aus


REM Ben�tigte Umgebungsvariablen (JAVA, DEPLOY_DIR) werden gesetzt
call setup.bat


REM die Grenzen f�r das Zahlen-Intervall, aus dem die Primzahlen ermittelt
REM werden sollen
set LOWER_BORDER=200000
set UPPER_BORDER=201000

REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% %LOWER_BORDER% %UPPER_BORDER%
set JVMPAR=%JVMPAR% z

REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.testenvironment.GeneratePrimes

%JAVA% -cp %DEPLOY_DIR%/Problems.jar %MAIN% %JVMPAR%


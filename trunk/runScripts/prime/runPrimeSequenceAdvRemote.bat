@echo off

REM Berechnet die Primzahlen unter Verwendung der Klasse ProblemComputation
REM auf dem Compute-System


REM Benötigte Umgebungsvariablen (JAVA, DEPLOY_DIR, CONFIG_DIR) werden gesetzt
call setup.sh

REM die Grenzen für das Zahlen-Intervall, aus dem die Primzahlen ermittelt
REM werden sollen
set LOWER_BORDER=200000
set UPPER_BORDER=201000

REM die Parameter für die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%%/transmitter.pol

REM die Parameter für die Anwendung
set ARGS=
set ARGS=%ARGS% %LOWER_BORDER% %UPPER_BORDER%
set ARGS=%ARGS% n

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.prime.advanced.GeneratePrimes"

exec %JAVA% -cp %DEPLOY_DIR%/User.jar;%DEPLOY_DIR%/Problems.jar %JVMPAR% %MAIN% %ARGS%


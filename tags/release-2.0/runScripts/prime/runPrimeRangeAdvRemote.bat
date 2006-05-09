@echo off

REM Berechnet die Primzahlen unter Verwendung der Klasse ProblemComputation
REM auf dem Compute-System


REM Benötigte Umgebungsvariablen (JAVA, CONFIG_DIR, DEPLOY_DIR,
REM CLASS_FILE_PATH) werden gesetzt
call setup.sh

REM die Grenzen für das Zahlen-Intervall, aus dem die Primzahlen ermittelt
REM werden sollen
set LOWER_BORDER=200000
set UPPER_BORDER=201000

REM die Parameter für die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter für die Anwendung
set ARGS=
set ARGS=%ARGS% %LOWER_BORDER% %UPPER_BORDER%
set ARGS=%ARGS% z

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.prime.advanced.GeneratePrimes"

exec %JAVA% -cp %DEPLOY_DIR%/User.jar;%CLASS_FILE_PATH% %JVMPAR% %MAIN% %ARGS%


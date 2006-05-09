@echo off

REM Berechnet die Primzahlen unter Verwendung der Klasse ProblemComputation
REM lokal


REM Ben�tigte Umgebungsvariablen (JAVA, CLASS_FILE_PATH) werden gesetzt
call setup.sh

REM die Grenzen f�r das Zahlen-Intervall, aus dem die Primzahlen ermittelt
REM werden sollen
set LOWER_BORDER=200000
set UPPER_BORDER=201000

REM die Parameter f�r die JVM
set JVMPAR=

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% %LOWER_BORDER% %UPPER_BORDER%
set ARGS=%ARGS% l

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.prime.basic.GeneratePrimes"

exec %JAVA% -cp %CLASS_FILE_PATH% %JVMPAR% %MAIN% %ARGS%


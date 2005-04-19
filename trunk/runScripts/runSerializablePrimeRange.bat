@echo off

REM Startet den Problem-Transmitter und �bermittelt dem Dispatcher das
REM serialisierte Primzahlbereich-Problem


REM Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
REM DISPATCHER_PORT, SOLUTIONFILE, JAVA, DEPLOY_DIR, CLASS_FILE_PATH) werden
REM gesetzt
call setup.bat


REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% -u %CLASSURL%
set ARGS=%ARGS% -r %DISPATCHER_HOST%:%DISPATCHER_PORT%
set ARGS=%ARGS% -s
set ARGS=%ARGS% -c de.unistuttgart.architeuthis.testenvironment.prime.PrimeRangeProblemImpl
set ARGS=%ARGS% -f %SOLUTIONFILE%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.ProblemTransmitterApp

%JAVA% -cp %DEPLOY_DIR%/User.jar;%CLASS_FILE_PATH%/Problems.jar %JVMPAR% %MAIN% %ARGS%

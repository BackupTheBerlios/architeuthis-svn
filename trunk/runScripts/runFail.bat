@echo off

REM Startet den Problem-Transmitter und �bermittelt dem Dispatcher ein
REM fehlerhaftes Problem


REM Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
REM DISPATCHER_PORT, SOLUTIONFILE, JAVA, DEPLOY_DIR) werden gesetzt
call setup.bat


REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% -u %CLASSURL%
set ARGS=%ARGS% -r %DISPATCHER_HOST%:%DISPATCHER_PORT%
set ARGS=%ARGS% -c de.unistuttgart.architeuthis.testenvironment.fail.FailProblemImpl
REM set ARGS=%ARGS% -f %SOLUTIONFILE%

REM Auf der Kommandozeile k�nnen die Optionen -d und entweder -p oder -n
REM angegeben werden.

REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.facade.ProblemTransmitterApp

%JAVA% -cp %DEPLOY_DIR%/User.jar %JVMPAR% %MAIN% %ARGS% %1 %2

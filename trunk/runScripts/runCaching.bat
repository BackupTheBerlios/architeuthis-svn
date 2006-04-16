@echo off

REM Startet den Problem-Transmitter und übermittelt dem Dispatcher das
REM cachende Primzahlbereich-Problem


REM Benötigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
REM DISPATCHER_PORT, SOLUTIONFILE, JAVA, DEPLOY_DIR) werden gesetzt
call setup.bat


REM die Parameter für die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter für die Anwendung
set ARGS=
set ARGS=%ARGS% -u %CLASSURL%
set ARGS=%ARGS% -r %DISPATCHER_HOST%:%DISPATCHER_PORT%
set ARGS=%ARGS% -c de.unistuttgart.architeuthis.testenvironment.caching.CachingTestProblem
set ARGS=%ARGS% -f %SOLUTIONFILE%

REM Auf der Kommandozeile können die Optionen -d und entweder -p oder -n
REM angegeben werden.

REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.ProblemTransmitterApp

%JAVA% -cp %DEPLOY_DIR%/User.jar %JVMPAR% %MAIN% %ARGS% %1 %2

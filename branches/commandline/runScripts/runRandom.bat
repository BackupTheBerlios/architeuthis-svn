@echo off

REM Startet den Problem-Transmitter und 
REM �bermittelt dem Dispatcher ein zuf�llige Zeit wartendes Problem


REM Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat


REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% -u %CLASSURL%
set ARGS=%ARGS% -r %DISPATCHER_HOST%:%DISPATCHER_PORT%
set ARGS=%ARGS% -c de.unistuttgart.architeuthis.testenvironment.random.RandomProblemImpl
set ARGS=%ARGS% -f %SOLUTIONFILE%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.ProblemTransmitterApp

%JAVA% -cp %DEPLOY_DIR%/User.jar %JVMPAR% %MAIN% %ARGS%

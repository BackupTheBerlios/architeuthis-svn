@echo off

REM Startet den Problem-Transmitter und 
REM �bermittelt dem Dispatcher ein fehlerhaftes Problem

REM Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat


REM die Parameter fuer die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%POLICYDIR%/transmitter.pol

REM die Parameter fuer die Anwendung
set ARGS=
set ARGS=%ARGS% -u %CLASSURL%
set ARGS=%ARGS% -r %DISPATCHER_HOST%:%DISPATCHER_PORT%
set ARGS=%ARGS% -c de.unistuttgart.architeuthis.testenvironment.fail.FailProblemImpl
set ARGS=%ARGS% -f %SOLUTIONFILE%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.ProblemTransmitterApp

%JAVA% -cp ../deploy/User.jar %JVMPAR% %MAIN% %ARGS%

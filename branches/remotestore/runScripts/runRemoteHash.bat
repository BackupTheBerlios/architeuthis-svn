@echo off

REM Startet den Problem-Transmitter und �bermittelt dem Dispatcher
REM ein Problem zum Testen des RemoteStores.


REM Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
REM DISPATCHER_PORT, JAVA) werden gesetzt
call setup.bat


REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% %CLASSURL%
set ARGS=%ARGS% %DISPATCHER_HOST%:%DISPATCHER_PORT%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.testenvironment.hashstore.HashStoreMain

%JAVA% -cp ../build %JVMPAR% %MAIN% %ARGS%

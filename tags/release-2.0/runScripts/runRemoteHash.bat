@echo off

REM Startet den Problem-Transmitter und �bermittelt dem Dispatcher
REM ein Problem zum Testen des RemoteStores.


REM Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
REM DISPATCHER_PORT, JAVA, DEPLOY_DIR, CLASS_FILE_PATH) werden gesetzt
call setup.bat

REM die Anzahl der zu erzeugenden Put-Teilprobleme
set PUT_PAR_PROB_NO=3

REM Nachfolgende Zeile einkommentieren, wenn verteilte RemoteStores verwendet
REM werden sollen. Der Wert true steht f�r synchrone, Wert false f�r
REM asynchrone Methodenaufrufe.
REM DIST_COMM=false


REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/transmitter.pol

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% %CLASSURL%
set ARGS=%ARGS% %DISPATCHER_HOST%:%DISPATCHER_PORT%
set ARGS=%ARGS% %PUT_PAR_PROB_NO%
set ARGS=%ARGS% %DIST_COMM%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.testenvironment.hashstore.HashStoreMain

%JAVA% -cp %DEPLOY_DIR%/User.jar;%CLASS_FILE_PATH% %JVMPAR% %MAIN% %ARGS%

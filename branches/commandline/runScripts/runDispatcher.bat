@echo off

call setup.bat

REM Startet den Dispatcher von Architeuthis - das Programm, das die
REM Verteilung übernimmt.

REM Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat

REM die Main-Klasse ist
REM   de.unistuttgart.architeuthis.dispatcher.DispatcherImpl
REM und wird als Main-Class Sttribut im jar-Manifest definiert
%JAVA% -jar ..\deploy\Dispatcher.jar -Djava.security.policy=%INSTALLDIR%/dispatcher.pol -Djava.rmi.server.RMIClassLoaderSpi=de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi -Djava.util.logging.config.file=%INSTALLDIR%/logging.properties -port %DISPATCHER_PORT%

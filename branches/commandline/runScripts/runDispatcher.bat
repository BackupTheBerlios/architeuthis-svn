@echo off

REM Startet den Dispatcher von Architeuthis - das Programm, das die
REM Verteilung übernimmt.

REM Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat


set POLICY_CONF=dispatcher.pol
set CLASSLOADER_SPI=de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi
set LOGGING_CONF=logging.properties


set JVMPAR= 
set JVMPAR=%JVMPAR% -Djava.security.policy=%POLICYDIR%/%POLICY_CONF%
set JVMPAR=%JVMPAR% -Djava.rmi.server.RMIClassLoaderSpi=%CLASSLOADER_SPI%
set JVMPAR=%JVMPAR% -Djava.util.logging.config.file=%INSTALLDIR%/%LOGGING_CONF%


REM die Main-Klasse ist
REM   de.unistuttgart.architeuthis.dispatcher.DispatcherImpl
REM und wird als Main-Class Attribut im jar-Manifest definiert
%JAVA% %JVMPAR% -jar ..\deploy\Dispatcher.jar -port %DISPATCHER_PORT%

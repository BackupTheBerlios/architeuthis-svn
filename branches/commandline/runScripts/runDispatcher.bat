@echo off

REM Startet den Dispatcher von Architeuthis - das Programm, das die
REM Verteilung �bernimmt.


REM Ben�tigte Umgebungsvariablen (CONFIG_DIR, JAVA, DEPLOY_DIR,
REM DISPATCHER_PORT) werden gesetzt
call setup.bat


set POLICY_CONF=dispatcher.pol
set CLASSLOADER_SPI=de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi
set LOGGING_CONF=logging.properties

REM die Parameter f�r die JVM
set JVMPAR= 
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/%POLICY_CONF%
set JVMPAR=%JVMPAR% -Djava.rmi.server.RMIClassLoaderSpi=%CLASSLOADER_SPI%
set JVMPAR=%JVMPAR% -Djava.util.logging.config.file=%CONFIG_DIR%/%LOGGING_CONF%


REM die Main-Klasse ist
REM   de.unistuttgart.architeuthis.dispatcher.DispatcherImpl
REM und wird als Main-Class Attribut im jar-Manifest definiert

%JAVA% %JVMPAR% -jar %DEPLOY_DIR%\Dispatcher.jar -port %DISPATCHER_PORT%

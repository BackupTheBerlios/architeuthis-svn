@echo off

REM Startet den Operative von Architeuthis - das Programm, das die
REM Berechnung übernimmt.

REM Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat


set POLICY_CONF=operative.pol
set CLASSLOADER_SPI=de.unistuttgart.architeuthis.operative.NonCachinngRMIClSpi
set LOGGING_CONF=operativeLogging.properties


set JVMPAR= 
set JVMPAR=%JVMPAR% -Djava.security.policy=%POLICYDIR%/%POLICY_CONF%
REM set JVMPAR=%JVMPAR% -Djava.rmi.server.RMIClassLoaderSpi=%CLASSLOADER_SPI%
set JVMPAR=%JVMPAR% -Djava.util.logging.config.file=%INSTALLDIR%/%LOGGING_CONF%


REM die Main-Klasse ist
REM   de.unistuttgart.architeuthis.operative.OperativeImpl
REM und wird als Main-Class Attribut im jar-Manifest definiert
%JAVA% %JVMPAR% -jar ..\deploy\Operative.jar %DISPATCHER_HOST%:%DISPATCHER_PORT% -d

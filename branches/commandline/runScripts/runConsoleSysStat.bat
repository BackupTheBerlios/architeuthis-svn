@echo off

REM Startet den ClassFileServer, der dem Dispatcher und den Operatives die
REM Klassen der Anwendung zur Verfügung stellt

REM Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat

POLICY_CONF=statisticreader.pol

set JVMPAR= 
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/%POLICY_CONF%

REM die Main-Klasse 
set MAIN=de.unistuttgart.architeuthis.user.SystemTextStatisticsReader

%JAVA% %JVMPAR% -cp %DEPLOY_DIR%/User.jar %MAIN% %DISPATCHER_HOST%:%DISPATCHER_PORT%

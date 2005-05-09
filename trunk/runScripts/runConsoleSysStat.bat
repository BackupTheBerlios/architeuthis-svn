@echo off

REM Startet die Statistik, um Informationen �ber einen Dispatcher darzustellen


REM Ben�tigte Umgebungsvariablen (CONFIG_DIR, JAVA, DEPLOY_DIR,
REM DISPATCHER_HOST, DISPATCHER_PORT) werden gesetzt
call setup.bat

set POLICY_CONF=statisticreader.pol

REM die Parameter f�r die JVM
set JVMPAR= 
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/%POLICY_CONF%

REM die Main-Klasse 
set MAIN=de.unistuttgart.architeuthis.user.SystemTextStatisticsReader

%JAVA% %JVMPAR% -cp %DEPLOY_DIR%/User.jar %MAIN% %DISPATCHER_HOST%:%DISPATCHER_PORT%

@echo off

REM Startet die graphische Statistik um Informationen über einen Dispatcher
REM darzustellen


REM Benötigte Umgebungsvariablen (CONFIG_DIR, DISPATCHER_HOST,
REM DISPATCHER_PORT, JAVA, DEPLOY_DIR) werden gesetzt
call setup.bat


REM die Parameter für die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/statisticreader.pol

REM die Parameter für die Anwendung
set ARGS=
set ARGS=%ARGS% %DISPATCHER_HOST%:%DISPATCHER_PORT%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.SystemGUIStatisticsReader

%JAVA% -cp %DEPLOY_DIR%/User.jar %JVMPAR% %MAIN% %ARGS%

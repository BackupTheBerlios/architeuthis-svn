@echo off

REM Startet die graphische Statistik um Informationen �ber einen 
REM Dispatcher darzustellen

REM Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
REM werden gesetzt
call setup.bat


REM die Parameter f�r die JVM
set JVMPAR=
set JVMPAR=%JVMPAR% -Djava.security.policy=%CONFIG_DIR%/statisticreader.pol

REM die Parameter f�r die Anwendung
set ARGS=
set ARGS=%ARGS% %DISPATCHER_HOST%:%DISPATCHER_PORT%


REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.SystemGUIStatisticsReader

%JAVA% -cp %DEPLOY_DIR%/User.jar %JVMPAR% %MAIN% %ARGS%

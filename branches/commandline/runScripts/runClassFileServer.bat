@echo off

REM Startet den ClassFileServer, der dem Dispatcher und den Operatives die
REM Klassen der Anwendung zur Verfügung stellt


REM Benötigte Umgebungsvariablen (JAVA, DEPLOY_DIR, CLASS_SERVER_PORT,
REM CLASS_FILE_PATH) werden gesetzt
call setup.bat

REM die Main-Klasse
set MAIN=de.unistuttgart.architeuthis.user.ClassFileServer

%JAVA% -cp %DEPLOY_DIR%/User.jar %MAIN% %CLASS_SERVER_PORT% %CLASS_FILE_PATH%

@echo off

REM Diese Datei dient zum Setzen der Umgebungsvariablen von Architeuthis
REM in den anderen Scripten

REM Der Pfad zum JDK. Wird standardm‰ﬂig als ‰uﬂere Umgebungsvariable gesetzt.
REM Kann hier aber auch explizit gesetzt werden.
REM JAVA_HOME=/usr/java

REM Der Pfad zur JVM (default java aus dem %PATH%)
set JAVA=java


REM Das Verzeichnis, in das Architeuthis installiert wurde
set INSTALLDIR=.

REM Das Verzeichnis mit den policy Dateien
set POLICYDIR=..\policy


REM Der Rechner, auf dem der Dispatcher von Architeuthis l‰uft
set DISPATCHER_HOST=127.0.0.1

REM Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist
set DISPATCHER_PORT=1854



REM Der Pfad, unter dem die Anwendungs-Klassen bzw. deren 
REM aeusserstes Package liegen.
set CLASS_FILE_PATH=..\build

REM Der Port, unter dem der ClassFileServer l‰uft
set CLASS_SERVER_PORT=1855

REM Der Rechner, auf dem der Fileserver laeuft
set CLASS_SERVER_HOST=127.0.0.1

REM URL, unter dem die Problemklassen abrufbar sind
set CLASSURL=http://%CLASS_SERVER_HOST%:%CLASS_SERVER_PORT%/


REM Die Ausgabedatei der Loesung
set SOLUTIONFILE=loesung



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

REM Der Rechner, auf dem der Dispatcher von Architeuthis l‰uft
set DISPATCHER_HOST=127.0.0.1

REM Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist
set DISPATCHER_PORT=1854

REM URL, unter dem die Problemklassen abrufbar sind
REM CLASSURL=http://meinrechner:1855/
set CLASSURL=http://www.iis.uni-stuttgart.de/forschung/RMI-Test/

REM Die Ausgabedatei der Lˆsung
set SOLUTIONFILE=loesung

REM Der Port, unter dem der ClassFileServer l‰uft
set CLASS_SERVER_PORT=1855

REM Der Pfad, unter dem die Anwendungs-Klassen bzw. deren ‰uﬂerstes Package
REM liegen. Wird standardm‰ﬂig als ‰uﬂere Umgebungsvariable gesetzt.
REM CLASS_FILE_PATH=.


@echo off

REM Diese Datei dient zum Setzen der Umgebungsvariablen von Architeuthis
REM in den anderen Scripten

REM Der Pfad zum JDK. Wird standardmäßig als äußere Umgebungsvariable gesetzt.
REM Kann hier aber auch explizit gesetzt werden.
REM JAVA_HOME=/usr/java

REM Der Pfad zur JVM (default java aus dem %PATH%)
set JAVA=java

REM Das Verzeichnis mit den policy-, properties- und config-Dateien
set CONFIG_DIR=..\config

REM Das Verzeichnis mit den jar-Dateien für die einzelnen Komponenten
set DEPLOY_DIR=..\deploy

REM Der Pfad, unter dem die Anwendungs-Klassen bzw. deren äußerstes
REM Package liegen. Wird für den Classfileserver verwendet und normalerweise
REM als äußere Umgebungsvariable gesetzt. Man kann sie nachfolgend aber auch
REM hier setzen.
REM set CLASS_FILE_PATH=..\classes

REM Der Rechner, auf dem der Dispatcher von Architeuthis läuft
set DISPATCHER_HOST=127.0.0.1

REM Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist
set DISPATCHER_PORT=1854

REM Der Rechner, auf dem der Fileserver läuft
set CLASS_SERVER_HOST=127.0.0.1

REM Der Port, unter dem der ClassFileServer läuft
set CLASS_SERVER_PORT=1855

REM URL, unter dem die Problemklassen abrufbar sind
set CLASSURL=http://%CLASS_SERVER_HOST%:%CLASS_SERVER_PORT%/

REM Die Ausgabedatei der Lösung
set SOLUTIONFILE=loesung


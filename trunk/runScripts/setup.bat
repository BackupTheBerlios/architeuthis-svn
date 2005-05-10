@echo off

REM Diese Datei dient zum Setzen der Umgebungsvariablen von Architeuthis
REM in den anderen Scripten. Alle relativen Pfade beziehen sich auf das
REM Verzeichnis runScripts.


REM Der Pfad zum JDK. Wird standardmäßig als äußere Umgebungsvariable gesetzt.
REM Kann hier aber auch explizit gesetzt werden.
REM set JAVA_HOME=/usr/java

REM Der Pfad zur JVM (default java aus dem %PATH%)
set JAVA=java
if not "%JAVA_HOME%"=="" set JAVA=%JAVA_HOME%/bin/java

REM Das Verzeichnis mit den policy-, properties- und config-Dateien.
set CONFIG_DIR=..\config

REM Das Verzeichnis mit den jar-Dateien für die einzelnen Komponenten.
set DEPLOY_DIR=..\deploy

REM Der Pfad, unter dem die Anwendungs-Klassen bzw. deren äußerstes
REM Package liegen. Wird für den Classfileserver verwendet und normalerweise
REM als äußere Umgebungsvariable gesetzt.
if "%CLASS_FILE_PATH%"=="" set CLASS_FILE_PATH=..\classes


REM Der Rechner, auf dem der Dispatcher von Architeuthis läuft.
set DISPATCHER_HOST=127.0.0.1

REM Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist.
set DISPATCHER_PORT=1854

REM Der Rechner, auf dem der Fileserver läuft. Es muß ein Name angegeben
REM werden, unter dem der Rechner von allen Operatives erreichbar ist.
REM CLASS_SERVER_HOST=rechner.meine-domain.de
set CLASS_SERVER_HOST=hostname

REM Der Port, unter dem der ClassFileServer läuft.
set CLASS_SERVER_PORT=1855

REM URL, unter dem die Problemklassen abrufbar sind. Der URL muß für alle
REM Operatives gültig sein.
REM set CLASSURL=http://meinrechner:1855/
REM set CLASSURL=http://meinrechner:1855/projekt.jar
REM set CLASSURL=http://www.meine-domain.de/java/projekt/
set CLASSURL=http://%CLASS_SERVER_HOST%:%CLASS_SERVER_PORT%/

REM Die Ausgabedatei der Lösung.
set SOLUTIONFILE=loesung


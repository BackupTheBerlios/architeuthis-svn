# Diese Datei dient zum Setzen der Umgebungsvariablen von Architeuthis
# in den anderen Scripten

# Der Pfad zum JDK. Wird standardmäßig als äußere Umgebungsvariable gesetzt.
# Kann hier aber auch explizit gesetzt werden.
# JAVA_HOME=/usr/java

# Der Pfad zur JVM
JAVA="$JAVA_HOME/bin/java"

# Das Verzeichnis, in das Architeuthis installiert wurde
INSTALLDIR="."

# Das Verzeichnis mit den policy-, properties- und config-Dateien
CONFIG_DIR="../config"

# Das Verzeichnis mit den jar-Dateien für die einzelnen Komponenten
DEPLOY_DIR="../deploy"


# Der Rechner, auf dem der Dispatcher von Architeuthis läuft
#DISPATCHER_HOST=isny.informatik.uni-stuttgart.de
DISPATCHER_HOST="127.0.0.1"

# Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist
DISPATCHER_PORT="1854"


# Der Pfad, unter dem die Anwendungs-Klassen bzw. deren äußerstes
# Package liegen, wird für den Classfileserver verwendet.
CLASS_FILE_PATH="../build"

# Der Port, unter dem der ClassFielServer läuft
CLASS_SERVER_PORT="1855"

# Der Rechner, auf dem der Fileserver läuft
CLASS_SERVER_HOST="127.0.0.1"

# URL, unter dem die Problemklassen abrufbar sind
#CLASSURL=http://meinrechner:1855/
#CLASSURL=http://www.iis.uni-stuttgart.de/forschung/RMI-Test/
CLASSURL="http://${CLASS_SERVER_HOST}:${CLASS_SERVER_PORT}/"


# Die Ausgabedatei der Lösung
SOLUTIONFILE="loesung"



# Diese Datei dient zum Setzen der Umgebungsvariablen von Architeuthis
# in den anderen Scripten. Alle relativen Pfade beziehen sich auf das
# Verzeichnis runScripts.


# Der Pfad zum JDK. Wird standardmäßig als äußere Umgebungsvariable gesetzt.
# Kann hier aber auch explizit gesetzt werden.
# JAVA_HOME=/usr/java

# Der Pfad zur JVM.
if [ -n "$JAVA_HOME" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA="java"
fi

# Das Verzeichnis mit den policy-, properties- und config-Dateien.
CONFIG_DIR="../../config"

# Das Verzeichnis mit den jar-Dateien für die einzelnen Komponenten.
DEPLOY_DIR="../../deploy"

# Der Pfad, unter dem die Anwendungs-Klassen bzw. deren äußerstes
# Package liegen. Wird für den Classfileserver verwendet und normalerweise
# als äußere Umgebungsvariable gesetzt.
if [ ! -n "$CLASS_FILE_PATH" ]; then
  CLASS_FILE_PATH="../../classes"
fi

# Der Rechner, auf dem der Dispatcher von Architeuthis läuft.
#DISPATCHER_HOST=isny.informatik.uni-stuttgart.de
DISPATCHER_HOST="127.0.0.1"

# Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist.
DISPATCHER_PORT="1854"

# Der Rechner, auf dem der Fileserver läuft. Es muß ein Name angegeben
# werden, unter dem der Rechner von allen Operatives erreichbar ist.
#CLASS_SERVER_HOST="rechner.meine-domain.de"
CLASS_SERVER_HOST=`hostname`

# Der Port, unter dem der ClassFielServer läuft.
CLASS_SERVER_PORT="1855"

# URL, unter dem die Problemklassen abrufbar sind. Der URL muß für alle
# Operatives gültig sein.
#CLASSURL=http://meinrechner:1855/
#CLASSURL=http://meinrechner:1855/projekt.jar
#CLASSURL=http://www.meine-domain.de/java/projekt/
CLASSURL="http://${CLASS_SERVER_HOST}:${CLASS_SERVER_PORT}/"

# Die Ausgabedatei der Lösung.
SOLUTIONFILE="loesung"


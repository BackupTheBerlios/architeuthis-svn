# Diese Datei dient zum Setzen der Umgebungsvariablen von Architeuthis
# in den anderen Scripten

# Der Pfad zum JDK. Wird standardm‰ﬂig als ‰uﬂere Umgebungsvariable gesetzt.
# Kann hier aber auch explizit gesetzt werden.
# JAVA_HOME=/usr/java

# Der Pfad zur JVM
JAVA=$JAVA_HOME/bin/java

# Das Verzeichnis, in das Architeuthis installiert wurde
INSTALLDIR=.

# Der Rechner, auf dem der Dispatcher von Architeuthis l‰uft
DISPATCHER_HOST=isny.informatik.uni-stuttgart.de

# Der Port, unter dem der Dispatcher von Architeuthis erreichbar ist
DISPATCHER_PORT=1854

# URL, unter dem die Problemklassen abrufbar sind
# CLASSURL=http://meinrechner:1855/
CLASSURL=http://www.iis.uni-stuttgart.de/forschung/RMI-Test/

# Die Ausgabedatei der Lˆsung
SOLUTIONFILE=loesung

# Der Port, unter dem der ClassFileServer l‰uft
CLASS_SERVER_PORT=1855

# Der Pfad, unter dem die Anwendungs-Klassen bzw. deren ‰uﬂerstes Package
# liegen. Wird standardm‰ﬂig als ‰uﬂere Umgebungsvariable gesetzt.
# CLASS_FILE_PATH=.


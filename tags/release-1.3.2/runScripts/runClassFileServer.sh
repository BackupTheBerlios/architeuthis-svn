#!/bin/sh

# Startet den ClassFileServer, der dem Dispatcher und den Operatives die
# Klassen der Anwendung zur Verfügung stellt

# Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, CLASS_SERVER_PORT, CLASS_FILE_PATH)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/User.jar de.unistuttgart.architeuthis.user.ClassFileServer $CLASS_SERVER_PORT $CLASS_FILE_PATH


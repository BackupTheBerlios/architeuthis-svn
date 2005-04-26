#!/bin/sh

# Startet den ClassFileServer, der dem Dispatcher und den Operatives die
# Klassen der Anwendung zur Verfügung stellt


# Benötigte Umgebungsvariablen (JAVA, DEPLOY_DIR, CLASS_SERVER_PORT,
# CLASS_FILE_PATH) werden gesetzt
. ./setup.sh

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.user.ClassFileServer"

exec $JAVA -cp $DEPLOY_DIR/User.jar $MAIN $CLASS_SERVER_PORT $CLASS_FILE_PATH


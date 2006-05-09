#!/bin/sh

# Startet den ClassFileServer, der dem Dispatcher und den Operatives die
# Klassen der Anwendung zur Verfügung stellt


# Benötigte Umgebungsvariablen (JAVA, DEPLOY_DIR, CLASS_SERVER_PORT,
# CLASS_FILE_PATH) werden gesetzt
. ./setup.sh

LOGGING_CONF=logging.properties

# die Parameter für die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.util.logging.config.file=$CONFIG_DIR/$LOGGING_CONF"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.facade.ClassFileServer"

exec $JAVA $JVMPAR -cp $DEPLOY_DIR/User.jar $MAIN $CLASS_SERVER_PORT $CLASS_FILE_PATH


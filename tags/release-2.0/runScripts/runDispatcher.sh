#!/bin/sh

# Startet den Dispatcher von Architeuthis - das Programm, das die
# Verteilung übernimmt.


# Benötigte Umgebungsvariablen (CONFIG_DIR, JAVA, DEPLOY_DIR,
# DISPATCHER_PORT) werden gesetzt
. ./setup.sh

POLICY_CONF=dispatcher.pol
CLASSLOADER_SPI=de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi
LOGGING_CONF=logging.properties

# die Parameter für die JVM
JVMPAR=" " 
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/$POLICY_CONF"
JVMPAR="$JVMPAR -Djava.rmi.server.RMIClassLoaderSpi=$CLASSLOADER_SPI"
JVMPAR="$JVMPAR -Djava.util.logging.config.file=$CONFIG_DIR/$LOGGING_CONF"

# die Parameter für die Anwendung
ARGS=" "
ARGS="$ARGS -port $DISPATCHER_PORT"

# Auf der Kommandozeile können die Optionen -d und -t oder angegeben werden.

# die Main-Klasse ist
#   de.unistuttgart.architeuthis.dispatcher.DispatcherImpl
# und wird als Main-Class Attribut im jar-Manifest definiert

exec $JAVA $JVMPAR -jar $DEPLOY_DIR/Dispatcher.jar $ARGS $@


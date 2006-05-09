#!/bin/sh

# Startet den Operative von Architeuthis - das Programm, das die
# haupts�chliche Berechnungsarbeit durchf�hrt.


# Ben�tigte Umgebungsvaribalen (CONFIG_DIR, JAVA, DEPLOY_DIR,
# DISPATCHER_HOST, DISPATCHER_PORT) werden gesetzt
. ./setup.sh

POLICY_CONF="operative.pol"
CLASSLOADER_SPI="de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi"
LOGGING_CONF="logging.properties"

# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/$POLICY_CONF"
JVMPAR="$JVMPAR -Djava.rmi.server.RMIClassLoaderSpi=$CLASSLOADER_SPI"
JVMPAR="$JVMPAR -Djava.util.logging.config.file=$CONFIG_DIR/$LOGGING_CONF"

# Name und Port vom Dispatcher zusammenfassen
DISPATCHER="$DISPATCHER_HOST:$DISPATCHER_PORT"

# die Parameter f�r die Anwendung
ARGS=" "

# Auf der Kommandozeile kann die Option -d zum Aktivieren des Debugging
# angegeben werden.

# die Main-Klasse ist
#   de.unistuttgart.architeuthis.operative.OperativeImpl
# und wird als Main-Class Attribut im jar-Manifest definiert

exec $JAVA $JVMPAR -jar $DEPLOY_DIR/Operative.jar $DISPATCHER $ARGS $@


#!/bin/sh

# Startet den Operative von Architeuthis - das Programm, das die
# hauptsächliche Berechnungsarbeit durchführt.


# Benötigte Umgebungsvaribalen (CONFIG_DIR, JAVA, DEPLOY_DIR,
# DISPATCHER_HOST, DISPATCHER_PORT) werden gesetzt
. ./setup.sh

POLICY_CONF="operative.pol"
CLASSLOADER_SPI="de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi"
LOGGING_CONF="logging.properties"

# die Parameter für die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/$POLICY_CONF"
# die Verwendung des folgenden Parameters führt bei gleichzeitig
# mehreren Problemen auf den Dispatcher gelegentlich zu einer Exception
#JVMPAR="$JVMPAR -Djava.rmi.server.RMIClassLoaderSpi=$CLASSLOADER_SPI"
JVMPAR="$JVMPAR -Djava.util.logging.config.file=$CONFIG_DIR/$LOGGING_CONF"

# die Parameter für die Anwendung
ARGS=" "
ARGS="$ARGS -d"

# die Main-Klasse ist
#   de.unistuttgart.architeuthis.operative.OperativeImpl
# und wird als Main-Class Attribut im jar-Manifest definiert

exec $JAVA $JVMPAR -jar $DEPLOY_DIR/Operative.jar $DISPATCHER_HOST:$DISPATCHER_PORT $ARGS


#!/bin/sh

# Startet den Operative von Architeuthis - das Programm, das die
# hauptsächliche Berechnungsarbeit durchführt.


# Benötigte Umgebungsvaribalen (CONFIG_DIR, JAVA, DEPLOY_DIR,
# DISPATCHER_HOST, DISPATCHER_PORT) werden gesetzt
. ./setup.sh

POLICY_CONF="operative.pol"
CLASSLOADER_SPI="de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi"
LOGGING_CONF="logging.properties"

JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/$POLICY_CONF"
JVMPAR="$JVMPAR -Djava.rmi.server.RMIClassLoaderSpi=$CLASSLOADER_SPI"
JVMPAR="$JVMPAR -Djava.util.logging.config.file=$CONFIG_DIR/$LOGGING_CONF"

# die Main-Klasse ist
#   de.unistuttgart.architeuthis.operative.OperativeImpl
# und wird als Main-Class Attribut im jar-Manifest definiert

exec $JAVA $JVMPAR -jar $DEPLOY_DIR/Operative.jar $DISPATCHER_HOST:$DISPATCHER_PORT -d


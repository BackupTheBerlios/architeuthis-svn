#!/bin/sh

# Startet die Statistik um Informationen �ber einen 
# Dispatcher darzustellen

# Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT)
# werden gesetzt
. ./setup.sh


POLICY_CONF="statisticreader.pol"

JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/$POLICY_CONF"

# die Main-Klasse 
MAIN="de.unistuttgart.architeuthis.user.SystemTextStatisticsReader"
exec $JAVA $JVMPAR -cp $DEPLOY_DIR/User.jar $MAIN $DISPATCHER_HOST:$DISPATCHER_PORT

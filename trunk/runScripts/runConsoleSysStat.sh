#!/bin/sh

# Startet die Statistik, um Informationen �ber einen Dispatcher darzustellen

# Ben�tigte Umgebungsvariablen (CONFIG_DIR, JAVA, DEPLOY_DIR,
# DISPATCHER_HOST, DISPATCHER_PORT) werden gesetzt
. ./setup.sh

POLICY_CONF="statisticreader.pol"

# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/$POLICY_CONF"

# die Main-Klasse 
MAIN="de.unistuttgart.architeuthis.user.SystemTextStatisticsReader"

exec $JAVA $JVMPAR -cp $DEPLOY_DIR/User.jar $MAIN $DISPATCHER_HOST:$DISPATCHER_PORT


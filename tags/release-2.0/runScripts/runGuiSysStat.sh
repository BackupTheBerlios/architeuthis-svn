#!/bin/sh

# Startet die graphische Statistik, um Informationen �ber einen Dispatcher
# darzustellen


# Ben�tigte Umgebungsvariablen (CONFIG_DIR, DISPATCHER_HOST,
# DISPATCHER_PORT, JAVA, DEPLOY_DIR) werden gesetzt
. ./setup.sh

# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/statisticreader.pol"

# die Parameter f�r die Anwendung
ARGS=" "
ARGS="$ARGS $DISPATCHER_HOST:$DISPATCHER_PORT"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.facade.SystemGUIStatisticsReader"

exec $JAVA -cp $DEPLOY_DIR/User.jar $JVMPAR $MAIN $ARGS


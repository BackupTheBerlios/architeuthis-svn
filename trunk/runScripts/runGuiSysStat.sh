#!/bin/sh

# Startet die graphische Statistik, um Informationen über einen Dispatcher
# darzustellen


# Benötigte Umgebungsvariablen (CONFIG_DIR, DISPATCHER_HOST,
# DISPATCHER_PORT, JAVA, DEPLOY_DIR) werden gesetzt
. ./setup.sh

# die Parameter für die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/statisticreader.pol"

# die Parameter für die Anwendung
ARGS=" "
ARGS="$ARGS $DISPATCHER_HOST:$DISPATCHER_PORT"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.facade.SystemGUIStatisticsReader"

exec $JAVA -cp $DEPLOY_DIR/User.jar $JVMPAR $MAIN $ARGS


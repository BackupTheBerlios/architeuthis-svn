#!/bin/sh

# Startet die graphische Statistik um Informationen �ber einen 
# Dispatcher darzustellen

# Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT)
# werden gesetzt
. ./setup.sh


# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/statisticreader.pol"

# die Parameter f�r die Anwendung
ARGS=" "
ARGS="$ARGS $DISPATCHER_HOST:$DISPATCHER_PORT"


# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.user.SystemGUIStatisticsReader"

exec $JAVA -cp $DEPLOY_DIR/User.jar $JVMPAR $MAIN $ARGS

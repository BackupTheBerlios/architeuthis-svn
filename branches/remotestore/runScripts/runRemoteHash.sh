#!/bin/sh

# Startet den Problem-Transmitter und �bermittelt dem Dispatcher ein
# Problem zum Testen des RemoteStores.


# Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
# DISPATCHER_PORT, JAVA) werden gesetzt
. ./setup.sh


# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/transmitter.pol"

# die Parameter f�r die Anwendung
ARGS=" "
ARGS="$ARGS $CLASSURL"
ARGS="$ARGS $DISPATCHER_HOST:$DISPATCHER_PORT"


# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.hashstore.HashStoreMain"

exec $JAVA  -cp ../build $JVMPAR $MAIN $ARGS


#!/bin/sh

# Startet den Dispatcher von Architeuthis - das Programm, das die
# Verteilung übernimmt.

# Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_PORT)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/Dispatcher.jar -Djava.security.policy=$INSTALLDIR/dispatcher.pol -Djava.rmi.server.RMIClassLoaderSpi=de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi -Djava.util.logging.config.file=$INSTALLDIR/logging.properties de.unistuttgart.architeuthis.dispatcher.DispatcherImpl -port $DISPATCHER_PORT

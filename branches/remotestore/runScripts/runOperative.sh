#!/bin/sh

# Startet den Operative von Architeuthis - das Programm, das die
# hauptsähliche Berechnungsarbeit durchführt.

# Benötigte Umgebungsvaribalen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/Operative.jar -Djava.security.policy=$INSTALLDIR/operative.pol -Djava.rmi.server.RMIClassLoaderSpi=de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi de.unistuttgart.architeuthis.operative.OperativeImpl $DISPATCHER_HOST:$DISPATCHER_PORT --debug

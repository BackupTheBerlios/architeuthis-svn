#!/bin/sh

# Startet die Statistik um Informationen �ber einen 
# Dispatcher darzustellen

# Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/User.jar -Djava.security.policy=$INSTALLDIR/statisticreader.pol de.unistuttgart.architeuthis.user.SystemTextStatisticsReader $DISPATCHER_HOST:$DISPATCHER_PORT

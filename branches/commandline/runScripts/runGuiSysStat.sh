#!/bin/sh

# Startet die graphische Statistik um Informationen über einen 
# Dispatcher darzustellen

# Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/User.jar -Djava.security.policy=$INSTALLDIR/statisticreader.pol de.unistuttgart.architeuthis.user.SystemGUIStatisticsReader $DISPATCHER_HOST:$DISPATCHER_PORT

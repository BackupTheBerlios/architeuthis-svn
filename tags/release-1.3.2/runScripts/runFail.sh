#!/bin/sh

# Startet den Problem-Transmitter und 
# �bermittelt dem Dispatcher ein fehlerhaftes Problem

# Ben�tigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT, CLASSURL, SOLUTIONFILE)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/User.jar -Djava.security.policy=$INSTALLDIR/transmitter.pol de.unistuttgart.architeuthis.user.ProblemTransmitterApp -u $CLASSURL -r $DISPATCHER_HOST:$DISPATCHER_PORT -c de.unistuttgart.architeuthis.testenvironment.fail.FailProblemImpl -f $SOLUTIONFILE

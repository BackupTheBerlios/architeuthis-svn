#!/bin/sh

# Startet den Problem-Transmitter und 
# übermittelt dem Dispatcher das Monta-Carlo-Problem

# Benötigte Umgebungsvariablen (JAVA, INSTALLDIR, DISPATCHER_HOST, DISPATCHER_PORT, CLASSURL, SOLUTIONFILE)
# werden gesetzt
source setup.sh

exec $JAVA -cp $INSTALLDIR/User.jar -Djava.security.policy=$INSTALLDIR/transmitter.pol de.unistuttgart.architeuthis.user.ProblemTransmitterApp -u $CLASSURL -r $DISPATCHER_HOST:$DISPATCHER_PORT -c de.unistuttgart.architeuthis.testenvironment.montecarlo.MonteCarloProblemImpl -f $SOLUTIONFILE

#!/bin/sh

# Startet den Problem-Transmitter und �bermittelt dem Dispatcher das
# serialisierte Primzahlfolge-Problem


# Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
# DISPATCHER_PORT, SOLUTIONFILE, JAVA, DEPLOY_DIR) werden gesetzt
. ./setup.sh

# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/transmitter.pol"

# die Parameter f�r die Anwendung
ARGS=" "
ARGS="$ARGS -u $CLASSURL"
ARGS="$ARGS -r $DISPATCHER_HOST:$DISPATCHER_PORT"
ARGS="$ARGS -s"
ARGS="$ARGS -c de.unistuttgart.architeuthis.testenvironment.prime.PrimeSequenceProblemImpl"
ARGS="$ARGS -f $SOLUTIONFILE"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.user.ProblemTransmitterApp"

exec $JAVA -cp $DEPLOY_DIR/User.jar:$CLASS_FILE_PATH/Problems.jar $JVMPAR $MAIN $ARGS


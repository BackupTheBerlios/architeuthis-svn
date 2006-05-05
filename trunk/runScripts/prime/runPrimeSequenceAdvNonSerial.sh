#!/bin/sh

# Startet den Problem-Transmitter und �bermittelt dem Dispatcher das
# Primzahlfolge-Problem


# Ben�tigte Umgebungsvariablen (CONFIG_DIR, CLASSURL, DISPATCHER_HOST,
# DISPATCHER_PORT, SOLUTIONFILE, JAVA, DEPLOY_DIR) werden gesetzt
. ./setup.sh

# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/transmitter.pol"
JVMPAR="$JVMPAR -Djava.util.logging.config.file=$CONFIG_DIR/logging.properties"

# die Parameter f�r die Anwendung
ARGS=" "
ARGS="$ARGS -u $CLASSURL"
ARGS="$ARGS -r $DISPATCHER_HOST:$DISPATCHER_PORT"
ARGS="$ARGS -c de.unistuttgart.architeuthis.testenvironment.prime.advanced.PrimeSequenceProblemImpl"
# ARGS="$ARGS -f $SOLUTIONFILE"

# Auf der Kommandozeile k�nnen die Optionen -d und entweder -p oder -n
# angegeben werden.

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.facade.ProblemTransmitterApp"

exec $JAVA -cp $DEPLOY_DIR/User.jar $JVMPAR $MAIN $ARGS $@


#!/bin/sh

# Berechnet die Primzahlen unter Verwendung der Klasse ProblemComputation
# auf dem Compute-System


# Benötigte Umgebungsvariablen (JAVA, CONFIG_DIR, DEPLOY_DIR,
# CLASS_FILE_PATH) werden gesetzt
. ./setup.sh

# die Grenzen für das Zahlen-Intervall, aus dem die Primzahlen ermittelt
# werden sollen
LOWER_BORDER="200000"
UPPER_BORDER="200000"

# die Parameter für die JVM
JVMPAR=" "
JVMPAR="$JVMPAR -Djava.security.policy=$CONFIG_DIR/transmitter.pol"

# die Parameter für die Anwendung
ARGS=" "
ARGS="$ARGS $LOWER_BORDER $UPPER_BORDER"
ARGS="$ARGS n"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.prime.advanced.GeneratePrimes"

exec $JAVA -cp $DEPLOY_DIR/User.jar:$CLASS_FILE_PATH $JVMPAR $MAIN $ARGS


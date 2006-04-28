#!/bin/sh

# Berechnet die Primzahlen unter Verwendung der Klasse ProblemComputation
# lokal


# Ben�tigte Umgebungsvariablen (JAVA, CLASS_FILE_PATH) werden gesetzt
. ./setup.sh

# die Grenzen f�r das Zahlen-Intervall, aus dem die Primzahlen ermittelt
# werden sollen
LOWER_BORDER="200000"
UPPER_BORDER="201000"

# die Parameter f�r die JVM
JVMPAR=" "

# die Parameter f�r die Anwendung
ARGS=" "
ARGS="$ARGS $LOWER_BORDER $UPPER_BORDER"
ARGS="$ARGS l"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.prime.basic.GeneratePrimes"

exec $JAVA -cp $CLASS_FILE_PATH $JVMPAR $MAIN $ARGS


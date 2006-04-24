#!/bin/sh

# Führt das lokale Programm zur Berechnung von Primzahlen aus


# Benötigte Umgebungsvariablen (JAVA, DEPLOY_DIR) werden gesetzt
. ./setup.sh

# die Grenzen für das Zahlen-Intervall, aus dem die Primzahlen ermittelt
# werden sollen
LOWER_BORDER="200000"
UPPER_BORDER="201000"

# die Parameter für die JVM
JVMPAR=" "
JVMPAR="$JVMPAR $LOWER_BORDER $UPPER_BORDER"
JVMPAR="$JVMPAR z"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.GeneratePrimes"

exec $JAVA -cp $DEPLOY_DIR/Problems.jar $MAIN $JVMPAR


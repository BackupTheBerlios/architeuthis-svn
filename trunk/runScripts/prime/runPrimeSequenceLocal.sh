#!/bin/sh

# F�hrt das lokale Programm zur Berechnung von Primzahlen aus


# Ben�tigte Umgebungsvariablen (JAVA, DEPLOY_DIR) werden gesetzt
. ./setup.sh

# die Grenzen f�r das Nummern-Intervall, aus dem die Primzahlen ermittelt
# werden sollen
LOWER_BORDER="2000000"
UPPER_BORDER="2000000"

# die Parameter f�r die JVM
JVMPAR=" "
JVMPAR="$JVMPAR $LOWER_BORDER $UPPER_BORDER"
JVMPAR="$JVMPAR n"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.GeneratePrimes"

exec $JAVA -cp $DEPLOY_DIR/Problems.jar $MAIN $JVMPAR


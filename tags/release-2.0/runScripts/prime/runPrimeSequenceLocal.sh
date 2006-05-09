#!/bin/sh

# F�hrt das lokale Programm zur Berechnung von Primzahlen aus


# Ben�tigte Umgebungsvariablen (JAVA, CLASS_FILE_PATH) werden gesetzt
. ./setup.sh

# die Grenzen f�r das Nummern-Intervall, aus dem die Primzahlen ermittelt
# werden sollen
LOWER_BORDER="2000000"
UPPER_BORDER="2000000"

# die Parameter f�r die JVM
ARGS=" "
ARGS="$ARGS $LOWER_BORDER $UPPER_BORDER"
ARGS="$ARGS n"

# die Main-Klasse
MAIN="de.unistuttgart.architeuthis.testenvironment.GeneratePrimes"

exec $JAVA -cp $CLASS_FILE_PATH $MAIN $ARGS


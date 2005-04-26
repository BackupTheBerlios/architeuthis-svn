#!/bin/sh

# Startet zwei Programme und beendet sie, sobald dieses Startprogramm
# beendet wird.
# Das Programm ist insbesondere zum Aufruf durch dem GDM gedacht. Als eines
# der beiden Programme ist dafür der Operative von Architeuthis vorgegeben,
# als das andere Programm kann das angegeben werden, das schon derzeit beim
# GDM im Hintergrund läuft.
#
# Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de

# Installationsverzeichnis vom Operative von Architeuthis
INSTALLDIR=/usr/local/cmd/Architeuthis

trap 'kill $PID1 $PID2 2>/dev/null ; exit ' 1 2 3 15

# Nachfolgend steht der Aufruf vom Operative von Architeuthis
$INSTALLDIR/runoperative.sh  > /dev/null &
PID1=$!

# In der folgenden Zeile kann das Programm angegeben werden, das schon
# derzeit beim GDM im Hintergrund läuft. Dies wird in der Datei
# /etc/X11/gdm/gdm.conf der Variablen BackgroundProgram zugewiesen.
# Z.B. kann dort stehen:
# /usr/bin/xpenguins -t "Big Penguins" -b -s &

PID2=$!

wait

kill $PID1 $PID2 2>/dev/null

exit


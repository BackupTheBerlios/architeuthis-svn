/*
 * file:        InfoParProbWrapperQueue.java
 * created:     12.7.2003
 * last change: 05.03.2006 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *              Michael Wohlfart,  michael.wohlfart@zsw-bw.de
 *
 *
 * This file is part of Architeuthis.
 *
 * Architeuthis is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Architeuthis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Architeuthis; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.dispatcher.computemanaging;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.unistuttgart.architeuthis.dispatcher.problemmanaging.ParProbWrapper;

/**
 * Die Klasse verwaltet alle Teilproblem-Wrapper-Info-Objekte, die vom
 * ComputeManager berechnet werden.
 *
 * @author Andreas Heydlauff, Jürgen Heit, Dietmar Lippold
 */
class InfoParProbWrapperQueue {

    /**
     * Logger für diese Klasse.
     */
    private static final Logger LOGGER
        = Logger.getLogger(InfoParProbWrapperQueue.class.getName());

    /**
     * Alle Teilproblem-Info-Objekte, die erzeugt wurden und nicht fertig
     * berechnet sind.
     */
    private List partProbsInComputation = new LinkedList();

    /**
     * Teilproblem-Info-Objekte, die bevorzugt bei der Vergabe zu behandlen
     * sind, da sie aus beliebigen Gründen nicht berechnet werden.
     */
    private List preferedPartProbs = new LinkedList();

    /**
     * Reiht ein Teilproblem-Info-Objekt in die Queue aller in Berechnung
     * befindlichen Teilproblem-Info-Objekte am Ende ein.
     *
     * @param partProbInf  hinzuzufügendes Teilproblem.
     */
    synchronized void enqueuePartProbInfo(InfoParProbWrapper partProbInf) {
        if (partProbInf != null) {
            partProbsInComputation.add(partProbInf);
        }
    }

    /**
     * Vermerkt, dass ein Teilproblem-Info-Objekt abgebrochen wurde. Hierzu
     * wird das Teilproblem-Info-Objekt aus der <code>Collection</code> der in
     * Berechnung befindlichen Teilprobleme entfernt und zur
     * <code>Collection</code> der abgebrochenen Teilprobleme hinzugefügt.
     *
     * @param partProbInf  abgebrochenes Teilproblem
     */
    synchronized void setAborted(InfoParProbWrapper partProbInf) {
        if (partProbsInComputation.contains(partProbInf)) {
            partProbsInComputation.remove(partProbInf);
            preferedPartProbs.add(partProbInf);
        }
    }

    /**
     * Holt das vorderste Teilproblem-Info-Objekt aus der Schlange aller
     * Teilproblem-Info-Objekte heraus und entfernt dieses aus der Schlange.
     *
     * @return  vorderstes Teilproblem.
     */
    private InfoParProbWrapper dequeuePartProbInfo() {
        if (partProbsInComputation.isEmpty()) {
            return null;
        } else {
            InfoParProbWrapper partProbInfoObj =
                (InfoParProbWrapper) partProbsInComputation.remove(0);
            return partProbInfoObj;
        }
    }

    /**
     * Gibt das vorderste Teilproblem-Info-Objekt aus der Schlange aller
     * Teilproblem-Info-Objekte zurück und verschiebt es an das Ende der
     * Schlange.
     *
     * @return  das vorderste Teilproblem.
     */
    synchronized InfoParProbWrapper rotatePartProbInfo() {
        InfoParProbWrapper partProbInf = dequeuePartProbInfo();
        enqueuePartProbInfo(partProbInf);
        return partProbInf;
    }

    /**
     * Macht eine Rotation der Queue der Teilprobleme, die sich in Berechnung
     * befinden, rückgängig. Hierzu wird das {@link InfoParProbWrapper}-Objekt
     * an der Stelle entfernt, an der es sich gerade in der Queue befindet, und
     * am Kopf der Queue wieder eingefügt.
     *
     * @param partProbInfo  Teilproblem-Info-Objekt, das an den Kopf der Queue
     *                      gebracht werden soll.
     */
    synchronized void undoRotation(InfoParProbWrapper partProbInfo) {
        boolean inList = partProbsInComputation.remove(partProbInfo);
        // das Teilproblem darf nur dann am Anfang der Liste eingefügt werden,
        // wenn es vorher tatsächlich entfernt wurde
        if (inList) {
            partProbsInComputation.add(0, partProbInfo);
        }
    }

    /**
     * Entfernt ein fertiges Teilproblem-Info-Objekt aus der Verwaltung und
     * liefert <code>true</code> zurück, falls das Teilproblem-Infoobjekt noch
     * vorhanden war; sonst <code>false</code>
     *
     * @param partProbInf  fertiges Teilproblem.
     * @return  <code>true</code>, falls das Teilproblem-Infoobjekt entfernt
     *          wurde.
     */
    synchronized boolean removePartProbInfo(InfoParProbWrapper partProbInf) {
        return (partProbsInComputation.remove(partProbInf)
                || preferedPartProbs.remove(partProbInf));
    }

    /**
     * Sucht das <code>InfoParProbWrapper</code> zu einem Teilproblem in
     * Berechnung.
     *
     * @param partProbWrap  Teilproblem der gesuchten <code>InfoParProbWrapper</code>.
     * @return   dem Teilproblem zugeordnete <code>InfoParProbWrapper</code>,
     *           oder <code>null</code>, wenn das Teilproblem nicht in
     *           Berechnung ist.
     */
    private InfoParProbWrapper partProbInfoInComputation(ParProbWrapper partProbWrap) {
        InfoParProbWrapper partProbInfo = null;

        Iterator partProbIter = partProbsInComputation.iterator();
        while (partProbIter.hasNext()) {
            partProbInfo = (InfoParProbWrapper) partProbIter.next();
            if (partProbInfo.getParProbWrapper() == partProbWrap) {
                return partProbInfo;
            }
        }
        return null;
    }

    /**
     * Sucht das <code>InfoParProbWrapper</code> zu einem abgebrochenen
     * Teilproblem.
     *
     * @param partProbWrap  Teilproblem der gesuchten <code>InfoParProbWrapper</code>.
     * @return   dem Teilproblem zugeordnete <code>InfoParProbWrapper</code>,
     *           oder <code>null</code>, wenn das Teilproblem nicht abgebrochen
     *           wurde.
     */
    private InfoParProbWrapper partProbInfoAborted(ParProbWrapper partProbWrap) {
        InfoParProbWrapper partProbInfo = null;

        Iterator partProbIter = preferedPartProbs.iterator();
        while (partProbIter.hasNext()) {
            partProbInfo = (InfoParProbWrapper) partProbIter.next();
            if (partProbInfo.getParProbWrapper() == partProbWrap) {
                return partProbInfo;
            }
        }
        return null;
    }

    /**
     * Entfernt einen Teilproblem-Wrapper aus der Verwaltung.
     *
     * @param partProbWrap  Teilproblem-Wrapper, der entfernt werden soll.
     *
     * @return  Das zum entfernten Teilproblem-Wrapper gehörige Info-Objekt.
     */
    synchronized InfoParProbWrapper removeParProbWrapper(ParProbWrapper partProbWrap) {
        InfoParProbWrapper partProbInfo;

        partProbInfo = partProbInfoInComputation(partProbWrap);
        if (partProbInfo == null) {
            partProbInfo = partProbInfoAborted(partProbWrap);
        } else {
            if (partProbInfoAborted(partProbWrap) != null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                        "Teilproblem ist sowohl in der Liste der Teilprobleme"
                        + " in Berechnung als auch in der der abgebrochenen Teilprobleme.");
                }
            }
        }
        removePartProbInfo(partProbInfo);
        return partProbInfo;
    }

    /**
     * Ermittelt, ob das Teilproblem vom übergebenen Teilproblem-Info-Objekt
     * ({@link InfoParProbWrapper}) in der <code>Collection</code> der sich in
     * Berechnung befindenden Teilprobleme enthalten ist.
     *
     * @param partProbInfo  Teilproblem-Info-Objekt von dessen Teilrpoblem
     *                      ermittelt werden soll, ob es sich in Berechnung
     *                      befindet.
     * @return  <code>true</code> falls das Objekt in Collection der in
     *          Berechnung befindlichen Teilprobleme enthalten ist,
     *           <code>false</code> sonst.
     */
    synchronized boolean isEnqueuedPartProbInfo(InfoParProbWrapper partProbInfo) {
        return partProbsInComputation.contains(partProbInfo);
    }

    /**
     * Holt das vorderste Teilproblem-Info-Objekt aus der Schlange der
     * bevorzugten Teilproblem-Info-Objekte heraus, entfernt es aus der
     * Schlange und fügt es der Schlange der in Berechnung befindlichen
     * Teilproblem-Info-Objekte hinzu.
     *
     * @return  vorderstes bevorzugtes Teilproblem.
     */
    synchronized InfoParProbWrapper getPreferedPartialProblemInfo() {
        if (preferedPartProbs.isEmpty()) {
            return null;
        } else {
            InfoParProbWrapper partProbInfoObj =
                (InfoParProbWrapper) preferedPartProbs.remove(0);
            partProbsInComputation.add(partProbInfoObj);
            return partProbInfoObj;
        }
    }

    /**
     * überprüft, ob sich überhaupt Teilproblem-Info-Objekte in der
     * {@link PartialProblemQueue} befinden.
     *
     * @return  <code>true</code> falls {@link PartialProblemQueue} leer,
     *          <code>false</code>, sonst.
     */
    synchronized boolean isEmpty() {
        return (partProbsInComputation.isEmpty() && preferedPartProbs.isEmpty());
    }
}

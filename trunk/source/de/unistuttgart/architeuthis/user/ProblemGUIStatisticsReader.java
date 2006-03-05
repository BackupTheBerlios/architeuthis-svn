/*
 * file:        ProblemGUIStatisticsReader.java
 * created:     13.08.2003
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


package de.unistuttgart.architeuthis.user;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.systeminterfaces.UserProblemTransmitter;

/**
 * Graphische Benutzeroberfläche für die Darstellung der Statistik eines
 * spezielles Problem.
 *
 * @author Ralf Kible, Dietmar Lippold
 */
public class ProblemGUIStatisticsReader extends JFrame {

    /**
     * Logger für diese KLasse
     */
    private static final Logger LOGGER = Logger
            .getLogger(ProblemGUIStatisticsReader.class.getName());

    /**
     * Der Thread aktualisiert in regelmäßigen Abständen die angezeigten
     * Daten.
     */
    private class Updater extends Thread {

        /**
         * Logger for this class
         */
        private final Logger LOGGER = Logger.getLogger(Updater.class.getName());

        /**
         * Der ProblemTransmitter, der das Problem, dessen Statistik angezeigt
         * werden soll, übertragen hat.
         */
        private UserProblemTransmitter problemTransmitter;

        /**
         * Gibt an, ob sich der Thread beenden soll.
         */
        private volatile boolean terminated = false;

        /**
         * Teilt dem Thread mit, sich zu beenden.
         */
        public void terminate() {
            terminated = true;
            synchronized (this) {
                notifyAll();
            }
        }

        /**
         * Erzeugt eine neue Instanz und startet den Thread.
         *
         * @param transmitter  der UserProblemTransmitter, von dem das Problem,
         *                     dessen Statistik angezeigt werden soll, zum
         *                     Dispatcher übertragen wurde.
         */
        public Updater(UserProblemTransmitter transmitter) {
            problemTransmitter = transmitter;
            start();
        }

        /**
         * Ruft in regelmäßigen Abständen die Daten der Statistik ab und
         * aktualisiert die Texte im JFrame. Wenn die Daten nicht abgerufen
         * werden können, z.B. weil das Problem auf dem Dispatcher noch nicht
         * angekommen oder schon fertig ist, wird auf die nächste Aktualisierung
         * gewartet. Die Zeit der Wartens wird durch den Wert des Attributs
         * <code>delaySlider</code> der äußeren Klasse angegeben.
         */
        public void run() {
            ProblemStatistics stat = null;

            while (!terminated) {
                try {
                    stat = problemTransmitter.getProblemStatistic();
                } catch (RemoteException e) {
                    statusLabel.setText(
                        "Verbindung zum Dispatcher verloren!");
                    terminated = true;
                }
                if (!terminated) {
                    if (stat != null) {
                        showStatistics(stat);
                    }
                    try {
                        synchronized (this) {
                            wait(delaySlider.getValue() * 1000);
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    /**
     * Die initiale Zeitdauer des Zyklus zum Update der Daten der Statistik.
     */
    private static final long SHOW_MAX_MS_TIME = 10000;

    /**
     * Die maximale Zeit in Millisekunden, die noch nicht in Sekunden
     * angezeigt wird.
     */
    private static final int INIT_UPDATE_TIME = 3;

    /**
     * Attribute für die graphische Darstellung der Statistik-Daten.
     */
    private JPanel contentPane;
    private JScrollPane dispatcherScrollPane = new JScrollPane();
    private JScrollPane problemNameScrollPane = new JScrollPane();
    private JLabel computingParProbsLabel = new JLabel();
    private JLabel processingParProbsLabel = new JLabel();
    private JLabel createdParProbsLabel = new JLabel();
    private JLabel requestedParProbsLabel = new JLabel();
    private JLabel computedParProbsLabel = new JLabel();
    private JLabel processedParProbsLabel = new JLabel();
    private JLabel abortedParProbsLabel = new JLabel();
    private JLabel computingOperativesLabel = new JLabel();
    private JLabel averageDurationLabel = new JLabel();
    private JLabel totalDurationLabel = new JLabel();
    private JLabel problemAgeLabel = new JLabel();
    private JLabel delayTextLabel = new JLabel();
    private JLabel delayValueLabel = new JLabel();
    private JLabel statusLabel = new JLabel();
    private JTextField dispatcherName = new JTextField();
    private JTextField problemName = new JTextField();
    private JTextField computingParProbsField = new JTextField();
    private JTextField processingParProbsField = new JTextField();
    private JTextField createdParProbsField = new JTextField();
    private JTextField requestedParProbField = new JTextField();
    private JTextField computedParProbsField = new JTextField();
    private JTextField processedParProbsField = new JTextField();
    private JTextField abortedParProbsField = new JTextField();
    private JTextField computingOperativesField = new JTextField();
    private JTextField averageDurationField = new JTextField();
    private JTextField totalDurationField = new JTextField();
    private JTextField problemAgeField = new JTextField();
    private JSlider delaySlider = new JSlider();

    /**
     * Der Thread, der die Daten der Statistik regelmäßig aktualisiert.
     */
    private Updater readerThread;

    /**
     * Format für die Ausgabe von Uhrzeiten.
     */
    private SimpleDateFormat fmt =
        new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);

    /**
     * Konstruktor, der den JFrame zur Anzeige der Daten der Statistik des
     * Prolems initialisiert und dann einen Thread startet, der regelmäßig die
     * angezeigten Daten der Statistik aktualisiert.
     *
     * @param transmitter  der UserProblemTransmitter, von dem das Problem,
     *                     dessen Statistik angezeigt werden soll, zum
     *                     Dispatcher übertragen wurde.
     * @param dispatcher   String, der die Adresse des Dispatchers angibt
     * @param problemUrl   String, der die Adresse der Problem-Klassen angibt
     */
    public ProblemGUIStatisticsReader(UserProblemTransmitter transmitter,
                                      String dispatcher,
                                      String problemUrl) {

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        // Initialisierung des Frame
        try {
            jbInit();
            validate();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = getSize();
            if (frameSize.height > screenSize.height) {
                frameSize.height = screenSize.height;
            }
            if (frameSize.width > screenSize.width) {
                frameSize.width = screenSize.width;
            }
            setLocation(
                (screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Problem-Statistik von " + dispatcher);
            dispatcherName.setText(dispatcher);
            problemName.setText(problemUrl);
            statusLabel.setText("Gestartet!");
            setVisible(true);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                       "Fehler bei der Initialisierung vom Frame der"
                       + " Problem-Statistik! - exception: " + e);
        }

        readerThread = new Updater(transmitter);
    }

    /**
     * Setzt den neuen Wert des Verzögerungsschalters.
     */
    private void delaySlider_stateChanged() {
        delayValueLabel.setText("" + delaySlider.getValue());
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Initialisierungsmethode für den JFrame.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(null);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        this.setSize(new Dimension(275, 540));
        this.setTitle("Problem Statistik");

        dispatcherName.setText("");
        dispatcherName.setBackground(Color.lightGray);
        dispatcherName.setEditable(false);
        dispatcherName.setHorizontalAlignment(SwingConstants.CENTER);

        dispatcherScrollPane.setBounds(new Rectangle(10, 10, 245, 45));

        problemName.setText("");
        problemName.setHorizontalAlignment(SwingConstants.CENTER);
        problemName.setEditable(false);
        problemName.setBackground(Color.lightGray);

        problemNameScrollPane.setBounds(new Rectangle(10, 70, 245, 45));

        computingParProbsLabel.setBounds(new Rectangle(10, 140, 170, 20));
        computingParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        computingParProbsLabel.setText("Teilprobleme in Berechnung:");

        computingParProbsField.setBounds(new Rectangle(180, 140, 75, 20));
        computingParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        computingParProbsField.setEditable(false);

        processingParProbsLabel.setBounds(new Rectangle(10, 160, 170, 20));
        processingParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        processingParProbsLabel.setText("Teilprobleme in Bearbeitung:");

        processingParProbsField.setBounds(new Rectangle(180, 160, 75, 20));
        processingParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        processingParProbsField.setEditable(false);

        createdParProbsLabel.setBounds(new Rectangle(10, 180, 170, 20));
        createdParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        createdParProbsLabel.setText("Erzeugte Teilprobleme:");

        createdParProbsField.setBounds(new Rectangle(180, 180, 75, 20));
        createdParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        createdParProbsField.setEditable(false);

        requestedParProbsLabel.setBounds(new Rectangle(10, 200, 170, 20));
        requestedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        requestedParProbsLabel.setText("Abgefragte Teilprobleme:");

        requestedParProbField.setBounds(new Rectangle(180, 200, 75, 20));
        requestedParProbField.setFont(new java.awt.Font("Dialog", 0, 10));
        requestedParProbField.setEditable(false);

        computedParProbsLabel.setBounds(new Rectangle(10, 220, 170, 20));
        computedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        computedParProbsLabel.setText("Berechnete Teilprobleme:");

        computedParProbsField.setBounds(new Rectangle(180, 220, 75, 20));
        computedParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        computedParProbsField.setEditable(false);

        processedParProbsLabel.setBounds(new Rectangle(10, 240, 170, 20));
        processedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        processedParProbsLabel.setText("Bearbeitete Teilprobleme:");

        processedParProbsField.setBounds(new Rectangle(180, 240, 75, 20));
        processedParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        processedParProbsField.setEditable(false);

        abortedParProbsLabel.setBounds(new Rectangle(10, 260, 170, 20));
        abortedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        abortedParProbsLabel.setText("Abgebrochene Teilprobleme:");

        abortedParProbsField.setBounds(new Rectangle(180, 260, 75, 20));
        abortedParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        abortedParProbsField.setEditable(false);

        computingOperativesLabel.setBounds(new Rectangle(10, 290, 170, 20));
        computingOperativesLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        computingOperativesLabel.setText("Berechnende Operatives:");

        computingOperativesField.setBounds(new Rectangle(180, 290, 75, 20));
        computingOperativesField.setFont(new java.awt.Font("Dialog", 0, 10));
        computingOperativesField.setEditable(false);

        averageDurationLabel.setBounds(new Rectangle(10, 320, 170, 20));
        averageDurationLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        averageDurationLabel.setText("Durchschnittl. Zeit für ein TP:");

        averageDurationField.setBounds(new Rectangle(180, 320, 75, 20));
        averageDurationField.setFont(new java.awt.Font("Dialog", 0, 10));
        averageDurationField.setEditable(false);

        totalDurationLabel.setBounds(new Rectangle(10, 340, 170, 20));
        totalDurationLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        totalDurationLabel.setText("Berechnungszeit für alle TP:");

        totalDurationField.setBounds(new Rectangle(180, 340, 75, 20));
        totalDurationField.setFont(new java.awt.Font("Dialog", 0, 10));
        totalDurationField.setEditable(false);

        problemAgeLabel.setBounds(new Rectangle(10, 370, 170, 20));
        problemAgeLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        problemAgeLabel.setText("Problem auf Dispatcher seit:");
        problemAgeLabel.setRequestFocusEnabled(true);

        problemAgeField.setBounds(new Rectangle(180, 370, 75, 20));
        problemAgeField.setFont(new java.awt.Font("Dialog", 0, 10));
        problemAgeField.setEditable(false);

        delaySlider.setExtent(0);
        delaySlider.setMaximum(60);
        delaySlider.setMinimum(1);
        delaySlider.setFont(new java.awt.Font("Dialog", 1, 10));
        delaySlider.setBounds(new Rectangle(10, 410, 250, 20));
        delaySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                delaySlider_stateChanged();
            }
        });

        delayTextLabel.setText("Aktuelle Verzögerung in s:");
        delayTextLabel.setBounds(new Rectangle(10, 440, 170, 20));

        delayValueLabel.setText("10");
        delayValueLabel.setBounds(new Rectangle(180, 440, 50, 20));

        statusLabel.setText("starte...");
        statusLabel.setBounds(new Rectangle(10, 470, 240, 20));

        contentPane.setMaximumSize(new Dimension(32767, 32767));
        contentPane.setMinimumSize(new Dimension(1, 1));

        dispatcherScrollPane.getViewport().add(dispatcherName, null);
        contentPane.add(dispatcherScrollPane, null);

        problemNameScrollPane.getViewport().add(problemName, null);
        contentPane.add(problemNameScrollPane, null);

        contentPane.add(computingParProbsLabel, null);
        contentPane.add(computingParProbsField, null);

        contentPane.add(processingParProbsLabel, null);
        contentPane.add(processingParProbsField, null);

        contentPane.add(createdParProbsLabel, null);
        contentPane.add(createdParProbsField, null);

        contentPane.add(requestedParProbsLabel, null);
        contentPane.add(requestedParProbField, null);

        contentPane.add(computedParProbsLabel, null);
        contentPane.add(computedParProbsField, null);

        contentPane.add(processedParProbsLabel, null);
        contentPane.add(processedParProbsField, null);

        contentPane.add(abortedParProbsLabel, null);
        contentPane.add(abortedParProbsField, null);

        contentPane.add(computingOperativesLabel, null);
        contentPane.add(computingOperativesField, null);

        contentPane.add(averageDurationLabel, null);
        contentPane.add(averageDurationField, null);

        contentPane.add(totalDurationLabel, null);
        contentPane.add(totalDurationField, null);

        contentPane.add(problemAgeLabel, null);
        contentPane.add(problemAgeField, null);

        contentPane.add(delaySlider, null);
        contentPane.add(delayTextLabel, null);
        contentPane.add(delayValueLabel, null);

        contentPane.add(statusLabel, null);

        this.delaySlider.setValue(INIT_UPDATE_TIME);
    }

    /**
     * Standard-Methode, die überschrieben wurde, um das JFrame zu schließen,
     * wenn die close-Funktion vom Fenster aktiviert wird.
     *
     * @param event  Das auslösende Ereignis
     */
    protected void processWindowEvent(WindowEvent event) {
        super.processWindowEvent(event);
        if (event.getID() == WindowEvent.WINDOW_CLOSING) {
            if (readerThread != null) {
                readerThread.terminate();
            }
        }
    }

    /**
     * Gibt die aktuellen Werte der Statistik aus.
     *
     * @param stat  ProblemStatistics, die ausgegeben werden soll.
     */
    private void showStatistics(ProblemStatistics stat) {
        long timeAverageComputationMs = 0;
        long timeAverageComputationSek = 0;
        long timeAverageComputationPartHh = 0;
        long timeAverageComputationPartMin = 0;
        long timeAverageComputationPartSek = 0;

        long timeTotalComputationSek = 0;
        long timeTotalComputationPartHh = 0;
        long timeTotalComputationPartMin = 0;
        long timeTotalComputationPartSek = 0;

        long timeProblemAgeSek = 0;
        long timeProblemAgePartHh = 0;
        long timeProblemAgePartMin = 0;
        long timeProblemAgePartSek = 0;

        computingParProbsField.setText("" + stat.getComputingPartialProblems());
        processingParProbsField.setText("" + stat.getProcessingPartialProblems());
        createdParProbsField.setText("" + stat.getCreatedPartialProblems());
        requestedParProbField.setText("" + stat.getRequestedPartialProblems());
        computedParProbsField.setText("" + stat.getComputedPartialProblems());
        processedParProbsField.setText("" + stat.getProcessedPartialProblems());
        abortedParProbsField.setText("" + stat.getAbortedPartialProblems());
        computingOperativesField.setText("" + stat.getComputingOperatives());

        timeAverageComputationMs = stat.getAverageComputationDuration();
        if (timeAverageComputationMs > SHOW_MAX_MS_TIME) {
            timeAverageComputationSek = timeAverageComputationMs / 1000;
            timeAverageComputationPartHh = timeAverageComputationSek / 3600;
            timeAverageComputationPartMin = timeAverageComputationSek / 60 % 60;
            timeAverageComputationPartSek = timeAverageComputationSek % 60;
            averageDurationField.setText(
                timeAverageComputationPartHh
                    + "h "
                    + timeAverageComputationPartMin
                    + "m "
                    + timeAverageComputationPartSek
                    + "s");
        } else {
            averageDurationField.setText(timeAverageComputationMs + "ms");
        }

        timeTotalComputationSek = stat.getTotalComputationDuration() / 1000;
        timeTotalComputationPartHh = timeTotalComputationSek / 3600;
        timeTotalComputationPartMin = timeTotalComputationSek / 60 % 60;
        timeTotalComputationPartSek = timeTotalComputationSek % 60;
        totalDurationField.setText(
            timeTotalComputationPartHh
                + "h "
                + timeTotalComputationPartMin
                + "m "
                + timeTotalComputationPartSek
                + "s");

        timeProblemAgeSek = stat.getProblemAge() / 1000;
        timeProblemAgePartHh = timeProblemAgeSek / 3600;
        timeProblemAgePartMin = timeProblemAgeSek / 60 % 60;
        timeProblemAgePartSek = timeProblemAgeSek % 60;
        problemAgeField.setText(
            timeProblemAgePartHh
                + "h "
                + timeProblemAgePartMin
                + "m "
                + timeProblemAgePartSek
                + "s");

        statusLabel.setText("Statistik empfangen um " + fmt.format(new Date()));
    }

    /**
     * Beendet den Thread, in dem die Statistik immer abgefragt wird und gibt
     * die letzte Statistik nochmal aus, falls diese übergeben wurde.
     *
     * @param stat  ProblemStatistics, die ausgegeben werden soll, oder der
     *              Wert <code>null</code>, wenn keine Statistik ausgegeben
     *              werden soll.
     */
    public void stopAndShowFinalStat(ProblemStatistics stat) {
        readerThread.terminate();
        try {
            readerThread.join();
        } catch (InterruptedException e) {
        }

        if (stat != null) {
            showStatistics(stat);
            statusLabel.setText("Beendet um " + fmt.format(new Date()));
        } else {
            statusLabel.setText("Abgebrochen um " + fmt.format(new Date()));
        }
    }
}

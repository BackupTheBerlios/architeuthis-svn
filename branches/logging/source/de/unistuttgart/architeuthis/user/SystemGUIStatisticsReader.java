/*
 * file:        SystemGUIStatisticsReader.java
 * created:     08.08.2003
 * last change: 16.06.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
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
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import de.unistuttgart.architeuthis.user.ProblemTransmitterImpl;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.systeminterfaces.UserProblemTransmitter;

/**
 * Graphische Benutzeroberfläche für die Darstellung einer
 * ProblemManager-Statistik.
 *
 * @author Ralf Kible, Dietmar Lippold
 *
 */
public class SystemGUIStatisticsReader extends JFrame {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(SystemGUIStatisticsReader.class.getName());


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
         * Ein ProblemTransmitter mit Verbindung zu dem Compute-Server, dessen
         * Statistik angezeigt werden soll.
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
         * @param transmitter  ein UserProblemTransmitter mit Verbindung zu
         *                     dem Compute-Server, dessen Statistik angezeigt
         *                     werden soll.
         */
        public Updater(UserProblemTransmitter transmitter) {
            problemTransmitter = transmitter;
            start();
        }

        /**
         * Ruft in regelmäßigen Abständen die Daten der Statistik ab und
         * aktualisiert die Texte im JFrame. Anschließend wartet er auf die
         * nächste Aktualisierung. Die Zeit der Wartens wird durch den Wert
         * des Attributs <code>delaySlider</code> der äußeren Klasse angegeben.
         */
        public void run() {
            SystemStatistics stat = null;

            while (!terminated) {
                try {
                    stat = problemTransmitter.getSystemStatistic();
                } catch (RemoteException e) {
                    statusLabel.setText(
                        "Verbindung zum Dispatcher verloren!");
                    terminated = true;
                }
                if (!terminated) {
                    showStatistics(stat);
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
     * Die maximale Zeit in Millisekunden, die noch nicht in Sekunden
     * angezeigt wird.
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
    private JLabel registeredOperativesLabel = new JLabel();
    private JLabel freeOperativesLabel = new JLabel();
    private JLabel currentProblemsLabel = new JLabel();
    private JLabel receivedProblemsLabel = new JLabel();
    private JLabel abortedProblemsLabel = new JLabel();
    private JLabel computingParProbsLabel = new JLabel();
    private JLabel processingParProbsLabel = new JLabel();
    private JLabel createdParProbsLabel = new JLabel();
    private JLabel computedParProbsLabel = new JLabel();
    private JLabel processedParProbsLabel = new JLabel();
    private JLabel abortedParProbsLabel = new JLabel();
    private JLabel computingOperativesLabel = new JLabel();
    private JLabel totalDurationLabel = new JLabel();
    private JLabel averageDurationLabel = new JLabel();
    private JLabel dispatcherAgeLabel = new JLabel();
    private JLabel delayTextLabel = new JLabel();
    private JLabel delayValueLabel = new JLabel();
    private JLabel statusLabel = new JLabel();
    private JTextField dispatcherName = new JTextField();
    private JTextField registeredOperativesField = new JTextField();
    private JTextField freeOperativesField = new JTextField();
    private JTextField currentProblemsField = new JTextField();
    private JTextField receivedProblemsField = new JTextField();
    private JTextField abortedProblemsField = new JTextField();
    private JTextField computingParProbsField = new JTextField();
    private JTextField processingParProbsField = new JTextField();
    private JTextField createdParProbsField = new JTextField();
    private JTextField computedParProbsField = new JTextField();
    private JTextField processedParProbsField = new JTextField();
    private JTextField abortedParProbsField = new JTextField();
    private JTextField totalDurationField = new JTextField();
    private JTextField averageDurationField = new JTextField();
    private JTextField dispatcherAgeField = new JTextField();
    private JSlider delaySlider = new JSlider();

    /**
     * Der Thread, der die Daten der Statistik regelmäßig aktualisiert.
     */
    private Updater readerThread;

    /**
     * Konstruktor, der den Frame initialisiert und dann Verbindung mit dem
     * ComputeManager herstellt und kontinuierlich Statistiken abfragt.
     *
     * @param transmitter  der UserProblemTransmitter, von dem das Problem,
     *                     dessen Statistik angezeigt werden soll, zum
     *                     Dispatcher übertragen wurde.
     * @param dispatcher   String, der die Adresse des Dispatchers angibt
     */
    public SystemGUIStatisticsReader(UserProblemTransmitter transmitter,
                                     String dispatcher) {

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
                (screenSize.width - frameSize.width) / 3,
                (screenSize.height - frameSize.height) / 3);

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Allgemeine Statistik von " + dispatcher);
            dispatcherName.setText(dispatcher);
            statusLabel.setText("Gestartet!");
            setVisible(true);
        } catch (Exception e) {
            System.err.println("Fehler bei der Initialisierung vom Frame der"
                               + " Problem-Statistik!");
            e.printStackTrace();
        }

        readerThread = new Updater(transmitter);
    }

    /**
     * Konstruktor, der den Frame initialisiert und dann Verbindung mit dem
     * ComputeManager herstellt und kontinuierlich Statistiken abfragt. Dazu
     * erzeugt er eine neue Instanz vom <code>UserProblemTransmitter</code>.
     *
     * @param dispatcher   String, der die Adresse des Dispatchers angibt
     */
    public SystemGUIStatisticsReader(String dispatcher)
        throws MalformedURLException,
               AccessException,
               RemoteException,
               NotBoundException {

        this(new ProblemTransmitterImpl(dispatcher), dispatcher);
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
        this.setSize(new Dimension(275, 550));
        this.setTitle("Dispatcher Statistik");

        dispatcherName.setText("");
        dispatcherName.setBackground(Color.lightGray);
        dispatcherName.setEditable(false);
        dispatcherName.setHorizontalAlignment(SwingConstants.CENTER);

        dispatcherScrollPane.setBounds(new Rectangle(10, 10, 245, 45));

        registeredOperativesLabel.setBounds(new Rectangle(10, 80, 170, 20));
        registeredOperativesLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        registeredOperativesLabel.setText("Angemeldete Operatives:");

        registeredOperativesField.setBounds(new Rectangle(180, 80, 75, 20));
        registeredOperativesField.setFont(new java.awt.Font("Dialog", 0, 10));
        registeredOperativesField.setEditable(false);

        freeOperativesLabel.setBounds(new Rectangle(10, 100, 170, 20));
        freeOperativesLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        freeOperativesLabel.setText("Freie Operatives:");

        freeOperativesField.setBounds(new Rectangle(180, 100, 75, 20));
        freeOperativesField.setFont(new java.awt.Font("Dialog", 0, 10));
        freeOperativesField.setEditable(false);

        currentProblemsLabel.setBounds(new Rectangle(10, 130, 170, 20));
        currentProblemsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        currentProblemsLabel.setText("Derzeit vorhandene Probleme:");

        currentProblemsField.setBounds(new Rectangle(180, 130, 75, 20));
        currentProblemsField.setFont(new java.awt.Font("Dialog", 0, 10));
        currentProblemsField.setEditable(false);

        receivedProblemsLabel.setBounds(new Rectangle(10, 150, 170, 20));
        receivedProblemsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        receivedProblemsLabel.setText("Bisher erhaltene Probleme:");

        receivedProblemsField.setBounds(new Rectangle(180, 150, 75, 20));
        receivedProblemsField.setFont(new java.awt.Font("Dialog", 0, 10));
        receivedProblemsField.setEditable(false);

        abortedProblemsLabel.setBounds(new Rectangle(10, 170, 170, 20));
        abortedProblemsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        abortedProblemsLabel.setText("Abgebrochene Probleme:");

        abortedProblemsField.setBounds(new Rectangle(180, 170, 75, 20));
        abortedProblemsField.setFont(new java.awt.Font("Dialog", 0, 10));
        abortedProblemsField.setEditable(false);

        computingParProbsLabel.setBounds(new Rectangle(10, 200, 170, 20));
        computingParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        computingParProbsLabel.setText("Teilprobleme in Berechnung:");

        computingParProbsField.setBounds(new Rectangle(180, 200, 75, 20));
        computingParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        computingParProbsField.setEditable(false);

        processingParProbsLabel.setBounds(new Rectangle(10, 220, 170, 20));
        processingParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        processingParProbsLabel.setText("Teilprobleme in Bearbeitung:");

        processingParProbsField.setBounds(new Rectangle(180, 220, 75, 20));
        processingParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        processingParProbsField.setEditable(false);

        createdParProbsLabel.setBounds(new Rectangle(10, 240, 170, 20));
        createdParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        createdParProbsLabel.setText("Erzeugte Teilprobleme:");

        createdParProbsField.setBounds(new Rectangle(180, 240, 75, 20));
        createdParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        createdParProbsField.setEditable(false);

        computedParProbsLabel.setBounds(new Rectangle(10, 260, 170, 20));
        computedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        computedParProbsLabel.setText("Berechnete Teilprobleme:");

        computedParProbsField.setBounds(new Rectangle(180, 260, 75, 20));
        computedParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        computedParProbsField.setEditable(false);

        processedParProbsLabel.setBounds(new Rectangle(10, 280, 170, 20));
        processedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        processedParProbsLabel.setText("Bearbeitete Teilprobleme:");

        processedParProbsField.setBounds(new Rectangle(180, 280, 75, 20));
        processedParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        processedParProbsField.setEditable(false);

        abortedParProbsLabel.setBounds(new Rectangle(10, 300, 170, 20));
        abortedParProbsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        abortedParProbsLabel.setText("Abgebrochene Teilprobleme:");

        abortedParProbsField.setBounds(new Rectangle(180, 300, 75, 20));
        abortedParProbsField.setFont(new java.awt.Font("Dialog", 0, 10));
        abortedParProbsField.setEditable(false);

        totalDurationLabel.setBounds(new Rectangle(10, 330, 170, 20));
        totalDurationLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        totalDurationLabel.setText("Berechnungszeit für alle TP:");

        totalDurationField.setBounds(new Rectangle(180, 330, 75, 20));
        totalDurationField.setFont(new java.awt.Font("Dialog", 0, 10));
        totalDurationField.setEditable(false);

        averageDurationLabel.setBounds(new Rectangle(10, 350, 170, 20));
        averageDurationLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        averageDurationLabel.setText("Durchschnittl. Zeit für ein TP:");

        averageDurationField.setBounds(new Rectangle(180, 350, 75, 20));
        averageDurationField.setFont(new java.awt.Font("Dialog", 0, 10));
        averageDurationField.setEditable(false);

        dispatcherAgeLabel.setBounds(new Rectangle(10, 380, 170, 20));
        dispatcherAgeLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        dispatcherAgeLabel.setText("Dispatcher läuft seit:");
        dispatcherAgeLabel.setRequestFocusEnabled(true);

        dispatcherAgeField.setBounds(new Rectangle(180, 380, 75, 20));
        dispatcherAgeField.setFont(new java.awt.Font("Dialog", 0, 10));
        dispatcherAgeField.setEditable(false);

        delaySlider.setExtent(0);
        delaySlider.setMaximum(60);
        delaySlider.setMinimum(1);
        delaySlider.setFont(new java.awt.Font("Dialog", 1, 10));
        delaySlider.setBounds(new Rectangle(10, 420, 250, 20));
        delaySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                delaySlider_stateChanged();
            }
        });

        delayTextLabel.setText("Aktuelle Verzögerung in s:");
        delayTextLabel.setBounds(new Rectangle(10, 450, 170, 20));

        delayValueLabel.setText("10");
        delayValueLabel.setBounds(new Rectangle(180, 450, 50, 20));

        statusLabel.setText("starte...");
        statusLabel.setBounds(new Rectangle(10, 480, 240, 20));

        contentPane.setMaximumSize(new Dimension(32767, 32767));
        contentPane.setMinimumSize(new Dimension(1, 1));

        dispatcherScrollPane.getViewport().add(dispatcherName, null);
        contentPane.add(dispatcherScrollPane, null);

        contentPane.add(registeredOperativesLabel, null);
        contentPane.add(registeredOperativesField, null);

        contentPane.add(freeOperativesLabel, null);
        contentPane.add(freeOperativesField, null);

        contentPane.add(currentProblemsLabel, null);
        contentPane.add(currentProblemsField, null);

        contentPane.add(receivedProblemsLabel, null);
        contentPane.add(receivedProblemsField, null);

        contentPane.add(abortedProblemsLabel, null);
        contentPane.add(abortedProblemsField, null);

        contentPane.add(computingParProbsLabel, null);
        contentPane.add(computingParProbsField, null);

        contentPane.add(processingParProbsLabel, null);
        contentPane.add(processingParProbsField, null);

        contentPane.add(createdParProbsLabel, null);
        contentPane.add(createdParProbsField, null);

        contentPane.add(computedParProbsLabel, null);
        contentPane.add(computedParProbsField, null);

        contentPane.add(processedParProbsLabel, null);
        contentPane.add(processedParProbsField, null);

        contentPane.add(abortedParProbsLabel, null);
        contentPane.add(abortedParProbsField, null);

        contentPane.add(totalDurationLabel, null);
        contentPane.add(totalDurationField, null);

        contentPane.add(averageDurationLabel, null);
        contentPane.add(averageDurationField, null);

        contentPane.add(dispatcherAgeLabel, null);
        contentPane.add(dispatcherAgeField, null);

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
     * @param e  Das auslösende Ereignis
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (readerThread != null) {
                readerThread.terminate();
            }
        }
    }

    /**
     * Gibt die aktuellen Werte der Statistik aus.
     *
     * @param stat  SystemStatistics, die ausgegeben werden soll.
     */
    private void showStatistics(SystemStatistics stat) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);

        long timeTotalComputationSek = 0;
        long timeTotalComputationPartHh = 0;
        long timeTotalComputationPartMin = 0;
        long timeTotalComputationPartSek = 0;

        long timeAverageComputationMs = 0;
        long timeAverageComputationSek = 0;
        long timeAverageComputationPartHh = 0;
        long timeAverageComputationPartMin = 0;
        long timeAverageComputationPartSek = 0;

        long timeDispatcherAgeSek = 0;
        long timeDispatcherAgePartHh = 0;
        long timeDispatcherAgePartMin = 0;
        long timeDispatcherAgePartSek = 0;

        registeredOperativesField.setText("" + stat.getRegisteredOperatives());
        freeOperativesField.setText("" + stat.getFreeOperatives());
        currentProblemsField.setText("" + stat.getCurrentProblems());
        receivedProblemsField.setText("" + stat.getReceivedProblems());
        abortedProblemsField.setText("" + stat.getAbortedProblems());
        computingParProbsField.setText("" + stat.getComputingPartialProblems());
        processingParProbsField.setText("" + stat.getProcessingPartialProblems());
        createdParProbsField.setText("" + stat.getCreatedPartialProblems());
        computedParProbsField.setText("" + stat.getComputedPartialProblems());
        processedParProbsField.setText("" + stat.getProcessedPartialProblems());
        abortedParProbsField.setText("" + stat.getAbortedPartialProblems());

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

        timeDispatcherAgeSek = stat.getDispatcherAge() / 1000;
        timeDispatcherAgePartHh = timeDispatcherAgeSek / 3600;
        timeDispatcherAgePartMin = timeDispatcherAgeSek / 60 % 60;
        timeDispatcherAgePartSek = timeDispatcherAgeSek % 60;
        dispatcherAgeField.setText(
            timeDispatcherAgePartHh
                + "h "
                + timeDispatcherAgePartMin
                + "m "
                + timeDispatcherAgePartSek
                + "s");

        statusLabel.setText("Statistik empfangen um " + fmt.format(new Date()));
    }

    /**
     * Startet eine graphische Benutzeroberfläche zur Darstellung der
     * allgemeinen Statistik des ComputeSystems, dessen Adresse als
     * Kommandozeilenparameter eingegeben wird.
     *
     * @param args  Die obligatorischen Kommandozeilenparameter, dabei muss
     *              der erste und einzige Parameter die Adresse des
     *              ComputeSystems sein.
     */
    public static void main(String[] args) {
        String compSys = null;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.warning("exception: " + e);
        }

        if (args.length == 1) {
            compSys = args[0];

            try {
                new SystemGUIStatisticsReader(compSys);
            } catch (Exception e) {
                LOGGER.warning("Fehler! -  : exception: " + e);
            }
        } else {
            System.err.println("Fehler: Als Kommandozeilenargument muss die Adresse desnComputesystems eingegeben werden. Beispiel:njava SystemGUIStatisticsReader meinSystem.de:"
                            + ProblemManager.PORT_NO + "exception: " + null);
        }
    }
}

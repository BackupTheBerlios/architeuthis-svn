/*
 * file:        HashStoreMain.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 15.02.2005 von Michael Wohlfart
 * developers:  Michael Wohlfart michael.wohlfart@zsw-bw.de
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
package de.unistuttgart.architeuthis.testenvironment.hashstore;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;
import de.unistuttgart.architeuthis.remotestore.hashmap.RemoteHashMapGenerator;
import de.unistuttgart.architeuthis.user.ProblemComputation;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;

/**
 * @author michael
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HashStoreMain {


    //private static final String DISPATCHER = "127.0.0.1:1099";

    //private static final String CODEBASE = "http://127.0.0.1:3456/";



    public static void main(String[] args) {
        try {
        	
        	String codebase = args[0];
        	String dispatcher = args[1];

            // Archi User-Interface:
            ProblemComputation computation = new ProblemComputation();

            // ein Problem:
            HashStoreProblemImpl problem = new HashStoreProblemImpl();

            // ein Speicher-Generator:
            RemoteHashMapGenerator generator = new RemoteHashMapGenerator();


            System.out.println("Problem wird abgeschickt");

            // Problem abschicken und auf Lösung warte:
            Serializable solution =
                computation.transmitProblem(problem, generator, dispatcher, codebase);


            // Lösung noch auspacken:
            Serializable sol = ((ContainerPartialSolution) solution).getPartialSolution();

            // Lösung ausgeben:
            System.out.println("gefundenen Lösung: " + sol);


        } catch (ProblemComputeException ex) {
            ex.printStackTrace();
        } catch (AccessException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NotBoundException ex) {
            ex.printStackTrace();
        }

    }


}

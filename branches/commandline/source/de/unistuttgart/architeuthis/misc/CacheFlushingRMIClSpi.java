/*
 * file:        CachingFlushingRMIClSpi.java
 * created:     <???>
 * last change: 26.09.2004 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.misc;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Dieser "Remote Method Invocation ClassLoader Service Provider" (also
 * RMIClassLoaderSpi) implemeniert die statischen Methoden des RMIClassLoader.
 * Er bietet außerdem Methoden an, die ermöglichen, einen ClassLoader zu einem
 * bestimmten URL zu entfernen, wenn aktuell keine Klasse mehr in Benutzung
 * ist, deren Code unter dem entsprechenden URL liegt. Dadurch wird beim
 * nächsten Laden einer Klasse von diesem URL ein neuer ClassLoader verwendet
 * und der Code der Klasse beim neuen Laden nicht aus dem Cache des alten
 * ClassLoaders entnommen.
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public class CacheFlushingRMIClSpi extends RMIClassLoaderSpi {

    /**
     * Speichert die zuletzt erzeugte Instanz dieser Klasse.
     */
    private static CacheFlushingRMIClSpi lastInstance = null;

    /**
     * Der standardmäßige Spi, der normal verwendet wurde. Wird immer dann
     * benutzt, wenn sonst nichts geht.
     */
    private RMIClassLoaderSpi defaultProvider =
        RMIClassLoader.getDefaultProviderInstance();

    /**
     * Hierin werden alle bisher erstellten URLClassLoader gespeichert, wobei
     * der Schlüssel der jeweilige URL ist.
     */
    private Map classloaders = Collections.synchronizedMap(new HashMap());

    /**
     * Speichert zu jedem URL die Anzahl der Klassen, zu denen ein ClassLoader
     * diesen URL benutzt.
     */
    private HashMap urlCounter = new HashMap();

    /**
     * Erzeugt eine neue Instanz.
     */
    public CacheFlushingRMIClSpi() {
        lastInstance = this;
    }

    /**
     * Ermittelt, ob diese Klasse von der JVM benutzt wird. Dies ist genau
     * dann der Fall, wenn beim Aufruf der JVM dem Property
     * <code>java.rmi.server.RMIClassLoaderSpi</code> diese Klasse als Wert
     * zugewiesen wurde.
     */
    public static boolean isUsed() {
        return (lastInstance != null);
    }

    /**
     * Registriert die Verwendung der übergebenen URLs, von denen Code zu einer
     * neuen Klasse geladen werden muß.
     *
     * @param urls  Ein Array von URL, von denen Code zu einer neuen Klasse
     *              geladen werden muß.
     */
    public static void registerUrls(URL[] urls) {
        Integer urlCount;

        if (lastInstance != null) {
            synchronized (lastInstance) {
                for (int urlNr = 0; urlNr < urls.length; urlNr++) {
                    urlCount = (Integer) lastInstance.urlCounter.get(urls[urlNr]);
                    if (urlCount == null) {
                        // der URL ist neu
                        urlCount = new Integer(1);
                    } else {
                        urlCount = new Integer(urlCount.intValue() + 1);
                    }
                    lastInstance.urlCounter.put(urls[urlNr], urlCount);
                }
            }
        }
    }

    /**
     * Vermerkt, daß von den übergebenen URLs zu einer Klasse kein Code mehr
     * geladen werden braucht. Wenn dies zu einem der URLs die letzte Klasse
     * war, zu der von diesem URL Code geladen wurde, wird der zu diesem URL
     * gehörende ClassLoader entfernt. URLs, die zuvor nicht registriert
     * wurden, werden ignoriert.
     *
     * @param urls  Ein Array von URL, von denen zu einer Klasse kein Code
     *              mehr geladen werden muß.
     */
    public static void unregisterUrls(URL[] urls) {
        Integer urlCount;

        if (lastInstance != null) {
            synchronized (lastInstance) {
                for (int urlNr = 0; urlNr < urls.length; urlNr++) {
                    urlCount = (Integer) lastInstance.urlCounter.get(urls[urlNr]);
                    if (urlCount != null) {
                        if (urlCount.intValue() == 1) {
                            // den zugehörigen ClassLoader entfernen
                            lastInstance.classloaders.remove(urls[urlNr]);
                            lastInstance.urlCounter.remove(urls[urlNr]);
                        } else {
                            urlCount = new Integer(urlCount.intValue() - 1);
                            lastInstance.urlCounter.put(urls[urlNr], urlCount);
                        }
                    }
                }
            }
        }
    }

    /**
     * Entfernt alle bisher erzeugten ClassLoader aus der zuletzt erzeugten
     * Instanz des NonCachingRMIClSpi. Damit wird der Klassen-Cache geleert.
     */
    public static void flushClassLoaders() {
        if (lastInstance != null) {
            synchronized (lastInstance) {
                lastInstance.classloaders =
                    Collections.synchronizedMap(new HashMap());
                lastInstance.urlCounter = new HashMap();
            }
        }
    }

    /**
     * Erstellt einen neuen URLClassLoader und fügt ihn in die HashMap
     * <code>classloaders</code> ein.
     *
     * @param url  Der URL, für den der ClassLoader zuständig sein soll.
     *
     * @return  Einen neuen URLClassLoader
     */
    private URLClassLoader generateNewClassLoader(URL url) {
        URL[] urls = new URL[] {url};
        URLClassLoader ucl = new URLClassLoader(urls);

        classloaders.put(url, ucl);
        return ucl;
    }

    /**
     * Lädt eine Klasse explizit von einem URL. Falls von diesem URL schon
     * einmal geladen wurde, wird der gleiche ClassLoader verwendet
     * (falls dieser in der Zwischenzeit nicht entfernt wurde), sonst wird
     * ein neuer instanziert.
     *
     * @param url  URL, unter der die Klasse zu finden ist.
     * @param name   Der Klassenname, mit Package-Bezeichnungen.
     *
     * @return  Die gesuchte Klasse.
     *
     * @throws ClassNotFoundException  Falls die angegebene Klasse nicht
     *                                 gefunden werden konnte.
     */
    public Class loadClass(URL url, String name)
        throws ClassNotFoundException {

        URLClassLoader ucl = (URLClassLoader) classloaders.get(url);
        if (ucl != null) {
            return ucl.loadClass(name);
        } else {
            return generateNewClassLoader(url).loadClass(name);
        }
    }

    /**
     * Lädt eine Klasse explizit von einem als String gegebenen URL. Falls von
     * diesem URL schon einmal geladen wurde, wird der gleiche ClassLoader
     * verwendet (falls dieser in der Zwischenzeit nicht entfernt wurde), sonst
     * wird ein neuer instanziert.
     *
     * @param url  String, der einen URL enthält, wo die Klasse zu finden ist.
     * @param name  Der Klassenname, mit Package-Bezeichnungen.
     *
     * @return  Die gesuchte Klasse
     *
     * @throws MalformedURLException  Falls der String keinen gültigen URL
     *                                darstellt.
     * @throws ClassNotFoundException  Falls die Klasse nicht gefunden wurde.
     */
    public Class loadClass(String url, String name)
        throws MalformedURLException, ClassNotFoundException {

        return loadClass(new URL(url), name);
    }

    /**
     * Lädt eine Klasse von dem unter <code>codebase</code> als String
     * gegebenen URL. Zuerst wird versucht, den <code>defaultLoader</code> zu
     * verwenden, sonst wird wie bei loadClass(URL, String) vorgegangen.
     *
     * @param codebase  Wo die Klasse zu finden ist.
     * @param name  Der Klassenname.
     * @param defaultLoader  Ein zu verwendender ClassLoader.
     *
     * @return  Die gesuchte Klasse
     *
     * @throws MalformedURLException   Falls der String keinen gültigen URL
     *                                 darstellt.
     * @throws ClassNotFoundException  Falls die Klasse nicht gefunden wurde.
     */
    public Class loadClass(
        String codebase,
        String name,
        ClassLoader defaultLoader)
        throws MalformedURLException, ClassNotFoundException {

        // Zuerst: defaultLoader probieren
        try {
            if (defaultLoader != null) {
                return defaultLoader.loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            // Egal, weiterprobieren
        }

        // Dann: Alten ClassLoader suchen
        try {
            URL codebaseUrl = new URL(codebase);
            URLClassLoader ucl = (URLClassLoader) classloaders.get(codebaseUrl);
            if (ucl != null) {
                return ucl.loadClass(name);
            }
        } catch (MalformedURLException e) {
            // Auch egal...
        }

        // Sonst: Neuen kreieren.
        try {
            URLClassLoader ucl = generateNewClassLoader(new URL(codebase));
            return ucl.loadClass(name);
        } catch (MalformedURLException e) {
            // Egal. Noch ein Versuch.
        }

        // Wenn alles nichts hilft: Den defaulProvider ranlassen.
        return defaultProvider.loadClass(codebase, name, defaultLoader);
    }

    /**
     * Liefert den ClassLoader zurück, mit dem (von nun an, oder schon bisher)
     * von der angegebenen <code>codebase</code> geladen wird.
     *
     * @param codebase  Der URL, wo die Klassen zu finden sind.
     *
     * @return Den ClassLoader, der bei dieser <code>codebase</code> verwendet
     *         wird.
     *
     * @throws MalformedURLException  Falls der String keinen gültigen URL
     *                                darstellt.
     */
    public ClassLoader getClassLoader(String codebase)
        throws MalformedURLException {

        URL codebaseUrl = new URL(codebase);
        ClassLoader clLoader = (ClassLoader) classloaders.get(codebaseUrl);
        if (clLoader != null) {
            return clLoader;
        } else {
            return generateNewClassLoader(codebaseUrl);
        }
    }

    /**
     * Liefert die URLs zurück, von denen der ClassLoader, mit der die
     * übergebene Klasse geladen wurde, andere Klassen laden kann.
     *
     * @param class  Die zu untersuchende Klasse.
     *
     * @return  Die URLs (durch Leerzeichen getrennt) von denen diese Klasse
     *          stammt.
     */
    public String getClassAnnotation(Class cl) {
        ClassLoader currentLoader = cl.getClassLoader();

        if (currentLoader == null) {
            return System.getProperty("java.rmi.codebase");
        }

        if (currentLoader instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader) currentLoader;
            if (ucl != null) {
                URL[] urls = ucl.getURLs();
                String annot = "";
                for (int i = 0; i < urls.length; i++) {
                    annot = annot.concat(urls[i].toExternalForm() + " ");
                }
                annot = annot.trim();
                return annot;
            }
        }

        return defaultProvider.getClassAnnotation(cl);
    }

    /**
     * Achtung: Keine Ahnung, was hier passieren soll, die Aufgabe wird
     * einfach dem normalen <code>defaultProvider</code> übertragen.
     *
     * @param codebase  Siehe oben!
     * @param interfaces  Siehe oben!
     * @param defaultLoader  Siehe oben!
     *
     * @return  Siehe oben!
     *
     * @throws MalformedURLException   Siehe oben!
     * @throws ClassNotFoundException   Siehe oben!
     */
    public Class loadProxyClass(String codebase, String[] interfaces,
                                ClassLoader defaultLoader)
        throws MalformedURLException, ClassNotFoundException {

        // Wenn man nicht weiß, was getan werden muss: defaultProvider nehmen
        return defaultProvider.loadProxyClass(codebase,
                                              interfaces, defaultLoader);
    }
}

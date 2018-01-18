/**
 * 
 */
package net.sf.statcvs.model;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Benoit
 *
 */
public class Module implements Comparable {
    private final SortedSet files = new TreeSet();
    private final String name;
    private int currentLoc = -1;

    public Module(final String name) {
        super();
        this.name = name;
    }

    public void addFile(final VersionedFile vf) {
        files.add(vf);
    }

    public int getCurrentLinesOfCode() {
        if (currentLoc < 0) {
            final Iterator it = files.iterator();
            currentLoc = 0;
            while (it.hasNext()) {
                final VersionedFile vf = (VersionedFile) it.next();
                currentLoc += vf.getCurrentLinesOfCode();
            }
        }
        return currentLoc;
    }

    /**
     * Returns all {@link Revision}s to files in
     * this directory, in order from oldest to most recent.
     * @return list of <tt>Revision</tt>s for this directory
     */
    public SortedSet getRevisions() {
        final SortedSet result = new TreeSet();
        final Iterator iterator = files.iterator();
        while (iterator.hasNext()) {
            final VersionedFile file = (VersionedFile) iterator.next();
            result.addAll(file.getRevisions());
        }
        return result;
    }

    public int compareTo(final Object mod) {
        return name.compareTo(((Module) mod).name);
    }

    public String getName() {
        return name;
    }

    public SortedSet getFiles() {
        return files;
    }
}

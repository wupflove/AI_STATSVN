/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: FileCollectionFormatter.java,v $ 
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.statcvs.util.IntegerMap;

/**
 * Groups a set of file names by directory. Provides a list
 * of directories in the file set, and lumps directories
 * with only one file together with its parent directory.
 * 
 * @author Richard Cyganiak
 * @version $Id: FileCollectionFormatter.java,v 1.9 2008/04/02 11:22:15 benoitx Exp $
 */
public class FileCollectionFormatter {

    private final Collection files;
    private final IntegerMap filesPerDir;
    private final IntegerMap dirDepths;

    /**
     * Creates a new instance from a <code>Collection</code> of
     * file names.
     * @param files Collection containing the String representations of files
     */
    public FileCollectionFormatter(final Collection files) {
        this.files = files;
        filesPerDir = createFilesPerDirCount();
        dirDepths = createDirDepths();
    }

    private IntegerMap createFilesPerDirCount() {
        final IntegerMap result = new IntegerMap();
        final Iterator it = files.iterator();
        while (it.hasNext()) {
            final String file = (String) it.next();
            result.addInt(getDirectory(file), 1);
        }
        return result;
    }

    private IntegerMap createDirDepths() {
        final IntegerMap result = new IntegerMap();
        final Iterator it = filesPerDir.iteratorSortedByKey();
        while (it.hasNext()) {
            final String dir = (String) it.next();
            result.put(dir, getDepth(dir));
        }
        return result;
    }

    /**
     * Gets a list of <code>String</code>s containing the
     * directories in the file set, ordered by name.
     * @return a list of <code>String</code>s containing the
     * directories in the file set, ordered by name.
     */
    public List getDirectories() {
        final List result = new ArrayList();
        final Iterator it = dirDepths.iteratorSortedByKey();
        while (it.hasNext()) {
            final String directory = (String) it.next();
            result.add(directory);
        }
        return result;
    }

    /**
     * Gets the names of all files which reside in a given directory.
     * The directory must be one from the {@link #getDirectories}
     * list. Files will be relative to the directory. They will be
     * ordered by name.
     * @param directory to process
     * @return the names of all files which reside in a given directory.
     * The directory must be one from the {@link #getDirectories}
     * list. Files will be relative to the directory. They will be
     * ordered by name.
     */
    public List getFiles(final String directory) {
        if (!dirDepths.contains(directory)) {
            throw new NoSuchElementException("doesn't contain directory '" + directory + "'");
        }
        final List result = new ArrayList(getFilesInDir(directory));
        Collections.sort(result);
        final List allSubdirFiles = getFilesInSubdirs(directory);
        Collections.sort(allSubdirFiles);
        result.addAll(allSubdirFiles);
        return result;
    }

    private List getFilesInSubdirs(final String directory) {
        final List result = new ArrayList();
        final Iterator it = files.iterator();
        while (it.hasNext()) {
            final String filename = (String) it.next();
            if (isInDirectory(filename, directory) && !getDirectory(filename).equals(directory) && !isInDeeperDirectory(filename, directory)) {
                result.add(getRelativeFilename(filename, directory));
            }
        }
        return result;
    }

    private boolean isInDeeperDirectory(final String filename, final String directory) {
        String currentDir = getDirectory(filename);
        int currentDepth = getDepth(currentDir);
        final int directoryDepth = getDepth(directory);
        while (currentDepth > directoryDepth) {
            if (dirDepths.contains(currentDir)) {
                return true;
            }
            currentDepth--;
            currentDir = getParent(currentDir);
        }
        return false;
    }

    private List getFilesInDir(final String directory) {
        final List result = new ArrayList();
        final Iterator it = files.iterator();
        while (it.hasNext()) {
            final String filename = (String) it.next();
            if (getDirectory(filename).equals(directory)) {
                result.add(getRelativeFilename(filename, directory));
            }
        }
        return result;
    }

    /**
     * Returns TRUE if file is in specified directroy, FALSE otherwise
     * @param filename File to test
     * @param directory Directory to test
     * @return boolean TRUE if file is in specified directroy, FALSE otherwise
     */
    protected static boolean isInDirectory(final String filename, final String directory) {
        return getDirectory(filename).startsWith(directory);
    }

    /**
     * Returns relative filename for specified file and directory
     * @param filename file
     * @param dir directory
     * @return String relative filename for specified file and directory
     */
    protected static String getRelativeFilename(final String filename, final String dir) {
        return filename.substring(dir.length());
    }

    /**
     * Returns directory name of specified file
     * @param filename file to compute
     * @return String directory name of specified file
     */
    protected static String getDirectory(final String filename) {
        return filename.substring(0, filename.lastIndexOf("/") + 1);
    }

    /**
     * Returns name of parent directory to specified directory
     * @param directory to use
     * @return String name of parent directory to specified directory
     */
    protected static String getParent(final String directory) {
        final int lastIndex = directory.lastIndexOf("/");
        if (lastIndex == -1) {
            return "";
        }
        return directory.substring(0, directory.lastIndexOf("/", lastIndex - 1) + 1);
    }

    /**
     * Returns the depth of the directory
     * @param directory to be analysed
     * @return int the depth of the directory
     */
    protected static int getDepth(final String directory) {
        int result = 0;
        int index = 0;
        while (directory.indexOf("/", index) != -1) {
            index = directory.indexOf("/", index) + 1;
            result++;
        }
        return result;
    }
}

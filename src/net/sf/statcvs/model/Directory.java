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
*/
package net.sf.statcvs.model;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a directory in the {@link Repository Repository}, a
 * container for {@link VersionedFile}s and sub<tt>Directory</tt>s.
 * A new root directory is created by {@link #createRoot}.
 * The {@link #createSubdirectory} method creates new subdirectories.
 * 
 * TODO: Rename getCurrentLOC to getCurrentLines or getCurrentLineCount
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: Directory.java,v 1.11 2008/04/02 11:22:16 benoitx Exp $
 */
public abstract class Directory implements Comparable {
    private final SortedSet files = new TreeSet();
    private final SortedSet directories = new TreeSet();

    /**
     * Factory method for creating a new root directory.
     * @return a new root directory
     */
    public static Directory createRoot() {
        return new DirectoryRoot();
    }

    /**
     * Factory method for creating a new subdirectory.
     * @param name the subdirectory's name
     * @return the subdirectory instance
     */
    public Directory createSubdirectory(final String name) {
        final Directory result = new DirectoryImpl(this, name);
        directories.add(result);
        return result;
    }

    /**
     * Returns the directory's name without full path or any slashes, 
     * for example "src".
     * @return the directory's name
     */
    public abstract String getName();

    /**
     * Returns the directory's full path with trailing slash,
     * for example "src/net/sf/statcvs/".
     * @return the directory's path
     */
    public abstract String getPath();

    /**
     * Returns the directory's parent directory or <tt>null</tt> if it is the root
     * @return the directory's parent.
     */
    public abstract Directory getParent();

    /**
     * Returns <tt>true</tt> if this is the root of the directory tree.
     * @return <tt>true</tt> if this is the root of the directory tree
     */
    public abstract boolean isRoot();

    /**
     * Returns the level of this directory in the direcotry tree.
     * The root has level 0, its subdirectories have level 1, and so forth.
     * @return the level of this directory in the directory tree
     */
    public abstract int getDepth();

    /**
     * Returns all {@link VersionedFile} objects in this directory, ordered
     * by filename. Files in subdirectories are not included.
     * @return the files in this directory
     */
    public SortedSet getFiles() {
        return files;
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

    /**
     * Returns a <tt>SortedSet</tt> of all immediate subdirectories,
     * ordered by name.
     * @return <tt>SortedSet</tt> of {@link Directory} objects
     */
    public SortedSet getSubdirectories() {
        return directories;
    }

    /**
     * Returns a list of all subdirectories, including their subdirectories
     * and this directory itself. The list is preordered, beginning with this
     * directory itself.
     * @return <tt>SortedSet</tt> of {@link Directory} objects
     */
    public SortedSet getSubdirectoriesRecursive() {
        final SortedSet result = new TreeSet();
        result.add(this);
        final Iterator it = directories.iterator();
        while (it.hasNext()) {
            final Directory dir = (Directory) it.next();
            result.addAll(dir.getSubdirectoriesRecursive());
        }
        return result;
    }

    /**
     * Returns the number of lines in this directory. The returned number
     * will be for the current revisions of all files.
     * @return lines in this directory
     */
    public int getCurrentLOC() {
        int result = 0;
        final Iterator it = files.iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            result += file.getCurrentLinesOfCode();
        }
        return result;
    }

    /**
     * Returns the number of files in this directory. Deleted files are not
     * counted.
     * @return number of files in this directory
     */
    public int getCurrentFileCount() {
        int result = 0;
        final Iterator it = files.iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                result++;
            }
        }
        return result;
    }

    /**
     * Returns <code>true</code> if all files in this directory and its
     * subdirectories are deleted, or if it doesn't have any files and
     * subdirectories at all.
     * @return <code>true</code> if the directory is currently empty
     */
    public boolean isEmpty() {
        Iterator it = files.iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                return false;
            }
        }
        it = directories.iterator();
        while (it.hasNext()) {
            final Directory subdir = (Directory) it.next();
            if (!subdir.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this directory to another one, based on their full names.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object o) {
        return getPath().compareTo(((Directory) o).getPath());
    }

    /**
     * Adds a file to this directory.
     * @param file a file in this directory
     */
    void addFile(final VersionedFile file) {
        files.add(file);
    }
}
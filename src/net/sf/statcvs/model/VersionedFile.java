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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents one versioned file in the {@link Repository Repository},
 * including its name, {@link Directory} and {@link Revision} list.
 * Revisions can be created using the <tt>addXXXRevision</tt> factory
 * methods. Revisions can be created in any order.
 * 
 * TODO: Rename class to something like VersionedFile, getCurrentLinesOfCode() to getCurrentLines(), maybe getFilenameXXX, isDead() to isDeleted()
 *  
 * @author Manuel Schulze
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: VersionedFile.java,v 1.5 2009/08/31 19:16:35 benoitx Exp $
 */
public class VersionedFile implements Comparable {
    private final String filename;
    private final SortedSet revisions = new TreeSet();
    private final Directory directory;
    private Module module;
    private final Set authors = new HashSet();

    /**
     * Creates a VersionedFile object.
     * 
     * @param name The full name of the file
     * @param directory the directory where the file resides
     */
    public VersionedFile(final String name, final Directory directory) {
        this.filename = name;
        this.directory = directory;
        if (directory != null) {
            directory.addFile(this);
        }
    }

    /**
     * Returns a list of authors that have commited at least one revision of the file.
     * @return a list of authors
     */
    public Set getAuthors() {
        return authors;
    }

    /**
     * Returns the full filename.
     * @return the full filename
     */
    public String getFilenameWithPath() {
        return filename;
    }

    /**
     * Returns the filename without path.
     * @return the filename without path
     */
    public String getFilename() {
        final int lastDelim = this.filename.lastIndexOf("/");
        return this.filename.substring(lastDelim + 1, this.filename.length());
    }

    /**
     * Returns the file's <tt>Directory</tt>.
     * @return the file's <tt>Directory</tt>
     */
    public Directory getDirectory() {
        return directory;
    }

    /**
     * Gets the latest revision of this file.
     * @return the latest revision of this file
     */
    public Revision getLatestRevision() {
        return (Revision) this.revisions.last();
    }

    /**
     * Gets the earliest revision of this file.
     * @return the earliest revision of this file
     */
    public Revision getInitialRevision() {
        return (Revision) this.revisions.first();
    }

    /**
     * Returns the list of {@link Revision}s of this file,
     * sorted from earliest to most recent.
     * @return a <tt>SortedSet</tt> of {@link Revision}s
     */
    public SortedSet getRevisions() {
        return this.revisions;
    }

    /**
     * Returns the current number of lines for this file. Binary files
     * and deleted files are assumed to have 0 lines.
     * @return the current number of lines for this file
     */
    public int getCurrentLinesOfCode() {
        return getLatestRevision().getLines();
    }

    /**
     * Returns <code>true</code> if the latest revision of this file was
     * a deletion.
     * @return <code>true</code> if this file is deleted
     */
    public boolean isDead() {
        return getLatestRevision().isDead();
    }

    /**
     * Returns true, if <code>author</code> worked on this file.
     * @param author The <code>Author</code> to search for
     * @return <code>true</code>, if the author is listed in one of
     * this file's revisions
     */
    public boolean hasAuthor(final Author author) {
        return authors.contains(author);
    }

    /**
     * Returns the revision which was replaced by the revision given as
     * argument. Returns <tt>null</tt> if the given revision is the initial
     * revision of this file.
     * @param revision a revision of this file
     * @return this revision's predecessor
     */
    public Revision getPreviousRevision(final Revision revision) {
        if (!revisions.contains(revision)) {
            throw new IllegalArgumentException("revision not containted in file");
        }
        final SortedSet headSet = revisions.headSet(revision);
        if (headSet.isEmpty()) {
            return null;
        }
        return (Revision) headSet.last();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getFilenameWithPath() + " (" + revisions.size() + " revisions)";
    }

    /**
     * Compares this file to another one, based on filename.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object other) {
        return filename.compareTo(((VersionedFile) other).filename);
    }

    public boolean equals(final Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (!(rhs instanceof VersionedFile)) {
            return false;
        }
        final VersionedFile that = (VersionedFile) rhs;

        final boolean eq = filename.equals(that.filename);

        return eq;
    }

    public int hashCode() {
        return filename.hashCode();
    }

    /**
     * Adds an initial revision to the file. An initial revision is either
     * the first revision of the file, or a re-add after the file was
     * deleted.
     * @param revisionNumber the revision number, for example "1.1"
     * @param author the login from which the change was committed
     * @param date the time when the change was committed
     * @param comment the commit message
     * @param lines the number of lines of the new file
     */
    public Revision addInitialRevision(final String revisionNumber, final Author author, final Date date, final String comment, final int lines,
            final SortedSet symbolicNames) {
        final Revision result = new Revision(this, revisionNumber, Revision.TYPE_CREATION, author, date, comment, lines, lines, 0, symbolicNames);
        addRevision(result);
        return result;
    }

    /**
     * Adds a change revision to the file.
     * @param revisionNumber the revision number, for example "1.1"
     * @param author the login from which the change was committed
     * @param date the time when the change was committed
     * @param comment the commit message
     * @param lines the number of lines in the file after the change
     * @param linesDelta the change in the number of lines
     * @param replacedLines number of lines that were removed and replaced by others
     */
    public Revision addChangeRevision(final String revisionNumber, final Author author, final Date date, final String comment, final int lines,
            final int linesDelta, final int replacedLines, final SortedSet symbolicNames) {
        final Revision result = new Revision(this, revisionNumber, Revision.TYPE_CHANGE, author, date, comment, lines, linesDelta, replacedLines, symbolicNames);
        addRevision(result);
        return result;
    }

    /**
     * Adds a deletion revision to the file.
     * @param revisionNumber the revision number, for example "1.1"
     * @param author the login from which the change was committed
     * @param date the time when the change was committed
     * @param comment the commit message
     * @param lines the number of lines in the file before it was deleted
     */
    public Revision addDeletionRevision(final String revisionNumber, final Author author, final Date date, final String comment, final int lines,
            final SortedSet symbolicNames) {
        final Revision result = new Revision(this, revisionNumber, Revision.TYPE_DELETION, author, date, comment, 0, -lines, 0, symbolicNames);
        addRevision(result);
        return result;
    }

    /**
     * Adds a "begin of log" revision to the file. This kind of revision
     * only marks the beginning of the log timespan if the file was
     * already present in the repository at this time. It is not an actual
     * revision committed by an author.
     * @param date the begin of the log
     * @param lines the number of lines in the file at that time
     */
    public Revision addBeginOfLogRevision(final Date date, final int lines, final SortedSet symbolicNames) {
        final Revision result = new Revision(this, "0.0", Revision.TYPE_BEGIN_OF_LOG, null, date, null, lines, 0, 0, symbolicNames);
        addRevision(result);
        return result;
    }

    private void addRevision(final Revision revision) {
        revisions.add(revision);
        if (revision.getAuthor() != null) {
            authors.add(revision.getAuthor());
        }
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
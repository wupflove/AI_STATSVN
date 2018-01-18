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
    
	$RCSfile: Revision.java,v $ 
	Created on $Date: 2009/08/20 17:44:05 $ 
*/
package net.sf.statcvs.model;

import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * One revision of a {@link VersionedFile}. That can be an initial revision
 * (checkin), a change, a deletion, or a re-add. Revisions are created
 * using the methods {@link VersionedFile#addInitialRevision},
 * {@link VersionedFile#addChangeRevision} and
 * {@link VersionedFile#addDeletionRevision}.
 *
 * TODO: Replace type code with hierarchy
 * TODO: Rename class to Revision, getAuthor() to getLogin(), isDead() to isDeletion()
 * 
 * @author Manuel Schulze
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: Revision.java,v 1.3 2009/08/20 17:44:05 benoitx Exp $
 */
public class Revision implements Comparable {

    /**
     * Marks a revision that creates a new file. The file did not exist
     * in the current branch before this revision, and it does exist
     * afterwards. Possibly the file existed before, that is, it was
     * deleted and restored. 
     */
    public static final int TYPE_CREATION = 1;

    /**
     * Marks a revision that changes the file. It does neither create nor
     * delete the file.
     */
    public static final int TYPE_CHANGE = 2;

    /**
     * Marks a revision that deletes the file. The file existed before, but
     * does not exist afterwards in the current branch.
     */
    public static final int TYPE_DELETION = 3;

    /**
     * Marks a revision at the very beginning of the log timespan. This is
     * only a container for the number of code lines at the beginning of
     * the log. It is not a real revision committed by an author.
     */
    public static final int TYPE_BEGIN_OF_LOG = 5;

    private final VersionedFile file;
    private final String revisionNumber;
    private final int type;
    private final Author author;
    private final Date date;
    private final String comment;
    private final int lines;
    private final int linesReplaced;
    private final int linesDelta;

    private final SortedSet symbolicNames;

    /**
     * Creates a new revision of a file with the
     * specified revision number. Should not be called directly. Instead,
     * {@link VersionedFile#addInitialRevision} and its sister methods should
     * be used.
     * @param file VersionedFile that belongs to this revision
     * @param revisionNumber revision number, for example "1.1"
     * @param type a <tt>TYPE_XXX</tt> constant
     * @param author the author of the revision
     * @param date the date of the revision
     * @param comment the author's comment
     * @param lines number of lines; 0 for deletions
     * @param linesDelta by how much did the number of lines change, compared to the previous revision?
     * @param linesReplaced How many lines were removed and replaced by other lines, without the delta changing?
     * @param symbolicNames list of symbolic names for this revision or null if this revision has no symbolic names	 
     */
    public Revision(final VersionedFile file, final String revisionNumber, final int type, final Author author, final Date date, final String comment,
            final int lines, final int linesDelta, final int linesReplaced, final SortedSet symbolicNames) {
        this.file = file;
        this.revisionNumber = revisionNumber;
        this.type = type;
        this.author = author;
        this.date = date;
        this.comment = comment;
        this.lines = lines;
        this.linesDelta = linesDelta;
        this.linesReplaced = linesReplaced;
        this.symbolicNames = symbolicNames;

        if (author != null) {
            author.addRevision(this);
        }

        if (symbolicNames != null) {
            final Iterator it = symbolicNames.iterator();
            while (it.hasNext()) {
                ((SymbolicName) it.next()).addRevision(this);
            }
        }
    }

    /**
     * Returns the revision number.
     * @return the revision number
     */
    public String getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * Returns the author of this revision.
     * @return the author
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * Returns the comment for this revision.
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns the date of this revision.
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the number of lines for this revision. This is 0 for
     * dead revisions.
     * 
     * @return the number of lines
     */
    public int getLines() {
        return lines;
    }

    /**
     * Returns by how many lines the line count changed with this
     * revision. Deletions return <code>-getLines()</code>,
     * re-adds and initial revisions return <code>getLines()</code>.
     * 
     * @return the line count change of this revision
     */
    public int getLinesDelta() {
        return linesDelta;
    }

    /**
     * Returns the number of lines that were removed and replaced
     * by other lines in this revision. For example, if 5 lines were
     * added and 2 lines removed, this would be 3. If 1 line was added
     * and 1 was removed, it would be 1. If it was an initial revision
     * or a deletion, it would be 0.
     * 
     * @return the number of lines that were replaced by other lines.
     */
    public int getReplacedLines() {
        return linesReplaced;
    }

    /**
     * Returns the number of "new" lines in this revision. This is the
     * sum of added and changed lines. In other words, all the "original"
     * lines the author of this revision came up with.
     * @return lines changed or added
     */
    public int getNewLines() {
        if (getLinesDelta() > 0) {
            return getLinesDelta() + getReplacedLines();
        }
        return getReplacedLines();
    }

    /**
     * Returns the change of the file count caused by this revision.
     * This is 1 for initial revisions and re-adds, -1 for deletions,
     * and 0 for normal revisions.
     * @return the file count change of this revision
     */
    public int getFileCountDelta() {
        if (isInitialRevision()) {
            return 1;
        } else if (isDead()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Returns <code>true</code> if the file did not exist before this
     * revision and does exist afterwards. Possibly the file was deleted
     * before, or it never existed before.
     * 
     * @return <code>true</code> if the file did not exist before
     */
    public boolean isInitialRevision() {
        return type == TYPE_CREATION;
    }

    /**
     * Returns <tt>true</tt> if the file is deleted in this revision.
     * @return <code>true</code> if the file is deleted in this revision
     */
    public boolean isDead() {
        return type == TYPE_DELETION;
    }

    /**
     * Returns <tt>true</tt> if this is a revision
     * at the very beginning of the log timespan which is
     * only a container for the number of code lines at the beginning
     * of the log and not a real revision committed by an author.
     * @return <code>true</code> if this revision exists
     * only for StatCvs bookkeeping purposes
     */
    public boolean isBeginOfLog() {
        return type == TYPE_BEGIN_OF_LOG;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.author.getName() + " - " + this.revisionNumber;
    }

    /**
     * Returns the file which was changed by this revision.
     * @return the file
     */
    public VersionedFile getFile() {
        return file;
    }

    /**
     * Returns the predecessor of this revision or <tt>null</tt> if it
     * is the first revision for the file.
     * @return the predecessor of this revision
     */
    public Revision getPreviousRevision() {
        return file.getPreviousRevision(this);
    }

    /**
     * Returns a list of {@link SymbolicName}s of this revision or null if
     * the revision has no symbolic names. The list is ordered from 
     * latest to oldest.
     *
     * @return list of symbolic names 
     */
    public SortedSet getSymbolicNames() {
        return symbolicNames;
    }

    /**
     * Compares this revision to another revision. A revision is considered
     * smaller if its date is smaller. If the dates are identical, the filename,
     * author name, revision number and comment will be used to break the tie.
     */
    public int compareTo(final Object other) {
        if (this == other) {
            return 0;
        }
        final Revision otherRevision = (Revision) other;
        int result = date.compareTo(otherRevision.getDate());
        if (result != 0) {
            return result;
        }
        result = file.getFilenameWithPath().compareTo(otherRevision.getFile().getFilenameWithPath());
        if (result != 0) {
            return result;
        }
        result = revisionNumber.compareTo(otherRevision.getRevisionNumber());
        if (result != 0) {
            return result;
        }
        if (author != null && otherRevision.getAuthor() != null) {
            result = author.compareTo(otherRevision.getAuthor());
            if (result != 0) {
                return result;
            }
        }
        if (comment != null && otherRevision.getComment() != null) {
            return comment.compareTo(otherRevision.getComment());
        }
        return 1;
    }

    public boolean equals(final Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (!(rhs instanceof Revision)) {
            return false;
        }
        final Revision that = (Revision) rhs;

        boolean eq = getDate().equals(that.getDate());

        if (eq) {
            eq = file.getFilenameWithPath().equals(that.file.getFilenameWithPath());
        }
        if (eq) {
            eq = revisionNumber.equals(that.getRevisionNumber());
        }
        if (eq) {
            eq = author != null && author.equals(that.getAuthor());
        }
        return eq;
    }

    public int hashCode() {
        return getDate().hashCode() + file.hashCode() + revisionNumber.hashCode();
    }

    //TODO: remove all deprecated methods when they are no longer used by StatCvs-XML

    /**
     * @deprecated Use {@link #getLinesDelta()} and {@link #getReplacedLines()} instead.
     */
    public int getLinesAdded() {
        if (isInitialRevision() && getPreviousRevision() != null) {
            return 0;
        }
        return getNewLines();
    }

    /**
     * @deprecated Use {@link #getLinesDelta()} and {@link #getReplacedLines()} instead.
     */
    public int getLinesRemoved() {
        if (isDead()) {
            return 0;
        }
        if (getLinesDelta() < 0) {
            return -getLinesDelta() + getReplacedLines();
        }
        return getReplacedLines();
    }

    /**
     * @deprecated Use {@link #getLines()} instead.
     */
    public int getLinesOfCode() {
        if (isDead() && getPreviousRevision() != null) {
            return getPreviousRevision().getLines();
        }
        return getLines();
    }

    /**
     * @deprecated Use {@link #getLines()} instead.
     */
    public int getEffectiveLinesOfCode() {
        return getLines();
    }

    /**
     * @deprecated Use {@link #getLinesDelta()} instead.
     */
    public int getLinesOfCodeChange() {
        return getLinesDelta();
    }

    /**
     * @deprecated Use {@link #getNewLines()} instead.
     */
    public int getLineValue() {
        return getNewLines();
    }

    /**
     * @deprecated Use {@link #getReplacedLines()} and {@link #getLinesDelta} instead.
     */
    public int getRemovingValue() {
        if (getLinesDelta() > 0) {
            return getReplacedLines();
        }
        return -getLinesDelta() + getReplacedLines();
    }

    /**
     * @deprecated Use {@link #getFileCountDelta()} instead.
     */
    public int getFileCountChange() {
        return getFileCountDelta();
    }

    /**
     * @deprecated Use {@link #getRevisionNumber()} instead.
     */
    public String getRevision() {
        return getRevisionNumber();
    }
}
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
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a commit, which may consist of several {@link Revision}
 * objects. A commit means that several files were committed at once by the
 * same author with the same message.
 * 
 * TODO: Rename getAuthor() to getLogin(), getAffectedFiles() to getAffectedFileNames() (or change to return CvsFiles?)
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: Commit.java,v 1.17 2009/08/20 17:44:05 benoitx Exp $
 */
public class Commit implements Comparable {
    private final Set revisions = new HashSet();
    private final Revision aRevision;

    /**
     * Creates a new instance which consists of the given revision.
     * @param revision the single revision out of which the commit will
     * 				   be created
     */
    public Commit(final Revision revision) {
        revisions.add(revision);
        aRevision = revision;
    }

    /**
     * Adds a revision to the commit. The revision must be part of the
     * commit, that is, it must have the same date, author and message
     * as all other revisions in the commit.
     * @param revision the <code>Revision</code> to add.
     */
    public void addRevision(final Revision revision) {
        revisions.add(revision);
    }

    /**
     * Returns the {@link Revision} objects that make up this commit.
     * 
     * @return a set of <tt>Revision</tt> instances
     */
    public Set getRevisions() {
        return revisions;
    }

    /**
     * Returns the author of the commit.
     * @return the author
     */
    public Author getAuthor() {
        return aRevision.getAuthor();
    }

    /**
     * Returns the comment of the commit.
     * @return the comment
     */
    public String getComment() {
        return aRevision.getComment();
    }

    /**
     * Returns the date when the commit took place. The implementation
     * simply returns the timestamp of the first change of the commit.
     * @return a date within the timeframe of the commit
     */
    public Date getDate() {
        return aRevision.getDate();
    }

    /**
     * Returns a <code>String</code> <code>Set</code> containing all filenames
     * which were affected by this <code>Commit</code>.
     * @return a <code>Set</code> of <code>String</code>s
     */
    public Set getAffectedFiles() {
        final Set result = new HashSet();
        final Iterator it = revisions.iterator();
        while (it.hasNext()) {
            final Revision element = (Revision) it.next();
            result.add(element.getFile().getFilenameWithPath());
        }
        return result;
    }

    /**
     * Compares this commit to another revision, based on their date. 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object other) {
        final Commit otherCommit = (Commit) other;
        return getDate().compareTo(otherCommit.getDate());
    }
    
    public boolean equals(final Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (!(rhs instanceof Commit)) {
            return false;
        }
        final Commit that = (Commit) rhs;
        if (this.getDate() == null || that.getDate() == null) {
            return false;
        }
        return this.getDate().equals(that.getDate());
    }
    
    public int hashCode() {
        return getDate().hashCode();
    }
}
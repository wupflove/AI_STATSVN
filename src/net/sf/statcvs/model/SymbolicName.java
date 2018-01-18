/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sf.statcvs.model;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a symbolic name (tags).
 * It is a container for {@link Revision}s.
 * 
 * @author Tammo van Lessen
 * @version $Id: SymbolicName.java,v 1.6 2009/08/20 17:44:05 benoitx Exp $
 */
public class SymbolicName implements Comparable {
    private final String name;
    private final SortedSet revisions = new TreeSet();
    private Date date = null;

    /**
     * Creates a new symbolic name.
     * 
     * @param name the symbolic name's name
     * @param date of the symbolic name creation
     */
    public SymbolicName(final String name, final Date date) {
        this.name = name;
        this.date = date;
    }

    /**
     * Creates a new symbolic name. With this constructor,
     * its creation date will be assumed to be the date
     * of its latest revision.
     * 
     * @param name the symbolic name's name
     */
    public SymbolicName(final String name) {
        this.name = name;
    }

    /**
     * Returns the symbolic name's name.
     * 
     * @return the symbolic name's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a revision to this symbolic name.
     * 
     * @param rev the revision
     */
    protected void addRevision(final Revision rev) {
        revisions.add(rev);
    }

    /**
     * Returns a set of {@link Revision}s contained in this symbolic name.
     * 
     * @return the revisions
     */
    public SortedSet getRevisions() {
        return revisions;
    }

    /**
     * Returns the 'date' of this symbolic name.
     *  
     * @return the smbolic name's date
     */
    public Date getDate() {
        if (date != null) {
            return date;
        }
        if (revisions.isEmpty()) {
            return null;
        }
        return ((Revision) revisions.last()).getDate();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object o) {
        final SymbolicName other = (SymbolicName) o;
        if (getDate() == null || other.getDate() == null) {
            return getName().compareTo(other.getName());
        }
        final int dateComp = getDate().compareTo(other.getDate());
        return (dateComp != 0) ? dateComp : getName().compareTo(other.getName());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name + "[" + getDate() + "] (" + revisions.size() + " revisions)";
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if (obj instanceof SymbolicName) {
            final SymbolicName other = (SymbolicName) obj;
            return ("" + getDate() + getName()).equals("" + other.getDate() + other.getName());
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return ("" + getDate() + getName()).hashCode();
    }
}

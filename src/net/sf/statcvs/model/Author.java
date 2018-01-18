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

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents an author of one or more {@link Revision}s in a repository.
 * 
 * TODO: Rename to <tt>Login</tt>
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: Author.java,v 1.16 2009/08/20 17:44:05 benoitx Exp $
 */
public class Author implements Comparable {
    private final String name;
    private final SortedSet revisions = new TreeSet();
    private final SortedSet directories = new TreeSet();
    private String realName;
    private String homePageUrl;
    private String imageUrl;
    private String email;
    private String twitterUserName;
    private String twitterUserId;
    private boolean twitterIncludeHtml = false;
    private boolean twitterIncludeFlash = false;

    /**
     * Creates a new author.
     * @param name the author's login name
     */
    public Author(final String name) {
        this.name = name;
        this.realName = name;
    }

    /**
     * Adds a revision for this author; called by {@link Revision} constructor
     * @param revision a revision committed by this author
     */
    protected void addRevision(final Revision revision) {
        revisions.add(revision);
        directories.add(revision.getFile().getDirectory());
    }

    /**
     * Returns the author's login name.
     * @return the author's login name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all {@link Revision}s of this author, sorted from oldest
     * to most recent.
     * @return all revisions of this author
     */
    public SortedSet getRevisions() {
        return revisions;
    }

    /**
     * Returns all {@link Directory}s the author
     * has committed a change to, sorted by name.
     * @return a set of <tt>Directory</tt> objects
     */
    public SortedSet getDirectories() {
        return directories;
    }

    /**
     * Compares the instance to another author, using their login names.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object o) {
        return name.compareTo(((Author) o).getName());
    }
    
    public boolean equals(final Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (!(rhs instanceof Author)) {
            return false;
        }
        final Author that = (Author) rhs;
        if (this.getName() == null || that.getName() == null) {
            return false;
        }
        return this.getName().equals(that.getName());
    }
    
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return realName + "(" + revisions.size() + " revisions)";
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public void setHomePageUrl(final String homePageUrl) {
        this.homePageUrl = homePageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(final String realName) {
        if (realName != null) {
            this.realName = realName;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTwitterUserName() {
        return twitterUserName;
    }

    public void setTwitterUserName(final String twitterUserName) {
        this.twitterUserName = twitterUserName;
    }

    public String getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(final String twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public boolean isTwitterIncludeHtml() {
        return twitterIncludeHtml;
    }

    public void setTwitterIncludeHtml(final boolean twitterIncludeHtml) {
        this.twitterIncludeHtml = twitterIncludeHtml;
    }

    public boolean isTwitterIncludeFlash() {
        return twitterIncludeFlash;
    }

    public void setTwitterIncludeFlash(final boolean twitterIncludeFlash) {
        this.twitterIncludeFlash = twitterIncludeFlash;
    }
}
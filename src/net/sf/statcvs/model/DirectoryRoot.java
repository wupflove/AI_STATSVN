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
    
	$RCSfile: DirectoryRoot.java,v $
	$Date: 2008/04/02 11:22:16 $
*/
package net.sf.statcvs.model;

/**
 * The root of a tree of <tt>Directory</tt> objects. To create an instance
 * of this class, call {@link Directory#createRoot}.
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: DirectoryRoot.java,v 1.4 2008/04/02 11:22:16 benoitx Exp $
 */
class DirectoryRoot extends Directory {

    /**
     * Use {@link Directory#createRoot} to construct instances of this class.
     *
     */
    DirectoryRoot() {
        // no code, we just want the Javadoc comment
    }

    /**
     * @see net.sf.statcvs.model.Directory#getName()
     */
    public String getName() {
        return "";
    }

    /**
     * @see net.sf.statcvs.model.Directory#getPath()
     */
    public String getPath() {
        return "";
    }

    /**
     * @see net.sf.statcvs.model.Directory#getParent()
     */
    public Directory getParent() {
        return null;
    }

    /**
     * @see net.sf.statcvs.model.Directory#isRoot()
     */
    public boolean isRoot() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "root directory";
    }

    /**
     * @see net.sf.statcvs.model.Directory#getDepth()
     */
    public int getDepth() {
        return 0;
    }
}
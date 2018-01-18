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
    
	$RCSfile: FilesLocComparator.java,v $
	$Date: 2009/08/20 17:44:05 $
*/
package net.sf.statcvs.reports;

import java.io.Serializable;
import java.util.Comparator;

import net.sf.statcvs.model.VersionedFile;

/**
 * Compares two files according to their lines of code
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: FilesLocComparator.java,v 1.4 2009/08/20 17:44:05 benoitx Exp $
 */
public class FilesLocComparator implements Comparator, Serializable {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final Object o1, final Object o2) {
        final VersionedFile file1 = (VersionedFile) o1;
        final VersionedFile file2 = (VersionedFile) o2;
        if (file1.getCurrentLinesOfCode() < file2.getCurrentLinesOfCode()) {
            return 1;
        } else if (file1.getCurrentLinesOfCode() == file2.getCurrentLinesOfCode()) {
            return 0;
        } else {
            return -1;
        }
    }
}

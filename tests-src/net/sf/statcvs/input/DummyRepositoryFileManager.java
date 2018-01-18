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
    
	$RCSfile: DummyRepositoryFileManager.java,v $ 
	Created on $Date: 2008/04/02 11:22:14 $ 
*/
package net.sf.statcvs.input;

import java.util.HashMap;

/**
 * Dummy <tt>RepositoryFileManager</tt> for unit tests
 * 
 * @author Manuel Schulze
 * @version $Id: DummyRepositoryFileManager.java,v 1.2 2008/04/02 11:22:14 benoitx Exp $
 */
public class DummyRepositoryFileManager extends RepositoryFileManager {

    private final HashMap linesForFile = new HashMap();

    /**
     * @see java.lang.Object#Object()
     */
    public DummyRepositoryFileManager() {
        super("foo");
    }

    /**
     * Sets the number of lines of code for specified file
     * @param filename of file to change
     * @param lines lines of code for specified file
     */
    public void setLinesOfCode(final String filename, final int lines) {
        linesForFile.put(filename, new Integer(lines));
    }

    /**
     * @see net.sf.statcvs.input.RepositoryFileManager#getLinesOfCode(String)
     */
    public int getLinesOfCode(final String filename) throws NoLineCountException {
        if (linesForFile.containsKey(filename)) {
            return ((Integer) linesForFile.get(filename)).intValue();
        }
        throw new NoLineCountException();
    }
}

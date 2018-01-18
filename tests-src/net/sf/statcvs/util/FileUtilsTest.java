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
    
	$Name:  $ 
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.util;

import junit.framework.TestCase;

/**
 * Test cases for {@link net.sf.statcvs.util.FileUtils}
 *
 * @author Richard Cyganiak
 * @version $Id: FileUtilsTest.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class FileUtilsTest extends TestCase {

    /**
     * Constructor for OutputUtilsTest.
     * @param arg0 input 
     */
    public FileUtilsTest(final String arg0) {
        super(arg0);
    }

    /**
     * Tests {@link FileUtils#getDirectoryName}
     */
    public void testGetDirectoryName() {
        assertEquals("statcvs", FileUtils.getDirectoryName("net/sf/statcvs/"));
        assertEquals("sf", FileUtils.getDirectoryName("net/sf/"));
        assertEquals("net", FileUtils.getDirectoryName("net/"));
        try {
            FileUtils.getDirectoryName("");
            fail("can't get directory name for root");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests {@link FileUtils#getDirectoryName}
     */
    public void testGetParentDirectoryPath() {
        assertEquals("net/sf/", FileUtils.getParentDirectoryPath("net/sf/statcvs/"));
        assertEquals("net/", FileUtils.getParentDirectoryPath("net/sf/"));
        assertEquals("", FileUtils.getParentDirectoryPath("net/"));
        try {
            FileUtils.getParentDirectoryPath("");
            fail("can't get parent directory for root");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }
}
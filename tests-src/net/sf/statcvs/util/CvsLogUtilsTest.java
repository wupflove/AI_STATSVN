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
 * Test cases for {link net.sf.statcvs.util.CvsLogUtils}
 * 
 * @author Richard Cyganiak
 * @version $Id: CvsLogUtilsTest.java,v 1.4 2008/04/02 11:22:15 benoitx Exp $
 */
public class CvsLogUtilsTest extends TestCase {

    /**
     * Constructor
     * @param arg0 input 
     */
    public CvsLogUtilsTest(final String arg0) {
        super(arg0);
    }

    /**
     * Tests {@link CvsLogUtil#isInAttic}
     */
    public void testIsInAttic() {
        assertTrue(!CvsLogUtils.isInAttic("/cvsroot/module/file,v", "file"));
        assertTrue(CvsLogUtils.isInAttic("/cvsroot/module/Attic/file,v", "file"));
        assertTrue(!CvsLogUtils.isInAttic("/cvsroot/module/path/file,v", "path/file"));
        assertTrue(CvsLogUtils.isInAttic("/cvsroot/module/path/Attic/file,v", "path/file"));
        assertTrue(!CvsLogUtils.isInAttic("/cvsroot/module/attic/file,v", "attic/file"));
    }

    /**
     * test {@link CvsLogUtil#isOnMainBranch(String)}
     */
    public void testMainBranchRevisions() {
        assertTrue(CvsLogUtils.isOnMainBranch("1.1"));
        assertTrue(CvsLogUtils.isOnMainBranch("5.12"));
        assertTrue(!CvsLogUtils.isOnMainBranch("1.1.1.1"));
        assertTrue(!CvsLogUtils.isOnMainBranch("5.12.2.4"));
        assertTrue(!CvsLogUtils.isOnMainBranch("5.12.2.4.4.11"));
    }

    /**
     * Test {@link CvsLogUtil#getModuleName}
     */
    public void testGetModuleName() {
        assertEquals("cvsroot", CvsLogUtils.getModuleName("/cvsroot/module/file,v", "module/file"));
        assertEquals("cvsroot", CvsLogUtils.getModuleName("/cvsroot/module/Attic/file,v", "module/file"));
        assertEquals("module", CvsLogUtils.getModuleName("/cvsroot/module/file,v", "file"));
        assertEquals("module", CvsLogUtils.getModuleName("/cvsroot/module/Attic/file,v", "file"));
        assertEquals("", CvsLogUtils.getModuleName("/file,v", "file"));
    }
}

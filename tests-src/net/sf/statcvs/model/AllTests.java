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
    
	$RCSfile: AllTests.java,v $ 
	Created on $Date: 2002/08/23 02:04:05 $ 
*/
package net.sf.statcvs.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test suite for the model package
 * 
 * @author Manuel Schulze
 * @version $Id: AllTests.java,v 1.14 2002/08/23 02:04:05 cyganiak Exp $
 */
public class AllTests {

    /**
     * Method suite.
     * @return Test suite
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite("Test for net.sf.statcvs.model");
        //$JUnit-BEGIN$
        suite.addTestSuite(CommitTest.class);
        suite.addTestSuite(AuthorTest.class);
        suite.addTestSuite(DirectoryTest.class);
        suite.addTestSuite(CvsFileTest.class);
        suite.addTestSuite(CvsContentTest.class);
        suite.addTestSuite(RevisionTest.class);
        //$JUnit-END$
        return suite;
    }
}

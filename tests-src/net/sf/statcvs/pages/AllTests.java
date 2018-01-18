package net.sf.statcvs.pages;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: AllTests.java,v 1.2 2008/04/02 11:22:16 benoitx Exp $
 */
public class AllTests {

    public static Test suite() {
        final TestSuite suite = new TestSuite("Test for net.sf.statcvs.pages");
        //$JUnit-BEGIN$
        suite.addTestSuite(HTMLTest.class);
        //$JUnit-END$
        return suite;
    }

}

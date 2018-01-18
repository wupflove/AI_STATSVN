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
	Created on $Date: 2002/08/17 21:24:55 $ 
*/
package net.sf.statcvs.model;

import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

/**
 * @author Richard Cyganiak
 * @version $Id: CvsContentTest.java,v 1.3 2002/08/17 21:24:55 cyganiak Exp $
 */
public class CvsContentTest extends TestCase {
    private Author tester1;
    private Author tester2;
    private Author tester3;
    private Author tester4;
    private Directory dirRoot;
    private Directory dirTest;
    private Directory dirTest1;
    private final Date date = new Date(100000000);

    /**
     * Constructor for CvsContentTest.
     * @param arg0 input
     */
    public CvsContentTest(final String arg0) {
        super(arg0);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        tester1 = new Author("tester1");
        tester2 = new Author("tester2");
        tester3 = new Author("tester3");
        tester4 = new Author("tester4");
        dirRoot = Directory.createRoot();
        dirTest = dirRoot.createSubdirectory("test");
        dirTest1 = dirRoot.createSubdirectory("test1");
    }

    /**
     * Method testGetDirectories.
     */
    public void testGetDirectories() {
        final VersionedFile file1 = new VersionedFile("test/test1.java", dirTest);
        file1.addInitialRevision("1.1", tester1, date, null, 0, null);
        final VersionedFile file2 = new VersionedFile("test2.java", dirRoot);
        file2.addInitialRevision("1.1", tester1, date, null, 0, null);
        final VersionedFile file3 = new VersionedFile("test1/test3.java", dirTest1);
        file3.addInitialRevision("1.1", tester1, date, null, 0, null);
        final VersionedFile file4 = new VersionedFile("test/test2.java", dirTest);
        file4.addInitialRevision("1.1", tester1, date, null, 0, null);
        final VersionedFile file5 = new VersionedFile("test1/test1.java", dirTest1);
        file5.addInitialRevision("1.1", tester1, date, null, 0, null);
        final VersionedFile file6 = new VersionedFile("test/test3.java", dirTest);
        file6.addInitialRevision("1.1", tester1, date, null, 0, null);

        final Repository content = new Repository();
        content.addFile(file1);
        content.addFile(file2);
        content.addFile(file3);
        content.addFile(file4);
        content.addFile(file5);
        content.addFile(file6);

        final Collection dirs = content.getDirectories();

        assertEquals(3, dirs.size());
        assertTrue(dirs.contains(dirTest));
        assertTrue(dirs.contains(dirTest1));
        assertTrue(dirs.contains(dirRoot));
    }

    /**
     * Method testGetDirectoriesPerUser.
     */
    public void testGetDirectoriesPerUser() {
        final VersionedFile file1 = new VersionedFile("test/test1.java", dirTest);
        file1.addChangeRevision("1.2", tester1, date, null, 0, 0, 0, null);
        file1.addInitialRevision("1.1", tester2, date, null, 0, null);
        final VersionedFile file2 = new VersionedFile("test2.java", dirRoot);
        file2.addChangeRevision("2.3", tester1, date, null, 0, 0, 0, null);
        file2.addChangeRevision("2.2", tester1, date, null, 0, 0, 0, null);
        file2.addInitialRevision("2.1", tester3, date, null, 0, null);
        final VersionedFile file3 = new VersionedFile("test1/test3.java", dirTest1);
        file3.addInitialRevision("3.1", tester2, date, null, 0, null);
        final VersionedFile file4 = new VersionedFile("test/test2.java", dirTest);
        file4.addInitialRevision("4.1", tester2, date, null, 0, null);
        final VersionedFile file5 = new VersionedFile("test1/test1.java", dirTest1);
        file5.addChangeRevision("5.3", tester2, date, null, 0, 0, 0, null);
        file5.addChangeRevision("5.2", tester2, date, null, 0, 0, 0, null);
        file5.addInitialRevision("5.1", tester2, date, null, 0, null);
        final VersionedFile file6 = new VersionedFile("test/test3.java", dirTest);
        file6.addChangeRevision("6.2", tester1, date, null, 0, 0, 0, null);
        file6.addInitialRevision("6.1", tester3, date, null, 0, null);

        final Repository content = new Repository();
        content.addFile(file1);
        content.addFile(file2);
        content.addFile(file3);
        content.addFile(file4);
        content.addFile(file5);
        content.addFile(file6);

        Collection dirs = tester1.getDirectories();
        assertEquals(2, dirs.size());
        assertTrue(dirs.contains(dirTest));
        assertTrue(dirs.contains(dirRoot));

        dirs = tester2.getDirectories();
        assertEquals(2, dirs.size());
        assertTrue(dirs.contains(dirTest));
        assertTrue(dirs.contains(dirTest1));

        dirs = tester3.getDirectories();
        assertEquals(2, dirs.size());
        assertTrue(dirs.contains(dirRoot));
        assertTrue(dirs.contains(dirTest));
    }

    /**
     * Method testUserNames.
     */
    public void testUserNames() {
        final VersionedFile file1 = new VersionedFile("test/Burg.java", dirTest);
        file1.addChangeRevision("1.3", tester1, date, null, 0, 0, 0, null);
        file1.addChangeRevision("1.2", tester2, date, null, 0, 0, 0, null);
        file1.addInitialRevision("1.1", tester1, date, null, 0, null);
        final VersionedFile file2 = new VersionedFile("test/History.java", dirTest);
        file2.addChangeRevision("2.2", tester3, date, null, 0, 0, 0, null);
        file2.addInitialRevision("2.1", tester4, date, null, 0, null);
        final VersionedFile file3 = new VersionedFile("test/Spieler.java", dirTest);
        file3.addChangeRevision("3.4", tester2, date, null, 0, 0, 0, null);
        file3.addChangeRevision("3.3", tester4, date, null, 0, 0, 0, null);
        file3.addChangeRevision("3.2", tester1, date, null, 0, 0, 0, null);
        file3.addInitialRevision("3.1", tester2, date, null, 0, null);
        final Repository content = new Repository();
        content.addFile(file1);
        content.addFile(file2);
        content.addFile(file3);

        assertEquals(4, content.getAuthors().size());
    }

    /**
     * The null author used for "begin of log" revisions must not be included
     * in the authors list
     */
    public void testIgnoreNullAuthor() {
        final VersionedFile file = new VersionedFile("test/file", dirTest);
        file.addInitialRevision("0.0", null, date, null, 0, null);
        final Repository content = new Repository();
        content.addFile(file);

        assertTrue(content.getAuthors().isEmpty());
    }
}

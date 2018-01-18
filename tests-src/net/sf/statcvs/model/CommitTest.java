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
	Created on $Date: 2002/08/23 02:04:06 $ 
*/
package net.sf.statcvs.model;

import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Richard Cyganiak
 * @version $Id: CommitTest.java,v 1.6 2002/08/23 02:04:06 cyganiak Exp $
 */
public class CommitTest extends TestCase {

    private static final int DATE = 10000000;

    private VersionedFile file1;
    private VersionedFile file2;
    private VersionedFile file3;
    private VersionedFile file4;
    private VersionedFile file5;
    private Revision rev1;
    private Revision rev2;
    private Revision rev4;
    private Revision rev5;
    private Revision rev6;
    private Revision rev7;
    private Revision rev8;
    private Commit commit;
    private Author author1;
    private Author author2;

    /**
     * Constructor for CommitTest.
     * @param arg0 input
     */
    public CommitTest(final String arg0) {
        super(arg0);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        author1 = new Author("author1");
        author2 = new Author("author2");
        final Directory root = Directory.createRoot();
        file1 = new VersionedFile("file1", root);
        file2 = new VersionedFile("file2", root);
        file3 = new VersionedFile("file3", root);
        file4 = new VersionedFile("file4", root);
        file5 = new VersionedFile("file5", root);
        rev1 = createRevision(file1, "rev1", DATE, author1, "message1");
        rev2 = createRevision(file5, "rev2", DATE + 100, author2, "message1");
        rev4 = createRevision(file2, "rev4", DATE + 100000, author1, "message1");
        rev5 = createRevision(file3, "rev5", DATE + 200000, author1, "message1");
        rev6 = createRevision(file1, "rev6", DATE + 400000, author1, "message1");
        rev7 = createRevision(file2, "rev7", DATE + 650000, author1, "message1");
        rev8 = createRevision(file4, "rev8", DATE + 900000, author1, "message1");
    }

    /**
     * Method testCreation.
     */
    public void testCreation() {
        commit = new Commit(rev1);
        assertEquals(author1, commit.getAuthor());
        assertEquals("message1", commit.getComment());
        assertEquals(rev1.getDate(), commit.getDate());
        assertEquals(1, commit.getRevisions().size());
        assertTrue(commit.getRevisions().contains(rev1));
    }

    /**
     * Method testAddAfter.
     */
    public void testAddAfter() {
        commit = new Commit(rev6);
        commit.addRevision(rev7);
        assertEquals(author1, commit.getAuthor());
        assertEquals("message1", commit.getComment());
        assertEquals(rev6.getDate(), commit.getDate());
        assertEquals(2, commit.getRevisions().size());
        assertTrue("should contain rev6", commit.getRevisions().contains(rev6));
        assertTrue("should contain rev7", commit.getRevisions().contains(rev7));
    }

    /**
     * Method testAddBefore.
     */
    public void testAddBefore() {
        commit = new Commit(rev6);
        commit.addRevision(rev5);
        assertEquals(author1, commit.getAuthor());
        assertEquals("message1", commit.getComment());
        assertEquals(rev6.getDate(), commit.getDate());
        assertEquals(2, commit.getRevisions().size());
        assertTrue("should contain rev6", commit.getRevisions().contains(rev6));
        assertTrue("should contain rev5", commit.getRevisions().contains(rev5));
    }

    /**
     * Method testAffectedFiles.
     */
    public void testAffectedFiles() {
        commit = new Commit(rev1);
        commit.addRevision(rev4);
        commit.addRevision(rev5);
        commit.addRevision(rev6);
        commit.addRevision(rev7);
        commit.addRevision(rev8);
        final Set affectedFiles = commit.getAffectedFiles();
        assertEquals(4, affectedFiles.size());
        assertTrue("should contain file1", affectedFiles.contains("file1"));
        assertTrue("should contain file2", affectedFiles.contains("file2"));
        assertTrue("should contain file3", affectedFiles.contains("file3"));
        assertTrue("should contain file4", affectedFiles.contains("file4"));
    }

    public void testCompareTo() {
        final Commit commit1 = new Commit(rev1);
        final Commit commit2 = new Commit(rev1);
        final Commit commit3 = new Commit(rev2);
        assertEquals(0, commit1.compareTo(commit2));
        assertEquals(0, commit2.compareTo(commit1));
        assertTrue(commit1.compareTo(commit3) < 0);
        assertTrue(commit3.compareTo(commit1) > 0);
    }

    private Revision createRevision(final VersionedFile file, final String revision, final long time, final Author author, final String message) {
        return new Revision(file, revision, Revision.TYPE_CHANGE, author, new Date(time), message, 0, 0, 0, null);
    }
}

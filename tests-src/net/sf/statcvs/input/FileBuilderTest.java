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
package net.sf.statcvs.input;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;

/**
 * Tests for {@link FileBuilder}
 * 
 * TODO: Add tests for all revision types
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: FileBuilderTest.java,v 1.8 2008/04/02 11:22:14 benoitx Exp $
 */
public class FileBuilderTest extends TestCase {
    private FileBuilder fb;
    private final DummyBuilder builder;
    private final Date date1 = new Date(100000000);
    private final Date date2 = new Date(200000000);
    private final RevisionData rev1;
    private final RevisionData rev1dead;
    private final RevisionData rev1branch;

    public FileBuilderTest(final String arg0) {
        super(arg0);
        builder = new DummyBuilder();
        rev1 = new RevisionData();
        rev1.setDate(date1);
        rev1.setRevisionNumber("1.1");
        rev1.setLoginName("author1");
        rev1.setComment("comment");
        rev1.setStateExp();
        rev1dead = new RevisionData();
        rev1dead.setDate(date1);
        rev1dead.setRevisionNumber("1.1");
        rev1dead.setLoginName("author1");
        rev1dead.setComment("comment");
        rev1dead.setStateDead();
        rev1branch = new RevisionData();
        rev1branch.setDate(date2);
        rev1branch.setRevisionNumber("1.1.2.1");
        rev1branch.setLoginName("author1");
        rev1branch.setComment("comment");
        rev1branch.setStateExp();
        rev1branch.setLines(100, 0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreation() throws Exception {
        initBuilder("file", false);
        fb.createFile(date1);
    }

    public void testNotInLogTimespan() throws Exception {
        initBuilder("nolinecount", false);
        assertNull(fb.createFile(null));
    }

    public void testSimple() throws Exception {
        initBuilder("file", false);
        fb.addRevisionData(rev1);
        final VersionedFile file = fb.createFile(date1);
        assertNotNull(file);
        assertEquals("file", file.getFilenameWithPath());
        assertEquals(builder.getDirectory("file"), file.getDirectory());
        assertEquals(1, file.getRevisions().size());
    }

    public void testFileWithoutRevs() throws Exception {
        initBuilder("file", false);
        final VersionedFile file = fb.createFile(date1);
        assertNotNull(file);
        assertEquals(1, file.getRevisions().size());
        assertTrue(file.getInitialRevision().isBeginOfLog());
        assertEquals(100, file.getInitialRevision().getLines());
        assertEquals(new Date(date1.getTime() - 60000), file.getInitialRevision().getDate());
    }

    public void testOneRev() throws Exception {
        initBuilder("file", false);
        fb.addRevisionData(rev1);
        final VersionedFile file = fb.createFile(date1);
        assertEquals(1, file.getRevisions().size());
        final Revision rev = file.getInitialRevision();
        assertEquals(date1, rev.getDate());
        assertEquals("1.1", rev.getRevisionNumber());
        assertEquals(builder.getAuthor("author1"), rev.getAuthor());
        assertEquals("comment", rev.getComment());
        assertEquals(100, rev.getLines());
        assertTrue(rev.isInitialRevision());
    }

    /**
     * A file added only on a subbranch and not merged
     * into the trunk must be ignored.
     * @throws Exception
     */
    public void testAddOnSubbranch() throws Exception {
        initBuilder("nolinecount", false);
        fb.addRevisionData(rev1dead);
        fb.addRevisionData(rev1branch);
        final VersionedFile file = fb.createFile(date1);
        assertNull(file);
    }

    public void testIgnoreRevisionsOnBranches() throws Exception {
        initBuilder("file", false);
        fb.addRevisionData(rev1);
        fb.addRevisionData(rev1branch);
        final VersionedFile file = fb.createFile(date1);
        assertEquals(1, file.getRevisions().size());
        assertEquals("1.1", file.getInitialRevision().getRevisionNumber());
    }

    private void initBuilder(final String filename, final boolean isBinary) {
        fb = new FileBuilder(builder, filename, isBinary, new HashMap());
    }
}
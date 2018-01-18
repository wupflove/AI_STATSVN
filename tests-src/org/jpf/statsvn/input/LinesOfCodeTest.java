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
    
	$RCSfile: LinesOfCodeTest.java,v $ 
	Created on $Date: 2004/12/14 13:38:13 $ 
*/

package net.sf.statsvn.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;

/**
 * Test cases for {@link Builder}, covering LOC calculations.
 * 
 * @todo most/all of them test only the FileBuilder -- refactor!
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @see BuilderTest
 * @version $Id: LinesOfCodeTest.java,v 1.18 2004/12/14 13:38:13 squig Exp $
 */
public class LinesOfCodeTest extends TestCase {
	private Builder builder;

	private VersionedFile file;

	private Date date11;

	private Date date12;

	private Date date13;

	private Date date14;

	private Date date15;

	private Revision rev0;

	private Revision rev1;

	private Revision rev2;

	private Revision rev3;

	private Revision rev4;

	private DummyRepositoryFileManager fileman;

	/**
	 * Constructor
	 * @param arg0 input
	 */
	public LinesOfCodeTest(final String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		fileman = new DummyRepositoryFileManager();
		builder = new Builder(fileman, null, null, null);
		builder.buildFile("file", false, false, new HashMap(), new HashMap());
		file = null;
		date11 = new Date(1100000000);
		date12 = new Date(1200000000);
		date13 = new Date(1300000000);
		date14 = new Date(1400000000);
		date15 = new Date(1500000000);
		rev0 = null;
		rev1 = null;
		rev2 = null;
		rev3 = null;
		rev4 = null;
	}

	/**
	 * Method testLinesOfCodeWithoutRepository1.
	 */
	public void testLinesOfCodeWithoutRepository1() throws Exception {
		buildRevision("2", date12, 5, 0);
		buildRevisionInitial("1", date11);
		finishBuilder();
		assertEquals(5, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 5, 5, 5);
		assertRevisionLines(rev1, 0, 0, 0);
	}

	/**
	 * Method testLinesOfCodeWithoutRepository2.
	 */
	public void testLinesOfCodeWithoutRepository2() throws Exception {
		buildRevision("2", date12, 0, 5);
		buildRevisionInitial("1", date11);
		finishBuilder();
		assertEquals(0, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 0, -5, 0);
		assertRevisionLines(rev1, 5, 5, 5);
	}

	/**
	 * Method testLinesOfCodeWithoutRepository3.
	 */
	public void testLinesOfCodeWithoutRepository3() throws Exception {
		buildRevision("5", date15, 10, 15);
		buildRevision("4", date14, 10, 0);
		buildRevision("3", date13, 25, 15);
		buildRevision("2", date12, 10, 0);
		buildRevisionInitial("1", date11);
		finishBuilder();
		assertEquals(30, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 30, -5, 10);
		assertRevisionLines(rev1, 35, 10, 10);
		assertRevisionLines(rev2, 25, 10, 25);
		assertRevisionLines(rev3, 15, 10, 10);
		assertRevisionLines(rev4, 5, 5, 5);
	}

	//  not relevant for SVN
	//	/**
	//	 * Test a file whose initial revision is dead (this means it was
	//	 * added on another branch). The builder should filter this file,
	//	 * so the Repository should be empty.
	//	 */
	//	public void testLinesOfCodeDeadInitial() throws Exception {
	// not relevant for SVN
	//		buildRevisionDead("1.1", date11);
	//		try {
	//			finishBuilder();
	//			fail("should have thrown EmptyRepositoryException");
	//		} catch (EmptyRepositoryException expected) {
	//			// is expected
	//		}
	//	}

	//  not relevant for SVN
	//	/**
	//	 * Test a file whose initial revision is dead (this means it was
	//	 * added on another branch), but that was later merged into the
	//	 * trunk.
	//	 */
	//	public void testLinesOfCodeDeadInitialMerged() throws Exception {
	//		buildRevision("1.2", date12, 10, 0);
	//		buildRevisionDead("1.1", date11);
	//		fileman.setLinesOfCode("file", 10);
	//		finishBuilder();
	//		assertTrue(rev0.isInitialRevision());
	//		assertRevisionLines(rev0, 10, 10, 10);
	//		assertEquals(1, file.getRevisions().size());
	//	}

	/**
	 * Simple test to make sure that the Builder pulls the LOC number
	 * from the RepositoryFileManager
	 */
	public void testLinesOfCodeInitial() throws Exception {
		fileman.setLinesOfCode("file", 100);
		buildRevisionInitial("1", date11);
		finishBuilder();
		assertEquals(100, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 100, 100, 100);
	}

	/**
	 * Test to make sure that LOC count for binary files is 0
	 */
	public void testLinesOfCodeBinary() throws Exception {
		fileman.setLinesOfCode("binaryfile", 100);
		builder.buildFile("binaryfile", true, false, new HashMap(), new HashMap());
		buildRevisionInitial("1", date11);
		finishBuilder();

		// get "binaryfile"
		file = (VersionedFile) builder.createRepository().getFiles().first();
		assertEquals(0, file.getCurrentLinesOfCode());
		rev0 = (Revision) file.getRevisions().first();
		assertRevisionLines(rev0, 0, 0, 0);
	}

	/**
	 * Test the behaviour of a deleted file, for which no HEAD LOC count
	 * is available.
	 */
	public void testLinesOfCodeWithDeletion() throws Exception {
		buildRevisionDead("3", date13);
		buildRevision("2", date12, 100, 0);
		buildRevisionInitial("1", date11);
		finishBuilder();
		assertTrue(file.isDead());
		assertEquals(0, file.getCurrentLinesOfCode());
		//TODO: WTF should LOC for a deleted file be 100? Counter-intuitive.
		assertRevisionLines(rev0, 0, -100, 0);
		assertRevisionLines(rev1, 100, 100, 100);
		assertRevisionLines(rev2, 0, 0, 0);
	}

	/**
	 * Tests the behaviour for deleted and re-added files.
	 */
	public void testLinesOfCodeWithRestore() throws Exception {
		fileman.setLinesOfCode("file", 100);
		buildRevision("3", date13, 0, 0);
		buildRevisionDead("2", date12);
		buildRevisionInitial("1", date11);
		finishBuilder();
		assertTrue(!file.isDead());
		assertEquals(100, file.getCurrentLinesOfCode());
		assertTrue(rev0.isInitialRevision());
		assertRevisionLines(rev0, 100, 100, 100);
		assertTrue(rev1.isDead());
		assertRevisionLines(rev1, 0, -100, 0);
		assertTrue(rev2.isInitialRevision());
		assertRevisionLines(rev2, 100, 100, 100);
	}

	/**
	 * Tests if "cvs log -d" logs work correctly when the file
	 * has no revisions in the -d timespan, but it did exist
	 */
	public void testPartialLogZeroRevisions() throws Exception {
		fileman.setLinesOfCode("file", 100);
		addAnotherFile();
		finishBuilder();
		assertNotNull(file);
		assertEquals(1, file.getRevisions().size());
		assertTrue(rev0.isBeginOfLog());
		assertNull(rev0.getAuthor());
		assertEquals(100, rev0.getLines());
		assertEquals(0, rev0.getReplacedLines());
		assertEquals(0, rev0.getLinesDelta());
	}

	/**
	 * Tests if "cvs log -d" logs work correctly when the file
	 * did not exist during the -d timespan (that is, it was
	 * added at a later date, or removed before the start date)
	 */
	public void testPartialLogZeroRevisionsNoFile() throws Exception {
		addAnotherFile();
		finishBuilder();
		assertNull(file);
	}

	public void testPartialLogOneRevision() throws Exception {
		fileman.setLinesOfCode("file", 100);
		buildRevision("5", date15, 80, 30);
		finishBuilder();
		assertEquals(3, file.getRevisions().size());
		assertEquals("5", rev0.getRevisionNumber());
		assertEquals("5", rev1.getRevisionNumber());
		assertEquals("0.0", rev2.getRevisionNumber());
		assertTrue(rev2.isBeginOfLog());
		assertRevisionLines(rev0, 100, 50, 80);
		assertRevisionLines(rev1, 0, 0, 0);
		assertRevisionLines(rev2, 50, 0, 0);
	}

	private void buildRevision(final String revision, final Date date, final int linesAdded, final int linesRemoved) {
		final RevisionData data = new RevisionData();
		data.setStateExp(true);
		data.setRevisionNumber(revision);
		data.setLoginName("author1");
		data.setDate(date);
		data.setLines(linesAdded, linesRemoved);
		data.setComment("comment");
		builder.buildRevision(data);
	}

	private void buildRevisionInitial(final String revision, final Date date) {
		final RevisionData data = new RevisionData();
		data.setStateExp(true);
		data.setStateAdded(true);
		data.setRevisionNumber(revision);
		data.setLoginName("author1");
		data.setDate(date);
		data.setComment("comment");
		builder.buildRevision(data);
	}

	private void buildRevisionDead(final String revision, final Date date) {
		final RevisionData data = new RevisionData();
		data.setStateDead(true);
		data.setRevisionNumber(revision);
		data.setLoginName("author1");
		data.setDate(date);
		data.setComment("comment");
		builder.buildRevision(data);
	}

	private void addAnotherFile() {
		builder.buildFile("file2", false, false, new HashMap(), new HashMap());
		buildRevisionInitial("1", date11);
	}

	private void finishBuilder() {
		final Iterator it = builder.createRepository().getFiles().iterator();
		while (it.hasNext()) {
			final VersionedFile f = (VersionedFile) it.next();
			if (f.getFilename().equals("file")) {
				file = f;
			}
		}
		if (file == null) {
			return;
		}
		final List revisions = new ArrayList(file.getRevisions());
		Collections.reverse(revisions);
		try {
			rev0 = (Revision) revisions.get(0);
			rev1 = (Revision) revisions.get(1);
			rev2 = (Revision) revisions.get(2);
			rev3 = (Revision) revisions.get(3);
			rev4 = (Revision) revisions.get(4);
		} catch (final IndexOutOfBoundsException mightHappen) {
			// do nothing
		}
	}

	private void assertRevisionLines(final Revision revision, final int effectiveLinesOfCode, final int locChange, final int lineValue) {
		assertEquals("effective lines of code", effectiveLinesOfCode, revision.getLines());
		assertEquals("lines of code change", locChange, revision.getLinesDelta());
		assertEquals("line value", lineValue, revision.getNewLines());
	}
}
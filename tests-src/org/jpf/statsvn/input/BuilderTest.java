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
 
 $RCSfile: BuilderTest.java,v $ 
 Created on $Date: 2004/12/14 13:38:13 $ 
 */

package net.sf.statsvn.input;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.util.FilePatternMatcher;

/**
 * Test cases for {@link Builder}.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @see LinesOfCodeTest
 * @version $Id: BuilderTest.java,v 1.22 2004/12/14 13:38:13 squig Exp $
 */
public class BuilderTest extends TestCase {
	private Builder builder;

	private RevisionData rev1;

	private RevisionData rev2;

	private RevisionData rev3;

	/**
	 * Constructor
	 * 
	 * @param arg0
	 *            input
	 */
	public BuilderTest(final String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		builder = new Builder(null, null, null, null);
		builder.buildModule("testmodule");
		rev1 = new RevisionData();
		rev1.setRevisionNumber("1");
		rev1.setLoginName("author1");
		rev1.setStateExp(true);
		rev1.setStateAdded(true);
		rev1.setDate(new Date(100000));
		rev1.setComment("comment1");
		rev2 = new RevisionData();
		rev2.setRevisionNumber("2");
		rev2.setLoginName("author2");
		rev2.setStateExp(true);
		rev2.setLines(10, 2);
		rev2.setDate(new Date(200000));
		rev2.setComment("comment2");
		rev3 = new RevisionData();
		rev3.setRevisionNumber("3");
		rev3.setLoginName("author1");
		rev3.setStateDead(true);
		rev3.setDate(new Date(300000));
		rev3.setComment("comment3");
	}

	/**
	 * Test if the module name is correctly passed on
	 */
	public void testBuildModule() {
		final Builder b = new Builder(null, null, null, null);
		b.buildModule("test");
		assertEquals("test", b.getProjectName());
		assertTrue(b.getAtticFileNames().isEmpty());
	}

	/**
	 * test {@link Builder.getAuthor(String)}
	 */
	public void testGetAuthor() {
		final Author author1 = builder.getAuthor("author1");
		final Author author2 = builder.getAuthor("author2");
		final Author author1b = builder.getAuthor("author1");
		assertEquals(author1, author1b);
		assertTrue(!author1.equals(author2));
		assertTrue(!author1b.equals(author2));
	}

	/**
	 * On some Windows systems, people can use both upper and lower case for the
	 * same login
	 */
	public void testGetAuthorCase() {
		final Author upper = this.builder.getAuthor("richard");
		final Author lower = this.builder.getAuthor("Richard");
		assertSame(upper, lower);
	}

	/**
	 * test {@link Builder.getDirectory(String)}
	 */
	public void testGetDirectoryRoot() {
		final Directory dir1 = builder.getDirectory("fileInRoot");
		final Directory dir2 = builder.getDirectory("anotherFileInRoot");
		assertEquals(dir1, dir2);
		assertTrue(dir1.isRoot());
		assertEquals("", dir1.getName());
		assertEquals("", dir1.getPath());
	}

	/**
	 * test {@link Builder.getDirectory(String)}
	 */
	public void testGetDirectoryDeepPath() {
		final Directory dir1 = builder.getDirectory("src/file");
		final Directory dir2 = builder.getDirectory("src/net/sf/statcvs/Main.java");
		assertEquals(dir1, dir2.getParent().getParent().getParent());
		assertTrue(dir1.getParent().isRoot());
	}

	/**
	 * test {@link Builder.getDirectory(String)}
	 */
	public void testGetDirectorySeveralPaths() {
		final Directory dir1 = builder.getDirectory("src/net/sf/statcvs/Main.java");
		final Directory dir2 = builder.getDirectory("src/com/microsoft/Windows95.java");
		final Directory dir3 = builder.getDirectory("src/com/microsoft/Windows98.java");
		assertEquals(dir2, dir3);
		assertEquals(dir1.getParent().getParent().getParent(), dir2.getParent().getParent());
	}

	/**
	 * test {@link Builder.addFile(VersionedFile)} and
	 * {@link Builder.getFiles()}
	 */
	public void testFilesEmpty() throws Exception {
		final Builder builder1 = new Builder(null, null, null, null);
		final Repository repository = builder1.createRepository();
		assertTrue(repository.isEmpty());
	}

	/**
	 * test {@link Builder.addFile(VersionedFile)} and
	 * {@link Builder.getFiles()}
	 */
	public void testFilesOneFile() throws Exception {
		builder.buildFile("file1", false, false, new HashMap(), new HashMap());
		builder.buildRevision(rev1);
		final Repository content = builder.createRepository();

		assertNotNull(content.getFiles());
		assertEquals(1, content.getFiles().size());
		final VersionedFile file1 = (VersionedFile) content.getFiles().first();
		assertEquals("file1", file1.getFilenameWithPath());
		assertEquals(builder.getDirectory(""), file1.getDirectory());
		assertEquals(1, file1.getRevisions().size());
	}

	/**
	 * test {@link Builder.addFile(VersionedFile)} and
	 * {@link Builder.getFiles()}
	 */
	public void testFileTwoFiles() throws Exception {
		builder.buildFile("file2", false, false, new HashMap(), new HashMap());
		builder.buildRevision(rev1);
		builder.buildFile("file3", false, false, new HashMap(), new HashMap());
		builder.buildRevision(rev2);
		final Repository content = builder.createRepository();

		assertNotNull(content.getFiles());
		assertEquals(2, content.getFiles().size());
		final VersionedFile file2 = (VersionedFile) content.getFiles().first();
		final VersionedFile file3 = (VersionedFile) content.getFiles().last();
		assertEquals("file2", file2.getFilenameWithPath());
		assertEquals("file3", file3.getFilenameWithPath());
	}

	/**
	 * Tests {@link Builder.buildRevisionBegin}
	 */
	public void testBuildRevision() throws Exception {
		builder.buildFile("file", false, false, new HashMap(), new HashMap());
		builder.buildRevision(rev3);
		builder.buildRevision(rev2);
		builder.buildRevision(rev1);
		final Repository content = builder.createRepository();

		final VersionedFile file = (VersionedFile) content.getFiles().first();
		final Iterator it = file.getRevisions().iterator();
		assertTrue(it.hasNext());
		final Revision r1 = (Revision) it.next();
		assertTrue(it.hasNext());
		final Revision r2 = (Revision) it.next();
		assertTrue(it.hasNext());
		final Revision r3 = (Revision) it.next();
		assertTrue(!it.hasNext());

		assertEquals("1", r1.getRevisionNumber());
		assertTrue(r1.isInitialRevision());
		assertEquals("author1", r1.getAuthor().getName());
		assertEquals("comment1", r1.getComment());

		assertEquals("2", r2.getRevisionNumber());
		assertTrue(!r2.isInitialRevision());
		assertEquals(2, r2.getReplacedLines());
		assertEquals(8, r2.getLinesDelta());
		assertEquals("author2", r2.getAuthor().getName());

		assertEquals("3", r3.getRevisionNumber());
		assertTrue(r3.isDead());
		assertEquals("author1", r3.getAuthor().getName());

		assertSame(r1.getAuthor(), r3.getAuthor());
	}

	public void testStartDateForPartialLog() throws Exception {
		final RevisionData rev4 = new RevisionData();
		rev4.setRevisionNumber("5");
		rev4.setLines(10, 5);
		rev4.setStateExp(true);
		rev4.setLoginName("somebody");
		rev4.setDate(new Date(100000000));
		builder.buildFile("dir/normal_file", true, false, new HashMap(), new HashMap());
		builder.buildRevision(rev3);
		builder.buildRevision(rev2);
		builder.buildRevision(rev1);
		builder.buildFile("partial_logged_file", true, false, new HashMap(), new HashMap());
		builder.buildRevision(rev4);
		final Repository content = builder.createRepository();

		final VersionedFile file = (VersionedFile) content.getRoot().getFiles().iterator().next();
		assertTrue(file.getInitialRevision().isBeginOfLog());
		final Date beforeRev1 = new Date(rev1.getDate().getTime() - 60000);
		assertEquals(beforeRev1, file.getInitialRevision().getDate());
	}

	public void testIncludePattern() throws Exception {
		final Builder b = new Builder(null, new FilePatternMatcher("a*"), new FilePatternMatcher("*z"), null);
		assertTrue(b.matchesPatterns("abc"));
		assertTrue(!b.matchesPatterns("xyz"));
		assertTrue(!b.matchesPatterns("az"));
	}

	public void testNoAtticFiles() throws Exception {
		builder.buildFile("file1", false, false, new HashMap(), new HashMap());
		builder.buildFile("file2", false, false, new HashMap(), new HashMap());
		builder.buildFile("file3", false, false, new HashMap(), new HashMap());
		assertTrue(builder.getAtticFileNames().isEmpty());
	}

	public void testAtticFiles() throws Exception {
		builder.buildFile("file1", false, false, new HashMap(), new HashMap());
		builder.buildFile("file2", false, true, new HashMap(), new HashMap());
		builder.buildFile("file3", false, true, new HashMap(), new HashMap());
		builder.buildFile("file4", false, false, new HashMap(), new HashMap());
		final Set attic = builder.getAtticFileNames();
		assertTrue(attic.contains("file2"));
		assertTrue(attic.contains("file3"));
		assertEquals(2, attic.size());
	}
}
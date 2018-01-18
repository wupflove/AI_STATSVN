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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import junit.framework.TestCase;
import net.sf.statcvs.util.LookaheadReader;

/**
 * Tests for {@link CvsLogfileParser} and {@link CvsFileBlockParser}. Most
 * tests run the parser class on a logfile loaded from the file system and
 * use a {@link MockBuilder} to verify the results.
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: ParserTest.java,v 1.8 2009/08/20 17:44:05 benoitx Exp $
 */
public class ParserTest extends TestCase {
    private MockLogBuilder mock;
    private RevisionData rev1;

    public ParserTest(final String arg0) {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        mock = new MockLogBuilder();
        rev1 = new RevisionData();
    }

    /**
     * Tests simple.log
     * @throws Exception on error
     */
    public void testSimpleLog() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        rev1.setRevisionNumber("1.1");
        rev1.setDate(createDate(2003, 06, 04, 19, 32, 58));
        rev1.setLoginName("cyganiak");
        rev1.setStateExp();
        rev1.setComment("renamed license.txt to LICENSE");
        mock.expectBuildRevision(rev1);
        parseLog("simple.log");
        mock.verify();
    }

    /**
     * Tests a logfile which was created when uncommited files were present in
     * the working copy ("? filename" lines at the beginning). They must be
     * ignored.
     * @throws Exception on error
     */
    public void testUncommittedFiles() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseLog("uncommitted-files.log");
        mock.verify();
    }

    /**
     * Tests two-files.log
     * @throws Exception
     */
    public void testTwoFiles() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.2");
        mock.expectNextRevision();
        mock.expectCurrentRevisionNumber("1.1");
        mock.expectBuildFile("README", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseLog("two-files.log");
        mock.verify();
    }

    /**
     * Tests parsing a log with a file that has no selected revisions.
     * Necessary when specifying ranges of dates or tags. The log still
     * contains all files but some may have no selected revisions.
     * @throws Exception
     */
    public void testNoRevisionsSelected() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectBuildFile("README", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseLog("no-revs-selected.log");
        mock.verify();
    }

    /**
     * Same as {@link #testNoRevisionsSelected}, but now the file has a description.
     * The description must be ignored by the parser.
     * @throws Exception
     */
    public void testNoRevisionsSelectedWithDescription() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectBuildFile("README", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseLog("no-revs-selected-w-description.log");
        mock.verify();
    }

    /**
     * Not sure why that was put in, but apparently we want the parser to
     * deal gracefully with newlines at the end of the file.
     * @throws Exception on error
     */
    public void testEmptyLinesAfterEnd() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectNextRevision();
        parseLog("newlines-after-end.log");
        mock.verify();
    }

    /**
     * Tests if the parser can handle a revision delimiter in the comment.
     * @throws Exception
     */
    public void testRevisionDelimiterInComment() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        mock.expectCurrentComment("comment\n----------------------------\ncomment");
        mock.expectBuildFile("README", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseLog("delimiter-in-comment.log");
        mock.verify();
    }

    /**
     * Tests for exception on empty logfile 
     * @throws Exception
     */
    public void testEmptyLog() throws Exception {
        final Reader reader = new StringReader("");
        final CvsLogfileParser parser = new CvsLogfileParser(reader, mock);
        parser.parse();
        mock.verify();
    }

    /**
     * Tests for exception on bogus logfile 
     * @throws Exception
     */
    public void testBogusLog() throws Exception {
        final Reader reader = new StringReader("foo\nbar");
        final CvsLogfileParser parser = new CvsLogfileParser(reader, mock);
        try {
            parser.parse();
            fail("should have thrown LogSyntaxException");
        } catch (final LogSyntaxException expected) {
            // expected
        }
    }

    /**
     * Tests the CvsFileBlockParser for a first file
     * @throws Exception
     */
    public void testFirstFile() throws Exception {
        mock.expectBuildModule("statcvs");
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        final Reader reader = new InputStreamReader(getClass().getResourceAsStream("simple.log2"));
        final LookaheadReader lookahead = new LookaheadReader(reader);
        lookahead.nextLine();
        new CvsFileBlockParser(lookahead, mock, true).parse();
        mock.verify();
    }

    /**
     * Tests the CvsFileBlockParser for a non-first file
     * @throws Exception
     */
    public void testNonFirstFile() throws Exception {
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("simple.log2");
        mock.verify();
    }

    /**
     * Tests the CvsFileBlockParser for a file with description
     * @throws Exception
     */
    public void testDescription() throws Exception {
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("description.log2");
        mock.verify();
    }

    /**
     * Tests parsing a log with a lock ("cyganiak: 1.1")
     * @throws Exception on error
     */
    public void testLocks() throws Exception {
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("locks.log2");
        mock.verify();
    }

    /**
     * Tests the CvsFileBlockParser for a file with access list
     * @throws Exception on error
     */
    public void testAccessList() throws Exception {
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("access-list.log2");
        mock.verify();
    }

    /**
     * Test log with missing "symbolic names:" section. Such
     * logs are created by the -N switch of the cvs log command. 
     * @throws Exception on error
     */
    public void testNoSymbolicNames() throws Exception {
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("no-symbolic-names.log2");
        mock.verify();
    }

    /**
     * Tests if attic files are correctly identified. 
     * @throws Exception on error
     * @see net.sf.statcvs.util.CvsLogUtilsTest.testIsInAttic
     */
    public void testIsInAttic() throws Exception {
        mock.expectBuildFile("LICENSE", false, true);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("in-attic.log2");
        mock.verify();
    }

    public void testTwoRevisions() throws Exception {
        mock.expectBuildFile("LICENSE", false, false);
        mock.expectCurrentRevisionNumber("1.2");
        mock.expectCurrentAuthor("jentzsch");
        mock.expectCurrentDate(createDate(2003, 6, 5, 19, 32, 58));
        mock.expectCurrentComment("comment2");
        mock.expectCurrentStateExp();
        mock.expectCurrentLines(10, 0);
        mock.expectNextRevision();
        mock.expectCurrentRevisionNumber("1.1");
        mock.expectCurrentAuthor("cyganiak");
        mock.expectCurrentDate(createDate(2003, 6, 4, 19, 32, 58));
        mock.expectCurrentComment("comment1");
        mock.expectCurrentNoLines();
        parseOneFile("two-revisions.log2");
        mock.verify();
    }

    /**
     * Tests the CvsFileBlockParser for a binary file
     * @throws Exception
     */
    public void testBinary() throws Exception {
        mock.expectBuildFile("LICENSE", true, false);
        mock.expectCurrentRevisionNumber("1.1");
        parseOneFile("binary.log2");
        mock.verify();
    }

    /**
     * Recent CVS versions have a different date format
     */
    public void testNewCVSDates() throws Exception {
        this.mock.expectBuildFile("LICENSE", false, false);
        this.mock.expectCurrentRevisionNumber("1.1");
        this.mock.expectCurrentDate(createDate(2004, 07, 18, 17, 42, 25));
        parseOneFile("newdate.log2");
        this.mock.verify();
    }

    public void testPrematurelyEndingLog() throws Exception {
        this.mock.expectBuildFile("LICENSE", false, false);
        try {
            parseOneFile("premature-end.log2");
            fail();
        } catch (final NoSuchElementException ex) {
            // is expected because log ends right within a revision
        }
    }

    private void parseLog(final String name) throws Exception {
        final Reader reader = new InputStreamReader(getClass().getResourceAsStream(name));
        new CvsLogfileParser(reader, mock).parse();
    }

    private void parseOneFile(final String name) throws Exception {
        final Reader reader = new InputStreamReader(getClass().getResourceAsStream(name));
        final LookaheadReader lookahead = new LookaheadReader(reader);
        lookahead.nextLine();
        new CvsFileBlockParser(lookahead, mock, false).parse();
    }

    private Date createDate(final int year, final int month, final int day, final int hour, final int minute, final int second) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }
}
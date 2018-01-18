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
    
	$RCSfile: FileBlockParserTest.java,v $ 
	Created on $Date: 2002/08/26 20:54:12 $ 
*/

package net.sf.statcvs.input;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.util.LookaheadReader;

/**
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: FileBlockParserTest.java,v 1.21 2002/08/26 20:54:12 cyganiak Exp $
 */
public class FileBlockParserTest extends TestCase {

    private static final String REVISION_DELIMITER = "----------------------------\n";
    private static final String FILE_DELIMITER = "=============================================================================\n";

    /**
     * Constructor for FileBlockParserTest.
     * @param arg0 input
     */
    public FileBlockParserTest(final String arg0) {
        super(arg0);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * TODO: move to ParserTest
     * Method testIsCheckIn.
     * @throws Exception on error
     */
    public void testIsCheckIn() throws Exception {
        String log = "";
        log += "RCS file: /home/CVSROOT/TEST/testfile,v\n";
        log += "Working file: testfile\n";
        log += "head: 1.3\n";
        log += "branch:\n";
        log += "locks: strict\n";
        log += "access list:\n";
        log += "symbolic names:\n";
        log += "keyword substitution: kv\n";
        log += "total revisions: 1;     selected revisions: 1\n";
        log += "description:\n";
        log += REVISION_DELIMITER;
        log += "revision 1.2\n";
        log += "date: 2002/05/26 15:22:52;  author: autor2;  state: Exp;  lines: +3 -2\n";
        log += "abc\n";
        log += REVISION_DELIMITER;
        log += "revision 1.1\n";
        log += "date: 2002/05/25 09:52:07;  author: ewender;  state: Exp;\n";
        log += "comment text\n";
        log += FILE_DELIMITER;
        final VersionedFile file = parseString(log);
        assertNotNull("file was null", file);
        assertTrue("rev 1.2 is no checkin", !file.getLatestRevision().isInitialRevision());
        assertTrue("rev 1.1 is a checkin", file.getInitialRevision().isInitialRevision());
        assertEquals(2, file.getRevisions().size());
    }

    /**
     * TODO: move to FileBuilderTest (=LinesOfCodeTest)
     * Tests a file that was added on another branch. It should be removed.
     * @throws Exception on error
     */
    public void testFileOnOtherBranch() throws Exception {
        String log = "";
        log += "RCS file: /home/bude/cyganiak/cvstest/test/Attic/testfile2,v\n";
        log += "Working file: testfile2\n";
        log += "head: 1.1\n";
        log += "branch:\n";
        log += "locks: strict\n";
        log += "access list:\n";
        log += "symbolic names:\n";
        log += "		branch: 1.1.0.2\n";
        log += "keyword substitution: kv\n";
        log += "total revisions: 2;     selected revisions: 2\n";
        log += "description:\n";
        log += REVISION_DELIMITER;
        log += "revision 1.1\n";
        log += "date: 2003/05/02 21:33:31;  author: cyganiak;  state: dead;\n";
        log += "branches:  1.1.2;\n";
        log += "file testfile2 was initially added on branch branch.\n";
        log += REVISION_DELIMITER;
        log += "revision 1.1.2.1\n";
        log += "date: 2003/05/02 21:33:31;  author: cyganiak;  state: Exp;  lines: +1 -0\n";
        log += "asdf\n";
        log += FILE_DELIMITER;
        final Builder builder = new Builder(null, null, null, null);
        final LookaheadReader lookahead = new LookaheadReader(new StringReader(log));
        lookahead.nextLine();
        new CvsFileBlockParser(lookahead, builder, false).parse();
        final Repository repo = builder.createCvsContent();
        assertTrue(repo.isEmpty());
    }

    /**
     * TODO: move to ParserTest
     * CVSNT has a slightly different logfile format: After the "lines: +x -y"
     * part of each revision, there will be a ";" and maybe more fields.
     * @throws Exception on error
     */
    public void testCVSNTLog() throws Exception {
        String log = "";
        log += "RCS file: k:/cvsroot/Ellison/index.html,v\n";
        log += "Working file: index.html\n";
        log += "head: 1.1\n";
        log += "branch: 1.1.1\n";
        log += "locks: strict\n";
        log += "access list:\n";
        log += "symbolic names:\n";
        log += "release_2003_03_31: 1.1.1.1.0.2\n";
        log += "start: 1.1.1.1\n";
        log += "Ellison: 1.1.1\n";
        log += "keyword substitution: kv\n";
        log += "total revisions: 2;selected revisions: 2\n";
        log += "description:\n";
        log += "----------------------------\n";
        log += "revision 1.2\n";
        log += "date: 2002/06/04 13:49:00;  author: kdavis;  state: Exp;  lines: +3 -2;\n";
        log += "branches:  1.1.1;\n";
        log += "Initial revision\n";
        log += "----------------------------\n";
        log += "revision 1.1\n";
        log += "date: 2002/06/04 13:48:00;  author: kdavis;  state: Exp;\n";
        log += "Initial import.\n";
        log += FILE_DELIMITER;
        final VersionedFile file = parseString(log);
        final Revision rev = file.getLatestRevision();
        assertEquals(2, rev.getReplacedLines());
        assertEquals(1, rev.getLinesDelta());
        assertEquals(2, file.getRevisions().size());
    }

    private VersionedFile parseString(final String log) throws LogSyntaxException, IOException {
        final Builder builder = new Builder(null, null, null, null);
        final LookaheadReader lookahead = new LookaheadReader(new StringReader(log));
        lookahead.nextLine();
        new CvsFileBlockParser(lookahead, builder, false).parse();
        final Repository content = builder.createCvsContent();
        return (VersionedFile) content.getFiles().first();
    }
}
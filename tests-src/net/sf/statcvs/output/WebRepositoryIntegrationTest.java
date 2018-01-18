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
    
	$RCSfile: WebRepositoryIntegrationTest.java,v $
	$Date: 2008/04/02 11:22:16 $
*/
package net.sf.statcvs.output;

import java.util.Date;
import java.util.HashSet;

import junit.framework.TestCase;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;

/**
 * Test cases for {ViewCvsIntegration}
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: WebRepositoryIntegrationTest.java,v 1.21 2008/04/02 11:22:16 benoitx Exp $
 */
public class WebRepositoryIntegrationTest extends TestCase {

    private static final String BASE = "http://example.com/";

    private WebRepositoryIntegration viewcvs;
    private WebRepositoryIntegration cvsweb;
    private WebRepositoryIntegration chora;
    private final Date date = new Date(100000000);
    private Directory root;
    private Directory path;

    /**
     * Checkstyle drives me nuts
     * @param arg0 stuff
     */
    public WebRepositoryIntegrationTest(final String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        viewcvs = new ViewCvsIntegration(BASE);
        cvsweb = new CvswebIntegration(BASE);
        chora = new ChoraIntegration(BASE);
        root = Directory.createRoot();
        path = root.createSubdirectory("path");
    }

    /**
     * test
     */
    public void testViewcvsCreation() {
        assertEquals("ViewCVS", viewcvs.getName());
    }

    /**
     * Tests if stuff still works when the trailing slash is omitted from
     * the base URL
     */
    public void testViewcvsForgivingBaseURL() {
        final ViewCvsIntegration viewcvs2 = new ViewCvsIntegration("http://example.com");
        final VersionedFile file = new VersionedFile("file", root);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        assertEquals("http://example.com/file", viewcvs2.getFileHistoryUrl(file));
    }

    /**
     * test URLs for a normal file
     */
    public void testViewcvsNormalFile() {
        final VersionedFile file = new VersionedFile("path/file", path);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        assertEquals(BASE + "path/file", viewcvs.getFileHistoryUrl(file));
        assertEquals(BASE + "path/file?rev=HEAD&content-type=text/vnd.viewcvs-markup", viewcvs.getFileViewUrl(file));
    }

    /**
     * Test for a ViewCVS URL that includes a cvsroot parameter
     */
    public void testViewcvsWithCvsroot() {
        this.viewcvs = new ViewCvsIntegration("http://example.com/cgi-bin/viewcvs.cgi/module?cvsroot=CvsRoot");
        final VersionedFile file = new VersionedFile("path/file", this.path);
        file.addChangeRevision("1.1", null, this.date, null, 0, 0, 0, null);
        assertEquals("http://example.com/cgi-bin/viewcvs.cgi/module/path/file?cvsroot=CvsRoot", this.viewcvs.getFileHistoryUrl(file));
        assertEquals("http://example.com/cgi-bin/viewcvs.cgi/module/path/file?rev=HEAD&content-type=text/vnd.viewcvs-markup&cvsroot=CvsRoot", this.viewcvs
                .getFileViewUrl(file));
    }

    /**
     * test URLs for an attic file
     */
    public void testViewcvsAtticFile() {
        final VersionedFile file = new VersionedFile("path/file", path);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        final HashSet atticFileNames = new HashSet();
        atticFileNames.add("path/file");
        viewcvs.setAtticFileNames(atticFileNames);
        assertEquals(BASE + "path/Attic/file", viewcvs.getFileHistoryUrl(file));
        assertEquals(BASE + "path/Attic/file?rev=HEAD&content-type=text/vnd.viewcvs-markup", viewcvs.getFileViewUrl(file));
    }

    /**
     * Test URLs for directories
     */
    public void testViewcvsDirectory() {
        assertEquals("http://example.com/", viewcvs.getDirectoryUrl(root));
        assertEquals("http://example.com/path/", viewcvs.getDirectoryUrl(path));
    }

    /**
     * Test URLs for diff
     */
    public void testViewcvsDiff() {
        final VersionedFile file = new VersionedFile("file", root);
        final Revision rev1 = file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        final Revision rev2 = file.addChangeRevision("1.2", null, date, null, 0, 0, 0, null);
        assertEquals("http://example.com/file.diff?r1=1.1&r2=1.2", viewcvs.getDiffUrl(rev1, rev2));
    }

    /**
     * Test URLs for diff with cvsroot parameter
     */
    public void testViewcvsDiffWithCvsroot() {
        this.viewcvs = new ViewCvsIntegration("http://example.com/cgi-bin/viewcvs.cgi/module?cvsroot=CvsRoot");
        final VersionedFile file = new VersionedFile("file", this.root);
        final Revision rev1 = file.addChangeRevision("1.1", null, this.date, null, 0, 0, 0, null);
        final Revision rev2 = file.addChangeRevision("1.2", null, this.date, null, 0, 0, 0, null);
        assertEquals("http://example.com/cgi-bin/viewcvs.cgi/module/file.diff?r1=1.1&r2=1.2&cvsroot=CvsRoot", this.viewcvs.getDiffUrl(rev1, rev2));
    }

    /**
     * test
     */
    public void testCvswebCreation() {
        assertEquals("cvsweb", cvsweb.getName());
    }

    /**
     * Tests if stuff still works when the trailing slash is omitted from
     * the base URL
     */
    public void testCvswebForgivingBaseURL() {
        final CvswebIntegration cvsweb2 = new CvswebIntegration("http://example.com");
        final VersionedFile file = new VersionedFile("file", root);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        assertEquals("http://example.com/file", cvsweb2.getFileHistoryUrl(file));
    }

    /**
     * test URLs for a normal file
     */
    public void testCvswebNormalFile() {
        final VersionedFile file = new VersionedFile("path/file", path);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        assertEquals(BASE + "path/file", cvsweb.getFileHistoryUrl(file));
        assertEquals(BASE + "path/file?rev=HEAD&content-type=text/vnd.viewcvs-markup", cvsweb.getFileViewUrl(file));
    }

    /**
     * Test for a URL that includes a cvsroot parameter
     */
    public void testCvswebWithCvsroot() {
        this.cvsweb = new CvswebIntegration("http://example.com/cgi-bin/cvsweb.cgi/module?cvsroot=CvsRoot");
        final VersionedFile file = new VersionedFile("path/file", this.path);
        file.addChangeRevision("1.1", null, this.date, null, 0, 0, 0, null);
        assertEquals("http://example.com/cgi-bin/cvsweb.cgi/module/path/file?cvsroot=CvsRoot", this.cvsweb.getFileHistoryUrl(file));
        assertEquals("http://example.com/cgi-bin/cvsweb.cgi/module/path/file?rev=HEAD&content-type=text/vnd.viewcvs-markup&cvsroot=CvsRoot", this.cvsweb
                .getFileViewUrl(file));
    }

    /**
     * test URLs for an attic file
     */
    public void testCvswebAtticFile() {
        final VersionedFile file = new VersionedFile("path/file", path);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        final HashSet atticFileNames = new HashSet();
        atticFileNames.add("path/file");
        cvsweb.setAtticFileNames(atticFileNames);
        assertEquals(BASE + "path/Attic/file", cvsweb.getFileHistoryUrl(file));
        assertEquals(BASE + "path/Attic/file?rev=HEAD&content-type=text/vnd.viewcvs-markup", cvsweb.getFileViewUrl(file));
    }

    /**
     * Test URLs for directories
     */
    public void testCvswebDirectory() {
        assertEquals("http://example.com/", cvsweb.getDirectoryUrl(root));
        assertEquals("http://example.com/path/", cvsweb.getDirectoryUrl(path));
    }

    /**
     * Test URLs for diff
     */
    public void testCvswebDiff() {
        final VersionedFile file = new VersionedFile("file", root);
        final Revision rev1 = file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        final Revision rev2 = file.addChangeRevision("1.2", null, date, null, 0, 0, 0, null);
        assertEquals("http://example.com/file.diff?r1=1.1&r2=1.2&f=h", cvsweb.getDiffUrl(rev1, rev2));
    }

    /**
     * Test URLs for diff with cvsroot parameter
     */
    public void testCvswebDiffWithCvsroot() {
        this.cvsweb = new CvswebIntegration("http://example.com/cgi-bin/cvsweb.cgi/module?cvsroot=CvsRoot");
        final VersionedFile file = new VersionedFile("file", this.root);
        final Revision rev1 = file.addChangeRevision("1.1", null, this.date, null, 0, 0, 0, null);
        final Revision rev2 = file.addChangeRevision("1.2", null, this.date, null, 0, 0, 0, null);
        assertEquals("http://example.com/cgi-bin/cvsweb.cgi/module/file.diff?r1=1.1&r2=1.2&f=h&cvsroot=CvsRoot", this.cvsweb.getDiffUrl(rev1, rev2));
    }

    /**
     * test
     */
    public void testChoraCreation() {
        assertEquals("Chora", chora.getName());
    }

    /**
     * Tests if stuff still works when the trailing slash is omitted from
     * the base URL
     */
    public void testChoraForgivingBaseURL() {
        final ChoraIntegration chora2 = new ChoraIntegration("http://example.com");
        final VersionedFile file = new VersionedFile("file", root);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        assertEquals("http://example.com/file", chora2.getFileHistoryUrl(file));
    }

    /**
     * test URLs for a normal file
     */
    public void testChoraNormalFile() {
        final VersionedFile file = new VersionedFile("path/file", path);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        assertEquals(BASE + "path/file", chora.getFileHistoryUrl(file));
        assertEquals(BASE + "path/file?r=HEAD", chora.getFileViewUrl(file));
    }

    /**
     * test URLs for an attic file
     */
    public void testChoraAtticFile() {
        final VersionedFile file = new VersionedFile("path/file", path);
        file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        final HashSet atticFileNames = new HashSet();
        atticFileNames.add("path/file");
        chora.setAtticFileNames(atticFileNames);
        assertEquals(BASE + "path/Attic/file", chora.getFileHistoryUrl(file));
        assertEquals(BASE + "path/Attic/file?r=HEAD", chora.getFileViewUrl(file));
    }

    /**
     * Test URLs for directories
     */
    public void testChoraDirectory() {
        assertEquals("http://example.com/", viewcvs.getDirectoryUrl(root));
        assertEquals("http://example.com/path/", viewcvs.getDirectoryUrl(path));
    }

    /**
     * Test URLs for diff
     */
    public void testChoraDiff() {
        final VersionedFile file = new VersionedFile("file", root);
        final Revision rev1 = file.addChangeRevision("1.1", null, date, null, 0, 0, 0, null);
        final Revision rev2 = file.addChangeRevision("1.2", null, date, null, 0, 0, 0, null);
        assertEquals("http://example.com/file?r1=1.1&r2=1.2", chora.getDiffUrl(rev1, rev2));
    }
}
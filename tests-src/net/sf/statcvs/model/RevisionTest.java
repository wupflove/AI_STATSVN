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
package net.sf.statcvs.model;

import java.util.Date;

import junit.framework.TestCase;

/**
 * Tests for {@link net.sf.statcvs.model.Revision}
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: RevisionTest.java,v 1.6 2008/04/02 11:22:15 benoitx Exp $
 */
public class RevisionTest extends TestCase {
    private Author author;
    private Date date1;
    private Date date2;
    private Date date3;
    private Date date4;

    public RevisionTest(final String arg) {
        super(arg);
    }

    public void setUp() {
        author = new Author("author");
        date1 = new Date(110000000);
        date2 = new Date(120000000);
        date3 = new Date(130000000);
        date4 = new Date(140000000);
    }

    public void testGetFileCountChange1() {
        final VersionedFile file = new VersionedFile("file", Directory.createRoot());
        final Revision rev4 = new Revision(file, "1.4", Revision.TYPE_CREATION, author, date4, null, 0, 0, 0, null);
        final Revision rev3 = new Revision(file, "1.3", Revision.TYPE_CHANGE, author, date3, null, 0, 0, 0, null);
        final Revision rev2 = new Revision(file, "1.2", Revision.TYPE_DELETION, author, date2, null, 0, 0, 0, null);
        final Revision rev1 = new Revision(file, "1.1", Revision.TYPE_CREATION, author, date1, null, 0, 0, 0, null);
        assertEquals(1, rev4.getFileCountDelta());
        assertEquals(0, rev3.getFileCountDelta());
        assertEquals(-1, rev2.getFileCountDelta());
        assertEquals(1, rev1.getFileCountDelta());
    }

    public void testGetFileCountChange2() {
        final VersionedFile file = new VersionedFile("file", Directory.createRoot());
        final Revision rev = new Revision(file, null, Revision.TYPE_BEGIN_OF_LOG, author, date1, null, 0, 0, 0, null);
        assertEquals(0, rev.getFileCountDelta());
    }
}

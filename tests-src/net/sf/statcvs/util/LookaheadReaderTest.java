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
    
	$RCSfile: LookaheadReaderTest.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.util;

import java.io.StringReader;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * Tests for {@link LookaheadReader}
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: LookaheadReaderTest.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class LookaheadReaderTest extends TestCase {

    private LookaheadReader l;

    /**
     * Constructor
     * @param arg arg
     */
    public LookaheadReaderTest(final String arg) {
        super(arg);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
        this.l = new LookaheadReader(new StringReader("1\n2\n3"));
    }

    /**
     * Tests creation of a new LookaheadReader and reading of the first line
     * @throws Exception on error
     */
    public void testCreation() throws Exception {
        assertNotNull(this.l);
        assertEquals(0, this.l.getLineNumber());
        assertTrue(this.l.hasNextLine());
        try {
            this.l.getCurrentLine();
            fail();
        } catch (final NoSuchElementException ex) {
            // is expected
        }
    }

    /**
     * Tests {@link LookaheadReader#getCurrentLine} and
     * {@link LookaheadReader#nextLine} and
     * {@link LookaheadReader#hasNextLine}.
     * @throws Exception on error
     */
    public void testCurrentLine() throws Exception {
        assertTrue(this.l.hasNextLine());
        assertEquals("1", this.l.nextLine());
        assertTrue(this.l.hasNextLine());
        assertEquals("1", this.l.getCurrentLine());
        assertTrue(this.l.hasNextLine());
        assertEquals("1", this.l.getCurrentLine());
        assertEquals("2", this.l.nextLine());
        assertEquals("2", this.l.getCurrentLine());
        assertEquals("2", this.l.getCurrentLine());
        assertEquals("3", this.l.nextLine());
        assertEquals("3", this.l.getCurrentLine());
        assertEquals("3", this.l.getCurrentLine());
        assertFalse(this.l.hasNextLine());
        try {
            this.l.nextLine();
            fail();
        } catch (final NoSuchElementException ex) {
            // is expected
        }
    }

    /**
     * Tests {@link LookaheadReader#getLineNumber}
     * @throws Exception on error
     */
    public void testLineNumbers() throws Exception {
        assertEquals(0, this.l.getLineNumber());
        this.l.nextLine();
        assertEquals(1, this.l.getLineNumber());
        this.l.nextLine();
        assertEquals(2, this.l.getLineNumber());
        this.l.getCurrentLine();
        assertEquals(2, this.l.getLineNumber());
        this.l.hasNextLine();
        assertEquals(2, this.l.getLineNumber());
        this.l.nextLine();
        assertEquals(3, this.l.getLineNumber());
        this.l.getCurrentLine();
        assertEquals(3, this.l.getLineNumber());
    }
}
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
    
	$RCSfile: LookaheadReader.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * <p>Wraps a {@link java.io.Reader} for line-by-line access.
 * This works like {@link java.util.Iterator}: {@link #hasNextLine}
 * returns true if another line can be read; {@link #nextLine} reads
 * the next line and returns it. Additionally, {@link #getCurrentLine}
 * can be used to access multiple times the line returned by
 * <tt>nextLine()</tt>.</p>
 * 
 * <p>At construction time, <tt>getCurrentLine()</tt> is undefined.
 * <tt>nextLine()</tt> must be called once to read the first line.</p>
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: LookaheadReader.java,v 1.4 2008/04/02 11:22:15 benoitx Exp $
 */
public class LookaheadReader {
    private final BufferedReader reader;
    private String currentLine = null;
    private String nextLine = null;
    private boolean afterEnd = false;
    private int lineNumber = 0;

    /**
     * Creates a LookaheadReader from a source reader.
     * @param reader a reader whose contents will be returned by the
     * 			LookaheadReader
     */
    public LookaheadReader(final Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /**
     * Returns the current line without reading a line from the source
     * reader. Will throw an exception if {@link #nextLine} was not
     * called before.
     * @return The line returned by the previous call to {@link #nextLine()}
     * @throws NoSuchElementException if {@link #nextLine} was not yet called
     */
    public String getCurrentLine() {
        if (this.currentLine == null) {
            throw new NoSuchElementException("Call to getCurrentLine() before nextLine() was called");
        }
        return this.currentLine;
    }

    /**
     * Reads and returns a line from the source reader. The result of
     * this call will be the new current line. Will throw an exception
     * if trying to read from after the end of the source reader.
     * @return The next line of the source reader
     * @throws IOException on error while reading the source reader
     * @throws NoSuchElementException if {@link #hasNextLine} is false 
     */
    public String nextLine() throws IOException {
        if (!hasNextLine()) {
            throw new NoSuchElementException("Call to nextLine() when hasNextLine() is false");
        }
        this.currentLine = this.nextLine;
        this.nextLine = null;
        this.lineNumber++;
        return this.currentLine;
    }

    /**
     * Checks if more lines are available for reading.
     * @return <tt>true</tt> if at least one more line can be read
     * @throws IOException on error while reading the source reader
     */
    public boolean hasNextLine() throws IOException {
        if (this.afterEnd) {
            return false;
        }
        if (this.nextLine != null) {
            return true;
        }
        this.nextLine = this.reader.readLine();
        if (this.nextLine != null) {
            return true;
        }
        this.afterEnd = true;
        return false;
    }

    /**
     * Returns the number of the line that would be returned by
     * {@link #getCurrentLine}, or 0 before the first
     * call to {@link #nextLine}. The first line has line number 1.
     * @return the current line number
     */
    public int getLineNumber() {
        return this.lineNumber;
    }
}
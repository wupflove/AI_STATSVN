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
    
	$RCSfile: ConsoleOutHandler.java,v $
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A simplified copy of <code>java.util.logging.ConsoleHandler</code>.
 * It writes to <code>System.out</code> instead of
 * <code>System.err</code> and uses the {@link LogFormatter}
 * to format 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: ConsoleOutHandler.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class ConsoleOutHandler extends StreamHandler {

    /**
     * Create a <tt>ConsoleOutHandler</tt> for <tt>System.out</tt>.
     */
    public ConsoleOutHandler() {
        setLevel(Level.FINEST);
        setFormatter(new LogFormatter());
        setOutputStream(System.out);
    }

    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * @param  record  description of the log event
     */
    public void publish(final LogRecord record) {
        super.publish(record);
        flush();
    }

    /**
     * Override <tt>StreamHandler.close</tt> to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close <tt>System.err</tt>.
     */
    public void close() {
        flush();
    }
}

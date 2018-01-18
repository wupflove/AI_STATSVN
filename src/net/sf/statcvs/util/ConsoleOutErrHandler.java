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
    
	$RCSfile: ConsoleOutErrHandler.java,v $
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Customized console logging handler.
 * <p>
 * The <tt>ConsoleOutErrHandler</tt> writes log messages
 * of severity <tt>WARNING</tt> and higher to the <tt>System.err</tt>
 * stream, and all other log messages to <tt>System.out</tt>.
 * It uses a {@link LogFormatter} to format the records.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: ConsoleOutErrHandler.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class ConsoleOutErrHandler extends Handler {

    private final Handler errHandler = new ConsoleHandler();
    private final Handler outHandler = new ConsoleOutHandler();

    /**
     * Constructor for ConsoleOutErrHandler.
     */
    public ConsoleOutErrHandler() {
        super();
    }

    /**
     * @see java.util.logging.Handler#publish(LogRecord)
     * @param record a log record
     */
    public void publish(final LogRecord record) {
        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            errHandler.publish(record);
        } else {
            outHandler.publish(record);
        }
    }

    /**
     * @see java.util.logging.Handler#flush()
     */
    public void flush() {
        errHandler.flush();
        outHandler.flush();
    }

    /**
     * @see java.util.logging.Handler#close()
     */
    public void close() throws SecurityException {
        errHandler.close();
        outHandler.close();
    }
}

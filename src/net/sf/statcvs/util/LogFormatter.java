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
    
	$RCSfile: LogFormatter.java,v $ 
	Created on $Date: 2008/04/02 11:22:15 $ 
*/

package net.sf.statcvs.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logging formatter for StatCvs
 * @author Lukasz Pekacki <lukasz@pekacki.de>
 * @version $Id: LogFormatter.java,v 1.9 2008/04/02 11:22:15 benoitx Exp $
 */
public class LogFormatter extends Formatter {

    private final String lineSeparator = System.getProperty("line.separator");

    /**
     * @see java.util.logging.Formatter#format(LogRecord)
     */
    public String format(final LogRecord record) {
        final StringBuffer sb = new StringBuffer();
        if (record.getLevel().intValue() < Level.INFO.intValue()) {
            sb.append(record.getLevel().getLocalizedName());
            sb.append(" ");
            if (record.getSourceClassName() != null) {
                String className = record.getSourceClassName();
                className = className.substring(7);
                sb.append(className);
            } else {
                sb.append(record.getLoggerName());
            }
            if (record.getSourceMethodName() != null) {
                sb.append(" ");
                sb.append(record.getSourceMethodName());
            }
            sb.append("(): ");
        }
        final String message = formatMessage(record);
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            try {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (final Exception ex) {
                System.err.println("Error formatting logmessage! " + ex.toString());
            }
        }
        return sb.toString();
    }

}

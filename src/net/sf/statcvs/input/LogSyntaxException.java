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
    
	$RCSfile: LogSyntaxException.java,v $ 
	Created on $Date: 2008/04/02 11:22:15 $ 
*/

package net.sf.statcvs.input;

/**
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: LogSyntaxException.java,v 1.12 2008/04/02 11:22:15 benoitx Exp $
 */
public class LogSyntaxException extends Exception {

    /**
     * @see java.lang.Object#Object()
     */
    public LogSyntaxException() {
        super();
    }

    /**
     * @see java.lang.Throwable#Throwable(String)
     */
    public LogSyntaxException(final String message) {
        super(message);
    }
}

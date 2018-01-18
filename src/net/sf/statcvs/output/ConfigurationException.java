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
    
	$RCSfile: ConfigurationException.java,v $
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.output;

/**
 * Thrown on missing/illegal configuration arguments.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ConfigurationException.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class ConfigurationException extends Exception {

    /**
     * Constructor
     * @param message error message
     */
    public ConfigurationException(final String message) {
        super(message);
    }
}
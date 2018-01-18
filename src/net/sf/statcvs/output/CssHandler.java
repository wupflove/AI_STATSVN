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
    
	$RCSfile: CssHandler.java,v $
	$Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.output;

import java.io.IOException;

/**
 * Manager for the handling of CSS files. There are different ways for speciying
 * CSS files (local file, default file from the distribution JAR, HTTP URL).
 * A CssHandler must be implemented for each of these ways.
 * 
 * TODO: Should be refactored into something that produces a ReportFile, which
 * 		has methods getURL() and write() and can be added to report pages.
 * 
 * @author Richard Cyganiak
 */
public interface CssHandler {

    /**
     * returns a link to the CSS file, which can be used as the HREF in HTML's
     * &lt;LINK REL="stylesheet"&gt HREF="filename.css";.
     * 
     * @return a link to the CSS file
     */
    String getLink();

    /**
     * Checks if all necessary resources are available. This can be
     * used, for example, to check if a local CSS file really exists
     * or if a HTTP URL is valid.   
     *
     * @throws ConfigurationException if some resource is missing. 
     */
    void checkForMissingResources() throws ConfigurationException;

    /**
     * Creates any necessary output files.
     * 
     * @throws IOException if an output file can't be created
     */
    void createOutputFiles() throws IOException;
}
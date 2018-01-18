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
    
	$RCSfile: LocalFileCssHandler.java,v $
	$Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.output;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.statcvs.util.FileUtils;

/**
 * CSS handler for a local CSS file which will be copied to the output dir.
 * 
 * @author Richard Cyganiak
 */
public class LocalFileCssHandler implements CssHandler {

    private static Logger logger = Logger.getLogger("net.sf.statcvs.output.CssHandler");

    private final String filename;

    /**
     * Creates a new LocalFileCssHandler for a given CSS file.
     * The filename can be absoulte or relative.
     * @param filename Name of the CSS file
     */
    public LocalFileCssHandler(final String filename) {
        this.filename = filename;
    }

    /**
     * @see net.sf.statcvs.output.CssHandler#getLink()
     */
    public String getLink() {
        return FileUtils.getFilenameWithoutPath(filename);
    }

    /**
     * Checks if the local CSS file exists
     * @see net.sf.statcvs.output.CssHandler#checkForMissingResources()
     * @throws ConfigurationException if the file is not found
     */
    public void checkForMissingResources() throws ConfigurationException {
        logger.finer("Checking if CSS file exists: '" + filename + "'");
        final File f = new File(filename);
        if (!f.exists()) {
            throw new ConfigurationException("CSS file not found: " + filename);
        }
    }

    /**
     * Copies the local CSS file to the output directory
     * @see net.sf.statcvs.output.CssHandler#createOutputFiles()
     */
    public void createOutputFiles() throws IOException {
        final String destination = ConfigurationOptions.getOutputDir() + getLink();
        logger.info("Copying CSS file to '" + destination + "'");
        FileUtils.copyFile(filename, destination);
    }

    /**
     * toString
     * @return string
     */
    public String toString() {
        return "local CSS file (" + filename + ")";
    }
}

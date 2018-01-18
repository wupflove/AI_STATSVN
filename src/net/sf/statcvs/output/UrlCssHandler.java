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
    
	$RCSfile: UrlCssHandler.java,v $
	$Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.output;

import java.io.IOException;
import java.net.URL;

/**
 * CSS handler for a CSS file specified by a HTTP URL.
 * 
 * @author Richard Cyganiak
 */
public class UrlCssHandler implements CssHandler {

    private final URL url;

    /**
     * Creates a new UrlCssHandler for a CSS file located at a given URL.
     * @param url the url to the CSS file
     */
    public UrlCssHandler(final URL url) {
        this.url = url;
    }

    /**
     * Simply return the URL
     * @see net.sf.statcvs.output.CssHandler#getLink()
     */
    public String getLink() {
        return url.toString();
    }

    /**
     * We could check here if there is a real CSS file at the URL, but
     * this would require net access, so we just do nothing.
     * @see net.sf.statcvs.output.CssHandler#checkForMissingResources()
     */
    public void checkForMissingResources() throws ConfigurationException {
        // do nothing
    }

    /**
     * We don't create any output files. We could copy the CSS file from
     * the URL to the output dir, but this would require net access, so
     * we just do nothing.
     * @see net.sf.statcvs.output.CssHandler#createOutputFiles()
     */
    public void createOutputFiles() throws IOException {
        // do nothing
    }

    /**
     * toString
     * @return string
     */
    public String toString() {
        return "remote CSS file (" + url + ")";
    }
}

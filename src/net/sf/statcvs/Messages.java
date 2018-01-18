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
    
	$RCSfile: Messages.java,v $ 
	Created on $Date: 2008/04/02 11:22:16 $ 
*/
package net.sf.statcvs;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.statcvs.output.ConfigurationOptions;

/**
 * This class manages the externalization of strings that will
 * possiby be presented to the user
 * @author Lukasz Pekacki
 * @version $Id: Messages.java,v 1.11 2008/04/02 11:22:16 benoitx Exp $
 */
public class Messages {
    /**
     * Whitespace constant
     */
    public static final String WS = " ";
    /**
     * Newline constant
     */
    public static final String NL = "\n";

    private static final String BUNDLE_NAME = "net.sf.statcvs.statcvs";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private static ResourceBundle primaryResourceBundle = null;

    /**
     * Returns the value for the specified key. Key-value pairs are specified
     * in the resourcebundle properties file, either the primary one or,
     * it falls back on the original one.
     * @param key key of the requested string
     * @return String
     */
    public static String getString(final String key) {
        final boolean xml = "xml".equalsIgnoreCase(ConfigurationOptions.getOutputFormat());
        String val = null;
        try {
            if (primaryResourceBundle != null) {
                try {
                    val = primaryResourceBundle.getString(key + (xml ? ".xml" : ""));
                    if (val != null) {
                        return val;
                    }
                } catch (final MissingResourceException e) {
                    if (xml) {
                        try {
                            val = primaryResourceBundle.getString(key);
                            if (val != null) {
                                return val;
                            }
                        } catch (final MissingResourceException e2) {
                            // do nofin...
                        }
                    }
                }
            }
            val = RESOURCE_BUNDLE.getString(key + (xml ? ".xml" : ""));
            return val;
        } catch (final MissingResourceException e) {
            try {
                if (xml) {
                    val = RESOURCE_BUNDLE.getString(key);
                    if (val != null) {
                        return val;
                    }
                }
                return '!' + key + '!';
            } catch (final MissingResourceException e2) {
                return '!' + key + '!';
            }
        }
    }

    /**
     * This method enables a user, typically of another project, to set
     * a primary resource bundle. If no value or null, it will revert to
     * the original bundle.
     * @param primaryResourceName
     */
    public static void setPrimaryResource(final String primaryResourceName) {
        primaryResourceBundle = ResourceBundle.getBundle(primaryResourceName);
    }
}

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
    
	$RCSfile: FileUtils.java,v $ 
	Created on $Date: 2009/08/19 22:11:15 $ 
*/
package net.sf.statcvs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Some helpful file functions
 * TODO: Remove redundancy, write tests
 * @author Lukasz Pekacki
 * @version $Id: FileUtils.java,v 1.21 2009/08/19 22:11:15 benoitx Exp $
 */
public class FileUtils {
    /**
     * Copies a file to a specified destination
     * @param inputName File
     * @param destination Filename
     * @throws FileNotFoundException if no input file exists
     * @throws IOException if cannot read or write
     */
    public static void copyFile(final String inputName, final String destination) throws FileNotFoundException, IOException {
        final File input = new File(inputName);
        final File outputFile = new File(destination);
        FileReader in = null;
        FileWriter out = null;
        try {
            in = new FileReader(input);
            out = new FileWriter(outputFile);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    /**
     * Copy a InputStream into a File
     * @param in source
     * @param out destination
     * @throws FileNotFoundException if not found
     * @throws IOException if read/write error
     */
    public static void copyFile(final InputStream in, final File out) throws FileNotFoundException, IOException {
        final InputStream fis = in;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(out);
            final byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) > 0) {
                fos.write(buf, 0, i);
            }
        } finally {
            try {
                fis.close();
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    /**
     * Takes a filename with path and returns just the filename.
     * @param filename a filename with path
     * @return just the filename part
     */
    public static String getFilenameWithoutPath(final String filename) {
        final File f = new File(filename);
        return f.getName();
    }

    /**
     * Returns the os dependent path separator
     * @return String os dependent path separator
     */
    public static String getDirSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * Returns the java path separator
     * @return String java  path separator
     */
    public static String getDefaultDirSeparator() {
        // Thanks for this hint in our bug tracking system
        return java.io.File.separator;
    }

    /**
     * Deletes the ending directory separator of a 
     * given <code>path</code> if there is one and returns
     * the result.
     * Otherwise the path is unhandled returned.
     * 
     * <p>The separator is the one used bye the
     * underlying operating system and it is the one returned
     * bye the <code>getDirSeparator()</code> method.
     * 
     * @param path The <code>path</code> to delete the directory
     * separator from.
     * @return The <code>path</code> without the ending
     * directory separator.
     * @see net.sf.statcvs.util.FileUtils#getDirSeparator
     */
    public static String getPathWithoutEndingSlash(final String path) {
        if (path.endsWith(getDefaultDirSeparator())) {
            final int pos = path.lastIndexOf(getDefaultDirSeparator());
            return path.substring(0, pos);
        }
        return path;
    }

    /**
     * Concatenates <code>path</code> and filename to an
     * absolute filename by inserting the system file separator.
     * 
     * @param path The path to use.
     * @param filename The filename for concatenation.
     * @return The concatenated absolute filename.
     */
    public static String getAbsoluteName(final String path, final String filename) {
        return path + getDirSeparator() + filename;
    }

    /**
     * Returns the last component of a directory path.
     * @param path a directory, ending in "/", for example "src/net/sf/statcvs/"
     * @return the last component of the path, for example "statcvs"
     */
    public static String getDirectoryName(final String path) {
        if ("".equals(path)) {
            throw new IllegalArgumentException("can't get directory name for root");
        }
        final String pathWithoutLastSlash = path.substring(0, path.length() - 1);
        final int lastSlash = pathWithoutLastSlash.lastIndexOf('/');
        if (lastSlash == -1) {
            return pathWithoutLastSlash;
        }
        return pathWithoutLastSlash.substring(lastSlash + 1);
    }

    /**
     * Returns all but the last component of a directory path
     * @param path a directory, ending in "/", for example "src/net/sf/statcvs/"
     * @return all but the last component of the path, for example "src/net/sf/"
     */
    public static String getParentDirectoryPath(final String path) {
        if ("".equals(path)) {
            throw new IllegalArgumentException("can't get directory name for root");
        }
        final String pathWithoutLastSlash = path.substring(0, path.length() - 1);
        final int lastSlash = pathWithoutLastSlash.lastIndexOf('/');
        if (lastSlash == -1) {
            return "";
        }
        return pathWithoutLastSlash.substring(0, lastSlash + 1);
    }

    /**
     * Read a full file into a string.
     * @param urlTxt URL of the text to get
     * @return
     */
    public static String readTextFromURL(final String urlTxt) {
        final StringBuffer buf = new StringBuffer();
        BufferedReader in = null;
        try {
            // Create a URL for the desired page 
            final URL url = new URL(urlTxt);
            // Read all the text returned by the server 
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
                buf.append(str);
            }
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException("Mal-Formed URL " + urlTxt);
        } catch (final IOException e) {
            throw new IllegalArgumentException("can't get file " + urlTxt);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                }
            }
        }
        return buf.toString();
    }
}

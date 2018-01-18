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
 
 $RCSfile: DummyRepositoryFileManager.java,v $ 
 Created on $Date: 2004/02/17 18:37:15 $ 
 */
package net.sf.statsvn.input;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import net.sf.statcvs.input.LogSyntaxException;
import net.sf.statcvs.input.NoLineCountException;
import net.sf.statsvn.util.ISvnProcessor;
import net.sf.statsvn.util.SvnCommandLineProcessor;

/**
 * Dummy <tt>RepositoryFileManager</tt> for unit tests
 * 
 * @author Manuel Schulze
 * @author Jason Kealey <jkealey@shade.ca>
 * @version $Id: DummyRepositoryFileManager.java,v 1.1 2004/02/17 18:37:15
 *          cyganiak Exp $
 */
public class DummyRepositoryFileManager extends RepositoryFileManager {

    protected HashMap hmFinalLineCounts;

    protected String sFinalLineCountsFile;

    protected String sSvnInfoUtilPath;

    protected String sSvnPropgetPath;

    /**
     * Only call this constructor if you provide line counts using (@link
     * #setLinesOfCode(String, int)) and only use (@link
     * #getLinesOfCode(String)) to read them.
     * 
     */
    public DummyRepositoryFileManager() {
        super("foo");
        hmFinalLineCounts = new HashMap();
    }

    /**
     * Creates a new instance with root at <code>pathName</code>.
     * 
     * @param checkedOutPath
     *            the root of the checked out repository
     * @param sSvnInfoUtilPath
     *            the path of a saved svn info command.
     * @param sSvnPropgetPath
     *            the path of a saved svn propget command
     * @param sFinalLineCountsFile
     *            the path of a saved svn list augmented with linecounts
     *            command.
     * @throws IOException
     */
    public DummyRepositoryFileManager(final String checkedOutPath, final String sSvnInfoUtilPath, final String sSvnPropgetPath,
            final String sFinalLineCountsFile) throws IOException {
        super(checkedOutPath);
        this.sSvnInfoUtilPath = sSvnInfoUtilPath;
        this.sSvnPropgetPath = sSvnPropgetPath;
        this.sFinalLineCountsFile = sFinalLineCountsFile;

        getProcessor().getPropgetProcessor().loadBinaryFiles(sSvnPropgetPath);

        FileReader freader = null;
        BufferedReader reader = null;
        try {
            freader = new FileReader(sFinalLineCountsFile);
            reader = new BufferedReader(freader);
            String s;
            hmFinalLineCounts = new HashMap();
            while ((s = reader.readLine()) != null) {
                final String[] vals = s.split(" ");
                if (vals.length == 1) {
                    continue;
                }

                setLinesOfCode(vals[1], Integer.parseInt(vals[0]));
            }
        } finally {
            if (freader != null) {
                freader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Returns line count differences between two revisions of a file.
     * 
     * @param oldRevNr
     *            old revision number
     * @param newRevNr
     *            new revision number
     * @param filename
     *            the filename
     * @return A int[2] array of [lines added, lines removed] is returned.
     * @throws IOException
     *             problem parsing the stream
     */
    public int[] getLineDiff(final String oldRevNr, final String newRevNr, final String filename) throws IOException {
        // return SvnDiffUtils.getLineDiff(oldRevNr, newRevNr, filename);
        final int[] lines = new int[2];
        lines[0] = 0;
        lines[1] = 0;
        return lines;

    }

    /**
     * @see net.sf.statsvn.input.RepositoryFileManager#getLinesOfCode(String)
     */
    public int getLinesOfCode(final String filename) throws NoLineCountException {
        if (hmFinalLineCounts.containsKey(filename)) {
            return ((Integer) hmFinalLineCounts.get(filename)).intValue();
        }
        throw new NoLineCountException();
    }

    public String getRevision(final String filename) throws IOException {
        if (sSvnInfoUtilPath != null) {
            return super.getRevision(filename);
        } else {
            return "";
        }
    }

    /**
     * Is the given path a binary file in the <b>working</b> directory?
     * 
     * @param relativePath
     *            the directory
     * @return true if it is marked as a binary file
     */
    public boolean isBinary(final String relativePath) {
        return getProcessor().getPropgetProcessor().getBinaryFiles().contains(relativePath);
    }

    /**
     * Initializes our representation of the repository.
     * 
     * @throws LogSyntaxException
     *             if the svn info --xml is malformed
     * @throws IOException
     *             if there is an error reading from the stream
     */
    public void loadInfo() throws LogSyntaxException, IOException {

        final FileInputStream stream = new FileInputStream(sSvnInfoUtilPath);
        getProcessor().getInfoProcessor().loadInfo(stream);
    }

    /**
     * Sets the number of lines of code for specified file
     * 
     * @param filename
     *            of file to change
     * @param lines
     *            lines of code for specified file
     */
    public void setLinesOfCode(final String filename, final int lines) {
        hmFinalLineCounts.put(filename, new Integer(lines));
    }

    private ISvnProcessor svnProcessor;

    public ISvnProcessor getProcessor() {
        if (svnProcessor == null)
            svnProcessor = new SvnCommandLineProcessor();
        return svnProcessor;
    }

}

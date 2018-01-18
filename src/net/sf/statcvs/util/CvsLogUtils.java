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
    
	$RCSfile: CvsLogUtils.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.util;

/**
 * Utility class containing various methods related to CVS logfile parsing
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: CvsLogUtils.java,v 1.5 2008/04/02 11:22:15 benoitx Exp $
 */
public class CvsLogUtils {

    /**
     * <p>Determines if a file is in the attic by comparing the location of
     * the RCS file and the working file.</p>
     * 
     * <p>The RCS file is the file containing the version history.
     * It is located in the CVSROOT directory of the repository.
     * It's name ends in ",v". The filename is absoulte.</p>
     * 
     * <p>The working filename is the actual filename relative to the
     * root of the checked-out module.</p>
     * 
     * <p>A file is said to be in the attic if and only if it is dead
     * on the main branch. If a file is in the attic, it's RCS file is
     * moved to a subdirectory called "Attic". This method checks if
     * the RCS file is in the "Attic" subdirectory.
     *  
     * @param rcsFilename a version-controlled file's RCS filename
     * @param workingFilename a version-controlled file's working filename
     * @return <tt>true</tt> if the file is in the attic
     */
    public static boolean isInAttic(final String rcsFilename, final String workingFilename) {
        final int lastDelim = workingFilename.lastIndexOf("/");
        final String filename = workingFilename.substring(lastDelim + 1, workingFilename.length());

        final int rcsPathLength = rcsFilename.length() - filename.length() - 2;
        final String rcsPath = rcsFilename.substring(0, rcsPathLength);
        return rcsPath.endsWith("/Attic/");
    }

    /**
     * Returns <code>true</code> if this revision is part of the main branch,
     * and <code>false</code> if it is part of a side branch. Revisions
     * like 1.1 and 5.212 are on the main branch, 1.1.1.1 and 1.4.2.13 and
     * 1.4.2.13.4.1 are on side branches.
     * @param revisionNumber the revision's number, for example "1.1"
     * @return <code>true</code> if this revision is part of the main branch.
     */
    public static boolean isOnMainBranch(final String revisionNumber) {
        int index = 0;
        int dotCount = 0;
        while (revisionNumber.indexOf('.', index) != -1) {
            index = revisionNumber.indexOf('.', index) + 1;
            dotCount++;
        }
        return (dotCount == 1);
    }

    /**
     * Determines the module name by comparing the RCS filename and
     * the working filename.  
     * @param rcsFilename a version-controlled file's RCS filename
     * @param workingFilename a version-controlled file's working filename
     * @return the module name
     */
    public static String getModuleName(final String rcsFilename, final String workingFilename) {
        int localLenght = workingFilename.length() + ",v".length();
        if (CvsLogUtils.isInAttic(rcsFilename, workingFilename)) {
            localLenght += "/Attic".length();
        }
        final String cvsroot = rcsFilename.substring(0, rcsFilename.length() - localLenght - 1);
        final int lastSlash = cvsroot.lastIndexOf("/");
        if (lastSlash == -1) {
            return "";
        }
        return cvsroot.substring(lastSlash + 1);
    }

    /**
     * Returns <tt>true</tt> if files with a given keyword substitution
     * should be treated as binary files. That is, they should be assumed
     * to be 0 lines of code. Possible values are the same as for the -kXXX
     * option of CVS (for example, kv, kvl, b).
     * @param kws the keyword substitution, as of CVS option -kXXX
     * @return <tt>true</tt> if this is a binary keyword substitution
     * @throws IllegalArgumentException if <tt>kws</tt> is not a known keyword substitution
     */
    public static boolean isBinaryKeywordSubst(final String kws) {
        if ("kv".equals(kws)) {
            return false;
        }
        if ("kvl".equals(kws)) {
            return false;
        }
        if ("k".equals(kws)) {
            return false;
        }
        if ("o".equals(kws)) {
            return false;
        }
        if ("b".equals(kws)) {
            return true;
        }
        if ("v".equals(kws)) {
            return false;
        }
        if ("u".equals(kws)) {
            return false;
        }
        throw new IllegalArgumentException("unknown keyword substitution: " + kws);
    }
}

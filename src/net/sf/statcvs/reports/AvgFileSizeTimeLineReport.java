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
    
	$RCSfile: AvgFileSizeTimeLineReport.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.reportmodel.TimeLine;

/**
 * Time line for the average file size from a specified file list.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: AvgFileSizeTimeLineReport.java,v 1.6 2008/04/02 11:22:15 benoitx Exp $
 */
public class AvgFileSizeTimeLineReport {
    private final TimeLine timeLine;

    /**
     * Creates a new file count time line for a specified list of files.
     * @param files a list of {@link net.sf.statcvs.model.VersionedFile}s
     */
    public AvgFileSizeTimeLineReport(final SortedSet files) {
        timeLine = new TimeLine(Messages.getString("AVERAGE_FILE_SIZE_TITLE"), Messages.getString("RANGE_LOC_PER_FILE"));
        final List revisions = new ArrayList();
        final Iterator filesIt = files.iterator();
        while (filesIt.hasNext()) {
            final VersionedFile file = (VersionedFile) filesIt.next();
            revisions.addAll(file.getRevisions());
        }
        Collections.sort(revisions);
        final Iterator it = revisions.iterator();
        int loc = 0;
        int fileCount = 0;
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            loc += rev.getLinesDelta();
            fileCount += rev.getFileCountDelta();
            final int ratio = (fileCount == 0) ? 0 : loc / fileCount;
            timeLine.addTimePoint(rev.getDate(), ratio);
        }
    }

    /**
     * Returns the result time line
     * @return the result time line
     */
    public TimeLine getTimeLine() {
        return timeLine;
    }
}
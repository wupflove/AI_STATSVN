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
    
	$RCSfile: FileCountTimeLineReport.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.reports;

import java.util.Iterator;
import java.util.SortedSet;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.reportmodel.TimeLine;

/**
 * Time line for the number of non-dead files from a specified file list.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: FileCountTimeLineReport.java,v 1.6 2008/04/02 11:22:15 benoitx Exp $
 */
public class FileCountTimeLineReport {
    private final TimeLine timeLine;

    /**
     * Creates a new file count time line for a specified list of files.
     * @param files a list of {@link net.sf.statcvs.model.VersionedFile}s
     */
    public FileCountTimeLineReport(final SortedSet files) {
        timeLine = new TimeLine(Messages.getString("FILE_COUNT_TITLE"), Messages.getString("RANGE_FILES"));
        timeLine.setInitialValue(0);
        final Iterator filesIt = files.iterator();
        while (filesIt.hasNext()) {
            final VersionedFile file = (VersionedFile) filesIt.next();
            addRevisions(file);
        }
    }

    /**
     * Returns the result time line
     * @return the result time line
     */
    public TimeLine getTimeLine() {
        return timeLine;
    }

    private void addRevisions(final VersionedFile file) {
        final Iterator it = file.getRevisions().iterator();
        while (it.hasNext()) {
            final Revision revision = (Revision) it.next();
            if (revision.getFileCountDelta() != 0 || !it.hasNext()) {
                this.timeLine.addChange(revision.getDate(), revision.getFileCountDelta());
            }
        }
    }
}
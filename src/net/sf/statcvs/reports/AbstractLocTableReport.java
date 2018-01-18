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
    
	$RCSfile: AbstractLocTableReport.java,v $ 
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.reports;

import java.util.Collection;
import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.GenericColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.RatioColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Convenience superclass for table reports related to authors and directories.
 * Contains methods to calculate some common stuff for these tables.
 * @author Lukasz Pekacki
 * @version $Id: AbstractLocTableReport.java,v 1.12 2008/04/02 11:22:15 benoitx Exp $
 */
public abstract class AbstractLocTableReport {

    /**
     * Sort the authors table by name
     * */
    public static final int SORT_BY_NAME = 0;

    /**
     * Sort the authors table by lines of code
     * */
    public static final int SORT_BY_LINES = 1;

    private final ReportConfig config;
    private final Repository content;

    private final IntegerMap changesMap = new IntegerMap();
    private final IntegerMap linesMap = new IntegerMap();

    /**
     * Constructor
     * @param content render table on specified content
     */
    public AbstractLocTableReport(final ReportConfig config) {
        this.config = config;
        this.content = config.getRepository();
    }

    protected void calculateChangesAndLinesPerDeveloper(final Collection revs) {
        final Iterator it = revs.iterator();
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            if (rev.getAuthor() == null || !this.config.isDeveloper(rev.getAuthor())) {
                continue;
            }
            changesMap.addInt(rev.getAuthor(), 1);
            linesMap.addInt(rev.getAuthor(), rev.getNewLines());
        }
    }

    protected void calculateChangesAndLinesPerDirectory(final Collection revisions) {
        final Iterator it = revisions.iterator();
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            final Directory dir = rev.getFile().getDirectory();
            changesMap.addInt(dir, 1);
            linesMap.addInt(dir, rev.getNewLines());
        }
    }

    protected Table createChangesAndLinesTable(final GenericColumn keys, final GenericColumn keys2, final String summary) {

        final Table result = new Table(summary);
        final IntegerColumn changes = new IntegerColumn(Messages.getString("COLUMN_CHANGES"));
        final IntegerColumn linesOfCode = new IntegerColumn(Messages.getString("COLUMN_LOC"));
        final RatioColumn linesPerChange = new RatioColumn(Messages.getString("COLUMN_LOC_PER_CHANGE"), linesOfCode, changes);
        keys.setTotal(Messages.getString("TOTALS"));
        changes.setShowPercentages(true);
        linesOfCode.setShowPercentages(true);
        result.addColumn(keys);
        if (keys2 != null) {
            keys.setTotal("");
            keys2.setTotal(Messages.getString("TOTALS"));
            result.addColumn(keys2);
        }
        result.addColumn(changes);
        result.addColumn(linesOfCode);
        result.addColumn(linesPerChange);
        result.setKeysInFirstColumn(true);

        Iterator it;
        it = linesMap.iteratorSortedByValueReverse();
        while (it.hasNext()) {
            final Object key = it.next();
            keys.addValue(key);
            if (keys2 != null) {
                keys2.addValue(key);
            }
            changes.addValue(changesMap.get(key));
            linesOfCode.addValue(linesMap.get(key));
        }
        if (result.getRowCount() > 1) {
            result.setShowTotals(true);
        }
        return result;
    }

    protected Repository getContent() {
        return content;
    }

    protected IntegerMap getChangesMap() {
        return changesMap;
    }

    protected IntegerMap getLinesMap() {
        return linesMap;
    }

    public int getDeveloperCount() {
        int result = 0;
        final Iterator it = getContent().getAuthors().iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            if (this.config.isDeveloper(author)) {
                result++;
            }
        }
        return result;
    }
}
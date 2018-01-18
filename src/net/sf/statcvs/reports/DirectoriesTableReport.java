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
    
	$RCSfile: DirectoriesTableReport.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.reports;

import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.reportmodel.DirectoryColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Table report which creates a table containing the names of
 * all directories and the number of changes and LOC in them.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DirectoriesTableReport.java,v 1.6 2008/04/02 11:22:15 benoitx Exp $
 */
public class DirectoriesTableReport implements TableReport {
    private final Repository content;
    private Table table = null;
    private final IntegerMap changesMap = new IntegerMap();
    private final IntegerMap linesMap = new IntegerMap();

    /**
     * Creates a table report containing all directories and their
     * respective number of changes and LOC.
     * @param content the version control source data
     */
    public DirectoriesTableReport(final Repository content) {
        this.content = content;
    }

    public void calculate() {
        Iterator it = this.content.getRevisions().iterator();
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            final Directory dir = rev.getFile().getDirectory();
            changesMap.addInt(dir, 1);
            if (rev.isBeginOfLog()) {
                linesMap.addInt(dir, rev.getLines());
            } else {
                linesMap.addInt(dir, rev.getLinesDelta());
            }
        }
        this.table = new Table(Messages.getString("DIRECTORIES_TABLE_SUMMARY"));
        final IntegerColumn changes = new IntegerColumn(Messages.getString("COLUMN_CHANGES"));
        final IntegerColumn linesOfCode = new IntegerColumn(Messages.getString("COLUMN_LOC"));
        final DirectoryColumn keys = new DirectoryColumn();
        keys.setTotal(Messages.getString("TOTALS"));
        changes.setShowPercentages(true);
        linesOfCode.setShowPercentages(true);
        this.table.addColumn(keys);
        this.table.addColumn(changes);
        this.table.addColumn(linesOfCode);
        this.table.setKeysInFirstColumn(true);

        it = linesMap.iteratorSortedByValueReverse();
        while (it.hasNext()) {
            final Object key = it.next();
            keys.addValue(key);
            changes.addValue(changesMap.get(key));
            linesOfCode.addValue(linesMap.get(key));
        }
        if (this.table.getRowCount() > 1) {
            this.table.setShowTotals(true);
        }
    }

    public Table getTable() {
        return table;
    }
}
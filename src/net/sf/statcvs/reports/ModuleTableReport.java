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
    
	$RCSfile: ModuleTableReport.java,v $
	$Date: 2009/08/22 10:30:42 $
*/
package net.sf.statcvs.reports;

import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Module;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.SimpleTextColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Table report which creates a table containing the names of
 * all modules and the number of changes and LOC in them.
 * 
 * @author Benoit Xhenseval
 * @version $Id: ModuleTableReport.java,v 1.2 2009/08/22 10:30:42 benoitx Exp $
 */
public class ModuleTableReport implements TableReport {
    private final Repository content;
    private Table table = null;
    private final IntegerMap filesMap = new IntegerMap();
    private final IntegerMap changesMap = new IntegerMap();
    private final IntegerMap linesMap = new IntegerMap();

    /**
     * Creates a table report containing all modules and their
     * respective number of changes and LOC.
     * @param content the version control source data
     */
    public ModuleTableReport(final Repository content) {
        this.content = content;
    }

    public void calculate() {
        Iterator it = this.content.getModules().values().iterator();
        while (it.hasNext()) {
            final Module mod = (Module) it.next();
            //            final Directory dir = rev.getFile().getDirectory();
            final Iterator files = mod.getFiles().iterator();
            while (files.hasNext()) {
                final VersionedFile vf = (VersionedFile) files.next();
                filesMap.addInt(mod.getName(), 1);
                changesMap.addInt(mod.getName(), vf.getRevisions().size());
            }
            linesMap.addInt(mod.getName(), mod.getCurrentLinesOfCode());
        }
        this.table = new Table(Messages.getString("MODULES_TABLE_SUMMARY"));
        final IntegerColumn changes = new IntegerColumn(Messages.getString("COLUMN_CHANGES"));
        final IntegerColumn linesOfCode = new IntegerColumn(Messages.getString("COLUMN_LOC"));
        final IntegerColumn filesCol = new IntegerColumn(Messages.getString("FILES"));
        final SimpleTextColumn keys = new SimpleTextColumn(Messages.getString("COLUMN_MODULES"));
        keys.setTotal(Messages.getString("TOTALS"));
        changes.setShowPercentages(true);
        linesOfCode.setShowPercentages(true);
        this.table.addColumn(keys);
        this.table.addColumn(filesCol);
        this.table.addColumn(changes);
        this.table.addColumn(linesOfCode);
        this.table.setKeysInFirstColumn(true);

        it = linesMap.iteratorSortedByValueReverse();
        while (it.hasNext()) {
            final Object key = it.next();
            keys.addValue(key);
            changes.addValue(changesMap.get(key));
            linesOfCode.addValue(linesMap.get(key));
            filesCol.addValue(filesMap.get(key));
        }
        if (this.table.getRowCount() > 1) {
            this.table.setShowTotals(true);
        }
    }

    public Table getTable() {
        return table;
    }
}
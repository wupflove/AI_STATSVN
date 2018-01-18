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
    
	$RCSfile: Table.java,v $
	$Date: 2008/04/02 11:22:14 $
*/
package net.sf.statcvs.reportmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a data table for a report. Columns may be added to the table.
 * Values can be added to the columns. Finally, the table can be rendered
 * as HTML.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: Table.java,v 1.2 2008/04/02 11:22:14 benoitx Exp $
 */
public class Table {

    private final String summary;
    private boolean keysInFirstColumn = false;
    private boolean showTotals = false;
    private final List columns = new ArrayList();

    /**
     * Creates a new table model
     * @param summary a short summary of the table data, intended for
     * non-visual web browsers 
     */
    public Table(final String summary) {
        this.summary = summary;
    }

    /**
     * set if the first column contains keys that identify each row
     * @param enabled <tt>true</tt> if first column contains keys
     */
    public void setKeysInFirstColumn(final boolean enabled) {
        keysInFirstColumn = enabled;
    }

    /**
     * Returns if the first column contains keys that identify each row
     * @return <tt>true</tt> if first column contains keys
     */
    public boolean hasKeysInFirstColumn() {
        return keysInFirstColumn;
    }

    /**
     * set if totals of each column should be shown
     * @param enabled <tt>true</tt> if totals should be shown
     */
    public void setShowTotals(final boolean enabled) {
        showTotals = enabled;
    }

    /**
     * Returns if totals of each column should be shown
     * @return <tt>true</tt> if so
     */
    public boolean showTotals() {
        return showTotals;
    }

    /**
     * Returns the summary text of the table. This is intended for non-visual
     * web browsers.
     * @return the table summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Returns the number of data rows in the table.
     * @return number of data rows in the table
     */
    public int getRowCount() {
        int result = 0;
        final Iterator it = columns.iterator();
        while (it.hasNext()) {
            final Column column = (Column) it.next();
            if (column.getRows() > result) {
                result = column.getRows();
            }
        }
        return result;
    }

    /**
     * Adds a column to the table
     * @param column the column
     */
    public void addColumn(final Column column) {
        columns.add(column);
    }

    /**
     * Returns an iterator of all {@link Column} objects of the table
     * @return an iterator of Columns
     */
    public Iterator getColumnIterator() {
        return columns.iterator();
    }
}
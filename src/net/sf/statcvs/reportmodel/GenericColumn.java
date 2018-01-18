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
    
	$RCSfile: GenericColumn.java,v $
	$Date: 2008/04/02 11:22:14 $
*/
package net.sf.statcvs.reportmodel;

import java.util.ArrayList;
import java.util.List;

import net.sf.statcvs.renderer.TableCellRenderer;

/**
 * A generic column with a text header and a text total. Each cell contains
 * an <tt>Object</tt>. The renderCell method must be implemented by subclasses. 
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: GenericColumn.java,v 1.2 2008/04/02 11:22:14 benoitx Exp $
 */
public abstract class GenericColumn extends Column {

    private final String title;
    private final List values = new ArrayList();
    private String total = null;

    /**
     * Creates a new <tt>GenericColumn</tt> with the given head
     * @param title the head of the column
     */
    public GenericColumn(final String title) {
        this.title = title;
    }

    /**
     * Sets the total for this column
     * @param value the total for this column
     */
    public void setTotal(final String value) {
        this.total = value;
    }

    /**
     * Adds a value to this column (in a new row)
     * @param value the new value
     */
    public void addValue(final Object value) {
        values.add(value);
    }

    /**
     * Returns a value of the column
     * @param rowIndex the row, starting at 0
     * @return the value
     */
    public Object getValue(final int rowIndex) {
        return values.get(rowIndex);
    }

    /**
     * @see net.sf.statcvs.reportmodel.Column#getRows()
     */
    public int getRows() {
        return values.size();
    }

    /**
     * @see net.sf.statcvs.reportmodel.Column#renderHead(net.sf.statcvs.renderer.TableCellRenderer)
     */
    public void renderHead(final TableCellRenderer renderer) {
        renderer.renderCell(title);
    }

    /**
     * @see net.sf.statcvs.reportmodel.Column#renderCell
     */
    public abstract void renderCell(int rowIndex, TableCellRenderer renderer);

    /**
     * @see net.sf.statcvs.reportmodel.Column#renderTotal
     */
    public void renderTotal(final TableCellRenderer renderer) {
        if (total == null) {
            renderer.renderEmptyCell();
        } else {
            renderer.renderCell(total);
        }
    }
}
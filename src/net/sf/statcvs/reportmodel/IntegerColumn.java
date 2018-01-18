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
    
	$RCSfile: IntegerColumn.java,v $
	$Date: 2009/03/09 21:45:42 $
*/
package net.sf.statcvs.reportmodel;

import java.util.ArrayList;
import java.util.List;

import net.sf.statcvs.renderer.TableCellRenderer;

/**
 * A column of integer values. The column's total is the sum of all values. 
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: IntegerColumn.java,v 1.4 2009/03/09 21:45:42 benoitx Exp $
 */
public class IntegerColumn extends Column {

    private final String title;
    private final List values = new ArrayList();
    private int sum = 0;
    private boolean showValues = true;
    private boolean showPercentages = true;

    /**
     * Creates a new <tt>SimpleTextColumn</tt> with the given head
     * @param title the head of the column
     */
    public IntegerColumn(final String title) {
        this.title = title;
    }

    /**
     * Set if the actual integer values should be shown
     * @param enable show values?
     */
    public void setShowValues(final boolean enable) {
        showValues = enable;
    }

    /**
     * Set if the values should be shown as percentages
     * @param enable show percentages?
     */
    public void setShowPercentages(final boolean enable) {
        showPercentages = enable;
    }

    /**
     * Adds a value to this column (in a new row)
     * @param value the new value
     */
    public void addValue(final int value) {
        values.add(new Integer(value));
        sum += value;
    }

    /**
     * Returns a value in the column
     * @param rowIndex the row to get, starting at 0
     * @return the value of this row
     */
    public int getValue(final int rowIndex) {
        return ((Integer) values.get(rowIndex)).intValue();
    }

    /**
     * Returns the sum of all values in the column
     * @return sum
     */
    public int getSum() {
        return sum;
    }

    /**
     * Sets the sum of the column. Useful if, for example, the
     * column contains only the top 10 values of more values, but
     * the column total should reflect all values.
     * @param sum the column's total
     */
    public void setSum(final int sum) {
        this.sum = sum;
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
    public void renderCell(final int rowIndex, final TableCellRenderer renderer) {
        callRenderer(renderer, getValue(rowIndex));
    }

    /**
     * @see net.sf.statcvs.reportmodel.Column#renderTotal(net.sf.statcvs.renderer.TableCellRenderer)
     */
    public void renderTotal(final TableCellRenderer renderer) {
        callRenderer(renderer, sum);
    }

    private void callRenderer(final TableCellRenderer renderer, final int value) {
        if (showValues && showPercentages) {
            renderer.renderIntegerCell(value, sum);
        } else if (showValues) {
            renderer.renderIntegerCell(value);
        } else if (showPercentages) {
            renderer.renderPercentageCell((double) value / (double) sum);
        } else {
            renderer.renderEmptyCell();
        }
    }
}
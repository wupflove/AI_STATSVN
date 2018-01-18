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

	$RCSfile: TableRenderer.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.renderer;

import java.util.Iterator;

import net.sf.statcvs.pages.HTML;
import net.sf.statcvs.pages.MarkupSyntax;
import net.sf.statcvs.reportmodel.Column;
import net.sf.statcvs.reportmodel.Table;

/**
 * Renders a {@link net.sf.statcvs.reportmodel.Table} to HTML
 *
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TableRenderer.java,v 1.41 2008/04/02 11:22:15 benoitx Exp $
 */
public class TableRenderer {

    private final Table table;
    private final HTMLTableCellRenderer renderer = new HTMLTableCellRenderer();

    /**
     * Creates a new table renderer for the given table model
     * @param table the table to render
     */
    public TableRenderer(final Table table, final MarkupSyntax output) {
        this.table = table;
        renderer.setOutput(output);
    }

    /**
     * Renders the table to HTML
     * @return a String of HTML
     */
    public String getRenderedTable() {
        final StringBuffer result = new StringBuffer("  <table ").append(renderer.getOutput().getTableFormat());
        result.append(" rules=\"groups\" summary=\"").append(HTML.escape(table.getSummary()));
        result.append("\">\n");
        result.append(getColumnDescriptions());
        result.append(getTableHead());
        if (table.showTotals()) {
            result.append(getTableTotals());
        }
        result.append(getTableBody());
        result.append("  </table>\n\n");
        return result.toString();
    }

    private String getColumnDescriptions() {
        final StringBuffer result = new StringBuffer();
        final Iterator it = table.getColumnIterator();
        boolean isFirstColumn = true;
        while (it.hasNext()) {
            it.next();
            if (table.hasKeysInFirstColumn() && isFirstColumn) {
                result.append("    <colgroup align=\"left\"/>\n");
                isFirstColumn = false;
            } else {
                result.append("    <colgroup align=\"right\"/>\n");
            }
        }
        return result.toString();
    }

    private String getTableHead() {
        final StringBuffer result = new StringBuffer("    <thead>\n      <tr>\n");
        final Iterator it = table.getColumnIterator();
        while (it.hasNext()) {
            final Column column = (Column) it.next();
            column.renderHead(renderer);
            result.append("        ").append(renderer.getColumnHead()).append("\n");
        }
        result.append("      </tr>\n    </thead>\n");
        return result.toString();
    }

    private String getTableTotals() {
        final StringBuffer result = new StringBuffer("    <tfoot>\n      <tr>\n");
        final Iterator it = table.getColumnIterator();
        boolean isFirstColumn = true;
        while (it.hasNext()) {
            final Column column = (Column) it.next();
            column.renderTotal(renderer);
            if (isFirstColumn && table.hasKeysInFirstColumn()) {
                result.append("        ").append(renderer.getRowHead()).append("\n");
                isFirstColumn = false;
            } else {
                result.append("        ").append(renderer.getTableCell()).append("\n");
            }
        }
        result.append("      </tr>\n    </tfoot>\n");
        return result.toString();
    }

    private String getTableBody() {
        final StringBuffer result = new StringBuffer("    <tbody>\n");
        for (int i = 0; i < table.getRowCount(); i++) {
            result.append(getTableRow(i));
        }
        result.append("    </tbody>\n");
        return result.toString();
    }

    private String getTableRow(final int rowIndex) {
        final StringBuffer result = new StringBuffer();
        if (rowIndex % 2 == 0) {
            result.append("      <tr ").append(renderer.getEvenRowFormat()).append(">\n");
        } else {
            result.append("      <tr ").append(renderer.getOddRowFormat()).append(">\n");
        }
        final Iterator it = table.getColumnIterator();
        boolean isFirstColumn = true;
        while (it.hasNext()) {
            final Column column = (Column) it.next();
            column.renderCell(rowIndex, renderer);
            if (isFirstColumn && table.hasKeysInFirstColumn()) {
                result.append("        ").append(renderer.getRowHead()).append("\n");
                isFirstColumn = false;
            } else {
                result.append("        ").append(renderer.getTableCell()).append("\n");
            }
        }
        result.append("      </tr>\n");
        return result.toString();
    }
}
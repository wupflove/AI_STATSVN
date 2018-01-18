/**
 * 
 */
package net.sf.statcvs.pages.xml;

import java.util.Iterator;

import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.renderer.XMLRenderer;
import net.sf.statcvs.reportmodel.Column;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.reports.DevelopersTableReport;
import net.sf.statcvs.reports.TableReport;

import org.jdom.Element;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: AllDevelopersXml.java,v 1.2 2008/04/02 11:22:16 benoitx Exp $
 * 
 * This is the class which generates the developer information of the xml report 
 */
public class AllDevelopersXml {
    private static final int NO_OF_COLS_IN_TABLE = 50;
    private final XMLRenderer renderer = new XMLRenderer();
    private final ReportConfig config;
    private DevelopersTableReport developers;
    private Table table;

    /**
     * @param config Configuration and data for the report suite
     */
    public AllDevelopersXml(final ReportConfig config) {
        this.config = config;
    }

    /**
     * returns jdom element which contains data extracted from DeveloeprsTableReport
     * 
     * @returns Element
     */
    public Element toFile() {
        this.developers = new DevelopersTableReport(this.config);
        final TableReport tr = developers;
        tr.calculate();
        this.table = tr.getTable();
        final Element div = new Element(XmlTags.TAG_DEVELOPERS);

        final String[] str = new String[NO_OF_COLS_IN_TABLE];
        for (int j = 0; j < table.getRowCount(); j++) {
            Element col = null;
            int i = 0;
            final Iterator it = table.getColumnIterator();
            final Iterator itr = table.getColumnIterator();
            while (it.hasNext()) {
                final Column column = (Column) it.next();
                column.renderHead(renderer);

                str[i] = renderer.getColumnHead();
                if (i == 0) {
                    col = new Element(str[i]);
                } else {
                    col.addContent(new Element(str[i]));
                }
                i++;
            }
            boolean isFirstColumn = true;
            int k = 0;
            while (itr.hasNext()) {
                final Column column = (Column) itr.next();
                column.renderCell(j, renderer);
                if (isFirstColumn && table.hasKeysInFirstColumn()) {
                    col.setAttribute("name", renderer.getRowHead().toLowerCase());
                    isFirstColumn = false;
                } else {
                    col.getChild(str[k]).setText(renderer.getTableCell());
                }
                k++;
            }
            div.addContent(col);
        }
        return div;
    }
}

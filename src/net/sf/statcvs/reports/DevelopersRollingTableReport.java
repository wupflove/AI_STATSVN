package net.sf.statcvs.reports;

import net.sf.statcvs.Messages;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.AuthorColumn;
import net.sf.statcvs.reportmodel.Table;

/**
 * Table report which creates a table containing the names of
 * all developers, their LOC contributions and number of changes over the last n months.
 * 
 * @author Benoit Xhenseval
 * @version $Id: DevelopersRollingTableReport.java,v 1.2 2009/03/11 16:28:45 benoitx Exp $
 */
public class DevelopersRollingTableReport extends AbstractRollingLocTableReport implements TableReport {
    private Table table = null;

    /**
     * Creates a table report containing all authors, their
     * number of changes and LOC contributions.
     * @param content the version control source data
     */
    public DevelopersRollingTableReport(final ReportConfig config) {
        super(config);
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#calculate()
     */
    public void calculate() {
        calculateChangesAndLinesPerDeveloper(getContent().getRevisions());
        table = createChangesAndLinesTable(new AuthorColumn(), null, Messages.getString("AUTHORS_TABLE_SUMMARY"));
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#getTable()
     */
    public Table getTable() {
        return table;
    }
}

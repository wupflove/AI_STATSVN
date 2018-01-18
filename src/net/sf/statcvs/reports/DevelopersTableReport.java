package net.sf.statcvs.reports;

import net.sf.statcvs.Messages;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.AuthorColumn;
import net.sf.statcvs.reportmodel.AuthorIdColumn;
import net.sf.statcvs.reportmodel.Table;

/**
 * Table report which creates a table containing the names of
 * all developers, their LOC contributions and number of changes.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DevelopersTableReport.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class DevelopersTableReport extends AbstractLocTableReport implements TableReport {
    private Table table = null;

    /**
     * Creates a table report containing all authors, their
     * number of changes and LOC contributions.
     * @param content the version control source data
     */
    public DevelopersTableReport(final ReportConfig config) {
        super(config);
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#calculate()
     */
    public void calculate() {
        calculateChangesAndLinesPerDeveloper(getContent().getRevisions());
        table = createChangesAndLinesTable(new AuthorColumn(), new AuthorIdColumn(), Messages.getString("AUTHORS_TABLE_SUMMARY"));
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#getTable()
     */
    public Table getTable() {
        return table;
    }
}

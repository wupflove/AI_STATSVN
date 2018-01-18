package net.sf.statcvs.reports;

import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.AuthorColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.Table;

/**
 * Table report which creates a table containing the names of the
 * top 10 developers and their LOC contributions.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TopDevelopersTableReport.java,v 1.2 2008/04/02 11:22:15 benoitx Exp $
 */
public class TopDevelopersTableReport extends AbstractLocTableReport implements TableReport {
    private Table table = null;

    /**
     * Creates a table report containing the top 10 authors and their
     * LOC contributions
     * @param content the version control source data
     */
    public TopDevelopersTableReport(final ReportConfig config) {
        super(config);
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#calculate()
     */
    public void calculate() {
        if (this.table != null) {
            return;
        }
        String summary;
        if (getDeveloperCount() > 10) {
            summary = Messages.getString("TOP_AUTHORS_TABLE_SUMMARY1");
        } else {
            summary = Messages.getString("TOP_AUTHORS_TABLE_SUMMARY2");
        }
        table = new Table(summary);
        final AuthorColumn authors = new AuthorColumn();
        final IntegerColumn linesOfCode = new IntegerColumn(Messages.getString("COLUMN_LOC"));
        linesOfCode.setShowPercentages(true);
        table.addColumn(authors);
        table.addColumn(linesOfCode);
        table.setKeysInFirstColumn(true);

        calculateChangesAndLinesPerDeveloper(getContent().getRevisions());
        int lines = 0;
        final Iterator it = getLinesMap().iteratorSortedByValueReverse();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            authors.addValue(author);
            linesOfCode.addValue(getLinesMap().get(author));
            lines++;
            if (lines == 10) {
                break;
            }
        }
        linesOfCode.setSum(getLinesMap().sum());
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#getTable()
     */
    public Table getTable() {
        return table;
    }
}

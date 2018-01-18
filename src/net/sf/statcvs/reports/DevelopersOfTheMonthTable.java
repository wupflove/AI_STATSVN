package net.sf.statcvs.reports;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.pages.CommitLogPageMaker;
import net.sf.statcvs.pages.TwitterHelp;
import net.sf.statcvs.reportmodel.AuthorColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.LinkColumn;
import net.sf.statcvs.reportmodel.SimpleTextColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Table report which creates a table containing the names of all developers,
 * their LOC contributions and number of changes.
 * 
 * @author Anja Jentzsch (anja@anjeve.de)
 * @version $Id: DevelopersTableReport.java,v 1.1 2006/12/08 16:19:25 cyganiak
 *          Exp $
 */
public class DevelopersOfTheMonthTable implements TableReport {
    private final ReportConfig config;
    private final Repository repository;
    private Table table;

    private final static String[] MONTH_NAME = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };

    /**
     * Creates a table report containing all authors, their number of changes
     * and LOC contributions.
     * 
     * @param content
     *            the version control source data
     */
    public DevelopersOfTheMonthTable(final ReportConfig config) {
        this.repository = config.getRepository();
        this.config = config;
    }

    public void calculate() {
        final LinkColumn months = new LinkColumn("Month");
        final AuthorColumn developers = new AuthorColumn();
        final IntegerColumn loc = new IntegerColumn("Lines");
        final SimpleTextColumn twitter = new SimpleTextColumn("Tweet This");
        loc.setShowPercentages(false);

        this.table = new Table("Repository Tags");
        this.table.setKeysInFirstColumn(true);
        this.table.addColumn(months);
        this.table.addColumn(developers);
        this.table.addColumn(loc);
        if (ConfigurationOptions.isEnableTwitterButton()) {
            this.table.addColumn(twitter);
        }
        final Date start = this.repository.getFirstDate();
        final Date end = this.repository.getLastDate();
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(end);
        final Calendar startCal = new GregorianCalendar();
        startCal.setTime(start);
        while (true) {
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final String month_year = MONTH_NAME[month] + " " + year;
            final IntegerMap developerMap = getMostActiveUserOfMonth(month, year);
            if (developerMap.size() > 0) {
                months.addValue(CommitLogPageMaker.getURL(calendar.getTime()), month_year);
                final Iterator it = developerMap.iteratorSortedByValueReverse();
                final Author developer = (Author) it.next();
                developers.addValue(developer);
                loc.addValue(developerMap.get(developer));
                twitter.addValue(TwitterHelp.buildDeveloperOfMonthLink(developer, developerMap.get(developer), repository, month_year, config));
            }

            if (calendar.get(Calendar.YEAR) == startCal.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == startCal.get(Calendar.MONTH)) {
                break;
            }
            calendar.add(Calendar.MONTH, -1);
        }
    }

    private IntegerMap getMostActiveUserOfMonth(final int month, final int year) {
        final Collection revisions = this.repository.getRevisions();
        final Calendar calendar = new GregorianCalendar();
        final IntegerMap developerMap = new IntegerMap();
        final Iterator it = revisions.iterator();
        while (it.hasNext()) {
            final Revision revision = (Revision) it.next();
            calendar.setTime(revision.getDate());
            if (calendar.get(Calendar.YEAR) != year || calendar.get(Calendar.MONTH) != month || revision.getAuthor() == null) {
                continue;
            }
            if (developerMap.contains(revision.getAuthor())) {
                final int loc = developerMap.get(revision.getAuthor());
                developerMap.put(revision.getAuthor(), revision.getNewLines() + loc);
            } else {
                developerMap.put(revision.getAuthor(), revision.getNewLines());
            }
        }
        return developerMap;
    }

    public Table getTable() {
        return this.table;
    }
}

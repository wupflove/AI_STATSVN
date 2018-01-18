package net.sf.statcvs.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.pages.HTML;
import net.sf.statcvs.pages.CommitLogPageMaker;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.LinkColumn;
import net.sf.statcvs.reportmodel.SimpleTextColumn;
import net.sf.statcvs.reportmodel.Table;

/**
 * Collects information about repository tags.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: TagReport.java,v 1.3 2009/08/22 10:30:42 benoitx Exp $
 */
public class TagReport implements TableReport {
    private final ReportConfig config;
    private final Repository repository;
    private Table table;

    public TagReport(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    public void calculate() {
        final LinkColumn tags = new LinkColumn("Name");
        final SimpleTextColumn dates = new SimpleTextColumn("Date");
        final IntegerColumn loc = new IntegerColumn("Lines");
        loc.setShowPercentages(false);
        final IntegerColumn churn = new IntegerColumn("LOC Churn");
        final IntegerColumn developers = new IntegerColumn("Developers");
        developers.setShowPercentages(false);

        this.table = new Table("Repository Tags");
        this.table.setKeysInFirstColumn(true);
        this.table.addColumn(tags);
        this.table.addColumn(dates);
        this.table.addColumn(loc);
        this.table.addColumn(churn);
        this.table.addColumn(developers);

        final List tagList = new ArrayList(this.repository.getSymbolicNames());
        tagList.add(this.repository.getHead());
        Collections.reverse(tagList);
        final Iterator it = tagList.iterator();
        while (it.hasNext()) {
            final SymbolicName tag = (SymbolicName) it.next();
            final Date startDate = getStartDate(tag);
            final List revisions = getRevisionsBetween(startDate, tag.getDate());
            if (tag == this.repository.getHead()) {
                tags.addValue(null, "(now)");
            } else {
                tags.addValue(CommitLogPageMaker.getURL(tag.getDate()) + "#" + CommitLogPageMaker.getAnchor(tag), tag.getName());
            }
            dates.addValue(HTML.getDate(tag.getDate()));
            loc.addValue(getLOC(tag));
            churn.addValue(getLOCChurn(revisions));
            developers.addValue(countDevelopers(revisions));
        }
    }

    public Table getTable() {
        return this.table;
    }

    private int getLOC(final SymbolicName tag) {
        int loc = 0;
        final Iterator it = tag.getRevisions().iterator();
        while (it.hasNext()) {
            final Revision revision = (Revision) it.next();
            loc += revision.getLines();
        }
        return loc;
    }

    /**
     * @param start Exclusive
     * @param end Inclusive
     */
    private List getRevisionsBetween(final Date start, final Date end) {
        final List revisions = new ArrayList();
        final Iterator it = this.repository.getRevisions().iterator();
        while (it.hasNext()) {
            final Revision revision = (Revision) it.next();
            final long time = revision.getDate().getTime();
            if (time > start.getTime() && time <= end.getTime()) {
                revisions.add(revision);
            }
        }
        return revisions;
    }

    private int getLOCChurn(final List revisions) {
        int churn = 0;
        final Iterator it = revisions.iterator();
        while (it.hasNext()) {
            final Revision revision = (Revision) it.next();
            churn += revision.getNewLines();
        }
        return churn;
    }

    private Date getStartDate(final SymbolicName tag) {
        final SortedSet earlierTags = this.repository.getSymbolicNames().headSet(tag);
        if (earlierTags.isEmpty()) {
            return this.repository.getFirstDate();
        }
        return ((SymbolicName) earlierTags.last()).getDate();
    }

    private int countDevelopers(final Collection revisions) {
        final Set authors = new HashSet();
        Iterator it = revisions.iterator();
        while (it.hasNext()) {
            final Revision revision = (Revision) it.next();
            authors.add(revision.getAuthor());
        }
        int result = 0;
        it = authors.iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            if (this.config.isDeveloper(author)) {
                result++;
            }
        }
        return result;
    }
}

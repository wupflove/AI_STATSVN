package net.sf.statcvs.pages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.ReportConfig;

public class CommitLogPageGroupMaker {
    private final ReportConfig config;
    private final Repository repository;

    public CommitLogPageGroupMaker(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    public PageGroup getPages() {
        final PageGroup pages = new PageGroup("Commit Logs");
        List commits = this.repository.getCommits();
        if (commits == null || commits.isEmpty()) {
            return null;
        }
        final Date start = this.repository.getFirstDate();
        final Date end = this.repository.getLastDate();
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(end);
        final Calendar startCal = new GregorianCalendar();
        startCal.setTime(start);
        final List results = new ArrayList();
        boolean firstPage = true;
        while (true) {
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);

            final NavigationNode page = new CommitLogPageMaker(this.config, year, month, commits, firstPage).toFile();
            results.add(page);
            if (calendar.get(Calendar.YEAR) == startCal.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == startCal.get(Calendar.MONTH)) {
                break;
            }
            firstPage = false;
            calendar.add(Calendar.MONTH, -1);
        }
        final Iterator it = results.iterator();
        while (it.hasNext()) {
            pages.add((NavigationNode) it.next());
        }
        pages.setShowLinkToPreviousSibling(true);
        return pages;
    }
}

package net.sf.statcvs.pages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.output.ReportConfig;

public class CommitLogPageMaker {
    private final static String[] MONTH_TWO_CHARACTERS = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
    private final static String[] MONTH_NAME = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };

    public static String getAnchor(final SymbolicName tag) {
        return "tag-" + tag.getName().replace('.', '_');
    }

    public static String getURL(final Date date) {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return getFileName(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)) + ".html";
    }

    private static String getFileName(final int year, final int month) {
        return year + "-" + MONTH_TWO_CHARACTERS[month];
    }

    private final ReportConfig config;
    private final int year;
    private final int month;
    private final boolean firstLogPage;
    private final List commits = new ArrayList();

    /**
     * Creates a new LogPageMaker.
     * @param year The log page's year
     * @param month The log page's month (0 for January)
     * @param commits A list of commits; those not in the
     * 		right month will be ignored
     * @param outputFormat
     */
    public CommitLogPageMaker(final ReportConfig config, final int year, final int month, final Collection commits, final boolean firstLogPage) {
        this.config = config;
        this.year = year;
        this.month = month;
        this.firstLogPage = firstLogPage;
        final Calendar calendar = new GregorianCalendar();
        final Iterator it = commits.iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            calendar.setTime(commit.getDate());
            if (calendar.get(Calendar.YEAR) != year || calendar.get(Calendar.MONTH) != month) {
                continue;
            }
            this.commits.add(commit);
        }
    }

    public NavigationNode toFile() {
        final Page result = this.config.createPage(getFileName(), getTitle(), getTitle() + " Commit Log");
        result.addAttribute("Number of Commits", this.commits.size());
        result.addAttribute("Number of Active Developers", countActiveDevelopers());
        if (!this.commits.isEmpty()) {
            result.addRawContent(new CommitListFormatter(this.config, this.commits, getTags(), true).render());
        }
        return result;
    }

    private String getFileName() {
        if (!firstLogPage) {
            return getFileName(this.year, this.month);
        }
        return "commitlog";
    }

    private String getTitle() {
        return MONTH_NAME[this.month] + " " + year;
    }

    private int countActiveDevelopers() {
        final Set developers = new HashSet();
        final Iterator it = this.commits.iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            developers.add(commit.getAuthor());
        }
        return developers.size();
    }

    private List getTags() {
        final List tags = new ArrayList();
        final Calendar calendar = new GregorianCalendar();
        final Iterator it = this.config.getRepository().getSymbolicNames().iterator();
        while (it.hasNext()) {
            final SymbolicName tag = (SymbolicName) it.next();
            calendar.setTime(tag.getDate());
            if (calendar.get(Calendar.YEAR) == this.year && calendar.get(Calendar.MONTH) == this.month) {
                tags.add(tag);
            }
        }
        Collections.reverse(tags);
        return tags;
    }
}

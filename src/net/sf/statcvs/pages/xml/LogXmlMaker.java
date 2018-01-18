package net.sf.statcvs.pages.xml;

/**
 * This is a mere copy of the LogXMLpagemaker class with few changes done
 * */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.output.ReportConfig;

import org.jdom.Element;

public class LogXmlMaker {
    private final static String[] MONTH_TWO_CHARACTERS = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };

    public static String getAnchor(final SymbolicName tag) {
        return "tag-" + tag.getName();
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
    private final List commits = new ArrayList();

    /**
     * Creates a new LogPageMaker.
     * @param year The log page's year
     * @param month The log page's month (0 for January)
     * @param commits A list of commits; those not in the
     * 		right month will be ignored
     * @param outputFormat
     */
    public LogXmlMaker(final ReportConfig config, final Collection commits) {
        this.config = config;
        final Iterator it = commits.iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            this.commits.add(commit);
        }
    }

    public Element toFile() {
        Element lg = null;
        if (!this.commits.isEmpty()) {
            lg = new CommitListXml(this.commits, getTags(), true).renderCommitList(this.commits);
        }
        return lg;
    }

    private List getTags() {
        final List tags = new ArrayList();
        final Calendar calendar = new GregorianCalendar();
        final Iterator it = this.config.getRepository().getSymbolicNames().iterator();
        while (it.hasNext()) {
            final SymbolicName tag = (SymbolicName) it.next();
            calendar.setTime(tag.getDate());
        }
        Collections.reverse(tags);
        return tags;
    }
}

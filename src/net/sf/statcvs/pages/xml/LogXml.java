package net.sf.statcvs.pages.xml;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: LogXml.java,v 1.3 2008/04/02 11:52:02 benoitx Exp $
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.ReportConfig;

import org.jdom.Element;

public class LogXml {
    private final ReportConfig config;
    private final Repository repository;

    /**
     * @param config Configuration and data for the report suite
     */
    public LogXml(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    /**
     * returns jdom element which contains data extracted from the commit list of the repository
     *
     * @returns Element
     */
    public Element toFile() {
        if (this.repository.getCommits().isEmpty()) {
            return null;
        }
        final Date start = this.repository.getFirstDate();
        final Date end = this.repository.getLastDate();
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(end);
        final Calendar startCal = new GregorianCalendar();
        startCal.setTime(start);
        final Element commLog = new LogXmlMaker(this.config, this.repository.getCommits()).toFile();
        commLog.setAttribute("no_of_commits", Integer.toString(this.repository.getCommits().size()));
        commLog.setAttribute("active_developers", countActiveDevelopers());
        return commLog;
    }

    /**
     * returns the number of active developers as a string
     *
     * @returns String
     */

    private String countActiveDevelopers() {
        final Set developers = new HashSet();
        final Iterator it = this.repository.getCommits().iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            developers.add(commit.getAuthor());
        }
        return Integer.toString(developers.size());
    }
}

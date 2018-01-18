package net.sf.statcvs.pages.xml;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: RevisedFilesXml.java,v 1.2 2008/04/02 11:22:16 benoitx Exp $
 *
 * This is the class which generates the per file information of the xml report
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.output.ReportConfig;

import org.jdom.Element;

public class RevisedFilesXml {
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

    private final Repository repo;
    private final List commits = new ArrayList();

    /**
     * Creates a new LogPageMaker.
     * @param year The log page's year
     * @param month The log page's month (0 for January)
     * @param commits A list of commits; those not in the
     * 		right month will be ignored
     * @param outputFormat
     */
    public RevisedFilesXml(final ReportConfig config) {
        this.repo = config.getRepository();
        final Iterator it = this.repo.getCommits().iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            this.commits.add(commit);
        }
        Collections.reverse(this.commits);
    }

    public Element toFile() throws NoSuchElementException {
        Set files = null;
        final Hashtable coms = new Hashtable();
        final Iterator commitIt = this.commits.iterator();
        Commit nextCommit = commitIt.hasNext() ? (Commit) commitIt.next() : null;
        final ArrayList al = new ArrayList();
        /*Iterator tagIt = this.tags.iterator();
        SymbolicName nextTag = tagIt.hasNext() ? (SymbolicName) tagIt.next() : null;*/
        while (nextCommit != null) {
            files = nextCommit.getAffectedFiles();
            final Iterator it = files.iterator();
            final String auth = nextCommit.getAuthor().toString();
            while (it.hasNext()) {
                final String path = it.next().toString();
                if (!(coms.containsKey(path))) {
                    al.add(path);
                    final ArrayList a = new ArrayList();
                    a.add(auth);
                    coms.put(path, a);
                } else {
                    final ArrayList alist = (ArrayList) coms.get(path);
                    final Iterator iter = alist.iterator();
                    final ArrayList l = new ArrayList();
                    for (int i = 0; i < alist.size(); i++) {
                        l.add(iter.next().toString());
                    }
                    l.add(auth);
                    coms.remove(path);
                    coms.put(path, l);
                }

            }
            nextCommit = commitIt.hasNext() ? (Commit) commitIt.next() : null;

        }
        final Element lg = new Element(XmlTags.TAG_REVISED_FILES);
        final Iterator it = al.iterator();
        while (it.hasNext()) {
            final Element file = new Element(XmlTags.TAG_FILE);
            final Element path = new Element(XmlTags.TAG_PATH);
            final String str = it.next().toString();
            path.setText(str);
            final ArrayList a = (ArrayList) coms.get(str);
            final Iterator itr = a.iterator();
            file.addContent(path);
            while (itr.hasNext()) {
                final String s = itr.next().toString();
                final Element auth = new Element(XmlTags.TAG_AUTHOR);
                auth.setText(s);
                file.addContent(auth);
            }
            lg.addContent(file);
        }
        return lg;
    }
}

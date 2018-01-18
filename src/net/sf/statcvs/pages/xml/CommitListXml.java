package net.sf.statcvs.pages.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.renderer.FileCollectionFormatter;

import org.jdom.CDATA;
import org.jdom.Element;

/**
 * This is a mere copy of the CommitListFormatter class with dfew minor changes done
 * Class for formatting a list of commits as XML.
 *
 * @author Anja Jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: CommitListXml.java,v 1.4 2008/04/02 11:52:02 benoitx Exp $
 */
public class CommitListXml {
    private final List commits;
    private final List tags;
    private final HashMap commitHashMap = new HashMap();

    /**
     * Creates a new instance for the list of commits.
     * @param commits A list of {@link Commit} objects
     */
    public CommitListXml(final List commits, final List tags, final boolean withPermalinks) {
        this(commits, tags, Integer.MAX_VALUE, withPermalinks);
    }

    /**
     * Creates a new instance for the list of commits.
     * @param commits A list of {@link Commit} objects
     * @param max maximum number of commits for the log; if there
     * are more, only the most recent will be used
     */
    public CommitListXml(final List commit, final List tags, final int max, final boolean withPermalinks) {
        this.commits = new ArrayList(commit);
        this.tags = tags;
        Collections.reverse(this.commits);
    }

    public Element renderCommitList(final List commitList) {
        if (commitList.isEmpty()) {
            return null;
        }
        int id = commitList.size();
        final Element lg = new Element(XmlTags.TAG_COMMIT_LIST);
        final Iterator commitIt = commitList.iterator();
        Commit nextCommit = commitIt.hasNext() ? (Commit) commitIt.next() : null;
        final Iterator tagIt = this.tags.iterator();
        SymbolicName nextTag = tagIt.hasNext() ? (SymbolicName) tagIt.next() : null;
        while (nextCommit != null) {
            Element log = null;
            if (nextTag == null || nextCommit.getDate().getTime() > nextTag.getDate().getTime()) {
                log = renderCommit(nextCommit, id);
                nextCommit = commitIt.hasNext() ? (Commit) commitIt.next() : null;
                id--;
            } else {
                nextTag = tagIt.hasNext() ? (SymbolicName) tagIt.next() : null;
            }
            lg.addContent(log);
        }
        return lg;
    }

    private Element renderCommit(final Commit commit, final int id) {
        final Element cmt = new Element(XmlTags.TAG_COMMIT);

        cmt.setAttribute("date", getDate(commit));
        cmt.setAttribute("author", getAuthor(commit));
        cmt.setAttribute("loc_change", getLinesOfCode(commit));
        final String revision = getRevisionNumber(commit);
        if (revision != null) {
            cmt.setAttribute("revision", revision);
        }
        final Element comment = new Element(XmlTags.TAG_COMMENT);
        final CDATA theActualComment = new CDATA(commit.getComment());
        comment.addContent(theActualComment);
        cmt.addContent(comment);
        cmt.addContent(getAffectedFiles(commit));

        return cmt;
    }

    private String getRevisionNumber(final Commit commit) {
        final Set rev = new HashSet();
        for (final Iterator it = commit.getRevisions().iterator(); it.hasNext();) {
            rev.add(((Revision) it.next()).getRevisionNumber());
        }
        if (rev.size() == 1) {
            return (String) rev.iterator().next();
        } else {
            return null;
        }
    }

    private String getDate(final Commit commit) {
        return XML.getDateAndTime(commit.getDate());
    }

    private String getAuthor(final Commit commit) {
        return XML.getAuthorLink(commit.getAuthor());
    }

    private String getLinesOfCode(final Commit commit) {
        final Iterator it = commit.getRevisions().iterator();
        int locSum = 0;
        while (it.hasNext()) {
            final Revision each = (Revision) it.next();
            locSum += each.getNewLines();
            saveRevision(each);
        }
        return Integer.toString(locSum);
    }

    private void saveRevision(final Revision revision) {
        commitHashMap.put(revision.getFile().getFilenameWithPath(), revision);
    }

    private Element getAffectedFiles(final Commit commit) {
        final Element result = new Element(XmlTags.TAG_FILES_AFFECTED);
        final FileCollectionFormatter formatter = new FileCollectionFormatter(commit.getAffectedFiles());
        final Iterator it = formatter.getDirectories().iterator();
        while (it.hasNext()) {
            final String directory = (String) it.next();
            final Iterator files = formatter.getFiles(directory).iterator();
            final StringBuffer fileList = new StringBuffer();
            while (files.hasNext()) {
                final Element File = new Element(XmlTags.TAG_FILE);
                if (fileList.length() > 0) {
                    fileList.append(",\n");
                }
                final String file = (String) files.next();
                final Element path = new Element(XmlTags.TAG_PATH).setText(directory + file);
                File.addContent(path);
                final Revision revision = (Revision) commitHashMap.get(directory + file);
                if (revision.isInitialRevision()) {
                    final int linesAdded = revision.getLines();
                    File.setAttribute("action", "new");
                    if (linesAdded > 0) {
                        File.addContent(new Element(XmlTags.TAG_LOC_ADDED).setText(Integer.toString(linesAdded)));
                    }
                } else if (revision.isDead()) {
                    File.setAttribute("action", "deleted");
                } else {
                    final int delta = revision.getLinesDelta();
                    final int linesAdded = revision.getReplacedLines() + ((delta > 0) ? delta : 0);
                    final int linesRemoved = revision.getReplacedLines() - ((delta < 0) ? delta : 0);
                    if (linesAdded > 0) {
                        File.setAttribute("action", "changed");
                        File.addContent(new Element(XmlTags.TAG_LOC_ADDED).setText(Integer.toString(linesAdded)));
                        if (linesRemoved > 0) {
                            File.addContent(new Element(XmlTags.TAG_LOC_REMOVED).setText(Integer.toString(linesRemoved)));
                        }
                    } else if (linesRemoved > 0) {
                        File.setAttribute("action", "changed");
                        File.addContent(new Element(XmlTags.TAG_LOC_ADDED).setText("0"));
                        File.addContent(new Element(XmlTags.TAG_LOC_REMOVED).setText(Integer.toString(linesRemoved)));
                    } else { // linesAdded == linesRemoved == 0
                        // should be binary file or keyword subst change
                        File.setAttribute("action", "binary file or keyword subst change");
                    }
                }
                result.addContent(File);
            }
        }
        return result;
    }
}

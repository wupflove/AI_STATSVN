package net.sf.statcvs.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.renderer.FileCollectionFormatter;

/**
 * Class for formatting a list of commits as HTML.
 *
 * @author Anja Jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: CommitListFormatter.java,v 1.17 2009/08/22 10:30:42 benoitx Exp $
 */
public class CommitListFormatter {
    private final ReportConfig config;
    private final List commits;
    private final List tags;
    private final int max;
    private final boolean withPermalinks;
    private final HashMap commitHashMap = new HashMap();
    
    private static final Logger logger = LogManager.getLogger();

    /**
     * Creates a new instance for the list of commits.
     * @param commits A list of {@link Commit} objects
     */
    public CommitListFormatter(final ReportConfig config, final List commits, final List tags, final boolean withPermalinks) {
        this(config, commits, tags, Integer.MAX_VALUE, withPermalinks);
    }

    /**
     * Creates a new instance for the list of commits.
     * @param commits A list of {@link Commit} objects
     * @param max maximum number of commits for the log; if there
     * are more, only the most recent will be used
     */
    public CommitListFormatter(final ReportConfig config, final List commit, final List tags, final int max, final boolean withPermalinks) {
        this.config = config;
        this.commits = new ArrayList(commit);
        this.tags = tags;
        this.max = max;
        this.withPermalinks = withPermalinks;
        Collections.reverse(this.commits);
    }

    /**
     * Returns HTML code for the commit log without splitting the list
     * into pages.
     *
     * @return HTML code for the commit log
     */
    public String render() {
        if (this.commits.size() > this.max) {
            final List recentCommits = this.commits.subList(0, this.max);
            return renderCommitList(recentCommits) + "<p>(" + (this.commits.size() - this.max) + " " + Messages.getString("MORE_COMMITS") + ")</p>\n";
        }
        return renderCommitList(this.commits);
    }

    private String renderCommitList(final List commitList) {
        if (commitList.isEmpty()) {
            return "<p>No commits</p>\n";
        }
        logger.debug(commitList.size());
        int id = commitList.size();
        final StringBuffer result = new StringBuffer("<dl class=\"commitlist\">\n");
        final Iterator commitIt = commitList.iterator();
        Commit nextCommit = commitIt.hasNext() ? (Commit) commitIt.next() : null;
        final Iterator tagIt = this.tags.iterator();
        SymbolicName nextTag = tagIt.hasNext() ? (SymbolicName) tagIt.next() : null;
        while (nextCommit != null) {
            if (nextTag == null || nextCommit.getDate().getTime() > nextTag.getDate().getTime()) {
                result.append(renderCommit(nextCommit, id));
                nextCommit = commitIt.hasNext() ? (Commit) commitIt.next() : null;
                id--;
            } else {
                renderTag(result, nextTag);
                nextTag = tagIt.hasNext() ? (SymbolicName) tagIt.next() : null;
            }
        }
        result.append("</dl>\n\n");
        return result.toString();
    }

    private void renderTag(final StringBuffer s, final SymbolicName tag) {
        final String anchor = HTML.escape(CommitLogPageMaker.getAnchor(tag));
        s.append("  <dt class=\"tag\"><a name=\"").append(anchor).append("\">\n");
        s.append("    Repository Tag: ").append(HTML.escape(tag.getName())).append("</a>\n");
        s.append("  </dt>\n");
    }

    private String renderCommit(final Commit commit, final int id) {
        final StringBuffer result = new StringBuffer();
        result.append("  <dt><a name=\"" + id + "\"></a>\n");
        result.append("    ").append(getAuthor(commit)).append("\n");
        result.append("    ").append(getDate(commit)).append("\n");
        if (this.withPermalinks) {
            result.append("    ").append(getPermalink(id)).append("\n");
        }
        final String revisionNumber = getRevisionNumber(commit);
        if (revisionNumber != null) {
            result.append("    ").append(revisionNumber).append("\n");
        }
        result.append("  </dt>\n");
        result.append("  <dd>\n");
        result.append("    <p class=\"comment\">\n");
        result.append(getComment(commit)).append("\n");
        result.append("    </p>\n");
        result.append("    <p class=\"commitdetails\"><strong>");
        result.append(getLinesOfCode(commit)).append("</strong> ");
        result.append("lines of code changed in ");
        result.append(getAffectedFilesCount(commit));
        result.append(":</p>\n");
        result.append(getAffectedFiles(commit)).append("  </dd>\n\n");
        return result.toString();
    }

    private String getPermalink(final int id) {
        return "<a class=\"permalink\" title=\"Permalink to this commit\" href=\"#" + id + "\">#" + id + "</a>";
    }

    private String getRevisionNumber(final Commit commit) {
        final Set rev = new HashSet();
        for (final Iterator it = commit.getRevisions().iterator(); it.hasNext();) {
            rev.add(((Revision) it.next()).getRevisionNumber());
        }
        if (rev.size() == 1) {
            return HTML.getRevisionNumber((String) rev.iterator().next());
        } else {
            return null;
        }
    }

    private String getDate(final Commit commit) {
        return HTML.getDateAndTime(commit.getDate());
    }

    private String getAuthor(final Commit commit) {
        return HTML.getAuthorLink(commit.getAuthor());
    }

    private String getAffectedFilesCount(final Commit commit) {
        return HTML.getAffectedFilesCount(commit.getRevisions());
    }

    private String getComment(final Commit commit) {
        return this.config.getWebBugtracker().toHTMLWithLinks(commit.getComment());
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

    private String getAffectedFiles(final Commit commit) {
        final StringBuffer result = new StringBuffer("    <ul class=\"commitdetails\">\n");
        final FileCollectionFormatter formatter = new FileCollectionFormatter(commit.getAffectedFiles());
        final Iterator it = formatter.getDirectories().iterator();
        while (it.hasNext()) {
            result.append("      <li>\n");
            final String directory = (String) it.next();
            if (!directory.equals("")) {
                result.append("        <strong>").append(directory.substring(0, directory.length() - 1)).append("</strong>:\n");
            }
            final Iterator files = formatter.getFiles(directory).iterator();
            final StringBuffer fileList = new StringBuffer();
            while (files.hasNext()) {
                if (fileList.length() > 0) {
                    fileList.append(",\n");
                }
                fileList.append("        ");
                final String file = (String) files.next();
                final Revision revision = (Revision) commitHashMap.get(directory + file);
                final WebRepositoryIntegration webRepository = this.config.getWebRepository();
                if (webRepository != null) {
                    final Revision previous = revision.getPreviousRevision();
                    String url;
                    if (previous == null || revision.isInitialRevision()) {
                        url = webRepository.getFileViewUrl(revision);
                    } else if (revision.isDead()) {
                        url = webRepository.getFileViewUrl(previous);
                    } else {
                        url = webRepository.getDiffUrl(previous, revision);
                    }
                    fileList.append("<a href=\"").append(HTML.escapeUrl(url)).append("\" class=\"webrepository\">").append(HTML.escape(file)).append("</a>");
                } else {
                    fileList.append(file);
                }
                if (revision.isInitialRevision()) {
                    final int linesAdded = revision.getLines();
                    fileList.append("&#160;<span class=\"new\">(new");
                    if (linesAdded > 0) {
                        fileList.append("&#160;").append(linesAdded);
                    }
                    fileList.append(")</span>");
                } else if (revision.isDead()) {
                    fileList.append("&#160;<span class=\"del\">(del)</span>");
                } else {
                    final int delta = revision.getLinesDelta();
                    final int linesAdded = revision.getReplacedLines() + ((delta > 0) ? delta : 0);
                    final int linesRemoved = revision.getReplacedLines() - ((delta < 0) ? delta : 0);
                    fileList.append("&#160;<span class=\"change\">(");
                    if (linesAdded > 0) {
                        fileList.append("+").append(linesAdded);
                        if (linesRemoved > 0) {
                            fileList.append("&#160;-").append(linesRemoved);
                        }
                    } else if (linesRemoved > 0) {
                        fileList.append("-").append(linesRemoved);
                    } else { // linesAdded == linesRemoved == 0
                        // should be binary file or keyword subst change
                        fileList.append("changed");
                    }
                    fileList.append(")</span>");
                }
            }
            result.append(fileList.toString()).append("\n      </li>\n");
        }
        result.append("    </ul>\n");
        return result.toString();
    }
}

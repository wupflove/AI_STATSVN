package net.sf.statcvs.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.LOCChartMaker.DirectoryLOCChartMaker;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.reports.AuthorsForDirectoryTableReport;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DirectoryPageMaker.java,v 1.10 2009/05/08 13:18:48 benoitx Exp $
 */
public class DirectoryPageMaker {
    private final static int RECENT_COMMITS_LENGTH = 40;

    public static String getURL(final Directory directory) {
        return getFilename(directory) + ".html";
    }

    private static String getFilename(final Directory directory) {
        return "dir" + HTML.escapeDirectoryName(directory.getPath());
    }

    private final ReportConfig config;
    private final Repository repository;
    private final Directory directory;

    public DirectoryPageMaker(final ReportConfig config, final Directory directory) {
        this.config = config;
        this.repository = config.getRepository();
        this.directory = directory;
    }

    public Page toFile() {
        final ChartImage chart = new DirectoryLOCChartMaker(this.config, this.directory).toFile();

        final String title = "Directory " + (this.directory.isRoot() ? "[root]" : this.directory.getPath());
        final Page page = this.config.createPage(getFilename(this.directory), title, title);
        if (!this.directory.getRevisions().isEmpty() && isInitiallyEmpty()) {
            page.addAttribute("Directory Created", ((Revision) this.directory.getRevisions().first()).getDate());
        }
        if (!this.directory.getRevisions().isEmpty() && getCurrentFileCount() == 0) {
            page.addAttribute("Directory Deleted", ((Revision) this.directory.getRevisions().last()).getDate());
        }
        page.addAttribute("Total Files", getCurrentFileCount());
        page.addAttribute("Deleted Files", getDeadFileCount());
        page.addAttribute("Lines of Code", getCurrentLOC());
        if (this.config.getWebRepository() != null) {
            final WebRepositoryIntegration rep = this.config.getWebRepository();
            final String text = Messages.getString("BROWSE_WEB_REPOSITORY") + " " + rep.getName();
            page.addLink(rep.getDirectoryUrl(this.directory), text);
        }
        page.add(this.directory, true);
        ;
        if (chart != null) {
            page.addSection(Messages.getString("LOC_TITLE"));
            page.add(chart);
        }
        if (!this.directory.getRevisions().isEmpty()) {
            page.addSection("Developers");
            page.add(new AuthorsForDirectoryTableReport(this.config, this.directory));
        }
        final List dirCommits = getCommitsInDirectory();
        if (!dirCommits.isEmpty()) {
            page.addSection(Messages.getString("MOST_RECENT_COMMITS"));
            final CommitListFormatter renderer = new CommitListFormatter(this.config, dirCommits, Collections.EMPTY_LIST, RECENT_COMMITS_LENGTH, false);
            page.addRawContent(renderer.render());
        }
        return page;
    }

    private Commit getCommit(final Revision rev) {
        final Iterator it = this.repository.getCommits().iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            if (commit.getRevisions().contains(rev)) {
                return commit;
            }
        }
        return null;
    }

    private List getCommitsInDirectory() {
        final Map commitsToFilteredCommits = new HashMap();
        final Iterator it = this.directory.getRevisions().iterator();
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            final Commit commit = getCommit(rev);
            if (commit == null) {
                continue;
            }
            if (commitsToFilteredCommits.containsKey(commit)) {
                final Commit filteredCommit = (Commit) commitsToFilteredCommits.get(commit);
                filteredCommit.addRevision(rev);
            } else {
                final Commit filteredCommit = new Commit(rev);
                commitsToFilteredCommits.put(commit, filteredCommit);
            }
        }
        final List commits = new ArrayList(commitsToFilteredCommits.values());
        Collections.sort(commits);
        return commits;
    }

    private int getCurrentLOC() {
        int result = 0;
        final Iterator it = this.directory.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            result += file.getCurrentLinesOfCode();
        }
        return result;
    }

    private int getCurrentFileCount() {
        int result = 0;
        final Iterator it = this.directory.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                result++;
            }
        }
        return result;
    }

    private int getDeadFileCount() {
        int result = 0;
        final Iterator it = this.directory.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (file.isDead()) {
                result++;
            }
        }
        return result;
    }

    private boolean isInitiallyEmpty() {
        final Iterator it = this.directory.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.getInitialRevision().isInitialRevision()) {
                return false;
            }
        }
        return true;
    }
}
package net.sf.statcvs.pages;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.LOCChartMaker.MainLOCChartMaker;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.CloudCommitTableReport;
import net.sf.statcvs.reports.TagReport;
import net.sf.statcvs.reports.TopDevelopersTableReport;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: IndexPageMaker.java,v 1.12 2009/06/02 13:28:53 benoitx Exp $
 */
public class IndexPageMaker {
    private final ReportConfig config;
    private final Repository repository;
    private final String notesHTML;
    private final PageGroup reports;
    private Page page;

    /**
     * Creates a new report index page.
     * @param config Report configuration to use
     * @param notesHTML A note to be inserted on top of the page; might be <tt>null</tt>
     * @param reports A list of {@link NavigationNode}s that constitute the page's main menu
     */
    public IndexPageMaker(final ReportConfig config, final String notesHTML, final PageGroup reports) {
        this.config = config;
        this.repository = config.getRepository();
        this.notesHTML = notesHTML;
        this.reports = reports;
        final String title = Messages.getString("INDEX_TITLE") + " " + this.config.getProjectName();
        page = this.config.createPage("index", title, title);
        page.addChild(this.reports);
    }
    
    

    public Page toFile() {
        final ChartImage chart = new MainLOCChartMaker("loc_small", this.config, "loc_small.png", this.config.getSmallChartSize()).toFile();

        final TopDevelopersTableReport topDevelopers = new TopDevelopersTableReport(this.config);
        final CloudCommitTableReport cloud = new CloudCommitTableReport(this.config);

        final Date date = Calendar.getInstance().getTime();
        page.addAttribute("Generated", date);
        final String headRevisionNumber = getHeadRevisionNumber();
        // Quick and dirty-ish trick: a revision for CVS contains "." and it does not make sense to
        // display a "head revision" for CVS.
        if (headRevisionNumber != null && headRevisionNumber.indexOf('.') < 0) {
            page.addAttribute("Head revision", headRevisionNumber);
        }
        page.addRawAttribute("Report Period", getReportPeriod());
        page.addAttribute("Total Files", getCurrentFileCount());
        page.addAttribute("Total Lines of Code", this.repository.getCurrentLOC());
        page.addAttribute("Developers", topDevelopers.getDeveloperCount());
        if (ConfigurationOptions.isEnableTwitterButton()) {
            page.addRawAttribute("Tweet this", TwitterHelp.buildOverviewLink(topDevelopers, repository, config));
        }
        if (this.notesHTML != null) {
            page.addRawContent(this.notesHTML);
        }
        page.addRawContent(reports.asLinkList());
//        page.add(this.reports);
        if (chart != null) {
            page.addSection(Messages.getString("LOC_TITLE"));
            page.add(chart, "loc.html");
        }
        if (topDevelopers.getDeveloperCount() > 1) {
            if (topDevelopers.getDeveloperCount() > 10) {
                page.addSection(Messages.getString("SECTION_TOP_AUTHORS"));
            } else {
                page.addSection("Developers");
            }
            page.add(topDevelopers);
            page.addRawContent(HTML.getLink("developers.html", Messages.getString("NAVIGATION_MORE")));
        }

        page.addSection(Messages.getString("CLOUD_SECTION_TITLE"));
        cloud.calculate();
        page.addRawContent(cloud.getRawContent());
        page.addRawContent(HTML.getLink("cloud.html", Messages.getString("NAVIGATION_MORE")));
        if (!this.repository.getSymbolicNames().isEmpty()) {
            page.addSection("Repository Tags");
            page.add(new TagReport(this.config));
        }
        page.addSection("Directories");
        page.add(this.repository.getRoot(), false);
        return page;
    }

    private String getReportPeriod() {
        return HTML.getDate(this.repository.getFirstDate()) + " to " + HTML.getDate(this.repository.getLastDate());
    }

    private int getCurrentFileCount() {
        int result = 0;
        final Iterator it = this.repository.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                result++;
            }
        }
        return result;
    }

    private String getHeadRevisionNumber() {
        final Revision headRevision = (Revision) (this.repository.getRevisions().last());

        if (headRevision != null) {
            return headRevision.getRevisionNumber();
        } else {
            return null;
        }
    }

    public Page getPage() {
        return page;
    }
}

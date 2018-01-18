package net.sf.statcvs.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.CommitScatterChartMaker;
import net.sf.statcvs.charts.ModifyAddChartMaker;
import net.sf.statcvs.charts.LOCChartMaker.AllDevelopersLOCChartMaker;
import net.sf.statcvs.charts.TimeBarChartMaker.HourBarChartMaker;
import net.sf.statcvs.charts.TimeBarChartMaker.WeekdayBarChartMaker;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.DevelopersOfTheMonthTable;
import net.sf.statcvs.reports.DevelopersRollingTableReport;
import net.sf.statcvs.reports.DevelopersTableReport;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: AllDevelopersPageMaker.java,v 1.15 2009/06/02 13:28:53 benoitx Exp $
 */
public class AllDevelopersPageMaker {
    private final ReportConfig config;

    public AllDevelopersPageMaker(final ReportConfig config) {
        this.config = config;
    }

    public NavigationNode toFile() {
        final DevelopersTableReport developers = new DevelopersTableReport(this.config);
        final DevelopersRollingTableReport rollingDevelopers = new DevelopersRollingTableReport(this.config);

        final Page page = this.config.createPage("developers", Messages.getString("DEVELOPERS"), this.config.getProjectName() + " "
                + Messages.getString("DEVELOPERS"));
        if (developers.getDeveloperCount() > 1) {
            page.addAttribute(Messages.getString("NUMBER_DEVELOPERS"), developers.getDeveloperCount());
            page.add(developers);
            page.addSection(Messages.getString("ROLLING_DEV_TITLE"));
            page.add(rollingDevelopers);
            page.addRawContent(getOtherLoginsLinks());
            page.addSection(Messages.getString("LOC_TITLE"));
            final ChartImage allAuthorsLOCChart = new AllDevelopersLOCChartMaker(this.config, this.config.getLargeChartSize()).toFile();
            page.add(allAuthorsLOCChart);
        }

        final ChartImage hoursChart = new HourBarChartMaker("activity_time", this.config, this.config.getRepository().getRevisions(), Messages
                .getString("ACTIVITY_TIME_TITLE"), "activity_time.png").toFile();
        final ChartImage weekdaysChart = new WeekdayBarChartMaker("activity_day", this.config, this.config.getRepository().getRevisions(), Messages
                .getString("ACTIVITY_DAY_TITLE"), "activity_day.png").toFile();
        final ChartImage scatterChart = new CommitScatterChartMaker(this.config, this.config.getLargeChartSize().width).toFile();
        final ChartImage modifyAddChart = new ModifyAddChartMaker(this.config, this.config.getSmallChartSize().width).toFile();

        final DevelopersOfTheMonthTable developerOfTheMonth = new DevelopersOfTheMonthTable(this.config);
        page.addSection(Messages.getString("DEVELOPER_OF_THE_MONTH"));
        page.add(developerOfTheMonth);

        page.addSection(Messages.getString("DEVELOPER_ACTIVITY"));
        page.add(scatterChart);
        page.add(modifyAddChart);

        page.addSection(Messages.getString("ACTIVITY_TITLE"));
        page.add(hoursChart);
        page.add(weekdaysChart);

        if (this.config.getRepository().getAuthors().size() >= 1) {
            final PageGroup developerPages = new PageGroup(Messages.getString("DEVELOPERS"), false);
            final Iterator it = this.config.getRepository().getAuthors().iterator();
            while (it.hasNext()) {
                final Author developer = (Author) it.next();
                Page devPage = new DeveloperPageMaker(this.config, developer).toFile();
                developerPages.add(devPage);
            }
            page.addChild(developerPages);
        }

        return page;
    }

    private String getOtherLoginsLinks() {
        final List nonDeveloperLogins = new ArrayList();
        Iterator it = this.config.getRepository().getAuthors().iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            if (!this.config.isDeveloper(author)) {
                nonDeveloperLogins.add(author);
            }
        }
        if (nonDeveloperLogins.isEmpty()) {
            return "";
        }
        Collections.sort(nonDeveloperLogins);
        final StringBuffer s = new StringBuffer("<p>\n  Other Logins:\n  ");
        it = nonDeveloperLogins.iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            s.append(HTML.getLink(DeveloperPageMaker.getURL(author), author.getRealName()));
            if (it.hasNext()) {
                s.append(", \n  ");
            }
        }
        s.append("</p>\n");
        return s.toString();
    }
}

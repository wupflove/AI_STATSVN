package net.sf.statcvs.pages;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jpf.statsvn.util.StringUtils;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.DirectoryPieChartMaker.CodeDistributionChartMaker;
import net.sf.statcvs.charts.TimeBarChartMaker.HourBarChartMaker;
import net.sf.statcvs.charts.TimeBarChartMaker.WeekdayBarChartMaker;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.DirectoriesForAuthorTableReport;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DeveloperPageMaker.java,v 1.18 2009/08/20 08:57:00 benoitx Exp $
 */
public class DeveloperPageMaker {
    private final static int RECENT_COMMITS_LENGTH = 20;
    private final static NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();

    static {
        PERCENT_FORMAT.setMinimumFractionDigits(1);
        PERCENT_FORMAT.setMaximumFractionDigits(1);
    }

    public static String getURL(final Author developer) {
        return getFilename(developer) + ".html";
    }

    private static String getFilename(final Author developer) {
        return "user_" + HTML.escapeAuthorName(developer.getName());
    }

    private final ReportConfig config;
    private final Author developer;
    private final Repository repository;

    public DeveloperPageMaker(final ReportConfig config, final Author developer) {
        this.config = config;
        this.developer = developer;
        this.repository = config.getRepository();
    }

    public Page toFile() {
        final ChartImage hourChart = new HourBarChartMaker("activity_time", this.config, this.developer.getRevisions(), Messages
                .getString("ACTIVITY_TIME_FOR_AUTHOR_TITLE")
                + " " + this.developer.getRealName(), "activity_time_" + HTML.escapeAuthorName(this.developer.getName()) + ".png").toFile();
        final ChartImage weekdayChart = new WeekdayBarChartMaker("activity_day", this.config, this.developer.getRevisions(), Messages
                .getString("ACTIVITY_DAY_FOR_AUTHOR_TITLE")
                + " " + this.developer.getRealName(), "activity_day_" + HTML.escapeAuthorName(this.developer.getName()) + ".png").toFile();
        final ChartImage codeDistributionChart = new CodeDistributionChartMaker(this.config, this.developer).toFile();

        String title;
        if (this.config.isDeveloper(this.developer)) {
            title = this.config.getProjectName() + " Developers: " + this.developer.getRealName();
        } else {
            title = "Non-developer Login: " + this.developer.getRealName();
        }
        final Page page = this.config.createPage(getFilename(this.developer), this.developer.getRealName(), title);
        page.addAttribute("Login name", this.developer.getName());
        if (this.developer.getRealName() != null && !this.developer.getRealName().equals(this.developer.getName())) {
            page.addAttribute("Real name", this.developer.getRealName());
        }
        if (StringUtils.isNotEmpty(this.developer.getTwitterUserName())) {
            page.addRawAttribute("Twitter", "@<a href=\"http://twitter.com/" + this.developer.getTwitterUserName() + "\">"
                    + this.developer.getTwitterUserName() + "</a>");
        }
        if (StringUtils.isNotEmpty(this.developer.getEmail())) {
            page.addRawAttribute("Email", "<a href=\"mailto:" + this.developer.getEmail() + "\">" + this.developer.getEmail() + "</a>");
        }
        if (StringUtils.isNotEmpty(this.developer.getHomePageUrl())) {
            page.addRawAttribute("Home Page", "<a href=\"" + this.developer.getHomePageUrl() + "\">" + this.developer.getHomePageUrl() + "</a>");
        }
        if (StringUtils.isNotEmpty(this.developer.getImageUrl())) {
            page.addRawAttribute("Image", "<img src=\"" + this.developer.getImageUrl() + "\" alt=\"" + this.developer.getRealName() + "\"/>");
        }
        
        page.addAttribute("Total Commits", getNumberAndPercentage(this.developer.getRevisions().size(), this.repository.getRevisions().size()));
        String loc = getNumberAndPercentage(countContributedLines(this.developer.getRevisions()), countContributedLines(this.repository.getRevisions()));
        page.addAttribute("Lines of Code", loc);
        page.addAttribute("Most Recent Commit", ((Revision) this.developer.getRevisions().last()).getDate());
        if (ConfigurationOptions.isEnableTwitterButton()) {
            page.addRawAttribute("Tweet this", TwitterHelp.buildDeveloperLink(developer, loc, repository, config));
        }
        page.addSection(Messages.getString("ACTIVITY_TITLE"));
        page.add(hourChart);
        page.add(weekdayChart);

        if (StringUtils.isNotEmpty(this.developer.getTwitterUserName()) && this.developer.isTwitterIncludeHtml()) {
            page.addSection("Twitter");
            page.addRawContent("<div id=\"twitter_div\">");
            page.addRawContent("<ul id=\"twitter_update_list\"/>");
            page.addRawContent("<a href=\"http://twitter.com/" + developer.getTwitterUserName()
                    + "\" id=\"twitter-link\" style=\"display:block;text-align:right;\">follow me on Twitter</a>");
            page.addRawContent("</div>");
            page.addRawContent("<script type=\"text/javascript\" src=\"http://twitter.com/javascripts/blogger.js\"></script>");
            page.addRawContent("<script type=\"text/javascript\" src=\"http://twitter.com/statuses/user_timeline/" + developer.getTwitterUserName()
                    + ".json?callback=twitterCallback2&amp;count=5\"></script>");
        }
        if (StringUtils.isNotEmpty(this.developer.getTwitterUserId()) && this.developer.isTwitterIncludeFlash()) {
            page.addSection("Twitter");
            page
                    .addRawContent("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,124,0\" width=\"550\" height=\"350\" id=\"TwitterWidget\" align=\"middle\">");
            page.addRawContent("<param name=\"allowScriptAccess\" value=\"sameDomain\" />");
            page.addRawContent("<param name=\"allowFullScreen\" value=\"false\" />");
            page.addRawContent("<param name=\"movie\" value=\"http://static.twitter.com/flash/widgets/profile/TwitterWidget.swf\" />");
            page.addRawContent("<param name=\"quality\" value=\"high\" />");
            page.addRawContent("<param name=\"bgcolor\" value=\"#000000\" />");
            page.addRawContent("<param name=\"FlashVars\" value=\"userID=" + developer.getTwitterUserId()
                    + "&amp;styleURL=http://static.twitter.com/flash/widgets/profile/velvetica.xml\"/>");
            page
                    .addRawContent("<embed src=\"http://static.twitter.com/flash/widgets/profile/TwitterWidget.swf\" quality=\"high\" bgcolor=\"#000000\" width=\"550\" height=\"350\" name=\"TwitterWidget\" align=\"middle\" allowScriptAccess=\"sameDomain\" allowFullScreen=\"false\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" FlashVars=\"userID="
                            + developer.getTwitterUserId() + "&amp;styleURL=http://static.twitter.com/flash/widgets/profile/velvetica.xml\"/></object>");
        }

        page.addSection("Activity in Directories");
        page.add(new DirectoriesForAuthorTableReport(this.config, this.developer));
        if (codeDistributionChart != null) {
            page.add(codeDistributionChart);
        }
        page.addSection(Messages.getString("MOST_RECENT_COMMITS"));
        page.addRawContent(new CommitListFormatter(this.config, getRecentCommits(), Collections.EMPTY_LIST, RECENT_COMMITS_LENGTH, false).render());
        return page;
    }

    private List getRecentCommits() {
        final List results = new ArrayList();
        final Iterator it = this.repository.getCommits().iterator();
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            if (this.developer.equals(commit.getAuthor())) {
                results.add(commit);
            }
        }
        return results;
    }

    private int countContributedLines(final Collection revisions) {
        int result = 0;
        final Iterator it = revisions.iterator();
        while (it.hasNext()) {
            final Revision element = (Revision) it.next();
            result += element.getNewLines();
        }
        return result;
    }

    /**
     * returns the percentage of a given total count and the count.
     * This will work, because division by zero is not a problem with doubles:
     * you get NaN (and the formatter will format that too).
     * @author Jan Dockx
     * @param value
     * @param total
     * @return String percentage string
     */
    private String getNumberAndPercentage(final int value, final int total) {
        final double factor = (double) value / (double) total;
        return NumberFormat.getNumberInstance().format(value) + " (" + PERCENT_FORMAT.format(factor) + ")";
    }
}

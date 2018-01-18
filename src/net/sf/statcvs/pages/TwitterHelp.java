/**
 * 
 */
package net.sf.statcvs.pages;

import java.text.NumberFormat;

import org.jpf.statsvn.util.StringUtils;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.TopDevelopersTableReport;

/**
 * @author Benoit
 *
 */
public final class TwitterHelp {
    private TwitterHelp() {
    }

    public static String buildOverviewLink(final TopDevelopersTableReport topDevelopers, final Repository repository, final ReportConfig config) {
        final StringBuffer buf = new StringBuffer();
        startLink(buf);
        buf.append("http://twitter.com/home?status=Project");

        final StringBuffer url = new StringBuffer();
        url.append(" ");
        url.append(config.getProjectName());
        url.append(" {0} on ");
        url.append(HTML.OUTPUT_DATE_FORMAT.format(repository.getLastDate()));
        url.append(" has ");
        url.append(NumberFormat.getNumberInstance().format(repository.getCurrentLOC()));
        url.append(" Lines of Code and ");
        url.append(topDevelopers.getDeveloperCount());
        url.append(" Developers: stats by ");
        url.append(Messages.getString("PROJECT_SHORTNAME"));
        url.append(" ").append(Messages.getString("PROJECT_SMALL_URL"));
        
        buf.append(HTML.escapeUrlParameters(url.toString()));

        endLinkAndIcon(buf, ReportSuiteMaker.TWEET_THIS_ICON);
        return buf.toString();
    }

    private static void endLinkAndIcon(final StringBuffer buf, final String iconName) {
        buf.append("\"><img style=\"border:0px\" alt=\"Tweet this\" src=\"").append(iconName).append("\"/></a>");
    }

    private static void startLink(final StringBuffer buf) {
        buf.append("<a onclick=\"return shortenTweet(this.href);\" href=\"");
    }

    public static String buildDeveloperLink(final Author developer, final String loc, final Repository repository, final ReportConfig config) {
        final StringBuffer buf = new StringBuffer();
        startLink(buf);
        buf.append("http://twitter.com/home?status=");
        final StringBuffer url = new StringBuffer();
        if (StringUtils.isNotEmpty(developer.getTwitterUserName())) {
            url.append("@").append(developer.getTwitterUserName());
        } else if (StringUtils.isNotEmpty(developer.getRealName())) {
            url.append(developer.getRealName());
        } else {
            url.append(developer.getName());
        }
        url.append(" contributed ");
        url.append(loc);
        url.append(" lines to ");
        url.append(config.getProjectName());
        url.append(" {0} most recent commit ");
        url.append(HTML.OUTPUT_DATE_FORMAT.format(((Revision) developer.getRevisions().last()).getDate()));
        url.append(" stats by ");
        url.append(Messages.getString("PROJECT_SHORTNAME"));
        url.append(" ").append(Messages.getString("PROJECT_SMALL_URL"));

        //        System.out.println("Raw:"+url.toString());

        String escapeUrl = HTML.escapeUrlParameters(url.toString());
        //        System.out.println("url:"+escapeUrl);
        buf.append(escapeUrl);
        endLinkAndIcon(buf, ReportSuiteMaker.TWEET_THIS_ICON);
        return buf.toString();
    }

    public static String buildDeveloperOfMonthLink(final Author developer, final int loc, final Repository repository, final String month_year,
            ReportConfig config) {
        final StringBuffer buf = new StringBuffer();
        startLink(buf);
        final StringBuffer url = new StringBuffer();
        buf.append("http://twitter.com/home?status=");
        if (StringUtils.isNotEmpty(developer.getTwitterUserName())) {
            url.append("@").append(developer.getTwitterUserName());
        } else if (StringUtils.isNotEmpty(developer.getRealName())) {
            url.append(developer.getRealName());
        } else {
            url.append(developer.getName());
        }
        url.append(" is Developer of The Month for ");
        url.append(month_year);
        url.append(" for ");
        url.append(config.getProjectName());

        url.append(" {0} with ");
        url.append(NumberFormat.getNumberInstance().format(loc));
        url.append(" lines. Stats by ");
        url.append(Messages.getString("PROJECT_SHORTNAME"));
        url.append(" ").append(Messages.getString("PROJECT_SMALL_URL"));

        buf.append(HTML.escapeUrlParameters(url.toString()));

        endLinkAndIcon(buf, ReportSuiteMaker.TWEET_THIS_SMALL);
        return buf.toString();
    }
}

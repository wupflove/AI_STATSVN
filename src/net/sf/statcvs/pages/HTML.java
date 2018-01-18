package net.sf.statcvs.pages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;

/**
 * TODO: Can we turn this into an abstract base class of MarkupHTML and MarkupXDoc?
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: HTML.java,v 1.19 2009/08/05 16:32:10 benoitx Exp $
 */
public final class HTML {
    public static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat(Messages.getString("DATE_FORMAT"));
    public static final SimpleDateFormat OUTPUT_DATE_TIME_FORMAT = new SimpleDateFormat(Messages.getString("DATE_TIME_FORMAT"));
    private static final Pattern HTTP_REGEXP2 = Pattern
            .compile("\\b(http://|https://|www.)(\\w|\\d|\\.)+(\\.|/|\\w|\\d|%|;|&|=)*\\b", Pattern.CASE_INSENSITIVE);

    //    private final static Logger LOGGER = Logger.getLogger("sf.net.statcvs");

    /**
     * Creates a HTML representation of a hyperlink
     * @param link URL
     * @param linkName Name of the Link
     * @return String HTML code of the hyperlink
     */
    public static String getLink(final String link, final String linkName) {
        return getLink(link, linkName, "", "");
    }

    /**
     * Creates a HTML representation of a hyperlink
     * @param link URL
     * @param linkName Name of the Link
     * @param prefix A prefix to be inserted before the link label; no HTML escaping is performed
     * @param prefix A suffix to be inserted after the link label; no HTML escaping is performed
     * @return String HTML code of the hyperlink
     */
    public static String getLink(final String link, final String linkName, final String prefix, final String suffix) {
        if (link == null) {
            return prefix + escape(linkName) + suffix;
        }
        return "<a href=\"" + escape(link) + "\">" + prefix + escape(linkName) + suffix + "</a>";
    }

    /**
     * Returns HTML code for a link to an author page
     * @param author the author
     * @return HTML code for the link
     */
    public static String getAuthorLink(final Author author) {
        return "<a href=\"" + escape(DeveloperPageMaker.getURL(author)) + "\" class=\"author\">" + escape(author.getRealName()) + "</a>";
    }

    /**
     * Returns HTML code for a link to an author Id page
     * @param author the author
     * @return HTML code for the link
     */
    public static String getAuthorIdLink(final Author author) {
        return "<a href=\"" + escape(DeveloperPageMaker.getURL(author)) + "\" class=\"author\">" + escape(author.getName()) + "</a>";
    }

    /**
     * Returns HTML code for a date
     * @param date the date
     * @return HTML code for the date
     */
    public static String getDate(final Date date) {
        return "<span class=\"date\">" + OUTPUT_DATE_FORMAT.format(date) + "</span>";
    }

    /**
     * Returns HTML code for number of affected files
     * @param files affected files    
     * @return HTML code for number of affected files
     */
    public static String getAffectedFilesCount(final Set files) {
        final StringBuffer sb = new StringBuffer();
        sb.append("<span class=\"files\">" + files.size());
        if (files.size() <= 1) {
            sb.append(" file");
        } else {
            sb.append(" files");
        }
        sb.append("</span>");
        return sb.toString();
    }

    /**
     * Returns HTML code for a date, including time
     * @param date the date
     * @return HTML code for the date
     */
    public static String getDateAndTime(final Date date) {
        return "<span class=\"date\">" + OUTPUT_DATE_TIME_FORMAT.format(date) + "</span>";
    }

    /**
     * Returns HTML code for a revision number
     * @param revisionNumber    a revision number
     * @return  HTML code for a revision number
     */
    public static String getRevisionNumber(final String revisionNumber) {
        return "<span class=\"revisionNumberOuter\">Rev.: <span class=\"revisionNumberInner\">" + revisionNumber + "</span></span>";
    }

    /**
     * Returns HTML code for a directory page link
     * @param directory a directory
     * @return HTML code for the link
     */
    public static String getDirectoryLink(final Directory directory) {
        final String caption = directory.isRoot() ? "/" : directory.getPath();
        return "<a href=\"" + escape(DirectoryPageMaker.getURL(directory)) + "\" class=\"directory\">" + escape(caption) + "</a>";
    }

    /**
     * Generates HTML for an icon
     * @param iconFilename an icon filename (HTMLOutput.XXXX_ICON constants)
     * @return HTML string
     */
    public static String getIcon(final String iconFilename) {
        final StringBuffer result = new StringBuffer("<img src=\"");
        result.append(escape(iconFilename)).append("\" width=\"");
        result.append(ReportSuiteMaker.ICON_WIDTH).append("\" height=\"");
        result.append(ReportSuiteMaker.ICON_HEIGHT).append("\" alt=\"\"/>");
        return result.toString();
    }

    /**
     * Generates HTML for an icon
     * @param iconFilename an icon filename (HTMLOutput.XXXX_ICON constants)
     * @param title the title for the icon.
     * @return HTML string
     */
    public static String getIcon(final String iconFilename, final String title) {
        final StringBuffer result = new StringBuffer("<img src=\"");
        result.append(escape(iconFilename)).append("\" width=\"");
        result.append(ReportSuiteMaker.ICON_WIDTH).append("\" height=\"");
        result.append(ReportSuiteMaker.ICON_HEIGHT).append("\" alt=\"").append(title).append("\"");
        result.append(" title=\"").append(title).append("\"");
        result.append("/>");
        return result.toString();
    }

    /**
     * <p>
     * Escapes evil characters in author's names. E.g. "#" must be escaped
     * because for an author "my#name" a page "author_my#name.html" will be
     * created, and you can't link to that in HTML
     * </p>
     *
     * TODO: Replace everything *but* known good characters, instead of just
     * evil ones
     *
     * @param authorName an author's name
     * @return a version safe for creation of files and URLs
     */
    public static String escapeAuthorName(final String authorName) {
        return authorName.replaceAll("#", "_").replaceAll("\\\\", "_");
    }

    public static String escapeDirectoryName(String directoryName) {
        if (!directoryName.startsWith("/")) {
            directoryName = "/" + directoryName;
        }
        return directoryName.substring(0, directoryName.length() - 1).replaceAll("/", "_");
    }

    /**
     * Escapes HTML meta characters "&", "<", ">" and turns "\n" line breaks
     * into HTML line breaks ("<br />");
     * @param text some string, for example "x > 0 && y < 100"
     * @return HTML-escaped string, for example "x &gt; 0 &amp;&amp; y &lt; 100"
     */
    public static String escape(final String text) {
        String result = text.replaceAll("&", "&amp;");
        result = result.replaceAll("<", "&lt;");
        result = result.replaceAll(">", "&gt;");
        result = result.replaceAll("\n", "<br />\n");
        return result;
    }

    /**
     * Escapes HTML as well as " as it is not valid in URL.
     * @param text some string, for example "x > 0 && y < 100"
     * @return HTML-escaped string, for example "x &gt; 0 &amp;&amp; y &lt; 100"
     */
    public static String escapeUrl(final String text) {
        String result = escape(text);
        result = result.replaceAll("\"", "");
        result = result.replaceAll(" ", "%20");
        return result;
    }

    /**
     * Escapes HTML as well as " as it is not valid in URL.
     * @param text some string, for example "x > 0 && y < 100"
     * @return HTML-escaped string, for example "x &gt; 0 &amp;&amp; y &lt; 100"
     */
    public static String escapeUrlParameters(final String text) {
        String result = escape(text);
//        result = result.replaceAll("\"", "");
        result = result.replaceAll("\\%", "%25");
        result = result.replaceAll("\\$", "%24");
        result = result.replaceAll("\\&", "%26");
        result = result.replaceAll("\\+", "%2B");
        result = result.replaceAll(",", "%2C");
        result = result.replaceAll("/", "%2F");
        result = result.replaceAll(":", "%3A");
        result = result.replaceAll(";", "%3B");
        result = result.replaceAll("=", "%3D");
        result = result.replaceAll("\\?", "%3F");
        result = result.replaceAll("@", "%40");
        result = result.replaceAll("\"", "%22");
//        result = result.replaceAll("<", "%3C");
//        result = result.replaceAll(">", "%3E");
        result = result.replaceAll("#", "%23");
        result = result.replaceAll("\\{", "%7B");
//        result = result.replaceAll("\\}", "%7C");
        result = result.replaceAll("\\\\", "%5C");
        result = result.replaceAll("\\^", "%5E");
        result = result.replaceAll("~", "%7E");
        result = result.replaceAll("\\[", "%5B");
        result = result.replaceAll("\\]", "%5D");
        result = result.replaceAll("`", "%60");
        result = result.replaceAll(" ", "%20");
        return result;
    }

    /**
     * A utility class (only static methods) should be final and have
     * a private constructor.
     */
    private HTML() {
    }

    /**
     * From a plain text comment identify the http: and https links and create a link for them.
     * @param plainText
     * @author Benoit Xhenseval
     */
    public static String webifyLinksFromPlainText(final String plainText) {
        final String escapedText = HTML.escape(plainText);
        final Matcher m = HTTP_REGEXP2.matcher(escapedText);

        //        System.out.println("escaped : " + escapedText);
        //        while (m.find()) {
        //            System.out.println(m.group());
        //        }
        String res = m.replaceAll("<a href=\"$0\">$0</a>");
        res = res.replaceAll("href=\"www.", "href=\"http://www.");

        //        System.out.println("And now: " + res);

        return res;
    }
}
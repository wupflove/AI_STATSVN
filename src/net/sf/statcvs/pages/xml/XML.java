package net.sf.statcvs.pages.xml;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.pages.DirectoryPageMaker;
import net.sf.statcvs.pages.ReportSuiteMaker;

/**
 * TODO: Can we turn this into an abstract base class of MarkupHTML and MarkupXDoc?
 *
 * @author Anja Jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: XML.java,v 1.5 2009/03/13 23:04:28 benoitx Exp $
 */
public final class XML {
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat(Messages.getString("DATE_FORMAT"));
    private static final SimpleDateFormat OUTPUT_DATE_TIME_FORMAT = new SimpleDateFormat(Messages.getString("DATE_TIME_FORMAT"));

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
        return escape(author.getName());
    }

    /**
     * Returns HTML code for a link to an author Id page
     * @param author the author
     * @return HTML code for the link
     */
    public static String getAuthorIdLink(final Author author) {
        return escape(author.getName());
    }

    /**
     * Returns HTML code for a date
     * @param date the date
     * @return HTML code for the date
     */
    public static String getDate(final Date date) {
        return OUTPUT_DATE_FORMAT.format(date);
    }

    /**
     * Returns HTML code for a date, including time
     * @param date the date
     * @return HTML code for the date
     */
    public static String getDateAndTime(final Date date) {
        return OUTPUT_DATE_TIME_FORMAT.format(date);
    }

    /**
     * Returns HTML code for a directory page link
     * @param directory a directory
     * @return HTML code for the link
     */
    public static String getDirectoryLink(final Directory directory) {
        final String caption = directory.isRoot() ? "/" : directory.getPath();
        return escape(DirectoryPageMaker.getURL(directory)) + escape(caption);
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
        final String result = text; /*.replaceAll("&", "&amp;");
                                    	result = result.replaceAll("<", "&lt;");
                                    	result = result.replaceAll(">", "&gt;");
                                    	result = result.replaceAll("\n", "<br />\n");*/
        return result;
    }

    /**
     * A utility class (only static methods) should be final and have
     * a private constructor.
     */
    private XML() {
    }
}
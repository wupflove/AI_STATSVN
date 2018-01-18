package net.sf.statcvs.weblinks.bugs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.statcvs.Messages;
import net.sf.statcvs.pages.HTML;
import net.sf.statcvs.pages.ReportSuiteMaker;

/**
 * A BugTracker generates links to numbered bugs. We use this to turn
 * bug references in commit log messages (e.g. "Bug #123") into clickable
 * links.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: BugTracker.java,v 1.9 2009/03/09 21:45:42 benoitx Exp $
 */
public abstract class BugTracker {
    protected static final Pattern bugRegex = Pattern.compile("bug\\s*(?:number\\s*)?(?:#\\s*)?(\\d+)", Pattern.CASE_INSENSITIVE);

    /**
     * A null object that can be used in place of a real bug tracker.
     */
    public static final BugTracker NO_BUG_TRACKER = new BugTracker("") {
        public String bugURL(final String bugNumber) {
            return null;
        }

        public String getName() {
            return "No bug tracker";
        }

        public String toHTMLWithLinks(final String plainText) {
            return HTML.webifyLinksFromPlainText(plainText);
        }
    };

    private final String baseURL;

    /**
     * Creates a new BugTracker instance.
     * @param baseURL The bug tracker's base URL; a slash is appended
     * 		if it doesn't end in a slash
     */
    public BugTracker(final String baseURL) {
        this.baseURL = baseURL + (baseURL.endsWith("/") ? "" : "/");
    }

    /**
     * Returns the name of the bug tracker
     * @return the name of the bug tracker
     */
    public abstract String getName();

    /**
     * Returns the bug tracker's base URL.
     * @return The bug tracker's base URL
     */
    public String baseURL() {
        return this.baseURL;
    }

    /**
     * Returns the URL of the bug tracker page about a certain bug.
     * @param bugNumber The bug number; one or more digits.
     * @return The URL of the bug page
     */
    public abstract String bugURL(String bugNumber);

    /**
     * Filters a String, e.g. a commit message, replacing bug references with
     * links to the tracker.
     * @param plainTextInput String to examine for bug references
     * @return A copy of <code>input</code>, with bug references replaced with HTML links
     */
    public String toHTMLWithLinks(final String plainTextInput) {
        if (baseURL() == null || baseURL().length() == 0) {
            return HTML.webifyLinksFromPlainText(plainTextInput);
        }
        final StringBuffer result = new StringBuffer();
        final Matcher m = bugRegex.matcher(plainTextInput);
        int offset = 0;
        while (m.find()) {
            final String linkLabel = m.group();
            final String bugNumber = m.group(1);
            final String bugURL = bugURL(bugNumber);
            result.append(HTML.webifyLinksFromPlainText(plainTextInput.substring(offset, m.start())));
            if (bugURL == null) {
                result.append(HTML.webifyLinksFromPlainText(linkLabel));
            } else {
                result.append(HTML.getLink(bugURL, linkLabel, HTML.getIcon(ReportSuiteMaker.BUG_ICON, Messages.getString("BUG_ICON")), ""));
            }
            offset = m.end();
        }
        result.append(HTML.webifyLinksFromPlainText(plainTextInput.substring(offset, plainTextInput.length())));
        return result.toString();
    }
}
package net.sf.statcvs.pages;

import net.sf.statcvs.Messages;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.util.FileUtils;

/**
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: MarkupHTML.java,v 1.7 2009/08/19 22:11:15 benoitx Exp $
 */
public class MarkupHTML extends AbstractMarkup implements MarkupSyntax {
    private final static MarkupSyntax instance = new MarkupHTML();

    public static MarkupSyntax getInstance() {
        return instance;
    }

    private MarkupHTML() {
        // Singleton
    }

    public String getExtension() {
        return "html";
    }

    public String getHeader(final String pageName, final String stylesheetURL, final String charSet) {
        final StringBuffer b = new StringBuffer();
        b.append("<?xml version=\"1.0\"?>\n");
        b.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
        b.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        b.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        b.append("<head>\n    <title>");
        b.append(Messages.getString("PROJECT_SHORTNAME"));
        b.append(" - ");
        b.append(pageName);
        b.append("</title>\n");
        b.append(" <meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
        b.append(charSet);
        b.append("\"/>\n");
        b.append("    <meta name=\"Generator\" content=\"");
        b.append(Messages.getString("PROJECT_SHORTNAME"));
        b.append(" ");
        b.append(Messages.getString("PROJECT_VERSION"));
        b.append("\"/>\n");
        b.append("<link rel=\"stylesheet\" href=\"");
        b.append(HTML.escape(stylesheetURL));
        b.append("\" type=\"text/css\"/>\n");
        if (ConfigurationOptions.isEnableTwitterButton()) {
            addTwitterScript(b);
        }
        b.append("  </head>\n\n<body>\n");

        if (ConfigurationOptions.getHeaderUrl() != null) {
            b.append(FileUtils.readTextFromURL(ConfigurationOptions.getHeaderUrl()));
        }
        return b.toString();
    }

    public String getEndOfPage() {
        final StringBuffer b = new StringBuffer();
        if (ConfigurationOptions.getFooterUrl() != null) {
            b.append(FileUtils.readTextFromURL(ConfigurationOptions.getFooterUrl()));
        }
        b.append("</body>\n</html>");
        return b.toString();
    }

    public String startSection1(final String title) {
        return "\n<h1>" + title + "</h1>\n";
    }

    public String endSection1() {
        return "";
    }

    public String startSection2(final String title) {
        return "\n<div class=\"section\">\n<h2>" + title + "</h2>\n";
    }

    public String startSection2(final String title, final String id) {
        return "\n<div id=\"" + HTML.escape(id) + "\" class=\"section\">\n<h2>" + title + "</h2>\n";
    }

    public String endSection2() {
        return "</div>";
    }

    public String getTableFormat() {
        return "";
    }

    public String toString() {
        return "HTML";
    }
}

package net.sf.statcvs.pages;

import net.sf.statcvs.output.ConfigurationOptions;

/**
 * @author Benoit Xhenseval
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: MarkupXDoc.java,v 1.9 2009/08/05 17:22:52 benoitx Exp $
 */
public class MarkupXDoc extends AbstractMarkup implements MarkupSyntax {
    private final static MarkupSyntax instance = new MarkupXDoc();

    public static MarkupSyntax getInstance() {
        return instance;
    }

    private MarkupXDoc() {
        // Singleton
    }

    public String getEndOfPage() {
        return "</body>\n</document>";
    }

    public String getExtension() {
        return "xml";
    }

    public String getHeader(final String pageName, final String stylesheetURL, final String charSet) {
        StringBuffer b = new StringBuffer();
        
        
        b.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
        b.append("<document xmlns:lxslt=\"http://xml.apache.org/xslt\">\n");
        b.append("<properties><title>");
        b.append(pageName);
        b.append("</title></properties>\n");
        b.append("<head>");
        b.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
        b.append(charSet);
        b.append("\"/>\n");
        b.append("    <meta name=\"Generator\" content=\"StatCVS @VERSION@\"/>\n");
        b.append("   <link rel=\"stylesheet\" href=\"");
        b.append(HTML.escape(stylesheetURL));
        b.append("\" type=\"text/css\"/>\n");
        
        if (ConfigurationOptions.isEnableTwitterButton()) {
            addTwitterScript(b);
        }
        b.append("</head>\n\n<body>");

        return b.toString();
    }

    public String startSection1(final String title) {
        return "<section name=\"" + title + "\">\n";
    }

    public String endSection1() {
        return "</section>";
    }

    public String startSection2(final String title) {
        return "\n<div class=\"section\">\n<subsection name =\"" + title + "\">\n";
    }

    public String startSection2(final String title, final String id) {
        return "\n<div id=\"" + HTML.escape(id) + "\" class=\"section\">\n<subsection name =\"" + title + "\">\n";
    }

    public String endSection2() {
        return "</subsection></div>";
    }

    public String getTableFormat() {
        return " class=\"statCvsTable\"";
    }

    public String toString() {
        return "XDoc";
    }
}

/**
 * 
 */
package net.sf.statcvs.pages.xml;

import net.sf.statcvs.pages.MarkupSyntax;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: MarkupXML.java,v 1.3 2008/06/25 20:46:43 benoitx Exp $
 */
public class MarkupXML implements MarkupSyntax {
    private final static MarkupSyntax instance = new MarkupXML();

    public static MarkupSyntax getInstance() {
        return instance;
    }

    private MarkupXML() {
        // Singleton
    }

    public String endSection1() {
        return null;
    }

    public String endSection2() {
        return null;
    }

    public String getEndOfPage() {
        return null;
    }

    public String getExtension() {
        return "xml";
    }

    public String getHeader(final String pageName, final String stylesheetURL, final String charSet) {
        return null;
    }

    public String getTableFormat() {
        return null;
    }

    public String startSection1(final String title) {
        return null;
    }

    public String startSection2(final String title) {
        return null;
    }

    public String startSection2(final String title, final String id) {
        return null;
    }
}

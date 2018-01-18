package net.sf.statcvs.pages;

/**
 * @author Benoit Xhenseval
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: MarkupSyntax.java,v 1.3 2008/06/25 20:46:43 benoitx Exp $
 */
public interface MarkupSyntax {

    /**
     * Extension for report pages, e.g. <tt>html</tt>
     */
    String getExtension();

    String getHeader(String pageName, String stylesheetURL, String charSet);

    String getEndOfPage();

    String startSection1(String title);

    String endSection1();

    String startSection2(String title);

    String startSection2(String title, String id);

    String endSection2();

    String getTableFormat();
}
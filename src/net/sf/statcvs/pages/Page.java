package net.sf.statcvs.pages;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.renderer.TableRenderer;
import net.sf.statcvs.reports.TableReport;

public class Page implements NavigationNode {
    private final static NumberFormat[] DOUBLE_FORMATS = { new DecimalFormat("0"), new DecimalFormat("0.0"), new DecimalFormat("0.00"),
            new DecimalFormat("0.000"), new DecimalFormat("0.0000") };

    private static final Logger logger = LogManager.getLogger();
    
    private final ReportConfig config;
    private final String fileName;
    private final String shortTitle;
    private final String fullTitle;
    private final MarkupSyntax outputFormat;
    private StringBuffer contents = new StringBuffer();
    private NavigationNode parent = null;
    private String siblingsTitle = null;
    private List siblings = Collections.EMPTY_LIST;
    private final List children = new ArrayList(0);
    private final List attributeKeys = new ArrayList(0);
    private final List attributeValues = new ArrayList(0);
    private boolean showLinkToPreviousSibling = false;
    private boolean inSection = false;
    private boolean written = false;

    /**
     * Creates a new page.
     * @param config The configuration to use
     * @param fileName File name for the page, <em>without</em> file extension
     * @param shortTitle A short navigation title
     * @param fullTitle A full headline title
     */
    public Page(final ReportConfig config, final String fileName, final String shortTitle, final String fullTitle) {
        this.config = config;
        this.fileName = fileName;
        this.shortTitle = shortTitle;
        this.fullTitle = fullTitle;
        this.outputFormat = config.getMarkup();
    }

    /* (non-Javadoc)
     * @see net.sf.statcvs.pages.NavigationNode#setParent(net.sf.statcvs.pages.NavigationNode)
     */
    public void setParent(final NavigationNode parent) {
        this.parent = parent;
    }

    /**
     * Sets a list of {@link Page}s that are siblings of this page.
     * The generated page will contain a navigation list that links
     * to all siblings. The sibling list may contain the page
     * itself.
     * @param siblingsTitle Title for navigation list, e.g. "Monthly Reports"
     * @param sibling A list of {@link Page}s
     */
    public void setSiblings(final String siblingsTitle, final List siblingPages) {
        this.siblingsTitle = siblingsTitle;
        this.siblings = siblingPages;
    }

    public void addChild(final NavigationNode child) {
        this.children.add(child);
        child.setParent(this);
    }

    /* (non-Javadoc)
     * @see net.sf.statcvs.pages.NavigationNode#getURL()
     */
    public String getURL() {
        return this.fileName + ".html";
    }

    /* (non-Javadoc)
     * @see net.sf.statcvs.pages.NavigationNode#getShortTitle()
     */
    public String getShortTitle() {
        return this.shortTitle;
    }

    /* (non-Javadoc)
     * @see net.sf.statcvs.pages.NavigationNode#getFullTitle()
     */
    public String getFullTitle() {
        return this.fullTitle;
    }

    public void setShowLinkToPreviousSibling(final boolean showLink) {
        this.showLinkToPreviousSibling = showLink;
    }

    public void addAttribute(final String key, final int value) {
        addAttribute(key, Integer.toString(value));
    }

    public void addAttribute(final String key, final int value, final String unit) {
        addAttribute(key, Integer.toString(value) + " " + unit);
    }

    public void addAttribute(final String key, final Date value) {
        addRawAttribute(key, HTML.getDateAndTime(value));
    }

    public void addAttribute(final String key, final String value) {
        addRawAttribute(key, HTML.escape(value));
    }

    public void addAttribute(final String key, final double value, final int decimalPlaces) {
        addAttribute(key, DOUBLE_FORMATS[decimalPlaces].format(value));
    }

    public void addAttribute(final String key, final double value, final int decimalPlaces, final String unit) {
        addAttribute(key, DOUBLE_FORMATS[decimalPlaces].format(value) + " " + unit);
    }

    public void addRawAttribute(final String key, final String rawValue) {
        this.attributeKeys.add(key);
        this.attributeValues.add(rawValue);
    }

    public void addRawContent(final String s) {
        this.contents.append(s);
    }

    public void addSection(final String title) {
        if (this.inSection) {
            this.contents.append(this.outputFormat.endSection2());
        }
        this.contents.append(this.outputFormat.startSection2(title));
        this.inSection = true;
    }

    public void addLink(final String url, final String text) {
        this.addRawContent("<p>" + HTML.getLink(url, text) + "</p>\n");
    }

    public void add(final ChartImage chart) {
        if (chart == null) {
            return;
        }
        addRawContent("<p class=\"chart\"><img src=\"" + HTML.escape(chart.getURL()) + "\" alt=\"" + HTML.escape(chart.getFullTitle()) + "\" width=\""
                + chart.getWidth() + "\" height=\"" + chart.getHeight() + "\" /></p>");
        chart.write();
    }

    public void add(final ChartImage chart, final String linkURL) {
        if (chart == null) {
            return;
        }
        addRawContent("<p class=\"chart\"><a href=\"" + HTML.escape(linkURL) + "\"><img src=\"" + HTML.escape(chart.getURL()) + "\" alt=\""
                + HTML.escape(chart.getFullTitle()) + "\" width=\"" + chart.getWidth() + "\" height=\"" + chart.getHeight() + "\" /></a></p>");
        chart.write();
    }

    public void add(final TableReport table) {
        table.calculate();
        addRawContent(new TableRenderer(table.getTable(), this.outputFormat).getRenderedTable());
    }

    public void add(final Directory directory, final boolean withRootLinks) {
        addRawContent(new DirectoryTreeFormatter(directory, withRootLinks).getFormatted());
    }

    public void add(final PageGroup pages) {
        addRawContent(pages.asLinkList());
        addChild(pages);
    }

    /* (non-Javadoc)
     * @see net.sf.statcvs.pages.NavigationNode#write()
     */
    public void write() {
        if (this.written) {
            return;
        }
        if (this.inSection) {
            this.contents.append(this.outputFormat.endSection2());
        }
        
        final Iterator it = this.children.iterator();
        while (it.hasNext()) {
            final NavigationNode cNavigationNode = (NavigationNode) it.next();
            cNavigationNode.setParent(this);
            cNavigationNode.write();
        }
        
        final String fileWithExtension = this.fileName + "." + this.config.getMarkup().getExtension();
        logger.info("writing page '" + this.fullTitle + "' to " + fileWithExtension);
        FileWriter w = null;
        try {
            w = new FileWriter(this.config.getRootDirectory() + fileWithExtension);
            w.write(this.outputFormat.getHeader(this.fullTitle, this.config.getCssHandler().getLink(), config.getCharSet()));
            w.write(this.outputFormat.startSection1(this.fullTitle));
            w.write(getLinkToParent());
            w.write(getNavigationLinks());
            w.write(getAttributes());
            w.write(this.contents.toString());
            //w.write(getLinkToPreviousSibling());
            //w.write(this.outputFormat.endSection1());
            //w.write(getGeneratedByBlock());
            w.write(this.outputFormat.getEndOfPage());
        } catch (final IOException ex) {
            logger.warn (ex.getMessage());
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (final IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
        this.written = true;

        // Free memory? Not sure if this has any effect ...
        this.contents = null;
    }

    public String asParentLink() {
        String result = "&#171; " + HTML.getLink(getURL(), getShortTitle());
        if (this.parent != null) {
            result = this.parent.asParentLink() + " " + result;
        }
        return result;
    }

    private String getLinkToParent() {
        if (this.parent == null) {
            return "";
        }
        return "<div id=\"parentlink\">" + this.parent.asParentLink() + "</div>\n";
    }

    private String getNavigationLinks() {
        if (this.siblingsTitle == null || this.siblings.isEmpty()) {
            return "";
        }
        final StringBuffer s = new StringBuffer();
        s.append(this.outputFormat.startSection2(this.siblingsTitle, "nav"));
        s.append("<ul>\n");
        final Iterator it = this.siblings.iterator();
        while (it.hasNext()) {
            final NavigationNode sibling = (NavigationNode) it.next();
            s.append("    <li>");
            if (sibling == this) {
                s.append("<span class=\"here\">" + HTML.escape(sibling.getShortTitle()) + "</span>");
            } else {
                s.append(HTML.getLink(sibling.getURL(), sibling.getShortTitle()));
            }
            s.append("</li>\n");
        }
        s.append("</ul>\n");
        s.append(this.outputFormat.endSection2());
        return s.toString();
    }

    private String getAttributes() {
        if (this.attributeKeys.isEmpty()) {
            return "";
        }
        final StringBuffer s = new StringBuffer();
        s.append("<dl class=\"attributes\">\n");
        for (int i = 0; i < this.attributeKeys.size(); i++) {
            final String key = (String) this.attributeKeys.get(i);
            final String value = (String) this.attributeValues.get(i);
            s.append("    <dt>" + HTML.escape(key) + ":</dt>\n");
            s.append("    <dd>" + value + "</dd>\n");
        }
        s.append("</dl>\n");
        return s.toString();
    }

    private String getGeneratedByBlock() {
        final StringBuffer s = new StringBuffer();
        s.append("<div id=\"generatedby\">");
        s.append(Messages.getString("PAGE_GENERATED_BY"));
        s.append(" ");
        s.append(HTML.getLink(Messages.getString("PROJECT_URL"), Messages.getString("PROJECT_SHORTNAME")) + " " + Messages.getString("PROJECT_VERSION"));
        s.append("</div>\n");
        return s.toString();
    }

    private String getLinkToPreviousSibling() {
        if (!this.showLinkToPreviousSibling) {
            return "";
        }
        final NavigationNode sibling = findPreviousSibling();
        if (sibling == null) {
            return "";
        }
        return "<p class=\"previous\">" + HTML.getLink(sibling.getURL(), sibling.getShortTitle()) + " &#187; </p>\n";
    }

    private NavigationNode findPreviousSibling() {
        final Iterator it = this.siblings.iterator();
        while (it.hasNext()) {
            final NavigationNode sibling = (NavigationNode) it.next();
            if (sibling != this) {
                continue;
            }
            if (!it.hasNext()) {
                return null;
            }
            return (NavigationNode) it.next();
        }
        return null;
    }
}
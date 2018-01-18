package net.sf.statcvs.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PageGroup implements NavigationNode {
    private final String title;
    private final boolean connectSiblings;
    private final List pages = new ArrayList(1);
    private NavigationNode mainPage = null;
    private NavigationNode parent = null;
    private boolean showLinkToPreviousSibling = false;

    public PageGroup(final String title) {
        this(title, true);
    }

    public PageGroup(final String title, final boolean connectSiblings) {
        this.title = title;
        this.connectSiblings = connectSiblings;
    }

    public void add(final NavigationNode page) {
        this.pages.add(page);
        if (this.mainPage == null) {
            this.mainPage = page;
        }
    }

    public void setParent(final NavigationNode parentPage) {
        this.parent = parentPage;
    }

    public void setSiblings(final String siblingsTitle, final List siblingPages) {
        throw new UnsupportedOperationException("Cannot set siblings for PageGroup");
    }

    public void setShowLinkToPreviousSibling(final boolean showLink) {
        this.showLinkToPreviousSibling = showLink;
    }

    public String getFullTitle() {
        return this.title;
    }

    public String getShortTitle() {
        return this.title;
    }

    public String getURL() {
        if (mainPage != null) {
            return this.mainPage.getURL();
        }
        
        new Exception("Dummy Exception please report to bx").printStackTrace();
        
        return "no-page-defined-yet";
    }

    public void write() {
        final Iterator it = this.pages.iterator();
        while (it.hasNext()) {
            final NavigationNode page = (NavigationNode) it.next();
            if (this.showLinkToPreviousSibling) {
                page.setShowLinkToPreviousSibling(this.showLinkToPreviousSibling);
            }
            if (this.parent != null) {
                page.setParent(this.parent);
            }
            if (this.connectSiblings) {
                page.setSiblings(this.title, this.pages);
            }
            page.write();
        }
        if (this.mainPage != null && !this.pages.contains(this.mainPage)) {
            this.mainPage.write();
        }
    }

    public String asLinkList() {
        final StringBuffer s = new StringBuffer();
        s.append("<ul class=\"linklist\">\n");
        final Iterator it = this.pages.iterator();
        while (it.hasNext()) {
            final NavigationNode page = (NavigationNode) it.next();
            s.append("    <li>" + HTML.getLink(page.getURL(), page.getShortTitle()) + "</li>\n");
        }
        s.append("</ul>");
        return s.toString();
    }

    public String asParentLink() {
        return this.mainPage.asParentLink();
    }
}

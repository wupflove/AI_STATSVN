package net.sf.statcvs.pages;

import java.util.List;

public interface NavigationNode {

    /**
     * Sets the page's parent. The generated page will link back to
     * the parent.
     */
    void setParent(NavigationNode parent);

    /**
     * Sets a list of {@link Page}s that are siblings of this page.
     * The generated page will contain a navigation list that links
     * to all siblings. The sibling list may contain the page
     * itself.
     * @param siblingsTitle Title for navigation list, e.g. "Monthly Reports"
     * @param sibling A list of {@link Page}s
     */
    void setSiblings(String siblingsTitle, List siblingPages);

    void setShowLinkToPreviousSibling(boolean showLink);

    String getURL();

    String getShortTitle();

    String getFullTitle();

    void write();

    String asParentLink();
}
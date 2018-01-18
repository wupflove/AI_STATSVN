package net.sf.statcvs.pages;

import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Directory;

/**
 * Formats a directory and its subdirectories into a tree.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DirectoryTreeFormatter.java,v 1.6 2008/04/02 11:22:14 benoitx Exp $
 */
public class DirectoryTreeFormatter {
    private static final int SPACE_COUNT = 4;

    private final Directory directory;
    private final boolean withRootLinks;

    public DirectoryTreeFormatter(final Directory directory, final boolean withRootLinks) {
        this.directory = directory;
        this.withRootLinks = withRootLinks;
    }

    private String getRootLinks(Directory dir) {
        String result = getFormattedName(dir, false, true);
        while (!dir.isRoot()) {
            final Directory parent = dir.getParent();
            result = getFormattedName(parent, true, false) + "/" + result;
            dir = parent;
        }
        return result;
    }

    public String getFormatted() {
        final StringBuffer result = new StringBuffer("<p class=\"dirtree\">\n");
        final Iterator it = this.directory.getSubdirectoriesRecursive().iterator();
        if (this.withRootLinks) {
            final Directory current = (Directory) it.next();
            result.append(getRootLinks(current)).append("<br/>\n");
        }
        while (it.hasNext()) {
            final Directory subdirectory = (Directory) it.next();
            format(subdirectory, 0, result);
        }
        result.append("</p>\n");
        return result.toString();
    }

    private void format(final Directory dir, final int currentDepth, final StringBuffer s) {
        s.append(getSpaces(dir.getDepth() - currentDepth));
        if (dir.isEmpty()) {
            s.append(HTML.getIcon(ReportSuiteMaker.DELETED_DIRECTORY_ICON, Messages.getString("DELETED_DIRECTORY_ICON")));
        } else {
            s.append(HTML.getIcon(ReportSuiteMaker.DIRECTORY_ICON, Messages.getString("DIRECTORY_ICON")));
        }
        s.append(" \n").append(getFormattedName(dir, true, false));
        s.append(" \n(").append(dir.getCurrentFileCount()).append(" ");
        s.append(Messages.getString("DIRECTORY_TREE_FILES")).append(", ");
        s.append(dir.getCurrentLOC()).append(" ");
        s.append(Messages.getString("DIRECTORY_TREE_LINES")).append(")<br />\n");
    }

    private String getFormattedName(final Directory directory, final boolean link, final boolean bold) {
        String name = directory.isRoot() ? Messages.getString("NAVIGATION_ROOT") : directory.getName();
        if (link) {
            final String url = DirectoryPageMaker.getURL(directory);
            name = HTML.getLink(url, name);
        } else {
            name = HTML.escape(name);
        }
        if (bold) {
            return "<strong>" + name + "</strong>";
        }
        return name;
    }

    private String getSpaces(final int count) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < count * SPACE_COUNT; i++) {
            result.append("&#160;");
        }
        return result.toString();
    }
}

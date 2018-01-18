package net.sf.statcvs.pages.xml;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: DirectoriesXml.java,v 1.2 2008/04/02 11:22:16 benoitx Exp $
 *
 * This is the class which generates the per directory information of the xml report
 */

import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;

import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.ReportConfig;

import org.jdom.Element;

public class DirectoriesXml {
    private final SortedSet directory;
    private final Repository repository;

    /**
     * @param config Configuration and data for the report suite
     */
    public DirectoriesXml(final ReportConfig config) {
        this.repository = config.getRepository();
        directory = this.repository.getDirectories();
    }

    /**
     * returns jdom element which contains data extracted from the SortedSet of directories
     *
     * @returns Element
     */
    public Element toFile() {
        final Element dir = new Element(XmlTags.TAG_DIRECTORIES);
        final Iterator it = this.directory.iterator();

        //        int depth = -1;
        final Element parent = dir;
        final Stack stack = new Stack();
        stack.add(dir);
        while (it.hasNext()) {
            final Directory direc = (Directory) it.next();
            final Element item = format(direc, 0);

            //            if (direc.getDepth() < depth) {
            //                int toReach = depth - direc.getDepth();
            //                while (toReach-- > 0) {
            //                    parent = (Element) stack.pop();
            //                }
            //            } else if (direc.getDepth() == depth) {
            //                parent = (Element) stack.pop();
            //            }
            //            System.err.println("Directory depth " + direc.getDepth() + " " + depth + " " + getFormattedName(direc) + " " + parent.getName());

            parent.addContent(item);
            //            if (direc.getDepth() >= depth) {
            //                stack.push(parent);
            //                parent = item;
            //            }
            //            depth = direc.getDepth();
        }

        return dir;
    }

    /**
     * returns jdom element properly formatted with the attributes and child elements
     *
     * @param Directory the current directory
     * @param int the depth of the directory
     */
    private Element format(final Directory dir, final int currentDepth) {
        final Element element = new Element(XmlTags.TAG_DIRECTORY);
        final Element path = new Element(XmlTags.TAG_PATH);
        //        path.setText(getFormattedName(dir));
        path.setText(dir.getPath());
        if (dir.isEmpty()) {
            element.setAttribute("status", "deleted");
        }
        final Element child1 = new Element(XmlTags.TAG_FILES);
        final String str = Integer.toString(dir.getCurrentFileCount());
        child1.setText(str);
        final Element child2 = new Element(XmlTags.TAG_LINES_CHANGED);
        final String str2 = Integer.toString(dir.getCurrentLOC());
        child2.setText(str2);
        element.addContent(path);
        element.addContent(child1);
        element.addContent(child2);
        return element;
    }
}

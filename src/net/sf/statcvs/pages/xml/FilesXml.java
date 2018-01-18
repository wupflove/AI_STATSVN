package net.sf.statcvs.pages.xml;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: FilesXml.java,v 1.5 2008/04/02 11:52:02 benoitx Exp $
 */

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.renderer.XMLRenderer;
import net.sf.statcvs.reportmodel.Column;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.reports.FileTypeReport;
import net.sf.statcvs.reports.FilesWithMostRevisionsTableReport;
import net.sf.statcvs.reports.LargestFilesTableReport;
import net.sf.statcvs.reports.TableReport;

import org.jdom.Element;

public class FilesXml {
    private final static NumberFormat[] DOUBLE_FORMATS = { new DecimalFormat("0"), new DecimalFormat("0.0"), new DecimalFormat("0.00"),
            new DecimalFormat("0.000"), new DecimalFormat("0.0000") };
    private static final int NO_OF_COLS_IN_EXT_TABLE = 4;
    private static final int NO_OF_COLS_IN_LARG_TABLE = 4;
    private static final int MAX_LARGEST_FILES = 40;
    private Table table;
    private final XMLRenderer renderer = new XMLRenderer();

    private final ReportConfig config;
    private final Repository repository;

    /**
     * @param config Configuration and data for the report suite
     */
    public FilesXml(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    /**
     * returns jdom element which contains data regarding the files of the repository
     *
     * @returns Element
     */
    public Element toFile() {
        final Element fileStats = new Element("FileStats");
        final Element summ = new Element(XmlTags.TAG_SUMMARY);
        summ.addContent(new Element(XmlTags.TAG_TOTAL_FILES).setText(Integer.toString(getCurrentFileCount())));
        summ.addContent(new Element(XmlTags.TAG_AVG_FILE_SIZE).setText(DOUBLE_FORMATS[1].format(getCurrentAverageFileSize()) + " lines"));
        summ.addContent(new Element(XmlTags.TAG_AVG_REVISIONS_PER_FILE).setText(Double.toString(getCurrentAverageRevisionCount())));
        fileStats.addContent(summ);
        fileStats.addContent(fileExts());
        fileStats.addContent(largestFiles());
        fileStats.addContent(mostRevs());

        return fileStats;
    }

    /**
     * returns jdom element which contains data about files grouped by extension
     *
     * @returns Element
     */
    private Element fileExts() {
        final Element ele = new Element(XmlTags.TAG_EXTENSIONS);
        final TableReport tr = new FileTypeReport(this.config);
        tr.calculate();
        this.table = tr.getTable();
        final String[] str = new String[NO_OF_COLS_IN_EXT_TABLE];
        for (int j = 0; j < table.getRowCount(); j++) {
            Element col = null;
            int i = 0;
            final Iterator it = table.getColumnIterator();
            final Iterator itr = table.getColumnIterator();
            while (it.hasNext()) {
                final Column column = (Column) it.next();
                column.renderHead(renderer);
                str[i] = renderer.getColumnHead();
                if (i == 0) {
                    col = new Element(str[i]);
                } else {
                    col.addContent(new Element(str[i]));
                }
                i++;
            }
            boolean isFirstColumn = true;
            int k = 0;
            while (itr.hasNext()) {
                final Column column = (Column) itr.next();
                column.renderCell(j, renderer);
                if (isFirstColumn && table.hasKeysInFirstColumn()) {
                    col.setAttribute("ext", renderer.getRowHead().toLowerCase());
                    isFirstColumn = false;
                } else {
                    col.getChild(str[k]).setText(renderer.getTableCell());
                }
                k++;
            }
            ele.addContent(col);
        }
        return ele;
    }

    /**
     * returns jdom element which contains data of the largest files in terms of LOC of the repository
     *
     * @returns Element
     */
    public Element largestFiles() {
        final Element larg = new Element(XmlTags.TAG_LARGEST_FILES);
        final TableReport largestFilesTable = new LargestFilesTableReport(this.config, this.repository.getFiles(), MAX_LARGEST_FILES);
        largestFilesTable.calculate();
        this.table = largestFilesTable.getTable();
        final String[] str = new String[NO_OF_COLS_IN_LARG_TABLE];
        for (int j = 0; j < table.getRowCount(); j++) {
            Element col = null;
            int i = 0;
            final Iterator it = table.getColumnIterator();
            final Iterator itr = table.getColumnIterator();
            while (it.hasNext()) {
                final Column column = (Column) it.next();
                column.renderHead(renderer);
                str[i] = renderer.getColumnHead();
                if (i == 0) {
                    col = new Element(str[i]);
                } else {
                    str[i] = renderer.getColumnHead().replaceAll(" ", "_");
                    //col.addContent(new Element (str[i]));
                }
                i++;
            }

            boolean isFirstColumn = true;
            int k = 0;
            while (itr.hasNext()) {
                final Column column = (Column) itr.next();
                column.renderCell(j, renderer);
                if (isFirstColumn && table.hasKeysInFirstColumn()) {
                    col.setText(/*removeHTML(*/renderer.getRowHead()/*)*/);
                    isFirstColumn = false;
                } else {
                    col.setAttribute(str[k].toLowerCase(), renderer.getTableCell());
                }
                k++;
            }
            larg.addContent(col);
        }
        return larg;
    }

    /**
     * returns jdom element which contains data of the files with most revisions
     *
     * @returns Element
     */
    public Element mostRevs() {
        final Element revs = new Element(XmlTags.TAG_MOST_REVISIONS);
        final TableReport mostRevs = new FilesWithMostRevisionsTableReport(this.config, this.repository.getFiles(), MAX_LARGEST_FILES);
        mostRevs.calculate();
        this.table = mostRevs.getTable();
        final String[] str = new String[NO_OF_COLS_IN_LARG_TABLE];
        for (int j = 0; j < table.getRowCount(); j++) {
            Element col = null;
            int i = 0;
            final Iterator it = table.getColumnIterator();
            final Iterator itr = table.getColumnIterator();
            while (it.hasNext()) {
                final Column column = (Column) it.next();
                column.renderHead(renderer);
                if (i == 0) {
                    str[i] = renderer.getColumnHead();
                    col = new Element(str[i]);
                } else {
                    str[i] = renderer.getColumnHead().replaceAll(" ", "_");
                    //col.addContent(new Element (str[i]));
                }
                i++;
            }

            boolean isFirstColumn = true;
            int k = 0;
            while (itr.hasNext()) {
                final Column column = (Column) itr.next();
                column.renderCell(j, renderer);
                if (isFirstColumn && table.hasKeysInFirstColumn()) {
                    col.setText(/*removeHTML(*/renderer.getRowHead());
                    isFirstColumn = false;
                } else {
                    col.setAttribute(str[k].toLowerCase(), renderer.getTableCell());
                }
                k++;
            }
            revs.addContent(col);
        }
        return revs;
    }

    /**
     * returns Integer of the number of files
     *
     * @returns int
     */
    private int getCurrentFileCount() {
        int result = 0;
        final Iterator it = this.repository.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                result++;
            }
        }
        return result;
    }

    /**
     * returns the average size of a file in terms of LOC
     *
     * @returns double
     */
    private double getCurrentAverageFileSize() {
        return ((double) this.repository.getCurrentLOC()) / getCurrentFileCount();
    }

    /**
     * returns number of revisions done on average
     *
     * @returns double
     */
    private double getCurrentAverageRevisionCount() {
        int revisions = 0;
        final Iterator it = this.repository.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                revisions += file.getRevisions().size();
            }
        }
        return ((double) revisions) / getCurrentFileCount();
    }
}
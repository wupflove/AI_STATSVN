package net.sf.statcvs.renderer;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.pages.MarkupSyntax;
import net.sf.statcvs.pages.xml.XML;

/**
 * Helper class for rendering different types of table cells and table heads
 * to XML
 *
 * @author Nilendra Weerasinghe <nilendraw@gmail.com>
 * @version $Id: XMLRenderer.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class XMLRenderer implements TableCellRenderer {
    private String xml = null;
    private MarkupSyntax output = null;

    /**
     * Render a generic table cell to XML
     * @param content the cell's content
     */
    public void renderCell(final String content) {
        xml = content;
    }

    /**
     * Render an empty cell to XML
     */
    public void renderEmptyCell() {
        xml = null;
    }

    /**
     * Render an integer cell to XML
     * @param value the cell's content
     */
    public void renderIntegerCell(final int value) {
        xml = Integer.toString(value);
    }

    /**
     * Render an integer cell to XML, showing both the integer value and
     * a percentage of a total
     * @param value the cell's content
     * @param total the total, worth 100%
     */
    public void renderIntegerCell(final int value, final int total) {
        xml = Integer.toString(value) + " (" + getPercentage((double) value / (double) total) + ")";
    }

    /**
     * Render a percentage cell to XML
     * @param ratio the cell's content
     */
    public void renderPercentageCell(final double ratio) {
        xml = getPercentage(ratio);
    }

    /**
     * Render a cell containing an author to XML
     * @param author the author
     */
    public void renderAuthorCell(final Author author) {
        xml = XML.escape(author.getName());
    }

    /**
     * Render a cell containing a directory to XML
     * @param directory the directory
     */
    public void renderDirectoryCell(final Directory directory) {
        xml = XML.getDirectoryLink(directory);
    }

    /**
     * Render a cell containing a file to XML
     * @param file the file
     * @param withIcon display an icon in front of the filename?
     * @param webRepository for creating links; might be <tt>null</tt>
     */
    public void renderFileCell(final VersionedFile file, final boolean withIcon, final WebRepositoryIntegration webRepository) {
        //		if (webRepository == null) {
        xml = file.getFilenameWithPath();
        //		} else {
        //			xml = XML.getLink(webRepository.getFileViewUrl(file),
        //					file.getFilenameWithPath());
        //		}
        //		if (withIcon) {
        //			if (file.isDead()) {
        //				xml = XML.getIcon(ReportSuiteMaker.DELETED_FILE_ICON) + " " + xml;
        //			} else {
        //				xml = XML.getIcon(ReportSuiteMaker.FILE_ICON) + " " + xml;
        //			}
        //		}
    }

    /**
     * Render a cell containing a repository tag.
     */
    public void renderLinkCell(final String url, final String label) {
        this.xml = XML.getLink(url, label);
    }

    /**
     * Return the results of the last <tt>renderCell</tt> call
     * @return XML
     */
    public String getColumnHead() {
        return getHtml();
    }

    /**
     * Return the results of the last <tt>renderCell</tt> call
     * as a row head
     * @return XML
     */
    public String getRowHead() {
        return getHtml();
    }

    /**
     * Return the results of the last <tt>renderCell</tt> call
     * as an ordinary table cell
     * @return XML
     */
    public String getTableCell() {
        return xml;
    }

    private String getPercentage(final double ratio) {
        if (Double.isNaN(ratio)) {
            return "-";
        }
        final int percentTimes10 = (int) Math.round(ratio * 1000);
        final double percent = percentTimes10 / 10.0;
        return Double.toString(percent) + "%";
    }

    private String getHtml() {
        return xml.replaceAll(" ", "");
    }

    /**
     * @return the output
     */
    public MarkupSyntax getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(final MarkupSyntax output) {
        this.output = output;
    }

    public String getOddRowFormat() {
        return " class=\"even\"";
    }

    public String getEvenRowFormat() {
        return " class=\"odd\"";
    }

    public void renderAuthorIdCell(final Author author) {
        xml = XML.getAuthorIdLink(author);
    }
}
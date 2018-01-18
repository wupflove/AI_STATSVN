/*
	StatCvs - CVS statistics generation 
	Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
	http://statcvs.sf.net/
    
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: TableCellRenderer.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.renderer;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.WebRepositoryIntegration;

/**
 * Interface for a class that turns {@link net.sf.statcvs.reportmodel.Column}s
 * into their representation for some output format, for example a HTML
 * &lt;td&gt; or an XML element.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TableCellRenderer.java,v 1.11 2008/04/02 11:22:15 benoitx Exp $
 */
public interface TableCellRenderer {

    /**
     * Render a generic table cell
     * @param content the cell's content
     */
    void renderCell(String content);

    /**
     * Render an empty cell
     */
    void renderEmptyCell();

    /**
     * Render an integer cell
     * @param value the cell's content
     */
    void renderIntegerCell(int value);

    /**
     * Render an integer cell, showing both the integer value and
     * a percentage of a total
     * @param value the cell's content
     * @param total the total, worth 100%
     */
    void renderIntegerCell(int value, int total);

    /**
     * Render a percentage cell
     * @param ratio the cell's content
     */
    void renderPercentageCell(double ratio);

    /**
     * Render a cell containing an author
     * @param author the author
     */

    void renderAuthorCell(Author author);

    /**
     * Render a cell containing an author Id
     * @param author the author
     */

    void renderAuthorIdCell(Author author);

    /**
     * Render a cell containing a directory
     * @param directory the directory
     */
    void renderDirectoryCell(Directory directory);

    /**
     * Render a cell containing a file
     * @param file the file
     * @param withIcon display an icon in front of the filename?
     * @param webRepository for creating links; might be <tt>null</tt>
     */
    void renderFileCell(VersionedFile file, boolean withIcon, WebRepositoryIntegration webRepository);

    /**
     * Render a cell containing a link.
     */
    void renderLinkCell(String url, String label);
}
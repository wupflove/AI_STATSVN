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
    
	$RCSfile: DirectoryColumn.java,v $
	$Date: 2008/04/02 11:22:14 $
*/
package net.sf.statcvs.reportmodel;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.renderer.TableCellRenderer;

/**
 * A table column containing directories
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: DirectoryColumn.java,v 1.4 2008/04/02 11:22:14 benoitx Exp $
 */
public class DirectoryColumn extends GenericColumn {

    /**
     * Creates a new directory column
     */
    public DirectoryColumn() {
        super(Messages.getString("COLUMN_DIRECTORY"));
    }

    /**
     * @see net.sf.statcvs.reportmodel.Column#renderCell
     */
    public void renderCell(final int rowIndex, final TableCellRenderer renderer) {
        renderer.renderDirectoryCell((Directory) getValue(rowIndex));
    }
}

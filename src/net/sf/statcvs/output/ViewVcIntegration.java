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

	$RCSfile: ViewVcIntegration.java,v $
	$Date: 2009/06/02 13:27:20 $
*/
package net.sf.statcvs.output;

import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;

/**
 * Integration of ViewVC
 *
 * @author Richard Cyganiak
 * @author Jason Kealey
 *
 * @version $Id: ViewVcIntegration.java,v 1.4 2009/06/02 13:27:20 benoitx Exp $
 */
public class ViewVcIntegration extends ViewCvsIntegration {
    /**
     * @param baseURL base URL of the ViewVC installation
     */
    public ViewVcIntegration(final String baseURL) {
        super(baseURL);
    }

    /**
     * @see net.sf.statsvn.output.WebRepositoryIntegration#getName
     */
    public String getName() {
        return "ViewVC";
    }

    protected String getFileUrl(final VersionedFile file, final String parameter) {
        String filename;
        //		if (isInAttic(file)) {
        //			String path = file.getDirectory().getPath();
        //			filename = "/" + path + "Attic/" + file.getFilename();
        //
        //		} else {
        filename = "/" + file.getFilenameWithPath();
        //		}

        String append = parameter;
        if (getPostfix() != null) {
            append += (append.length() > 0) ? "&" + getPostfix() : "?" + getPostfix();
        }
        return getBaseUrl() + filename + append;
    }

    /**
     * @see net.sf.statcvs.output.WebRepositoryIntegration#getDiffUrl
     */
    public String getDiffUrl(final Revision oldRevision, final Revision newRevision) {
        if (!oldRevision.getFile().equals(newRevision.getFile())) {
            throw new IllegalArgumentException("revisions must be of the same file");
        }
        // because of ViewCVS limitations regarding dead files.
        if (isInAttic(newRevision.getFile())) {
            return getFileViewUrl(newRevision);
        } else {
            return getFileUrl(oldRevision.getFile(), "?r1=" + oldRevision.getRevisionNumber() + "&r2=" + newRevision.getRevisionNumber());
        }
    }
}

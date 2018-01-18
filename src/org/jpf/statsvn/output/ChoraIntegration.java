/*
 StatCvs - CVS statistics generation 
 Copyright (C) 2002  Lukasz Pekacki &lt;lukasz@pekacki.de&gt;
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
 
 $RCSfile: ChoraIntegration.java,v $
 $Date: 2004/10/12 07:22:42 $ 
 */
package org.jpf.statsvn.output;

import java.util.Set;

import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.WebRepositoryIntegration;

/**
 * Integration of the <a href="http://www.horde.org/chora/">Chora CVS Viewer</a>
 * 
 * @author Richard Cyganiak
 * @version $Id: ChoraIntegration.java,v 1.9 2004/10/12 07:22:42 cyganiak Exp $
 */
public class ChoraIntegration implements WebRepositoryIntegration {
	private String baseURL;

	/**
	 * @param baseURL
	 *            base URL of the Chora installation
	 */
	public ChoraIntegration(final String baseURL) {
		if (baseURL.endsWith("/")) {
			this.baseURL = baseURL.substring(0, baseURL.length() - 1);
		} else {
			this.baseURL = baseURL;
		}
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getName()"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getName()  </a>
	 */
	public String getName() {
		return "Chora";
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getDirectoryUrl(net.sf.statcvs.model.Directory)"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getDirectoryUrl(net.sf.statcvs.model.Directory) </a>
	 */
	public String getDirectoryUrl(final Directory directory) {
		return baseURL + "/?f=" + directory.getPath();
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getFileHistoryUrl(net.sf.statcvs.model.VersionedFile)"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getFileHistoryUrl(net.sf.statcvs.model.VersionedFile)  </a>
	 */
	public String getFileHistoryUrl(final VersionedFile file) {
		// chora doesn't seem to support deleted files for subversion
		// repositories
		//		if (isInAttic(file)) {
		// String path = file.getDirectory().getPath();
		// String filename = file.getFilename();
		// return baseURL + "/" + path + "Attic/" + filename;
		//		}
		return this.baseURL + "/?f=" + file.getFilenameWithPath();
	}

	private String getFileViewBaseUrl(final VersionedFile file) {
		return this.baseURL + "/co.php?f=" + file.getFilenameWithPath();
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getFileViewUrl(net.sf.statcvs.model.Revision)"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getFileViewUrl(net.sf.statcvs.model.Revision)  </a>
	 */
	public String getFileViewUrl(final VersionedFile file) {
		return getFileViewBaseUrl(file) + "&r=HEAD";
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getFileViewUrl(net.sf.statcvs.model.Revision)"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getFileViewUrl(net.sf.statcvs.model.Revision)  </a>
	 */
	public String getFileViewUrl(final Revision revision) {
		return getFileViewBaseUrl(revision.getFile()) + "&r=" + revision.getRevisionNumber();
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getDiffUrl(net.sf.statcvs.model.Revision, net.sf.statcvs.model.Revision)"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getDiffUrl(net.sf.statcvs.model.Revision, net.sf.statcvs.model.Revision)  </a>
	 */
	public String getDiffUrl(final Revision oldRevision, final Revision newRevision) {
		if (!oldRevision.getFile().equals(newRevision.getFile())) {
			throw new IllegalArgumentException("revisions must be of the same file");
		}

		return this.baseURL + "/diff.php?f=" + oldRevision.getFile().getFilenameWithPath() + "&r1=" + oldRevision.getRevisionNumber() + "&r2="
		        + newRevision.getRevisionNumber() + "&ty=h";
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/xref/net/sf/statcvs/output/WebRepositoryIntegration.html#setAtticFileNames(java.util.Set)"> http://statcvs.sourceforge.net/xref/net/sf/statcvs/output/WebRepositoryIntegration.html#setAtticFileNames(java.util.Set) </a>
	 */
	public void setAtticFileNames(final Set atticFileNames) {
		//		this.atticFileNames = atticFileNames;
	}

	/**
	 * @see <a href="http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getBaseUrl()"> http://statcvs.sourceforge.net/apidocs/net/sf/statcvs/output/WebRepositoryIntegration.html#getBaseUrl() </a>
	 */	
	public String getBaseUrl() {
		return baseURL;
	}
}

/*
 StatCVS - CVS statistics generation
 Copyright (C) 2006 Benoit Xhenseval

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

 */
package net.sf.statcvs.output;

import java.util.Set;

import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;

public class TracIntegration implements WebRepositoryIntegration {

    private String baseUrl;

    public TracIntegration(final String baseURL) {
        if (baseURL.endsWith("/")) {
            baseUrl = baseURL.substring(0, baseURL.length() - 1);
        } else {
            baseUrl = baseURL;
        }
    }

    public String getName() {
        return "Trac";
    }

    public String getDirectoryUrl(final Directory directory) {
        return baseUrl + "/browser/" + directory.getPath();
    }

    public String getFileHistoryUrl(final VersionedFile file) {
        return baseUrl + "/log/" + file.getFilenameWithPath();
    }

    public String getFileViewUrl(final Revision revision) {
        return baseUrl + "/browser/" + revision.getFile().getFilenameWithPath() + "?rev=" + revision.getRevisionNumber();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDiffUrl(final Revision oldRevision, final Revision newRevision) {
        return baseUrl + "/changeset?" + "old=" + oldRevision.getRevisionNumber() + "@" + oldRevision.getFile().getFilenameWithPath() + "&new="
                + newRevision.getRevisionNumber() + "@" + newRevision.getFile().getFilenameWithPath();
    }

    public String getFileViewUrl(final VersionedFile file) {
        return baseUrl + "/browser/" + file.getFilenameWithPath();
    }

    public void setAtticFileNames(final Set atticFileNames) {
        // Doing nothing ...
    }

}

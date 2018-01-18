package net.sf.statcvs.output;

import java.util.Set;

import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;

/**
 * Creates links to a <a href="http://www.jcvs.org/jcvsweb/">JCVSWeb</a> repository browser.
 * 
 * @author ERMEANEY
 * @version $Id: JCVSWebIntegration.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class JCVSWebIntegration implements WebRepositoryIntegration {
    private String baseURL;

    public JCVSWebIntegration(final String baseURL) {
        if (baseURL.endsWith("/")) {
            this.baseURL = baseURL.substring(0, baseURL.length() - 1);
        } else {
            this.baseURL = baseURL;
        }
    }

    public String getName() {
        return "JCVSWeb";
    }

    public String getDirectoryUrl(final Directory directory) {
        final String path = baseURL + "/list/" + directory.getPath();
        return path;
    }

    public String getFileHistoryUrl(final VersionedFile file) {
        return baseURL + "/vers/" + file.getFilenameWithPath();
    }

    public String getFileViewUrl(final VersionedFile file) {
        return baseURL + "/view/" + file.getFilenameWithPath() + "/" + file.getLatestRevision().getRevisionNumber();
    }

    public String getFileViewUrl(final Revision revision) {
        return baseURL + "/view/" + revision.getFile().getFilenameWithPath() + "/" + revision.getRevisionNumber();
    }

    public String getDiffUrl(final Revision oldRevision, final Revision newRevision) {
        return baseURL + "/pdiff/" + newRevision.getFile().getFilenameWithPath() + "/" + oldRevision.getRevisionNumber() + "/"
                + newRevision.getRevisionNumber();
    }

    public void setAtticFileNames(final Set atticFileNames) {
        // Ignore
    }

    public String getBaseUrl() {
        return baseURL;
    }
}
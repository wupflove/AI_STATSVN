package org.jpf.statsvn.util.svnkit;

import java.io.File;
import java.io.IOException;

import net.sf.statcvs.input.LogSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.statsvn.output.SvnConfigurationOptions;
import org.jpf.statsvn.util.ISvnProcessor;
import org.jpf.statsvn.util.SvnInfoUtils;
import org.jpf.statsvn.util.SvnVersionMismatchException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.xml.SVNXMLInfoHandler;
import org.xml.sax.Attributes;

/**
 * 
 * Performs svn info using svnkit. 
 * 
 * @author jkealey, yogesh
 *
 */
public class SvnKitInfo extends SvnInfoUtils {

    private static final Logger logger = LogManager.getLogger();
    protected static class SvnKitInfoHandler extends SvnInfoUtils.SvnInfoHandler {

        public SvnKitInfoHandler(SvnInfoUtils infoUtils) {
            super(infoUtils);
        }

        protected boolean isRootFolder(Attributes attributes) {
            String path = attributes.getValue("path");
            // . is never returned by SvnKit, it appears. 
            return (path.equals(".") || new File(path).equals(((SvnKitInfo) getInfoUtils()).getCheckoutDirectory()))
                    && attributes.getValue("kind").equals("dir");
        }

    }

    public SvnKitInfo(ISvnProcessor processor) {
        super(processor);
    }

    /**
     * Verifies that the "svn info" command can return the repository root
     * (info available in svn &gt;= 1.3.0)
     * 
     * @throws SvnVersionMismatchException
     *             if <tt>svn info</tt> failed to provide a non-empty repository root
     */
    public synchronized void checkRepoRootAvailable() throws SvnVersionMismatchException {

        try {
            loadInfo(true);
            if (getRootUrl() != null)
                return;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        throw new SvnVersionMismatchException(SVN_REPO_ROOT_NOTFOUND);
    }

    public File getCheckoutDirectory() {
        return getSvnKitProcessor().getCheckoutDirectory();
    }

    public SVNClientManager getManager() {
        return getSvnKitProcessor().getManager();
    }

    public SvnKitProcessor getSvnKitProcessor() {
        return (SvnKitProcessor) getProcessor();
    }

    protected void handleSvnException(SVNException ex) throws IOException {
        String msg = "svn info " + ex.getMessage();
        logger.error(msg);
        throw new IOException(msg);
    }

    /**
     * Loads the information from svn info if needed.
     * 
     * @param bRootOnly
     *            load only the root?
     * @throws LogSyntaxException
     *             if the format of the svn info is invalid
     * @throws IOException
     *             if we can't read from the response stream.
     */
    protected void loadInfo(final boolean bRootOnly) throws LogSyntaxException, IOException {
        if (isQueryNeeded(true /*bRootOnly*/)) {
            clearCache();

            try {
                SVNXMLInfoHandler handler = new SVNXMLInfoHandler(new SvnKitInfoHandler(this));
                handler.setTargetPath(getCheckoutDirectory());
                getManager().getWCClient().doInfo(getCheckoutDirectory(), null, null, SVNDepth.fromRecurse(!bRootOnly), null,
                        handler);
            } catch (SVNException e) {
                handleSvnException(e);
            }
        }
    }

}

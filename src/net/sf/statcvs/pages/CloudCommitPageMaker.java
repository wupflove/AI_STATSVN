package net.sf.statcvs.pages;

import net.sf.statcvs.Messages;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.CloudCommitTableReport;

/**
 * @author Benoit Xhenseval
 * @version $Id: CloudCommitPageMaker.java,v 1.2 2009/04/22 08:20:42 benoitx Exp $
 */
public class CloudCommitPageMaker {
    private final ReportConfig config;

    public CloudCommitPageMaker(final ReportConfig config) {
        this.config = config;
    }

    public NavigationNode toFile() {
        final CloudCommitTableReport developers = new CloudCommitTableReport(this.config);

        final Page page = this.config.createPage("cloud", Messages.getString("CLOUD"), this.config.getProjectName() + " "
                + Messages.getString("CLOUD"));
            page.add(developers);

        return page;
    }
}

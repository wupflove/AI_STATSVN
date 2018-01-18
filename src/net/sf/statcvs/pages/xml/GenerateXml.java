package net.sf.statcvs.pages.xml;

/**
 * @author Nilendra Weerasinghe (nilendraw@gmail.com)
 * @version $Id: GenerateXml.java,v 1.2 2008/04/02 11:22:16 benoitx Exp $
 *
 * This is the central class which creates all of the xml reports
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.reports.TopDevelopersTableReport;
import net.sf.statcvs.weblinks.bugs.BugTracker;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class GenerateXml {
    private static final String TAG_REPO_STATISTICS = "RepoStatistics";
    private final ReportConfig config;
    private final Repository repository;

    /**
     * @param config Configuration and data for the report suite
     */
    public GenerateXml(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    /**
     * This method will invoke the classes which capture the data required to generate the xmls
     */
    public void generate() {
        final TopDevelopersTableReport topDevelopers = new TopDevelopersTableReport(this.config);
        final String gen_time = Calendar.getInstance().getTime().toString();
        final String period = getReportPeriod();
        final String title = Messages.getString("INDEX_TITLE") + " " + this.config.getProjectName();
        final Element root = new Element(TAG_REPO_STATISTICS);
        root.setAttribute("date", gen_time.toLowerCase());
        root.setAttribute("period", period.toLowerCase());
        root.setAttribute("numberfiles", Integer.toString(getCurrentFileCount()));
        root.setAttribute("totalloc", Integer.toString(this.repository.getCurrentLOC()));
        root.setAttribute("numberdevelopers", Integer.toString(topDevelopers.getDeveloperCount()));

        final Document myXML = new Document(root);
        myXML.addContent(new Comment(title));

        final Element el2 = new Element(XmlTags.TAG_CHECKED_OUT_DIR);
        el2.setAttribute("path", ConfigurationOptions.getCheckedOutDirectory());
        root.addContent(el2);

        if (ConfigurationOptions.getProjectName() != null) {
            final Element el3 = new Element(XmlTags.TAG_PROJECT);
            el3.setAttribute("path", ConfigurationOptions.getCheckedOutDirectory());
            root.addContent(el3);
        }
        /*
        if (repository.getRoot() != null) {
            Element el3 = new Element(XmlTags.TAG_ROOT);
            el3.setAttribute("path", repository.getRoot().getPath());
            root.addContent(el3);
        }
        */

        final BugTracker bt = ConfigurationOptions.getWebBugtracker();
        if (bt != null) {
            final Element el = new Element(XmlTags.TAG_BUG_TRACKER);
            el.setAttribute("baseurl", bt.baseURL());
            el.setAttribute("name", bt.getName());
            root.addContent(el);
        }

        final WebRepositoryIntegration wi = ConfigurationOptions.getWebRepository();
        if (wi != null) {
            final Element el = new Element(XmlTags.TAG_WEB_REPOSITORY);
            el.setAttribute("baseurl", wi.getBaseUrl());
            el.setAttribute("name", wi.getName());
            root.addContent(el);
        }

        /*
         * add developer statistics section to xml report
         */
        final AllDevelopersXml adxml = new AllDevelopersXml(config);
        final Element child1 = adxml.toFile();
        root.addContent(child1);

        /*
         * add directories statistics section to xml report
         */
        final DirectoriesXml dxml = new DirectoriesXml(config);
        final Element child2 = dxml.toFile();
        root.addContent(child2);

        /*
         * add commit log section to xml report
         */
        final LogXml log = new LogXml(config);
        final Element child3 = log.toFile();
        root.addContent(child3);

        /*
         * add commit log section to xml
         */
        final FilesXml fl = new FilesXml(config);
        final Element child4 = fl.toFile();
        root.addContent(child4);

        /*
         * add revised file list section to xml report
        RevisedFilesXml rxml = new RevisedFilesXml(config);
        Element child5 = rxml.toFile();
        root.addContent(child5);
         */

        // serialize it into a file
        try {
            final String file = ConfigurationOptions.getOutputDir() + "repo-statistics.xml";
            final FileOutputStream out = new FileOutputStream(file);
            final XMLOutputter serializer = new XMLOutputter();
            serializer.output(myXML, out);
            out.flush();
            out.close();
        } catch (final IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Returns the period for which the report is being generated
     *
     * @returns String
     */
    private String getReportPeriod() {
        return XML.getDate(this.repository.getFirstDate()) + " to " + XML.getDate(this.repository.getLastDate());
    }

    /**
     * Returns the no. of files in the repository
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
}

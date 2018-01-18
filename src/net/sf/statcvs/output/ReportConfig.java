package net.sf.statcvs.output;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.pages.MarkupHTML;
import net.sf.statcvs.pages.MarkupSyntax;
import net.sf.statcvs.pages.MarkupXDoc;
import net.sf.statcvs.pages.Page;
import net.sf.statcvs.pages.xml.MarkupXML;
import net.sf.statcvs.util.FileUtils;
import net.sf.statcvs.weblinks.bugs.BugTracker;

import org.jfree.chart.JFreeChart;

/**
 * A configuration object that controls several aspects of 
 * report creation, such as the output directory and chart
 * sizes. A single instance is passed around to all objects
 * involved in report creation.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ReportConfig.java,v 1.12 2009/06/02 13:28:53 benoitx Exp $
 */
public class ReportConfig {
    public static final MarkupSyntax XDOC = MarkupXDoc.getInstance();
    public static final MarkupSyntax HTML = MarkupHTML.getInstance();
    public static final MarkupSyntax XML = MarkupXML.getInstance();
    private static final Dimension SMALL_CHART_SIZE = new Dimension(512, 320);
    private static final Dimension LARGE_CHART_SIZE = new Dimension(800, 500);

    private final Repository repository;
    private final String projectName;
    private final String rootDirectory;
    private final MarkupSyntax markup;
    private final CssHandler cssHandler;
    private Dimension smallChartSize;
    private Dimension largeChartSize;
    private WebRepositoryIntegration webRepository = null;
    private BugTracker webBugtracker = null;
    private Collection nonDeveloperLogins = Collections.EMPTY_LIST;
    private final String charSet;

    public ReportConfig(final Repository repository, final String projectName, final String rootDirectory, final MarkupSyntax syntax,
            final CssHandler cssHandler, final String charSet) {
        this.repository = repository;
        this.projectName = projectName;
        this.rootDirectory = rootDirectory;
        this.markup = syntax;
        this.cssHandler = cssHandler;
        this.smallChartSize = SMALL_CHART_SIZE;
        this.largeChartSize = LARGE_CHART_SIZE;
        this.charSet = charSet;
    }

    public void setSmallChartSize(final Dimension newSize) {
        this.smallChartSize = newSize;
    }

    public void setLargeChartSize(final Dimension newSize) {
        this.largeChartSize = newSize;
    }

    public void setWebRepository(final WebRepositoryIntegration webRepository) {
        this.webRepository = webRepository;
    }

    public void setWebBugtracker(final BugTracker webBugtracker) {
        this.webBugtracker = webBugtracker;
    }

    public void setNonDeveloperLogins(final Collection names) {
        this.nonDeveloperLogins = names;
    }

    public String getRootDirectory() {
        return this.rootDirectory;
    }

    public Repository getRepository() {
        return this.repository;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public Dimension getSmallChartSize() {
        return this.smallChartSize;
    }

    public Dimension getLargeChartSize() {
        return this.largeChartSize;
    }

    public MarkupSyntax getMarkup() {
        return this.markup;
    }

    public CssHandler getCssHandler() {
        return this.cssHandler;
    }

    public WebRepositoryIntegration getWebRepository() {
        return this.webRepository;
    }

    public BugTracker getWebBugtracker() {
        return this.webBugtracker;
    }

    /**
     * Creates an empty report page.
     * @param fileName The page's file name, relative to the root, 
     * 		<em>without</em> file extension
     * @param shortTitle A short title for use in navigation links
     * @param fullTitle The full title for the headline
     * @return An empty page according to the specifications
     */
    public Page createPage(final String fileName, final String shortTitle, final String fullTitle) {
        return new Page(this, fileName, shortTitle, fullTitle);
    }

    /**
     * Writes a chart image file.
     * @param fileName The file's name, relative to the root.
     * @param title The chart's title
     * @param chart The JFreeChart representation
     * @param size Width and height in pixels
     * @return An object representing the file
     */
    public ChartImage createChartImage(final String fileName, final String title, final JFreeChart chart, final Dimension size) {
        final ChartImage img = new ChartImage(this.rootDirectory, fileName, title, chart, size);
        img.write();
        return img;
    }

    /**
     * Copies a file from a URL into the report.
     * @param source The source file
     * @param destinationFilename The destination, relative to the 
     * 		report root, without initial slash.
     */
    public void copyFileIntoReport(final URL source, final String destinationFilename) {
        if (source == null) {
            throw new NullPointerException("Source was null");
        }
        InputStream stream = null;
        try {
            stream = source.openStream();
            FileUtils.copyFile(stream, new File(this.rootDirectory + destinationFilename));
            stream.close();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean isDeveloper(final Author author) {
        return !this.nonDeveloperLogins.contains(author.getName());
    }

    public String getCharSet() {
        return charSet;
    }
}

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
    
	$RCSfile: ConfigurationOptions.java,v $
	$Date: 2009/08/21 23:06:51 $ 
*/
package net.sf.statcvs.output;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.statcvs.pages.MarkupSyntax;
import net.sf.statcvs.util.FilePatternMatcher;
import net.sf.statcvs.util.FileUtils;
import net.sf.statcvs.weblinks.bugs.BugTracker;
import net.sf.statcvs.weblinks.bugs.Bugzilla;
import net.sf.statcvs.weblinks.bugs.Mantis;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jpf.statsvn.util.StringUtils;

/**
 * Class for storing all command line parameters. The parameters
 * are set by the {@link net.sf.statcvs.Main#main} method. Interested classes
 * can read all parameter values from here.
 * 
 * TODO: Should be moved to more appropriate package and made non-public
 * 
 * @author jentzsch
 * @version $Id: ConfigurationOptions.java,v 1.40 2009/08/21 23:06:51 benoitx Exp $
 */
public class ConfigurationOptions {

    private static final String LOGGING_CONFIG_DEFAULT = "logging.properties";
    private static final String LOGGING_CONFIG_VERBOSE = "logging-verbose.properties";
    private static final String LOGGING_CONFIG_DEBUG = "logging-debug.properties";

    private static String headerUrl = null;
    private static String footerUrl = null;
    //modify by wupf
    private static String svn_xml_log_file = null;
    private static String checkedOutDirectory = null;
    private static String projectName = null;
    private static String outputDir = "";
    private static String loggingProperties = LOGGING_CONFIG_DEFAULT;
    private static String notesFile = null;
    private static String notes = null;

    private static FilePatternMatcher includePattern = null;
    private static FilePatternMatcher excludePattern = null;

    private static Collection nonDeveloperLogins = new ArrayList();
    private static boolean enableTwitterButton = true;

    private static CssHandler cssHandler = new DefaultCssHandler("objectlab-statcvs.css");
    private static String charSet = "ISO-8859-1";
    private static WebRepositoryIntegration webRepository = null;
    private static Pattern symbolicNamesPattern;

    private static BugTracker webBugTracker = BugTracker.NO_BUG_TRACKER;
    private static String outputFormat = "html";
    private static Properties properties = new Properties();


    private static String  svn_filepath= "";

    static {
        XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());
        BarRenderer.setDefaultBarPainter(new StandardBarPainter());
    }

    public static String getCharSet() {
        return charSet;
    }

    public static void setCharSet(final String cs) {
        charSet = cs;
    }

    /**
     * returns the {@link CssHandler}
     * @return the CssHandler
     */
    public static CssHandler getCssHandler() {
        return cssHandler;
    }

    /**
     * Method getProjectName.
     * @return String name of the project
     */
    public static String getProjectName() {
        return projectName;
    }

    /**
     * Method getCheckedOutDirectory.
     * @return String name of the checked out directory
     */
    public static String getCheckedOutDirectory() {
        return checkedOutDirectory;
    }

    /**
     * Method getLogfilename.
     * @return String name of the logfile to be parsed
     */
    public static String getLogFileName() {
        return svn_xml_log_file;
    }

    /**
     * Returns the outputDir.
     * @return String output Directory
     */
    public static String getOutputDir() {
        return outputDir;
    }

    /**
     * Returns the report notes (from "-notes filename" switch) or <tt>null</tt>
     * if not specified
     * @return the report notes
     */
    public static String getNotes() {
        return notes;
    }

    /**
     * Returns a {@link WebRepositoryIntegration} object if the user
     * has specified a URL to one. <tt>null</tt> otherwise.
     * @return the web repository
     */
    public static WebRepositoryIntegration getWebRepository() {
        return webRepository;
    }

    /**
     * Sets the checkedOutDirectory.
     * @param checkedOutDirectory The checkedOutDirectory to set
     * @throws ConfigurationException if directory does not exist
     */
    public static void setCheckedOutDirectory(final String checkedOutDirectory) throws ConfigurationException {
        final File directory = new File(checkedOutDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new ConfigurationException("directory does not exist: " + checkedOutDirectory);
        }
        ConfigurationOptions.checkedOutDirectory = checkedOutDirectory;
    }

    /**
     * Sets the cssFile. Currently, the css file can be any local file or
     * a HTTP URL. If it is a local file, a copy will be included in the
     * output directory. If this method is never called, a default CSS file
     * will be generated in the output directory.
     *
     * @param cssFile The cssFile to set
     * @throws ConfigurationException if the specified CSS file can not be
     * accessed from local file system or from URL source, or if the specified
     * CSS file is local and does not exist
     */
    public static void setCssFile(final String cssFile) throws ConfigurationException {
        try {
            final URL url = new URL(cssFile);
            //            if (!url.getProtocol().equals("http")) {
            //                throw new ConfigurationException("Only HTTP URLs or local files allowed for -css");
            //            }
            cssHandler = new UrlCssHandler(url);
        } catch (final MalformedURLException isLocalFile) {
            cssHandler = new LocalFileCssHandler(cssFile);
        }
        cssHandler.checkForMissingResources();
    }

    /**
     * Sets the logFileName.
     * @param logFileName The logFileName to set
     * @throws ConfigurationException if the file does not exist
     */
    public static void setLogFileName(final String logFileName) throws ConfigurationException {
        final File inputFile = new File(logFileName);
        if (!inputFile.exists()) {
            throw new ConfigurationException("Specified logfile not found: " + logFileName);
        }
        ConfigurationOptions.svn_xml_log_file = logFileName;
    }

    /**
     * Sets the outputDir.
     * @param outputDir The outputDir to set
     * @throws ConfigurationException if the output directory cannot be created
     */
    public static void setOutputDir(String outputDir) throws ConfigurationException {
        if (!outputDir.endsWith(FileUtils.getDirSeparator())) {
            outputDir += FileUtils.getDefaultDirSeparator();
        }
        final File outDir = new File(outputDir);
        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new ConfigurationException("Can't create output directory: " + outputDir);
        }
        ConfigurationOptions.outputDir = outputDir;
    }

    /**
     * Sets the name of the notes file. The notes file will be included
     * on the index page of the output. It must contain a valid
     * block-level HTML fragment (for example
     * <tt>"&lt;p&gt;Some notes&lt;/p&gt;"</tt>) 
     * @param notesFile a local filename
     * @throws ConfigurationException if the file is not found or can't be read
     */
    public static void setNotesFile(final String notesFile) throws ConfigurationException {
        final File f = new File(notesFile);
        if (!f.exists()) {
            throw new ConfigurationException("Notes file not found: " + notesFile);
        }
        if (!f.canRead()) {
            throw new ConfigurationException("Can't read notes file: " + notesFile);
        }
        ConfigurationOptions.notesFile = notesFile;
        try {
            notes = readNotesFile();
        } catch (final IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    /**
     * Sets the URL to a <a href="http://viewcvs.sourceforge.net/">ViewCVS</a>
     * web-based CVS browser. This must be the URL at which the checked-out
     * module's root can be viewed in ViewCVS.
     * @param url URL to a ViewCVS repository
     */
    public static void setViewCvsURL(final String url) {
        ConfigurationOptions.webRepository = new ViewCvsIntegration(url);
    }

    /**
     * Sets the URL to a 
     * <a href="http://www.freebsd.org/projects/cvsweb.html">cvsweb</a>
     * web-based CVS browser. This must be the URL at which the checked-out
     * module's root can be viewed in cvsweb.
     * @param url URL to a cvsweb repository
     */
    public static void setCvswebURL(final String url) {
        ConfigurationOptions.webRepository = new CvswebIntegration(url);
    }

    /**
     * Sets the URL to a <a href="http://www.horde.org/chora/">Chora</a>
     * web-based CVS browser. This must be the URL at which the checked-out
     * module's root can be viewed in Chora.
     * @param url URL to a cvsweb repository
     */
    public static void setChoraURL(final String url) {
        ConfigurationOptions.webRepository = new ChoraIntegration(url);
    }

    /**
     * Sets the URL to a <a href="http://www.jcvs.org/jcvsweb/">JCVSWeb</a>
     * web-based CVS browser. This must be the URL at which the checked-out
     * module's root can be viewed in JCVSWeb.
     * @param url URL to a JCVSWeb repository
     */
    public static void setJCVSWebURL(final String url) {
        ConfigurationOptions.webRepository = new JCVSWebIntegration(url);
    }

    /**
     * Sets the URL to a <a href="http://www.viewvc.org/">ViewVC</a> web-based CVS/SVN browser. 
     * This must be the URL at which the checked-out module's
     * root can be viewed in ViewVC.
     * 
     * @param url
     *            URL to a ViewVC repository
     */
    public static void setViewVcURL(final String url) {
        ConfigurationOptions.webRepository = new ViewVcIntegration(url);
    }

    /**
     * Sets the URL to a <a href="http://trac.edgewall.org/wiki/TracBrowser">Trac</a> web-based SVN browser and issue tracking. 
     * This must be the URL at which the checked-out module's
     * root can be viewed in Trac 
     * 
     * @param url
     *            URL to a Trac website
     */
    public static void setViewTracURL(final String url) {
        ConfigurationOptions.webRepository = new TracIntegration(url);
    }

    /**
     * Sets a project title to be used in the reports
     * @param projectName The project title to be used in the reports
     */
    public static void setProjectName(final String projectName) {
        ConfigurationOptions.projectName = projectName;
    }

    /**
     * Gets the name of the logging properties file
     * @return the name of the logging properties file
     */
    public static String getLoggingProperties() {
        return loggingProperties;
    }

    /**
     * Sets the logging level to verbose
     */
    public static void setVerboseLogging() {
        ConfigurationOptions.loggingProperties = LOGGING_CONFIG_VERBOSE;
    }

    /**
     * Sets the logging level to debug
     */
    public static void setDebugLogging() {
        ConfigurationOptions.loggingProperties = LOGGING_CONFIG_DEBUG;
    }

    private static String readNotesFile() throws IOException {
        BufferedReader r = null;
        final StringBuffer result = new StringBuffer();
        try {
            r = new BufferedReader(new FileReader(notesFile));
            String line = r.readLine();
            while (line != null) {
                result.append(line);
                line = r.readLine();
            }
        } finally {
            if (r != null) {
                r.close();
            }
        }
        return result.toString();
    }

    /**
     * Sets a file include pattern list. Only files matching one of the
     * patterns will be included in the analysis.
     * @param patternList a list of Ant-style wildcard patterns, seperated
     *                    by : or ;
     * @see net.sf.statcvs.util.FilePatternMatcher
     */
    public static void setIncludePattern(final String patternList) {
        includePattern = new FilePatternMatcher(patternList);
    }

    /**
     * Sets a file exclude pattern list. Files matching any of the
     * patterns will be excluded from the analysis.
     * @param patternList a list of Ant-style wildcard patterns, seperated
     *                    by : or ;
     * @see net.sf.statcvs.util.FilePatternMatcher
     */
    public static void setExcludePattern(final String patternList) {
        excludePattern = new FilePatternMatcher(patternList);
    }

    /**
     * @return Returns the excludePattern.
     */
    public static FilePatternMatcher getExcludePattern() {
        return excludePattern;
    }

    /**
     * @return Returns the includePattern.
     */
    public static FilePatternMatcher getIncludePattern() {
        return includePattern;
    }

    public static void setSymbolicNamesPattern(final String symbolicNamesPattern) throws ConfigurationException {
        try {
            ConfigurationOptions.symbolicNamesPattern = Pattern.compile(symbolicNamesPattern);
        } catch (final PatternSyntaxException e) {
            throw new ConfigurationException("Invalid regular expression for tags: " + e.getLocalizedMessage());
        }
    }

    public static Pattern getSymbolicNamesPattern() {
        return symbolicNamesPattern;
    }

    public static void setBugzillaUrl(final String bugzillaUrl) {
        ConfigurationOptions.webBugTracker = new Bugzilla(bugzillaUrl);
    }

    public static void setMantisUrl(final String mantisUrl) {
        ConfigurationOptions.webBugTracker = new Mantis(mantisUrl);
    }

    public static BugTracker getWebBugtracker() {
        return ConfigurationOptions.webBugTracker;
    }

    public static void setOutputFormat(final String outputFormat) throws ConfigurationException {
        if (!"html".equals(outputFormat) && !"xdoc".equals(outputFormat) && !"xml".equals(outputFormat)) {
            throw new ConfigurationException("Invalid output format, use only 'html', 'xdoc' or 'xml'");
        }
        ConfigurationOptions.outputFormat = outputFormat;
    }

    public static String getOutputFormat() {
        return outputFormat;
    }

    public static MarkupSyntax getMarkupSyntax() {
        if ("xdoc".equals(outputFormat)) {
            return ReportConfig.XDOC;
        } else if ("xml".equals(outputFormat)) {
            return ReportConfig.XML;
        }
        return ReportConfig.HTML;
    }

    public static void setWebRepositoryIntegration(final WebRepositoryIntegration webRepo) {
        webRepository = webRepo;
    }

    /**
    * Allow change between css that are shipped with the tool.
    * @param cssName statcvs.css or objectlab-statcvs-xdoc.css
    */
    public static void setDefaultCssFile(final String cssName) {
        cssHandler = new DefaultCssHandler(cssName);
    }

    /**
     * Excludes a login name from charts and reports that compare
     * several developers. Useful to reduce the noise from admin
     * accounts.
     * @param loginName A login name
     */
    public static void addNonDeveloperLogin(final String loginName) {
        nonDeveloperLogins.add(loginName);
    }

    /**
     * Gets login names that should be excluded from charts and
     * reports that compare several developers.
     */
    public static Collection getNonDeveloperLogins() {
        return nonDeveloperLogins;
    }

    /**
     * Set the config file that may contain user details.
     * @param propertiesFilename
     */
    public static void setConfigFile(final String propertiesFilename) throws ConfigurationException {
        if (propertiesFilename != null) {
            InputStream is = null;
            try {
                is = new FileInputStream(propertiesFilename);
                properties.load(is);
            } catch (final FileNotFoundException e) {
                throw new ConfigurationException("Unable to find the configuration file " + propertiesFilename);
            } catch (final IOException e) {
                throw new ConfigurationException("Problem reading the configuration file " + propertiesFilename);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (final IOException e) {
                        throw new ConfigurationException("Problem closing stream to the configuration file " + propertiesFilename);
                    }
                }
            }
        }
    }

    /**
     * The config properties.
     * @return
     */
    public static Properties getConfigProperties() {
        return properties;
    }

    /**
     * Return a String prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static String getConfigStringProperty(final String propName, final String defaultValue) {
        if (properties != null) {
            return properties.getProperty(propName, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Return a list of String prop.
     * @param propName
     * @param defaultValue
     * @return List (could be empty)
     */
    public static List getConfigStringListProperty(final String propName, final String defaultValue) {
        if (properties != null) {
            return StringUtils.listify(properties.getProperty(propName, defaultValue), ",");
        }
        return StringUtils.listify(defaultValue, ",");
    }

    public static String getConfigStringProperty(final String propName, final String fallBackPropName, final String defaultValue) {
        if (properties != null) {
            final String val = properties.getProperty(propName);
            if (val != null) {
                return val;
            } else {
                return properties.getProperty(fallBackPropName, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Return a Integer prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Integer getConfigIntegerProperty(final String propName, final Integer defaultValue) {
        if (properties != null) {
            final String val = properties.getProperty(propName);
            if (val != null) {
                try {
                    return Integer.valueOf(val);
                } catch (final NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Return a Float prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Float getConfigIntegerProperty(final String propName, final Float defaultValue) {
        if (properties != null) {
            final String val = properties.getProperty(propName);
            if (val != null) {
                try {
                    return Float.valueOf(val);
                } catch (final NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Return a String prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Color getConfigColorProperty(final String propName, final Color defaultValue) {
        if (properties != null) {
            String val = properties.getProperty(propName);
            if (val != null) {
                if (val.startsWith("#")) {
                    val = val.substring(1);
                }
                val = val.toLowerCase();
                if (val.length() > 6) {
                    return defaultValue;
                }
                return new Color(Integer.parseInt(val, 16));
            }
        }
        return defaultValue;
    }

    /**
     * Return a Integer prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Integer getConfigIntegerProperty(final String propName, final String fallBackPropName, final Integer defaultValue) {
        if (properties != null) {
            final String val = properties.getProperty(propName);
            if (val != null) {
                try {
                    return Integer.valueOf(val);
                } catch (final NumberFormatException e) {
                    return defaultValue;
                }
            } else {
                return getConfigIntegerProperty(fallBackPropName, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Return a Float prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Float getConfigFloatProperty(final String propName, final String fallBackPropName, final Float defaultValue) {
        if (properties != null) {
            final String val = properties.getProperty(propName);
            if (val != null) {
                try {
                    return Float.valueOf(val);
                } catch (final NumberFormatException e) {
                    return defaultValue;
                }
            } else {
                return getConfigIntegerProperty(fallBackPropName, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Return a Boolean prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Boolean getConfigBooleanProperty(final String propName, final String fallBackPropName, final Boolean defaultValue) {
        if (properties != null) {
            String val = properties.getProperty(propName);
            if (val != null) {
                try {
                    return Boolean.valueOf(val);
                } catch (final NumberFormatException e) {
                    return defaultValue;
                }
            } else {
                val = properties.getProperty(fallBackPropName);
                if (val != null) {
                    return Boolean.valueOf(val);
                }
            }
        }
        return defaultValue;
    }

    /**
     * Return a Color prop.
     * @param propName
     * @param defaultValue
     * @return
     */
    public static Color getConfigColorProperty(final String propName, final String fallBackPropName, final Color defaultValue) {
        if (properties != null) {
            String val = properties.getProperty(propName);
            if (val != null) {
                if (val.startsWith("#")) {
                    val = val.substring(1);
                }
                val = val.toLowerCase();
                if (val.length() > 6) {
                    return defaultValue;
                }
                return new Color(Integer.parseInt(val, 16));
            } else {
                return getConfigColorProperty(fallBackPropName, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Enable/disable the Twitter "Tweet This" Buttons.
     * @param value
     */
    public static void setEnableTwitterButton(boolean value) {
        enableTwitterButton = value;
    }

    public static boolean isEnableTwitterButton() {
        return enableTwitterButton;
    }

    public static String getHeaderUrl() {
        return headerUrl;
    }

    public static void setHeaderUrl(String headerUrl) {
        ConfigurationOptions.headerUrl = headerUrl;
    }

    public static String getFooterUrl() {
        return footerUrl;
    }

    public static void setFooterUrl(String footerUrl) {
        ConfigurationOptions.footerUrl = footerUrl;
    }
}

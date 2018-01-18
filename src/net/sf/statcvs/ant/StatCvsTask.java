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
    
	$RCSfile: StatCvsTask.java,v $
	$Date: 2009/06/02 15:35:43 $ 
*/
package net.sf.statcvs.ant;

import java.io.IOException;

import net.sf.statcvs.Main;
import net.sf.statcvs.input.LogSyntaxException;
import net.sf.statcvs.output.ConfigurationException;
import net.sf.statcvs.output.ConfigurationOptions;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for running statcvs. 
 * 
 * @author Andy Glover
 * @author Richard Cyganiak
 */
public class StatCvsTask extends Task {
    private String title;
    private String logFile;
    private String pDir;
    private String outDir;
    private String cssFile;
    private String notesFile;
    private String viewcvs;
    private String viewvc;
    private String cvsweb;
    private String chora;
    private String jcvsweb;
    private String bugzilla;
    private String mantis;
    private String include = null;
    private String exclude = null;
    private String tags;
    private String format;
    private String nonDeveloperLogin;
    private String charset;
    private String configFile;
    private boolean disableTwitterButton = false;
    
    /**
     * Constructor for StatCvsTask.
     */
    public StatCvsTask() {
        super();
    }

    /**
     * Runs the task
     * @throws BuildException if an IO Error occurs
     */
    public void execute() throws BuildException {
        try {
            this.initProperties();
            Main.generateDefaultHTMLSuite();
        } catch (final ConfigurationException e) {
            throw new BuildException(e.getMessage());
        } catch (final IOException e) {
            throw new BuildException(e.getMessage());
        } catch (final LogSyntaxException e) {
            throw new BuildException(e.getMessage());
        }
    }

    /**
     * method initializes the ConfigurationOptions object with
     * received values. 
     */
    protected void initProperties() throws ConfigurationException {

        // required params
        ConfigurationOptions.setLogFileName(this.logFile);
        ConfigurationOptions.setCheckedOutDirectory(this.pDir);

        // optional params
        if (this.title != null) {
            ConfigurationOptions.setProjectName(this.title);
        }
        if (this.outDir != null) {
            ConfigurationOptions.setOutputDir(this.outDir);
        }
        if (cssFile != null) {
            ConfigurationOptions.setCssFile(this.cssFile);
        }
        if (notesFile != null) {
            ConfigurationOptions.setNotesFile(this.notesFile);
        }
        if (viewcvs != null) {
            ConfigurationOptions.setViewCvsURL(this.viewcvs);
        }
        if (viewvc != null) {
            ConfigurationOptions.setViewVcURL(this.viewvc);
        }
        if (cvsweb != null) {
            ConfigurationOptions.setCvswebURL(this.cvsweb);
        }
        if (chora != null) {
            ConfigurationOptions.setChoraURL(this.chora);
        }
        if (jcvsweb != null) {
            ConfigurationOptions.setJCVSWebURL(this.jcvsweb);
        }
        if (bugzilla != null) {
            ConfigurationOptions.setBugzillaUrl(this.bugzilla);
        }
        if (mantis != null) {
            ConfigurationOptions.setMantisUrl(this.mantis);
        }
        if (include != null) {
            ConfigurationOptions.setIncludePattern(this.include);
        }
        if (exclude != null) {
            ConfigurationOptions.setExcludePattern(this.exclude);
        }
        if (tags != null) {
            ConfigurationOptions.setSymbolicNamesPattern(this.tags);
        }
        if (format != null) {
            ConfigurationOptions.setOutputFormat(this.format);
        }
        if (charset != null) {
            ConfigurationOptions.setCharSet(this.charset);
        }
        if (nonDeveloperLogin != null) {
            ConfigurationOptions.addNonDeveloperLogin(this.nonDeveloperLogin);
        }
        if (configFile != null) {
            ConfigurationOptions.setConfigFile(this.configFile);
        }
        ConfigurationOptions.setEnableTwitterButton(!disableTwitterButton);
    }

    /**
     * @param logFile String representing the cvs log file
     */
    public void setLog(final String logFile) {
        this.logFile = logFile;
    }

    /**
     * @param modDir String representing the directory containing the CVS project
     */
    public void setPath(final String modDir) {
        this.pDir = modDir;
    }

    /**
     * @param outDir String representing the output directory of the report
     */
    public void setOutputDir(final String outDir) {
        this.outDir = outDir;
    }

    /**
     * Specifies files to include in the analysis.
     * @param include a list of Ant-style wildcard patterns, delimited by : or ;
     * @see net.sf.statcvs.util.FilePatternMatcher
     */
    public void setInclude(final String include) {
        this.include = include;
    }

    /**
     * Specifies files to exclude from the analysis.
     * @param exclude a list of Ant-style wildcard patterns, delimited by : or ;
     * @see net.sf.statcvs.util.FilePatternMatcher
     */
    public void setExclude(final String exclude) {
        this.exclude = exclude;
    }

    /**
     * Specifies regular expression to include tag to lines
     * of code graph.
     * @param tags regular expression to included tags names.
     */
    public void setTags(final String tags) {
        this.tags = tags;
    }

    /**
     * @param title String representing the title to be used in the reports
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @param cssFile String representing the CSS file to use for the report
     */
    public void setCss(final String cssFile) {
        this.cssFile = cssFile;
    }

    /**
     * @param notesFile String representing the notes file to include on
     * the report's index page
     */
    public void setNotes(final String notesFile) {
        this.notesFile = notesFile;
    }

    /**
     * @param viewcvs String representing the URL of a ViewCVS installation
     */
    public void setViewCVS(final String viewcvs) {
        this.viewcvs = viewcvs;
    }

    /**
     * @param viewvc String representing the URL of a ViewVC installation
     */
    public void setViewVC(final String viewvc) {
        this.viewvc = viewvc;
    }

    /**
     * @param cvsweb String representing the URL of a cvsweb installation
     */
    public void setCvsweb(final String cvsweb) {
        this.cvsweb = cvsweb;
    }

    /**
     * @param chora String representing the URL of a Chora installation
     */
    public void setChora(final String chora) {
        this.chora = chora;
    }

    /**
     * @param jcvsweb String representing the URL of a JCVSWeb installation
     */
    public void setJCVSWeb(final String jcvsweb) {
        this.jcvsweb = jcvsweb;
    }

    /**
     * @param bugzilla String representing the URL of a Bugzilla installation
     */
    public void setBugzilla(final String bugzilla) {
        this.bugzilla = bugzilla;
    }

    /**
     * @param mantis String representing the URL of a Mantis installation
     */
    public void setMantis(final String mantis) {
        this.mantis = mantis;
    }

    /**
     * @param generateXDoc Generate XDoc or HTML?
     */
    public void setXDoc(final boolean generateXDoc) {
        this.format = generateXDoc ? "xdoc" : "html";
    }

    /**
     * @param format "<tt>xdoc</tt>" or "<tt>html</tt>"
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * TODO: This supports just a single value, but should support multiple
     *       login names -- how?
     * @param nonDeveloperLogin A login name to be excluded from developer
     * 		lists
     */
    public void setNoDeveloper(final String nonDeveloperLogin) {
        this.nonDeveloperLogin = nonDeveloperLogin;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setDisableTwitterButton(boolean disableTwitterButton) {
        this.disableTwitterButton = disableTwitterButton;
    }
}
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
    
	$RCSfile: CommandLineParser.java,v $
	Created on $Date: 2009/08/19 22:11:15 $ 
*/
package net.sf.statcvs.output;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Takes a command line, like given to the {@link net.sf.statcvs.Main#main} method,
 * and turns it into a {@link ConfigurationOptions} object.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: CommandLineParser.java,v 1.29 2009/08/19 22:11:15 benoitx Exp $
 */
public class CommandLineParser {
    private static final Logger logger = LogManager.getLogger();
    
    private final String[] argsArray;
    private final List args_list = new ArrayList();
    private int argCount = 0;
    private boolean givenCss = false;

    /**
     * Constructor for CommandLineParser
     * 
     * @param args the command line parameters
     */
    public CommandLineParser(final String[] args) {
        argsArray = args;
    }

    /**
     * Parses the command line and sets the options (as static
     * fields in {@link ConfigurationOptions}).
     * 
     * @throws ConfigurationException if errors are present on the command line
     */
    public void parse() throws ConfigurationException {
        for (int i = 0; i < argsArray.length; i++) {
            args_list.add(argsArray[i]);
        }
        while (!args_list.isEmpty()) {
            final String currentArg = popNextArg();
            logger.debug(currentArg);
            if (currentArg.startsWith("-")) {
                parseSwitch(currentArg.substring(1));
            } else {
                parseArgument(currentArg);
            }
        }
        checkForRequiredArgs();
    }

    protected String popNextArg() {
        return (String) args_list.remove(0);
    }

    protected void parseSwitch(final String switchName) throws ConfigurationException {
        final String s = switchName.toLowerCase();
        if (s.equals("css")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -css");
            }
            ConfigurationOptions.setCssFile(popNextArg());
            givenCss = true;
        } else if (s.equals("output-dir")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -output-dir");
            }
            ConfigurationOptions.setOutputDir(popNextArg());
        } else if (s.equals("verbose")) {
            ConfigurationOptions.setVerboseLogging();
        } else if (s.equals("debug")) {
            ConfigurationOptions.setDebugLogging();
        } else if (s.equals("notes")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -notes");
            }
            ConfigurationOptions.setNotesFile(popNextArg());
        } else if (s.equals("viewcvs")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -viewcvs");
            }
            ConfigurationOptions.setViewCvsURL(popNextArg());
        } else if (s.equals("viewvc")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -viewvc");
            }
            ConfigurationOptions.setViewVcURL(popNextArg());
        } else if (s.equals("trac")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -trac");
            }
            ConfigurationOptions.setViewTracURL(popNextArg());
        } else if (s.equals("cvsweb")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -cvsweb");
            }
            ConfigurationOptions.setCvswebURL(popNextArg());
        } else if (s.equals("chora")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -chora");
            }
            ConfigurationOptions.setChoraURL(popNextArg());
        } else if (s.equals("jcvsweb")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -jcvsweb");
            }
            ConfigurationOptions.setJCVSWebURL(popNextArg());
        } else if (s.equals("include")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -include");
            }
            ConfigurationOptions.setIncludePattern(popNextArg());
        } else if (s.equals("exclude")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -exclude");
            }
            ConfigurationOptions.setExcludePattern(popNextArg());
        } else if (s.equals("title")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -title");
            }
            ConfigurationOptions.setProjectName(popNextArg());
        } else if (s.equals("tags")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -tags");
            }
            ConfigurationOptions.setSymbolicNamesPattern(popNextArg());
        } else if (s.equals("bugzilla")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -bugzilla");
            }
            ConfigurationOptions.setBugzillaUrl(popNextArg());
        } else if (s.equals("mantis")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -mantis");
            }
            ConfigurationOptions.setMantisUrl(popNextArg());
        } else if (s.equals("config-file")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -config-file");
            }
            ConfigurationOptions.setConfigFile(popNextArg());
        } else if (s.equals("charset")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -charset");
            }
            ConfigurationOptions.setCharSet(popNextArg());
        } else if (s.equals("headerurl")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -headerUrl");
            }
            ConfigurationOptions.setHeaderUrl(popNextArg());
        } else if (s.equals("footerurl")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -footerUrl");
            }
            ConfigurationOptions.setFooterUrl(popNextArg());
        } else if (s.equals("xdoc")) {
            ConfigurationOptions.setOutputFormat("xdoc");
            if (!this.givenCss) {
                // set the default to the XDOC css.
                ConfigurationOptions.setDefaultCssFile("objectlab-statcvs-xdoc.css");
            }
        } else if (s.equals("disable-twitter-button")) {
            ConfigurationOptions.setEnableTwitterButton(false);
        } else if (s.equals("xml")) {
            /*
             * edited by Nilendra
             * test for the input parameter '-xml'
             */
            ConfigurationOptions.setOutputFormat("xml");
        } else if (s.equals("format")) { // This is undocumented in StatCVS, is for StatSVN compatibility
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -format");
            }
            final String format = popNextArg();
            if ("xdoc".equals(format) && !givenCss) {
                // set the default to the XDOC css.
                ConfigurationOptions.setDefaultCssFile("objectlab-statcvs-xdoc.css");
            }
            ConfigurationOptions.setOutputFormat(format);
        } else if (s.equals("no-developer")) {
            if (args_list.isEmpty()) {
                throw new ConfigurationException("Missing argument for -no-developer");
            }
            ConfigurationOptions.addNonDeveloperLogin(popNextArg());
        }  else if (!doChildrenSwitch(s)) {
            throw new ConfigurationException("Unrecognized option -" + s);
        }
    }

    /**
     * Give the argument to children classes that may have a way to recognise it.
     * @param s
     * @return
     */
    protected boolean doChildrenSwitch(final String s) throws ConfigurationException {
        return false;
    }

    private void parseArgument(final String arg) throws ConfigurationException {
        argCount++;
        switch (argCount) {
        case 1:
            ConfigurationOptions.setLogFileName(arg);
            break;
        case 2:
            ConfigurationOptions.setCheckedOutDirectory(arg);
            break;
        default:
            throw new ConfigurationException("Too many arguments");
        }
    }

    protected void checkForRequiredArgs() throws ConfigurationException {
        switch (argCount) {
        case 0:
            throw new ConfigurationException("Not enough arguments - <logfile> is missing");
        case 1:
            throw new ConfigurationException("Not enough arguments - <directory> is missing");
        }
    }

    protected int getArgCount() {
        return argCount;
    }

    protected boolean isArgsEmpty() {
        return args_list.isEmpty();
    }
}
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

	$RCSfile: Main.java,v $
	Created on $Date: 2009/08/05 16:32:10 $
*/
package net.sf.statcvs;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;



import net.sf.statcvs.input.LogSyntaxException;

import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.CommandLineParser;
import net.sf.statcvs.output.ConfigurationException;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.pages.Page;
import net.sf.statcvs.pages.ReportSuiteMaker;

/**
 * StatCvs Main Class; it starts the application and controls command-line
 * related stuff
 * @author Lukasz Pekacki
 * @author Richard Cyganiak
 * @version $Id: Main.java,v 1.68 2009/08/05 16:32:10 benoitx Exp $
 */
public class Main {
    private static Logger logger = Logger.getLogger("net.sf.statcvs");
    private static LogManager lm = LogManager.getLogManager();

    /**
     * Main method of StatCvs
     * @param args command line options
     */
    public static void main(final String[] args) {
        System.out.println(Messages.getString("PROJECT_NAME") + Messages.NL);

        if (args.length == 0) {
            printProperUsageAndExit();
        }
        if (args.length == 1) {
            final String arg = args[0].toLowerCase();
            if (arg.equals("-h") || arg.equals("-help")) {
                printProperUsageAndExit();
            } else if (arg.equals("-version")) {
                printVersionAndExit();
            }
        }

        try {
            new CommandLineParser(args).parse();
            generateDefaultHTMLSuite();
        } catch (final ConfigurationException cex) {
            System.err.println(cex.getMessage());
            System.exit(1);
        } catch (final LogSyntaxException lex) {
            printLogErrorMessageAndExit(lex.getMessage());
        } catch (final IOException ioex) {
            printIoErrorMessageAndExit(ioex.getMessage());
        } catch (final OutOfMemoryError oome) {
            printOutOfMemMessageAndExit();
        }
        System.exit(0);
    }

    private static void initLogManager(final String loggingProperties) {
        try {
            lm.readConfiguration(Main.class.getResourceAsStream(loggingProperties));
        } catch (final IOException e) {
            System.err.println("ERROR: Logging could not be initialized!");
        }
    }

    private static void printProperUsageAndExit() {
        System.out.println(
        //max. 80 chars
                //         12345678901234567890123456789012345678901234567890123456789012345678901234567890
                "Usage: java -jar statcvs.jar [options] <logfile> <directory>\n" + "\n" + "Required parameters:\n"
                        + "  <logfile>          path to the cvs logfile of the module\n"
                        + "  <directory>        path to the directory of the checked out module\n" + "\n" + "Some options:\n"
                        + "  -version           print version information and exit\n" + "  -output-dir <dir>  set directory where HTML suite will be saved\n"
                        + "  -include <pattern> include only files matching pattern, e.g. **/*.c;**/*.h\n"
                        + "  -exclude <pattern> exclude matching files, e.g. tests/**;docs/**\n"
                        + "  -tags <regexp>     show matching tags in lines of code chart, e.g. version-.*\n"
                        + "  -title <title>     set project title to be used in reports\n" + "  -xdoc              generate Maven XDoc instead of HTML\n"
                        + "  -trac <url>        integrate with Trac at <url>\n" + "  -xml               generate XML instead of HTML\n"
                        + "  -charset <charset> specify the charset to use for html/xdoc\n" + "  -verbose           print extra progress information\n"
                        + "  -viewcvs/viewvc/cvsweb/chora/jcvsweb/bugzilla/mantis <url>\n" + "                     add links to installation at <url>\n"
                        + "  -disable-twitter-button\n" + "\n" + "Full options list: http://statcvs.sf.net/manual");
        System.exit(1);
    }

    private static void printVersionAndExit() {
        System.out.println("Version " + Messages.getString("PROJECT_VERSION"));
        System.exit(1);
    }

    private static void printOutOfMemMessageAndExit() {
        System.err.println("OutOfMemoryError.");
        System.err.println("Try running java with the -mx option (e.g. -mx128m for 128Mb).");
        System.exit(1);
    }

    private static void printLogErrorMessageAndExit(final String message) {
        System.err.println("Logfile parsing failed.");
        System.err.println(message);
        System.exit(1);
    }

    private static void printIoErrorMessageAndExit(final String message) {
        System.err.println(message);
        System.exit(1);
    }

    /**
     * Generates HTML report. {@link net.sf.statcvs.output.ConfigurationOptions}
     * must be initialized before calling this method.
     * @throws LogSyntaxException if the logfile contains unexpected syntax
     * @throws IOException if some file can't be read or written
     * @throws ConfigurationException if a required ConfigurationOption was not set
     */
    public static void generateDefaultHTMLSuite() throws LogSyntaxException, IOException, ConfigurationException {

    }
}

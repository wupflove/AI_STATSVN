package org.jpf.statsvn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.statsvn.output.SvnConfigurationOptions;

import net.sf.statcvs.util.LookaheadReader;

/**
 * Utility class that verifies if the correct version of subversion is used.
 * 
 * @author Jean-Philippe Daigle &lt;jpdaigle@softwareengineering.ca&gt;
 * 
 * @version $Id: SvnStartupUtils.java 394 2009-08-10 20:08:46Z jkealey $
 */
public class SvnStartupUtils implements ISvnVersionProcessor {
    
    private static final Logger logger = LogManager.getLogger();
    
	private static final String SVN_VERSION_COMMAND = "svn --version";

	public static final String SVN_MINIMUM_VERSION = "1.3.0";

	public static final String SVN_MINIMUM_VERSION_DIFF_PER_REV = "1.4.0";

	private static final String SVN_VERSION_LINE_PATTERN = ".* [0-9]+\\.[0-9]+\\.[0-9]+.*";

	private static final String SVN_VERSION_PATTERN = "[0-9]+\\.[0-9]+\\.[0-9]+";


    protected ISvnProcessor processor;

    /**
     * Invokes various calls needed during StatSVN's startup, including the svn version command line.   
     */
    public SvnStartupUtils(ISvnProcessor processor) {
        this.processor = processor;
    }

    protected ISvnProcessor getProcessor() {
        return processor;
    }

	/* (non-Javadoc)
     * @see net.sf.statsvn.util.IVersionProcessor#checkSvnVersionSufficient()
     */
	public synchronized String checkSvnVersionSufficient() throws SvnVersionMismatchException {
		ProcessUtils pUtils = null;
		try {

			pUtils = ProcessUtils.call(SVN_VERSION_COMMAND);
			final InputStream istream = pUtils.getInputStream();
			final LookaheadReader reader = new LookaheadReader(new InputStreamReader(istream));

			while (reader.hasNextLine()) {
				final String line = reader.nextLine();
				if (line.matches(SVN_VERSION_LINE_PATTERN)) {
					// We have our version line
					final Pattern pRegex = Pattern.compile(SVN_VERSION_PATTERN);
					final Matcher m = pRegex.matcher(line);
					if (m.find()) {
						final String versionString = line.substring(m.start(), m.end());

						// we perform a simple string comparison against the version numbers
						if (versionString.compareTo(SVN_MINIMUM_VERSION) >= 0) {
							return versionString; // success
						} else {
							throw new SvnVersionMismatchException(versionString, SVN_MINIMUM_VERSION);
						}
					}
				}
			}

			if (pUtils.hasErrorOccured()) {
				throw new IOException(pUtils.getErrorMessage());
			}
		} catch (final IOException e) {
			logger.error(e);
		} catch (final RuntimeException e) {
			logger.error(e);
		} finally {
			if (pUtils != null) {
				try {
					pUtils.close();
				} catch (final IOException e) {
					logger.error(e);
				}
			}
		}

		throw new SvnVersionMismatchException();
	}

	/* (non-Javadoc)
     * @see net.sf.statsvn.util.IVersionProcessor#checkDiffPerRevPossible(java.lang.String)
     */
	public synchronized boolean checkDiffPerRevPossible(final String version) {
		// we perform a simple string comparison against the version numbers
		return version.compareTo(SVN_MINIMUM_VERSION_DIFF_PER_REV) >= 0;
	}
}

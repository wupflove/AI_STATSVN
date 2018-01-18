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
    
	$RCSfile: FilePatternMatcher.java,v $
	$Date: 2008/04/02 11:22:15 $
*/
package net.sf.statcvs.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * <p>Matches filenames against an Ant-style wildcard pattern list.</p>
 * 
 * <p>In short, ? matches one character, * matches zero or more characters
 * but no directory changes (it doesn't match / or \), and **
 * matches zero or more directory levels. If the wildcard pattern
 * ends in / or \, an implicit ** is added.</p>
 * 
 * <p>Several patterns can be specified, seperated by : or ;.</p>
 * 
 * <p>Everything is case sensitive. If you need case insensitive pattern
 * matching, use <tt>String.toLower()</tt> on the pattern and on the
 * candidate string.</p>
 *
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: FilePatternMatcher.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class FilePatternMatcher {
    private final String originalPattern;
    private final List patterns = new ArrayList();

    /**
     * Creates a matcher to match filenames against a specified
     * wildcard pattern
     * @param wildcardPattern an Ant-style wildcard pattern
     */
    public FilePatternMatcher(final String wildcardPattern) {
        this.originalPattern = wildcardPattern;
        final StringTokenizer tokenizer = new StringTokenizer(wildcardPattern, ":;");
        while (tokenizer.hasMoreTokens()) {
            patterns.add(Pattern.compile(buildRegex(tokenizer.nextToken())));
        }
    }

    /**
     * Matches a filename against the wildcard pattern.
     * @param filename a filename
     * @return <tt>true</tt> if the filename matches the pattern
     */
    public boolean matches(final String filename) {
        final Iterator it = patterns.iterator();
        while (it.hasNext()) {
            final Pattern regex = (Pattern) it.next();
            if (regex.matcher(filename).matches()) {
                return true;
            }
        }
        return false;
    }

    private String buildRegex(final String wildcardPattern) {
        String temp = wildcardPattern;
        temp = temp.replace('\\', '/');
        if (temp.endsWith("/")) {
            temp += "**";
        }
        // replace **/** with **
        temp = temp.replaceAll("\\*\\*/\\*\\*", "**");
        if ("**".equals(temp)) {
            return ".*";
        }
        // replace **/ at start with (.*/)? and /** at end with (/.*)?
        if (temp.startsWith("**/") && temp.endsWith("/**")) {
            final String inner = temp.substring(3, temp.length() - 3);
            return "(.*/)?" + buildInnerRegex(inner) + "(/.*)?";
        }
        if (temp.startsWith("**/")) {
            final String inner = temp.substring(3);
            return "(.*/)?" + buildInnerRegex(inner);
        }
        if (temp.endsWith("/**")) {
            final String inner = temp.substring(0, temp.length() - 3);
            return buildInnerRegex(inner) + "(/.*)?";
        }
        return buildInnerRegex(temp);
    }

    private String buildInnerRegex(final String wildcardPattern) {
        // replace /**/ with /(.*/)?
        final int pos = wildcardPattern.indexOf("/**/");
        if (pos > -1) {
            final String before = wildcardPattern.substring(0, pos);
            final String after = wildcardPattern.substring(pos + 4);
            return buildInnerRegex(before) + "/(.*/)?" + buildInnerRegex(after);
        }
        // replace ? with [^/] and * with [^/]*
        return wildcardPattern.replaceAll("\\?", "[^/]").replaceAll("\\*", "[^/]*");
    }

    public String toString() {
        return this.originalPattern;
    }
}

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
    
	$Name:  $ 
	Created on $Date: 2008/04/02 11:22:15 $ 
*/
package net.sf.statcvs.util;

import junit.framework.TestCase;

/**
 * Test cases for {@link FilePatternMatcher}
 *
 * @author Richard Cyganiak
 * @version $Id: FilePatternMatcherTest.java,v 1.2 2008/04/02 11:22:15 benoitx Exp $
 */
public class FilePatternMatcherTest extends TestCase {

    /**
     * Constructor for OutputUtilsTest.
     * @param arg0 input 
     */
    public FilePatternMatcherTest(final String arg0) {
        super(arg0);
    }

    /**
     * Test a pattern without wildcards
     */
    public void testSimplePattern() {
        final FilePatternMatcher fpm = new FilePatternMatcher("test");
        assertTrue(fpm.matches("test"));
        assertTrue(!fpm.matches("foo"));
        assertTrue(!fpm.matches("atest"));
        assertTrue(!fpm.matches("testa"));
        assertTrue(!fpm.matches("test/test"));
        assertTrue(!fpm.matches("/test"));
        assertTrue(!fpm.matches("test/"));
    }

    /**
     * Test a pattern with a ? wildcard, matching exactly one char
     */
    public void testQuestionMarkPattern1() {
        final FilePatternMatcher fpm = new FilePatternMatcher("test?");
        assertTrue(fpm.matches("test1"));
        assertTrue(fpm.matches("test2"));
        assertTrue(fpm.matches("test "));
        assertTrue(fpm.matches("test?"));
        assertTrue(fpm.matches("test*"));
        assertTrue(!fpm.matches("foo"));
        assertTrue(!fpm.matches("atest"));
        assertTrue(!fpm.matches("test11"));
        assertTrue(!fpm.matches("test/test"));
        assertTrue(!fpm.matches("/test"));
        assertTrue(!fpm.matches("test/"));
    }

    /**
     * Test a pattern with a ??? wildcard, matching exactly three chars
     */
    public void testQuestionMarkPattern2() {
        final FilePatternMatcher fpm = new FilePatternMatcher("???");
        assertTrue(fpm.matches("abc"));
        assertTrue(fpm.matches("123"));
        assertTrue(fpm.matches("   "));
        assertTrue(!fpm.matches("1234"));
        assertTrue(!fpm.matches("///"));
        assertTrue(!fpm.matches("/123"));
        assertTrue(!fpm.matches("123/"));
    }

    /**
     * Test a pattern with a * wildcard, matching any character sequence
     * not containing /
     */
    public void testAsteriskPattern1() {
        final FilePatternMatcher fpm = new FilePatternMatcher("*.java");
        assertTrue(fpm.matches(".java"));
        assertTrue(fpm.matches("AllTests.java"));
        assertTrue(fpm.matches("FilePatternMatcher.java"));
        assertTrue(!fpm.matches("foo"));
        assertTrue(!fpm.matches("java"));
        assertTrue(!fpm.matches("dir/AllTests.java"));
        assertTrue(!fpm.matches("AllTests.java/foo"));
    }

    /**
     * Test a pattern with a * wildcard, matching any character sequence
     * not containing /
     */
    public void testAsteriskPattern2() {
        final FilePatternMatcher fpm = new FilePatternMatcher("*");
        assertTrue(fpm.matches("AllTests.java"));
        assertTrue(!fpm.matches("foo/bar"));
    }

    /**
     * Test a pattern with a * wildcard, matching any character sequence
     * not containing /
     */
    public void testAsteriskPattern3() {
        final FilePatternMatcher fpm = new FilePatternMatcher("A*T**.java");
        assertTrue(fpm.matches("AT.java"));
        assertTrue(fpm.matches("AllTests.java"));
        assertTrue(!fpm.matches("All.java"));
        assertTrue(!fpm.matches("java"));
        assertTrue(!fpm.matches("A"));
        assertTrue(!fpm.matches("AllTests/.java"));
    }

    /**
     * Test a pattern ending with /**, matching nothing or any file within
     * any subdirectory
     */
    public void testDirWildcardEnd() {
        final FilePatternMatcher fpm = new FilePatternMatcher("src/**");
        assertTrue(fpm.matches("src"));
        assertTrue(fpm.matches("src/foo"));
        assertTrue(fpm.matches("src/foo/bar"));
        assertTrue(fpm.matches("src/src"));
        assertTrue(!fpm.matches("foo"));
        assertTrue(!fpm.matches("foo/src"));
        assertTrue(!fpm.matches("src1"));
        assertTrue(!fpm.matches("src1/foo"));
    }

    /**
     * Test a pattern ending with /, which is shorthand for /** and matches
     * the same files.
     */
    public void testImplicitDirWildcardEnd() {
        final FilePatternMatcher fpm = new FilePatternMatcher("src/");
        assertTrue(fpm.matches("src"));
        assertTrue(fpm.matches("src/foo"));
        assertTrue(fpm.matches("src/foo/bar"));
        assertTrue(fpm.matches("src/src"));
        assertTrue(!fpm.matches("foo"));
        assertTrue(!fpm.matches("foo/src"));
        assertTrue(!fpm.matches("src1"));
        assertTrue(!fpm.matches("src1/foo"));
    }

    /**
     * Test a pattern beginning with asterisk asterisk slash, matching
     * nothing or any directory
     */
    public void testDirWildcardBegin() {
        final FilePatternMatcher fpm = new FilePatternMatcher("**/AllTests.java");
        assertTrue(fpm.matches("AllTests.java"));
        assertTrue(fpm.matches("src/AllTests.java"));
        assertTrue(fpm.matches("src/foo/AllTests.java"));
        assertTrue(fpm.matches("AllTests.java/AllTests.java"));
        assertTrue(!fpm.matches("foo"));
        assertTrue(!fpm.matches("ReallyAllTests.java"));
        assertTrue(!fpm.matches("foo/bar"));
        assertTrue(!fpm.matches("foo/AllTests.java/bar"));
    }

    /**
     * directory wildcards ** are not allowed in combination with filenames,
     * so they are interpreted as two normal asterisk wildcards
     */
    public void testBogus1() {
        final FilePatternMatcher fpm = new FilePatternMatcher("**AllTests.java");
        assertTrue(fpm.matches("ReallyAllTests.java"));
        assertTrue(!fpm.matches("src/AllTests.java"));
    }

    /**
     * Test a pattern with a ** wildcard, matching zero or more directories
     */
    public void testDirWildcard() {
        final FilePatternMatcher fpm = new FilePatternMatcher("src/**/AllTests.java");
        assertTrue(fpm.matches("src/AllTests.java"));
        assertTrue(fpm.matches("src/foo/AllTests.java"));
        assertTrue(fpm.matches("src/foo/bar/AllTests.java"));
        assertTrue(!fpm.matches("srcAllTests.java"));
        assertTrue(!fpm.matches("1src/AllTests.java"));
        assertTrue(!fpm.matches("foo/src/AllTests.java"));
        assertTrue(!fpm.matches("src/AllTests.java1"));
    }

    /**
     * Test a really complex pattern
     */
    public void testComplexPattern() {
        final FilePatternMatcher fpm = new FilePatternMatcher("**/*o*/**/*a*/**/");
        assertTrue(fpm.matches("o/a"));
        assertTrue(fpm.matches("1o1/1a1"));
        assertTrue(fpm.matches("1/2/3/1o1/4/5/6/1a1/7/8/9"));
        assertTrue(!fpm.matches("1/2/3/a/4/5/6/o/7/8/9"));
    }

    /**
     * Test a pattern list with : as delimiter
     */
    public void testMultiplePatterns1() {
        final FilePatternMatcher fpm = new FilePatternMatcher("a:b:c");
        assertTrue(!fpm.matches("a:b:c"));
        assertTrue(fpm.matches("a"));
        assertTrue(fpm.matches("b"));
        assertTrue(fpm.matches("c"));
        assertTrue(!fpm.matches("d"));
    }

    /**
     * Test a pattern list with ; as delimiter
     */
    public void testMultiplePatterns2() {
        final FilePatternMatcher fpm = new FilePatternMatcher("a;b;c");
        assertTrue(!fpm.matches("a;b;c"));
        assertTrue(fpm.matches("a"));
        assertTrue(fpm.matches("b"));
        assertTrue(fpm.matches("c"));
        assertTrue(!fpm.matches("d"));
    }
}
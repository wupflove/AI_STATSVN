package net.sf.statcvs.pages;

import junit.framework.TestCase;

/**
 * Test cases for {link net.sf.statcvs.pages.HTML}
 *
 * @author Richard Cyganiak
 * @version $Id: HTMLTest.java,v 1.3 2008/04/14 18:32:59 benoitx Exp $
 */
public class HTMLTest extends TestCase {

    public void testNormalString() {
        assertEquals("abc", HTML.escape("abc"));
    }

    public void testAmp() {
        assertEquals("x &amp;&amp; y", HTML.escape("x && y"));
    }

    public void testLessThan() {
        assertEquals("x &lt; y", HTML.escape("x < y"));
    }

    public void testGreaterThan() {
        assertEquals("x &gt; y", HTML.escape("x > y"));
    }

    public void testLineBreak() {
        assertEquals("line1<br />\nline2<br />\n", HTML.escape("line1\nline2\n"));
    }

    public void testCombination() {
        assertEquals("(x &lt; y) &amp;&amp;<br />\n(y &gt; x)", HTML.escape("(x < y) &&\n(y > x)"));
    }

    public void testWebifyLinksFromPlainText() {
        final String[] tests = new String[] { 
                "Hello www.cnn.com/bla.html World with http://www.cnn.com ok",
                "Hello www.cnn.com/bla.html World",
                "www.cnn.com/bla.html World",
                "Hello http://cnn.com/bla.html World",
                "Hello www.cnn.com/bla.html",
                "www.cnn.com/bla.html",
                "Hello http://cnn.com/bla.html",
                "http://cnn.com/bla.html",
                "http://cnn.com/bla.php&test=1&test2=3%20%test",
                "http://cnn.com",
                "http://www.cnn.com", 
                "https://cnn.com/bla.html",
                "https://cnn.com/bla.php&test=1&test2=3%20%test",
                "https://cnn.com",
                "https://www.cnn.com" 
                };
        final String[] results = new String[] { 
                "Hello <a href=\"http://www.cnn.com/bla.html\">www.cnn.com/bla.html</a> World with <a href=\"http://www.cnn.com\">http://www.cnn.com</a> ok",
                "Hello <a href=\"http://www.cnn.com/bla.html\">www.cnn.com/bla.html</a> World",
                "<a href=\"http://www.cnn.com/bla.html\">www.cnn.com/bla.html</a> World",
                "Hello <a href=\"http://cnn.com/bla.html\">http://cnn.com/bla.html</a> World",
                "Hello <a href=\"http://www.cnn.com/bla.html\">www.cnn.com/bla.html</a>",
                "<a href=\"http://www.cnn.com/bla.html\">www.cnn.com/bla.html</a>",
                "Hello <a href=\"http://cnn.com/bla.html\">http://cnn.com/bla.html</a>",
                "<a href=\"http://cnn.com/bla.html\">http://cnn.com/bla.html</a>",
                "<a href=\"http://cnn.com/bla.php&amp;test=1&amp;test2=3%20%test\">http://cnn.com/bla.php&amp;test=1&amp;test2=3%20%test</a>",
                "<a href=\"http://cnn.com\">http://cnn.com</a>",
                "<a href=\"http://www.cnn.com\">http://www.cnn.com</a>",
                "<a href=\"https://cnn.com/bla.html\">https://cnn.com/bla.html</a>",
                "<a href=\"https://cnn.com/bla.php&amp;test=1&amp;test2=3%20%test\">https://cnn.com/bla.php&amp;test=1&amp;test2=3%20%test</a>",
                "<a href=\"https://cnn.com\">https://cnn.com</a>",
                "<a href=\"https://www.cnn.com\">https://www.cnn.com</a>"
                };
        for (int i = 0; i < tests.length; i++) {
            assertEquals("Test " + tests[i], results[i], HTML.webifyLinksFromPlainText(tests[i]));
        }
    }
}

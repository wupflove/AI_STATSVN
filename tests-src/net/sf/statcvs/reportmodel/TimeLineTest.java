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
    
	$RCSfile: TimeLineTest.java,v $
	$Date: 2008/04/02 11:22:16 $
*/
package net.sf.statcvs.reportmodel;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test cases for {@link TimeLine}
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TimeLineTest.java,v 1.2 2008/04/02 11:22:16 benoitx Exp $
 */
public class TimeLineTest extends TestCase {
    private final Date date1 = new Date(100000000);
    private final Date date2 = new Date(200000000);
    private final Date date3 = new Date(300000000);
    private final Date date4 = new Date(400000000);
    private final Date beforeDate1 = new Date(99999999);

    /**
     * Constructor
     * @param arg0 stuff
     */
    public TimeLineTest(final String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tests the {@link HelperTimePoint} helper class 
     */
    public void testHelperTimePoint() {
        final HelperTimePoint p1 = HelperTimePoint.createAbsoluteValueTimePoint(date1, 100);
        final HelperTimePoint p2 = HelperTimePoint.createAbsoluteValueTimePoint(date1, 200);
        final HelperTimePoint p3 = HelperTimePoint.createDeltaTimePoint(date1, 50);
        final HelperTimePoint p4 = HelperTimePoint.createDeltaTimePoint(date1, 20);
        assertTrue(p1.isAbsolute());
        assertTrue(!p3.isAbsolute());
        assertEquals(100, p1.getValue());
        assertEquals(50, p3.getValue());
        assertEquals(date1, p1.getDate());
        assertEquals(date1, p3.getDate());
        final HelperTimePoint p1p2 = p1.join(p2);
        final HelperTimePoint p1p3 = p1.join(p3);
        final HelperTimePoint p3p4 = p3.join(p4);
        assertTrue(p1p2.isAbsolute());
        assertTrue(p1p3.isAbsolute());
        assertTrue(!p3p4.isAbsolute());
        assertTrue(p1p2.getValue() == 100 || p1p2.getValue() == 200);
        assertTrue(p1p3.getValue() == 100 || p1p2.getValue() == 150);
        assertEquals(70, p3p4.getValue());
    }

    /**
     * Tests an empty TimeLine
     */
    public void testCreation() {
        final TimeLine tl = new TimeLine("title", "domain");
        assertTrue(tl.isEmpty());
        assertTrue(tl.getDataPoints().isEmpty());
        assertEquals("title", tl.getTitle());
        assertEquals("domain", tl.getRangeLabel());
    }

    /**
     * Tests a TimeLine with one data point
     */
    public void testOneDataPoint() {
        final TimeLine tl = new TimeLine("title", "domain");
        tl.addTimePoint(date1, 100);
        assertTrue(tl.isEmpty());
        assertEquals(1, tl.getDataPoints().size());
        final TimePoint tp = (TimePoint) tl.getDataPoints().get(0);
        assertEquals(date1, tp.getDate());
        assertEquals(100, tp.getValue());
    }

    /**
     * Test if the time points will be sorted if added in a non-ascending order 
     */
    public void testSorting() {
        final TimeLine tl = new TimeLine("title", "domain");
        tl.addTimePoint(date2, 100);
        tl.addTimePoint(date1, 110);
        tl.addTimePoint(date4, 120);
        tl.addTimePoint(date3, 130);
        assertTrue(!tl.isEmpty());
        assertEquals(4, tl.getDataPoints().size());
    }

    /**
     * Test a time line with only relative values
     */
    public void testDeltaTimeLine() {
        final TimeLine t1 = new TimeLine("title", "domain");
        t1.setInitialValue(100);
        t1.addChange(date1, 10);
        t1.addChange(date2, -5);
        final List points = t1.getDataPoints();
        assertEquals(3, points.size());
        assertEquals(beforeDate1, ((TimePoint) points.get(0)).getDate());
        assertEquals(100, ((TimePoint) points.get(0)).getValue());
        assertEquals(date1, ((TimePoint) points.get(1)).getDate());
        assertEquals(110, ((TimePoint) points.get(1)).getValue());
        assertEquals(10, ((TimePoint) points.get(1)).getDelta());
        assertEquals(date2, ((TimePoint) points.get(2)).getDate());
        assertEquals(105, ((TimePoint) points.get(2)).getValue());
        assertEquals(-5, ((TimePoint) points.get(2)).getDelta());
    }

    /**
     * Test a time line with only relative values and no initial value
     */
    public void testIllegalDeltaTimeLine() {
        final TimeLine t1 = new TimeLine("title", "domain");
        t1.addChange(date1, 10);
        t1.addChange(date2, -5);
        try {
            t1.getDataPoints();
            fail("expected IllegalStateException because of missing setInitialValue");
        } catch (final IllegalStateException expected) {
            // expected
        }
    }

    /**
     * Test a time line with multiple relative values at the same time
     */
    public void testDeltaTimeLineMultipleValues() {
        final TimeLine t1 = new TimeLine("title", "domain");
        t1.setInitialValue(100);
        t1.addChange(date1, 10);
        t1.addChange(date2, -5);
        t1.addChange(date1, 20);
        final List points = t1.getDataPoints();
        assertEquals(3, points.size());
        assertEquals(beforeDate1, ((TimePoint) points.get(0)).getDate());
        assertEquals(100, ((TimePoint) points.get(0)).getValue());
        assertEquals(date1, ((TimePoint) points.get(1)).getDate());
        assertEquals(130, ((TimePoint) points.get(1)).getValue());
        assertEquals(30, ((TimePoint) points.get(1)).getDelta());
        assertEquals(date2, ((TimePoint) points.get(2)).getDate());
        assertEquals(125, ((TimePoint) points.get(2)).getValue());
        assertEquals(-5, ((TimePoint) points.get(2)).getDelta());
    }
}
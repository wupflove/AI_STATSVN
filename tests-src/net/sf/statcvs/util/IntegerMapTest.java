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
	Created on $Date: 2002/08/23 02:03:58 $ 
*/
package net.sf.statcvs.util;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: IntegerMapTest.java,v 1.7 2002/08/23 02:03:58 cyganiak Exp $
 */
public class IntegerMapTest extends TestCase {

    private static final double ACCURACY = 0.00001;

    private IntegerMap map;

    /**
     * Constructor for IntegerMapTest.
     * @param arg0 input
     */
    public IntegerMapTest(final String arg0) {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        map = new IntegerMap();
    }

    /**
     * Method testCreation.
     */
    public void testCreation() {
        assertEquals(0, map.size());
        assertEquals(0, map.get("some string"));
        assertEquals(null, map.getInteger("some string"));
        assertTrue(!map.iteratorSortedByKey().hasNext());
        assertTrue(!map.iteratorSortedByValue().hasNext());
        assertTrue(!map.iteratorSortedByValueReverse().hasNext());
        assertEquals(0, map.sum());
        assertEquals(0, map.max());
        assertTrue("should be NaN", Double.isNaN(map.average()));
        assertTrue("should be NaN", Double.isNaN(map.getPercent("x")));
    }

    /**
     * Method testInsert.
     */
    public void testInsert() {
        map.put("a", 42);
        assertEquals(1, map.size());
        assertEquals(0, map.get("some string"));
        assertEquals(null, map.getInteger("some string"));
        assertEquals(42, map.get("a"));
        assertEquals(new Integer(42), map.getInteger("a"));
    }

    /**
     * Method testAdd.
     */
    public void testAdd() {
        map.put("a", 1);
        map.addInt("a", 1);
        assertEquals(1, map.size());
        assertEquals(2, map.get("a"));
    }

    /**
     * Method testRemove.
     */
    public void testRemove() {
        map.put("a", 1);
        map.remove("a");
        assertEquals(0, map.size());
        assertEquals(0, map.get("a"));
        assertEquals(0, map.sum());
    }

    /**
     * Method testRemoveUnused.
     */
    public void testRemoveUnused() {
        map.remove("a");
        assertEquals(0, map.size());
        assertEquals(0, map.get("a"));
        assertEquals(0, map.sum());
    }

    /**
     * Method testMultipleKeys.
     */
    public void testMultipleKeys() {
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("d", 4);
        map.put("e", 5);
        map.addInt("b", 55);
        map.addInt("d", -4);
        assertEquals(5, map.size());
        assertEquals(1, map.get("a"));
        assertEquals(57, map.get("b"));
        assertEquals(3, map.get("c"));
        assertEquals(0, map.get("d"));
        assertEquals(5, map.get("e"));
    }

    /**
     * Method testIteratorSortedByKey.
     */
    public void testIteratorSortedByKey() {
        map.put("f", 24);
        map.put("d", 23);
        map.put("e", 22);
        map.put("a", 21);
        map.put("c", 20);
        map.put("b", 19);
        final Iterator keys = map.iteratorSortedByKey();
        assertTrue("should have next", keys.hasNext());
        assertEquals("a", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("b", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("c", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("d", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("e", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("f", keys.next());
        assertTrue("should not have next", !keys.hasNext());
    }

    /**
     * Method testIteratorSortedByValue.
     */
    public void testIteratorSortedByValue() {
        map.put("f", 4);
        map.put("d", 17);
        map.put("e", -100);
        map.put("a", 99);
        map.put("c", 3);
        map.put("b", 2);
        final Iterator keys = map.iteratorSortedByValue();
        assertTrue("should have next", keys.hasNext());
        assertEquals("e", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("b", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("c", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("f", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("d", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("a", keys.next());
        assertTrue("should not have next", !keys.hasNext());
    }

    /**
     * Method testIteratorSortedByValueReverse.
     */
    public void testIteratorSortedByValueReverse() {
        map.put("f", 4);
        map.put("d", 17);
        map.put("e", -100);
        map.put("a", 99);
        map.put("c", 3);
        map.put("b", 2);
        final Iterator keys = map.iteratorSortedByValueReverse();
        assertTrue("should have next", keys.hasNext());
        assertEquals("a", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("d", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("f", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("c", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("b", keys.next());
        assertTrue("should have next", keys.hasNext());
        assertEquals("e", keys.next());
        assertTrue("should not have next", !keys.hasNext());
    }

    /**
     * Method testSumAvgMax.
     */
    public void testSumAvgMax() {
        map.put("f", 4);
        map.put("d", 0);
        map.put("e", -1);
        map.put("a", 5);
        map.put("c", 3);
        map.put("b", 2);
        assertEquals(13, map.sum());
        assertEquals(5, map.max());
        assertEquals(13.0 / 6.0, map.average(), ACCURACY);
    }

    /**
     * Method testPercent.
     */
    public void testPercent() {
        map.put("a", 5);
        map.put("b", 0);
        map.put("c", 1);
        map.put("d", 4);
        assertEquals(50.0, map.getPercent("a"), ACCURACY);
        assertEquals(0.0, map.getPercent("b"), ACCURACY);
        assertEquals(10.0, map.getPercent("c"), ACCURACY);
        assertEquals(40.0, map.getPercent("d"), ACCURACY);
        assertEquals(100.0, map.getPercentOfMaximum("a"), ACCURACY);
        assertEquals(0.0, map.getPercentOfMaximum("b"), ACCURACY);
        assertEquals(20.0, map.getPercentOfMaximum("c"), ACCURACY);
        assertEquals(80.0, map.getPercentOfMaximum("d"), ACCURACY);
    }

    /**
     * Tests percentages when sum is 0
     */

    public void testPercentSum0() {
        map.put("a", 0);
        assertTrue(Double.isNaN(map.getPercent("a")));
    }

    /**
     * Method testKeySetEmpty.
     */
    public void testKeySetEmpty() {
        final Set keys = map.keySet();
        assertTrue("should be empty", keys.isEmpty());
    }

    /**
     * Method testKeySet.
     */
    public void testKeySet() {
        map.put("c", 5);
        map.put("b", 0);
        map.put("a", 1);
        final Set keys = map.keySet();
        assertEquals(3, keys.size());
        final Iterator it = keys.iterator();
        assertTrue(it.next().equals("a"));
        assertTrue(it.next().equals("b"));
        assertTrue(it.next().equals("c"));
    }

    /**
     * Method testContains.
     */
    public void testContains() {
        map.put("a", 13);
        map.put("b", 0);
        assertTrue(map.contains("a"));
        assertTrue(map.contains("b"));
        assertTrue(!map.contains("c"));
    }

    /**
     * Method testContainsRemoved.
     */
    public void testContainsRemoved() {
        map.put("a", 13);
        map.remove("a");
        assertTrue(!map.contains("a"));
    }
}

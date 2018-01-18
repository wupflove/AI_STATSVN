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
	Created on $Date: 2009/08/20 17:44:05 $ 
*/
package net.sf.statcvs.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Utility class for storing a map from <code>Object</code>s to
 * <code>int</code>s.
 * This class makes it easy to sort by key or value, and provides
 * useful features like {@link #sum()}, {@link #max()}, and
 * percent calculation.
 * <p>
 * The keys must be comparable, for example <code>String</code>s.
 * <p>
 * Behaviour for <code>null</code> keys is unspecified.
 * 
 * @author Richard Cyganiak
 * @version $Id: IntegerMap.java,v 1.17 2009/08/20 17:44:05 benoitx Exp $
 */
public class IntegerMap {

    private final Map map = new TreeMap();
    private final Comparator comparator = new SortByValueComparator(map);
    private int sum = 0;
    private int max = 0;

    /**
     * Puts a value into the map, overwriting any previous value
     * for the same key.
     * 
     * @param key an <code>Object</code> which is used as key.
     * @param value the <code>int</code> value to be stored at this key.
     */
    public void put(final Object key, final int value) {
        max = Math.max(max, value);
        sum -= get(key);
        sum += value;
        map.put(key, new Integer(value));
    }

    /**
     * Gets a value from the map. Returns the value which was
     * stored in the map at the same key before. If no value was
     * stored for this key, 0 will be returned.
     * 
     * @param key an <code>Object</code> which is used as key.
     * @return the value for this key
     */
    public int get(final Object key) {
        final Integer result = (Integer) map.get(key);
        if (result == null) {
            return 0;
        }
        return result.intValue();
    }

    /**
     * Same as {@link #get(Object)}, but returns an <code>Integer</code>,
     * not an <code>int</code>.
     * 
     * @param key the key to get the value for
     * @return the value wrapped in an <code>Integer</code> object
     */
    public Integer getInteger(final Object key) {
        return (Integer) map.get(key);
    }

    /**
     * Gets the value stored at a key as a percentage of all values
     * in the map.
     * 
     * @param key the key to get the value for
     * @return the value as a percentage of the sum of all values
     */
    public double getPercent(final Object key) {
        return (double) get(key) * 100 / sum;
    }

    /**
     * Gets the value stored at a key as a percentage of the maximum
     * value in the map. For the maximum value, this will return
     * 100.0. For a value half as large as the maximum value, this
     * will return 50.0.
     * 
     * @param key the key to get the value for
     * @return the value as a percentage of largest value in the map
     */
    public double getPercentOfMaximum(final Object key) {
        return get(key) * 100 / (double) max;
    }

    /**
     * Adds an <code>int</code> to the value stored at a key.
     * If no value was stored before at this key, the <code>int</code>
     * will be stored there.
     * 
     * @param key the key to whose value <code>addValue</code> should be added
     * @param addValue the <code>int</code> to be added
     */
    public void addInt(final Object key, final int addValue) {
        put(key, addValue + get(key));
    }

    /**
     * Same as <code>addInt(key, 1)</code>
     * 
     * @param key the key whose value should be increased
     */
    public void inc(final Object key) {
        addInt(key, 1);
    }

    /**
     * Same as <code>addInt(key, -1)</code>
     * 
     * @param key the key whose value should be decreased
     */
    public void dec(final Object key) {
        addInt(key, -1);
    }

    /**
     * Deletes a value from the map. This is different from
     * <code>put(key, 0)</code>. Removing will reduce
     * the size of the map, putting 0 will not.
     * 
     * @param key the key that should be removed
     */
    public void remove(final Object key) {
        sum -= get(key);
        map.remove(key);
    }

    /**
     * Returns <code>true</code> if the map contains a value
     * for this key.
     * 
     * @param key the key to check for
     * @return <code>true</code> if the key is in the map
     */
    public boolean contains(final Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns the number of key-value pairs stored in the map.
     * 
     * @return the number of key-value pairs stored in the map
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns a set view of the keys. The set will be in
     * ascending key order.
     * 
     * @return a <code>Set</code> view of all keys
     */
    public Set keySet() {
        return map.keySet();
    }

    /**
     * Returns an iterator on the keys, sorted by key ascending.
     * 
     * @return an iterator on the keys
     */
    public Iterator iteratorSortedByKey() {
        return map.keySet().iterator();
    }

    /**
     * Returns an iterator on the keys, sorted by values ascending.
     * 
     * @return an iterator on the keys
     */
    public Iterator iteratorSortedByValue() {
        final List keys = new ArrayList(map.keySet());
        Collections.sort(keys, comparator);
        return keys.iterator();
    }

    /**
     * Returns an iterator on the keys, sorted by values descending.
     * 
     * @return an iterator on the keys
     */
    public Iterator iteratorSortedByValueReverse() {
        final List keys = new ArrayList(map.keySet());
        Collections.sort(keys, comparator);
        Collections.reverse(keys);
        return keys.iterator();
    }

    /**
     * Returns the sum of all values in the map.
     * 
     * @return the sum of all values in the map
     */
    public int sum() {
        return sum;
    }

    /**
     * Returns the average of all values in the map.
     * 
     * @return the average of all values in the map
     */
    public double average() {
        return (double) sum() / size();
    }

    /**
     * Returns the maximum value in the map.
     * 
     * @return the maximum value in the map.
     */
    public int max() {
        return max;
    }

    /**
     * Private utility class for comparing of map entries by value.
     */
    private static class SortByValueComparator implements Comparator, Serializable {
        private final Map mapToBeSorted;

        public SortByValueComparator(final Map map) {
            this.mapToBeSorted = map;
        }

        public int compare(final Object o1, final Object o2) {
            final int i1 = ((Integer) this.mapToBeSorted.get(o1)).intValue();
            final int i2 = ((Integer) this.mapToBeSorted.get(o2)).intValue();
            if (i1 < i2) {
                return -1;
            } else if (i1 > i2) {
                return 1;
            }
            return 0;
        }
    }
}
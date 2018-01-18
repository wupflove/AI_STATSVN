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
    
	$RCSfile: TimeLine.java,v $
	$Date: 2008/04/02 11:52:02 $
*/
package net.sf.statcvs.reportmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Contains time line data for an integer value. The semantics is that at
 * every data point, the time line's value changed from the previous point's
 * value to the current point's value. Time points may be specified either
 * by an absolute value using <tt>addTimePoint</tt>, or by a value relative
 * to the previous time point using {@link #addChange}. If all points are
 * specified using <tt>addChange</tt>, an initial value must be given
 * using {@link #setInitialValue}.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TimeLine.java,v 1.5 2008/04/02 11:52:02 benoitx Exp $
 */
public class TimeLine {
    private final TreeMap dataPoints = new TreeMap();
    private Date minimumDate = null;
    private int initialValue;
    private boolean hasInitialValue = false;
    private final String title;
    private final String rangeLabel;

    /**
     * Creates a new time line.
     * @param title the time line's title
     * @param rangeLabel a range label (axis label) for the values
     */
    public TimeLine(final String title, final String rangeLabel) {
        this.title = title;
        this.rangeLabel = rangeLabel;
    }

    /**
     * Sets the initial value of the time line, that is the value just before
     * the first data point.
     * @param initialValue the time line's initial value
     */
    public void setInitialValue(final int initialValue) {
        this.initialValue = initialValue;
        this.hasInitialValue = true;
    }

    /**
     * Adds a data point to the time line. Data points may be added in any
     * order.
     * @param date the data point's date
     * @param value the data point's value
     */
    public void addTimePoint(final Date date, final int value) {
        addTimePoint(HelperTimePoint.createAbsoluteValueTimePoint(date, value));
    }

    /**
     * Specifies that the time line's value changed at a given date. Data
     * points may be added in any order.
     * @param date the data point's date
     * @param delta the value change at this time
     */
    public void addChange(final Date date, final int delta) {
        addTimePoint(HelperTimePoint.createDeltaTimePoint(date, delta));
    }

    /**
     * Checks if the time series is empty. A series is considered empty if
     * it has zero or one time points. It takes two distinct time points
     * to actually make it a series.
     * @return <tt>true</tt> if the time series is empty
     */
    public boolean isEmpty() {
        return dataPoints.size() <= 1;
    }

    /**
     * Returns a <tt>List</tt> of data points, ordered by date.
     * @return a <tt>List</tt> of {@link TimePoint}s
     */
    public List getDataPoints() {
        List result;
        int currentValue;
        if (hasInitialValue && dataPoints.size() > 0) {
            result = new ArrayList(dataPoints.size() + 1);
            final Date beforeMinimum = new Date(minimumDate.getTime() - 1);
            result.add(new TimePoint(beforeMinimum, initialValue, 0));
            currentValue = initialValue;
        } else {
            result = new ArrayList(dataPoints.size());
            if (dataPoints.size() == 0) {
                return result;
            }
            final HelperTimePoint firstPoint = (HelperTimePoint) dataPoints.get(dataPoints.firstKey());
            if (!firstPoint.isAbsolute()) {
                throw new IllegalStateException("The first data point must be absolute, or setInitialValue must be used");
            }
            currentValue = firstPoint.getValue();
        }
        final Iterator it = dataPoints.values().iterator();
        while (it.hasNext()) {
            final HelperTimePoint point = (HelperTimePoint) it.next();
            if (point.isAbsolute()) {
                final int delta = point.getValue() - currentValue;
                result.add(new TimePoint(point.getDate(), point.getValue(), delta));
                currentValue = point.getValue();
            } else {
                currentValue += point.getValue();
                result.add(new TimePoint(point.getDate(), currentValue, point.getValue()));
            }
        }
        return result;
    }

    private void addTimePoint(HelperTimePoint newPoint) {
        final HelperTimePoint oldPoint = (HelperTimePoint) dataPoints.get(newPoint.getDate());
        if (oldPoint == null) {
            if (minimumDate == null || newPoint.getDate().before(minimumDate)) {
                minimumDate = newPoint.getDate();
            }
            //oldPoint = newPoint;
        } else {
            newPoint = oldPoint.join(newPoint);
        }
        dataPoints.put(newPoint.getDate(), newPoint);
    }

    /**
     * Returns the range label (axis label) of the values
     * @return an axis label for the values
     */
    public String getRangeLabel() {
        return rangeLabel;
    }

    /**
     * Returns the title of the time line
     * @return the title
     */
    public String getTitle() {
        return title;
    }
}
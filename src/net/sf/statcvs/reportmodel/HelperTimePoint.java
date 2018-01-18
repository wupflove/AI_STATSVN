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
    
	$RCSfile: HelperTimePoint.java,v $
	$Date: 2009/08/20 17:44:05 $
*/
package net.sf.statcvs.reportmodel;

import java.util.Date;

/**
 * Helper class for {@link TimeLine}. It encapsulates a data point
 * in the time line and may have an absolute value or a delta value (change).
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: HelperTimePoint.java,v 1.3 2009/08/20 17:44:05 benoitx Exp $
 */
class HelperTimePoint implements Comparable {
    private final Date date;
    private final int value;
    private final boolean isAbsolute;

    private HelperTimePoint(final Date date, final int value, final boolean isAbsolute) {
        this.date = date;
        this.value = value;
        this.isAbsolute = isAbsolute;
    }

    /**
     * Creates a new time point having an absolute value.
     * @param date the time point's date
     * @param value the time point's value
     * @return a new time point
     */
    public static HelperTimePoint createAbsoluteValueTimePoint(final Date date, final int value) {
        return new HelperTimePoint(date, value, true);
    }

    /**
     * Creates a new time point representing a change relative to the previous
     * time point's value.
     * @param date the time point's date
     * @param delta the time point's change relative to the previous value
     * @return a new time point
     */
    public static HelperTimePoint createDeltaTimePoint(final Date date, final int delta) {
        return new HelperTimePoint(date, delta, false);
    }

    /**
     * Creates a new time point representing two time points with the same
     * time. If one of them is absolute, it will be returned. If both
     * are relative, a new time point with the sum of the relative
     * values will be returned.
     * @param other a <tt>HelperTimePoint</tt>
     * @return a time point representing both <tt>this</tt> and <tt>other</tt>
     */
    public HelperTimePoint join(final HelperTimePoint other) {
        if (!date.equals(other.getDate())) {
            throw new IllegalArgumentException("Can only add time points having the same date");
        }
        if (isAbsolute) {
            return this;
        }
        if (other.isAbsolute()) {
            return other;
        }
        return HelperTimePoint.createDeltaTimePoint(date, value + other.getValue());
    }

    /**
     * @return Date the data point's time
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return int the data point's value
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns <tt>true</tt> if the time point has an absolute value, and
     * <tt>false</tt> if it has a delta value relative to the previous
     * time point. 
     * @return <tt>true</tt> if absolute value
     */
    public boolean isAbsolute() {
        return isAbsolute;
    }

    /**
     * Compares the time point's date to that of another time point
     * @param o another time point
     * @return See {@link java.lang.Comparable#compareTo(java.lang.Object)}
     */
    public int compareTo(final Object o) {
        final HelperTimePoint other = (HelperTimePoint) o;
        return getDate().compareTo(other.getDate());
    }

    public boolean equals(final Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (!(rhs instanceof HelperTimePoint)) {
            return false;
        }
        final HelperTimePoint that = (HelperTimePoint) rhs;

        final boolean eq = getDate().equals(that.getDate());

        return eq;
    }

    public int hashCode() {
        return getDate().hashCode();
    }

}

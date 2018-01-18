package net.sf.statcvs.reports;

import net.sf.statcvs.model.Revision;

import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

/**
 * Builds a <tt>BasicTimesSeries</tt> for the LOC history of a set of
 * revisions. All revisions that should be counted must be passed to
 * the {@link #addRevision} method. When all revisions have been passed
 * to this method, a <tt>BasicTimeSeries</tt> can
 * be obtained from {@link #getTimeSeries} and can be added to a chart.
 * 
 * TODO: Replace by a custom LocTimeSeriesReport
 * 
 * @author Richard Cyganiak
 * @version $Id: LOCSeriesBuilder.java,v 1.4 2008/04/02 11:22:15 benoitx Exp $
 **/
public class LOCSeriesBuilder {
    //	private static Logger logger = Logger.getLogger(LOCSeriesBuilder.class.getName());
    private final TimeSeries series;
    private boolean hasRevisions = false;
    private Minute minute;
    private int loc = 0;
    private boolean finished = false;
    private final boolean countEffective;
    private int maximum = 0;

    /**
     * Creates a new <tt>LOCSeriesBuilder</tt>
     * @param seriesTitle the title for the time series
     * @param countEffective If <tt>true</tt>, the effective LOC number will
     *                       be counted. If <tt>false</tt>, the contributed
     *                       value of new lines will be counted. 
     */
    public LOCSeriesBuilder(final String seriesTitle, final boolean countEffective) {
        series = new TimeSeries(seriesTitle, Minute.class);
        this.countEffective = countEffective;
    }

    /**
     * Adds a revision to the time series. The revision must
     * be at a later date than all previously added revisions.
     * @param revision the revision to add to the series
     */
    public void addRevision(final Revision revision) {
        if (finished) {
            throw new IllegalStateException("can't add more revisions after getTimeSeries()");
        }
        if (!hasRevisions) {
            if (revision.isBeginOfLog()) {
                loc += revision.getLines();
                return;
            }
            minute = new Minute(revision.getDate());

            // Work around a bug in JFreeChart 1.0.3
            RegularTimePeriod previousMinute = minute.previous();
            if (previousMinute == null) {
                previousMinute = new Minute(Minute.LAST_MINUTE_IN_HOUR, (Hour) minute.getHour().previous());
            }

            series.add(previousMinute, loc);
            hasRevisions = true;
        } else {
            final Minute currentMinute = new Minute(revision.getDate());
            if (!currentMinute.equals(minute)) {
                series.add(minute, loc);
                minute = currentMinute;
            }
        }
        if (countEffective) {
            loc += revision.getLinesDelta();
        } else {
            loc += revision.getNewLines();
        }
        this.maximum = Math.max(this.maximum, this.loc);
    }

    /**
     * gets the finished time series. Should not be called before
     * all revisions have been added.
     * @return the resulting <tt>BasicTimeSeries</tt> or <tt>null</tt>
     * if no LOC data is available for the revision set
     */
    public TimeSeries getTimeSeries() {
        if (!hasRevisions) {
            return null;
        }
        if (!finished) {
            series.add(minute, loc);
            series.add(minute.next(), loc);
            finished = true;
        }
        return series;
    }

    /**
     * @return The maximum value over the lifetime of the series
     */
    public int getMaximum() {
        return this.maximum;
    }
}
package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.SortedSet;

import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Produces bar charts where each bar represents a time slot, e.g. a weekday.,
 * and each revision from a given collection is sorted into the appropriate
 * slot.
 * 
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: TimeBarChartMaker.java,v 1.7 2009/04/25 16:36:20 benoitx Exp $
 */
public abstract class TimeBarChartMaker {
    private final ReportConfig config;
    private final SortedSet revisions;
    private final String title;
    private final String fileName;
    private final String[] barLabels;
    private final String chartName;

    /**
     * Creates a new BarChartMaker.
     * @param config The configuration to use
     * @param revisions The revisions to analyze
     * @param title The chart's title
     * @param fileName The file name for the image file, including <tt>.png</tt> extension
     * @param barLabels The labels for each bar
     */
    public TimeBarChartMaker(final String chartName, final ReportConfig config, final SortedSet revisions, final String title, final String fileName,
            final String[] barLabels) {
        this.config = config;
        this.chartName = chartName;
        this.revisions = revisions;
        this.title = title;
        this.fileName = fileName;
        this.barLabels = barLabels;
    }

    /**
     * Creates a bar chart image file.
     * @return An image file containing the chart
     */
    public ChartImage toFile() {
        final int[] barValues = new int[this.barLabels.length];
        for (int i = 0; i < barValues.length; i++) {
            barValues[i] = 0;
        }
        final Iterator it = this.revisions.iterator();
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            final Date date = rev.getDate();
            final Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            barValues[barNumberForTime(cal)]++;
        }
        final DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (int i = 0; i < barValues.length; i++) {
            data.addValue(barValues[i], "Commits", this.barLabels[i]);
        }
        final JFreeChart chart = ChartFactory.createBarChart(this.config.getProjectName() + ": " + this.title, "", "Commits", data, PlotOrientation.VERTICAL,
                false, false, false);
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.getRenderer().setSeriesPaint(0, Color.blue);

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(chartName));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(chartName));
        //        final CategoryItemRenderer renderer = plot.getRenderer();
        //        ChartConfigUtil.configureStroke(chartName, renderer, data);
        //        ChartConfigUtil.configureShapes(chartName, renderer);
        ChartConfigUtil.configureCopyrightNotice(chartName, chart);
        ChartConfigUtil.configureChartBackgroungImage(chartName, chart);
        ChartConfigUtil.configurePlotImage(chartName, chart);

        final Dimension dim = ChartConfigUtil.getDimension(chartName, config.getLargeChartSize());

        return this.config.createChartImage(this.fileName, this.title, chart, dim);
    }

    protected abstract int barNumberForTime(Calendar time);

    public static class HourBarChartMaker extends TimeBarChartMaker {
        private final static String[] HOURS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
                "18", "19", "20", "21", "22", "23" };

        /**
         * Creates a bar chart showing a distribution of revisions
         * over the hours of the day.
         * @param config The configuration to use
         * @param revisions The set of revisions to analyze
         * @param title The title of the chart
         * @param fileName The file for saving the chart
         */
        public HourBarChartMaker(final String chartName, final ReportConfig config, final SortedSet revisions, final String title, final String fileName) {
            super(chartName, config, revisions, title, fileName, HOURS);
        }

        protected int barNumberForTime(final Calendar time) {
            return time.get(Calendar.HOUR_OF_DAY);
        }
    }

    public static class WeekdayBarChartMaker extends TimeBarChartMaker {
        private final static String[] WEEKDAYS = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

        /**
         * Creates a bar chart showing a distribution of revisions
         * over the days of the week.
         * @param config The configuration to use
         * @param revisions The set of revisions to analyze
         * @param title The title of the chart
         * @param fileName The file for saving the chart
         */
        public WeekdayBarChartMaker(final String chartName, final ReportConfig config, final SortedSet revisions, final String title, final String fileName) {
            super(chartName, config, revisions, title, fileName, WEEKDAYS);
        }

        protected int barNumberForTime(final Calendar time) {
            return time.get(Calendar.DAY_OF_WEEK) - 1;
        }
    }
}
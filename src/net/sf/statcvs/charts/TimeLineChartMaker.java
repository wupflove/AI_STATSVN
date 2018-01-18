package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Messages;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reportmodel.TimePoint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * Creates charts from {@link net.sf.statcvs.reportmodel.TimeLine}s.
 *
 * @author Richard Cyganiak
 * @version $Id: TimeLineChartMaker.java,v 1.11 2009/04/25 16:36:20 benoitx Exp $
 */
public class TimeLineChartMaker {
    private final ReportConfig config;
    private final TimeLine timeLine;
    private final String fileName;
    private final List annotations;
    private final String chartName;

    /**
     * Creates a chart from a time line.
     * @param config The configuration to use
     * @param timeLine the time line data for the chart 
     * @param fileName the file name for the PNG image
     */
    public TimeLineChartMaker(final String chartName, final ReportConfig config, final TimeLine timeLine, final String fileName, final Collection symbolicNames) {
        this.chartName = chartName;
        this.config = config;
        this.timeLine = timeLine;
        this.fileName = fileName;
        this.annotations = SymbolicNameAnnotation.createAnnotations(symbolicNames);
    }

    public ChartImage toFile() {
        if (this.timeLine.isEmpty()) {
            return null;
        }
        final TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(createTimeSeries(timeLine));

        final String range = timeLine.getRangeLabel();
        final String domain = Messages.getString("DOMAIN_TIME");

        final XYDataset data = collection;
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(this.config.getProjectName() + ": " + timeLine.getTitle(), domain, range, data, false,
                false, false);

        final XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, Color.blue);
        final DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setVerticalTickLabels(true);
        plot.getRangeAxis().setLowerBound(0);
        plot.setRenderer(new XYStepRenderer());
        for (final Iterator it = annotations.iterator(); it.hasNext();) {
            plot.addAnnotation((XYAnnotation) it.next());
        }

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(chartName));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(chartName));
        final XYItemRenderer renderer = plot.getRenderer();
        ChartConfigUtil.configureStroke(chartName, renderer, data);
        ChartConfigUtil.configureShapes(chartName, renderer);
        ChartConfigUtil.configureCopyrightNotice(chartName, chart);
        ChartConfigUtil.configureChartBackgroungImage(chartName, chart);
        ChartConfigUtil.configurePlotImage(chartName, chart);

        final Dimension dim = ChartConfigUtil.getDimension(chartName, config.getLargeChartSize());

        return this.config.createChartImage(this.fileName, this.timeLine.getTitle(), chart, dim);
    }

    private TimeSeries createTimeSeries(final TimeLine timeLine) {
        final TimeSeries result = new TimeSeries("!??!SERIES_LABEL!??!", Millisecond.class);
        final Iterator it = timeLine.getDataPoints().iterator();
        while (it.hasNext()) {
            final TimePoint timePoint = (TimePoint) it.next();
            result.add(new Millisecond(timePoint.getDate()), timePoint.getValue());
        }
        return result;
    }
}

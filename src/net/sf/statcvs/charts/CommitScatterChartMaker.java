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
    
	Created on $Date: 2009/04/25 16:36:20 $ 
*/
package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Produces the commit scatter chart.
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CommitScatterChartMaker {
    private static final String CHART_NAME = "commitscatterauthors";
    private final static Color ALL_COMMITS_COLOR = new Color(128, 128, 255);
    private final ReportConfig config;
    private final Repository repository;
    private final int width;

    /**
     * Creates a new CommitScatterChartMaker.
     * @param repository The repository to be analyzed
     * @param width The width of the chart in pixels
     */
    public CommitScatterChartMaker(final ReportConfig config, final int width) {
        this.config = config;
        this.repository = config.getRepository();
        this.width = width;
    }

    /**
     * @return An image file for the chart
     */
    public ChartImage toFile() {
        final DateAxis timeAxis = new DateAxis(Messages.getString("TIME_CSC_DOMAIN"));
        timeAxis.setVerticalTickLabels(true);
        final CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(timeAxis);
        List annotations = SymbolicNameAnnotation.createAnnotations(repository.getSymbolicNames());
        combinedPlot.add(createPlot(createTimeSeries(this.repository.getRevisions()), "All (" + Messages.getString("TIME_CSC_RANGE") + ")", ALL_COMMITS_COLOR,
                annotations));
        annotations = SymbolicNameAnnotation.createAnnotations(repository.getSymbolicNames(), SymbolicNameAnnotation.STYLE_NO_LABELS);
        final List authors = new ArrayList(this.repository.getAuthors());
        Collections.sort(authors);
        final Iterator it = authors.iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            final Color color = this.config.isDeveloper(author) ? Color.RED : Color.GRAY;
            combinedPlot.add(createPlot(createTimeSeries(author.getRevisions()), author.getName(), color, annotations));
        }
        combinedPlot.setGap(10);
        final JFreeChart chart = new JFreeChart(this.config.getProjectName() + ": " + Messages.getString("TIME_CSC_SUBTITLE"), JFreeChart.DEFAULT_TITLE_FONT,
                combinedPlot, false);

        combinedPlot.setBackgroundPaint(ChartConfigUtil.getPlotColor(CHART_NAME));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(CHART_NAME));
        final XYItemRenderer renderer = combinedPlot.getRenderer();
        ChartConfigUtil.configureShapes(CHART_NAME, renderer);
        ChartConfigUtil.configureCopyrightNotice(CHART_NAME, chart);
        ChartConfigUtil.configureChartBackgroungImage(CHART_NAME, chart);
        ChartConfigUtil.configurePlotImage(CHART_NAME, chart);

        final Dimension dim = ChartConfigUtil.getDimension(CHART_NAME, config.getLargeChartSize());

        return this.config.createChartImage(CHART_NAME + ".png", Messages.getString("TIME_CSC_SUBTITLE"), chart, getSize(dim));
    }

    private TimeSeries createTimeSeries(final SortedSet revisions) {
        final TimeSeries result = new TimeSeries("Dummy", Second.class);
        final Iterator it = revisions.iterator();
        Date lastDate = new Date();
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            if (lastDate != null) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(lastDate);
                final double lastDateSeconds = cal.get(Calendar.SECOND);
                cal.setTime(rev.getDate());
                final double dateSeconds = cal.get(Calendar.SECOND);
                if (lastDateSeconds == dateSeconds) {
                    continue;
                }
            }
            lastDate = rev.getDate();
            final Calendar cal = Calendar.getInstance();
            cal.setTime(lastDate);
            final double hour = cal.get(Calendar.HOUR_OF_DAY);
            final double minutes = cal.get(Calendar.MINUTE);
            result.add(new Second(lastDate), hour + minutes / 60.0);
        }
        return result;
    }

    private XYPlot createPlot(final TimeSeries series, final String label, final Color color, final List annotations) {
        final NumberAxis valueAxis = new NumberAxis(label);
        valueAxis.setTickUnit(new NumberTickUnit(6.0, new DecimalFormat("0")));
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setRange(0.0, 24.0);
        valueAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 9));
        final XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        renderer.setShape(new Rectangle(new Dimension(2, 2)));
        final XYPlot result = new XYPlot(new TimeSeriesCollection(series), null, valueAxis, renderer);
        result.getRenderer().setSeriesPaint(0, color);
        final Iterator it = annotations.iterator();
        while (it.hasNext()) {
            final XYAnnotation annotation = (XYAnnotation) it.next();
            result.addAnnotation(annotation);
        }
        return result;
    }

    private Dimension getSize(final Dimension dim) {
        return new Dimension(dim.width, 70 * (this.repository.getAuthors().size() + 1) + 110);
    }
}

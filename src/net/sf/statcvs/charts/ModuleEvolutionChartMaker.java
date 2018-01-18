package net.sf.statcvs.charts;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.statcvs.model.Module;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.data.contour.ContourDataset;
import org.jfree.data.contour.DefaultContourDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Class for producing directory pie charts
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ModuleEvolutionChartMaker.java,v 1.2 2010/01/01 10:03:43 benoitx Exp $
 */
public class ModuleEvolutionChartMaker {
    private static final int SLICE_MIN_PERCENT = 5;

    private final ReportConfig config;
    private final String title;
    private final String fileName;
    private final List files = new ArrayList();
    private final String chartName;
    private final Repository repository;

    /**
     * Creates a new PieChartMaker
     * @param config The report configuration to use
     * @param directories The set of directories to consider
     * @param title The chart title
     * @param fileName The file name for chart
     */
    public ModuleEvolutionChartMaker(final String chartName, final ReportConfig config, final String title, final String fileName) {
        this.chartName = chartName;
        this.config = config;
        this.title = title;
        this.fileName = fileName;
        this.repository = config.getRepository();
    }

    public ChartImage toFile() {

        final ContourDataset data = buildDs();

        if (data == null) {
            return null;
        }

        final ValueAxis xAxis = new DateAxis("Date");
        final SymbolAxis yAxis = new SymbolAxis("Module", (String[]) repository.getModules().keySet().toArray(new String[0]));

        //        SymbolicAxis yAxis = new SymbolicAxis(grouper.getName(), (String[])groupNames.toArray(new String[0])); 
        yAxis.setInverted(true);
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);

        final ColorBar zAxis = new ColorBar("Commit Activity (%)");
        zAxis.getAxis();

        final ContourPlot plot = new ContourPlot(data, xAxis, yAxis, zAxis);
        //plot.setRenderAsPoints(true);
        // don't use plot units for ratios when x axis is date
        plot.setDataAreaRatio(0.0);
        plot.setColorBarLocation(RectangleEdge.BOTTOM);

        final JFreeChart chart = new JFreeChart(config.getProjectName(), null, plot, false);

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(chartName));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(chartName));
        ChartConfigUtil.configureCopyrightNotice(chartName, chart);
        ChartConfigUtil.configureChartBackgroungImage(chartName, chart);
        ChartConfigUtil.configurePlotImage(chartName, chart);
        final Dimension dim = ChartConfigUtil.getDimension(chartName, config.getLargeChartSize());
        return this.config.createChartImage(this.fileName, this.title, chart, dim);
    }

    private ContourDataset buildDs() {
        final Map mapByDate = new LinkedHashMap();
        int max = 0;
        final long elapsed = repository.getLastDate().getTime() - repository.getFirstDate().getTime();
        final long windowSize = Math.max(elapsed / 60, 1);

        IntegerMap commitsPerModule = new IntegerMap();

        final Iterator itRev = repository.getRevisions().iterator();

        long currentDate = -1;
        while (itRev.hasNext()) {
            final Revision rev = (Revision) itRev.next();

            final Date date = rev.getDate();
            System.out.println("| Consider Rev " + rev.getDate() + " Mod:" + rev.getFile().getModule().getName() + " currentDate:" + currentDate);
            if (currentDate == -1) {
                currentDate = date.getTime();
            } else if (date.getTime() > currentDate + windowSize) {
                // save old map
                max = Math.max(commitsPerModule.max(), max);
                mapByDate.put(new Date(currentDate), commitsPerModule);

                // create new map
                commitsPerModule = new IntegerMap();

                // hack: fill in intermediate values
                final int fill = (int) ((date.getTime() - currentDate) / windowSize);
                if (fill > 1) {
                    mapByDate.put(new Date(currentDate + windowSize), null);
                }
                currentDate += fill * windowSize;
            }
            commitsPerModule.inc(rev.getFile().getModule().getName());
        }
        if (currentDate != -1) {
            mapByDate.put(new Date(currentDate), commitsPerModule);
            max = Math.max(commitsPerModule.max(), max);
        }

        /*
        final Iterator itMod = repository.getModules().values().iterator();

        while (itMod.hasNext()) {
            final Module module = (Module) itMod.next();
            final Iterator it = module.getRevisions().iterator();
            System.out.println("Consider Module " + module.getName());
            long currentDate = -1;
            while (it.hasNext()) {
                final Revision rev = (Revision) it.next();
                final Date date = rev.getDate();
                System.out.println(module.getName() + " | Consider Rev " + rev.getDate() + " currentDate:" + currentDate);
                if (currentDate == -1) {
                    currentDate = date.getTime();
                } else if (date.getTime() > currentDate + windowSize) {
                    // save old map
                    max = Math.max(commitsPerModule.max(), max);
                    mapByDate.put(new Date(currentDate), commitsPerModule);

                    // create new map
                    commitsPerModule = new IntegerMap();

                    // hack: fill in intermediate values
                    final int fill = (int) ((date.getTime() - currentDate) / windowSize);
                    if (fill > 1) {
                        mapByDate.put(new Date(currentDate + windowSize), null);
                    }
                    currentDate += fill * windowSize;
                }
                commitsPerModule.inc(module.getName());
            }
            if (currentDate != -1) {
                mapByDate.put(new Date(currentDate), commitsPerModule);
                max = Math.max(commitsPerModule.max(), max);
            }
        }
        */
        System.out.println("Module Commit");
        Iterator it = mapByDate.keySet().iterator();
        while (it.hasNext()) {
            final Object key = it.next();
            final IntegerMap map = (IntegerMap) mapByDate.get(key);
            System.out.println("Key:" + key);
            if (map != null) {
                final Iterator it2 = map.iteratorSortedByKey();
                while (it2.hasNext()) {
                    final Object o = it2.next();
                    System.out.println("Val:" + o + "\t -> " + map.get(o));
                }
            }
        }

        final int groupCount = repository.getModules().size();

        final int dateCount = mapByDate.size();
        final int numValues = dateCount * groupCount;
        if (numValues == 0 || max == 0 || dateCount == 1) {
            return null;
        }

        final Date[] oDateX = new Date[numValues];
        final Double[] oDoubleY = new Double[numValues];
        final Double[] oDoubleZ = new Double[numValues];

        it = mapByDate.keySet().iterator();
        for (int x = 0; x < dateCount; x++) {
            if (!it.hasNext()) {
                throw new RuntimeException("Invalid date count");
            }
            final Date date = (Date) it.next();

            final IntegerMap map = (IntegerMap) mapByDate.get(date);
            if (map != null) {
                final Iterator it2 = repository.getModules().values().iterator();
                for (int y = 0; y < groupCount; y++) {
                    if (!it2.hasNext()) {
                        throw new RuntimeException("Invalid group count");
                    }
                    final Module group = (Module) it2.next();

                    final int index = (x * groupCount) + y;
                    oDateX[index] = date;
                    oDoubleY[index] = new Double(y);
                    final double value = map.get(group.getName()) * 100.0 / max;
                    oDoubleZ[index] = (value != 0) ? new Double(value) : null;
                }
            } else {
                for (int y = 0; y < groupCount; y++) {
                    final int index = (x * groupCount) + y;
                    oDateX[index] = date;
                    oDoubleY[index] = new Double(y);
                    //oDoubleZ[index] = null;
                }
            }
        }
        return new DefaultContourDataset(null, oDateX, oDoubleY, oDoubleZ);
    }
}
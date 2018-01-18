package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.Module;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Class for producing directory pie charts
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ModuleSizePieChartMaker.java,v 1.2 2009/08/31 19:16:35 benoitx Exp $
 */
public class ModuleSizePieChartMaker {
    private static final int SLICE_MIN_PERCENT = 5;

    private final ReportConfig config;
    private final String title;
    private final String subTitle;
    private final String fileName;
    private final List files = new ArrayList();
    private final String chartName;

    /**
     * Creates a new PieChartMaker
     * @param config The report configuration to use
     * @param directories The set of directories to consider
     * @param title The chart title
     * @param fileName The file name for chart
     */
    public ModuleSizePieChartMaker(final String chartName, final ReportConfig config, final String title, final String subTitle, final String fileName) {
        this.chartName = chartName;
        this.config = config;
        this.title = title;
        this.subTitle = subTitle;
        this.fileName = fileName;
    }

    public ChartImage toFile() {
        final DefaultPieDataset data = new DefaultPieDataset();

        final IntegerMap dirSizes = new IntegerMap();
        Iterator it = config.getRepository().getModules().values().iterator();
        int total = 0;
        while (it.hasNext()) {
            final Module module = (Module) it.next();

            dirSizes.addInt(module.getName(), module.getCurrentLinesOfCode());
            total += module.getCurrentLinesOfCode();
        }
        it = dirSizes.iteratorSortedByValue();
        final NumberFormat nf = NumberFormat.getNumberInstance();
        final NumberFormat nf2 = NumberFormat.getPercentInstance();
        while (it.hasNext()) {
            final String modName = (String) it.next();
            final Integer loc = dirSizes.getInteger(modName);
            final double percent = (double) loc.intValue() / (double) total;
            final String dirName = modName + " = " + nf.format(loc) + " (" + nf2.format(percent) + ")";
            data.setValue(dirName, loc);
        }

        final JFreeChart chart = ChartFactory.createPieChart(this.config.getProjectName() + ": " + title, data, false, false, false);
        final ArrayList arrayList = new ArrayList();
        arrayList.add(new TextTitle(subTitle));
        chart.setSubtitles(arrayList);
        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setShadowPaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(Color.LIGHT_GRAY);
        plot.setForegroundAlpha(0.8f);
        plot.setSectionOutlinePaint(Color.BLACK);

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(chartName));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(chartName));
        ChartConfigUtil.configureCopyrightNotice(chartName, chart);
        ChartConfigUtil.configureChartBackgroungImage(chartName, chart);
        ChartConfigUtil.configurePlotImage(chartName, chart);

        final Dimension dim = ChartConfigUtil.getDimension(chartName, config.getLargeChartSize());

        return this.config.createChartImage(this.fileName, this.title, chart, dim);
    }
}
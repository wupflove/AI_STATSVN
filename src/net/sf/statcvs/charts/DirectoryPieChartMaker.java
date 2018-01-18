package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.pages.HTML;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Class for producing directory pie charts
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DirectoryPieChartMaker.java,v 1.14 2009/08/21 23:06:51 benoitx Exp $
 */
public abstract class DirectoryPieChartMaker {
    private static final int SLICE_MIN_PERCENT = 5;

    private final ReportConfig config;
    private final String title;
    private final String fileName;
    private List directories = new ArrayList();
    private final String chartName;

    /**
     * Creates a new PieChartMaker
     * @param config The report configuration to use
     * @param directories The set of directories to consider
     * @param title The chart title
     * @param fileName The file name for chart
     */
    public DirectoryPieChartMaker(final String chartName, final ReportConfig config, final SortedSet directories, final String title, final String fileName) {
        this.chartName = chartName;
        this.config = config;
        this.title = title;
        this.fileName = fileName;
        this.directories = new ArrayList(directories);
    }

    public ChartImage toFile() {
        final DefaultPieDataset data = new DefaultPieDataset();

        final IntegerMap dirSizes = new IntegerMap();
        Collections.sort(directories);
        Iterator it = directories.iterator();
        while (it.hasNext()) {
            final Directory dir = (Directory) it.next();
            dirSizes.addInt(dir, calculateValue(dir));
        }

        int otherSum = 0;
        final Map colors = new HashMap();
        it = dirSizes.iteratorSortedByValue();
        while (it.hasNext()) {
            final Directory dir = (Directory) it.next();
            if (dirSizes.getPercent(dir) >= SLICE_MIN_PERCENT) {
                final String dirName = dir.isRoot() ? "/" : dir.getPath();
                data.setValue(dirName, dirSizes.getInteger(dir));
                colors.put(dirName, ChartUtils.getStringColor(dirName));
            } else {
                otherSum += dirSizes.get(dir);
            }
        }
        data.setValue(Messages.getString("PIE_MODSIZE_OTHER"), new Integer(otherSum));
        colors.put(Messages.getString("PIE_MODSIZE_OTHER"), Color.GRAY);

        final JFreeChart chart = ChartFactory.createPieChart(this.config.getProjectName() + ": " + title, data, false, false, false);

        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setShadowPaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(Color.LIGHT_GRAY);
        plot.setForegroundAlpha(0.8f);
        plot.setSectionOutlinePaint(Color.BLACK);
        it = colors.entrySet().iterator();
        while (it.hasNext()) {
            final Entry entry = (Entry) it.next();
            plot.setSectionPaint((String) entry.getKey(), (Paint) entry.getValue());
        }

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(chartName));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(chartName));
        ChartConfigUtil.configureCopyrightNotice(chartName, chart);
        ChartConfigUtil.configureChartBackgroungImage(chartName, chart);
        ChartConfigUtil.configurePlotImage(chartName, chart);

        final Dimension dim = ChartConfigUtil.getDimension(chartName, config.getLargeChartSize());

        return this.config.createChartImage(this.fileName, this.title, chart, dim);
    }

    protected abstract int calculateValue(Directory directory);

    public static class DirectorySizesChartMaker extends DirectoryPieChartMaker {
        public DirectorySizesChartMaker(final ReportConfig config) {
            super("directory_sizes", config, config.getRepository().getDirectories(), Messages.getString("PIE_DIRSIZE_SUBTITLE"), "directory_sizes.png");
        }

        protected int calculateValue(final Directory directory) {
            int result = 0;
            final Iterator fileIt = directory.getFiles().iterator();
            while (fileIt.hasNext()) {
                final VersionedFile element = (VersionedFile) fileIt.next();
                result += element.getCurrentLinesOfCode();
            }
            return result;
        }
    }

    public static class CodeDistributionChartMaker extends DirectoryPieChartMaker {
        private static String getFileName(final Author author) {
            return "directory_sizes_" + HTML.escapeAuthorName(author.getName()) + ".png";
        }

        private final Author author;

        public CodeDistributionChartMaker(final ReportConfig config, final Author author) {
            super("directory_sizes", config, author.getDirectories(), Messages.getString("PIE_CODEDISTRIBUTION_SUBTITLE") + " " + author.getRealName(),
                    getFileName(author));
            this.author = author;
        }

        public ChartImage toFile() {
            final Iterator it = this.author.getRevisions().iterator();
            int totalLinesOfCode = 0;
            while (it.hasNext()) {
                final Revision rev = (Revision) it.next();
                totalLinesOfCode += rev.getNewLines();
            }
            if (totalLinesOfCode == 0) {
                return null;
            }
            return super.toFile();
        }

        protected int calculateValue(final Directory directory) {
            int result = 0;
            final Iterator it = directory.getRevisions().iterator();
            while (it.hasNext()) {
                final Revision rev = (Revision) it.next();
                if (!this.author.equals(rev.getAuthor())) {
                    continue;
                }
                result += rev.getNewLines();
            }
            return result;
        }
    }
}
package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Module;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.pages.HTML;
import net.sf.statcvs.reports.LOCSeriesBuilder;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Produces Lines Of Code charts
 * 
 * TODO: At least the single-series charts should be done by TimeLineChartMakers
 * 
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: LOCChartMaker.java,v 1.19 2009/08/31 19:16:35 benoitx Exp $
 */
public class LOCChartMaker {
    private final ReportConfig config;
    private ChartImage chartFile = null;
    private final String chartName;

    /**
     * Creates a Lines Of Code chart from a <tt>BasicTimeSeries</tt> and
     * saves it as PNG
     * @param locSeries the LOC history
     * @param title the chart title
     * @param fileName the filename where the chart will be saved
     * @param size width and height of PNG in pixels
     * @param annotations
     */
    public LOCChartMaker(final String chartName, final ReportConfig config, final TimeSeries locSeries, final String title, final String fileName,
            final Dimension size, final List annotations) {
        this.chartName = chartName;
        this.config = config;
        if (locSeries == null) {
            return;
        }
        final Paint[] colors = new Paint[1];
        colors[0] = Color.RED;

        final TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(locSeries);
        final JFreeChart chart = createLOCChart(collection, colors, title, annotations);
        final Dimension dim = ChartConfigUtil.getDimension(chartName, size);
        this.chartFile = this.config.createChartImage(fileName, title, chart, dim);
    }

    /**
     * Creates a Lines Of Code chart from a list of <tt>BasicTimesSeries</tt> and
     * saves it as PNG
     * @param locSeriesList a list of <tt>BasicTimesSeries</tt>
     * @param title the chart title
     * @param fileName the filename where the chart will be saved
     * @param size width and height of PNG in pixels
     */
    public LOCChartMaker(final String chartName, final ReportConfig config, final List locSeriesList, final String title, final String fileName,
            final Dimension size, final List annotations) {
        this.chartName = chartName;
        this.config = config;
        if (locSeriesList.isEmpty()) {
            return;
        }
        int i = 0;
        final TimeSeriesCollection collection = new TimeSeriesCollection();
        final Iterator it = locSeriesList.iterator();
        while (it.hasNext()) {
            final TimeSeries series = (TimeSeries) it.next();
            collection.addSeries(series);
            i++;
        }
        final JFreeChart chart = createLOCChart(collection, null, title, annotations);
        final Dimension dim = ChartConfigUtil.getDimension(chartName, size);

        this.chartFile = this.config.createChartImage(fileName, title, chart, dim);
    }

    private JFreeChart createLOCChart(final TimeSeriesCollection data, final Paint[] colors, final String title, final List annotations) {
        final String domain = Messages.getString("TIME_LOC_DOMAIN");
        final String range = Messages.getString("TIME_LOC_RANGE");

        final boolean legend = (data.getSeriesCount() > 1);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(this.config.getProjectName() + ": " + title, domain, range, data, legend, false, false);

        final XYPlot plot = chart.getXYPlot();
        plot.setRenderer(new XYStepRenderer());
        if (colors == null) {
            // We don't like the bright yellow color early on in the series, use a darker one
            for (int i = 0; i < plot.getSeriesCount(); i++) {
                final Paint seriesPaint = plot.getRenderer().getSeriesPaint(i);
                if (seriesPaint != null && seriesPaint.equals(new Color(0xFF, 0xFF, 0x55))) {
                    plot.getRenderer().setSeriesPaint(i, new Color(240, 220, 0x55));
                }
            }
        } else {
            for (int i = 0; i < colors.length; i++) {
                plot.getRenderer().setSeriesPaint(i, colors[i]);
            }
        }
        final DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setVerticalTickLabels(true);
        final ValueAxis valueAxis = plot.getRangeAxis();
        valueAxis.setLowerBound(0);

        if (annotations != null) {
            for (final Iterator it = annotations.iterator(); it.hasNext();) {
                plot.addAnnotation((XYAnnotation) it.next());
            }
        }

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(chartName));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(chartName));
        final XYItemRenderer renderer = plot.getRenderer();
        ChartConfigUtil.configureStroke(chartName, renderer, data);
        ChartConfigUtil.configureShapes(chartName, renderer);
        ChartConfigUtil.configureCopyrightNotice(chartName, chart);
        ChartConfigUtil.configureChartBackgroungImage(chartName, chart);
        ChartConfigUtil.configurePlotImage(chartName, chart);

        return chart;
    }

    public ChartImage toFile() {
        return this.chartFile;
    }

    private static TimeSeries getLOCTimeSeries(final SortedSet revisions, final String title) {
        final LOCSeriesBuilder locCounter = new LOCSeriesBuilder(title, true);
        final Iterator it = revisions.iterator();
        while (it.hasNext()) {
            locCounter.addRevision((Revision) it.next());
        }
        if (locCounter.getMaximum() == 0) {
            return null;
        }
        return locCounter.getTimeSeries();
    }

    public static class MainLOCChartMaker extends LOCChartMaker {
        public MainLOCChartMaker(final String chartName, final ReportConfig config, final String fileName, final Dimension size) {
            super(chartName, config, getLOCTimeSeries(config.getRepository().getRevisions(), Messages.getString("TIME_LOC_SUBTITLE")), Messages
                    .getString("TIME_LOC_SUBTITLE"), fileName, size, SymbolicNameAnnotation.createAnnotations(config.getRepository().getSymbolicNames()));
        }
    }

    public static class DirectoryLOCChartMaker extends LOCChartMaker {
        private static String getTitle(final Directory directory) {
            return directory.getPath() + (directory.getPath() != null && directory.getPath().length() > 1 ? " " : "") + Messages.getString("TIME_LOC_SUBTITLE");
        }

        private static String getFilename(final Directory directory) {
            return "loc_module" + HTML.escapeDirectoryName(directory.getPath()) + ".png";
        }

        public DirectoryLOCChartMaker(final ReportConfig config, final Directory directory) {
            super("loc_module", config, getLOCTimeSeries(directory.getRevisions(), getTitle(directory)), getTitle(directory), getFilename(directory), config
                    .getLargeChartSize(), SymbolicNameAnnotation.createAnnotations(config.getRepository().getSymbolicNames()));
        }
    }

    public static class AllDevelopersLOCChartMaker extends LOCChartMaker {
        private static List createAllDevelopersLOCSeries(final ReportConfig config) {
            Iterator it = config.getRepository().getAuthors().iterator();
            final Map authorSeriesMap = new HashMap();
            while (it.hasNext()) {
                final Author author = (Author) it.next();
                if (!config.isDeveloper(author)) {
                    continue;
                }
                authorSeriesMap.put(author, new LOCSeriesBuilder(author.getRealName(), false));
            }
            it = config.getRepository().getRevisions().iterator();
            while (it.hasNext()) {
                final Revision rev = (Revision) it.next();
                if (rev.isBeginOfLog()) {
                    continue;
                }
                final LOCSeriesBuilder builder = (LOCSeriesBuilder) authorSeriesMap.get(rev.getAuthor());
                if (builder != null) {
                    builder.addRevision(rev);
                } // otherwise the revision was by a non-developer login
            }
            final List authors = new ArrayList(authorSeriesMap.keySet());
            Collections.sort(authors);
            final List result = new ArrayList();
            it = authors.iterator();
            while (it.hasNext()) {
                final Author author = (Author) it.next();
                final LOCSeriesBuilder builder = (LOCSeriesBuilder) authorSeriesMap.get(author);
                final TimeSeries series = builder.getTimeSeries();
                if (series != null) {
                    result.add(series);
                }
            }
            return result;
        }

        public AllDevelopersLOCChartMaker(final ReportConfig config, final Dimension size) {
            super("loc_per_author", config, createAllDevelopersLOCSeries(config), Messages.getString("CONTRIBUTED_LOC_TITLE"), "loc_per_author.png", size,
                    SymbolicNameAnnotation.createAnnotations(config.getRepository().getSymbolicNames()));
        }
    }

    public static class AllDirectoriesLOCChartMaker extends LOCChartMaker {
        private static Collection getMajorDirectories(final Repository repository, final int max) {
            if (repository.getFirstDate() == null || repository.getLastDate() == null || repository.getFirstDate().equals(repository.getLastDate())) {
                return Collections.EMPTY_LIST;
            }
            final IntegerMap importances = new IntegerMap();
            final Iterator it = repository.getDirectories().iterator();
            while (it.hasNext()) {
                final Directory directory = (Directory) it.next();
                importances.put(directory, getImportance(directory, repository.getFirstDate(), repository.getLastDate()));
            }
            final List result = new ArrayList(repository.getDirectories());
            Collections.sort(result, new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    final int importance1 = importances.get(o1);
                    final int importance2 = importances.get(o2);
                    if (importance1 > importance2) {
                        return -1;
                    }
                    if (importance1 == importance2) {
                        return 0;
                    }
                    return 1;
                }
            });
            return firstN(result, max);
        }

        private static int getImportance(final Directory dir, final Date start, final Date end) {
            final long timeRange = end.getTime() - start.getTime();
            double maxImportance = 0;
            int currentLines = 0;
            final Iterator it = dir.getRevisions().iterator();
            while (it.hasNext()) {
                final Revision revision = (Revision) it.next();
                currentLines += revision.getLinesDelta();
                final long timeInRange = revision.getDate().getTime() - start.getTime();
                final double timeFraction = (timeInRange / (double) timeRange) * 0.9 + 0.1;
                maxImportance = Math.max(maxImportance, (currentLines) * (timeFraction));
            }
            return (int) (maxImportance * 10);
        }

        private static List firstN(final List list, final int n) {
            return list.subList(0, Math.min(list.size(), n));
        }

        private static List createAllDirectoriesLOCSeries(final Repository repository, final int max) {
            Iterator it = getMajorDirectories(repository, max).iterator();
            final Map directorySeriesMap = new HashMap();
            while (it.hasNext()) {
                final Directory directory = (Directory) it.next();
                directorySeriesMap.put(directory, new LOCSeriesBuilder(directory.getPath(), true));
            }
            it = repository.getRevisions().iterator();
            while (it.hasNext()) {
                final Revision rev = (Revision) it.next();
                if (rev.isBeginOfLog()) {
                    continue;
                }
                final LOCSeriesBuilder builder = (LOCSeriesBuilder) directorySeriesMap.get(rev.getFile().getDirectory());
                if (builder == null) {
                    continue; // minor directory
                }
                builder.addRevision(rev);
            }
            final List directories = new ArrayList(directorySeriesMap.keySet());
            Collections.sort(directories);
            final List result = new ArrayList();
            it = directories.iterator();
            while (it.hasNext()) {
                final Directory directory = (Directory) it.next();
                final LOCSeriesBuilder builder = (LOCSeriesBuilder) directorySeriesMap.get(directory);
                final TimeSeries series = builder.getTimeSeries();
                if (series != null) {
                    result.add(series);
                }
            }
            return result;
        }

        public AllDirectoriesLOCChartMaker(final ReportConfig config, final int showMaxDirectories) {
            super("directories_loc_timeline", config, createAllDirectoriesLOCSeries(config.getRepository(), showMaxDirectories), Messages
                    .getString("DIRECTORY_LOC_TITLE"), "directories_loc_timeline.png", config.getLargeChartSize(), SymbolicNameAnnotation
                    .createAnnotations(config.getRepository().getSymbolicNames()));
        }
    }


    public static class AllModulesLOCChartMaker extends LOCChartMaker {
        private static Collection getMajorModules(final Repository repository, final int max) {
            if (repository.getFirstDate() == null || repository.getLastDate() == null || repository.getFirstDate().equals(repository.getLastDate())) {
                return Collections.EMPTY_LIST;
            }
            final IntegerMap importances = new IntegerMap();
            final Iterator it = repository.getModules().values().iterator();
            while (it.hasNext()) {
                final Module directory = (Module) it.next();
                importances.put(directory, getImportance(directory, repository.getFirstDate(), repository.getLastDate()));
            }
            final List result = new ArrayList(repository.getModules().values());
            Collections.sort(result, new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    final int importance1 = importances.get(o1);
                    final int importance2 = importances.get(o2);
                    if (importance1 > importance2) {
                        return -1;
                    }
                    if (importance1 == importance2) {
                        return 0;
                    }
                    return 1;
                }
            });
            return firstN(result, max);
        }

        private static int getImportance(final Module dir, final Date start, final Date end) {
            final long timeRange = end.getTime() - start.getTime();
            double maxImportance = 0;
            int currentLines = 0;
            final Iterator it = dir.getRevisions().iterator();
            while (it.hasNext()) {
                final Revision revision = (Revision) it.next();
                currentLines += revision.getLinesDelta();
                final long timeInRange = revision.getDate().getTime() - start.getTime();
                final double timeFraction = (timeInRange / (double) timeRange) * 0.9 + 0.1;
                maxImportance = Math.max(maxImportance, (currentLines) * (timeFraction));
            }
            return (int) (maxImportance * 10);
        }

        private static List firstN(final List list, final int n) {
            return list.subList(0, Math.min(list.size(), n));
        }

        private static List createAllModulesLOCSeries(final Repository repository, final int max) {
            Iterator it = getMajorModules(repository, max).iterator();
            final Map directorySeriesMap = new HashMap();
            while (it.hasNext()) {
                final Module module = (Module) it.next();
                directorySeriesMap.put(module, new LOCSeriesBuilder(module.getName(), true));
            }
            it = repository.getRevisions().iterator();
            while (it.hasNext()) {
                final Revision rev = (Revision) it.next();
                if (rev.isBeginOfLog()) {
                    continue;
                }
                final LOCSeriesBuilder builder = (LOCSeriesBuilder) directorySeriesMap.get(rev.getFile().getModule());
                if (builder == null) {
                    continue; // minor directory
                }
                builder.addRevision(rev);
            }
            final List modules = new ArrayList(directorySeriesMap.keySet());
            Collections.sort(modules);
            final List result = new ArrayList();
            it = modules.iterator();
            while (it.hasNext()) {
                final Module module = (Module) it.next();
                final LOCSeriesBuilder builder = (LOCSeriesBuilder) directorySeriesMap.get(module);
                final TimeSeries series = builder.getTimeSeries();
                if (series != null) {
                    result.add(series);
                }
            }
            return result;
        }

        public AllModulesLOCChartMaker(final ReportConfig config, final int showMaxModules) {
            super("modules_loc_timeline", config, createAllModulesLOCSeries(config.getRepository(), showMaxModules), Messages
                    .getString("MODULES_LOC_TITLE"), "modules_loc_timeline.png", config.getLargeChartSize(), SymbolicNameAnnotation
                    .createAnnotations(config.getRepository().getSymbolicNames()));
        }
    }
}

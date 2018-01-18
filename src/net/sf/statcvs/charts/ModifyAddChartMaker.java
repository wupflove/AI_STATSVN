package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Class for producing the "Author Activity: Modifying/Adding" chart
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ModifyAddChartMaker.java,v 1.9 2009/04/25 16:36:20 benoitx Exp $
 */
public class ModifyAddChartMaker {
    private static final String CHART_NAME = "activity";
    private static final int MODIFYING = 0;
    private static final int ADDING = 1;

    private final ReportConfig config;
    private final Repository repository;
    private final int width;
    private double[][] categories;
    private final ArrayList categoryNames = new ArrayList();

    /**
     * Creates a new ModifyAddChartMaker
     * @param content Repository
     * @param width width of the chart in pixels
     */
    public ModifyAddChartMaker(final ReportConfig config, final int width) {
        this.config = config;
        this.repository = config.getRepository();
        this.width = width;
    }

    public ChartImage toFile() {
        final Collection authors = this.repository.getAuthors();
        final Iterator it = authors.iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            if (!config.isDeveloper(author)) {
                continue;
            }
            categoryNames.add(author.getRealName());
        }
        Collections.sort(categoryNames);

        categories = new double[2][categoryNames.size()];
        for (int j = 0; j < categoryNames.size(); j++) {
            categories[MODIFYING][j] = 0;
            categories[ADDING][j] = 0;
        }

        final Iterator commitIt = this.repository.getCommits().iterator();
        while (commitIt.hasNext()) {
            final Commit commit = (Commit) commitIt.next();
            final Set commitRevList = commit.getRevisions();
            final Iterator commitRevIt = commitRevList.iterator();
            final String authorName = commit.getAuthor().getRealName();
            if (authorName == null) {
                continue;
            }
            final int author = categoryNames.indexOf(authorName);
            if (author == -1) {
                continue;
            }
            int linesAdded = 0;
            int linesRemoved = 0;
            while (commitRevIt.hasNext()) {
                final Revision revision = (Revision) commitRevIt.next();
                if (revision.getLinesDelta() > 0) {
                    linesAdded += revision.getLinesDelta() + revision.getReplacedLines();
                    linesRemoved += revision.getReplacedLines();
                } else {
                    linesAdded += revision.getReplacedLines();
                    linesRemoved += -revision.getLinesDelta() + revision.getReplacedLines();
                }
            }
            if (linesAdded == linesRemoved) {
                categories[MODIFYING][author] += linesAdded;
            }
            if (linesAdded < linesRemoved) {
                categories[MODIFYING][author] += linesRemoved;
            }
            if (linesAdded > linesRemoved) {
                categories[ADDING][author] += linesAdded - linesRemoved;
                categories[MODIFYING][author] += linesRemoved;
            }
        }

        for (int i = 0; i < categoryNames.size(); i++) {
            final double maxLines = categories[MODIFYING][i] + categories[ADDING][i];
            for (int k = 0; k < 2; k++) {
                categories[k][i] *= (100 / maxLines);
            }
        }

        final DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (int i = 0; i < categories[MODIFYING].length; i++) {
            data.addValue(categories[MODIFYING][i], "modifying", (Comparable) categoryNames.get(i));
        }
        for (int j = 0; j < categories[ADDING].length; j++) {
            data.addValue(categories[ADDING][j], "adding", (Comparable) categoryNames.get(j));
        }
        //data.setSeriesName(MODIFYING, "modifying");
        //data.setSeriesName(ADDING, "adding");
        //data.setCategories(categoryNames.toArray());

        final JFreeChart chart = ChartFactory.createStackedBarChart(this.config.getProjectName() + ": " + Messages.getString("AUTHOR_ACTIVITY_TITLE"), "", "%",
                data, PlotOrientation.HORIZONTAL, true, false, false);

        final CategoryPlot plot = chart.getCategoryPlot();
        //plot.setSeriesPaint(new Paint[] { Color.yellow, Color.green });
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.yellow);
        renderer.setSeriesPaint(1, Color.green);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickUnit(new NumberTickUnit(20.0, new DecimalFormat("0")));
        rangeAxis.setUpperBound(100.0);

        chart.getLegend().setPosition(RectangleEdge.TOP);

        plot.setBackgroundPaint(ChartConfigUtil.getPlotColor(CHART_NAME));
        chart.setBackgroundPaint(ChartConfigUtil.getBackgroundColor(CHART_NAME));
        //        final XYItemRenderer renderer = plot.getRenderer();
        //        ChartConfigUtil.configureStroke(CHART_NAME, renderer, data);
        //        ChartConfigUtil.configureShapes(CHART_NAME, renderer);
        ChartConfigUtil.configureCopyrightNotice(CHART_NAME, chart);
        ChartConfigUtil.configureChartBackgroungImage(CHART_NAME, chart);
        ChartConfigUtil.configurePlotImage(CHART_NAME, chart);

        final Dimension dim = ChartConfigUtil.getDimension(CHART_NAME, new Dimension(config.getSmallChartSize().width, 19));
        final int totalHeight = dim.height * this.categoryNames.size() + 110;
        return this.config.createChartImage(CHART_NAME + ".png", Messages.getString("AUTHOR_ACTIVITY_TITLE"), chart, new Dimension(dim.width, totalHeight));
    }
}

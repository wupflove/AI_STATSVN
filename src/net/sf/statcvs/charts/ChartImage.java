package net.sf.statcvs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * An image file for a chart.
 * 
 * TODO: Better integrate all charts with ReportConfig 
 * @author jentzsch
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ChartImage.java,v 1.9 2009/03/14 11:08:35 benoitx Exp $
 */
public class ChartImage {
    private static final Logger logger = LogManager.getLogger();
//    public final static Color BACKGROUND_COLOR = new Color(204, 204, 187);
//    public final static Color PLOT_COLOR = new Color(100, 204, 187);
    public final static Color BACKGROUND_COLOR = new Color(255, 255, 255);
    public final static Color PLOT_COLOR = new Color(0xD0, 0xE0, 0xE0);
    
    private final String rootDirectory;
    private JFreeChart chart;
    private final String fileName;
    private final Dimension size;
    private final String title;
    private boolean written = false;

    /**
     * Creates a new ChartFile.
     * @param rootDirectory The report root directory with trailing slash
     * @param fileName The relative file name for the chart, with .png extension
     * @param title The chart's title
     * @param chart The JFreeChart object to save as a file
     * @param size width and height of the chart in pixels
     */
    public ChartImage(final String rootDirectory, final String fileName, final String title, final JFreeChart chart, final Dimension size) {
        this.rootDirectory = rootDirectory;
        this.fileName = fileName;
        this.title = title;
        this.chart = chart;
        this.size = size;
        //        chart.setBackgroundPaint(BACKGROUND_COLOR);
    }

    /**
     * Writes the chart to disk as a PNG file.
     */
    public void write() {
        if (this.written) {
            return;
        }
        logger.info("writing chart '" + this.title + "' to " + this.fileName);
        try {
            ChartUtilities.saveChartAsPNG(new File(rootDirectory + fileName), chart, size.width, size.height);
        } catch (final IOException e) {
            logger.warn("could not write chart '" + fileName + "': " + e);
        }
        this.written = true;
        this.chart = null; // Free memory? Not sure if this has any effect ... 
    }

    /**
     * Returns the chart's URL, relative to the report root.
     */
    public String getURL() {
        return this.fileName;
    }

    /**
     * Returns the chart's title.
     */
    public String getFullTitle() {
        return this.title;
    }

    /**
     * Returns the chart's width in pixels.
     */
    public int getWidth() {
        return this.size.width;
    }

    /**
     * Returns the chart's height in pixels.
     */
    public int getHeight() {
        return this.size.height;
    }
}
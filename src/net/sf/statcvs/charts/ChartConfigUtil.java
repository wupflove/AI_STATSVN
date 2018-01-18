/**
 * 
 */
package net.sf.statcvs.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import net.sf.statcvs.output.ConfigurationOptions;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Align;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jpf.statsvn.util.StringUtils;

/**
 * @author benoitx
 *
 */
public final class ChartConfigUtil {
    private ChartConfigUtil() {
    }

    /**
     * returns the background color from the config file, tries properties:
     * <pre>
     * chart.&lt;chartName&gt;.backgroundColor
     * chart.backgroundColor
     * </pre>
     * @param chartName
     */
    public static Color getBackgroundColor(final String chartName) {
        return ConfigurationOptions.getConfigColorProperty("chart." + chartName + ".backgroundColor", "chart.backgroundColor", ChartImage.BACKGROUND_COLOR);
    }

    /**
     * returns the PLOT color from the config file, tries properties:
     * <pre>
     * chart.&lt;chartName&gt;.plotColor
     * chart.plotColor
     * </pre>
     * @param chartName
     */
    public static Color getPlotColor(final String chartName) {
        return ConfigurationOptions.getConfigColorProperty("chart." + chartName + ".plotColor", "chart.plotColor", ChartImage.BACKGROUND_COLOR);
    }

    /**
     * returns the PLOT color from the config file, tries properties:
     * <pre>
     * chart.&lt;chartName&gt;.width
     * chart.width
     * chart.&lt;chartName&gt;.height
     * chart.height
     * </pre>
     * @param chartName
     */
    public static Dimension getDimension(final String chartName, final Dimension defaultDimension) {
        final Integer width = ConfigurationOptions
                .getConfigIntegerProperty("chart." + chartName + ".width", "chart.width", new Integer(defaultDimension.width));

        final Integer height = ConfigurationOptions.getConfigIntegerProperty("chart." + chartName + ".height", "chart.height", new Integer(
                defaultDimension.height));

        return new Dimension(width.intValue(), height.intValue());
    }

    /**
     * configure the lines for the chart, tries properties:
     * <pre>
     * chart.&lt;chartName&gt;.lineStroke
     * chart.lineStroke
     * </pre>
     * @param chartName
     */
    public static void configureStroke(final String chartName, final XYItemRenderer renderer, final XYDataset data) {
        final Float stroke = ConfigurationOptions.getConfigFloatProperty("chart." + chartName + ".lineStroke", "chart.lineStroke", null);
        if (stroke != null) {
            for (int i = 0; i < data.getSeriesCount(); i++) {
                renderer.setSeriesStroke(i, new BasicStroke(stroke.floatValue()));
            }
        }
    }

    /**
     * configure the shapes for the chart (if renderer is of type XYLineAndShapeRenderer) , tries properties:
     * <pre>
     * chart.&lt;chartName&gt;.showShapes
     * chart.filledShapes
     * </pre>
     * @param chartName
     */
    public static void configureShapes(final String chartName, final XYItemRenderer renderer) {
        if (renderer instanceof XYLineAndShapeRenderer) {
            final Boolean showShapes = ConfigurationOptions.getConfigBooleanProperty("chart." + chartName + ".showShapes", "chart.showShapes", Boolean.FALSE);
            final Boolean filledShapes = ConfigurationOptions.getConfigBooleanProperty("chart." + chartName + ".filledShapes", "chart.filledShapes",
                    Boolean.FALSE);

            ((XYLineAndShapeRenderer) renderer).setBaseShapesVisible(showShapes.booleanValue());
            ((XYLineAndShapeRenderer) renderer).setBaseShapesFilled(filledShapes.booleanValue());
        }
    }

    /**
     * Add a copyright notice on the bottom right part of the chart.
     * <pre>
     * chart.&lt;chartName&gt;.chartBackgroundImage.url
     * chart.chartBackgroundImage.url
     * chart.&lt;chartName&gt;.chartBackgroundImage.transparency
     * chart.chartBackgroundImage.transparency (0-1, defaulted to 0.35)
     * </pre>
     * @param chartName
     * @param chart
     */
    public static void configureChartBackgroungImage(final String chartName, final JFreeChart chart) {
        final String imageLocation = ConfigurationOptions.getConfigStringProperty("chart." + chartName + ".chartBackgroundImage.url",
                "chart.chartBackgroundImage.url", null);
        final Float alpha = ConfigurationOptions.getConfigFloatProperty("chart." + chartName + ".chartBackgroundImage.transparency",
                "chart.chartBackgroundImage.transparency", new Float(0.35));
        if (StringUtils.isNotEmpty(imageLocation) && alpha != null) {
            Image image = null;
            try {
                image = new ImageIcon(new URL(imageLocation)).getImage();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (image != null) {
                chart.setBackgroundImageAlpha(alpha.floatValue());
                chart.setBackgroundImageAlignment(Align.BOTTOM_LEFT);
                chart.setBackgroundImage(image);
            }
        }
    }

    /**
     * Add a copyright notice on the bottom right part of the chart.
     * <pre>
     * chart.&lt;chartName&gt;.plotImage.url eg file:///C:/project/statcvs/site/images/statcvslogo.gif
     * chart.plotImage.url
     * chart.&lt;chartName&gt;.plotImage.transparency
     * chart.plotImage.transparency (0-1, default to 0.35)
     * </pre>
     * @param chartName
     * @param chart
     */
    public static void configurePlotImage(final String chartName, final JFreeChart chart) {
        final String imageLocation = ConfigurationOptions.getConfigStringProperty("chart." + chartName + ".plotImage.url", "chart.plotImage.url",
                null);
        final Float alpha = ConfigurationOptions.getConfigFloatProperty("chart." + chartName + ".plotImage.transparency", "chart.plotImage.transparency",
                new Float(0.35));
        if (StringUtils.isNotEmpty(imageLocation) && alpha != null) {
            Image image = null;
            try {
                image = new ImageIcon(new URL(imageLocation)).getImage();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (image != null) {
                final Plot plot = chart.getPlot();
                plot.setBackgroundImageAlpha(alpha.floatValue());
                plot.setBackgroundImage(image);
            }
        }
    }

    /**
     * Add a copyright notice on the bottom right part of the chart.
     * <pre>
     * chart.&lt;chartName&gt;.copyright
     * chart.copyright
     * </pre>
     * @param chartName
     * @param chart
     */
    public static void configureCopyrightNotice(final String chartName, final JFreeChart chart) {
        final String copyrightTxt = ConfigurationOptions.getConfigStringProperty("chart." + chartName + ".copyright", "chart.copyright", null);
        final Integer copyrightTxtSize = ConfigurationOptions.getConfigIntegerProperty("chart." + chartName + ".copyrightTextSize", "chart.copyrightTextSize",
                new Integer(9));
        if (StringUtils.isNotEmpty(copyrightTxt)) {
            final TextTitle copyright = new TextTitle(copyrightTxt, new Font("SansSerif", Font.PLAIN, copyrightTxtSize.intValue()));

            copyright.setPosition(RectangleEdge.BOTTOM);
            copyright.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            chart.addSubtitle(copyright);
        }
    }
}

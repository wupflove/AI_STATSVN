package net.sf.statcvs.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.SymbolicName;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * SymbolicNameAnnotation
 * 
 * Provides symbolic name annotations for XYPlots with java.util.Date
 * objects on the domain axis.
 * 
 * TODO: Move vertically to accommodate annotations that are close together 
 * 
 * @author Tammo van Lessen
 * @version $Id: SymbolicNameAnnotation.java,v 1.6 2008/04/02 11:22:15 benoitx Exp $
 */
public class SymbolicNameAnnotation implements XYAnnotation {
    public static final int STYLE_DEFAULT = 0;
    public static final int STYLE_NO_LABELS = 1;
    public static final float MIN_LABEL_X_SPACING = 7.0f;

    public static List createAnnotations(final Collection symbolicNames) {
        return createAnnotations(symbolicNames, STYLE_DEFAULT);
    }

    public static List createAnnotations(final Collection symbolicNames, final int style) {
        final List annotations = new ArrayList();
        for (final Iterator it = symbolicNames.iterator(); it.hasNext();) {
            final SymbolicName sn = (SymbolicName) it.next();
            annotations.add(new SymbolicNameAnnotation(sn, symbolicNames, style));
        }
        return annotations;
    }

    private final Color linePaint = Color.GRAY;
    private final Color textPaint = Color.DARK_GRAY;
    private final Font font = new Font("Dialog", Font.PLAIN, 9);
    private final static Stroke STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 3.5f }, 0.0f);

    private final SymbolicName symbolicName;
    private final List allSymbolicNames;
    private final int style;

    /**
     * Creates an annotation for a symbolic name.
     * Paints a gray dashed vertical line at the symbolic names
     * date position and draws its name at the top left.
     * 
     * @param symbolicName
     * @param style {@link #STYLE_HEAVY} or {@link #STYLE_LIGHT}
     */
    public SymbolicNameAnnotation(final SymbolicName symbolicName, final Collection all, final int style) {
        this.symbolicName = symbolicName;
        this.allSymbolicNames = new ArrayList(all);
        Collections.sort(this.allSymbolicNames);
        this.style = style;
    }

    /**
     * @see org.jfree.chart.annotations.XYAnnotation#draw(java.awt.Graphics2D, org.jfree.chart.plot.XYPlot, java.awt.geom.Rectangle2D, org.jfree.chart.axis.ValueAxis, org.jfree.chart.axis.ValueAxis, int, org.jfree.chart.plot.PlotRenderingInfo)
     */
    public void draw(final Graphics2D g2d, final XYPlot xyPlot, final Rectangle2D dataArea, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final int rendererIndex, final PlotRenderingInfo info) {
        final PlotOrientation orientation = xyPlot.getOrientation();

        // don't draw the annotation if symbolic names date is out of axis' bounds.
        if (domainAxis.getUpperBound() < symbolicName.getDate().getTime() || domainAxis.getLowerBound() > symbolicName.getDate().getTime()) {

            return;
        }

        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(xyPlot.getDomainAxisLocation(), orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(xyPlot.getRangeAxisLocation(), orientation);

        final float x = getNaturalX(symbolicName, dataArea, domainAxis, domainEdge);

        final float y1 = (float) rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, rangeEdge);
        final float y2 = (float) rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, rangeEdge);

        g2d.setPaint(linePaint);
        g2d.setStroke(STROKE);

        final Line2D line = new Line2D.Float(x, y1, x, y2);
        g2d.draw(line);

        if (this.style == STYLE_NO_LABELS) {
            return;
        }
        g2d.setFont(font);
        g2d.setPaint(textPaint);
        TextUtilities.drawRotatedString(symbolicName.getName(), g2d, getArrangedLabelX(symbolicName, dataArea, domainAxis, domainEdge), y1 + 2,
                TextAnchor.BOTTOM_RIGHT, -Math.PI / 2, TextAnchor.BOTTOM_RIGHT);
    }

    private float getArrangedLabelX(final SymbolicName tag, final Rectangle2D dataArea, final ValueAxis domainAxis, final RectangleEdge domainEdge) {
        final float naturalX = getNaturalX(tag, dataArea, domainAxis, domainEdge);
        final int offset = this.allSymbolicNames.indexOf(tag);
        if (offset == this.allSymbolicNames.size() - 1) {
            return naturalX;
        }
        final SymbolicName next = (SymbolicName) this.allSymbolicNames.get(offset + 1);
        final float nextX = getArrangedLabelX(next, dataArea, domainAxis, domainEdge);
        if (nextX > naturalX + MIN_LABEL_X_SPACING) {
            return naturalX;
        }
        return nextX - MIN_LABEL_X_SPACING;
    }

    private float getNaturalX(final SymbolicName tag, final Rectangle2D dataArea, final ValueAxis domainAxis, final RectangleEdge domainEdge) {
        return (float) domainAxis.valueToJava2D(tag.getDate().getTime(), dataArea, domainEdge);
    }
}
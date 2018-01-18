package net.sf.statcvs.charts;

import java.awt.Color;

/**
 * Utility class for chart related stuff.
 * 
 * @author Richard Cyganiak
 * @version $Id: ChartUtils.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class ChartUtils {
    private static final String MAGIC_SEED_1 = "0 Ax-!";
    private static final String MAGIC_SEED_2 = "!Z x5";

    private ChartUtils() {
        // Only static methods
    }

    /**
     * Returns a distinct <code>Color</code> for a <code>String</code> argument.
     * The algorithm tries to provide different colors for similar strings, and
     * will return equal colors for equal strings. The colors will all have
     * similar brightness and maximum intensity. Useful for chart coloring.
     * @param s a <code>String</code> to get a color for
     * @return a distinct <code>Color</code> for a <code>String</code> argument.
     * The algorithm tries to provide different colors for similar strings, and
     * will return equal colors for equal strings. The colors will all have
     * similar brightness and maximum intensity. Useful for chart coloring.
     */
    public static Color getStringColor(final String s) {
        double d = (MAGIC_SEED_1 + s + MAGIC_SEED_2).hashCode();
        d -= Integer.MIN_VALUE;
        d /= ((double) Integer.MAX_VALUE - (double) Integer.MIN_VALUE);
        d *= 3;
        // Now 0 <= d < 3
        if (d < 1) {
            final int r = (int) ((1 - d) * MAX_RED);
            final int g = (int) (d * MAX_GREEN);
            return new Color(r, g, 0);
        } else if (d < 2) {
            final int g = (int) ((2 - d) * MAX_GREEN);
            final int b = (int) ((d - 1) * MAX_BLUE);
            return new Color(0, g, b);
        } else {
            final int r = (int) ((d - 2) * MAX_RED);
            final int b = (int) ((3 - d) * MAX_BLUE);
            return new Color(r, 0, b);
        }
    }

    // Use values less than 256 here, or the colors will get too bright
    private static double MAX_RED = 200;
    private static double MAX_GREEN = 150;
    private static double MAX_BLUE = 250;
}
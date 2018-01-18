package org.jpf.statsvn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtils {
    /**
     * This method is a 1.4 replacement of the String.replace(CharSequence, CharSequence) found in 1.5.
     * @param originalPattern
     * @param newPattern
     * @param originalString
     * @return
     */
    public static String replace(final String originalPattern, final String newPattern, final String originalString) {
        if ((originalPattern == null) || (originalPattern.length() == 0) || (originalString == null)) {
            return originalString;
        }

        final StringBuffer newString = new StringBuffer(originalString.length());
        int index = 0;
        final int originalLength = originalPattern.length();
        int previousIndex = 0;

        while ((index = originalString.indexOf(originalPattern, index)) != -1) {
            newString.append(originalString.substring(previousIndex, index)).append(newPattern);
            index += originalLength;
            previousIndex = index;
        }

        if (previousIndex == 0) {
            newString.append(originalString);
        } else if (previousIndex != originalString.length()) {
            newString.append(originalString.substring(previousIndex));
        }

        return newString.toString();
    }
    
    /**
     * @return true if txt !=null and not empty.
     */
    public static boolean isNotEmpty(final String txt) {
        return txt != null && txt.trim().length() > 0;
    }

    /**
     * helper method to convert a 'delimiter' separated string to a list.
     * 
     * @param str
     *            the 'delimiter' separated string
     * @param delimiter
     *            typically a ','
     * @return a list
     */
    public static List listify(final String str, final String delimiter) {
        if (str == null) {
            return Collections.EMPTY_LIST;
        }

        final StringTokenizer tok = new StringTokenizer(str, delimiter);
        final List list = new ArrayList();

        while (tok.hasMoreElements()) {
            list.add(StringUtils.trim(tok.nextToken()));
        }

        return list;
    }

    public static String trim(String tok) {
        return tok != null ? tok.trim() : null;
    }


}

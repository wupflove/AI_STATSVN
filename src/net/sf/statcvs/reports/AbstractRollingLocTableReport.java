/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: AbstractRollingLocTableReport.java,v $ 
	Created on $Date: 2009/03/09 22:21:36 $ 
*/
package net.sf.statcvs.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.GenericColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Convenience superclass for table reports related to last n months for authors and directories.
 * Contains methods to calculate some common stuff for these tables.
 * @author Lukasz Pekacki
 * @author Benoit Xhenseval
 * @version $Id: AbstractRollingLocTableReport.java,v 1.3 2009/03/09 22:21:36 benoitx Exp $
 */
public abstract class AbstractRollingLocTableReport {
    private static final int NUMBER_OF_MONTHS = 12;
    /**
     * Sort the authors table by name
     * */
    public static final int SORT_BY_NAME = 0;

    /**
     * Sort the authors table by lines of code
     * */
    public static final int SORT_BY_LINES = 1;

    private final ReportConfig config;
    private final Repository content;

    private final List changesMaps;
    private final List linesMaps;
    private final IntegerMap authors = new IntegerMap();
    private Date cutOff;

    /**
     * Constructor
     * @param content render table on specified content
     */
    public AbstractRollingLocTableReport(final ReportConfig config) {
        this.config = config;
        this.content = config.getRepository();
        changesMaps = new ArrayList(NUMBER_OF_MONTHS + 1);
        linesMaps = new ArrayList(NUMBER_OF_MONTHS + 1);
        for (int i = 0; i < NUMBER_OF_MONTHS + 1; i++) {
            changesMaps.add(new IntegerMap());
            linesMaps.add(new IntegerMap());
        }
    }

    protected void calculateChangesAndLinesPerDeveloper(final Collection revs) {
        final Iterator it = revs.iterator();
        final Date last = content.getLastDate();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(last);
        cal.add(Calendar.MONTH, -NUMBER_OF_MONTHS + 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cutOff = cal.getTime();
        //        System.out.println("LAST DATE " + last);
        //        System.out.println("CUTO DATE " + cutOff);
        while (it.hasNext()) {
            final Revision rev = (Revision) it.next();
            if (rev.getAuthor() == null || !this.config.isDeveloper(rev.getAuthor())) {
                continue;
            }

            final IntegerMap changesMap = findMap(cal, rev.getDate(), cutOff, changesMaps);
            final IntegerMap linesMap = findMap(cal, rev.getDate(), cutOff, linesMaps);

            changesMap.addInt(rev.getAuthor(), 1);
            linesMap.addInt(rev.getAuthor(), rev.getNewLines());
            authors.addInt(rev.getAuthor(), rev.getNewLines());
        }
    }

    private IntegerMap findMap(final Calendar cal, final Date date, final Date cutOff, final List listOfMaps) {
        if (date.compareTo(cutOff) < 0) {
            return (IntegerMap) listOfMaps.get(0);
        }

        cal.setTime(cutOff);
        final int cutOffMonth = cal.get(Calendar.MONTH) + 1;
        cal.setTime(date);
        final int monthRev = cal.get(Calendar.MONTH) + 1;

        int idx = monthRev - cutOffMonth;

        if (idx < 0) {
            idx += 12;
        }

        //        System.out.println(" Date " + date + " / " + cutOffMonth + " vs " + monthRev + " Diff " + (monthRev + cutOffMonth) + " idx " + (idx));

        if (idx == 0) {
            idx = 12;
        }

        return (IntegerMap) listOfMaps.get(idx);
    }

    protected Table createChangesAndLinesTable(final GenericColumn keys, final GenericColumn keys2, final String summary) {
        final Table result = new Table(summary);
        keys.setTotal(Messages.getString("TOTALS"));
        result.addColumn(keys);
        if (keys2 != null) {
            keys.setTotal("");
            keys2.setTotal(Messages.getString("TOTALS"));
            result.addColumn(keys2);
        }
        result.setKeysInFirstColumn(true);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(cutOff);
        final List columns = new ArrayList();
        for (int i = 0; i < NUMBER_OF_MONTHS + 1; i++) {
            String title = null;
            if (i == 0) {
                title = "Up to " + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
            }
            if (i != 0) {
                cal.add(Calendar.MONTH, 1);
                title = "" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
            }
            final IntegerColumn column = new IntegerColumn(title);
            columns.add(column);
            result.addColumn(column);
        }

        final Iterator it = authors.iteratorSortedByValueReverse();

        while (it.hasNext()) {
            final Object key = it.next();
            keys.addValue(key);
            if (keys2 != null) {
                keys2.addValue(key);
            }
            //            System.out.println("Author " + key);
            for (int i = 0; i < NUMBER_OF_MONTHS + 1; i++) {
                final IntegerMap linesMap = (IntegerMap) linesMaps.get(i);
                ((IntegerColumn) columns.get(i)).addValue(linesMap.get(key));
            }
        }

        if (result.getRowCount() > 1) {
            result.setShowTotals(true);
        }
        return result;
    }

    protected Repository getContent() {
        return content;
    }

    public int getDeveloperCount() {
        int result = 0;
        final Iterator it = getContent().getAuthors().iterator();
        while (it.hasNext()) {
            final Author author = (Author) it.next();
            if (this.config.isDeveloper(author)) {
                result++;
            }
        }
        return result;
    }
}
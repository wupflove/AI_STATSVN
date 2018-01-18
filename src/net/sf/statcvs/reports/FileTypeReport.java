package net.sf.statcvs.reports;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.RatioColumn;
import net.sf.statcvs.reportmodel.SimpleTextColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

public class FileTypeReport implements TableReport {
    private final static String NON_CODE_FILES = "@@@NON-CODE FILES";
    private final static String NO_EXTENSION = "@@@NO EXTENSION";
    private final static Pattern extensionPattern = Pattern.compile(".*[^.]\\.([CH]|[a-z0-9_-]+)");

    private final ReportConfig config;
    private Table table;

    public FileTypeReport(final ReportConfig config) {
        this.config = config;
    }

    public void calculate() {
        final IntegerMap lines = new IntegerMap();
        final IntegerMap counts = new IntegerMap();
        lines.put(NON_CODE_FILES, 0);
        lines.put(NON_CODE_FILES, 0);
        Iterator it = this.config.getRepository().getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (file.isDead()) {
                continue;
            }
            if (file.getCurrentLinesOfCode() == 0) {
                counts.addInt(NON_CODE_FILES, 1);
            } else {
                lines.addInt(getExtension(file.getFilename()), file.getCurrentLinesOfCode());
                counts.addInt(getExtension(file.getFilename()), 1);
            }
        }

        final SimpleTextColumn typeCol = new SimpleTextColumn(Messages.getString("FILE_TYPE"));
        typeCol.setTotal("Totals");
        final IntegerColumn filesCol = new IntegerColumn(Messages.getString("FILES"));
        final IntegerColumn linesCol = new IntegerColumn(Messages.getString("FILE_LOC"));
        this.table = new Table("File Extensions");
        this.table.setShowTotals(true);
        this.table.setKeysInFirstColumn(true);
        this.table.addColumn(typeCol);
        this.table.addColumn(filesCol);
        this.table.addColumn(linesCol);
        this.table.addColumn(new RatioColumn(Messages.getString("LOC_PER_FILE"), linesCol, filesCol));

        double cumulativePercent = 0;
        int otherLines = 0;
        int otherCount = 0;
        it = lines.iteratorSortedByValueReverse();
        while (it.hasNext()) {
            final String extension = (String) it.next();
            if (NO_EXTENSION.equals(extension) || NON_CODE_FILES.equals(extension)) {
                continue;
            }
            if (cumulativePercent < 80 || this.table.getRowCount() < 10) {
                typeCol.addValue("*." + extension);
                filesCol.addValue(counts.get(extension));
                linesCol.addValue(lines.get(extension));
                cumulativePercent += lines.getPercent(extension);
            } else {
                otherCount += counts.get(extension);
                otherLines += lines.get(extension);
            }
        }
        counts.addInt(NO_EXTENSION, otherCount);
        lines.addInt(NO_EXTENSION, otherLines);
        if (counts.get(NO_EXTENSION) > 0) {
            typeCol.addValue("Others");
            filesCol.addValue(counts.get(NO_EXTENSION));
            linesCol.addValue(lines.get(NO_EXTENSION));
        }
        if (counts.get(NON_CODE_FILES) > 0) {
            typeCol.addValue("Non-Code Files");
            filesCol.addValue(counts.get(NON_CODE_FILES));
            linesCol.addValue(lines.get(NON_CODE_FILES));
        }
    }

    public Table getTable() {
        return this.table;
    }

    private String getExtension(final String fileName) {
        final Matcher m = extensionPattern.matcher(fileName);
        if (!m.matches()) {
            return NO_EXTENSION;
        }
        return m.group(1);
    }
}

package net.sf.statcvs.reports;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.SimpleTextColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Table report which A list of word frequencies in commit messages.
 * 
 * @author Benoit Xhenseval
 * @version $Id: CloudCommitTableReport.java,v 1.5 2009/05/08 13:18:48 benoitx Exp $
 */
public class CloudCommitTableReport implements TableReport {
    private static final String ENGLISH_EXCLUSIONS = "\\d+|an|the|me|my|we|you|he|she|it|are|is|am|will|shall|should|would|had|have|has|was|were|be|been|this|that|there|"
            + "|who|when|how|where|which|already|after|by|on|or|so|some|commit|also|got|get|do|don't|from|all|but|yet|to|in|does|doesn't"
            + "out|of|for|if|yes|no|not|may|might|can|could|at|as|with|without|some|more|lot|lots|than|then|adding|added|work|they|used|still|show|must|into|same";
    private Table table = null;
    private final ReportConfig config;
    private final Repository content;
    private final IntegerMap cloudMap = new IntegerMap();
    private Pattern excluded;

    /**
     * Creates a table report containing the top 10 authors and their
     * LOC contributions
     * @param content the version control source data
     */
    public CloudCommitTableReport(final ReportConfig config) {
        content = config.getRepository();
        this.config = config;
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#calculate()
     */
    public void calculate() {
        if (this.table != null) {
            return;
        }
        if (excluded == null) {
            excluded = Pattern.compile(ConfigurationOptions.getConfigStringProperty("cloud.exclusionRegExp", ENGLISH_EXCLUSIONS), Pattern.CASE_INSENSITIVE);
        }
        final String summary = Messages.getString("CLOUD_TABLE_TITLE");
        table = new Table(summary);
        final SimpleTextColumn wordColumn = new SimpleTextColumn(Messages.getString("CLOUD_WORD_COL"));
        final IntegerColumn frequencyColumn = new IntegerColumn(Messages.getString("CLOUD_COUNT_COL"));
        frequencyColumn.setShowPercentages(true);
        table.addColumn(wordColumn);
        table.addColumn(frequencyColumn);
        table.setKeysInFirstColumn(true);

        calculate(content.getCommits());
        int lines = 0;
        final Integer minFrequency = ConfigurationOptions.getConfigIntegerProperty("cloud.minFrequency", new Integer(5));
        final Integer maxNumbers = ConfigurationOptions.getConfigIntegerProperty("cloud.maxWordNumberInTable", new Integer(50));
        final Iterator it = cloudMap.iteratorSortedByValueReverse();
        double maxFreq = -1;
        while (it.hasNext()) {
            final String word = (String) it.next();
            final int frequency = cloudMap.get(word);

            if (maxFreq < 0) {
                maxFreq = Math.log(frequency);
            }

            if (frequency < minFrequency.intValue()) {
                break;
            }

            //            long fontSize = Math.round(Math.min(-2 + Math.log(frequency) * 10 / maxFreq, 8));
            //            System.out.println("FONT SIZE " + fontSize + " Word " + word + " Freq " + frequency + " MaxFreq " + maxFreq);
            wordColumn.addValue(word);
            frequencyColumn.addValue(frequency);
            lines++;
            if (lines >= maxNumbers.intValue()) {
                break;
            }
        }
        //        linesOfCode.setSum(getLinesMap().sum());
    }

    private void calculate(final List commits) {
        final Iterator it = commits.iterator();
        final Integer minSize = ConfigurationOptions.getConfigIntegerProperty("cloud.minLengthForWord", new Integer(4));
        while (it.hasNext()) {
            final Commit commit = (Commit) it.next();
            if (commit.getAuthor() == null || !this.config.isDeveloper(commit.getAuthor())) {
                continue;
            }

            final String comment = commit.getComment();

            if (comment != null && comment.length() > minSize.intValue()) {
                final String[] split = comment.split("\\W+");
                for (int i = 0; i < split.length; i++) {
                    final String word = split[i];
                    if (word != null && word.length() >= minSize.intValue()) {
                        //                        System.out.println(commit.getFile().getFilename() + " ==> Word " + word + " Comment " + comment);
                        tryToAdd(word.toLowerCase());
                    }
                }
            }
        }

    }

    private void tryToAdd(final String word) {
        final Matcher m = excluded.matcher(word);
        if (m.matches()) {
            return;
        }
        mergeIfRequired(word, "ed", 1);
        mergeIfRequired(word, "ing", 3);
        mergeIfRequired(word, "es", 1);
        mergeIfRequired(word, "s", 1);

        //        cloudMap.addInt(word.toLowerCase(), 1);

    }

    private void mergeIfRequired(final String word, final String suffix, final int toRemove) {
        //        if (cloudMap.contains(word)) {
        cloudMap.addInt(word, 1);
        //        }
        if (word.endsWith(suffix)) {
            final String chopped = word.substring(0, word.length() - toRemove);
            if (cloudMap.contains(chopped)) {
                cloudMap.addInt(chopped, cloudMap.get(word));
                cloudMap.remove(word);
            }
        }
    }

    /**
     * @see net.sf.statcvs.reports.TableReport#getTable()
     */
    public Table getTable() {
        return table;
    }

    public String getRawContent() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<p>");

        int lines = 0;
        final Iterator it = cloudMap.iteratorSortedByValueReverse();
        final Integer minFrequency = ConfigurationOptions.getConfigIntegerProperty("cloud.minFrequency", new Integer(5));
        final Integer maxNumbers = ConfigurationOptions.getConfigIntegerProperty("cloud.maxWordNumberInCloud", new Integer(100));
        double maxFreq = -1;
        final TreeMap tm = new TreeMap();
        while (it.hasNext()) {
            final String word = (String) it.next();
            final int frequency = cloudMap.get(word);

            if (maxFreq < 0) {
                maxFreq = Math.log(frequency);
            }

            if (frequency < minFrequency.intValue()) {
                break;
            }

            final long fontSize = Math.round(Math.min(-2 + Math.log(frequency) * 10 / maxFreq, 8));
            //            System.out.println("FONT SIZE " + fontSize + " Word " + word + " Freq " + frequency + " MaxFreq " + maxFreq);
            //            wordColumn.addValue(word);
            //            frequencyColumn.addValue(frequency);
            final StringBuffer buffer1 = new StringBuffer();
            buffer1.append("<font size=\"").append(fontSize).append("\">").append(word).append(" </font> ");
            tm.put(word, buffer1.toString());

            lines++;
            if (lines >= maxNumbers.intValue()) {
                break;
            }
        }

        final Iterator it2 = tm.values().iterator();
        while (it2.hasNext()) {
            buffer.append(it2.next());
        }

        buffer.append("</p>");
        return buffer.toString();
    }
}

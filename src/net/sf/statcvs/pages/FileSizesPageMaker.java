package net.sf.statcvs.pages;

import java.util.Iterator;

import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.TimeLineChartMaker;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.AvgFileSizeTimeLineReport;
import net.sf.statcvs.reports.FileCountTimeLineReport;
import net.sf.statcvs.reports.FileTypeReport;
import net.sf.statcvs.reports.FilesWithMostRevisionsTableReport;
import net.sf.statcvs.reports.LargestFilesTableReport;
import net.sf.statcvs.reports.TableReport;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: FileSizesPageMaker.java,v 1.8 2009/03/13 23:04:28 benoitx Exp $
 */
public class FileSizesPageMaker {
    private static final int MAX_LARGEST_FILES = 20;
    private static final int MAX_FILES_WITH_MOST_REVISIONS = 20;

    private final ReportConfig config;
    private final Repository repository;

    public FileSizesPageMaker(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    public NavigationNode toFile() {
        final ChartImage fileCountChart = new TimeLineChartMaker("file_count", this.config, new FileCountTimeLineReport(this.repository.getFiles())
                .getTimeLine(), "file_count.png", this.repository.getSymbolicNames()).toFile();
        final ChartImage fileSizeChart = new TimeLineChartMaker("file_size", this.config, new AvgFileSizeTimeLineReport(this.repository.getFiles())
                .getTimeLine(), "file_size.png", this.repository.getSymbolicNames()).toFile();
        final TableReport largestFilesTable = new LargestFilesTableReport(this.config, this.repository.getFiles(), MAX_LARGEST_FILES);
        final TableReport mostRevisionsTable = new FilesWithMostRevisionsTableReport(this.config, this.repository.getFiles(), MAX_FILES_WITH_MOST_REVISIONS);

        final Page page = this.config.createPage("file_sizes", "File Statistics", "File Sizes and File Counts");
        page.addAttribute("Total Files", getCurrentFileCount());
        page.addAttribute("Average File Size", getCurrentAverageFileSize(), 1, "lines");
        page.addAttribute("Average Revisions Per File", getCurrentAverageRevisionCount(), 1);
        page.add(fileCountChart);
        page.add(fileSizeChart);
        page.addSection("File Types");
        page.add(new FileTypeReport(this.config));
        page.addSection("Largest Files");
        page.add(largestFilesTable);
        page.addSection("Files With Most Revisions");
        page.add(mostRevisionsTable);
        return page;
    }

    private int getCurrentFileCount() {
        int result = 0;
        final Iterator it = this.repository.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                result++;
            }
        }
        return result;
    }

    private double getCurrentAverageFileSize() {
        return ((double) this.repository.getCurrentLOC()) / getCurrentFileCount();
    }

    private double getCurrentAverageRevisionCount() {
        int revisions = 0;
        final Iterator it = this.repository.getFiles().iterator();
        while (it.hasNext()) {
            final VersionedFile file = (VersionedFile) it.next();
            if (!file.isDead()) {
                revisions += file.getRevisions().size();
            }
        }
        return ((double) revisions) / getCurrentFileCount();
    }
}
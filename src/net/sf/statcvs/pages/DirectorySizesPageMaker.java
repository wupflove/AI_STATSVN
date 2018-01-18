package net.sf.statcvs.pages;

import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.DirectoryPieChartMaker.DirectorySizesChartMaker;
import net.sf.statcvs.charts.LOCChartMaker.AllDirectoriesLOCChartMaker;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.DirectoriesTableReport;
import net.sf.statcvs.reports.TableReport;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: DirectorySizesPageMaker.java,v 1.5 2008/04/02 11:22:14 benoitx Exp $
 */
public class DirectorySizesPageMaker {
    private final ReportConfig config;
    private final Repository repository;

    public DirectorySizesPageMaker(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    public NavigationNode toFile() {
        final ChartImage allDirLOCChart = new AllDirectoriesLOCChartMaker(this.config, 6).toFile();
        final ChartImage dirSizesChart = new DirectorySizesChartMaker(this.config).toFile();
        final TableReport table = new DirectoriesTableReport(this.repository);

        final Page page = this.config.createPage("dir_sizes", "Directory Sizes", "Directory Sizes");
        page.addAttribute("Total Directories", this.repository.getDirectories().size());
        page.add(allDirLOCChart);
        page.addSection("Directory Statistics");
        page.add(table);
        page.add(dirSizesChart);
        return page;
    }
}
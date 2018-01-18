package net.sf.statcvs.pages;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.LOCChartMaker.MainLOCChartMaker;
import net.sf.statcvs.output.ReportConfig;

public class LOCPageMaker {
    private final ReportConfig config;

    public LOCPageMaker(final ReportConfig config) {
        this.config = config;
    }

    public NavigationNode toFile() {
        final ChartImage chart = new MainLOCChartMaker("loc", this.config, "loc.png", this.config.getLargeChartSize()).toFile();

        final Page result = config.createPage("loc", Messages.getString("LOC_TITLE"), Messages.getString("LOC_TITLE"));
        result.addAttribute(Messages.getString("TOTAL_LOC_TITLE"), this.config.getRepository().getCurrentLOC());
        result.addAttribute(Messages.getString("MOST_RECENT_COMMIT"), this.config.getRepository().getLastDate());
        result.add(chart);
        return result;
    }
}

package net.sf.statcvs.pages;

import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Messages;
import net.sf.statcvs.charts.ChartImage;
import net.sf.statcvs.charts.ModuleEvolutionChartMaker;
import net.sf.statcvs.charts.ModuleSizePieChartMaker;
import net.sf.statcvs.charts.LOCChartMaker.AllModulesLOCChartMaker;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.reports.ModuleTableReport;
import net.sf.statcvs.reports.TableReport;
import net.sf.statcvs.util.ModuleUtil;

/**
 * @author anja
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ModulesPageMaker.java,v 1.3 2009/08/31 20:13:25 benoitx Exp $
 */
public class ModulesPageMaker {
    private final ReportConfig config;
    private final Repository repository;

    public ModulesPageMaker(final ReportConfig config) {
        this.config = config;
        this.repository = config.getRepository();
    }

    public NavigationNode toFile() {
        final ChartImage allDirLOCChart = new AllModulesLOCChartMaker(this.config, 6).toFile();
        final Page page = this.config.createPage("modules", Messages.getString("MODULES_SIZES_TITLE"), Messages.getString("MODULES_SIZES_SUBTITLE"));
        final List moduleIds = ModuleUtil.getConfigModules();
        final Iterator mod = moduleIds.iterator();
        while (mod.hasNext()) {
            final String moduleId = (String) mod.next();
            final String pattern = ModuleUtil.getConfigModuleRegexp(moduleId);
            final String name = ModuleUtil.getConfigModuleName(moduleId);
            page.addAttribute(name, pattern);
        }

        page.addSection(Messages.getString("MODULES_CURRENT_SIZES_TITLE"));
        final ChartImage dirSizesChart = new ModuleSizePieChartMaker("modules_sizes", this.config, Messages.getString("PIE_MODSIZE_TITLE"), Messages
                .getString("PIE_MODSIZE_SUBTITLE"), "modules_sizes.png").toFile();
        page.add(dirSizesChart);

        final TableReport table = new ModuleTableReport(this.repository);

        //        page.addAttribute("Total Directories", this.repository.getDirectories().size());
        page.addSection(Messages.getString("MODULES_LOC_TITLE"));
        page.add(allDirLOCChart);
        page.addSection(Messages.getString("MODULES_EVO_TITLE"));
        final ChartImage modEvoChart = new ModuleEvolutionChartMaker("modules_evolution", this.config, Messages.getString("MODULE_EVO_TITLE"),
                "modules_evolution.png").toFile();
        page.add(modEvoChart);
        page.addSection(Messages.getString("MODULES_STATS_TITLE"));
        page.add(table);

        return page;
    }
}
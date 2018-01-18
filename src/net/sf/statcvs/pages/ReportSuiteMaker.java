package net.sf.statcvs.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Main;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.output.ChurnPageMaker;
import net.sf.statcvs.output.RepoMapPageMaker;
import net.sf.statcvs.output.ReportConfig;
import net.sf.statcvs.pages.xml.GenerateXml;
import net.sf.statcvs.util.ModuleUtil;

/**
 * TODO: Clean up
 * 
 * @author Anja Jentzsch
 * @author Benoit Xhenseval (OutputRenderer and ExtraReportRegister interfaces)
 * @version $Id: ReportSuiteMaker.java,v 1.11 2009/08/22 10:30:42 benoitx Exp $
 */
public class ReportSuiteMaker {

    /**
     * Path to web distribution files inside the distribution JAR, relative to
     * the {@link net.sf.statcvs.Main} class
     */
    public static final String WEB_FILE_PATH = "web-files/";

    public static final String DIRECTORY_ICON = "folder.png";
    public static final String BUG_ICON = "bug.png";
    public static final String TWEET_THIS_SMALL = "tt-twitter-micro3.png";
    public static final String TWEET_THIS_ICON = "tweet-this.png";
    public static final String DELETED_DIRECTORY_ICON = "folder-deleted.png";
    public static final String FILE_ICON = "file.png";
    public static final String DELETED_FILE_ICON = "file-deleted.png";
    public static final int ICON_WIDTH = 15;
    public static final int ICON_HEIGHT = 13;

    private final ReportConfig config;
    private final String notesHTML;
    private List extraPages = new ArrayList();

    /**
     * @param config Configuration and data for the report suite
     * @param notesHTML A note to be inserted on top of the page; might be <tt>null</tt>
     */
    public ReportSuiteMaker(final ReportConfig config, final String notesHTML) {
        this(config, notesHTML, Collections.EMPTY_LIST);
    }

    /**
     * @param config Configuration and data for the report suite
     * @param notesHTML A note to be inserted on top of the page; might be <tt>null</tt>
     * @param additionalPages A list of {@link Page}s for inclusion in the index page's main menu
     */
    public ReportSuiteMaker(final ReportConfig config, final String notesHTML, final List additionalPages) {
        this.config = config;
        this.notesHTML = notesHTML;
        this.extraPages = additionalPages;
    }

    /**
    * this method will move away from the ordinary routine taken by
    * statSVn and will invoke some new classes that are written to generate the xml's
    * 
    */
    public void toXml() {
        new GenerateXml(this.config).generate();
    }

    /**
     * TODO: Don't throw exception
     * @throws IOException on error while writing the files
     */
    public Page toFile() throws IOException {
        this.config.getCssHandler().createOutputFiles();
        if (this.config.getRepository().isEmpty()) {
            return createEmptyRepositoryPage();
        }
        createIcon(TWEET_THIS_SMALL);
        createIcon(TWEET_THIS_ICON);
        createIcon(BUG_ICON);
        createIcon(DIRECTORY_ICON);
        createIcon(DELETED_DIRECTORY_ICON);
        createIcon(FILE_ICON);
        createIcon(DELETED_FILE_ICON);

        final PageGroup mainMenu = new PageGroup("Reports", false);
        final IndexPageMaker indexPageMaker = new IndexPageMaker(this.config, this.notesHTML, mainMenu);

        //        Runtime.getRuntime().gc();
        //        final long memoryUsedOnStart = Runtime.getRuntime().freeMemory();

        final NavigationNode allDevPage = new AllDevelopersPageMaker(this.config).toFile();
        mainMenu.add(allDevPage);
        allDevPage.setParent(indexPageMaker.getPage());
        //        Runtime.getRuntime().gc();
        //        long memoryUsedOnStop = Runtime.getRuntime().freeMemory();
        //        System.out.println("Mem with HTML " + (memoryUsedOnStop-memoryUsedOnStart));
        allDevPage.write();
        //        Runtime.getRuntime().gc();
        //        memoryUsedOnStop = Runtime.getRuntime().freeMemory();
        //        System.out.println("Mem AFTER WRITING " + (memoryUsedOnStop-memoryUsedOnStart));
        final PageGroup locPageGroup = new CommitLogPageGroupMaker(this.config).getPages();
        if (locPageGroup != null) {
            mainMenu.add(locPageGroup);
            locPageGroup.setParent(indexPageMaker.getPage());
            locPageGroup.write();
        }

        final NavigationNode locPage = new LOCPageMaker(this.config).toFile();
        mainMenu.add(locPage);
        locPage.setParent(indexPageMaker.getPage());
        locPage.write();

        final NavigationNode fileSizePage = new FileSizesPageMaker(this.config).toFile();
        mainMenu.add(fileSizePage);
        fileSizePage.setParent(indexPageMaker.getPage());
        fileSizePage.write();

        final NavigationNode dirPage = new DirectorySizesPageMaker(this.config).toFile();
        mainMenu.add(dirPage);
        dirPage.setParent(indexPageMaker.getPage());
        dirPage.write();

        if (ModuleUtil.modulesPresent()) {
            final NavigationNode modulePage = new ModulesPageMaker(this.config).toFile();
            mainMenu.add(modulePage);
            modulePage.setParent(indexPageMaker.getPage());
            modulePage.write();
        }

        final NavigationNode repoPage = new RepoMapPageMaker(config).toFile();
        mainMenu.add(repoPage);
        repoPage.setParent(indexPageMaker.getPage());
        repoPage.write();

        final NavigationNode churnPage = new ChurnPageMaker(config).toFile();
        mainMenu.add(churnPage);
        churnPage.setParent(indexPageMaker.getPage());
        churnPage.write();

        final NavigationNode cloudPage = new CloudCommitPageMaker(this.config).toFile();
        mainMenu.add(cloudPage);
        cloudPage.setParent(indexPageMaker.getPage());
        cloudPage.write();

        Iterator it = this.extraPages.iterator();
        while (it.hasNext()) {
            final Page extra = (Page) it.next();
            extra.setParent(indexPageMaker.getPage());
            mainMenu.add(extra);
        }
        final Page indexPage = indexPageMaker.toFile();

        final PageGroup directoryPages = new PageGroup("Directories", false);
        it = this.config.getRepository().getDirectories().iterator();
        while (it.hasNext()) {
            final Page dirPageGrp = new DirectoryPageMaker(this.config, (Directory) it.next()).toFile();
            dirPageGrp.setParent(indexPageMaker.getPage());
            directoryPages.add(dirPageGrp);
            dirPageGrp.write();
        }
        indexPage.addChild(directoryPages);
        directoryPages.write();
        return indexPage;
    }

    private void createIcon(final String iconFilename) throws IOException {
        this.config.copyFileIntoReport(Main.class.getResource(WEB_FILE_PATH + iconFilename), iconFilename);
    }

    private Page createEmptyRepositoryPage() {
        String projectName = this.config.getProjectName();
        if (projectName == null) {
            projectName = "empty repository";
        }
        final String title = "Development statistics for " + projectName;
        final Page page = this.config.createPage("index", title, title);
        page.addAttribute("Generated", Calendar.getInstance().getTime());
        page.addRawContent("<p>No matching files in repository.</p>");
        return page;
    }
}
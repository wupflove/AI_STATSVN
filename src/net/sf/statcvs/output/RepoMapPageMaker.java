/*
 StatCVS - CVS statistics generation
 Copyright (C) 2006 Benoit Xhenseval

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

 */
package net.sf.statcvs.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.Repository;
import net.sf.statcvs.model.Revision;
import net.sf.statcvs.model.VersionedFile;
import net.sf.statcvs.pages.HTML;
import net.sf.statcvs.pages.NavigationNode;
import net.sf.statcvs.pages.Page;
import net.sf.statcvs.util.FileUtils;

/**
 * New report that Repo Map, a jtreemap-based report (applet) that shows the
 * entire source tree in a hierarchical manner, the size of each box is related
 * to LOC and the colour to the changes over the last 30 days (red -loc, green
 * +loc).
 *
 * @author Benoit Xhenseval (www.objectlab.co.uk)
 * @see http://jtreemap.sourceforge.net for more about JTreeMap.
 */
public class RepoMapPageMaker {
    private static final int DAYS_FROM_LAST_DATE = 30;

    private static final String WEB_FILE_PATH = "web-files/";

    private static final String REPO_FILE = "repomap-data.txt";

    private final Date deadline;

    private final Date currentDate;

    private final ReportConfig config;

    private int indent = 0;

    /**
     * @see net.sf.statcvs.output.HTMLPage#HTMLPage(Repository)
     */
    public RepoMapPageMaker(final ReportConfig config) {
        final Calendar cal = Calendar.getInstance();
        if (config != null && config.getRepository() != null && config.getRepository().getLastDate() != null) {
            cal.setTime(config.getRepository().getLastDate());
        }
        currentDate = cal.getTime();
        cal.add(Calendar.DATE, -DAYS_FROM_LAST_DATE);
        deadline = cal.getTime();
        this.config = config;
    }

    public NavigationNode toFile() {
        final Page page = this.config.createPage("repomap", Messages.getString("REPOMAP_TITLE"), Messages.getString("REPOMAP_TITLE"));
        page.addRawAttribute(Messages.getString("REPOMAP_START_DATE"), HTML.getDate(deadline));
        page.addRawAttribute(Messages.getString("REPOMAP_END_DATE"), HTML.getDate(currentDate));

        page.addRawContent("<p>" + Messages.getString("REPOMAP_DESCRIPTION") + "</p>");
        page.addRawContent("<p>" + getApplet() + "</p>");
        page.addRawContent("<p><small>This page uses <a href=\"http://jtreemap.sourceforge.net\">JTreeMap</a>.</small></p>");
        buildXmlForJTreeMap();

        return page;
    }

    private String getApplet() {
        return "<applet archive=\"./" + Messages.getString("JTREEMAP_JAR") + "\" code=\"net.sf.jtreemap.swing.example.JTreeMapAppletExample\""
                + " width=\"940\" height=\"600\"><param name=\"dataFile\" value=\"" + REPO_FILE + "\"/>" + "<param name=\"viewTree\" value=\"true\"/>"
                + "<param name=\"showWeight\" value=\"true\"/>" + "<param name=\"valuePrefix\" value=\"Change:\"/>"
                + "<param name=\"weightPrefix\" value=\"LOC:\"/>" + "<param name=\"dataFileType\" value=\"xml\"/>"
                + "<param name=\"colorProvider\" value=\"HSBLog\"/>" + "</applet>";
    }

    private void buildXmlForJTreeMap() {
        BufferedWriter out = null;
        try {
            copyJar(Messages.getString("JTREEMAP_JAR"));
            out = new BufferedWriter(new FileWriter(ConfigurationOptions.getOutputDir() + REPO_FILE));
            out.write("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
            // out.append("<!DOCTYPE root SYSTEM \"TreeMap.dtd\" >\n");
            out.write("<root>\n");
            final Iterator it = config.getRepository().getDirectories().iterator();
            if (it.hasNext()) {
                final Directory dir = (Directory) it.next();
                doDirectory(out, dir);
            }
            out.write("</root>");
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    //					SvnConfigurationOptions.getTaskLogger().error(e.toString());
                }
            }
        }
    }

    private void copyJar(final String jtreemapJar) throws IOException {
        InputStream stream = null;
        try {
            stream = RepoMapPageMaker.class.getResourceAsStream(WEB_FILE_PATH + jtreemapJar);
            if (stream != null) {
                FileUtils.copyFile(stream, new File(ConfigurationOptions.getOutputDir() + jtreemapJar));
            } else {
                throw new IOException("The stream to " + (WEB_FILE_PATH + jtreemapJar) + " failed, is it copied in the jar?");
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void addSpaces(final int count, final BufferedWriter out) throws IOException {
        out.write(getSpaces(count));
    }

    private String getSpaces(final int count) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < count; i++) {
            result.append("  ");
        }
        return result.toString();
    }

    private void doDirectory(final BufferedWriter out, final Directory dir) throws IOException {
        indent++;
        //		SvnConfigurationOptions.getTaskLogger().log("Directory:" + getSpaces(indent) + dir.getName());

        if (dir.isEmpty()) {
            indent--;
            return;
        }

        final SortedSet set = dir.getSubdirectories();
        final SortedSet files = dir.getFiles();
        final String name = dir.isRoot() ? Messages.getString("NAVIGATION_ROOT") : dir.getName();
        boolean addedBranch = false;
        if (indent > 1 && set != null && !set.isEmpty()) {
            out.write("\n");
            addSpaces(indent, out);
            out.write("<branch>\n");
            addSpaces(indent + 2, out);
            labelTag(out, name);
            addedBranch = true;
        } else if (indent == 1) {
            addSpaces(indent, out);
            labelTag(out, name);
        }
        if (set != null) {
            for (final Iterator it2 = set.iterator(); it2.hasNext();) {
                doDirectory(out, (Directory) it2.next());
            }
        }
        addedBranch = handleEachFileInDir(out, files, name, addedBranch);
        if (addedBranch) {
            addSpaces(indent, out);
            out.write("</branch>\n");
        }
        indent--;
    }

    private boolean handleEachFileInDir(final BufferedWriter out, final SortedSet files, final String name, boolean addedBranch) throws IOException {
        if (files != null && !files.isEmpty()) {
            for (final Iterator file = files.iterator(); file.hasNext();) {
                final VersionedFile vfile = (VersionedFile) file.next();

                int loc = vfile.getCurrentLinesOfCode();

                //				SvnConfigurationOptions.getTaskLogger().log("File:" + vfile.getFilename() + " LOC:" + loc);

                final int delta = calculateTotalDelta(vfile);
                if (loc == 0) {
                    loc = Math.abs(delta);
                }
                if (loc == 0) {
                    continue;
                }
                if (!addedBranch) {
                    out.write("\n");
                    addSpaces(indent, out);
                    out.write("<branch>\n");
                    addSpaces(indent + 2, out);
                    labelTag(out, name);
                    out.write("\n");
                    addedBranch = true;
                }
                addSpaces(indent + 2, out);
                out.write("<leaf>");
                labelTag(out, vfile.getFilename());
                tag(out, "weight", String.valueOf(loc));
                final double percentage = ((double) delta) / (double) loc * 100.0;
                tag(out, "value", String.valueOf(percentage));
                out.write("</leaf>\n");
                //				SvnConfigurationOptions.getTaskLogger().log("===========>>> LOC=" + loc + " totalDelta=" + delta + " Delta%=" + percentage);
            }
        }
        return addedBranch;
    }

    private int calculateTotalDelta(final VersionedFile vfile) {
        int delta = 0;
        final SortedSet revisions = vfile.getRevisions();
        // take all deltas for the last 30 days.
        for (final Iterator rev = revisions.iterator(); rev.hasNext();) {
            final Revision revision = (Revision) rev.next();

            //			SvnConfigurationOptions.getTaskLogger().log(
            //			        "Revision " + revision.getDate() + " file:" + vfile.getFilename() + " Dead:" + vfile.isDead() + " LOC:" + revision.getLines() + " delta:"
            //			                + revision.getLinesDelta());

            if (deadline.before(revision.getDate())) {
                delta += revision.getLinesDelta();

                //				SvnConfigurationOptions.getTaskLogger().log(
                //				        "Revision " + revision.getRevisionNumber() + " Delta:" + revision.getLinesDelta() + " totalDelta:" + delta + " LOC:"
                //				                + revision.getLines() + " Dead:" + revision.isDead());
            }
        }
        return delta;
    }

    private void labelTag(final Writer result, final String name) throws IOException {
        if (name == null || name.length() == 0) {
            tag(result, "label", "[root]");
        } else {
            tag(result, "label", name);
        }
    }

    private void tag(final Writer result, final String tagName, final String value) throws IOException {
        result.write("<");
        result.write(tagName);
        result.write(">");
        result.write(value);
        result.write("</");
        result.write(tagName);
        result.write(">");
    }
}

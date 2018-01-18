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
    
	$Name:  $ 
	Created on $Date: 2002/08/23 02:04:12 $ 
*/
package net.sf.statcvs.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: FileCollectionFormatterTest.java,v 1.4 2002/08/23 02:04:12 cyganiak Exp $
 */
public class FileCollectionFormatterTest extends TestCase {

    private FileCollectionFormatter fcf;
    private final List files = new ArrayList();

    /**
     * Constructor for FileCollectionFormatterTest.
     * @param arg0 filename
     */
    public FileCollectionFormatterTest(final String arg0) {
        super(arg0);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Method testHelperMethods.
     */
    public void testHelperMethods() {
        assertEquals(0, FileCollectionFormatter.getDepth(""));
        assertEquals(1, FileCollectionFormatter.getDepth("test/"));
        assertEquals(2, FileCollectionFormatter.getDepth("test/foo/"));
        assertEquals("", FileCollectionFormatter.getParent(""));
        assertEquals("", FileCollectionFormatter.getParent("test/"));
        assertEquals("dir/", FileCollectionFormatter.getParent("dir/test/"));
        assertEquals("", FileCollectionFormatter.getDirectory("test.file"));
        assertEquals("dir/", FileCollectionFormatter.getDirectory("dir/test.file"));
        assertEquals("dir/subdir/", FileCollectionFormatter.getDirectory("dir/subdir/test.file"));
        assertEquals("file.name", FileCollectionFormatter.getRelativeFilename("file.name", ""));
        assertEquals("subdir/file.name", FileCollectionFormatter.getRelativeFilename("subdir/file.name", ""));
        assertEquals("subdir/", FileCollectionFormatter.getRelativeFilename("subdir/", ""));
        assertEquals("", FileCollectionFormatter.getRelativeFilename("", ""));
        assertEquals("file.name", FileCollectionFormatter.getRelativeFilename("subdir/file.name", "subdir/"));
        assertEquals("", FileCollectionFormatter.getRelativeFilename("subdir/", "subdir/"));
        assertEquals("dir/", FileCollectionFormatter.getRelativeFilename("subdir/dir/", "subdir/"));
    }

    /**
     * Method testCreation.
     */
    public void testCreation() {
        new FileCollectionFormatter(new ArrayList());
    }

    /**
     * Method testEmpty.
     */
    public void testEmpty() {
        fcf = new FileCollectionFormatter(files);
        final List dirs = fcf.getDirectories();
        assertNotNull(dirs);
        assertTrue(dirs.isEmpty());
    }

    /**
     * Method testOneFile.
     */
    public void testOneFile() {
        files.add("directory/file.txt");
        fcf = new FileCollectionFormatter(files);
        final List dirs = fcf.getDirectories();
        assertEquals(1, dirs.size());
        assertEquals("directory/", dirs.get(0));
        final List names = fcf.getFiles("directory/");
        assertEquals(1, names.size());
        assertEquals("file.txt", names.get(0));
    }

    /**
     * Method testOneFileDeep.
     */
    public void testOneFileDeep() {
        files.add("sub1/sub2/sub3/file.txt");
        fcf = new FileCollectionFormatter(files);
        final List dirs = fcf.getDirectories();
        assertEquals(1, dirs.size());
        assertEquals("sub1/sub2/sub3/", dirs.get(0));
        final List names = fcf.getFiles("sub1/sub2/sub3/");
        assertEquals(1, names.size());
        assertEquals("file.txt", names.get(0));
    }

    /**
     * Method testTwoFilesDifferentDir.
     */
    public void testTwoFilesDifferentDir() {
        files.add("directory/file.txt");
        files.add("inRoot.txt");
        fcf = new FileCollectionFormatter(files);
        final List dirs = fcf.getDirectories();
        assertEquals(2, dirs.size());
        assertEquals("", dirs.get(0));
        assertEquals("directory/", dirs.get(1));
        List names = fcf.getFiles("");
        assertEquals(1, names.size());
        assertEquals("inRoot.txt", names.get(0));
        names = fcf.getFiles("directory/");
        assertEquals(1, names.size());
        assertEquals("file.txt", names.get(0));
    }

    /**
     * Method testTwoFilesSameDir.
     */
    public void testTwoFilesSameDir() {
        files.add("directory/file2.txt");
        files.add("directory/file1.txt");
        fcf = new FileCollectionFormatter(files);
        final List dirs = fcf.getDirectories();
        assertEquals(1, dirs.size());
        assertEquals("directory/", dirs.get(0));
        final List names = fcf.getFiles("directory/");
        assertEquals(2, names.size());
        assertEquals("file1.txt", names.get(0));
        assertEquals("file2.txt", names.get(1));
    }

    /**
     * Method testUnknownDirectory.
     */
    public void testUnknownDirectory() {
        files.add("directory/foo.txt");
        fcf = new FileCollectionFormatter(files);
        try {
            fcf.getFiles("nonexistingDirectory/");
            fail("should have thrown exception");
        } catch (final NoSuchElementException e) {
            // do nothing
        }
    }

    /**
     * Method testExcludeSubdir.
     */
    public void testExcludeSubdir() {
        files.add("directory/file2.txt");
        files.add("directory/file1.txt");
        files.add("fileInRoot");
        fcf = new FileCollectionFormatter(files);
        final List dirs = fcf.getDirectories();
        assertEquals(2, dirs.size());
        assertEquals("", dirs.get(0));
        assertEquals("directory/", dirs.get(1));
        List names = fcf.getFiles("directory/");
        assertEquals(2, names.size());
        assertEquals("file1.txt", names.get(0));
        assertEquals("file2.txt", names.get(1));
        names = fcf.getFiles("");
        assertEquals(1, names.size());
        assertEquals("fileInRoot", names.get(0));
    }
}

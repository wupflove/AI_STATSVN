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
*/
package net.sf.statsvn.input;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import junit.framework.Assert;

/**
 * Mock Object implementing {@link SvnLogBuilder}
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id: MockLogBuilder.java,v 1.4 2004/12/14 13:38:13 squig Exp $
 */
public class MockLogBuilder implements SvnLogBuilder {
	private final LinkedList expectedMethods = new LinkedList();

	private final LinkedList expectedData = new LinkedList();

	/*
	 *  (non-Javadoc)
	 * @see net.sf.statsvn.input.SvnLogBuilder#addToAttic(java.lang.String)
	 * @todo implementation
	 */
	public void addToAttic(final String filename) {
	}

	/* (non-Javadoc)
	 * @see net.sf.statsvn.input.SvnLogBuilder#buildModule(java.lang.String)
	 */
	public void buildModule(final String moduleName) {
		Assert.assertEquals(expectedMethods.removeFirst(), "buildModule");
		Assert.assertEquals(expectedData.removeFirst(), moduleName);
	}

	/* (non-Javadoc)
	 * @see net.sf.statsvn.input.SvnLogBuilder#buildFile(java.lang.String, boolean, boolean)
	 */
	public void buildFile(final String filename, final boolean isBinary, final boolean isInAttic, final Map revBySymnames) {
		Assert.assertEquals(expectedMethods.removeFirst(), "buildFile");
		Assert.assertEquals(expectedData.removeFirst(), filename);
		Assert.assertEquals(expectedData.removeFirst(), Boolean.valueOf(isBinary));
		Assert.assertEquals(expectedData.removeFirst(), Boolean.valueOf(isInAttic));
	}

	/* (non-Javadoc)
	 * @see net.sf.statsvn.input.SvnLogBuilder#buildRevision(net.sf.statsvn.input.RevisionData)
	 */
	public void buildRevision(final RevisionData data) {
		if (expectedMethods.isEmpty()) {
			Assert.fail("expected no more revisions");
		}
		if (!"buildRevision".equals(expectedMethods.getFirst()) && !"nextRevision".equals(expectedMethods.getFirst())
		        && !((String) expectedMethods.getFirst()).startsWith("current")) {
			Assert.assertEquals(expectedMethods.getFirst(), "buildRevision");
		}
		if ("buildRevision".equals(expectedMethods.getFirst())) {
			Assert.assertEquals(expectedMethods.removeFirst(), "buildRevision");
			final RevisionData expected = (RevisionData) expectedData.removeFirst();
			Assert.assertEquals(expected.getRevisionNumber(), data.getRevisionNumber());
			Assert.assertEquals(expected.getDate().getTime() / 1000, data.getDate().getTime() / 1000);
			Assert.assertEquals(expected.getLoginName(), data.getLoginName());
			//Assert.assertEquals(expected.isAddOnSubbranch(), data.isAddOnSubbranch());
			Assert.assertEquals(expected.isDeletion(), data.isDeletion());
			Assert.assertEquals(expected.isCreationOrRestore(), data.isCreationOrRestore());
			Assert.assertEquals(expected.isChange(), data.isChange());
			Assert.assertEquals(expected.getComment(), data.getComment());
			Assert.assertEquals(expected.getLinesAdded(), data.getLinesAdded());
			Assert.assertEquals(expected.getLinesRemoved(), data.getLinesRemoved());
			return;
		}
		while (!expectedMethods.isEmpty() && ((String) expectedMethods.getFirst()).startsWith("current")) {
			final String expected = (String) expectedMethods.removeFirst();
			if ("currentRevisionNumber".equals(expected)) {
				Assert.assertEquals(expectedData.removeFirst(), data.getRevisionNumber());
			} else if ("currentDate".equals(expected)) {
				Assert.assertEquals(((Date) expectedData.removeFirst()).getTime() / 1000, data.getDate().getTime() / 1000);
			} else if ("currentAuthor".equals(expected)) {
				Assert.assertEquals(expectedData.removeFirst(), data.getLoginName());
			} else if ("currentComment".equals(expected)) {
				Assert.assertEquals(expectedData.removeFirst(), data.getComment());
			} else if ("currentStateExp".equals(expected)) {
				Assert.assertTrue(data.isStateExp());
			} else if ("currentStateDead".equals(expected)) {
				Assert.assertTrue(data.isStateDead());
			} else if ("currentLines".equals(expected)) {
				Assert.assertEquals(expectedData.removeFirst(), new Integer(data.getLinesAdded()));
				Assert.assertEquals(expectedData.removeFirst(), new Integer(data.getLinesRemoved()));
			} else if ("currentNoLines".equals(expected)) {
				Assert.assertTrue(data.hasNoLines());
			} else { // can't happen
				Assert.fail("bad state: " + expected);
			}
		}
		if (!expectedMethods.isEmpty() && "nextRevision".equals(expectedMethods.getFirst())) {
			expectedMethods.removeFirst();
		}
	}

	public void expectBuildModule(final String moduleName) {
		expectedMethods.add("buildModule");
		expectedData.add(moduleName);
	}

	public void expectBuildFile(final String filename, final boolean isBinary, final boolean isInAttic) {
		expectedMethods.add("buildFile");
		expectedData.add(filename);
		expectedData.add(Boolean.valueOf(isBinary));
		expectedData.add(Boolean.valueOf(isInAttic));
	}

	public void expectBuildRevision(final RevisionData data) {
		expectedMethods.add("buildRevision");
		expectedData.add(data);
	}

	public void expectNextRevision() {
		expectedMethods.add("nextRevision");
	}

	public void expectCurrentRevisionNumber(final String revision) {
		expectedMethods.add("currentRevisionNumber");
		expectedData.add(revision);
	}

	public void expectCurrentDate(final Date date) {
		expectedMethods.add("currentDate");
		expectedData.add(date);
	}

	public void expectCurrentAuthor(final String name) {
		expectedMethods.add("currentAuthor");
		expectedData.add(name);
	}

	public void expectCurrentComment(final String comment) {
		expectedMethods.add("currentComment");
		expectedData.add(comment);
	}

	public void expectCurrentStateExp() {
		expectedMethods.add("currentStateExp");
	}

	public void expectCurrentStateDead() {
		expectedMethods.add("currentStateDead");
	}

	public void expectCurrentNoLines() {
		expectedMethods.add("currentNoLines");
	}

	public void expectCurrentLines(final int added, final int removed) {
		expectedMethods.add("currentLines");
		expectedData.add(new Integer(added));
		expectedData.add(new Integer(removed));
	}

	public void verify() {
		if (!expectedMethods.isEmpty()) {
			Assert.fail("expected " + expectedMethods.getFirst());
		}
	}

	/**
	 * @todo implementation
	 */
	public Map getFileBuilders() {
		return null;
	}

	/**
	 * @todo implementation
	 */
	public void updateRevision(final String filename, final String revisionNumber, final int linesAdded, final int linesRemoved) {
	}

	public void buildFile(final String filename, final boolean isBinary, final boolean isInAttic, final Map revBySymnames, final Map dateBySymnames) {
		// TODO Auto-generated method stub

	}

	public boolean matchesPatterns(final String filename) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean matchesTagPatterns(final String tag) {
		// TODO Auto-generated method stub
		return false;
	}
}
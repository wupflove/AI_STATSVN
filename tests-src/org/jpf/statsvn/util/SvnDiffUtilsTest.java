/**
 * 
 */
package net.sf.statsvn.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;
import net.sf.statcvs.input.LogSyntaxException;
import net.sf.statcvs.output.ConfigurationException;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statsvn.output.SvnConfigurationOptions;

/**
 * @author Jason Kealey
 *
 */
public class SvnDiffUtilsTest extends TestCase {
	public void testSimple() {
		try {
			SvnConfigurationOptions.setSvnUsername("jkealey");
			SvnConfigurationOptions.setSvnPassword("PASSWORD");
			ConfigurationOptions.setCheckedOutDirectory("C:\\eclipse3.4\\workspace\\statsvn");
			
			ISvnProcessor processor = new SvnCommandLineProcessor();
			processor.getInfoProcessor().loadInfo();

			ISvnDiffProcessor diffUtils = processor.getDiffProcessor();
			final Vector output = diffUtils.getLineDiff("123");

			for (final Iterator iter = output.iterator(); iter.hasNext();) {
				final Object[] element = (Object[]) iter.next();
				if (element.length == 3) {
					final String file = element[0].toString();
					final int[] diff = (int[]) element[1];
					final Boolean isBinary = (Boolean) element[2];
					System.out.println("File: " + file + ", Added: " + diff[0] + ", Removed: " + diff[1] + ", Binary:" + isBinary);
				}
			}

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final BinaryDiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final LogSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

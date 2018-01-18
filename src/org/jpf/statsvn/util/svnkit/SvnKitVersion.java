package org.jpf.statsvn.util.svnkit;

import org.jpf.statsvn.util.ISvnProcessor;
import org.jpf.statsvn.util.SvnStartupUtils;
import org.jpf.statsvn.util.SvnVersionMismatchException;

/**
 * Runs svn -version using svnkit. (Possible?)
 *  
 * @author jkealey, yogesh
 */
public class SvnKitVersion extends SvnStartupUtils {

    public SvnKitVersion(ISvnProcessor processor) {
        super(processor);
    }

    public SvnKitProcessor getSvnKitProcessor() {
        return (SvnKitProcessor) getProcessor();
    }
    
    public String checkSvnVersionSufficient() throws SvnVersionMismatchException {
        // TODO: Not sure how to implement with svnkit. 
        return "1.4.0";
    }

}

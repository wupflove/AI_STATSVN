package net.sf.statcvs.weblinks.bugs;

/**
 * Implements support for the Bugzilla bug tracker.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: Bugzilla.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class Bugzilla extends BugTracker {

    public Bugzilla(final String baseURL) {
        super(baseURL);
    }

    public String getName() {
        return "Bugzilla";
    }

    public String bugURL(final String bugNumber) {
        return baseURL() + "show_bug.cgi?id=" + bugNumber;
    }
}
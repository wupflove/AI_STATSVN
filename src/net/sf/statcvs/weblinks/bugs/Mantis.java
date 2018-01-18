package net.sf.statcvs.weblinks.bugs;

/**
 * Implements support for the <a href="http://www.mantisbt.org/">Mantis
 * bug tracker</a>. 
 *
 * @author Brian J¿rgensen (qte@cs.aau.dk)
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: Mantis.java,v 1.3 2008/04/02 11:22:15 benoitx Exp $
 */
public class Mantis extends BugTracker {

    public Mantis(final String baseURL) {
        super(baseURL);
    }

    public String getName() {
        return "Mantis";
    }

    public String bugURL(final String bugNumber) {
        return baseURL() + "view.php?id=" + bugNumber;
    }
}
/**
 * 
 */
package net.sf.statcvs.util;

import java.util.List;

import net.sf.statcvs.output.ConfigurationOptions;

/**
 * @author benoitx
 *
 */
public final class ModuleUtil {
    private ModuleUtil() {
    }

    /**
     * returns true if there are some module definitions
     * <pre>
    * modules=1,2,3,4
    * module.1.name=Chart
    * module.1.regexp=** /charts/ *.java
    * module.2.name=I/O
    * module.2.regexp=** /output/ *.java.** /input/ *.java
    * module.3.name=Model
    * module.3.regexp=** /model/ *.java|** /renderer/ *.java
    * module.4.name=Pages
    * module.4.regexp=** /pages/ ** / *.java,** /reportmodel/ *.java,** /reports/ *.java
     * </pre>
     */
    public static boolean modulesPresent() {
        return ConfigurationOptions.getConfigStringProperty("modules", null) != null;
    }

    public static List getConfigModules() {
        return ConfigurationOptions.getConfigStringListProperty("modules", null);
    }

    public static String getConfigModuleName(final String moduleId) {
        return ConfigurationOptions.getConfigStringProperty("module." + moduleId + ".name", moduleId);
    }

    public static String getConfigModuleRegexp(final String moduleId) {
        return ConfigurationOptions.getConfigStringProperty("module." + moduleId + ".regexp", null);
    }
}

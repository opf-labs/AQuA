/**
 * 
 */
package uk.bl.dpt.aqua;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author  <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *          <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a>
 *          <a href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 */
public class ManifestStarter {
    private static Logger LOG = Logger.getLogger(ManifestStarter.class);

    /**
     * @param args if not 8080 then pass port as an integer arg
     * @throws Exception when it goes wrong
     */
    public static void main(String... args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        LOG.info("Setting up Jetty server on port " + port);
        Server server = new Server(port);
        server.addHandler(new WebAppContext(getProjectRoot().getAbsolutePath() + "/manifest-characterisation-tool/src/main/webapp", "/tool"));
        server.start();
    }


    private static File getProjectRoot() throws IOException {
        return getProjectRoot(new File(".").getCanonicalFile());
    }

    private static File getProjectRoot(File here) {
        if (here == null) {
            throw new RuntimeException("Couldn't find root");
        }
        else if (isProjectParentDir(here)) {
            return here;
        }
        else {
            return getProjectRoot(here.getParentFile());
        }
    }

    private static boolean isProjectParentDir(File here) {
        return checkFor(here, "manifest-characterisation-tool");
    }

    private static boolean checkFor(File here, String... subDirectories) {
        File[] subdirs = here.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        for (String subDirectory : subDirectories) {
            if (!checkFor(subDirectory, subdirs)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkFor(String name, File[] subdirs) {
        if (subdirs == null) return false;

        for (File subdir : subdirs) {
            if (subdir.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

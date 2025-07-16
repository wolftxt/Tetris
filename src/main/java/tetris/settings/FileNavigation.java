package tetris.settings;

import java.io.File;
import java.net.URL;

public class FileNavigation {

    public static final String CONTROLLS_FILE_NAME = "ControllsSettings";
    public static final String SETTINGS_FILE_NAME = "Settings";

    /**
     * Returns the folder where the Tetris.jar file is located.
     *
     * @return
     */
    public static File getJarFolder() {
        URL path = FileNavigation.class.getProtectionDomain().getCodeSource().getLocation();
        return new File(path.getPath()).getParentFile();
    }

}

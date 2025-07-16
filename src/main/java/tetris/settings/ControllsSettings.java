package tetris.settings;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Singleton class used to hold and save the controlls settings.
 *
 * @author davidwolf
 */
public class ControllsSettings implements Serializable {

    private static ControllsSettings instance;

    private ControllsSettings() {
    }

    public static ControllsSettings getInstance() {
        if (instance == null) {
            instance = load();
        }
        if (instance == null) {
            instance = new ControllsSettings();
        }
        return instance;
    }

    public static void resetToDefaults() {
        instance = new ControllsSettings();
        File folder = FileNavigation.getJarFolder();
        File file = new File(folder, FileNavigation.CONTROLLS_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void save() throws IOException {
        File folder = FileNavigation.getJarFolder();
        File file = new File(folder, FileNavigation.CONTROLLS_FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(instance);
        oos.close();
    }

    private static ControllsSettings load() {
        File folder = FileNavigation.getJarFolder();
        File file = new File(folder, FileNavigation.CONTROLLS_FILE_NAME);

        ControllsSettings result = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            result = (ControllsSettings) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
        }
        return result;
    }

    public int hardDropKey = KeyEvent.VK_SPACE;
    public int holdKey = KeyEvent.VK_C;
    public int clockwiseKey = KeyEvent.VK_UP;
    public int clockwiseKeyAlternative = KeyEvent.VK_F;
    public int rotate180Key = KeyEvent.VK_D;
    public int counterClockwiseKey = KeyEvent.VK_S;
    public int softDropKey = KeyEvent.VK_DOWN;
    public int leftKey = KeyEvent.VK_LEFT;
    public int rightKey = KeyEvent.VK_RIGHT;

    public int helpKey = KeyEvent.VK_H;
    public int newGameKey = KeyEvent.VK_N;
    public int controllsKey = KeyEvent.VK_O;
}

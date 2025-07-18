package tetris.settings;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Singleton class holding UI settings.
 *
 * @author davidwolf
 */
public class GameSettings implements Serializable {

    private static GameSettings instance;

    private GameSettings() {
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = load();
        }
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public static void resetToDefaults() {
        instance = new GameSettings();
        File folder = FileNavigation.getJarFolder();
        File file = new File(folder, FileNavigation.SETTINGS_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void save() throws IOException {
        File folder = FileNavigation.getJarFolder();
        File file = new File(folder, FileNavigation.SETTINGS_FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(instance);
        oos.close();
    }

    private static GameSettings load() {
        File folder = FileNavigation.getJarFolder();
        File file = new File(folder, FileNavigation.SETTINGS_FILE_NAME);

        GameSettings result = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            result = (GameSettings) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
        }
        return result;
    }

    public int SHADOW_PIECE_ALPHA_VALUE = 100;
    public int PREVIEW_PIECE_COUNT = 5;
    public int SOFT_DROP_SPEED_IN_MS = 10;
    public int WAIT_TIME_AFTER_SOFT_DROP = 500;
    public int DAS = 150;
    public int ARR = 10;
    public boolean RANDOM_COLORS = false;
    public boolean SEVEN_BAG = true;
    public Color O = Color.YELLOW;
    public Color L = Color.ORANGE;
    public Color J = Color.BLUE;
    public Color T = Color.MAGENTA;
    public Color Z = Color.RED;
    public Color S = Color.GREEN;
    public Color I = Color.CYAN;
    public Font pageFont = new Font("PageFont", Font.PLAIN, 16);
}

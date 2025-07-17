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

    public int shadowPieceAlphaValue = 100;
    public int previewPieceCount = 5;
    public int softDropSpeedInMs = 10;
    public int hardDropSpeedAfterSoftDropInMs = 1000;
    public int DAS = 175;
    public int ARR = 15;
    public boolean randomColors = false;
    public boolean sevenBag = true;
    public Color O = Color.YELLOW;
    public Color L = Color.ORANGE;
    public Color J = Color.BLUE;
    public Color T = Color.MAGENTA;
    public Color Z = Color.RED;
    public Color S = Color.GREEN;
    public Color I = Color.CYAN;
    public Font pageFont = new Font("PageFont", Font.PLAIN, 16);
}

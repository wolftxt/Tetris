package tetris.settings;

import java.awt.Font;

/**
 * Singleton class holding UI settings.
 *
 * @author davidwolf
 */
public class Settings {

    private static Settings instance;

    private Settings() {
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    public boolean randomColors = false;
    public int shadowPieceAlphaValue = 100;
    public Font pageFont = new Font("PageFont", Font.PLAIN, 16);
}

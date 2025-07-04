package tetris.settings;

import java.awt.Font;

/**
 * Singleton class holding UI settings.
 *
 * @author davidwolf
 */
public class UISettings {

    private static UISettings instance;

    private UISettings() {
    }

    public static UISettings getInstance() {
        if (instance == null) {
            instance = new UISettings();
        }
        return instance;
    }
    public boolean randomColors = false;
    public int shadowPieceAlphaValue = 100;
    public Font pageFont = new Font("PageFont", Font.PLAIN, 16);
}

package tetris.settings;

import java.awt.Font;

public class UISettings {

    private static UISettings instance;

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

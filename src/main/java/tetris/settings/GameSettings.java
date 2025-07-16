package tetris.settings;

import java.awt.Font;

/**
 * Singleton class holding UI settings.
 *
 * @author davidwolf
 */
public class GameSettings {

    private static GameSettings instance;

    private GameSettings() {
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    public boolean randomColors = false;
    public int shadowPieceAlphaValue = 100;
    public Font pageFont = new Font("PageFont", Font.PLAIN, 16);
    public int previewPieceCount = 5;
    public boolean randomPieces = false;
    public int DAS = 175;
    public int ARR = 15;
}

package tetris.settings;

import java.awt.event.KeyEvent;
import java.io.Serializable;

/**
 * Singleton class used to hold and save the controlls settings.
 *
 * @author davidwolf
 */
public class ControllsSettings implements Serializable {

    private static ControllsSettings instance;

    public static ControllsSettings getInstance() {
        if (instance == null) {
            instance = new ControllsSettings();
        }
        return instance;
    }

    public static void resetToDefaults() {
        instance = new ControllsSettings();
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

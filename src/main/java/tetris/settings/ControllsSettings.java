package tetris.settings;

import java.awt.event.KeyEvent;

public class ControllsSettings {

    private static ControllsSettings instance;

    public static ControllsSettings getInstance() {
        if (instance == null) {
            instance = new ControllsSettings();
        }
        return instance;
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
}

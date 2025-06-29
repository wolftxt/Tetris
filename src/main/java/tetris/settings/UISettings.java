package tetris.settings;

public class UISettings {

    private static UISettings instance;

    public static UISettings getInstance() {
        if (instance == null) {
            instance = new UISettings();
        }
        return instance;
    }
    public boolean randomColors = false;
}

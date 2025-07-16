package tetris.view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;
import tetris.settings.ControllsSettings;
import tetris.settings.GameSettings;

/**
 * Class with static methods used by TetrisWindow to show popups.
 *
 * @author davidwolf
 */
public class Popups {

    public static TetrisWindow parent;

    public static void help() {
        Scanner sc = new Scanner(Popups.class.getResourceAsStream("/help.txt"));
        sc.useDelimiter("\\A");
        JOptionPane.showMessageDialog(parent, sc.next());
    }

    /**
     * Shows a popup asking the user for a number representing the game speed.
     * If user inputs invalid input, speed 2000 is used as default.
     *
     * @return
     */
    public static void gameSpeed() {
        int result = 0;
        String title = "You died / New game";
        String message = "Write the speed of the game in terms of time (in milliseconds) for a piece to fall.\nInvalid input will result in a game speed of 2000 (Piece falls every 2 seconds)";
        String initialInputText = "2000";
        String input = (String) JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE, null, null, initialInputText);
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            result = 2000;
        }
        parent.getWidget().newGame(result);
        parent.initMaps();
    }

    /**
     * Shows a popup for modifying controlls settings. For each row uses the
     * addRow() method.
     */
    public static void configureControllsSettings() {
        ControllsSettings cs = ControllsSettings.getInstance();
        GameSettings gs = GameSettings.getInstance();

        JDialog dialog = new JDialog(parent, "Configure settings:", true);
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Field field : cs.getClass().getDeclaredFields()) {
            Object object;
            try {
                object = field.get(cs);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                continue;
            }
            String value = KeyEvent.getKeyText((int) object);
            addRow(panel, field, value);
        }

        JButton resetKeys = new JButton("Reset all keybind settings to defaults");
        resetKeys.setFont(gs.pageFont);
        resetKeys.addActionListener(e -> {
            ControllsSettings.resetToDefaults();
            parent.initMaps();
            dialog.dispose();
        });
        JPanel wrapperKeys = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapperKeys.add(resetKeys);
        panel.add(wrapperKeys);

        for (Field field : gs.getClass().getDeclaredFields()) {
            Object object;
            try {
                object = field.get(gs);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                continue;
            }
            addRow(panel, field, object);
        }

        JButton resetGameSettings = new JButton("Reset all game settings to defaults");
        resetGameSettings.setFont(gs.pageFont);
        resetGameSettings.addActionListener(e -> {
            GameSettings.resetToDefaults();
            parent.initMaps();
            dialog.dispose();
        });
        JPanel wrapperGame = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapperGame.add(resetGameSettings);
        panel.add(wrapperGame);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Used for every row in configureControllsSettings() method. Allows the
     * user to click a button and press a key to modify the controll setting.
     * After a settings modification calls parent.initMaps() to update the
     * settings. (Makes sure changes take effect immediately and don't require a
     * restart)
     *
     * @param panel
     * @param field
     * @param object
     */
    private static void addRow(JPanel panel, Field field, Object object) {
        String value = getStringValue(object);
        if (value == null) {
            return;
        }
        GameSettings settings = GameSettings.getInstance();
        JPanel row = new JPanel(new BorderLayout(100, 10));

        JLabel label = new JLabel(field.getName() + ": ");
        label.setFont(settings.pageFont);
        row.add(label, BorderLayout.WEST);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel currValue = new JLabel(value);
        currValue.setHorizontalAlignment(SwingConstants.CENTER);
        currValue.setFont(settings.pageFont);
        left.add(currValue);

        JButton button = new JButton("Change");
        button.setFont(settings.pageFont);
        button.addActionListener(e -> {
            setSetting(field, object, currValue);
        });
        left.add(button);

        row.add(left, BorderLayout.EAST);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    private static String getStringValue(Object object) {
        switch (object) {
            case String s -> {
                return s;
            }
            case Color c -> {
                return Arrays.toString(c.getColorComponents(null));
            }
            case Integer i -> {
                return String.valueOf(i);
            }
            case Boolean b -> {
                return Boolean.toString(b);
            }
            default -> {
                return null;
            }
        }
    }

    private static void setSetting(Field field, Object object, JLabel panel) {
        GameSettings gs = GameSettings.getInstance();
        try {
            switch (object) {
                case Color c -> {
                    c = JColorChooser.showDialog(parent, "Choose a new color", c, true);
                    if (c == null) {
                        return;
                    }
                    field.set(gs, c);
                    panel.setText(getStringValue(c));
                }
                case Integer i -> {
                    String input = JOptionPane.showInputDialog(panel, "Set a numeric value");
                    try {
                        i = Integer.parseInt(input);
                        field.set(gs, i);
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid number input");
                    }
                    panel.setText(getStringValue(i));
                }
                case Boolean b -> { // IMPORTANT - ignores which class the boolean comes from, tries to get it from GameSettings
                    b = !field.getBoolean(GameSettings.getInstance());
                    field.set(gs, b);
                    panel.setText(getStringValue(b));
                }
                case String s -> {
                    panel.setForeground(Color.YELLOW);
                    panel.setText("Press key");

                    JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(panel);
                    dialog.requestFocusInWindow();

                    KeyAdapter keyAdapter = new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent evt) {
                            try {
                                field.setInt(ControllsSettings.getInstance(), evt.getKeyCode());
                                ControllsSettings.save();
                                parent.initMaps();

                                panel.setText(KeyEvent.getKeyText(evt.getKeyCode()));
                                panel.setForeground(Color.WHITE);
                            } catch (IllegalAccessException | IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                dialog.removeKeyListener(this);
                            }
                        }
                    };
                    dialog.addKeyListener(keyAdapter);
                }
                default ->
                    JOptionPane.showMessageDialog(parent, "Unsupported datatype", "Cannot modify the datatype of this setting", JOptionPane.ERROR_MESSAGE);
            }
            ControllsSettings.save();
            GameSettings.save();
        } catch (IllegalArgumentException | IllegalAccessException | IOException ex) {
            ex.printStackTrace();
        }
    }
}

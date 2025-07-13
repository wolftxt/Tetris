package tetris.view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Scanner;
import javax.swing.*;
import tetris.settings.ControllsSettings;
import tetris.settings.Settings;

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
        Settings settings = Settings.getInstance();

        JDialog dialog = new JDialog();
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
            addRow(panel, field, object);
        }
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton reset = new JButton("Reset all settings to defaults");
        reset.setFont(settings.pageFont);
        reset.addActionListener(e -> {
            ControllsSettings.resetToDefaults();
            parent.initMaps();
            dialog.dispose();
        });
        wrapper.add(reset);
        panel.add(wrapper);
        dialog.add(panel);
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
    public static void addRow(JPanel panel, Field field, Object object) {
        Settings settings = Settings.getInstance();
        JPanel row = new JPanel(new BorderLayout(100, 10));

        JLabel label = new JLabel(field.getName() + ": ");
        label.setFont(settings.pageFont);
        row.add(label, BorderLayout.WEST);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel currValue = new JLabel(KeyEvent.getKeyText((int) object));
        currValue.setHorizontalAlignment(SwingConstants.CENTER);
        currValue.setFont(settings.pageFont);
        left.add(currValue);

        JButton button = new JButton("Change");
        button.setFont(settings.pageFont);
        button.addActionListener(e -> {
            currValue.setForeground(Color.YELLOW);
            currValue.setText("Press key");

            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(panel);
            dialog.requestFocusInWindow();

            KeyAdapter keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    try {
                        field.setInt(ControllsSettings.getInstance(), evt.getKeyCode());
                        ControllsSettings.save();
                        parent.initMaps();

                        currValue.setText(KeyEvent.getKeyText(evt.getKeyCode()));
                        currValue.setForeground(Color.WHITE);
                    } catch (IllegalAccessException | IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        dialog.removeKeyListener(this);
                    }
                }
            };
            dialog.addKeyListener(keyAdapter);
        });
        left.add(button);

        row.add(left, BorderLayout.EAST);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

}

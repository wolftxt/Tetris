package tetris.view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Scanner;
import javax.swing.*;
import tetris.settings.ControllsSettings;
import tetris.settings.UISettings;

public class Popups {

    public static TetrisWindow parent;

    public static void help() {
        Scanner sc = new Scanner(Popups.class.getResourceAsStream("/help.txt"));
        sc.useDelimiter("\\A");
        JOptionPane.showMessageDialog(parent, sc.next());
    }

    public static int getGameSpeed() {
        int result = 0;
        String title = "Input game speed";
        String message = "Write the speed of the game in terms of time for a piece to fall.\nInvalid input will result in a game speed of 2000 (Piece falls every 2 seconds)";
        String input = JOptionPane.showInputDialog(parent, message, title);
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            result = 2000;
        }
        return result;
    }

    public static void configureControllsSettings() {
        ControllsSettings cs = ControllsSettings.getInstance();
        UISettings settings = UISettings.getInstance();

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

    public static void addRow(JPanel panel, Field field, Object object) {
        UISettings settings = UISettings.getInstance();
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
                        parent.initMaps();

                        currValue.setText(KeyEvent.getKeyText(evt.getKeyCode()));
                        currValue.setForeground(Color.WHITE);
                    } catch (IllegalAccessException ex) {
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

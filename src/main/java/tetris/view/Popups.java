package tetris.view;

import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Popups {

    public static JFrame parent;

    public static void help() {
        Scanner sc = new Scanner(Popups.class.getResourceAsStream("/help.txt"));
        sc.useDelimiter("\\A");
        JOptionPane.showMessageDialog(parent, sc.next());
    }

    public static int getGameSpeed() {
        int result = 0;
        String input;
        boolean validInput = false;
        while (!validInput) {
            try {
                input = JOptionPane.showInputDialog(parent, "Write the speed of the game in terms of time for a piece to fall.");
                result = Integer.parseInt(input);
                validInput = true;
            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

}

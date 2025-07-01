package tetris.view;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import tetris.controller.TetrisGame;
import tetris.settings.ControllsSettings;

/**
 * Class handling the window of the program and actions like key presses. Uses
 * FlatDarkLaf for looks and feels, hashmap to map keys to functions.
 *
 * @author davidwolf
 */
public class TetrisWindow extends javax.swing.JFrame {

    private Map<Integer, Runnable> releasedKeys;
    private Map<Integer, Runnable> pressedKeys;

    public TetrisWindow() {
        initComponents();
        this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                tetrisWidget1.requestFocusInWindow();
            }
        });
        initMaps();
    }

    public void initMaps() {
        ControllsSettings cs = ControllsSettings.getInstance();
        TetrisGame game = tetrisWidget1.getGame();

        // Released keys map
        releasedKeys = new HashMap();
        releasedKeys.put(cs.hardDropKey, () -> game.hardDrop());
        releasedKeys.put(cs.holdKey, () -> game.hold());
        releasedKeys.put(cs.clockwiseKey, () -> game.rotate(3));
        releasedKeys.put(cs.clockwiseKeyAlternative, () -> game.rotate(3));
        releasedKeys.put(cs.rotate180Key, () -> game.rotate(2));

        releasedKeys.put(cs.helpKey, () -> Popups.help());
        releasedKeys.put(cs.newGameKey, () -> {
            tetrisWidget1.newGame(Popups.getGameSpeed());
            initMaps();
        });
        releasedKeys.put(cs.controllsKey, () -> Popups.configureControllsSettings());

        // Pressed keys map
        pressedKeys = new HashMap();
        pressedKeys.put(cs.softDropKey, () -> game.softDrop());
        pressedKeys.put(cs.leftKey, () -> game.moveDirection(-1));
        pressedKeys.put(cs.rightKey, () -> game.moveDirection(1));
        pressedKeys.put(cs.softDropKey, () -> game.softDrop());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tetrisWidget1 = new tetris.view.TetrisWidget();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tetrisWidget1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tetrisWidget1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tetrisWidget1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tetrisWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tetrisWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tetrisWidget1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tetrisWidget1KeyReleased
        releasedKeys.getOrDefault(evt.getKeyCode(), () -> {
        }).run();
    }//GEN-LAST:event_tetrisWidget1KeyReleased

    private void tetrisWidget1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tetrisWidget1KeyPressed
        pressedKeys.getOrDefault(evt.getKeyCode(), () -> {
        }).run();
    }//GEN-LAST:event_tetrisWidget1KeyPressed

    public static void main(String args[]) {
        FlatDarkLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
            TetrisWindow window = new TetrisWindow();
            window.setVisible(true);
            Popups.parent = window;
            Popups.help();
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private tetris.view.TetrisWidget tetrisWidget1;
    // End of variables declaration//GEN-END:variables
}

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

    public TetrisWidget getWidget() {
        return tetrisWidget1;
    }

    public void initMaps() {
        ControllsSettings cs = ControllsSettings.getInstance();
        TetrisGame game = tetrisWidget1.getGame();

        // Released keys map
        releasedKeys = new HashMap();
        releasedKeys.put(cs.helpKey, () -> Popups.help());
        releasedKeys.put(cs.newGameKey, () -> Popups.gameSpeed());
        releasedKeys.put(cs.controllsKey, () -> Popups.configureControllsSettings());

        releasedKeys.put(cs.softDropKey, () -> game.softDropEnd());
        releasedKeys.put(cs.leftKey, () -> game.leftRightEnd());
        releasedKeys.put(cs.rightKey, () -> game.leftRightEnd());

        // Pressed keys map
        pressedKeys = new HashMap();
        pressedKeys.put(cs.softDropKey, () -> game.softDropStart());
        pressedKeys.put(cs.leftKey, () -> game.leftRightStart(-1));
        pressedKeys.put(cs.rightKey, () -> game.leftRightStart(1));

        pressedKeys.put(cs.hardDropKey, () -> game.hardDrop());
        pressedKeys.put(cs.holdKey, () -> game.hold());
        pressedKeys.put(cs.clockwiseKey, () -> game.rotate(3));
        pressedKeys.put(cs.clockwiseKeyAlternative, () -> game.rotate(3));
        pressedKeys.put(cs.rotate180Key, () -> game.rotate(2));
        pressedKeys.put(cs.counterClockwiseKey, () -> game.rotate(1));
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
            .addComponent(tetrisWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tetrisWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
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

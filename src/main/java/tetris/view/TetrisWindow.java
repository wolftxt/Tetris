package tetris.view;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import tetris.controller.TetrisGame;
import tetris.settings.ControllsSettings;

/**
 * Class handling the window of the program and actions like key presses. Uses
 * FlatDarkLaf for looks and feels, hashmap to map keys to functions.
 *
 * @author davidwolf
 */
public class TetrisWindow extends javax.swing.JFrame {

    private LinkedBlockingQueue<Runnable> userInputs;
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

        userInputs = new LinkedBlockingQueue();
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    userInputs.take().run();
                } catch (InterruptedException ex) {
                }
            }
        });
    }

    public TetrisWidget getWidget() {
        return tetrisWidget1;
    }

    public void initMaps() {
        ControllsSettings cs = ControllsSettings.getInstance();
        TetrisGame game = tetrisWidget1.getGame();

        // Released keys map
        releasedKeys = new HashMap();
        releasedKeys.put(cs.HELP_KEY, () -> Popups.help());
        releasedKeys.put(cs.NEW_GAME_KEY, () -> Popups.gameSpeed());
        releasedKeys.put(cs.CONTROLLS_KEY, () -> Popups.configureControllsSettings());

        releasedKeys.put(cs.SOFT_DROP_KEY, () -> game.softDropEnd());
        releasedKeys.put(cs.LEFT_KEY, () -> game.leftRightEnd(-1));
        releasedKeys.put(cs.RIGHT_KEY, () -> game.leftRightEnd(1));

        // Pressed keys map
        pressedKeys = new HashMap();
        pressedKeys.put(cs.SOFT_DROP_KEY, () -> game.softDropStart());
        pressedKeys.put(cs.LEFT_KEY, () -> game.leftRightStart(-1));
        pressedKeys.put(cs.RIGHT_KEY, () -> game.leftRightStart(1));

        pressedKeys.put(cs.HARD_DROP_KEY, () -> game.hardDrop());
        pressedKeys.put(cs.HOLD_KEY, () -> game.hold());
        pressedKeys.put(cs.CLOCKWISE_KEY, () -> game.rotate(3));
        pressedKeys.put(cs.CLOCKWISE_KEY_ALTERNATIVE, () -> game.rotate(3));
        pressedKeys.put(cs.ROTATE_180_KEY, () -> game.rotate(2));
        pressedKeys.put(cs.COUNTER_CLOCKWISE_KEY, () -> game.rotate(1));
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
        Runnable method = releasedKeys.get(evt.getKeyCode());
        if (method != null) {
            userInputs.add(method);
        }
    }//GEN-LAST:event_tetrisWidget1KeyReleased

    private void tetrisWidget1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tetrisWidget1KeyPressed
        Runnable method = pressedKeys.get(evt.getKeyCode());
        if (method != null) {
            userInputs.add(method);
        }
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

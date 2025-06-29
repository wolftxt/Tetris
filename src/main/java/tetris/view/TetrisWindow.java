package tetris.view;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import tetris.controller.TetrisGame;

public class TetrisWindow extends javax.swing.JFrame {

    public TetrisWindow() {
        initComponents();
        this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                tetrisWidget1.requestFocusInWindow();
            }
        });
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
            .addComponent(tetrisWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tetrisWidget1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tetrisWidget1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tetrisWidget1KeyReleased
        TetrisGame game = tetrisWidget1.getGame();
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                game.moveDirection(-1);
            }
            case KeyEvent.VK_RIGHT -> {
                game.moveDirection(1);
            }
            case KeyEvent.VK_SPACE -> {
                game.hardDrop();
            }
            case KeyEvent.VK_C -> {
                game.hold();
            }
            case KeyEvent.VK_UP -> {
                game.rotate(3); // rotate 3 times to rotate clockwise
            }
            case KeyEvent.VK_F -> {
                game.rotate(3); // rotate 3 times to rotate clockwise
            }
            case KeyEvent.VK_D -> {
                game.rotate(2); // rotate 2 times to rotate 180°
            }
            case KeyEvent.VK_S -> {
                game.rotate(1); // rotate 1 time to rotate counter-clockwise
            }
        }
    }//GEN-LAST:event_tetrisWidget1KeyReleased

    private void tetrisWidget1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tetrisWidget1KeyPressed
        TetrisGame game = tetrisWidget1.getGame();
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DOWN -> {
                game.softDrop();
            }
        }
    }//GEN-LAST:event_tetrisWidget1KeyPressed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new TetrisWindow().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private tetris.view.TetrisWidget tetrisWidget1;
    // End of variables declaration//GEN-END:variables
}

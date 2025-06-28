package tetris.controller;

import tetris.model.TetrisPlan;
import tetris.view.TetrisWidget;

public class TetrisGame {

    private TetrisPlan plan;

    public TetrisGame(TetrisWidget callback) {
        plan = new TetrisPlan();
        plan.newNextPiece();
        plan.newPiece();
        plan.newNextPiece();
        gameLoop(callback);
    }

    public TetrisPlan getPlan() {
        return plan;
    }

    private void gameLoop(TetrisWidget callback) {
        Thread.ofVirtual().start(() -> {
            try {
                while (plan.isPlaying()) {
                    Thread.sleep(1000);
                    if (!plan.moveDown()) {
                        plan.placePiece();
                        plan.newPiece();
                        plan.newNextPiece();
                    }
                    callback.repaint();
                }
            } catch (InterruptedException ex) {
                System.err.println("Game loop thread got unexpectedly interrupted");
            }
        });
    }
}

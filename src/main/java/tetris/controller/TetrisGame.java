package tetris.controller;

import tetris.model.TetrisPlan;
import tetris.view.TetrisWidget;

public class TetrisGame {

    private TetrisPlan plan;
    private TetrisWidget callback;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        this.callback = callback;

        plan = new TetrisPlan();
        plan.newNextPiece();
        plan.newPiece();
        plan.newNextPiece();
        gameLoop(timeToFall);
    }

    public TetrisPlan getPlan() {
        return plan;
    }

    public void hardDrop() {
        if (!plan.isPlaying()) {
            return;
        }
        while (plan.move(0, 1)) {
        }
        plan.placePiece();
        plan.newPiece();
        plan.newNextPiece();
        this.callback.repaint();
    }

    public void rotate(int rotateTimes) {
        if (!plan.isPlaying()) {
            return;
        }
        for (int i = 0; i < rotateTimes; i++) {
            plan.getPiece().rotate();
        }
        if (!plan.move(0, 0)) {
            for (int i = 0; i < 4 - rotateTimes; i++) {
                plan.getPiece().rotate();
            }
            return;
        }
        this.callback.repaint();
    }

    public void moveDirection(int direction) {
        if (!plan.isPlaying()) {
            return;
        }
        plan.move(direction, 0);
        callback.repaint();
    }

    private void gameLoop(int timeToFall) {
        Thread.ofVirtual().start(() -> {
            try {
                while (plan.isPlaying()) {
                    Thread.sleep(timeToFall);
                    if (!plan.move(0, 1)) {
                        plan.placePiece();
                        plan.newPiece();
                        plan.newNextPiece();
                    }
                    this.callback.repaint();
                }
            } catch (InterruptedException ex) {
                System.err.println("Game loop thread got unexpectedly interrupted");
            }
        });
    }
}

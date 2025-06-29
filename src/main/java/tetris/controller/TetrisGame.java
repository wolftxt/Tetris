package tetris.controller;

import tetris.model.TetrisPlan;
import tetris.view.TetrisWidget;

public class TetrisGame {

    private TetrisPlan plan;
    private TetrisWidget callback;

    private boolean softDrop;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        plan = new TetrisPlan();
        this.callback = callback;

        softDrop = false;

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

    public void softDrop() {
        plan.move(0, 1);
        callback.repaint();
    }

    public void hold() {
        if (plan.hold()) {
            this.callback.repaint();
        }
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
            while (plan.isPlaying()) {
                try {
                    int time = softDrop ? timeToFall / 10 : timeToFall;
                    Thread.sleep(time);

                    if (!plan.move(0, 1)) {
                        plan.placePiece();
                        plan.newPiece();
                        plan.newNextPiece();
                    }
                    this.callback.repaint();
                } catch (InterruptedException ex) {
                }
            }
        });
    }
}

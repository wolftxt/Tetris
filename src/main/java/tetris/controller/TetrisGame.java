package tetris.controller;

import tetris.model.TetrisPlan;
import tetris.view.TetrisWidget;

/**
 * Controller used to handle game logic, game loop and user inputs like soft
 * dropping, hard dropping, rotating, moving left and right.
 *
 * @author davidwolf
 */
public class TetrisGame {

    private TetrisPlan plan;
    private TetrisWidget callback;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        plan = new TetrisPlan();
        this.callback = callback;

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
        settlePieceInPlace();
        if (!plan.move(0, 0)) {
            for (int i = 0; i < 4 - rotateTimes; i++) {
                plan.getPiece().rotate();
            }
            return;
        }
        this.callback.repaint();
    }

    /**
     * Method used to move around a piece after rotating to find a valid place.
     * Allows the piece to rotate when close to the ceiling or to the walls.
     */
    private void settlePieceInPlace() {
        if (plan.move(0, 0)
                || plan.move(0, 1)
                || plan.move(1, 0) || plan.move(-1, 0)
                || plan.move(2, 0) || plan.move(-2, 0)) {
        }
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
                    Thread.sleep(timeToFall);
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

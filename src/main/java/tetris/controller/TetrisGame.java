package tetris.controller;

import java.lang.Thread.State;
import java.util.concurrent.locks.LockSupport;
import tetris.model.TetrisPlan;
import tetris.view.Popups;
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
    private Thread gameLoopThread;
    private Thread softDropThread;
    private Thread leftRightThread;
    private boolean softDrop;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        softDrop = false;
        plan = new TetrisPlan();
        this.callback = callback;

        plan.newNextPiece();
        plan.newPiece();
        plan.newNextPiece();
        initiateThreads(timeToFall);
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
        callback.repaint();
    }

    public void softDropStart() {
        if (!softDrop) {
            softDrop = true;
            gameLoopThread.interrupt();
            LockSupport.unpark(softDropThread);
        }
    }

    public void softDropEnd() {
        if (softDrop) {
            softDrop = false;
            softDropThread.interrupt();
            LockSupport.unpark(gameLoopThread);
        }
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

    public void leftRightStart(int direction) {
        if (!plan.isPlaying()) {
            return;
        }
        leftRight(direction);
    }

    public void leftRightEnd() {
        if (!plan.isPlaying()) {
            return;
        }
        leftRightThread.interrupt();
    }

    private void initiateThreads(int timeToFall) {
        gameLoopThread = Thread.ofVirtual().start(() -> {
            while (plan.isPlaying()) {
                try {
                    Thread.sleep(timeToFall);
                    if (!plan.move(0, 1)) {
                        plan.placePiece();
                        plan.newPiece();
                        plan.newNextPiece();
                    }
                    callback.repaint();
                } catch (InterruptedException ex) {
                    LockSupport.park();
                    while (softDrop) {
                        LockSupport.park();
                    }
                }
            }
            Popups.gameSpeed();
        });

        softDropThread = Thread.ofVirtual().start(() -> {
            LockSupport.park();
            while (plan.isPlaying()) {
                try {
                    Thread.sleep(10);
                    if (!plan.move(0, 1)) {
                        LockSupport.unpark(gameLoopThread);
                        LockSupport.park();
                        while (!softDrop) {
                            LockSupport.park();
                        }
                    }
                    callback.repaint();
                } catch (InterruptedException ex) {
                    LockSupport.park();
                    while (!softDrop) {
                        LockSupport.park();
                    }
                }
            }
        });
    }

    private void leftRight(int direction) {
        leftRightThread = Thread.ofVirtual().start(() -> {
            try {
                if (!plan.move(direction, 0)) {
                    return;
                }
                callback.repaint();
                Thread.sleep(175);
                while (plan.move(direction, 0)) {
                    callback.repaint();
                    Thread.sleep(15);
                }
            } catch (InterruptedException e) {
            }
        });
    }
}

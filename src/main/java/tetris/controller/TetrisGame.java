package tetris.controller;

import java.util.concurrent.locks.LockSupport;
import tetris.model.TetrisPlan;
import tetris.settings.GameSettings;
import tetris.view.Popups;
import tetris.view.TetrisWidget;

/**
 * Controller used to handle game logic, game loop and user inputs like soft
 * dropping, hard dropping, rotating, moving left and right.
 *
 * @author davidwolf
 */
public class TetrisGame {

    private final TetrisPlan plan;
    private final TetrisWidget callback;
    private Thread gameLoopThread;
    private Thread leftRightThread;
    private int leftRight;
    private boolean softDrop;
    private boolean moved;

    private boolean terminated;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        softDrop = false;
        terminated = false;
        plan = new TetrisPlan();
        this.callback = callback;

        plan.newNextPieces();
        plan.newPiece();
        initiateThreads(timeToFall);
    }

    public void terminateGame() {
        terminated = true;
        plan.setPlaying(false);
        leftRightThread.interrupt();
        gameLoopThread.interrupt();
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
        callback.repaint();
    }

    public void softDropStart() {
        if (!softDrop) {
            softDrop = true;
            gameLoopThread.interrupt();
        }
    }

    public void softDropEnd() {
        softDrop = false;
        if (!moved) {
            gameLoopThread.interrupt();
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
        if (plan.getPiece().rotateSRS(rotateTimes, plan)) {
            checkSoftDrop();
            this.callback.repaint();
        }
    }

    public void leftRightStart(int direction) {
        if (!plan.isPlaying() || leftRight == direction) {
            return;
        }
        leftRight = direction;
        LockSupport.unpark(leftRightThread);
    }

    public void leftRightEnd(int direction) {
        if (!plan.isPlaying()) {
            return;
        }
        if (direction == leftRight) {
            leftRight = 0;
            leftRightThread.interrupt();
        }
    }

    private void gameLoop(int timeToFall) throws InterruptedException {
        while (plan.isPlaying()) {
            Thread.sleep(timeToFall);
            if (!plan.move(0, 1)) {
                plan.placePiece();
                plan.newPiece();
            }
            callback.repaint();
        }
    }

    /**
     * Soft drop implementation. Moves down until key is released or reaches the
     * ground. Then if the user keeps moving the piece, gives the user up to 10x
     * gs.WAIT_TIME_AFTER_SOFT_DROP time until it drops.
     */
    private void softDrop() {
        try {
            GameSettings gs = GameSettings.getInstance();
            while (plan.move(0, 1)) {
                if (gs.SOFT_DROP_SPEED_IN_MS > 0) {
                    callback.repaint();
                }
                Thread.sleep(gs.SOFT_DROP_SPEED_IN_MS);
            }
            callback.repaint();
            int count = 0;
            do {
                count++;
                moved = false;
                Thread.sleep(gs.WAIT_TIME_AFTER_SOFT_DROP);
            } while (moved && count < 10 && softDrop);

            if (!plan.move(0, 1)) {
                plan.placePiece();
                plan.newPiece();
            }
            callback.repaint();
        } catch (InterruptedException e) {
        }
    }

    private void initiateThreads(int timeToFall) {
        GameSettings gs = GameSettings.getInstance();

        gameLoopThread = Thread.ofVirtual().start(() -> {
            while (plan.isPlaying()) {
                try {
                    if (softDrop) {
                        softDrop();
                    } else {
                        gameLoop(timeToFall);
                    }
                } catch (InterruptedException e) {
                }
            }
            if (!terminated) {
                Popups.gameSpeed();
            }
        });

        leftRightThread = Thread.ofVirtual().start(() -> {
            while (plan.isPlaying()) {
                try {
                    while (leftRight == 0) {
                        LockSupport.park();
                    }
                    plan.move(leftRight, 0);
                    checkSoftDrop();
                    if (gs.DAS > 0) {
                        callback.repaint();
                    }
                    Thread.sleep(gs.DAS);
                    while (!Thread.interrupted()) {
                        if (plan.move(leftRight, 0)) {
                            checkSoftDrop();
                            if (gs.ARR > 0) {
                                callback.repaint();
                            }
                        }
                        callback.repaint();
                        Thread.sleep(gs.ARR);
                    }
                } catch (InterruptedException e) {
                }
            }
        });
    }

    public void checkSoftDrop() {
        if (!softDrop) {
            return;
        }
        if (!plan.isLegal(0, 1) && !moved) { // 00
            moved = true;
        }
        if (plan.isLegal(0, 1) && moved) { // 11
            gameLoopThread.interrupt();
        }
    }

}

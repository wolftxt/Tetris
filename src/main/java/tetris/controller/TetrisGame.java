package tetris.controller;

import java.util.concurrent.locks.LockSupport;
import javax.swing.SwingUtilities;
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
        gameLoopThread.interrupt();
        leftRightThread.interrupt();
        try {
            gameLoopThread.join();
            leftRightThread.join();
        } catch (InterruptedException ex) {
        }
    }

    public TetrisPlan getPlan() {
        return plan;
    }

    public synchronized void hardDrop() {
        if (!plan.isPlaying()) {
            return;
        }
        while (plan.move(0, 1)) {
        }
        plan.placePiece();
        plan.newPiece();
        repaint();
    }

    public void softDropStart() {
        if (!softDrop) {
            softDrop = true;
            gameLoopThread.interrupt();
        }
    }

    public void softDropEnd() {
        softDrop = false;
    }

    public synchronized void hold() {
        if (plan.hold()) {
            repaint();
        }
    }

    public synchronized void rotate(int rotateTimes) {
        if (!plan.isPlaying()) {
            return;
        }
        if (plan.getPiece().rotateSRS(rotateTimes, plan)) {
            checkSoftDrop();
            repaint();
        }
    }

    public void leftRightStart(int direction) {
        if (!plan.isPlaying() || leftRight == direction) {
            return;
        }
        leftRight = direction;
        leftRightThread.interrupt();
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
            synchronized (this) {
                if (!plan.move(0, 1)) {
                    plan.placePiece();
                    plan.newPiece();
                }
                repaint();
            }
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
            while (plan.isLegal(0, 1)) {
                if (!softDrop) {
                    return;
                }
                synchronized (this) {
                    if (gs.SOFT_DROP_SPEED_IN_MS == 0) {
                        while (plan.move(0, 1)) {
                        }
                        repaint();
                        continue;
                    }
                    if (plan.move(0, 1)) {
                        repaint();
                    }
                }
                long endTime = System.currentTimeMillis() + gs.SOFT_DROP_SPEED_IN_MS;
                while (System.currentTimeMillis() < endTime && plan.isPlaying()) {
                    try { // Ensures the correct sleep time even when interrupted
                        Thread.sleep(endTime - System.currentTimeMillis());
                    } catch (InterruptedException e) {
                    }
                }
                if (!plan.isPlaying()) {
                    return;
                }
            }
            int count = 0;
            do {
                count++;
                moved = false;
                Thread.sleep(gs.WAIT_TIME_AFTER_SOFT_DROP);
            } while (moved && count < 10);

            synchronized (this) {
                if (!plan.move(0, 1)) {
                    plan.placePiece();
                    plan.newPiece();
                }
                repaint();
            }
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
                    while (plan.isPlaying() && leftRight == 0) {
                        LockSupport.park();
                    }
                    synchronized (this) {
                        if (plan.move(leftRight, 0)) {
                            checkSoftDrop();
                            if (gs.DAS > 0) {
                                repaint();
                            }
                        }
                    }
                    Thread.sleep(gs.DAS);
                    while (!Thread.interrupted() && plan.isPlaying()) {
                        while (!plan.isLegal(leftRight, 0)) {
                            Thread.sleep(5);
                        }
                        synchronized (this) {
                            if (gs.ARR == 0) {
                                while (plan.move(leftRight, 0)) {
                                    checkSoftDrop();
                                }
                                repaint();
                                continue;
                            }
                            if (plan.move(leftRight, 0)) {
                                checkSoftDrop();
                                repaint();
                            }
                        }
                        Thread.sleep(gs.ARR);
                    }
                } catch (InterruptedException e) {
                }
            }
        });
    }

    private synchronized void checkSoftDrop() {
        moved = true;
        if (!softDrop) {
            return;
        }
        if (plan.isLegal(0, 1)) {
            gameLoopThread.interrupt();
        }
    }

    public void repaint() {
        SwingUtilities.invokeLater(() -> {
            synchronized (TetrisGame.this) {
                callback.repaint();
            }
        });
    }

}

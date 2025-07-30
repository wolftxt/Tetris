package tetris.controller;

import java.util.concurrent.locks.LockSupport;
import javax.swing.SwingUtilities;
import tetris.model.TetrisPlan;
import tetris.settings.GameSettings;
import tetris.view.Popups;
import tetris.view.TetrisWidget;

/**
 * Controller used to handle game logic, game loop and user inputs like soft
 * dropping, hard dropping, rotating, moving left and right. Uses
 * synchronization to prevent race conditions
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
    private boolean isLRlegal;

    private boolean terminated;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        softDrop = false;
        terminated = false;
        isLRlegal = false;
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
        placePiece();
        checkSoftDrop();
        checkLRlegality();
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
            checkSoftDrop();
            checkLRlegality();
            repaint();
        }
    }

    public synchronized void rotate(int rotateTimes) {
        if (!plan.isPlaying()) {
            return;
        }
        if (plan.getPiece().rotateSRS(rotateTimes, plan)) {
            checkSoftDrop();
            checkLRlegality();
            repaint();
        }
    }

    public void leftRightStart(int direction) {
        if (!plan.isPlaying() || leftRight == direction) {
            return;
        }
        leftRight = direction;
        checkLRlegality();
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
                    placePiece();
                }
                checkLRlegality();
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
                            checkLRlegality();
                        }
                        repaint();
                        continue;
                    }
                    if (plan.move(0, 1)) {
                        checkLRlegality();
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
                    placePiece();
                }
                checkLRlegality();
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
                    Thread.interrupted();
                    synchronized (this) {
                        if (plan.move(leftRight, 0)) {
                            checkLRlegality();
                            checkSoftDrop();
                            if (gs.DAS > 0) {
                                repaint();
                            }
                        }
                    }
                    Thread.sleep(gs.DAS);
                    while (!Thread.interrupted() && plan.isPlaying()) {
                        while (plan.isPlaying() && !isLRlegal) {
                            LockSupport.park(); // Ensures that threads keeps trying to move the direction without freezin ghte program
                            if (Thread.interrupted()) {
                                throw new InterruptedException("");
                            }
                        }
                        synchronized (this) {
                            if (gs.ARR == 0) {
                                while (plan.move(leftRight, 0)) {
                                    checkSoftDrop();
                                }
                                isLRlegal = false;
                                repaint();
                            } else {
                                if (plan.move(leftRight, 0)) {
                                    checkSoftDrop();
                                    checkLRlegality();
                                    repaint();
                                }
                            }
                        }
                        Thread.sleep(gs.ARR);
                    }
                } catch (InterruptedException e) {
                }
            }
        });
    }

    private void placePiece() {
        plan.placePiece();
        if (!plan.newPiece()) { // ensures the game terminates immediately when the player tops out
            leftRightThread.interrupt();
            gameLoopThread.interrupt();
        }
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

    private void checkLRlegality() {
        if (leftRight == 0) {
            return;
        }
        isLRlegal = plan.isLegal(leftRight, 0);
        if (isLRlegal) {
            LockSupport.unpark(leftRightThread);
        }
    }

    public void repaint() {
        SwingUtilities.invokeLater(() -> {
            synchronized (TetrisGame.this) { // prevents reading illegal state
                callback.repaint();
            }
        });
    }

}

package tetris.controller;

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
        if (leftRightThread != null) {
            leftRightThread.interrupt();
        }
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
        if (softDrop) {
            softDrop = false;
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
            this.callback.repaint();
        }
    }

    public void leftRightStart(int direction) {
        if (!plan.isPlaying() || leftRight == direction) {
            return;
        }
        leftRight = direction;
        if (leftRightThread != null) {
            leftRightThread.interrupt();
        }
        leftRight(direction);
    }

    public void leftRightEnd(int direction) {
        if (!plan.isPlaying()) {
            return;
        }
        if (direction == leftRight) {
            leftRightThread.interrupt();
            leftRight = 0;
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

    private void softDrop() throws InterruptedException {
        GameSettings gs = GameSettings.getInstance();
        while (plan.move(0, 1)) {
            callback.repaint();
            Thread.sleep(gs.softDropSpeedInMs);
        }
        softDrop = false;
        try {
            Thread.sleep(gs.hardDropSpeedAfterSoftDropInMs);
        } catch (InterruptedException e) {
        } finally {
            if (!plan.move(0, 1)) {
                plan.placePiece();
                plan.newPiece();
            }
        }
    }

    private void initiateThreads(int timeToFall) {
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
    }

    private void leftRight(int direction) {
        GameSettings gs = GameSettings.getInstance();
        leftRightThread = Thread.ofVirtual().start(() -> {
            try {
                if (!plan.move(direction, 0)) {
                    return;
                }
                callback.repaint();
                Thread.sleep(gs.DAS);
                while (true) {
                    plan.move(direction, 0);
                    callback.repaint();
                    Thread.sleep(gs.ARR);
                }
            } catch (InterruptedException e) {
            }
        });
    }
}

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
    private boolean softDrop;
    private int leftRight;

    public TetrisGame(TetrisWidget callback, int timeToFall) {
        softDrop = false;
        plan = new TetrisPlan();
        this.callback = callback;

        plan.newNextPieces();
        plan.newPiece();
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

    public synchronized void leftRightStart(int direction) {
        if (!plan.isPlaying() || leftRight == direction) {
            return;
        }
        leftRight = direction;
        if (leftRightThread != null) {
            leftRightThread.interrupt();
        }
        leftRight(direction);
    }

    public synchronized void leftRightEnd(int direction) {
        if (!plan.isPlaying()) {
            return;
        }
        if (direction == leftRight) {
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

    private void softDrop() throws InterruptedException {
        while (plan.move(0, 1)) {
            callback.repaint();
            Thread.sleep(10);
        }
        softDrop = false;
        Thread.sleep(1000);
        hardDrop();
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
            Popups.gameSpeed();
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
                while (plan.move(direction, 0)) {
                    callback.repaint();
                    Thread.sleep(gs.ARR);
                }
            } catch (InterruptedException e) {
            }
            leftRight = 0;
        });
    }
}

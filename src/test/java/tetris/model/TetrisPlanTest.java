package tetris.model;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class TetrisPlanTest {

    public TetrisPlanTest() {
    }

    @Test
    public void testMoveDown() {
        TetrisPlan plan = new TetrisPlan();
        plan.newPiece();
        for (int i = 0; i < 18; i++) {
            Assert.assertEquals(plan.moveDown(), true);
        }
        Assert.assertEquals(plan.moveDown(), false);
    }

    @Test
    public void testPlacePiece() {
        TetrisPlan plan = new TetrisPlan();
        plan.newPiece();
        for (int i = 0; i < 18; i++) {
            plan.moveDown();
        }
        plan.placePiece();
        plan.newNextPiece();
        plan.newPiece();
        for (int i = 0; i < 16; i++) {
            plan.moveDown();
        }
        Assert.assertEquals(plan.moveDown(), false);
    }
}

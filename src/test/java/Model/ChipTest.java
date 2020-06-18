package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChipTest {

    @Test
    void moveTo() {
        Chip chip = new Chip(ChipColor.White);
        int row=1;
        int col=3;
        chip.moveTo(row,col);
        int realRow=chip.getRow();
        int realCol=chip.getCol();
        assertEquals(row, realRow);
        assertEquals(col, realCol);
    }

    @Test
    void isOut() {
        Chip chip = new Chip(ChipColor.Black);
        chip.moveOut();
        assertTrue(chip.isOut());
        assertFalse(chip.isOnHand());
        chip.throwOff();
        assertFalse(chip.isOut());
        assertTrue(chip.isOnHand());
        chip.moveTo(1,1);
        assertFalse(chip.isOut());
        assertFalse(chip.isOnHand());
    }

    @Test
    void isOnHand() {
        Chip chip = new Chip(ChipColor.White);
        chip.throwOff();
        assertTrue(chip.isOnHand());
        assertFalse(chip.isOut());
        int realRow=chip.getRow();
        int realCol=chip.getCol();
        assertEquals(-1, realRow);
        assertEquals(-1, realCol);
        chip.moveTo(1,1);
        assertFalse(chip.isOnHand());
        chip.moveOut();
        assertFalse(chip.isOnHand());
    }

    @Test
    void moveOut() {
        Chip chip = new Chip(ChipColor.Black);
        chip.moveOut();
        assertTrue(chip.isOut());
        assertFalse(chip.isOnHand());
        int realRow=chip.getRow();
        int realCol=chip.getCol();
        assertEquals(-2, realRow);
        assertEquals(-2, realCol);
    }

    @Test
    void throwOff() {
        Chip chip = new Chip(ChipColor.Black);
        chip.throwOff();
        assertTrue(chip.isOnHand());
        assertFalse(chip.isOut());
        int realRow=chip.getRow();
        int realCol=chip.getCol();
        assertEquals(-1, realRow);
        assertEquals(-1, realCol);
    }
}
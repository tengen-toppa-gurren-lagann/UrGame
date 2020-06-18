package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {
    private boolean assertIntInRange(int num, int min, int max) {
        if (num>=min && num<=max) return true;
        else return false;
    }

    @Test
    void diceRoll() {
        Dice dice = new Dice();
        boolean was0=false; // Выпадал 0
        boolean was1=false; // Выпадала 1
        boolean was2=false; // Выпадало 2
        boolean was3=false; // Выпадало 3
        boolean was4=false; // Выпадало 4ы
        for (int i=0; i<100; i++) { // 100 раз бросаем кубики и проверяем, что выпадают только числа от 0 до 4
            int diceNum = dice.diceRoll();
            if (diceNum==0) was0=true;
            if (diceNum==1) was1=true;
            if (diceNum==2) was2=true;
            if (diceNum==3) was3=true;
            if (diceNum==4) was4=true;
            assertTrue(assertIntInRange(diceNum, 0, 4));
        }
        // Ещё проверяем, что выпадали ВСЕ возможные числа от 0 до 4 (за 100 бросков они точно должны были выпасть все)
        assertTrue(was0);
        assertTrue(was1);
        assertTrue(was2);
        assertTrue(was3);
        assertTrue(was4);
    }

}
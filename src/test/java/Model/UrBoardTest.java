package Model;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrBoardTest {

    private UrBoard board;

    @BeforeEach
    void setUp() {
        board = new UrBoard();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCell() {
        UrBoard.CellType cell = board.getCell(1, 1); // Клетка в пределах доски
        assertNotNull(cell);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(-1, -1); // Клетка за пределами доски -> должно быть брошено исключение
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(3, 0); // Клетка за пределами доски -> должно быть брошено исключение
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getCell(0, 8); // Клетка за пределами доски -> должно быть брошено исключение
        });
    }

    @Test
    void getWhiteChips() {
        boolean allWhite=true; // Все ли фишки белые?
        Chip[] chips = board.getWhiteChips();
        assertEquals(board.chipsTotalNum, chips.length); // Получили ли все фишки?
        for (int i=0; i<board.chipsTotalNum; i++) {
            if (chips[i].getColor()==ChipColor.Black) {
                allWhite=false;
            }
        }
        assertTrue(allWhite); // Все ли фишки белые?
    }

    @Test
    void getBlackChips() {
        boolean allBlack=true; // Все ли фишки черные?
        Chip[] chips = board.getBlackChips();
        assertEquals(board.chipsTotalNum, chips.length); // Получили ли все фишки?
        for (int i=0; i<board.chipsTotalNum; i++) {
            if (chips[i].getColor()==ChipColor.White) {
                allBlack=false;
            }
        }
        assertTrue(allBlack); // Все ли фишки черные?
    }

    @Test
    void getWhitesOnHandCnt() {
        Chip[] chips = board.getWhiteChips();
        assertEquals(board.chipsTotalNum, board.getWhitesOnHandCnt()); // Сначала все фишки на руке
        board.moveChipOrCheck(chips[0],1,false); // Переместим на доску одну фишку
        assertEquals(board.chipsTotalNum-1, board.getWhitesOnHandCnt()); // На руке должно стать на одну меньше
        for (int i=0;i<board.chipsTotalNum; i++) { // Переместим на доску все фишки
            chips[i].moveTo(1,1);
        }
        assertEquals(0, board.getWhitesOnHandCnt()); // На руке должно остаться 0
    }

    @Test
    void getBlacksOnHandCnt() {
        Chip[] chips = board.getBlackChips();
        assertEquals(board.chipsTotalNum, board.getBlacksOnHandCnt()); // Сначала все фишки на руке
        board.moveChipOrCheck(chips[0],1,false); // Переместим на доску одну фишку
        assertEquals(board.chipsTotalNum-1, board.getBlacksOnHandCnt()); // На руке должно стать на одну меньше
        for (int i=0;i<board.chipsTotalNum; i++) { // Переместим на доску все фишки
            chips[i].moveTo(1,1);
        }
        assertEquals(0, board.getBlacksOnHandCnt()); // На руке должно остаться 0
    }

    @Test
    void getWhiteChipFromHand() {
        Chip chip = board.getWhiteChipFromHand(); // Сначала все фишки на руке
        assertNotNull(chip);
        Chip[] chips = board.getWhiteChips();
        for (int i=0;i<board.chipsTotalNum; i++) { // Переместим на доску все фишки
            chips[i].moveTo(1,1);
        }
        chip = board.getWhiteChipFromHand(); // На руке фишек нет
        assertNull(chip);
    }

    @Test
    void getBlackChipFromHand() {
        Chip chip = board.getBlackChipFromHand(); // Сначала все фишки на руке
        assertNotNull(chip);
        Chip[] chips = board.getBlackChips();
        for (int i=0;i<board.chipsTotalNum; i++) { // Переместим на доску все фишки
            chips[i].moveTo(1,1);
        }
        chip = board.getBlackChipFromHand(); // На руке фишек нет
        assertNull(chip);
    }

    @Test
    void getWhitesOutCnt() {
        Chip[] chips = board.getWhiteChips(); // Сначала все фишки на руке
        assertEquals(0,board.getWhitesOutCnt());
        for (int i=0;i<board.chipsTotalNum; i++) { // Переместим на доску все фишки
            chips[i].moveTo(1,1);
        }
        assertEquals(0,board.getWhitesOutCnt());
        chips[0].moveOut(); // Выведем с доски одну
        assertEquals(1,board.getWhitesOutCnt());
        for (int i=0;i<board.chipsTotalNum; i++) { // Выведем с доски все фишки
            chips[i].moveOut();
        }
        assertEquals(board.chipsTotalNum,board.getWhitesOutCnt());
    }

    @Test
    void getBlacksOutCnt() {
        Chip[] chips = board.getBlackChips(); // Сначала все фишки на руке
        assertEquals(0,board.getBlacksOutCnt());
        for (int i=0;i<board.chipsTotalNum; i++) { // Переместим на доску все фишки
            chips[i].moveTo(1,1);
        }
        assertEquals(0,board.getBlacksOutCnt());
        chips[0].moveOut(); // Выведем с доски одну
        assertEquals(1,board.getBlacksOutCnt());
        for (int i=0;i<board.chipsTotalNum; i++) { // Выведем с доски все фишки
            chips[i].moveOut();
        }
        assertEquals(board.chipsTotalNum,board.getBlacksOutCnt());
    }

    @Test
    void getCellForMoveChip() {
        Pair<Integer,Integer> pos;
        Chip[] wChips = board.getWhiteChips();
        Chip[] bChips = board.getBlackChips();
        pos = board.getCellForMoveChip(null);
        assertEquals(new Pair(-1,-1), pos);

        // Проверяем ход белой фишки с руки
        wChips[0].moveTo(0,0); // Ставим первую фишку на (0,0)
        board.setDiceNum(4); // На кубике - 4
        pos = board.getCellForMoveChip(wChips[1]);
        assertEquals(new Pair(-1,-1), pos); // Фишка не может пойти, т.к. клетка занята фишкой своего цвета
        board.setDiceNum(3); // На кубике - 3
        pos = board.getCellForMoveChip(wChips[1]);
        assertEquals(new Pair(0,1), pos); // Фишка может пойти

        // Проверяем ход белой фишки с поля
        wChips[0].moveTo(0,0); // Ставим первую белую фишку на (0,0)
        wChips[1].moveTo(1,0); // Ставим вторую белую фишку на (1,0)
        board.setDiceNum(1); // На кубике - 1
        pos = board.getCellForMoveChip(wChips[0]);
        assertEquals(new Pair(-1,-1), pos); // Первая фишка не может пойти, т.к. клетка занята фишкой своего цвета

        wChips[1].throwOff(); // Сбрасываем вторую белую фишку с поля (1,0)
        bChips[0].moveTo(1,0); // И ставим на это поле черную фишку
        pos = board.getCellForMoveChip(wChips[0]);
        assertEquals(new Pair(1,0), pos); // В этом случае первая белая фишка может пойти, сбросив черную (поле - не "розетка")

        bChips[0].moveTo(1,3); // Ставим черную фишку на поле-розетку
        board.setDiceNum(4); // На кубике - 4
        pos = board.getCellForMoveChip(wChips[0]);
        assertEquals(new Pair(-1,-1), pos); // В этом случае первая белая фишка не может пойти, сбросив черную (т.к. та стоит на поле - "розетке")

        // Сбрасываем фишки с поля, повторяем тесты для черных фишек
        wChips[0].throwOff();
        wChips[1].throwOff();

        // Проверяем ход черной фишки с руки
        bChips[0].moveTo(2,0); // Ставим первую фишку на (2,0)
        board.setDiceNum(4); // На кубике - 4
        pos = board.getCellForMoveChip(bChips[1]);
        assertEquals(new Pair(-1,-1), pos); // Фишка не может пойти, т.к. клетка занята фишкой своего цвета
        board.setDiceNum(3); // На кубике - 3
        pos = board.getCellForMoveChip(bChips[1]);
        assertEquals(new Pair(2,1), pos); // Фишка может пойти

        // Проверяем ход черной фишки с поля
        bChips[0].moveTo(2,0); // Ставим первую черную фишку на (2,0)
        bChips[1].moveTo(1,0); // Ставим вторую черную фишку на (1,0)
        board.setDiceNum(1); // На кубике - 1
        pos = board.getCellForMoveChip(bChips[0]);
        assertEquals(new Pair(-1,-1), pos); // Первая фишка не может пойти, т.к. клетка занята фишкой своего цвета

        bChips[1].throwOff(); // Сбрасываем вторую черную фишку с поля (1,0)
        wChips[0].moveTo(1,0); // И ставим на это поле белую фишку
        pos = board.getCellForMoveChip(bChips[0]);
        assertEquals(new Pair(1,0), pos); // В этом случае первая черная фишка может пойти, сбросив белую (поле - не "розетка")

        wChips[0].moveTo(1,3); // Ставим белую фишку на поле-розетку
        board.setDiceNum(4); // На кубике - 4
        pos = board.getCellForMoveChip(bChips[0]);
        assertEquals(new Pair(-1,-1), pos); // В этом случае первая черная фишка не может пойти, сбросив белую (т.к. та стоит на поле - "розетке")

        // Проверяем правильность выхода белой фишки с доски
        wChips[0].moveTo(0,7);
        board.setDiceNum(2); // На кубике - 2
        pos = board.getCellForMoveChip(wChips[0]);
        assertEquals(new Pair(-2,-2), pos);

        // Проверяем правильность выхода черной фишки с доски
        bChips[0].moveTo(2,7);
        board.setDiceNum(2); // На кубике - 2
        pos = board.getCellForMoveChip(bChips[0]);
        assertEquals(new Pair(-2,-2), pos);

    }

    @Test
    void getChipByPos() {
        Chip chip;
        chip = board.getChipByPos(-3,-3);
        assertNull(chip);
        chip = board.getChipByPos(3,0);
        assertNull(chip);
        chip = board.getChipByPos(0,8);
        assertNull(chip);
        chip = board.getChipByPos(-2,-2);
        assertNull(chip); // Сначала все фишки на руке
        chip = board.getChipByPos(-1,-1);
        assertNotNull(chip); // Сначала все фишки на руке
        chip.moveTo(1,1);
        Chip chip1 = board.getChipByPos(1,1);
        assertEquals(chip, chip1);
    }

    @Test
    void moveIsPossible() {
        Chip[] wChips = board.getWhiteChips(); // Сначала все фишки на руке
        Chip[] bChips = board.getBlackChips(); // Сначала все фишки на руке
        board.setDiceNum(1);
        assertTrue(board.moveIsPossible(wChips));
        assertTrue(board.moveIsPossible(bChips));
        wChips[0].moveTo(0,0); // Расставим белые фишки на поле так, чтобы ход нельзя было сделать
        wChips[1].moveTo(1,3);
        wChips[2].moveTo(1,7);
        board.setDiceNum(4);
        assertFalse(board.moveIsPossible(wChips));

        wChips[0].throwOff(); // Сбросим белые фишки с поля
        wChips[1].throwOff();
        wChips[2].throwOff();
        bChips[0].moveTo(2,0); // Расставим черные фишки на поле так, чтобы ход нельзя было сделать
        bChips[1].moveTo(1,3);
        bChips[2].moveTo(1,7);
        board.setDiceNum(4);
        assertFalse(board.moveIsPossible(bChips));

        for (int i=1;i<board.chipsTotalNum;i++) { // Выведем с доски все фишки кроме одной (для каждого цвета)
            wChips[i].moveOut();
            bChips[i].moveOut();
        }
        wChips[0].moveTo(0,6); // Ставим фишку на последнее поле
        bChips[0].moveTo(2,6); // Ставим фишку на последнее поле
        // Если на кубике больше 1, то ход невозможен
        board.setDiceNum(2); //
        assertFalse(board.moveIsPossible(wChips));
        assertFalse(board.moveIsPossible(bChips));
        board.setDiceNum(1);
        assertTrue(board.moveIsPossible(wChips));
        assertTrue(board.moveIsPossible(bChips));
    }

    @Test
    void moveChipOrCheck() {
        Chip[] wChips = board.getWhiteChips(); // Сначала все фишки на руке
        Chip[] bChips = board.getBlackChips(); // Сначала все фишки на руке

        int r=board.moveChipOrCheck(wChips[0],1,true); // Проверяем ход (не перемещаем фишку)
        assertEquals(0,r); // Проверяем, что можно передвинуть
        int row = wChips[0].getRow();
        int col = wChips[0].getCol();
        assertEquals(new Pair(-1,-1), new Pair(row,col)); // Проверяем, что перемещения не произошло
        r=board.moveChipOrCheck(wChips[0],1,false); // Перемещаем фишку
        assertEquals(0,r); // Проверяем, что можно передвинуть
        row = wChips[0].getRow();
        col = wChips[0].getCol();
        assertEquals(new Pair(0,3), new Pair(row,col)); // Проверяем, что перемещение произошло

        r=board.moveChipOrCheck(wChips[1],1,false);
        assertEquals(1, r); // Проверяем, что нельзя передвинуть (клетка занята своей фишкой)

        wChips[0].moveTo(0,0);
        bChips[0].moveTo(1,2);
        r=board.moveChipOrCheck(wChips[0],3,true);
        assertEquals(2, r); // Проверяем, что можно передвинуть, сбросив чужую фишку

        bChips[0].moveTo(1,3);
        r=board.moveChipOrCheck(wChips[0],4,true);
        assertEquals(1, r); // Проверяем, что нельзя передвинуть (клетка занята чужой фишкой, но это "розетка")

        wChips[0].moveTo(0,6); // Ставим фишку на последнюю клетку
        r=board.moveChipOrCheck(wChips[0],1,true);
        assertEquals(3, r); // Проверяем, что можно передвинуть с выходом с доски

        r=board.moveChipOrCheck(wChips[0],2,true);
        assertEquals(-1, r); // Проверяем, что нельзя передвинуть (выход с доски за пределы возможных позиций)

        // Повторяем тесты для черных фишек
        wChips[0].throwOff();
        bChips[0].throwOff();

        r=board.moveChipOrCheck(bChips[0],1,true); // Проверяем ход (не перемещаем фишку)
        assertEquals(0,r); // Проверяем, что можно передвинуть
        row = bChips[0].getRow();
        col = bChips[0].getCol();
        assertEquals(new Pair(-1,-1), new Pair(row,col)); // Проверяем, что перемещения не произошло
        r=board.moveChipOrCheck(bChips[0],1,false); // Перемещаем фишку
        assertEquals(0,r); // Проверяем, что можно передвинуть
        row = bChips[0].getRow();
        col = bChips[0].getCol();
        assertEquals(new Pair(2,3), new Pair(row,col)); // Проверяем, что перемещение произошло

        r=board.moveChipOrCheck(bChips[1],1,false);
        assertEquals(1, r); // Проверяем, что нельзя передвинуть (клетка занята своей фишкой)

        bChips[0].moveTo(2,0);
        wChips[0].moveTo(1,2);
        r=board.moveChipOrCheck(bChips[0],3,true);
        assertEquals(2, r); // Проверяем, что можно передвинуть, сбросив чужую фишку

        wChips[0].moveTo(1,3);
        r=board.moveChipOrCheck(bChips[0],4,true);
        assertEquals(1, r); // Проверяем, что нельзя передвинуть (клетка занята чужой фишкой, но это "розетка")

        bChips[0].moveTo(2,6); // Ставим фишку на последнюю клетку
        r=board.moveChipOrCheck(bChips[0],1,true);
        assertEquals(3, r); // Проверяем, что можно передвинуть с выходом с доски

        r=board.moveChipOrCheck(bChips[0],2,true);
        assertEquals(-1, r); // Проверяем, что нельзя передвинуть (выход с доски за пределы возможных позиций)
    }

}
package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BotTest {
    private UrBoard board = new UrBoard();
    private UrBot robot = new UrBot(board, ChipColor.BLACK, 2);

    @Test
    void getMostProfitableChip() {
        //Проверим, что выбор фишки зависит от ценности каждой конкретной фишки в данной ситуации
        robot.board.setDiceNum(1);
        robot.setDifficulty(2);
        Chip[] wChips = robot.board.getChips(ChipColor.WHITE);
        Chip[] bChips = robot.board.getChips(ChipColor.BLACK);
        wChips[0].moveTo(1,7);
        wChips[1].moveTo(1 ,4);
        wChips[2].moveTo(0,1);
        bChips[0].moveTo(1,6);
        bChips[1].moveTo(1,3);
        bChips[2].moveTo(2,1);
        Chip chip1 = robot.getMostProfitableChip();
        assertEquals(chip1, bChips[0]);
        //Проверим, что при различных уровнях сложности меняется алгоритм выбора фишки
        robot.board = new UrBoard();
        wChips = robot.board.getChips(ChipColor.WHITE);
        bChips = robot.board.getChips(ChipColor.BLACK);
        robot.board.setDiceNum(3);
        wChips[0].moveTo(1, 2);
        for (int chp = 1; chp < wChips.length; chp++) {
            wChips[chp].moveOut();
        }
        bChips[0].moveTo(2, 0);
        bChips[1].moveTo(1, 5);
        robot.setDifficulty(2);
        Chip chip2 = robot.getMostProfitableChip();
        assertEquals(chip2, bChips[0]);
        robot.setDifficulty(3);
        Chip chip3 = robot.getMostProfitableChip();
        assertEquals(chip3, bChips[1]);
        //Проверим, что при разных значениях кубика, робот выбирает различные ходы, основываясь на их ценности
        robot.board = new UrBoard();
        wChips = robot.board.getChips(ChipColor.WHITE);
        bChips = robot.board.getChips(ChipColor.BLACK);
        wChips[0].moveTo(1, 2);
        bChips[0].moveTo(0, 3);
        bChips[1].moveTo(1, 1);
        Chip dChip;
        robot.board.setDiceNum(1);
        dChip = robot.getMostProfitableChip();
        assertEquals(dChip, bChips[1]);
        robot.board.setDiceNum(2);
        dChip = robot.getMostProfitableChip();
        assertEquals(dChip, bChips[1]);
        robot.board.setDiceNum(3);
        dChip = robot.getMostProfitableChip();
        assertEquals(dChip, bChips[0]);
        robot.board.setDiceNum(4);
        dChip = robot.getMostProfitableChip();
        assertEquals(dChip, bChips[2]);
    }

}


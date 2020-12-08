package Model;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.Random;

public class UrBot {
    private  ChipColor color;
    public UrBoard board;
    private int difficulty;

    public UrBot(UrBoard urBoard, ChipColor chipColor, int level) {
        board = urBoard;
        color = chipColor;
        difficulty = level;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    private double getMoveValue(Chip chip) { //ЦФ
        double result = 0.0;
        int curRow = chip.getRow();
        int curCol = chip.getCol();
        Pair<Integer, Integer> nextPos = board.getCellForMoveChip(chip);
        int diceNum = board.getDiceNum();
        int moveResult = board.moveChipOrCheck(chip, diceNum, true);
        int nextRow = nextPos.getKey();
        int nextCol = nextPos.getValue();
        final int[][] positionScore = new int[][] {{7, 4, 4, 4, -1, -1, 7, 6},
                                                   {2, 3, 3, 8, 2, 3, 4, 5},
                                                   {7, 4, 4, 4, -1, -1, 7, 6}};
        final int[] bestKillScore = new int[] {2, 3, 4, 0, 5, 6, 7, 8};
        final int[][] nearEndPositionScore = new int[][] {{0, 0, 0, 0, 0, 0, 1, 1},
                                                          {0, 0, 0, 0, 1, 1, 1, 1},
                                                          {0, 0, 0, 0, 0, 0, 1, 1}};
        final int outOfBoardScore = 8;
        final int centralRosetteRow = 1;
        final int centralRosetteCol = 3;

        switch (difficulty) {
            case 1:
                Random random = new Random();
                result = random.nextDouble()*board.chipsTotalNum;
                break;
            case 2:
                if (nextRow >= 0 && nextCol >= 0) {
                    result = positionScore[nextRow][nextCol] + (moveResult == 2 ? bestKillScore[nextCol] : 0);
                } else result = outOfBoardScore;
                break;
            case 3:
                if (nextRow >=0 && nextCol >= 0) {
                    result = positionScore[nextRow][nextCol] + (moveResult == 2 ? bestKillScore[nextCol] : 0);
                } else result = outOfBoardScore;
                ChipColor enemyColor = color == ChipColor.WHITE ? ChipColor.BLACK : ChipColor.WHITE;
                int delta = board.getChipsOutCnt(enemyColor) - board.getChipsOutCnt(color);
                if (nextRow >= 0 && nextCol >= 0) {
                    result += nearEndPositionScore[nextRow][nextCol] * delta / 2.0;
                }
                if (curCol == centralRosetteCol && curRow == centralRosetteRow) {
                    if (moveResult == 2) {
                        for (int j = 1; j <= Dice.maxDiceNum; j++) {
                            Chip virtualEnemyChip = new Chip(enemyColor);
                            virtualEnemyChip.moveTo(chip.getRow(), chip.getCol());
                            Pair<Integer, Integer> pos = board.getNewPosition(virtualEnemyChip, -j);
                            if (board.getChipByPos(pos.getKey(), pos.getValue()) != null && board.getChipByPos(pos.getKey(), pos.getValue()).getColor() == enemyColor) {
                                result = 0;
                                break;
                            }
                        }
                    } else result = 0;
                }
                break;
        }
        return result;
    }

    public Chip getMostProfitableChip() {
        Chip[] chips = board.getChips(color);
        if (!board.moveIsPossible(chips)) return null;
        double[] profit = new double[chips.length];
        Arrays.fill(profit, 0.0);
        double max = -Double.MAX_VALUE;
        int iMax = -1;
        for (int i = 0; i < chips.length; i++) {
            if (chips[i].isOut()) continue;
            if (board.getCellForMoveChip(chips[i]).getKey() == -1) continue;
            profit[i] = getMoveValue(chips[i]);
            if (max < profit[i]) {
                max = profit[i];
                iMax = i;
            }
        }
        if (iMax < 0) throw new IllegalStateException(); //по идее, никогда не дойдёт
        if (chips[iMax].isOnHand()) {
            return board.getChipFromHand(color);
        }
        else {
            return chips[iMax];
        }
    }
}

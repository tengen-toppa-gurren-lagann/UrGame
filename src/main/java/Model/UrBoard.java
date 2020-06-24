package Model;

import javafx.util.Pair;

import java.util.ArrayList;

public class UrBoard { // Доска для игры в Ур
    public enum CellType {ORDINARY, ROSETTE, NONE} // Тип поля - обычное, розетка или нет поля (вырез в доске)

    private final int width = 8;  // Количество полей по горизонтали
    private final int height = 3; // Количество полей по вертикали
    private CellType[][] grid = new CellType[height][width]; // Поля доски

    public CellType getCell(int row, int col) {
       return grid[row][col];
    }

    public final int chipsTotalNum = 7; // Общее количество фишек каждого цвета
    private Chip[] whiteChips = new Chip[chipsTotalNum];
    private Chip[] blackChips = new Chip[chipsTotalNum];

    public Chip[] getChips(ChipColor color) {
        if (color == ChipColor.BLACK) {
            return blackChips;
        } else return whiteChips;
    }

    public Integer getChipsOnHandCnt(ChipColor color) {
        int n=0;
        if (color == ChipColor.BLACK) {
            for (int i=0; i<chipsTotalNum; i++) {
                if (blackChips[i].isOnHand()) n++;
            }
        } else
            for (int i=0; i<chipsTotalNum; i++) {
                if (whiteChips[i].isOnHand()) n++;
            }
        return n;
    }

    public Chip getChipFromHand(ChipColor color) { // Получить фишку с руки
        if (color == ChipColor.BLACK) {
            for (int i = 0; i < chipsTotalNum; i++) {
                if (blackChips[i].isOnHand()) return blackChips[i];
            }
        } else
            for (int i=0; i<chipsTotalNum; i++) {
                if (whiteChips[i].isOnHand()) return whiteChips[i];
            }
        return null;
    }

    public Integer getChipsOutCnt(ChipColor color) {
        int n = 0;
        if (color == ChipColor.BLACK) {
            for (int i = 0; i < chipsTotalNum; i++) {
                if (blackChips[i].isOut()) n++;
            }
        } else
            for (int i = 0; i < chipsTotalNum; i++) {
                if (whiteChips[i].isOut()) n++;
            }
        return n;
    }

    public Pair<Integer,Integer> getCellForMoveChip(Chip chip) {
        if (chip==null) return new Pair(-1,-1);
        int i=moveChipOrCheck(chip, diceNum, true);
        if (i==0 || i==2 || i==3) { // Ход возможен
            return getNewPosition(chip, diceNum);
        }
        else {
            return new Pair<>(-1,-1);
        }
    }

    public boolean moveIsPossible(Chip[] chips) { // Проверка возможности хода
        for (int i=0; i<chipsTotalNum; i++) { // Проходим по фишкам
            if (!chips[i].isOut()) { // Проверяем только не вышедшие фишки на возможность ими походить
                if (getCellForMoveChip(chips[i]).getKey()!=-1 && getCellForMoveChip(chips[i]).getValue()!=-1) return true;
            }
        }
        return false;
    }

    public Chip getChipByPos(int row, int col) {
        for (int i=0; i<chipsTotalNum; i++) {
           if (whiteChips[i].getCol()==col && whiteChips[i].getRow()==row) {
               return whiteChips[i];
           }
            if (blackChips[i].getCol()==col && blackChips[i].getRow()==row) {
                return blackChips[i];
            }
        }
        return null;
    }

    private int diceNum = 0;
    public int getDiceNum() {
        return diceNum;
    }
    public void setDiceNum(int num) {
        diceNum = num;
    }

    private ArrayList<Pair<Integer, Integer>> whitesRoute = new ArrayList<>(); // Путь белых фишек
    private ArrayList<Pair<Integer, Integer>> blacksRoute = new ArrayList<>(); // Путь черных фишек

    public UrBoard() {
        // Инициализируем поля доски
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = CellType.ORDINARY;
            }
        }
        grid[0][0] = grid[0][6] = grid[1][3] = grid[2][0] = grid[2][6] = CellType.ROSETTE; // Поля-розетки
        grid[0][4] = grid[0][5] = grid[2][4] = grid[2][5] = CellType.NONE; // Поля, которых нет (вырезы на доске)

        // Заполняем "путь белых"
        whitesRoute.add(new Pair<>(-1, -1));
        whitesRoute.add(new Pair<>(0, 3));
        whitesRoute.add(new Pair<>(0, 2));
        whitesRoute.add(new Pair<>(0, 1));
        whitesRoute.add(new Pair<>(0, 0));
        whitesRoute.add(new Pair<>(1, 0));
        whitesRoute.add(new Pair<>(1, 1));
        whitesRoute.add(new Pair<>(1, 2));
        whitesRoute.add(new Pair<>(1, 3));
        whitesRoute.add(new Pair<>(1, 4));
        whitesRoute.add(new Pair<>(1, 5));
        whitesRoute.add(new Pair<>(1, 6));
        whitesRoute.add(new Pair<>(1, 7));
        whitesRoute.add(new Pair<>(0, 7));
        whitesRoute.add(new Pair<>(0, 6));
        whitesRoute.add(new Pair<>(-2, -2));

        // Заполняем "путь черных"
        blacksRoute.add(new Pair<>(-1, -1));
        blacksRoute.add(new Pair<>(2, 3));
        blacksRoute.add(new Pair<>(2, 2));
        blacksRoute.add(new Pair<>(2, 1));
        blacksRoute.add(new Pair<>(2, 0));
        blacksRoute.add(new Pair<>(1, 0));
        blacksRoute.add(new Pair<>(1, 1));
        blacksRoute.add(new Pair<>(1, 2));
        blacksRoute.add(new Pair<>(1, 3));
        blacksRoute.add(new Pair<>(1, 4));
        blacksRoute.add(new Pair<>(1, 5));
        blacksRoute.add(new Pair<>(1, 6));
        blacksRoute.add(new Pair<>(1, 7));
        blacksRoute.add(new Pair<>(2, 7));
        blacksRoute.add(new Pair<>(2, 6));
        blacksRoute.add(new Pair<>(-2, -2));

        // Инициализируем фишки
        for (int i = 0; i < chipsTotalNum; i++) {
            whiteChips[i] = new Chip(ChipColor.WHITE);
            blackChips[i] = new Chip(ChipColor.BLACK);
        }
//        whiteChips[0].moveTo(0,6); // Для проверки отрисовки
//        blackChips[0].moveTo(2,7); // Для проверки отрисовки
//        moveChipOrCheck(whiteChips[1],7, false); // Для проверки перемещения фишки
//        moveChipOrCheck(blackChips[1],2, false); // Для проверки перемещения фишки
    }

    private Pair<Integer,Integer> getNewPosition(Chip chip, int n) { // Получить новую позицию при перемещении фишки на n клеток
        ArrayList<Pair<Integer, Integer>> route;
        if (chip.getColor() == ChipColor.WHITE) {
            route = whitesRoute;
        } else {
            route = blacksRoute;
        }
        Pair<Integer,Integer> pos = new Pair(chip.getRow(), chip.getCol());
        try {
            int i = route.indexOf(pos); // Получаем индекс текущей позиции фишки из траектории движения
            int newRow = route.get(i + n).getKey();   // Получаем новую позицию из траектории
            int newCol = route.get(i + n).getValue(); //  движения при перемещении фишки на n клеток
            return new Pair(newRow, newCol);
        }
        catch (IndexOutOfBoundsException e) {
            // Нельзя передвинуть, т.к. выйдет за пределы возможных позиций
            return new Pair(-1, -1);
        }
    }

    public int moveChipOrCheck(Chip chip, int n, boolean justCheck) {   // Передвинуть фишку на n клеток или проверить, можно ли передвинуть
                                                                        // Возврат: 0-можно передвинуть; 1-нельзя передвинуть, т.к. поле занято фишкой своего цвета; 2-можно передвинуть, сбросив фишку противника; 3-можно передвинуть, выйдет с доски; -1-нельзя передвинуть, т.к. выйдет за пределы возможных позиций
        Chip[] chips, oppositeChips;
        if (chip.getColor() == ChipColor.WHITE) {
            chips = whiteChips;
            oppositeChips = blackChips;
        } else {
            chips = blackChips;
            oppositeChips = whiteChips;
        }
        Pair<Integer,Integer> newPos = getNewPosition(chip, n);   // Получаем новую позицию при перемещении фишки на n клеток
        int newRow = newPos.getKey();
        int newCol = newPos.getValue();
        // Проверяем, можно ли передвинуть фишку
        if (newRow==-1 && newCol==-1) {
            return -1; // Нельзя -> выйдет за пределы
        }
        // Проверяем, не выходит ли фишка из игры
        if (newRow==-2 && newCol==-2) {
            if (!justCheck) {
                chip.moveOut();
            }
            return 3;
        }
        // Проверяем, нет ли на новой клетке другой фишки
        for (int j = 0; j < chipsTotalNum; j++) {
            if (chips[j].getRow() == newRow && chips[j].getCol() == newCol) {
                // Поле занято фишкой того же цвета, передвинуть нельзя
                return 1;
            }
            if (oppositeChips[j].getRow() == newRow && oppositeChips[j].getCol() == newCol) { // Поле занято фишкой другого цвета
                if (getCell(newRow,newCol) == CellType.ROSETTE) { // Чужая фишка стоит на "розетке" -> сбросить нельзя
                    return 1;
                }
                else {
                    if (!justCheck) {
                        oppositeChips[j].throwOff();
                        chip.moveTo(newRow, newCol);
                    }
                    return 2;
                }
            }
        }
        if (!justCheck) {
            chip.moveTo(newRow, newCol); // Новое поле пусто - переходим на него
        }
        return 0;
    }
}

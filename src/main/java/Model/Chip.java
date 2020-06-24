package Model;

public class Chip { // Фишка

    private ChipColor color; // Цвет фишки
    private int col =-1; // Позиция на доске по горизонтали (-1->на руке, -2->вышла)
    private int row =-1; // Позиция на доске по вертикали (-1->на руке, -2->вышла )
    private boolean isOnBoard=false; // Находится на доске (м.б. потом уберем за не надобностью, т.к. если не на доске, то позиция (-1,-1)
    private boolean isOut=false; // Вышла из игры (м.б. потом уберем за не надобностью, т.к. если не на доске, то позиция (-2,-2)

    public Chip(ChipColor initColor) {
        color = initColor;
    }
    public  ChipColor getColor() { return color; }

    void moveTo(int newRow, int newCol) {
        if (newRow==-1 && newCol==-1) {
            throwOff();
        }
        else {
            if (newRow == -2 && newCol == -2) {
                moveOut();
            }
            else {
                row = newRow;
                col = newCol;
                isOnBoard = true;
                isOut = false;
            }
        }

    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public boolean isOut() { return isOut; }
    public boolean isOnHand() { return (!isOnBoard && !isOut); }

    public void moveOut() { // Вывод с доски (дошла до конца)
        col =-2;
        row =-2;
        isOnBoard = false;
        isOut=true;
    }

    public void throwOff() { // Удаление с доски (побита врагом)
        col =-1;
        row =-1;
        isOnBoard = false;
        isOut=false;
    }

}

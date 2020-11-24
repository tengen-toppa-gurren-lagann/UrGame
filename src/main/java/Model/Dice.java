package Model;

import java.util.Random;

public class Dice {
    public static final int maxDiceNum = 4;
    private Random random;
    public Dice() {
        random = new Random();
    }
    public int diceRoll() {
        return random.nextInt(maxDiceNum + 1); // Бросаем не кубик, а четыре тетраэдра с мечеными углами -> результат от 0 до 4 включительно
    }
}

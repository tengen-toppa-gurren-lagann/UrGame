package Model;

import java.util.Random;

public class Dice {
    private Random random;
    public Dice() {
        random = new Random();
    }
    public int diceRoll() {
        return random.nextInt(5); // Бросаем не кубик, а четыре тетраэдра с мечеными углами -> результат от 0 до 4 включительно
    }
}

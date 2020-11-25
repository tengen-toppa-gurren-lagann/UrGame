package Controller;

import Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BoardController {

    @FXML private Pane boardPane;
    @FXML private Button rollDiceButton;
    @FXML private Label hintString;
    @FXML private Label turnLabel;
    @FXML private Label winnerLabel;
    @FXML private Text difficultyTxt;
    @FXML private Text winnerTxt;
    @FXML private Label whiteChipsOnHandCnt;
    @FXML private Label blackChipsOnHandCnt;
    @FXML private Label whiteChipsOutCnt;
    @FXML private Label blackChipsOutCnt;
    @FXML private Button whiteChipOutBtn;
    @FXML private Button blackChipOutBtn;
    @FXML private Label diceNum;
    @FXML private Label difficultyLabel;
    @FXML private Label blackPlayerName;

    private UrBoard board = new UrBoard();
    private GridPane grid = new GridPane();
    private static Canvas[][] cellCanvas = new Canvas[UrBoard.height][UrBoard.width];
    private static int clickedCellRow;
    private static int clickedCellCol;
    private Chip clickedChip = null;

    private final int cellSize = 76; // Размер клетки (высота и ширина)

    private Dice dice = new Dice(); // "Кубик"
    private final String diceInitStr = "Roll Dices";

    private int gameMode; // Режим игры: 1 - однопользовательский режим, 2 - режим игры на двоих
    public void setGameMode(int mode) {
        gameMode = mode;
    }

    private Stage boardStage;
    private int difficulty;
    public void setBoardStage(Stage stage) {
        boardStage = stage;
    }
    public void setDifficulty(int level) { difficulty = level;}

    private UrBot bot;

    private final String noMoveWavFilename = "/no_move.wav";
    private final String applauseWavFilename = "/applause.wav";
    private final String[] blackPlayerStr = new String[]{"Robot", "Human"};
    private final String[] difficultyStr = new String[]{"Easy", "Basic", "Advanced"};


    // Текущая фаза игры:
    private int curPhase = 0;   // 0 - Ход белых, бросок кубика
    // 1 - Ход белых, выбор фишки
    // 2 - Ход белых, выбор поля
    // 3 - Ход белых, поле выбрано: фишка перемещена, ход сделан (промежуточная фаза)
    // 4 - Ход черных, бросок кубика
    // 5 - Ход черных, выбор фишки
    // 6 - Ход черных, выбор поля
    // 7 - Ход черных, поле выбрано: фишка перемещена, ход сделан (промежуточная фаза)
    // 8 - Ход белых, но ходить некуда -> переход хода к черным
    // 9 - Ход черных, но ходить некуда -> переход хода к белым
    // 10 - Конец игры

    private String[] hint = {"Whites turn: Roll the dices",
            "Whites turn: Click the Chip to move",
            "Whites turn: Click board cell to make move or click another Chip to move",
            "Whites: Turn made",
            "Blacks turn: Roll the dices",
            "Blacks turn: Click the Chip to move",
            "Blacks turn: Click board cell to make move or click another Chip to move",
            "Blacks: Turn made",
            "Whites: No moves available. Turn is passing to Blacks, press 'Dices' button please",
            "Blacks: No moves available. Turn is passing to Whites, press 'Dices' button please",
            "Game over! Press 'Exit' button",
            "Your turn: Roll the dices",
            "Your turn: Click the Chip to move",
            "Your turn: Click board cell to make move or click another Chip to move",
            "Your turn made",
            "UrBot's turn: Roll the dices",
            "UrBot's turn: Click the Chip to move",
            "UrBot's turn: Click board cell to make move or click another Chip to move",
            "UrBot's turn made",
            "You have no moves available. Turn is passing to UrBot, press 'Dices' button please",
            "UrBot has no moves available. Turn is passing to you, press 'Dices' button please",
            "Game over."
            };
    private final String[] colorStr = {"Whites", "Blacks"};

    private void addPane(Canvas c, int col, int row) { // Добавить в клетку холст вместе с обработчиком мыши
       c.setOnMouseClicked(event -> {
           clickedCellRow = row;
           clickedCellCol = col;
           onClickOnBoard(row, col);
       });
       grid.add(c, col, row);
    }
    private void fillBoardCells() { // Заполняем доску клетками
        for (int i = 0; i < UrBoard.height; i++) {
            for (int j = 0; j < UrBoard.width; j++) {
                cellCanvas[i][j] = new Canvas(cellSize, cellSize);
                addPane(cellCanvas[i][j], j, i);
            }
        }
        grid.setGridLinesVisible(false);
    }

    public void initBoard() { // Инициализация доски
        fillBoardCells();
        boardPane.getChildren().add(grid);
        blackPlayerName.setText(blackPlayerStr[gameMode - 1]);
        difficultyLabel.setText(difficultyStr[difficulty - 1]);
        curPhase=0;
        if (gameMode == 1) {
            bot = new UrBot(board, ChipColor.BLACK, difficulty);
            Timeline tl = new Timeline(new KeyFrame(Duration.millis(1250), e -> onTimer()));
            tl.setCycleCount(Timeline.INDEFINITE);
            tl.play();
        }
        else {
            difficultyTxt.setVisible(false);
            difficultyLabel.setVisible(false);
        }
        winnerTxt.setVisible(false);
        drawBoard();
        if (gameMode == 2) {
            hintString.setText(hint[curPhase]);
        } else hintString.setText(hint[curPhase + 11]);
        winnerLabel.setText("");
    }

    public void drawBoard() { // Отрисовка доски
        whiteChipsOnHandCnt.setText(board.getChipsOnHandCnt(ChipColor.WHITE).toString());
        blackChipsOnHandCnt.setText(board.getChipsOnHandCnt(ChipColor.BLACK).toString());
        whiteChipsOutCnt.setText(board.getChipsOutCnt(ChipColor.WHITE).toString());
        blackChipsOutCnt.setText(board.getChipsOutCnt(ChipColor.BLACK).toString());
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i < UrBoard.height; i++) {
            for (int j = 0; j < UrBoard.width; j++) {
                GraphicsContext gc = cellCanvas[i][j].getGraphicsContext2D();
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(3);
                gc.strokeRect(x, y, cellSize, cellSize);
                gc.setFill(Color.BURLYWOOD);
                gc.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                double bgX = 5;
                double bgY = 5;
                double bgSize = cellSize - 10;
                if (board.getCell(i, j) == UrBoard.CellType.ORDINARY) { // Загружаем изображения полей доски
                    if (((i == 0) || (i == 2)) && ((j == 1) || (j == 3)) || ((i == 1) && (j == 6))) {
                        Image bgImage = new Image(getClass().getResourceAsStream("/eyes.png"));
                        gc.drawImage(bgImage, bgX, bgY, bgSize, bgSize);
                    } else if ((i == 0 && j == 2) || (i == 1 && (j == 1 || j == 4 || j == 7) || (i == 2 && j == 2))) {
                        Image bgImage = new Image(getClass().getResourceAsStream("/fivedots.png"));
                        gc.drawImage(bgImage, bgX, bgY, bgSize, bgSize);
                    } else if (i == 1 && j == 0) {
                        Image bgImage = new Image(getClass().getResourceAsStream("/manydots.png"));
                        gc.drawImage(bgImage, bgX, bgY, bgSize, bgSize);
                    } else if (i == 1 && (j == 2 || j == 5)) {
                        Image bgImage = new Image(getClass().getResourceAsStream("/manyfivedots.png"));
                        gc.drawImage(bgImage, bgX, bgY, bgSize, bgSize);
                    } else if ((j == 7) && (i == 0 || i == 2)) {
                        Image bgImage = new Image(getClass().getResourceAsStream("/fivedotsinframe.png"));
                        gc.drawImage(bgImage, bgX, bgY, bgSize, bgSize);
                    }
                } else if (board.getCell(i, j) == UrBoard.CellType.ROSETTE) {
                    Image bgImage = new Image(getClass().getResourceAsStream("/rosette.png"));
                    gc.drawImage(bgImage, bgX, bgY, bgSize, bgSize);
                } else if (board.getCell(i, j) == UrBoard.CellType.NONE) {
                    gc.setStroke(Color.LIGHTGRAY);
                    gc.setFill(Color.LIGHTGRAY);
                    gc.setLineWidth(2);
                    gc.strokeRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                    gc.fillRect(x + 1, y + 1, cellSize - 3, cellSize - 3);
                }
                // Если в клетке есть фишка - рисуем ее
                Chip chip=board.getChipByPos(i,j);
                if (chip!=null) {
                    gc.setLineWidth(4);
                    if (chip.getColor()== ChipColor.WHITE) {
                        gc.setStroke(Color.BLACK);
                        gc.setFill(Color.WHITE);
                    }
                    else {
                        gc.setStroke(Color.WHITE);
                        gc.setFill(Color.BLACK);
                    }
                    double diam = cellSize*0.6;
                    double xy = cellSize/2-diam/2;
                    gc.strokeOval(xy,xy,diam,diam);
                    gc.fillOval(xy,xy,diam,diam);
                }
                // Если надо пометить поле, доступное для хода, то помечаем желтым кружочком
                try {
                    int markCellRow = board.getCellForMoveChip(clickedChip).getKey();
                    int markCellCol = board.getCellForMoveChip(clickedChip).getValue();
                    if (markCellRow==i && markCellCol==j) {
                        gc.setLineWidth(1);
                        gc.setStroke(Color.BLACK);
                        gc.setFill(Color.YELLOW);
                        double diam = cellSize * 0.3;
                        double xy = cellSize / 2 - diam / 2;
                        gc.strokeOval(xy, xy, diam, diam);
                        gc.fillOval(xy, xy, diam, diam);
                    }
                    if (markCellRow==-2 && markCellCol==-2) { // Фишка выходит с поля - помечаем кнопку вышедших с доски
                        Image img;
                        if (clickedChip.getColor()==ChipColor.WHITE) {
                            img = new Image(getClass().getResourceAsStream("/chip_white_yellow_dot.png"));
                            whiteChipOutBtn.graphicProperty().setValue(new ImageView(img));
                        }
                        else {
                            img = new Image(getClass().getResourceAsStream("/chip_black_yellow_dot.png"));
                            blackChipOutBtn.graphicProperty().setValue(new ImageView(img));
                        }
                    }
                    else {
                        Image img = new Image(getClass().getResourceAsStream("/chip_white.png"));
                        whiteChipOutBtn.graphicProperty().setValue(new ImageView(img));
                        img = new Image(getClass().getResourceAsStream("/chip_black.png"));
                        blackChipOutBtn.graphicProperty().setValue(new ImageView(img));
                    }
                }
                catch (NullPointerException e) {
                    // Нет поля для пометки
                }
            }
        }
    }

    private void onClickOnBoard(int row, int col) { // Обработка нажатия мыши на доске
        if ((row==0 || row==2) && (col==4 || col==5) ) return; // При нажатиях на вырезе доски ничего не делаем ни на каких этапах
        int phase = curPhase;
        if (phase!=1 && phase!=2 && phase !=5 && phase !=6) return; // На текущем этапе не ожидаем клика по доске
        if (gameMode == 1 && phase != 1 && phase != 2) return;

        ChipColor color=ChipColor.WHITE;
        ChipColor oppositeColor=ChipColor.BLACK;
        if (phase==5 || phase==6) {
            color=ChipColor.BLACK;
            oppositeColor=ChipColor.WHITE;
        }
        if (phase==1 || phase==5) { // Выбор фишки
            Chip chip = board.getChipByPos(row, col); // Выбрана фишка или поле
            if (chip!=null) { // Выбрана фишка
                if (chip.getColor() == oppositeColor) { // Выбрана фишка не того цвета
                    clickedChip = null;
                    return;
                } else {
                    clickedChip = board.getChipByPos(row, col);
                }
            }
        }
        if (phase==2 || phase==6) { // Выбор поля или смена фишки
            Chip chip = board.getChipByPos(row, col); // Выбрана фишка
            if (chip!=null) {
                if (chip.getColor() == color) { // Выбрана другая фишка того же цвета
                    clickedChip = board.getChipByPos(row, col);
                }
            }
            else { // Клик не на фишке, а на пустом поле или фишке другого цвета
                clickedCellRow = row;
                clickedCellCol = col;
            }
        }
        processGame();
    }

    public void onWhiteChipOnHandBtn(ActionEvent event) {
        if (curPhase == 1 || curPhase == 2) {
            clickedChip = board.getChipFromHand(ChipColor.WHITE);
            if (clickedChip == null) return; // Не осталось фишек на руке)
            processGame();
        }
    }

    public void onBlackChipOnHandBtn(ActionEvent event) {
        if (gameMode == 1) return;
        if (curPhase == 5 || curPhase == 6) {
            clickedChip = board.getChipFromHand(ChipColor.BLACK);
            if (clickedChip == null) return; // Не осталось фишек на руке)
            processGame();
        }
    }

    public void onWhiteChipOutBtn(ActionEvent event) {
        if (curPhase == 2) {
            clickedCellRow = -2;
            clickedCellCol = -2;
            processGame();
        }
    }

    public void onBlackChipOutBtn(ActionEvent event) {
        if (gameMode == 1) return;
        if (curPhase == 6) {
            clickedCellRow = -2;
            clickedCellCol = -2;
            processGame();
        }
    }

    public void onExitGameButton(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void onRollDiceButton(ActionEvent event) {
        if (curPhase == 0 || curPhase == 4) { // Кости бросаем только на нужном этапе
            int num = dice.diceRoll();
            board.setDiceNum(num);
  //          rollDiceButton.setText("Dices: " + num);
            diceNum.setText("" + num);
            processGame();
        }
        else {
            if (curPhase == 8 || curPhase == 9) { // Переход хода (для продолжения надо нажать на кнопку броска кубика)
                rollDiceButton.setText(diceInitStr);
                processGame();
            }
        }
    }

    private void playSound(String soundFileName) {
        try {
            InputStream is = getClass().getResourceAsStream(soundFileName);
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        }
        catch (Exception e) {
            //Empty
        }
    }

    private void processGame() { // Ведение, смена и обработка этапов игры
        int phase=curPhase;
        int cellToMakeMoveRow = -1, cellToMakeMoveCol = -1;
        switch (phase) {
            case 0:  // Белые бросили кубик
                if (board.getDiceNum()==0) { // Выброшен 0 -> переход хода
                    playSound(noMoveWavFilename);
                    curPhase=8;
                }
                else {
                    if (board.moveIsPossible(board.getChips(ChipColor.WHITE))) { // Ходы есть
                        curPhase=1;
                    }
                    else {
                        playSound(noMoveWavFilename);
                        curPhase=8; // Ходов нет -> переход хода
                    }
                }
                break;
            case 1: // Выбрана белая фишка, ждем выбора поля
                drawBoard();
                if (clickedChip!=null) { // Кликнута именно фишка, а не поле
                    curPhase=2;
                }
                break;
            case 2:  // Выбрано поле для хода белой фишки
                drawBoard();
                // Обрабатываем ход
                boolean moveMade=false;
                cellToMakeMoveRow = board.getCellForMoveChip(clickedChip).getKey();
                cellToMakeMoveCol = board.getCellForMoveChip(clickedChip).getValue();
                if ((cellToMakeMoveRow == clickedCellRow && cellToMakeMoveCol == clickedCellCol)) {
                    board.moveChipOrCheck(clickedChip, board.getDiceNum(), false);
                    moveMade=true;
                }
                if (moveMade) { // Ход сделан
                    rollDiceButton.setText(diceInitStr);
                    diceNum.setText("");
                    try {
                        if (board.getCell(cellToMakeMoveRow,cellToMakeMoveCol) == UrBoard.CellType.ROSETTE) { // Перешли на поле-розетку, поэтому нет перехода хода
                            curPhase=0;
                        }
                        else {
                            curPhase=4;
                        }
                    }
                    catch (IndexOutOfBoundsException e) { // Фишка вышла с доски -> переход хода
                        curPhase=4;
                    }
                    clickedChip=null;
                    drawBoard();
                }
                break;
            case 3: // Промежуточные стадии
            case 7:
                break;
            case 4: // Черные бросили кубик
                if (board.getDiceNum()==0) { // Выброшен 0 -> переход хода
                    playSound(noMoveWavFilename);
                    curPhase=9;
                }
                else {
                    if (board.moveIsPossible(board.getChips(ChipColor.BLACK))) { // Ходы есть
                        curPhase=5;
                    }
                    else {
                        playSound(noMoveWavFilename);
                        curPhase=9; // Ходов нет -> переход хода
                    }
                }
                break;
            case 5: // Выбрана черная фишка, ждем выбора поля
                if (gameMode == 1) clickedChip = bot.getMostProfitableChip();
                drawBoard();
                if (clickedChip!=null) { // Кликнута именно фишка, а не поле
                    curPhase=6;
                }
                break;
            case 6: // Выбрано поле для хода черной фишки
                drawBoard();
                // Обрабатываем ход
                moveMade=false;
                cellToMakeMoveRow = board.getCellForMoveChip(clickedChip).getKey();
                cellToMakeMoveCol = board.getCellForMoveChip(clickedChip).getValue();
                if (gameMode == 1 || (cellToMakeMoveRow == clickedCellRow && cellToMakeMoveCol == clickedCellCol)) { // Ты робот, либо правильно походил
                    board.moveChipOrCheck(clickedChip, board.getDiceNum(), false);
                    moveMade = true;
                }
                if (moveMade) { // Ход сделан
                    rollDiceButton.setText(diceInitStr);
                    diceNum.setText("");
                    try {
                        if (board.getCell(cellToMakeMoveRow,cellToMakeMoveCol) == UrBoard.CellType.ROSETTE) { // Перешли на поле-розетку, поэтому нет перехода хода
                            curPhase=4;
                        }
                        else {
                            curPhase=0;
                        }
                    }
                    catch (IndexOutOfBoundsException e) { // Фишка вышла с доски -> переход хода
                        curPhase=0;
                    }
                    clickedChip=null;
                    drawBoard();
                }
                break;
            case 8: // Белым некуда ходить
                curPhase=4;
                break;
            case 9: // Черным некуда ходить
                curPhase=0;
                break;
            case 10: // Игра окончена
                break;
        }
        if (gameMode == 2) {
            hintString.setText(hint[curPhase]);
        } else hintString.setText(hint[curPhase + 11]);
        if (curPhase>=0 && curPhase<4) turnLabel.setText(colorStr[0]);  // Выводим очередность хода
        if (curPhase>=4 && curPhase<8 ) turnLabel.setText(colorStr[1]); // Выводим очередность хода
        if (board.getChipsOutCnt(ChipColor.WHITE)==board.chipsTotalNum) { // Белые выиграли
            curPhase=10;
            if (gameMode == 2) {
                hintString.setText(hint[curPhase]);
            } else hintString.setText(hint[curPhase + 11]);
            winnerTxt.setVisible(true);
            winnerLabel.setText("Whites!");
            playSound(applauseWavFilename);
        }
        else if (board.getChipsOutCnt(ChipColor.BLACK)==board.chipsTotalNum) { // Черные выиграли
            curPhase=10;
            if (gameMode == 2) {
                hintString.setText(hint[curPhase]);
            } else hintString.setText(hint[curPhase + 11]);
            winnerTxt.setVisible(true);
            winnerLabel.setText("Blacks!");
            playSound(applauseWavFilename);
        }
    }

    void onTimer() {
        if (curPhase == 5 || curPhase == 6) {
            processGame();
        }
        if (curPhase == 4) {
            onRollDiceButton(null);
        }
        if (curPhase == 8) {
            processGame();
        }
        if (curPhase == 9) {
            onRollDiceButton(null);
        }
    }
}
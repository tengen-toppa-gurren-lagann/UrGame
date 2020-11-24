package Controller;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import static javafx.stage.Modality.APPLICATION_MODAL;

public class StartController {

    @FXML private ToggleGroup gameModeGroup;
    @FXML private ToggleGroup difficultyGroup;
    @FXML private RadioButton easyDifficulty;
    @FXML private RadioButton basicDifficulty;
    @FXML private RadioButton advancedDifficulty;

    private final String linkToVideoStr="https://www.youtube.com/watch?v=WZskjLq040I";

    private Stage mainStage;
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    private int gameMode = 1; // 1 - однопользовательский режим, 2 - режим игры на двоих
    private int difficulty = 2; // Уровень сложности: 1 - Easy, 2 - Basic, 3 - Advanced

    public void onOnePlayerButton(ActionEvent event) {
        gameMode = 1;
        easyDifficulty.setDisable(false);
        basicDifficulty.setDisable(false);
        advancedDifficulty.setDisable(false);
    }
    public void onTwoPlayersButton(ActionEvent event) {
        gameMode = 2;
        easyDifficulty.setDisable(true);
        basicDifficulty.setDisable(true);
        advancedDifficulty.setDisable(true);
    }
    public void onEasyButton(ActionEvent event) { difficulty = 1; }
    public void onBasicButton(ActionEvent event) { difficulty = 2; }
    public void onAdvancedButton(ActionEvent event) { difficulty = 3; }
    public void onStartButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/board.fxml"));
        Parent root = loader.load();
        BoardController controller = loader.getController();
        controller.setGameMode(gameMode);
        controller.setDifficulty(difficulty);
        Stage boardStage = new Stage();
        boardStage.setTitle("Game Board");
        boardStage.setResizable(false);
        boardStage.setScene(new Scene(root));
        boardStage.initModality(APPLICATION_MODAL);
        controller.setBoardStage(boardStage);
        controller.initBoard();
        boardStage.show();
    }

    public void onRulesButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/rules.fxml"));
        Stage rulesStage = new Stage();
        rulesStage.setTitle("Rules of the Game");
        rulesStage.setResizable(false);
        rulesStage.setScene(new Scene(root));
        rulesStage.initModality(APPLICATION_MODAL);
        rulesStage.show();
    }

    public void onRulesOkButton(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void onExitButton(ActionEvent event) {
        mainStage.close();
    }

    public void onLinkToVideo(ActionEvent event) {
        Application app = new Application() {
            @Override
            public void start(Stage stage) {
            }
        };
        HostServices services = app.getHostServices();
        services.showDocument(linkToVideoStr);
    }


}

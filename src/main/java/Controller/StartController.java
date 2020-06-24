package Controller;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import static javafx.stage.Modality.APPLICATION_MODAL;

public class StartController {
    private String linkToVideoStr="https://www.youtube.com/watch?v=WZskjLq040I";

    private Stage mainStage;
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void onStartButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/board.fxml"));
        Parent root = loader.load();
        BoardController controller = loader.getController();
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

package main.scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Main;
import main.network.GameServer;

public class LobbyScene {
    private static Label playerCountLabel;
    private static Timeline updatePlayerCountTimeline;
    private static Scene lobbyScene;

    public LobbyScene(Stage primaryStage) {
        StackPane rootLayout = new StackPane();
        VBox lobbyLayout = new VBox();
        lobbyLayout.setAlignment(Pos.CENTER);
        // Set the background image
        Rectangle bg = new Rectangle(1280, 720);
        Image bg_img = new Image("assets/lobby.png");
        bg.setFill(new ImagePattern(bg_img));
        rootLayout.getChildren().add(bg);

        playerCountLabel = new Label();
        lobbyLayout.getChildren().add(playerCountLabel);

        rootLayout.getChildren().add(lobbyLayout);

        Button buttonStart = new Button("START");
        lobbyLayout.getChildren().add(buttonStart);
        buttonStart.setOnAction(event -> primaryStage.setScene(Main.getGameplayScene()));

        lobbyScene = new Scene(rootLayout, 1280, 720);

        updatePlayerCountTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updatePlayerCount()));
        updatePlayerCountTimeline.setCycleCount(Timeline.INDEFINITE);
        updatePlayerCountTimeline.play();
    }

    private static void updatePlayerCount() {
        int playerCount = GameServer.getConnectedPlayerCount();
        playerCountLabel.setText("Connected players: " + playerCount);
    }

    public static Scene getScene() {
        return lobbyScene;
    }
}
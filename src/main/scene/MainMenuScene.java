package main.scene;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;

import main.Main;
import main.network.GameServer;
import main.network.ChatServer;

public class MainMenuScene {
    private final Scene mainMenuScene;
    private Label playerCountLabel;
    private Timeline updatePlayerCountTimeline;

    public MainMenuScene(Stage primaryStage) {
        StackPane mainMenuLayout = new StackPane();
        Rectangle bg = new Rectangle(1280, 720);
        Image bg_img = new Image("assets/BINI.png");
        bg.setFill(new ImagePattern(bg_img));

        Button buttonPlay = new Button("PLAY");
        Button runServer = new Button("RUN SERVER");
        Button connectToServer = new Button("CONNECT TO SERVER");
        buttonPlay.setOnAction(event -> primaryStage.setScene(Main.getLobbyScene()));
        runServer.setOnAction(event -> runServer());
        connectToServer.setOnAction(event -> connectToServer());


        mainMenuLayout.getChildren().addAll(bg, buttonPlay, runServer, connectToServer);
        StackPane.setMargin(buttonPlay, new Insets(0, 0, 50, 0));
        StackPane.setMargin(runServer, new Insets(50, 0, 50, 0));
        StackPane.setMargin(connectToServer, new Insets(50, 0, 0, 0));

        // playerCountLabel = new Label();
        // mainMenuLayout.getChildren().add(playerCountLabel);

        this.mainMenuScene = new Scene(mainMenuLayout, 1280, 720);

        // updatePlayerCountTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updatePlayerCount()));
        // updatePlayerCountTimeline.setCycleCount(Timeline.INDEFINITE);
        // updatePlayerCountTimeline.play();
    }

    public Scene getScene() {
        return mainMenuScene;
    }

    private void runServer() {
    	  showAlert(AlertType.INFORMATION, "Server Status", "Server starting.");
    	  new Thread(() -> {
    	    try {
    	      ChatServer.main(null);
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    	  }).start();
    	  new Thread(() -> {
    	    try {
    	      GameServer.main(null);
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    	  }).start();
    	}

    private void connectToServer() {
    	TextInputDialog addressDialog = new TextInputDialog("localhost");
        addressDialog.setTitle("Server Address");
        addressDialog.setHeaderText("Enter the server address:");
        addressDialog.setContentText("Address:");

        Optional<String> addressResult = addressDialog.showAndWait();

        if (addressResult.isPresent()) {
        	Main.connectToServer(addressResult.get());
        }

    }

    private static void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePlayerCount() {
      int playerCount = GameServer.getConnectedPlayerCount();
      playerCountLabel.setText("Connected players: " + playerCount);
    }
}

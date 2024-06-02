package main.scene;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;

import main.Main;
import main.network.GameServer;

public class MainMenuScene {
    private final Scene mainMenuScene;
    
    public MainMenuScene(Stage primaryStage) {
        StackPane mainMenuLayout = new StackPane();
        Rectangle bg = new Rectangle(1280, 720);
        Image bg_img = new Image("assets/BINI.png");
        bg.setFill(new ImagePattern(bg_img));

        Button buttonPlay = new Button("PLAY");
        Button runServer = new Button("RUN SERVER");
        Button connectToServer = new Button("CONNECT TO SERVER");
        buttonPlay.setOnAction(event -> primaryStage.setScene(Main.getGameplayScene()));
        runServer.setOnAction(event -> runServer());
        connectToServer.setOnAction(event -> connectToServer());


        mainMenuLayout.getChildren().addAll(bg, buttonPlay, runServer, connectToServer);
        StackPane.setMargin(buttonPlay, new Insets(0, 0, 50, 0));
        StackPane.setMargin(runServer, new Insets(50, 0, 50, 0));
        StackPane.setMargin(connectToServer, new Insets(50, 0, 0, 0));
        this.mainMenuScene = new Scene(mainMenuLayout, 1280, 720);
    }

    public Scene getScene() {
        return mainMenuScene;
    }
    
    private void runServer() {
    		showAlert(AlertType.INFORMATION, "Server Status", "Server started.");
            new Thread(() -> {
                try {
                    // Start the server in a separate thread
                    GameServer.main(null);

                    // Restart the application on the JavaFX Application Thread
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
}

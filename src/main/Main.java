package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import main.scene.MainMenuScene;
import main.scene.GameOverScene;
import main.scene.GameplayScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Main extends Application {
    private static MainMenuScene mainMenu;
    private static Gameplay gameplay;
    private static GameOverScene gameOver;
    private static Stage primaryStage; 

    @Override
    public void start(Stage stage) {
    	primaryStage = stage;
        mainMenu = new MainMenuScene(primaryStage);
        gameplay = new Gameplay(primaryStage);
        gameOver = new GameOverScene(primaryStage);

        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setTitle("BINI Platformer");
        primaryStage.show();
    }

    public static Scene getMainMenuScene() {
        return mainMenu.getScene();
    }

    public static Scene getGameplayScene() {
        return gameplay.getScene();
    }

    public static Scene getGameOverScene() {
        return gameOver.getScene();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void connectToServer(String address) {
		gameplay.connectToServer(address);

    }
    
    private static void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

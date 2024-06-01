package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import main.scene.MainMenuScene;
import main.scene.GameplayScene;
import main.scene.GameOverScene;

public class Main extends Application {
    private static MainMenuScene mainMenu;
    private static GameplayScene gameplay;
    private static GameOverScene gameOver;

    @Override
    public void start(Stage primaryStage) {
        mainMenu = new MainMenuScene(primaryStage);
        gameplay = new GameplayScene(primaryStage);
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

    public static void main(String[] args) {
        launch(args);
    }

}

package main.scene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import main.Main;

public class GameOverScene {
    private final Scene gameOverScene;

    public GameOverScene(Stage primaryStage) {
        StackPane mainMenuLayout = new StackPane();
        Rectangle bg = new Rectangle(1280, 720);
        Image bg_img = new Image("assets/go.png");
        bg.setFill(new ImagePattern(bg_img));

        Button buttonReturn = new Button("RETURN TO MAIN MENU");
        buttonReturn.setOnAction(event -> primaryStage.setScene(Main.getMainMenuScene()));

        mainMenuLayout.getChildren().addAll(bg, buttonReturn);
        this.gameOverScene = new Scene(mainMenuLayout, 1280, 720);
    }

    public Scene getScene() {
        return gameOverScene;
    }
}
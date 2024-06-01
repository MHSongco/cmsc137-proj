package main.scene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import main.Main;

public class MainMenuScene {
    private final Scene mainMenuScene;

    public MainMenuScene(Stage primaryStage) {
        StackPane mainMenuLayout = new StackPane();
        Rectangle bg = new Rectangle(1280, 720);
        Image bg_img = new Image("assets/BINI.png");
        bg.setFill(new ImagePattern(bg_img));

        Button buttonPlay = new Button("PLAY");
        buttonPlay.setOnAction(event -> primaryStage.setScene(Main.getGameplayScene()));


        mainMenuLayout.getChildren().addAll(bg, buttonPlay);
        this.mainMenuScene = new Scene(mainMenuLayout, 1280, 720);
    }

    public Scene getScene() {
        return mainMenuScene;
    }
}

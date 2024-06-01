package main.scene;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import main.code.PlayerController;
import main.level.Platform;
import main.level.LevelData;
import main.object.Player;
import main.network.ChatClient;
import main.Main;

public class GameplayScene {
    private Scene gameplayScene;
    private Pane gameplayLayout;

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();
    private HashMap<Integer, Node> otherPlayers = new HashMap<>();

    private int levelWidth;

    private Socket socket;
    private BufferedReader in;
    //private PrintWriter out;
    private ExecutorService executor;
    private int clientId;

    public GameplayScene(Stage primaryStage) {
        gameplayLayout = new Pane();
        levelWidth = LevelData.LEVEL1[0].length() * 60;

        // Background
        Rectangle bg = new Rectangle(levelWidth, 720);
        Image bg_img = new Image("assets/Sky.png");
        bg.setFill(new ImagePattern(bg_img));
        gameplayLayout.getChildren().add(bg);

        // Platform
        ArrayList<Node> platforms = Platform.generatePlatforms();
        gameplayLayout.getChildren().addAll(platforms);

        // Player
        Node bottomPlatform = platforms.get(platforms.size() - 1);
        int playerStartY = (int) bottomPlatform.getTranslateY() - 30;
        Player player = new Player(100, playerStartY, 30, 30, Color.BLUE);
        gameplayLayout.getChildren().add(player);

        this.gameplayScene = new Scene(gameplayLayout, 1280, 720);

        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();
            if (offset > 640 && offset < levelWidth - 640) {
                gameplayLayout.setLayoutX(-(offset - 640));
            }
        });

        //Chat client
        Button openChatButton = new Button("Open Chat");
        gameplayLayout.getChildren().add(openChatButton);
        ChatClient chatClient = new ChatClient();
        openChatButton.setOnAction(event -> chatClient.show());
        connectToServer();

        PlayerController playerController = new PlayerController(player, platforms, levelWidth, keys);

        gameplayScene.setOnKeyPressed(e -> keys.put(e.getCode(), true));
        gameplayScene.setOnKeyReleased(e -> keys.put(e.getCode(), false));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                playerController.update();

                //Lose condition
                if (player.getTranslateY() > primaryStage.getHeight()) {
                    this.stop();

                    primaryStage.setScene(Main.getGameOverScene());
                }
                //Win Condition

            }
        };
        timer.start();
    }

    public Scene getScene() {
        return gameplayScene;
    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", 58901);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out = new PrintWriter(socket.getOutputStream(), true);

            executor.submit(() -> {
                try {
                    while (true) {
                        String line = in.readLine();
                        if (line.startsWith("YOURID")) {
                            clientId = Integer.parseInt(line.split(" ")[1]);
                        } else if (line.startsWith("PLAYER")) {
                            String[] tokens = line.split(" ");
                            int playerId = Integer.parseInt(tokens[1]);
                            if (playerId != clientId) {  // Check if the update is not for the current player
                                String[] pos = tokens[2].split(",");
                                int x = Integer.parseInt(pos[0]);
                                int y = Integer.parseInt(pos[1]);
                                javafx.application.Platform.runLater(() -> updateOtherPlayer(playerId, x, y));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateOtherPlayer(int playerId, int x, int y) {
        Node otherPlayer = otherPlayers.get(playerId);

        if (otherPlayer == null) {
            otherPlayer = new Player(x, y, 40, 40, Color.RED);
            otherPlayers.put(playerId, otherPlayer);
        } else {
            otherPlayer.setTranslateX(x);
            otherPlayer.setTranslateY(y);
        }
    }
}

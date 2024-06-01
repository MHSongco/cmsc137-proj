package main;

import java.io.IOException;
import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.level.LevelData;
import main.network.ChatClient;

public class Main2 extends Application {
	private HashMap<KeyCode, Boolean> keys = new HashMap<>();

	private ArrayList<Node> platforms = new ArrayList<>();
    private HashMap<Integer, Node> otherPlayers = new HashMap<>();

	private Pane appRoot = new Pane();
	private Pane gameRoot = new Pane();
	private Pane uiRoot = new Pane();

	private Node player; //make this another class that extends Node if we are to add graphics
	private Point2D playerVelocity = new Point2D(0, 0);
	private boolean canJump = true;

	private int levelWidth;

	private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    private int clientId = -1;  // Initialize to an invalid ID

	private void initContent(){
		Rectangle bg = new Rectangle(1280, 720);

		Image bg_img = new Image("assets/Sky.png");

    	bg.setFill(new ImagePattern(bg_img));

		levelWidth = LevelData.LEVEL1[0].length() * 60;

		for(int i = 0; i<LevelData.LEVEL1.length; i++){
			String line = LevelData.LEVEL1[i];
			for(int j = 0; j<line.length(); j++){
				switch(line.charAt(j)){
					case '0':
						break;
					case '1':
						if(i > 0) {
							String prevline = LevelData.LEVEL1[i - 1];
							if(line.charAt(j) == '1' && prevline.charAt(j) == '1') {
								Node platform = createPlatformBottom(j*60, i*60, 60,60);
								platforms.add(platform);
							} else {
								Node platform = createPlatformTop(j*60, i*60, 60,60);
								platforms.add(platform);
							}
						} else {
							Node platform = createPlatformTop(j*60, i*60, 60,60);
							platforms.add(platform);
						}
						break;
					//add additional case for enemies/powerups etc
				}
			}
		}

		player = createEntity(0, 600, 40, 40, Color.BLUE);

		player.translateXProperty().addListener((obs, old, newValue) -> {
			int offset = newValue.intValue();
			if(offset > 640 && offset < levelWidth - 640){
				gameRoot.setLayoutX(-(offset-640));
			}
		});

		appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
		Button openChatButton = new Button("Open Chat");

		uiRoot.getChildren().add(openChatButton);

		ChatClient chatClient = new ChatClient();

		openChatButton.setOnAction(event -> chatClient.show());
		connectToServer();
	}

	private void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }

        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {
            movePlayerX(-5);
        }

        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
            movePlayerX(5);
        }

        //gravity
        if (playerVelocity.getY() < 10) {
            playerVelocity = playerVelocity.add(0, 1);
        }

        movePlayerY((int)playerVelocity.getY());


        updatePlayerPositionOnServer();
    }

    private void updateOtherPlayer(int playerId, int x, int y) {
        Node otherPlayer = otherPlayers.get(playerId);
        //System.out.println(otherPlayer);
        if (otherPlayer == null) {
            otherPlayer = createEntity(x, y, 40, 40, Color.RED);
            otherPlayers.put(playerId, otherPlayer);
        } else {
            otherPlayer.setTranslateX(x);
            otherPlayer.setTranslateY(y);
        }
    }

    private void updatePlayerPositionOnServer() {
        if (out != null && player != null) { // Check if out and player are not null
            int x = (int) player.getTranslateX();
            int y = (int) player.getTranslateY();
            out.println(x + "," + y);
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 58901);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

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
                                Platform.runLater(() -> updateOtherPlayer(playerId, x, y));
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

	private void movePlayerX(int value) {
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1)); //if no collision (we move one unit at a time)
        }
    }

    private void movePlayerY(int value) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() == platform.getTranslateY() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1)); //if no collision
        }
    }

    private void jumpPlayer() {
        if (canJump) {
            playerVelocity = playerVelocity.add(0, -30); //since y-axis in JavaFX is opposite
            canJump = false; //no double jump
        }
    }

    private Node createPlatformTop(int x, int y, int w, int h) {
    	Rectangle platform = new Rectangle(w, h);
    	platform.setTranslateX(x);
    	platform.setTranslateY(y);
    	Image img = new Image("assets/Sand.png");

    	platform.setFill(new ImagePattern(img));

    	gameRoot.getChildren().add(platform);

		return platform;
    }

    private Node createPlatformBottom(int x, int y, int w, int h) {
    	Rectangle platform = new Rectangle(w, h);
    	platform.setTranslateX(x);
    	platform.setTranslateY(y);
    	Image img = new Image("assets/Sand-Below.png");

    	platform.setFill(new ImagePattern(img));

    	gameRoot.getChildren().add(platform);

		return platform;
    }

    private Node createEntity(int x, int y, int w, int h, Color color) { //we can make this return as image or class that extends node for graphics
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true);

        gameRoot.getChildren().add(entity);
        return entity;
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

	public void start(Stage primaryStage) throws Exception{
		initContent();

		Scene scene = new Scene(appRoot);
		scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
		scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
		primaryStage.setTitle("Bini Platformer");

		primaryStage.setScene(scene);
		primaryStage.show();

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now){
				update();

				if (player.getTranslateY() > primaryStage.getHeight()) {
					this.stop();
		        	PauseTransition transition = new PauseTransition(Duration.seconds(1));
		    		transition.play();

		    		transition.setOnFinished(new EventHandler<ActionEvent>() {

		    			public void handle(ActionEvent arg0) {
		    				Text gameOverText = new Text("Game Over");
		    	            gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 36));

		    	            Button restartButton = new Button("Restart Game");
		    	            restartButton.setOnAction(e -> {
		    	                primaryStage.close();
		    	            });

		    	            // Adding components to layout
		    	            VBox layout = new VBox(20);
		    	            layout.setAlignment(Pos.CENTER);
		    	            layout.getChildren().addAll(gameOverText, restartButton);

		    	            // Creating scene
		    	            Scene scene = new Scene(layout, 1280, 720);

		    	        	primaryStage.setScene(scene);
		    			}
		    		});
		        }
			}
		};
		timer.start();
	}

    public static void main(String[] args) {
        launch(args);
    }
}

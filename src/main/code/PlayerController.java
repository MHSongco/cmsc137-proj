package main.code;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import main.scene.GameplayScene;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerController {
    private Node player;
    private ArrayList<Node> platforms;
    private HashMap<KeyCode, Boolean> keys;
    private Point2D playerVelocity;
    private int levelWidth;
    private boolean canJump;

    public PlayerController(Node player, ArrayList<Node> platforms, int levelWidth, HashMap<KeyCode, Boolean> keys) {
        this.player = player;
        this.platforms = platforms;
        this.levelWidth = levelWidth;
        this.keys = keys;
        this.playerVelocity = new Point2D(0, 0);
    }

    public void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }

        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {
            movePlayerX(-5);
        }

        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
            movePlayerX(5);
        }

        updatePlayerPositionOnServer();
    }

    private void movePlayerX(int value) {
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    System.out.println("Collision detected with platform");
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {
                            return;
                        }
                    } else {
                        if (player.getTranslateX() == platform.getTranslateX() + 60) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1)); //if no collision (we move one unit at a time)
        }
    }

    private void jumpPlayer() {
        if (canJump) {
            playerVelocity = playerVelocity.add(0, -30); //since y-axis in JavaFX is opposite
            canJump = false; //no double jump
        }
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    public void updatePlayerPositionOnServer() {
        if (player != null) { // Check if out and player are not null
            int x = (int) player.getTranslateX();
            int y = (int) player.getTranslateY();
        }
    }
}

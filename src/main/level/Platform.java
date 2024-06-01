package main.level;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Platform {
    public static ArrayList<Node> generatePlatforms() {
        ArrayList<Node> platforms = new ArrayList<>();

        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        Node platform;
                        if (i > 0 && line.charAt(j) == '1' && LevelData.LEVEL1[i - 1].charAt(j) == '1') {
                            platform = createBottom(j * 60, i * 60, 60, 60);
                        } else {
                            platform = createTop(j * 60, i * 60, 60, 60);
                        }
                        platforms.add(platform);
                        break;
                    // add additional case for enemies/powerups etc
                }
            }
        }

        return platforms;
    }

    private static Rectangle createTop(int x, int y, int w, int h) {
        Rectangle platform = new Rectangle(w, h);
        platform.setTranslateX(x);
        platform.setTranslateY(y);
        Image img = new Image("assets/Sand.png");
        platform.setFill(new ImagePattern(img));
        return platform;
    }

    private static Rectangle createBottom(int x, int y, int w, int h) {
        Rectangle platform = new Rectangle(w, h);
        platform.setTranslateX(x);
        platform.setTranslateY(y);
        Image img = new Image("assets/Sand-Below.png");
        platform.setFill(new ImagePattern(img));
        return platform;
    }
}

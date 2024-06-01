package main.object;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;

public class Player extends Rectangle {
    public Player(int x, int y, int w, int h, Color color) {
        super(w, h);
        setTranslateX(x);
        setTranslateY(y);
        setFill(color);
        getProperties().put("alive", true);

    }
}
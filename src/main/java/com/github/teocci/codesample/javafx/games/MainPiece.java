package com.github.teocci.codesample.javafx.games;

import com.github.teocci.codesample.javafx.games.engine.EngineConnector;
import com.github.teocci.codesample.javafx.games.model.Board;
import com.github.teocci.codesample.javafx.games.model.Piece;
import com.github.teocci.codesample.javafx.games.model.Sprite;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;

import static javafx.scene.input.MouseButton.PRIMARY;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-20
 */
public class MainPiece extends Application
{
    public static final String IMG_BOARD = "/images/board.png";

    private Board board;

    @Override
    public void start(final Stage stage)
    {
        stage.setTitle("Chess");

        Pane pane = new Pane();

        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            System.out.println("MOUSE_DRAGGED (" + event.getX() + ", " + event.getY() + ")");
            board.onMoving(event.getX(), event.getY());
        });

        pane.setOnMousePressed(event -> {
            System.out.println("MOUSE_PRESSED (" + event.getX() + ", " + event.getY() + ")");
            if (event.getButton().equals(PRIMARY)) {
                board.initMove(event.getX(), event.getY());
            }
        });

        pane.setOnMouseReleased(event -> {
            if (event.getButton().equals(PRIMARY)) {
                System.out.println("MOUSE_RELEASED (" + event.getX() + ", " + event.getY() + ")");
                board.endMove(event.getX(), event.getY());
            }
        });

//        Group root = new Group();

        Canvas canvas = new Canvas(504, 504);

        StackPane layout = new StackPane(new ImageView(new Image(IMG_BOARD)), canvas, pane);

        board = new Board(canvas.getGraphicsContext2D());
        board.loadPosition();

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
package com.github.teocci.codesample.javafx.games;

import com.github.teocci.codesample.javafx.games.model.Board;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.scene.input.MouseButton.PRIMARY;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-20
 */
public class ChessMain extends Application
{
    public static final String IMG_BOARD = "/images/board.png";

    private Board board;
    private Stage stage;

    @Override
    public void start(final Stage stage)
    {
        this.stage = stage;
        stage.setTitle("Chess");

        Pane pane = new Pane();

        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
//            System.out.println("MOUSE_DRAGGED (" + event.getX() + ", " + event.getY() + ")");
            board.onMoving(event.getX(), event.getY());
        });

        pane.setOnMousePressed(event -> {
            if (event.getButton().equals(PRIMARY)) {
//            System.out.println("MOUSE_PRESSED (" + event.getX() + ", " + event.getY() + ")");
                board.initMove(event.getX(), event.getY());
            }
        });

        pane.setOnMouseReleased(event -> {
            if (event.getButton().equals(PRIMARY)) {
//                System.out.println("MOUSE_RELEASED (" + event.getX() + ", " + event.getY() + ")");
                board.endMove(event.getX(), event.getY());
            }
        });

//        Group root = new Group();

        Canvas canvas = new Canvas(504, 504);

        StackPane layout = new StackPane(new ImageView(new Image(IMG_BOARD)), canvas, pane);

        board = new Board(this, canvas.getGraphicsContext2D());
        board.loadPosition();

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public Popup createPopup(final String message)
    {
        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        Label label = new Label(message);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> popup.hide());
        pause.play();
//        label.setOnMouseReleased(e -> popup.hide());
        label.getStylesheets().add("/css/style.css");
        label.getStyleClass().add("popup");
        popup.getContent().add(label);
        return popup;
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    public void showPopupMessage(final String message)
    {
        final Popup popup = createPopup(message);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(stage.getY() + stage.getHeight() / 2 - popup.getHeight() / 2);
        });
        popup.show(stage);

    }
}
package com.github.teocci.codesample.javafx.games;

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
public class Main extends Application
{
    public static final String IMG_BOARD = "/images/board.png";
    public static final String IMG_FIGURES = "/images/figures.png";

    private static final int PIECE_SIZE = 56;
    private static final int OFFSET_SIZE = 28;

    private static final Point ADD_OFFSET = new Point(OFFSET_SIZE, OFFSET_SIZE);
    private static final Point REMOVE_OFFSET = new Point(-OFFSET_SIZE, -OFFSET_SIZE);
    private static final Point PIECE_CENTER = new Point(PIECE_SIZE / 2, PIECE_SIZE / 2);

    private Sprite[] pieces = new Sprite[32];
    private String position;

    private boolean isMove = false;

    private double dx = 0, dy = 0;
    private String str;
    private Point oldPos, newPos;
    private int pieceIndex = 0;

    private int[][] board = {
            {-1, -2, -3, -4, -5, -3, -2, -1},
            {-6, -6, -6, -6, -6, -6, -6, -6},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {6, 6, 6, 6, 6, 6, 6, 6},
            {1, 2, 3, 4, 5, 3, 2, 1}
    };

    private GraphicsContext gc;

    @Override
    public void start(final Stage stage)
    {
        stage.setTitle("Event Handling");

        Pane pane = new Pane();

        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isMove) {
                pieces[pieceIndex].setPosition(event.getX() - dx, event.getY() - dy);
                update();
            }
        });

        pane.setOnMousePressed(event -> {
            System.out.println("MOUSE_PRESSED (" + event.getX() + ", " + event.getY() + ")");
            if (event.getButton().equals(PRIMARY)) {
                int i = 0;
                for (Sprite piece : pieces) {
                    if (piece.contains(event.getX(), event.getY())) {
                        isMove = true;
                        pieceIndex = i;

                        oldPos = piece.getPosition();
                        dx = event.getX() - oldPos.x;
                        dy = event.getY() - oldPos.y;
                    }
                    i++;
                }
                update();
            }
        });

        pane.setOnMouseReleased(event -> {
            if (event.getButton().equals(PRIMARY)) {
                System.out.println("MOUSE_RELEASED (" + event.getX() + ", " + event.getY() + ")");
                System.out.println("PIECE_POS (" + pieces[pieceIndex].getPosition().x + ", " + pieces[pieceIndex].getPosition().y + ")");
                isMove = false;
                Point midPoint = getOldMidPoint(pieces[pieceIndex].getPosition());
                System.out.println("MID_POS (" + midPoint.x + ", " + midPoint.y + ")");

                newPos = getNewPoint(midPoint);
                System.out.println("NEW_POS (" + newPos.x + ", " + newPos.y + ")");
                str = toChessNote(oldPos) + toChessNote(newPos);
                move(str);
                if (oldPos.equals(newPos)) {
                    position += str + " ";
                }
                pieces[pieceIndex].setPosition(newPos);
//                pieces[pieceIndex].move(ADD_OFFSET);
                update();
            }
        });

//        Group root = new Group();

        Canvas canvas = new Canvas(504, 504);
        gc = canvas.getGraphicsContext2D();

        StackPane layout = new StackPane(new ImageView(new Image(IMG_BOARD)), canvas, pane);

        int index = 0;
        for (Sprite piece : pieces) {
            piece = new Sprite();
            piece.setImage(IMG_FIGURES);
            pieces[index] = piece;
            index++;
        }

        loadPosition();
        update();

//        root.getChildren().add(layout);
//        root.getChildren().add(canvas);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    private void update()
    {
        gc.clearRect(0, 0, 504, 504);

        for (Sprite piece : pieces) {
//            piece.move(ADD_OFFSET);
            piece.render(gc);
//            piece.move(REMOVE_OFFSET);
        }
    }

    private Point getOldMidPoint(Point pos)
    {
        return new Point(pos.x + PIECE_CENTER.x, pos.y + PIECE_CENTER.y);
    }

    private Point getNewPoint(Point pos)
    {
        int xIndex = (pos.x - OFFSET_SIZE) / PIECE_SIZE;
        int yIndex = (pos.y - OFFSET_SIZE) / PIECE_SIZE;

        xIndex = PIECE_SIZE * xIndex + OFFSET_SIZE;
        yIndex = PIECE_SIZE * yIndex + OFFSET_SIZE;
        return new Point(xIndex, yIndex);
    }

    private void move(String str)
    {
        Point oldPos = toCoord(str.charAt(0), str.charAt(1));
        Point newPos = toCoord(str.charAt(2), str.charAt(3));

        for (int i = 0; i < 32; i++)
            if (newPos.equals(pieces[i].getPosition())) pieces[i].setPosition(-100, -100);

        for (int i = 0; i < 32; i++)
            if (oldPos.equals(pieces[i].getPosition())) pieces[i].setPosition(newPos);

        //castling       //if the king didn't move
        if (str.equals("e1g1")) if (!position.contains("e1")) move("h1f1");
        if (str.equals("e8g8")) if (!position.contains("e8")) move("h8f8");
        if (str.equals("e1c1")) if (!position.contains("e1")) move("a1d1");
        if (str.equals("e8c8")) if (!position.contains("e8")) move("a8d8");
    }

    private void loadPosition()
    {
        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int n = board[i][j];
                if (n == 0) continue;
                int x = Math.abs(n) - 1;
                int y = n > 0 ? 1 : 0;
                pieces[index].setPosition(PIECE_SIZE * j, PIECE_SIZE * i);
                pieces[index].move(ADD_OFFSET);

                pieces[index].setBoundary(new Rectangle2D(PIECE_SIZE * x, PIECE_SIZE * y, PIECE_SIZE, PIECE_SIZE));
//                gc.clearRect(0, 0, 504,504);
                System.out.println("setBoundary(" + PIECE_SIZE * x + ", " + PIECE_SIZE * y + ")");
                System.out.println("setPosition(" + pieces[index].getPosition() + ")");
//                pieces[index].render(gc);
                index++;
            }
        }

//        for (int i = 0; i < position.length(); i += 5)
//            move(position.substr(i, 4));
    }

    private String toChessNote(Point p)
    {
        String s = "";
        s += (char) (p.x / PIECE_SIZE + 97);
        s += (char) (7 - p.y / PIECE_SIZE + 49);
        return s;
    }

    private Point toCoord(char a, char b)
    {
        int x = (a - 97) * PIECE_SIZE;
        int y = (7 - b + 49) * PIECE_SIZE;
        return new Point(x, y);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
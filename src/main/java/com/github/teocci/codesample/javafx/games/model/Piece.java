package com.github.teocci.codesample.javafx.games.model;

import com.github.teocci.codesample.javafx.games.enums.PieceColor;
import com.github.teocci.codesample.javafx.games.enums.PieceType;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

import static com.github.teocci.codesample.javafx.games.enums.PieceColor.BLACK;
import static com.github.teocci.codesample.javafx.games.enums.PieceColor.WHITE;
import static com.github.teocci.codesample.javafx.games.model.Board.OFFSET_SIZE;
import static com.github.teocci.codesample.javafx.games.model.Board.PIECE_SIZE;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-22
 */
public class Piece
{
    private static final String IMG_FIGURES = "/images/figures.png";

    private PieceType type;
    private PieceColor color;

    private Sprite sprite;
    private String position;

    private boolean alive = false;

    public Piece(PieceType type, PieceColor color)
    {
        this.sprite = new Sprite();
        this.sprite.setImage(IMG_FIGURES);
        this.sprite.setBoundary(new Rectangle2D(PIECE_SIZE * type.ordinal(), PIECE_SIZE * color.ordinal(), PIECE_SIZE, PIECE_SIZE));

        this.type = type;
        this.color = color;
        alive = true;
    }

    private Point toCoordinates()
    {
        int x = (position.charAt(0) - 97) * PIECE_SIZE + OFFSET_SIZE;
        int y = (7 - position.charAt(1) + 49) * PIECE_SIZE + OFFSET_SIZE;
//        System.out.println("PIECE <toCoordinates> (X: " + x + " Y: " + y + ")");

        return new Point(x, y);
    }

    public void setPosition(String position)
    {
        this.position = position;
        setPosition(toCoordinates());
    }

    public void setPosition(Point point)
    {
        sprite.setPosition(point);
    }

    public void setPosition(double x, double y)
    {
        sprite.setPosition(x, y);
    }

    public Point getPosition()
    {
        return sprite.getPosition();
    }

    public PieceType getType()
    {
        return type;
    }

    public PieceColor getColor()
    {
        return color;
    }

    public void render(GraphicsContext gc)
    {
        sprite.render(gc);
    }

    public boolean contains(double x, double y)
    {
        return sprite.contains(x, y);
    }

    public boolean contains(String pos)
    {
//        System.out.println("PIECE (Pos: " + position + " P: " + pos + ")");
        return position.equals(pos);
    }

    public void move(Point p)
    {
        sprite.move(p);
    }

    public void kill()
    {
        alive = false;
        sprite.setPosition(-100, -100);
    }

    public boolean isAlive()
    {
        return alive;
    }

    public boolean isEnemy(Piece currentPiece)
    {
        return !color.equals(currentPiece.getColor());
    }

    public boolean isWhite()
    {
        return color.equals(WHITE);
    }

    public boolean isBlack()
    {
        return color.equals(BLACK);
    }
}

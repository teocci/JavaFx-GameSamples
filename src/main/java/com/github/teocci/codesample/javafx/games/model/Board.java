package com.github.teocci.codesample.javafx.games.model;

import com.github.teocci.codesample.javafx.games.ChessMain;
import com.github.teocci.codesample.javafx.games.engine.EngineConnector;
import com.github.teocci.codesample.javafx.games.enums.PieceColor;
import com.github.teocci.codesample.javafx.games.enums.PieceType;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.teocci.codesample.javafx.games.enums.PieceColor.BLACK;
import static com.github.teocci.codesample.javafx.games.enums.PieceColor.WHITE;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-22
 */
public class Board
{
    public static final int PIECE_SIZE = 56;
    public static final int OFFSET_SIZE = 28;

    private static final String VALID_MOVE = "^([a-h]x?[1-8])([a-h]x?[1-8])";

    private static final Point ADD_OFFSET = new Point(OFFSET_SIZE, OFFSET_SIZE);
    private static final Point REMOVE_OFFSET = new Point(-OFFSET_SIZE, -OFFSET_SIZE);
    private static final Point PIECE_CENTER = new Point(PIECE_SIZE / 2, PIECE_SIZE / 2);

    private final Pattern movePattern = Pattern.compile(VALID_MOVE);

    private Piece[] pieces = new Piece[32];

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

    private String moves = "";
    private String currentMove;

    private String oldPos, newPos;

    private boolean moving = false;
    private boolean pcTurn = false;
    private boolean validMove;

    private double dx = 0, dy = 0;

    private Point oldPoint, newPoint;

    private Piece currentPiece;
    private int currentIndex = 0;

    private EngineConnector engine;

    private ChessMain app;

    public Board(ChessMain app, GraphicsContext gc)
    {
        this.gc = gc;
        this.app = app;
        engine = new EngineConnector();
    }

    public Piece[] getPieces()
    {
        return pieces;
    }


    public void loadPosition()
    {
        int index = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int n = board[row][col];
                if (n == 0) continue;
                PieceType type = PieceType.values()[Math.abs(n) - 1];
                PieceColor color = n > 0 ? WHITE : BLACK;
                pieces[index] = new Piece(type, color);

                String pos = toChessNotation(col, row);
//                System.out.println("PIECE (T: " + type + " C: " + color + " P: " + pos + ")");

                pieces[index].setPosition(pos);

//                gc.clearRect(0, 0, 504,504);
//                pieces[index].render(gc);
                index++;
            }
        }

//        for (int i = 0; i < moves.length(); i += 5)
//            move(moves.substr(i, 4));
        render();
    }


    private void render()
    {
        gc.clearRect(0, 0, 504, 504);

        for (Piece piece : pieces) {
//            piece.move(ADD_OFFSET);
            piece.render(gc);
//            piece.move(REMOVE_OFFSET);
        }
    }

    public void initMove(double x, double y)
    {
        if (pcTurn) return;
        String pos = toChessNotation(x, y);
        System.out.print("initMove (P: " + pos + ") --> ");

        int i = 0;
        for (Piece piece : pieces) {
            if (piece.isWhite() && piece.contains(pos)) {
                moving = true;
                currentIndex = i;
                currentPiece = piece;

                oldPos = pos;

                oldPoint = piece.getPosition();
                dx = x - oldPoint.x;
                dy = y - oldPoint.y;
                break;
            }
            i++;
        }
        render();
    }

    public void onMoving(double x, double y)
    {
        if (pcTurn) return;
        if (moving) {
            currentPiece.setPosition(x - dx, y - dy);
            render();
        }
    }

    public void endMove(double x, double y)
    {
        if (oldPos == null) return;
        if (pcTurn) return;
        String pos = toChessNotation(x, y);
        System.out.println("(P: " + pos + ") endMove.");

        newPos = pos;
        moving = false;

//        System.out.println("PIECE_POS (" + currentPiece.getPosition().x + ", " + currentPiece.getPosition().y + ")");
        pieces[currentIndex].setPosition(x - dx, y - dy);
//        Point midPoint = getOldMidPoint(currentPiece.getPosition());
//        System.out.println("MID_POS (" + midPoint.x + ", " + midPoint.y + ")");

//        newPoint = getNewPoint(midPoint);
//        System.out.println("OLD_POS (" + oldPoint.x + ", " + oldPoint.y + ")");
//        System.out.println("NEW_POS (" + newPoint.x + ", " + newPoint.y + ")");
//        currentMove = toChessNotation(oldPoint) + toChessNotation(newPoint);

        currentMove = oldPos + newPos;
        move(currentMove);

        if (validMove) {
            System.out.println("MOVE (" + currentMove + ")");
            moves += currentMove + " ";
            pcTurn = true;
        } else {
            pieces[currentIndex].setPosition(oldPos);
            pcTurn = false;
            app.showPopupMessage("Illegal move");
        }
        render();
        readyToMove();


//        pieces[currentIndex].setPosition(newPoint);
//                pieces[currentIndex].move(ADD_OFFSET);

        if (pcTurn) {
            pcMove();
        }
    }

    private void readyToMove()
    {
        currentIndex = -1;
        currentPiece = null;

        oldPos = null;
        newPos = null;

        oldPoint = null;
        newPoint = null;
    }


    private void pcMove()
    {
        currentMove = engine.getNextMove(moves);
        System.out.println("PC_MOVE (" + currentMove + ")");

        final Matcher matcher = movePattern.matcher(currentMove);

        if (matcher.find()) {
            oldPos = matcher.group(1);
            newPos = matcher.group(2);

//            System.out.println("PC_MOVE <oldPos> (" + oldPos.x + ", " + oldPos.y + ")");

            int index = 0;
            for (Piece piece : pieces) {
                if (piece.isAlive() && piece.contains(oldPos)) {
                    currentPiece = piece;
                    currentIndex = index;
                    break;
                }
                index++;
            }

            Point oldPoint = toCoordinates(oldPos.charAt(0), oldPos.charAt(1));
            Point newPoint = toCoordinates(newPos.charAt(0), newPos.charAt(1));

            for (int k = 0; k < 50; k++) {
                Point p = new Point((newPoint.x - oldPoint.x) / 50, (newPoint.y - oldPoint.y) / 50);
                pieces[currentIndex].move(p);
                for (Piece piece : pieces) {
                    piece.render(gc);
                }
            }

            move(currentMove);
            moves += currentMove + " ";
            pieces[currentIndex].setPosition(newPos);
            pcTurn = false;
            render();
            readyToMove();
        }
    }

    private void move(String move)
    {
        validMove = false;
        if (oldPos.equals(newPos)) return;

        final Matcher matcher = movePattern.matcher(move);
        if (matcher.find()) {
            // Castling verify if the king did not move before
            if (move.equals("e1g1")) if (moves.contains("e1")) return;
            if (move.equals("e1c1")) if (moves.contains("e1")) return;
            if (move.equals("e8g8")) if (moves.contains("e8")) return;
            if (move.equals("e8c8")) if (moves.contains("e8")) return;

            validMove = true;

            String oldPos = matcher.group(1);
            String newPos = matcher.group(2);

            int index = 0;
            for (Piece piece : pieces) {
                if (piece.isAlive() && piece.contains(newPos)) {
                    if (piece.isEnemy(currentPiece)) {
                        pieces[index].kill();
                    } else {
                        validMove = false;
                        return;
                    }
                    break;
                }
                index++;
            }

            index = 0;
            for (Piece piece : pieces) {
                if (piece.isAlive() && piece.contains(oldPos)) {
                    pieces[index].setPosition(newPos);
                    break;
                }
                index++;
            }

            if (move.equals("e1g1")) move("h1f1");
            if (move.equals("e8g8")) move("h8f8");
            if (move.equals("e1c1")) move("a1d1");
            if (move.equals("e8c8")) move("a8d8");
        }
    }

    private String toChessNotation(int x, int y)
    {
        String s = "";
        s += (char) (x + 97);
        s += (char) (7 - y + 49);
        return s;
    }

    private String toChessNotation(double x, double y)
    {
        String s = "";
        s += (char) ((x - OFFSET_SIZE) / PIECE_SIZE + 97);
        s += (char) (8 - (y - OFFSET_SIZE) / PIECE_SIZE + 49);
        return s;
    }

    private String toChessNotation(Point point)
    {
        String s = "";
        s += (char) ((point.x - OFFSET_SIZE) / PIECE_SIZE + 97);
        s += (char) ((point.y - OFFSET_SIZE) / PIECE_SIZE + 49);
        return s;
    }

    private Point toCoordinates(char a, char b)
    {
        int x = (a - 97) * PIECE_SIZE + OFFSET_SIZE;
        int y = (7 - b + 49) * PIECE_SIZE + OFFSET_SIZE;
        return new Point(x, y);
    }
}

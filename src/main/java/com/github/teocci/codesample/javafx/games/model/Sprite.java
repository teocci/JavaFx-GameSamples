package com.github.teocci.codesample.javafx.games.model;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

import java.awt.*;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-20
 */
public class Sprite
{
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;

    private Rectangle2D boundaries;

    public Sprite()
    {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    public void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
        boundaries = new Rectangle2D(positionX, positionY, width, height);
    }

    public void setImage(String filename)
    {
        Image i = new Image(filename);
        setImage(i);
    }

    public void setPosition(Point point)
    {
        positionX = point.x;
        positionY = point.y;
    }

    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }

    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }

    public void update(double time)
    {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    public void render(GraphicsContext gc)
    {
        if (boundaries == null) {
            gc.drawImage(image, positionX, positionY);
        } else {
            gc.drawImage(image, boundaries.getMinX(), boundaries.getMinY(), boundaries.getWidth(), boundaries.getHeight(),
                    positionX, positionY, boundaries.getWidth(), boundaries.getHeight());
        }
    }

    public Rectangle2D getBoundary()
    {
        return boundaries != null ? boundaries : new Rectangle2D(positionX, positionY, width, height);
    }

    public void setBoundary(Rectangle2D boundaries)
    {
        this.boundaries = boundaries;
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public Point getPosition()
    {
        return new Point((int) positionX, (int) positionY);
    }

    public boolean contains(double x, double y)
    {
        return x >= positionX && x <= positionX + boundaries.getWidth() && y >= positionY && y <= positionY + boundaries.getHeight();
    }

    public void move(Point offset)
    {
        positionX += offset.x;
        positionY += offset.y;
    }

    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]";
    }
}
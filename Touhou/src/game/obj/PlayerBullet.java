package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author under
 */
public class PlayerBullet {
    private final double SPEED = 3;
    private int bulletType;
    private double x;
    private double y;
    private int damage;

    private double height;
    private double width;
    private Image image;
    private Area hitboxArea;
    private Shape hitboxShape;


    public PlayerBullet(int type, double x, double y, double scale) {
        this.bulletType = type;
        switch(type) {
            case 1:
            this.image = new ImageIcon(getClass().getResource("/game/image/bullets/standardPlayerBullet.png")).getImage();
            Path2D path = new Path2D.Double();
            path.moveTo(19, 2);
            path.lineTo(27, 14);
            path.lineTo(29, 27);
            path.lineTo(17, 31);
            path.lineTo(14, 31);
            path.lineTo(2, 27);
            path.lineTo(4, 14);
            path.lineTo(12, 2);
            hitboxArea = new Area(path);
            this.damage = 1;
            break;
            case 2:
                // code block
            break;
        };
        width = image.getWidth(null);
        height = image.getHeight(null);

        
        x += Player.PLAYER_WIDTH / 2 - (width / 2);
        y += Player.PLAYER_HEIGHT / 2 - (height / 2);
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= SPEED;
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        g2.drawImage(image, 0, 0, null);
        g2.setTransform(oldTransform);
    }

    // Checks if bullet it still on screen
    public boolean check() {
        if (y < -height) {
            return false;
        } else {
            return true;
        }
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public double getCenterX() {
        return x + width / 2;
    }
    public double getCenterY() {
        return y + height / 2;
    }
    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        return new Area(afx.createTransformedShape(hitboxArea));
    }
    public int getDamage() {
        return damage;
    }
}
